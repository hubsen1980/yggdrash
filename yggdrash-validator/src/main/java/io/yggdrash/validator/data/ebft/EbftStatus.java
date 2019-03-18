package io.yggdrash.validator.data.ebft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import io.yggdrash.common.crypto.HashUtil;
import io.yggdrash.common.util.TimeUtils;
import io.yggdrash.common.utils.ByteUtil;
import io.yggdrash.core.wallet.Wallet;
import io.yggdrash.proto.EbftProto;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EbftStatus {
    private long index;
    private final List<EbftBlock> unConfirmedEbftBlockList = new ArrayList<>();
    private long timestamp;
    private byte[] signature;

    public EbftStatus(long index,
                      List<EbftBlock> unConfirmedEbftBlockList, Wallet wallet) {
        this.index = index;
        if (unConfirmedEbftBlockList != null) {
            this.unConfirmedEbftBlockList.addAll(unConfirmedEbftBlockList);
        }
        this.timestamp = TimeUtils.time();
        this.signature = wallet.sign(getHashForSigning(), true);
    }

    public EbftStatus(JsonObject jsonObject) {
        this.index = jsonObject.get("index").getAsLong();
        if (jsonObject.get("unConfirmedList") != null) {
            for (JsonElement pbftMessageJsonElement : jsonObject.get("unConfirmedList").getAsJsonArray()) {
                EbftBlock ebftBlock = new EbftBlock(pbftMessageJsonElement.getAsJsonObject());
                this.unConfirmedEbftBlockList.add(ebftBlock);
            }
        }
        this.timestamp = jsonObject.get("timestamp").getAsLong();
        this.signature = Hex.decode(jsonObject.get("signature").getAsString());
    }

    public EbftStatus(EbftProto.EbftStatus nodeStatus) {
        this.index = nodeStatus.getIndex();
        for (EbftProto.EbftBlock block :
                nodeStatus.getUnConfirmedEbftBlockList().getEbftBlockListList()) {
            this.unConfirmedEbftBlockList.add(new EbftBlock(block));
        }
        this.timestamp = nodeStatus.getTimestamp();
        this.signature = nodeStatus.getSignature().toByteArray();
    }

    public long getIndex() {
        return index;
    }

    public List<EbftBlock> getUnConfirmedEbftBlockList() {
        return unConfirmedEbftBlockList;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getHashForSigning() {
        ByteArrayOutputStream dataForSigning = new ByteArrayOutputStream();

        try {
            dataForSigning.write(ByteUtil.longToBytes(index));
            for (EbftBlock ebftBlock : unConfirmedEbftBlockList) {
                dataForSigning.write(ebftBlock.getHash());
            }
            dataForSigning.write(ByteUtil.longToBytes(timestamp));
        } catch (IOException e) {
            return null;
        }

        return HashUtil.sha3(dataForSigning.toByteArray());
    }

    public static boolean verify(EbftStatus ebftStatus) {
        if (ebftStatus != null) {
            return Wallet.verify(
                    ebftStatus.getHashForSigning(), ebftStatus.getSignature(), true);
        }
        return false;
    }

    public static EbftProto.EbftStatus toProto(EbftStatus ebftStatus) {
        EbftProto.EbftStatus.Builder protoBlockStatus = EbftProto.EbftStatus.newBuilder()
                .setIndex(ebftStatus.getIndex())
                .setUnConfirmedEbftBlockList(
                        EbftBlock.toProtoList(ebftStatus.getUnConfirmedEbftBlockList()))
                .setTimestamp(ebftStatus.getTimestamp())
                .setSignature(ByteString.copyFrom(ebftStatus.getSignature()));
        return protoBlockStatus.build();
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("index", this.index);

        JsonArray unConfirmedList = new JsonArray();
        for (EbftBlock ebftBlock : this.unConfirmedEbftBlockList) {
            unConfirmedList.add(ebftBlock.toJsonObject());
        }
        if (unConfirmedList.size() > 0) {
            jsonObject.add("unConfirmedList", unConfirmedList);
        }

        jsonObject.addProperty("timestamp", this.timestamp);
        jsonObject.addProperty("signature", Hex.toHexString(this.signature));

        return jsonObject;
    }

    public byte[] toBinary() {
        return this.toJsonObject().toString().getBytes(StandardCharsets.UTF_8);
    }

    public boolean equals(EbftStatus newStatus) {
        if (newStatus == null) {
            return false;
        }
        return Arrays.equals(this.toBinary(), newStatus.toBinary());
    }

    public void clear() {
        for (EbftBlock ebftBlock : this.unConfirmedEbftBlockList) {
            if (ebftBlock != null) {
                ebftBlock.clear();
            }
        }
        this.unConfirmedEbftBlockList.clear();
    }
}