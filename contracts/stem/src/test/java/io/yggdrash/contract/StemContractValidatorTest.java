package io.yggdrash.contract;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.yggdrash.common.contract.vo.dpoa.Validator;
import io.yggdrash.common.crypto.ECKey;
import io.yggdrash.common.crypto.HashUtil;
import io.yggdrash.common.crypto.HexUtil;
import io.yggdrash.common.store.StateStore;
import io.yggdrash.common.store.datasource.HashMapDbSource;
import io.yggdrash.common.utils.BranchUtil;
import io.yggdrash.common.utils.ByteUtil;
import io.yggdrash.common.utils.ContractUtils;
import io.yggdrash.common.utils.FileUtil;
import io.yggdrash.common.utils.JsonUtil;
import io.yggdrash.contract.core.ExecuteStatus;
import io.yggdrash.contract.core.TransactionReceipt;
import io.yggdrash.contract.core.TransactionReceiptImpl;
import io.yggdrash.contract.core.annotation.ContractBranchStateStore;
import io.yggdrash.contract.core.annotation.ContractStateStore;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.InvalidCipherTextException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StemContractValidatorTest {

    private static final Logger log = LoggerFactory.getLogger(StemContractValidatorTest.class);
    private static final StemContract.StemService stemContract = new StemContract.StemService();


    private Field txReceiptField;
    TestYeed testYeed = new TestYeed();

    StateStore stateStore;

    TestWallet v1;
    TestWallet v2;
    TestWallet v3;
    TestWallet v4;

    String updateBranchId;
    String proposer = "5244d8163ea6fdd62aa08ae878b084faa0b013be";


    @Before
    public void setUp() throws IllegalAccessException, IOException, InvalidCipherTextException {
        // Steup StemContract
        stateStore = new StateStore(new HashMapDbSource());

        List<Field> txReceipt = ContractUtils.txReceiptFields(stemContract);
        if (txReceipt.size() == 1) {
            txReceiptField = txReceipt.get(0);
        }
        for (Field f : ContractUtils.contractFields(stemContract, ContractStateStore.class)) {
            f.setAccessible(true);
            f.set(stemContract, stateStore);
        }
        // 1a0cdead3d1d1dbeef848fef9053b4f0ae06db9e
        JsonObject obj = new JsonObject();
        obj.addProperty("address", "1a0cdead3d1d1dbeef848fef9053b4f0ae06db9e");
        assertTrue("setup", BigInteger.ZERO.compareTo(testYeed.balanceOf(obj)) < 0);

        TestBranchStateStore branchStateStore = new TestBranchStateStore();
        branchStateStore.getValidators().getValidatorMap()
                .put("81b7e08f65bdf5648606c89998a9cc8164397647",
                        new Validator("81b7e08f65bdf5648606c89998a9cc8164397647"));

        for (Field f : ContractUtils.contractFields(stemContract, ContractBranchStateStore.class)) {
            f.setAccessible(true);
            f.set(stemContract, branchStateStore);
        }

        String v1Path = getClass()
                .getResource("/validatorKeys/5244d8163ea6fdd62aa08ae878b084faa0b013be.json")
                .getFile();
        v1 = new TestWallet(new File(v1Path), "Aa1234567890!");

        String v2Path = getClass()
                .getResource("/validatorKeys/a4e063d728ee7a45c5bab3aa2283822d49a9f73a.json")
                .getFile();
        v2 = new TestWallet(new File(v2Path), "Password1234!");

        String v3Path = getClass()
                .getResource("/validatorKeys/e38f532685b5e61eca5bc25a0da8ea87d74e671e.json")
                .getFile();
        v3 = new TestWallet(new File(v3Path), "Password1234!");

        String v4Path = getClass()
                .getResource("/validatorKeys/f5927c28b66d4bb4b50395662a097370e8cd7e58.json")
                .getFile();
        v4 = new TestWallet(new File(v4Path), "Password1234!");

        // Update Branch is
        InputStream testUpdateBranch = getClass().getClassLoader().getResourceAsStream("update-branch.json");
        String branchString = IOUtils.toString(testUpdateBranch, FileUtil.DEFAULT_CHARSET);
        JsonObject updateBranchObject = JsonUtil.parseJsonObject(branchString);
        byte[] rawBranchId = BranchUtil.branchIdGenerator(updateBranchObject);
        updateBranchId = HexUtil.toHexString(rawBranchId);

        JsonObject param = new JsonObject();
        // Get Branch sample in resources

        param.add("branch", updateBranchObject);
        param.addProperty("fee", BigInteger.valueOf(1000000));

        TransactionReceipt receipt = createReceipt(proposer);
        receipt.setIssuer(proposer);
        setUpReceipt(receipt);


        stemContract.create(param);
        assertTrue("Branch Create Success", receipt.isSuccess());
    }

    @Test
    public void updateValidatorsAddValidator() {
        // TODO validator can suggest validator and other validator vote to suggest validator
        // TODO all message is just one transaction

        // message property
        // BRANCH_ID , (20byte)
        // BLOCK_HEIGHT (8 BYTE),
        // PROPOSER_VALIDATOR (20 byte)
        // TARGET_VALIDATOR (20 byte)
        // OPERATING_FLAG (1byte)
        // SIGNATURE (65 byte) ( N Validators )

        String targetBranchId = updateBranchId;
        Long blockHeight = 100L;

        // add new validator
        String targetValidator = "c91e9d46dd4b7584f0b6348ee18277c10fd7cb94";
        String operatingFlag = StemOperation.ADD_VALIDATOR.toValue(); // ADD OR DELETE OR REPLACE

        byte[] message = ByteUtil.merge(
                HexUtil.hexStringToBytes(targetBranchId),
                ByteUtil.longToBytes(blockHeight),
                HexUtil.hexStringToBytes(proposer),
                HexUtil.hexStringToBytes(targetValidator),
                operatingFlag.getBytes()
        );
        // message make to sha3
        message = HashUtil.sha3(message);
        log.debug("message Size : {} ", message.length);
        assertEquals("message Length : ", message.length, 32);

        log.debug(updateBranchId);

        byte[] signV1 = v1.sign(message, true);
        byte[] signV2 = v2.sign(message, true);
        byte[] signV3 = v3.sign(message, true);
        byte[] signV4 = v4.sign(message, true);

        String[] signed = new String[]{
                HexUtil.toHexString(signV1), HexUtil.toHexString(signV2),
                HexUtil.toHexString(signV3), HexUtil.toHexString(signV4)
        };

        // add to params in sign
        JsonArray signedArray = new JsonArray();
        Arrays.stream(signed).forEach(sg -> signedArray.add(sg));

        JsonObject updateBranchValiator = new JsonObject();
        updateBranchValiator.addProperty("branchId", targetBranchId);
        updateBranchValiator.addProperty("blockHeight", blockHeight);
        updateBranchValiator.addProperty("proposer", proposer);
        updateBranchValiator.addProperty("targetValidator", targetValidator);
        updateBranchValiator.addProperty("operatingFlag", StemOperation.ADD_VALIDATOR.toValue());
        updateBranchValiator.add("signed", signedArray);

        log.debug(JsonUtil.prettyFormat(updateBranchValiator));
        /*
        // Test Sign verify
        ECKey.ECDSASignature signatureV1 = new ECKey.ECDSASignature(signV1);
        ECKey.ECDSASignature signatureV2 = new ECKey.ECDSASignature(signV2);
        ECKey.ECDSASignature signatureV3 = new ECKey.ECDSASignature(signV3);
        ECKey.ECDSASignature signatureV4 = new ECKey.ECDSASignature(signV4);

        byte[] addressV1 = ECKey.recoverAddressFromSignature(signV1[0] - 27, signatureV1, message);
        byte[] addressV2 = ECKey.recoverAddressFromSignature(signV2[0] - 27, signatureV2, message);
        byte[] addressV3 = ECKey.recoverAddressFromSignature(signV3[0] - 27, signatureV3, message);
        byte[] addressV4 = ECKey.recoverAddressFromSignature(signV4[0] - 27, signatureV4, message);
        log.debug("validator 1 {}", HexUtil.toHexString(addressV1));

        assertTrue("address Is equal", Arrays.equals(addressV1, v1.getAddress()));
        assertTrue("address Is equal", Arrays.equals(addressV2, v2.getAddress()));
        assertTrue("address Is equal", Arrays.equals(addressV3, v3.getAddress()));
        assertTrue("address Is equal", Arrays.equals(addressV4, v4.getAddress()));
        */

        // UPDATE Validator Set
        TransactionReceipt updateReceipt = createReceipt(proposer);
        setUpReceipt(updateReceipt);

        stemContract.updateValidator(updateBranchValiator);
        assert updateReceipt.getStatus() == ExecuteStatus.SUCCESS;

        JsonObject validators = stemContract.getValidators(updateBranchId);
        log.debug(JsonUtil.prettyFormat(validators));

        assertTrue("Add new Validator", validators.getAsJsonArray("validators").size() == 5);
    }

    @Test
    public void updateValidatorsRemoveValidator() {
        // TODO remove validator
    }

    @Test
    public void updateValidatorsReplaceValidator() {
        // TODO remove validator
    }

    @Test
    public void updateValidatorsUpdateValidatorSet() {
        // TODO Update validator Set
    }



    private TransactionReceipt createReceipt(String issuer) {
        TransactionReceipt receipt = new TransactionReceiptImpl();
        receipt.setIssuer(issuer);
        return receipt;
    }

    private void setUpReceipt(TransactionReceipt receipt) {
        try {
            txReceiptField.set(stemContract, receipt);
            testYeed.setTxReceipt(receipt);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
