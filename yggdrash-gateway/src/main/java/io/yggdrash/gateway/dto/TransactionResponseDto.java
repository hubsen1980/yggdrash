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

package io.yggdrash.gateway.dto;

import java.util.List;
import java.util.Map;

public class TransactionResponseDto {

    public String txHash;
    public boolean status;
    public Map<String, List<String>> logs;

    public static TransactionResponseDto createBy(String txHash, boolean status, Map<String, List<String>> logs) {
        TransactionResponseDto txResDto = new TransactionResponseDto();
        txResDto.txHash = txHash;
        txResDto.status = status;
        txResDto.logs = logs;

        return txResDto;
    }

    public static TransactionResponseDto createBy(Map<String, List<String>> logs) {
        TransactionResponseDto txResDto = new TransactionResponseDto();
        txResDto.logs = logs;

        return txResDto;
    }
}