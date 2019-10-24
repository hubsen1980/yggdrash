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

import com.woozooha.adonistrack.filter.AdonistrackFilter;
import io.yggdrash.common.config.Constants.ActiveProfiles;
import io.yggdrash.gateway.GatewayConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class YggdrashNodeApp {
    public static void main(String[] args) {
        SpringApplication.run(YggdrashNodeApp.class, args);
    }

    @Bean
    public FilterRegistrationBean<AdonistrackFilter> profileFilter() {
        FilterRegistrationBean<AdonistrackFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AdonistrackFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

    @Configuration
    @Profile(ActiveProfiles.GATEWAY)
    @Import(GatewayConfiguration.class)
    class NodeConfig {
    }
}
