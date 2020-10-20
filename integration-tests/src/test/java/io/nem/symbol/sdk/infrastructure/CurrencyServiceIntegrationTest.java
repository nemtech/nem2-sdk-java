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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.api.CurrencyService;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CurrencyServiceIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getNetworkCurrencies(RepositoryType type) {

    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    CurrencyService service = new CurrencyServiceImpl(repositoryFactory);

    NetworkCurrencies networkCurrencies = get(service.getNetworkCurrencies());

    Assertions.assertNotNull(networkCurrencies.getCurrency());
    Assertions.assertEquals(
        networkCurrencies.getCurrency().getUnresolvedMosaicId(),
        networkCurrencies.getCurrency().getMosaicId().get());
    Assertions.assertNotNull(networkCurrencies.getCurrency().getMosaicId().get());

    Assertions.assertEquals(
        networkCurrencies.getCurrency(), (get(repositoryFactory.getNetworkCurrency())));

    Assertions.assertNotNull(networkCurrencies.getHarvest());
    Assertions.assertEquals(
        networkCurrencies.getHarvest().getUnresolvedMosaicId(),
        networkCurrencies.getHarvest().getMosaicId().get());
    Assertions.assertNotNull(networkCurrencies.getHarvest().getMosaicId().get());
    Assertions.assertEquals(
        networkCurrencies.getHarvest(), (get(repositoryFactory.getHarvestCurrency())));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void testNetworkCurrencyInvalidNamespaceId(RepositoryType type) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    CurrencyService service = new CurrencyServiceImpl(repositoryFactory);

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () -> {
              get(
                  service.getCurrencyFromNamespaceId(
                      NamespaceId.createFromName("invalid.currency")));
            });

    Assertions.assertEquals(
        "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'C1DF8A076D934A50'",
        exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void testNetworkCurrencyInvaliMosaicId(RepositoryType type) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    CurrencyService service = new CurrencyServiceImpl(repositoryFactory);

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () -> {
              get(service.getCurrency(new MosaicId(BigInteger.TEN)));
            });

    Assertions.assertEquals(
        "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '000000000000000A'",
        exception.getMessage());
  }
}
