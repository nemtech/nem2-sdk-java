/*
 * Copyright 2020 NEM
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

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.NetworkCurrencyService;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NetworkCurrencyServiceIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNetworkCurrencies(RepositoryType type) {

        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        NetworkCurrencyService service = new NetworkCurrencyServiceImpl(repositoryFactory);

        List<NetworkCurrency> mosaicConfigurations = get(
            service.getNetworkCurrencies());

        System.out.println(toJson(mosaicConfigurations));

        Assertions.assertTrue(mosaicConfigurations.size() > 0);
        Assertions.assertTrue(mosaicConfigurations.size() < 3);

        Assertions
            .assertTrue(mosaicConfigurations.contains(get(repositoryFactory.getNetworkCurrency())));
        Assertions
            .assertTrue(mosaicConfigurations.contains(get(repositoryFactory.getHarvestCurrency())));

    }
}
