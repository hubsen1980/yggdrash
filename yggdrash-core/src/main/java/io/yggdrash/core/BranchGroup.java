/*
 * Copyright 2018 Akashic Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.yggdrash.core;

import com.google.gson.JsonObject;
import io.yggdrash.common.Sha3Hash;
import io.yggdrash.contract.Contract;
import io.yggdrash.core.exception.DuplicatedException;
import io.yggdrash.core.exception.FailedOperationException;
import io.yggdrash.core.store.StateStore;
import io.yggdrash.core.store.TransactionReceiptStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BranchGroup {

    private Map<String, BlockChain> branches = new ConcurrentHashMap<>();
    private static BlockChain chain;

    public void addBranch(BranchId branchId, BlockChain blockChain) {
        if (branches.containsKey(branchId.toString())) {
            throw new DuplicatedException(branchId.toString());
        }
        chain = blockChain; // TODO remove
        branches.put(branchId.toString(), blockChain);
    }

    public BlockChain getBranch(BranchId id) {
        return branches.get(id.toString());
    }

    public Collection<BlockChain> getAllBranch() {
        return branches.values();
    }

    public TransactionHusk addTransaction(TransactionHusk tx) {
        return chain.addTransaction(tx);
    }

    public long getLastIndex() {
        return chain.getLastIndex();
    }

    public List<TransactionHusk> getTransactionList() {
        return chain.getTransactionList();
    }

    public TransactionHusk getTxByHash(String id) {
        return getTxByHash(new Sha3Hash(id));
    }

    public TransactionHusk getTxByHash(Sha3Hash hash) {
        return chain.getTxByHash(hash);
    }

    public BlockHusk generateBlock(Wallet wallet) {
        return chain.generateBlock(wallet);
    }

    public BlockHusk addBlock(BlockHusk block) {
        return chain.addBlock(block);
    }

    public BlockHusk getBlockByIndexOrHash(String indexOrHash) {
        if (isNumeric(indexOrHash)) {
            int index = Integer.parseInt(indexOrHash);
            return chain.getBlockByIndex(index);
        } else {
            return chain.getBlockByHash(indexOrHash);
        }
    }

    public int getBranchSize() {
        return branches.size();
    }

    public StateStore<?> getStateStore() {
        return chain.getRuntime().getStateStore();
    }

    public TransactionReceiptStore getTransactionReceiptStore() {
        return chain.getRuntime().getTransactionReceiptStore();
    }

    public Contract getContract() {
        return chain.getContract();
    }

    public JsonObject query(JsonObject query) {
        try {
            return chain.getRuntime().query(chain.getContract(), query);
        } catch (Exception e) {
            throw new FailedOperationException(e);
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
