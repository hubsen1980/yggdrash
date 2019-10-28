/*
 * Copyright 2019 Akashic Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.yggdrash.node.sync;

import ch.qos.logback.classic.Level;
import io.yggdrash.BlockChainTestUtils;
import io.yggdrash.TestConstants;
import io.yggdrash.core.blockchain.BlockChainManager;
import io.yggdrash.node.TcpNodeTesting;
import io.yggdrash.node.TestNode;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeSyncLimitTesting extends TcpNodeTesting {

    //@Test
    public void testLargeBlockList() {
        rootLogger.setLevel(Level.INFO);

        // arrange
        // Node1 : start -> generate tx -> generate block
        TestNode node1 = createAndStartNode(8888, true);
        int blockCount = 23;
        generateTxAndBlock(node1, blockCount, 500);
        assert node1.getDefaultBranch().getBlockChainManager().getLastIndex() == blockCount;
        // Node2 : start -> add route node2 -> node1
        TestNode node2 = createAndStartNode(8889, true);
        node2.peerTableGroup.addPeer(TestConstants.yggdrash(), node1.peerTableGroup.getOwner());

        // act sync block
        node2.bootstrapping();

        // assert
        assertThat(node2.getDefaultBranch().getBlockChainManager().getLastIndex()).isEqualTo(21);
    }

    //@Test
    public void testLargeBlock() {
        rootLogger.setLevel(Level.INFO);

        // arrange
        // Node1 : start -> generate tx -> generate block
        TestNode node1 = createAndStartNode(8888, true);
        BlockChainManager node1BlockChainManager = node1.getDefaultBranch().getBlockChainManager();
        int blockCount = 1;
        generateTxAndBlock(node1, blockCount, 16000);
        assert node1BlockChainManager.getLastIndex() == blockCount;
        // Node2 : start -> add route node2 -> node1
        TestNode node2 = createAndStartNode(8889, true);
        node2.peerTableGroup.addPeer(TestConstants.yggdrash(), node1.peerTableGroup.getOwner());

        // act sync block
        node2.bootstrapping();

        node1.shutdown();
        node2.shutdown();

        // assert
        BlockChainManager node2BlockChainManager = node2.getDefaultBranch().getBlockChainManager();
        assertThat(node2BlockChainManager.getLastIndex()).isEqualTo(node1BlockChainManager.getLastIndex());
    }

    private void generateTxAndBlock(TestNode node, int blockCount, int txCount) {
        for (int i = 0; i < blockCount; i++) {
            for (int j = 0; j < txCount; j++) {
                node.getBranchGroup().addTransaction(BlockChainTestUtils.createTransferTx());
            }
            node.generateBlock();
        }
    }
}