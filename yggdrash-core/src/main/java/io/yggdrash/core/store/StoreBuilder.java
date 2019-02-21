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
import io.yggdrash.core.blockchain.BlockchainMetaInfo;
import io.yggdrash.core.blockchain.BranchId;
import io.yggdrash.core.store.datasource.DbSource;
import io.yggdrash.core.store.datasource.HashMapDbSource;
import io.yggdrash.core.store.datasource.LevelDbDataSource;
import java.io.File;

public class StoreBuilder {

    private final DefaultConfig config;
    private BranchId branchId;

    public StoreBuilder(DefaultConfig config) {
        this.config = config;
    }

    public DefaultConfig getConfig() {
        return config;
    }

    public StoreBuilder setBranchId(BranchId branchId) {
        this.branchId = branchId;
        return this;
    }

    public Store build(StoreTypeEnum typeEnum) {
        DbSource<byte[], byte[]> dbSource;
        dbSource = getDbSource(branchId + File.separator + typeEnum);
        switch (typeEnum) {
            case BLOCK_STORE:
                return new BlockStore(dbSource);
            case TRANSACTION_STORE:
                return new TransactionStore(dbSource);
            case TRANSACTION_RECEIPT_STORE:
                return new TransactionReceiptStore(dbSource);
            case PEER_STORE:
                return new PeerStore(dbSource);
            case META_STORE:
                return new MetaStore(dbSource);
            case STATE_STORE:
                return new StateStore(dbSource);
            case BRANCH_STORE:
                return new BranchStore(dbSource);
        }
        return null;
    }

    public BlockStore buildBlockStore(BranchId branchId) {
        return new BlockStore(getDbSource(branchId + "/blocks"));
    }

    public TransactionStore buildTxStore(BranchId branchId) {
        return new TransactionStore(getDbSource(branchId + "/txs"));
    }

    public PeerStore buildPeerStore(BranchId branchId) {
        return new PeerStore(getDbSource(branchId + "/peers"));
    }

    public MetaStore buildMetaStore(BranchId branchId) {
        MetaStore store = new MetaStore(getDbSource(branchId + "/meta"));
        store.put(BlockchainMetaInfo.BRANCH.toString(), branchId.toString());
        return store;
    }

    public StateStore buildStateStore(BranchId branchId) {
        return new StateStore(getDbSource(branchId + "/state"));
    }

    public TransactionReceiptStore buildTransactionReceiptStore(BranchId branchId) {
        return new TransactionReceiptStore(getDbSource(branchId + "/txreceipt"));
    }

    private DbSource<byte[], byte[]> getDbSource(String name) {
        if (config.isProductionMode()) {
            return new LevelDbDataSource(config.getDatabasePath(), name);
        } else {
            return new HashMapDbSource();
        }
    }
}
