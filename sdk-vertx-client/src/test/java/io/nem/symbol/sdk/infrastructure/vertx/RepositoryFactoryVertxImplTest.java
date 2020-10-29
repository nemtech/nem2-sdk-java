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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.catapult.builders.GeneratorUtils;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RepositoryFactoryConfiguration;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.reactivex.Observable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests for {@link RepositoryFactoryVertxImpl}. */
public class RepositoryFactoryVertxImplTest {

  @Test
  public void shouldCreateRepositories() {

    String baseUrl = "https://nem.com:3000/path";

    RepositoryFactory factory = new RepositoryFactoryVertxImpl(baseUrl);

    Assertions.assertNotNull(factory.createAccountRepository());
    Assertions.assertNotNull(factory.createBlockRepository());
    Assertions.assertNotNull(factory.createReceiptRepository());
    Assertions.assertNotNull(factory.createChainRepository());
    Assertions.assertNotNull(factory.createListener());
    Assertions.assertNotNull(factory.createMosaicRepository());
    Assertions.assertNotNull(factory.createNamespaceRepository());
    Assertions.assertNotNull(factory.createNetworkRepository());
    Assertions.assertNotNull(factory.createNodeRepository());
    Assertions.assertNotNull(factory.createTransactionRepository());
    Assertions.assertNotNull(factory.createTransactionStatusRepository());
    Assertions.assertNotNull(factory.createMetadataRepository());
    Assertions.assertNotNull(factory.createRestrictionAccountRepository());
    Assertions.assertNotNull(factory.createRestrictionMosaicRepository());
    Assertions.assertNotNull(factory.createHashLockRepository());
    Assertions.assertNotNull(factory.createSecretLockRepository());
    Assertions.assertNotNull(factory.createMultisigRepository());
    Assertions.assertNotNull(factory.createFinalizationRepository());
    Assertions.assertNotNull(factory.createJsonSerialization());

    factory.close();
    factory.close();
    factory.close();
  }

  @Test
  public void getNetworkTypeFailWhenInvalidServer() {
    String baseUrl = "https://localhost:1934/path";

    RepositoryCallException e =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                GeneratorUtils.propagate(
                    () ->
                        new RepositoryFactoryVertxImpl(baseUrl).getNetworkType().toFuture().get()));

    Assertions.assertTrue(e.getMessage().contains("ApiException: Connection refused"));
  }

  @Test
  public void getUserProvidedConfiguration() throws Exception {
    String baseUrl = "https://localhost:1934/path";
    RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(baseUrl);
    configuration.withGenerationHash("abc");
    configuration.withNetworkType(NetworkType.MAIN_NET);
    configuration.withNetworkCurrencies(NetworkCurrencies.PUBLIC);

    RepositoryFactory factory = new RepositoryFactoryVertxImpl(configuration);

    Assertions.assertEquals(
        configuration.getNetworkType(), factory.getNetworkType().toFuture().get());

    Assertions.assertEquals(
        configuration.getGenerationHash(), factory.getGenerationHash().toFuture().get());

    Assertions.assertEquals(
        configuration.getNetworkCurrencies().getHarvest(),
        factory.getHarvestCurrency().toFuture().get());

    Assertions.assertEquals(
        configuration.getNetworkCurrencies().getCurrency(),
        factory.getNetworkCurrency().toFuture().get());
  }

  @Test
  public void getRestProvidedNetworkCurrencies() throws Exception {
    String baseUrl = "https://localhost:1934/path";
    RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(baseUrl);
    configuration.withGenerationHash("abc");
    configuration.withNetworkType(NetworkType.MAIN_NET);

    RepositoryFactory factory =
        new RepositoryFactoryVertxImpl(configuration) {

          @Override
          protected Observable<NetworkCurrencies> loadNetworkCurrencies() {
            return Observable.just(
                new NetworkCurrencies(Currency.CAT_CURRENCY, Currency.CAT_HARVEST));
          }
        };

    Assertions.assertEquals(
        configuration.getNetworkType(), factory.getNetworkType().toFuture().get());

    Assertions.assertEquals(
        configuration.getGenerationHash(), factory.getGenerationHash().toFuture().get());

    Assertions.assertEquals(Currency.CAT_HARVEST, factory.getHarvestCurrency().toFuture().get());

    Assertions.assertEquals(Currency.CAT_CURRENCY, factory.getNetworkCurrency().toFuture().get());
  }

  @Test
  public void getGenerationHashFailWhenInvalidServer() {
    String baseUrl = "https://localhost:1934/path";

    RepositoryCallException e =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                GeneratorUtils.propagate(
                    () ->
                        new RepositoryFactoryVertxImpl(baseUrl).getNetworkType().toFuture().get()));

    Assertions.assertTrue(e.getMessage().contains("ApiException: Connection refused"));
  }
}
