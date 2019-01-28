package io.yggdrash.validator.data;

import com.google.protobuf.ByteString;
import io.yggdrash.common.util.TimeUtils;
import io.yggdrash.proto.PbftProto;

public class PbftStatus {
    private PbftBlock lastConfirmedBlock;
    private long timestamp;
    private byte[] signature;

    public PbftStatus(PbftBlock lastConfirmedBlock,
                      long timestamp,
                      byte[] signature) {
        this.lastConfirmedBlock = lastConfirmedBlock;

        if (timestamp == 0L) {
            this.timestamp = TimeUtils.time();
        } else {
            this.timestamp = timestamp;
        }

        this.signature = signature;
    }

    public PbftStatus(PbftProto.PbftStatus pbftStatus) {
        this.lastConfirmedBlock = new PbftBlock(pbftStatus.getLastConfirmedBlock());
        this.timestamp = pbftStatus.getTimestamp();
        this.signature = pbftStatus.getSignature().toByteArray();
    }

    public PbftBlock getLastConfirmedBlock() {
        return lastConfirmedBlock;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getSignature() {
        return signature;
    }

    public static PbftProto.PbftStatus toProto(PbftStatus pbftStatus) {
        PbftProto.PbftStatus.Builder protoPbftStatus = PbftProto.PbftStatus.newBuilder()
                .setLastConfirmedBlock(
                        PbftBlock.toProto(pbftStatus.getLastConfirmedBlock()))
                .setTimestamp(pbftStatus.getTimestamp())
                .setSignature(ByteString.copyFrom(pbftStatus.getSignature()));
        return protoPbftStatus.build();
    }

}