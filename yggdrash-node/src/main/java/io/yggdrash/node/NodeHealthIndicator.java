/*
 * Copyright 2018 Akashic Foundation
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

package io.yggdrash.node;

import io.yggdrash.common.config.Constants;
import io.yggdrash.common.config.DefaultConfig;
import io.yggdrash.core.blockchain.BlockChain;
import io.yggdrash.core.blockchain.BranchGroup;
import io.yggdrash.core.blockchain.BranchId;
import io.yggdrash.core.net.NodeStatus;
import io.yggdrash.core.p2p.PeerDialer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class NodeHealthIndicator implements HealthIndicator, NodeStatus {
    private static final Logger log = LoggerFactory.getLogger(NodeHealthIndicator.class);
    private static final Status SYNC = new Status("SYNC", "Synchronizing..");
    private static final Status UPDATE = new Status("UPDATE", "Updating..");

    private final AtomicReference<Health> health = new AtomicReference<>(Health.down().build());
    private final DefaultConfig defaultConfig;
    private final BranchGroup branchGroup;
    private final PeerDialer peerDialer;

    @Autowired
    public NodeHealthIndicator(DefaultConfig defaultConfig, BranchGroup branchGroup,
                               PeerDialer peerDialer) {
        this.defaultConfig = defaultConfig;
        this.branchGroup = branchGroup;
        this.peerDialer = peerDialer;
    }

    @Override
    public Health health() {
        updateDetail(health.get().getStatus());
        return health.get();
    }

    @Override
    public void up() {
        try {
            updateDetail(Status.UP);
        } catch (Exception e) {
            log.debug("Status up() is failed. {}", e.getMessage());
        } finally {
            log.debug("nodeStatus -> {}", health.get().getStatus());
        }
    }

    @Override
    public void sync() {
        try {
            updateDetail(SYNC);
        } catch (Exception e) {
            log.debug("Status sync() is failed. {}", e.getMessage());
        } finally {
            log.debug("nodeStatus -> {}", health.get().getStatus());
        }
    }

    @Override
    public void update() {
        try {
            updateDetail(UPDATE);
        } catch (Exception e) {
            log.debug("Status update() is failed. {}", e.getMessage());
        } finally {
            log.debug("nodeStatus -> {}", health.get().getStatus());
        }
    }

    @Override
    public boolean isUpdateStatus() {
        return health.get().getStatus().equals(UPDATE);
    }

    @Override
    public boolean isUpStatus() {
        return health.get().getStatus().equals(Status.UP);
    }

    @Override
    public boolean isSyncStatus() {
        return health.get().getStatus().equals(SYNC);
    }

    private void updateDetail(Status status) {
        Health.Builder builder = Health.status(status);
        builder.withDetail("name", Constants.NODE_NAME);
        builder.withDetail("version", Constants.NODE_VERSION);
        builder.withDetail("p2pVersion", defaultConfig.getNetworkP2PVersion());
        builder.withDetail("network", defaultConfig.getNetwork());
        // Add Node BranchIds and block index
        Map<BranchId, Long> branches = new HashMap<>();
        for (BlockChain blockChain : branchGroup.getAllBranch()) {
            branches.put(blockChain.getBranchId(), blockChain.getBlockChainManager().getLastIndex());
        }

        builder.withDetail("branches", branches);
        builder.withDetail("activePeers", peerDialer.handlerCount());
        health.set(builder.build());
    }
}
