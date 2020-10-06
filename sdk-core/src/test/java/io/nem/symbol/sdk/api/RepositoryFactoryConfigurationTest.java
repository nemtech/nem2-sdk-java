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
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencyBuilder;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test for RepositoryFactoryConfiguration */
class RepositoryFactoryConfigurationTest {

  @Test
  void constructorAndWith() {
    RepositoryFactoryConfiguration configuration =
        new RepositoryFactoryConfiguration("http://localhost:3000");
    configuration.withNetworkType(NetworkType.MAIN_NET);
    configuration.withGenerationHash("abc");
    Duration epochAdjustment = Duration.ofMillis(100L);
    configuration.withEpochAdjustment(epochAdjustment);
    NetworkCurrency networkCurrency =
        new NetworkCurrencyBuilder(NamespaceId.createFromName("my.custom.currency"), 6).build();
    configuration.withNetworkCurrency(networkCurrency);

    NetworkCurrency networkHarvestCurrency =
        new NetworkCurrencyBuilder(NamespaceId.createFromName("my.custom.harvest"), 3).build();
    configuration.withHarvestCurrency(networkHarvestCurrency);
    Assertions.assertEquals(epochAdjustment, configuration.getEpochAdjustment());
    Assertions.assertEquals("http://localhost:3000", configuration.getBaseUrl());
    Assertions.assertEquals("abc", configuration.getGenerationHash());
    Assertions.assertEquals(NetworkType.MAIN_NET, configuration.getNetworkType());
    Assertions.assertEquals(networkCurrency, configuration.getNetworkCurrency());
    Assertions.assertEquals(networkHarvestCurrency, configuration.getHarvestCurrency());
  }

  @Test
  void constructorAndSet() {
    RepositoryFactoryConfiguration configuration =
        new RepositoryFactoryConfiguration("http://localhost:3000");
    configuration.setNetworkType(NetworkType.MAIN_NET);
    configuration.setGenerationHash("abc");
    NetworkCurrency networkCurrency =
        new NetworkCurrencyBuilder(NamespaceId.createFromName("my.custom.currency"), 6).build();
    configuration.setNetworkCurrency(networkCurrency);

    Duration epochAdjustment = Duration.ofMillis(100L);
    configuration.setEpochAdjustment(epochAdjustment);
    NetworkCurrency networkHarvestCurrency =
        new NetworkCurrencyBuilder(NamespaceId.createFromName("my.custom.harvest"), 3).build();
    configuration.setHarvestCurrency(networkHarvestCurrency);

    Assertions.assertEquals(Duration.ofMillis(100L), configuration.getEpochAdjustment());
    Assertions.assertEquals("http://localhost:3000", configuration.getBaseUrl());
    Assertions.assertEquals("abc", configuration.getGenerationHash());
    Assertions.assertEquals(NetworkType.MAIN_NET, configuration.getNetworkType());
    Assertions.assertEquals(networkCurrency, configuration.getNetworkCurrency());
    Assertions.assertEquals(networkHarvestCurrency, configuration.getHarvestCurrency());
  }
}
