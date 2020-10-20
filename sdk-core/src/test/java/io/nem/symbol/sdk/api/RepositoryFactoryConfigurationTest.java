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

import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.CurrencyBuilder;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
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
    Currency currency =
        new CurrencyBuilder(NamespaceId.createFromName("my.custom.currency"), 6).build();

    Currency harvest =
        new CurrencyBuilder(NamespaceId.createFromName("my.custom.harvest"), 3).build();
    configuration.withNetworkCurrencies(new NetworkCurrencies(currency, harvest));
    Assertions.assertEquals(epochAdjustment, configuration.getEpochAdjustment());
    Assertions.assertEquals("http://localhost:3000", configuration.getBaseUrl());
    Assertions.assertEquals("abc", configuration.getGenerationHash());
    Assertions.assertEquals(NetworkType.MAIN_NET, configuration.getNetworkType());
    Assertions.assertEquals(currency, configuration.getNetworkCurrencies().getCurrency());
    Assertions.assertEquals(harvest, configuration.getNetworkCurrencies().getHarvest());
  }

  @Test
  void constructorAndSet() {
    RepositoryFactoryConfiguration configuration =
        new RepositoryFactoryConfiguration("http://localhost:3000");
    configuration.setNetworkType(NetworkType.MAIN_NET);
    configuration.setGenerationHash("abc");
    Currency currency =
        new CurrencyBuilder(NamespaceId.createFromName("my.custom.currency"), 6).build();

    Duration epochAdjustment = Duration.ofMillis(100L);
    configuration.setEpochAdjustment(epochAdjustment);
    Currency harvest =
        new CurrencyBuilder(NamespaceId.createFromName("my.custom.harvest"), 3).build();

    configuration.setNetworkCurrencies(new NetworkCurrencies(currency, harvest));

    Assertions.assertEquals(Duration.ofMillis(100L), configuration.getEpochAdjustment());
    Assertions.assertEquals("http://localhost:3000", configuration.getBaseUrl());
    Assertions.assertEquals("abc", configuration.getGenerationHash());
    Assertions.assertEquals(NetworkType.MAIN_NET, configuration.getNetworkType());
    Assertions.assertEquals(currency, configuration.getNetworkCurrencies().getCurrency());
    Assertions.assertEquals(harvest, configuration.getNetworkCurrencies().getHarvest());
  }
}
