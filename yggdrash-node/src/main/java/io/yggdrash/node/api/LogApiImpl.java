/*
 * Copyright 2019 Akashic Foundation
 * Licensed under the Apache License, Version 2.0 (the “License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package io.yggdrash.node.api;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import io.yggdrash.core.blockchain.BranchGroup;
import io.yggdrash.core.blockchain.BranchId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AutoJsonRpcServiceImpl
public class LogApiImpl implements LogApi {

    private final BranchGroup branchGroup;

    @Autowired
    public LogApiImpl(BranchGroup branchGroup) {
        this.branchGroup = branchGroup;
    }

    @Override
    public String getLog(String branchId, long index) {
        return branchGroup.getBranch(BranchId.of(branchId)).getContractManager().getLog(index);
    }

    @Override
    public List<String> getLogs(String branchId, long start, long offset) {
        return branchGroup.getBranch(BranchId.of(branchId)).getContractManager().getLogs(start, offset);
    }

    @Override
    public List<String> getLogs(String branchId, String regex, long start, long offset) {
        return getLogs(branchId, start, offset).stream()
                .filter(log -> Pattern.compile(regex).matcher(log).find())
                .collect(Collectors.toList());
    }

    @Override
    public long curIndex(String branchId) {
        return branchGroup.getBranch(BranchId.of(branchId)).getContractManager().getCurLogIndex();
    }
}