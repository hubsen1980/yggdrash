package io.yggdrash.core;

import com.google.gson.JsonObject;
import io.yggdrash.util.HashUtils;
import io.yggdrash.util.SerializeUtils;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.Serializable;

public class Transaction implements Serializable {

    // Header
    private TransactionHeader header;

    // Data
    private JsonObject data;


    /**
     * Transaction Constructor
     * @param from account for creating transaction
     * @param data transaction data(Json)
     */
    public Transaction(Account from, JsonObject data) throws IOException {
        makeTransaction(from, data);
    }

    public void makeTransaction(Account from, JsonObject data) {

        // 1. make data
        this.data = data;

        // 2. make header
        try {
            byte[] bin = SerializeUtils.serialize(data);
            this.header = new TransactionHeader(from, HashUtils.sha256(bin), bin.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * get transaction hash
     * @return transaction hash
     */
    public String getHashString() {
        return this.header.hashString();
    }

    /**
     * get transaction hash
     * @return transaction hash
     */
    public byte[] getHash() {
        return this.header.hash();
    }

    /**
     * get account for created tx
     * @return transaction hash
     */
    public String getFrom() {
        return Hex.encodeHexString(header.getFrom());
    }

    /**
     * get transaction data
     * @return tx data
     */
    public String getData() {
        return this.data.toString();
    }

    /**
     * print transaction
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.header.toString());
        buffer.append("transactionData=").append(this.data.toString());

        return buffer.toString();
    }

}