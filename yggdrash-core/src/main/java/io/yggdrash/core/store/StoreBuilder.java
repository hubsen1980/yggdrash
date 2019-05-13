/*
 * Copyright 2018 Akashic Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.yggdrash.core.store;

import io.yggdrash.common.config.DefaultConfig;
import io.yggdrash.common.store.StateStore;
import io.yggdrash.common.store.datasource.DbSource;
import io.yggdrash.common.store.datasource.HashMapDbSource;
import io.yggdrash.common.store.datasource.LevelDbDataSource;
import io.yggdrash.core.blockchain.BranchId;

public class StoreBuilder {

    private DefaultConfig config;
    private BranchId branchId;
    private String consensusAlgorithm;
    private BlockStoreFactory blockStoreFactory;

    public StoreBuilder setConfig(DefaultConfig config) {
        this.config = config;
        return this;
    }

    public DefaultConfig getConfig() {
        return config;
    }

    public StoreBuilder setBranchId(BranchId branchId) {
        this.branchId = branchId;
        return this;
    }

    public StoreBuilder setConsensusAlgorithm(String consensusAlgorithm) {
        this.consensusAlgorithm = consensusAlgorithm;
        return this;
    }

    public StoreBuilder setBlockStoreFactory(BlockStoreFactory blockStoreFactory) {
        this.blockStoreFactory = blockStoreFactory;
        return this;
    }

    public ContractStore buildContractStore() {
        return new ContractStore(buildBranchStore(), buildStateStore(), buildTransactionReceiptStore());
    }

    public ConsensusBlockStore buildBlockStore() {
        DbSource dbSource = getDbSource(branchId + "/blocks");
        return blockStoreFactory.create(consensusAlgorithm, dbSource);
    }

    public PeerStore buildPeerStore() {
        return new PeerStore(getDbSource(branchId + "/peers"));
    }

    public BranchStore buildBranchStore() {
        return new BranchStore(getDbSource(branchId + "/branch"));
    }

    public TransactionStore buildTransactionStore() {
        return new TransactionStore(getDbSource(branchId + "/txs"));
    }

    private StateStore buildStateStore() {
        return new StateStore(getDbSource(branchId + "/state"));
    }

    public TransactionReceiptStore buildTransactionReceiptStore() {
        return new TransactionReceiptStore(getDbSource(branchId + "/txreceipt"));
    }

    private DbSource<byte[], byte[]> getDbSource(String name) {
        if (config.isProductionMode()) {
            return new LevelDbDataSource(config.getDatabasePath(), name);
        } else {
            return new HashMapDbSource();
        }
    }

    public static StoreBuilder newBuilder() {
        return new StoreBuilder();
    }

    public interface BlockStoreFactory {
        ConsensusBlockStore create(String consensusAlgorithm, DbSource dbSource);
    }
}
