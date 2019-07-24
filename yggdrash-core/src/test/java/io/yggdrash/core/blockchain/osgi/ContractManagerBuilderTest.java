/*
 * Copyright 2019 Akashic Foundation
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

package io.yggdrash.core.blockchain.osgi;

import io.yggdrash.BlockChainTestUtils;
import io.yggdrash.common.config.DefaultConfig;
import io.yggdrash.common.contract.BranchContract;
import io.yggdrash.core.blockchain.BranchId;
import io.yggdrash.core.blockchain.SystemProperties;
import io.yggdrash.core.blockchain.genesis.GenesisBlock;
import io.yggdrash.core.blockchain.osgi.framework.BootFrameworkConfig;
import io.yggdrash.core.blockchain.osgi.framework.BootFrameworkLauncher;
import io.yggdrash.core.blockchain.osgi.framework.BundleService;
import io.yggdrash.core.blockchain.osgi.framework.BundleServiceImpl;
import io.yggdrash.core.blockchain.osgi.framework.FrameworkConfig;
import io.yggdrash.core.blockchain.osgi.framework.FrameworkLauncher;
import io.yggdrash.core.store.BlockChainStore;
import io.yggdrash.core.store.BlockChainStoreBuilder;
import io.yggdrash.core.store.ContractStore;
import io.yggdrash.core.store.PbftBlockStoreMock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContractManagerBuilderTest {
    private static final Logger log = LoggerFactory.getLogger(ContractManagerBuilderTest.class);

    @Test
    public void build() {
        DefaultConfig config = new DefaultConfig();
        GenesisBlock genesis = BlockChainTestUtils.getGenesis();
        SystemProperties systemProperties = BlockChainTestUtils.createDefaultSystemProperties();

        BranchId branchId = genesis.getBranchId();


        BlockChainStore bcStore = BlockChainStoreBuilder.newBuilder(branchId)
                .withDataBasePath(config.getDatabasePath())
                .withProductionMode(config.isProductionMode())
                .setConsensusAlgorithm(null)
                .setBlockStoreFactory(PbftBlockStoreMock::new)
                .build();

        ContractStore contractStore = bcStore.getContractStore();

        FrameworkConfig bootFrameworkConfig = new BootFrameworkConfig(config, branchId);
        FrameworkLauncher bootFrameworkLauncher = new BootFrameworkLauncher(bootFrameworkConfig);
        BundleService bundleService = new BundleServiceImpl();

        List<BranchContract> genesisContractList = genesis.getBranch().getBranchContracts();

        assert genesisContractList.size() > 0;

        ContractManager manager = ContractManagerBuilder.newInstance()
                .withGenesis(genesis)
                .withBootFramework(bootFrameworkLauncher)
                .withBundleManager(bundleService)
                .withDefaultConfig(config)
                .withContractStore(contractStore)
                .withLogStore(bcStore.getLogStore()) // is this logstore for what?
                .withSystemProperties(systemProperties)
                .build();

        assert manager != null;
        assert manager.getContractExecutor() != null;

        // Contract File
//        String filePath = getClass().getClassLoader()
//                .getResource("contracts/96206ff28aead93a49272379a85191c54f7b33c0.jar")
//                .getFile();
//        File contractFile = new File(filePath);
//
//        ContractVersion version = ContractVersion.of("TEST".getBytes());
//        if (contractFile.exists() && !manager.checkExistContract(
//                branchId.toString(), "io.yggdrash.contract.coin.CoinContract","1.0.0")) {
//            Bundle bundle = null;
//            try {
//                bundle = manager.installTest(branchId.toString(), version, true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (BundleException e) {
//                e.printStackTrace();
//            }
//            assert bundle != null;
//        }


        for (ContractStatus cs : manager.searchContracts(branchId.toString())) {
            log.debug("Description {}", cs.getDescription());
            log.debug("Location {}", cs.getLocation());
            log.debug("SymbolicName {}", cs.getSymbolicName());
            log.debug("Version {}", cs.getVersion());
            log.debug(Long.toString(cs.getId()));
        }

        ///manager.loadUserContract();
    }
}
