
package io.yggdrash.core.contract;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.yggdrash.common.contract.Contract;
import io.yggdrash.common.contract.standard.CoinStandard;
import io.yggdrash.common.contract.vo.dpoa.ValidatorSet;
import io.yggdrash.contract.core.TransactionReceipt;
import io.yggdrash.contract.core.annotation.ContractQuery;
import io.yggdrash.contract.core.annotation.ContractStateStore;
import io.yggdrash.contract.core.annotation.ContractTransactionReceipt;
import io.yggdrash.contract.core.annotation.Genesis;
import io.yggdrash.contract.core.annotation.InvokeTransaction;
import io.yggdrash.contract.core.store.ReadWriterStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import static io.yggdrash.common.config.Constants.BRANCH_ID;
import static io.yggdrash.common.config.Constants.TX_ID;


public class StemContract implements BundleActivator, ServiceListener {
    private static final Logger log = LoggerFactory.getLogger(StemContract.class);
    //private ServiceRegistration registration;

    // Get other Service
    private CoinStandard asset;


    @Override
    public void start(BundleContext context) {
        log.info("Start stem contract");

        //Find for service in another bundle
        Hashtable<String, String> props = new Hashtable<>();
        props.put("YGGDRASH", "Stem");
        context.registerService(StemService.class.getName(), new StemService(), props);
        //Register our service in the bundle context using the name.
        //registration = context.registerService(StemService.class.getName(), new StemService(), props);
    }

    @Override
    public void stop(BundleContext context) {
        log.info("Stop stem contract");
        //TODO Why don't unregister the service?
        //registration.unregister();
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        // get YEED contract in this
        //
    }

    public void setAsset(CoinStandard coinStandard) {
        this.asset = coinStandard;
    }

    public static class StemService implements Contract {
        private static final Logger log = LoggerFactory.getLogger(StemContract.class);

        @ContractStateStore
        ReadWriterStore<String, JsonObject> state;


        @ContractTransactionReceipt
        TransactionReceipt txReceipt;

        @Genesis
        @InvokeTransaction // TODO remove InvokeTransaction
        public TransactionReceipt init(JsonObject param) {
            //txReceipt = create(param);
            log.info("[StemContract | genesis] SUCCESS! param => {}", param);
            return txReceipt;
        }

        /**
         * Returns the id of a registered branch
         *
         * @param params branch   : The branch.json to register on the stem
         */
        @InvokeTransaction
        public void create(JsonObject params) {
            // TODO store branch
            // Validate branch spec
            // params

            JsonObject branch = params.getAsJsonObject("branch");
            // get branch id


            // check fee
            // check fee govonence


            // get validator

            // get meta infomation


        }

        /**
         * Returns the id of a updated branch
         *
         * @param params branchId The Id of the branch to update
         *               branch   The branch.json to update on the stem
         */
        @InvokeTransaction
        public void update(JsonObject params) {
            // TODO update branch meta information

            // get branch id

            // check branch validator

            // save branch meta information

        }

        @InvokeTransaction
        public void updateValidator(JsonObject params) {
            // TODO update branch meta information

            // get branch id

            // check branch validator vote

            // save branch validator set information

        }

        public ValidatorSet getValidator(String branchId) {
            // TODO get validator Set
            // get Validator set

            //
            //this.state.get()
            return null;
        }


        /**
         * Returns boolean
         *
         * @param branchId
         * */
        public void messageCall(String branchId) {
            // TODO message call to contract
            // TODO isEnoughFee
        }

        /**
         * @param params branch id
         *
         * @return branch json object
         */
        @ContractQuery
        public JsonObject getBranch(JsonObject params) {
            // TODO get branch information
            String branchId = params.get(BRANCH_ID).getAsString();
//            StemContractStateValue stateValue = getBranchStateValue(branchId);
//
//            if (isBranchExist(branchId) && isEnoughFee(stateValue)) {
//                stateValue.setFee(feeState(stateValue));
//                stateValue.setBlockHeight(txReceipt.getBlockHeight());
//                return stateValue.getJson();
//            }
            // TODO fee not enough mesaage
            return new JsonObject();
        }

        /**
         * @param params transaction id
         *
         * @return branch id
         */
        @ContractQuery
        public String getBranchIdByTxId(JsonObject params) {
            // TODO remove
            String txId = params.get(TX_ID).getAsString();
            JsonObject branchIdJson = state.get(txId);
//            if (branchIdJson != null && branchIdJson.has("branchId")) {
//                String branchId = branchIdJson.get("branchId").getAsString();
//                StemContractStateValue stateValue = getBranchStateValue(branchId);
//                if (isBranchExist(branchId) && isEnoughFee(stateValue)) {
//                    return branchIdJson.get("branchId").getAsString();
//                }
//            }
            return "";
        }

        /**
         * @param params branch id
         *
         * @return contract json object
         */
        @ContractQuery
        public Set<JsonElement> getContract(JsonObject params) {
            // TODO remove
            String branchId = params.get(BRANCH_ID).getAsString();
            Set<JsonElement> contractSet = new HashSet<>();
//            StemContractStateValue stateValue = getBranchStateValue(branchId);
//
//            if (isBranchExist(branchId) && isEnoughFee(stateValue)) {
//                JsonArray contracts = getBranchStateValue(branchId).getJson()
//                        .getAsJsonArray("contracts");
//                for (JsonElement c : contracts) {
//                    contractSet.add(c);
//                }
//            }
            return contractSet;
        }

        /**
         * @param params branch id
         *
         * @return fee state
         */
        public BigInteger feeState(JsonObject params) {
            String branchId = params.get(BRANCH_ID).getAsString();
//            StemContractStateValue stateValue = getBranchStateValue(branchId);
//            BigDecimal result = BigDecimal.ZERO;
//            if (isBranchExist(branchId)) {
//                Long currentHeight = txReceipt.getBlockHeight();
//                Long createPointHeight = stateValue.getBlockHeight();
//                Long height = currentHeight - createPointHeight;
//
//                //1block to 1yeed
//                BigDecimal currentFee = stateValue.getFee();
//                result = currentFee.subtract(BigDecimal.valueOf(height));
//            }
//            return result.longValue() > 0 ? result : BigDecimal.ZERO;
            return BigInteger.ZERO;

        }

//        private BigInteger feeState(StemContractStateValue stateValue) {
//            BigInteger currentFee = stateValue.getFee();
//            if (currentFee.longValue() > 0) {
//                Long currentHeight = txReceipt.getBlockHeight();
//                Long createPointHeight = stateValue.getBlockHeight();
//                Long overTimeHeight = currentHeight - createPointHeight;
//                return currentFee.subtract(BigDecimal.valueOf(overTimeHeight));
//            }
//            return BigInteger.ZERO;
//        }

//        private Boolean isEnoughFee(StemContractStateValue stateValue) {
//            return feeState(stateValue).longValue() > 0;
//        }

        private boolean isBranchExist(String branchId) {
            return state.contains(branchId);
        }

//        private void addBranchId(String newBranchId) {
//            if (!isBranchExist(newBranchId.toString())) {
//                JsonArray branchIds = new JsonArray();
//                for (String branchId : getBranchIdList()) {
//                    branchIds.add(branchId);
//                }
//                JsonObject obj = new JsonObject();
//                branchIds.add(newBranchId.toString());
//                obj.add("branchIds", branchIds);
//                state.put(branchIdListKey, obj);
//
//            }
//        }
//
//        private void addTxId(String branchId) {
//            if (isBranchExist(branchId.toString())
//                    && txReceipt.getTxId() != null) {
//                JsonObject bid = new JsonObject();
//                bid.addProperty("branchId", branchId.toString());
//                state.put(txReceipt.getTxId(), bid);
//            }
//        }
//
//        private boolean isBranchIdValid(String branchId, Branch branch) {
//            return branchId.equals(branch.getBranchId());
//        }
//
//        private StemContractStateValue getBranchStateValue(String branchId) {
//            JsonObject json = state.get(branchId);
//            if (json == null) {
//                return null;
//            } else {
//                return new StemContractStateValue(json);
//            }
//        }
    }
}