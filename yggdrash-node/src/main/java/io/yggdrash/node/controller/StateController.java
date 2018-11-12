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

package io.yggdrash.node.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.yggdrash.core.BlockChain;
import io.yggdrash.core.BlockHusk;
import io.yggdrash.core.BranchGroup;
import io.yggdrash.core.BranchId;
import io.yggdrash.core.TransactionHusk;
import io.yggdrash.core.genesis.BranchJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("branches")
public class StateController {
    private static final Logger log = LoggerFactory.getLogger(StateController.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final BranchGroup branchGroup;

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    public StateController(BranchGroup branchGroup) {
        this.branchGroup = branchGroup;
    }

    @GetMapping
    public ResponseEntity<Map<String, BranchJson>> getBranches() {
        Map<String, BranchJson> branchMap = new HashMap<>();
        for (BlockChain branch : branchGroup.getAllBranch()) {
            BlockHusk genesis = branch.getBlockByIndex(0);
            BranchJson branchJson = getBranchJson(genesis);
            branchMap.put(branch.getBranchId().toString(), branchJson);
        }
        return ResponseEntity.ok(branchMap);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Long>> getAll() {
        Map<String, Long> activeMap = new HashMap<>();
        branchGroup.getAllBranch().forEach(branch ->
                activeMap.put(branch.getBranchId().toString(), branch.getLastIndex()));
        return ResponseEntity.ok(activeMap);
    }

    @GetMapping("/{branchId}/states")
    public ResponseEntity<List> getStates(@PathVariable(name = "branchId") String branchId) {
        List state = branchGroup.getStateStore(BranchId.of(branchId)).getStateList();
        return ResponseEntity.ok(state);
    }

    private BranchJson getBranchJson(BlockHusk genesis) {
        for (TransactionHusk tx : genesis.getBody()) {
            JsonArray txBody = tx.toJsonObject().getAsJsonArray("body");
            if (txBody.size() != 0) {
                return getBranchJson(txBody);
            }
        }
        return new BranchJson();
    }

    private BranchJson getBranchJson(JsonArray txBody) {
        try {
            JsonElement firstTx = txBody.get(0);
            if (!firstTx.isJsonObject()) {
                log.warn("Genesis tx is not jsonObject.");
            } else if (!firstTx.getAsJsonObject().has("branch")) {
                log.warn("Genesis tx does not contains branch property.");
            } else {
                JsonObject branchJson = firstTx.getAsJsonObject().get("branch").getAsJsonObject();
                return MAPPER.readValue(branchJson.toString(), BranchJson.class);
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        return new BranchJson();
    }
}
