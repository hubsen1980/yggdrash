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

package io.yggdrash.gateway.controller;

import com.google.gson.JsonObject;
import io.yggdrash.TestUtils;
import io.yggdrash.common.util.Utils;
import io.yggdrash.core.blockchain.Branch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.autoconfigure.RefreshEndpointAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BranchController.class)
@Import(RefreshEndpointAutoConfiguration.class)
@IfProfileValue(name = "spring.profiles.active", value = "ci")
public class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldGetBranches() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/branches"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();
        String contentAsString = response.getContentAsString();
        JsonObject branchJson = Utils.parseJsonObject(contentAsString);
        for (String key : branchJson.keySet()) {
            Branch branch = Branch.of(branchJson.getAsJsonObject(key));
            assertThat(branch.getBranchId().toString()).isEqualTo(key);
        }

    }

    @Test
    public void shouldGetStemBrancheStates() throws Exception {
        mockMvc.perform(get("/branches/" + TestUtils.STEM + "/states"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
