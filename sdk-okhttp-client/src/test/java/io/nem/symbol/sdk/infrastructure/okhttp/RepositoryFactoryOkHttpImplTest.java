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

package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.catapult.builders.GeneratorUtils;
import io.nem.symbol.sdk.api.NetworkCurrencyService;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RepositoryFactoryConfiguration;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.reactivex.Observable;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link RepositoryFactoryOkHttpImpl}.
 */
public class RepositoryFactoryOkHttpImplTest {


    @Test
    public void shouldCreateRepositories() {

        String baseUrl = "https://nem.com:3000/path";

        RepositoryFactory factory = new RepositoryFactoryOkHttpImpl(
            baseUrl);
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
        Assertions.assertNotNull(factory.createMetadataRepository());
        Assertions.assertNotNull(factory.createRestrictionAccountRepository());
        Assertions.assertNotNull(factory.createRestrictionMosaicRepository());
        Assertions.assertNotNull(factory.createMultisigRepository());
        Assertions.assertNotNull(factory.createJsonSerialization());
        factory.close();
        factory.close();
        factory.close();
    }

    @Test
    public void getNetworkTypeFailWhenInvalidServer() {
        String baseUrl = "https://localhost:1934/path";

        RepositoryCallException e = Assertions.assertThrows(RepositoryCallException.class,
            () -> GeneratorUtils.propagate(
                () -> new RepositoryFactoryOkHttpImpl(baseUrl).getNetworkType().toFuture().get()));

        Assertions.assertTrue(
            e.getMessage().contains("ApiException: java.net.ConnectException: Failed to connect"));
    }

    @Test
    public void getUserProvidedConfiguration() throws Exception {
        String baseUrl = "https://localhost:1934/path";
        RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(baseUrl);
        configuration.withGenerationHash("abc");
        configuration.withNetworkType(NetworkType.MAIN_NET);
        configuration.withNetworkCurrency(NetworkCurrency.CAT_CURRENCY);
        configuration.withHarvestCurrency(NetworkCurrency.CAT_HARVEST);

        RepositoryFactory factory = new RepositoryFactoryOkHttpImpl(configuration);

        Assertions.assertEquals(configuration.getNetworkType(),
            factory.getNetworkType().toFuture().get());

        Assertions.assertEquals(configuration.getGenerationHash(),
            factory.getGenerationHash().toFuture().get());

        Assertions.assertEquals(configuration.getHarvestCurrency(),
            factory.getHarvestCurrency().toFuture().get());

        Assertions.assertEquals(configuration.getNetworkCurrency(),
            factory.getNetworkCurrency().toFuture().get());
    }

    @Test
    public void getRestProvidedNetworkCurrencies() throws Exception {
        String baseUrl = "https://localhost:1934/path";
        RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(baseUrl);
        configuration.withGenerationHash("abc");
        configuration.withNetworkType(NetworkType.MAIN_NET);

        RepositoryFactory factory = new RepositoryFactoryOkHttpImpl(configuration) {
            @Override
            protected NetworkCurrencyService createNetworkCurrencyService() {
                NetworkCurrencyService mock = Mockito.mock(NetworkCurrencyService.class);
                Mockito.when(mock.getNetworkCurrenciesFromNemesis()).thenReturn(Observable.just(
                    Arrays.asList(NetworkCurrency.CAT_CURRENCY, NetworkCurrency.CAT_HARVEST)));
                return mock;
            }
        };

        Assertions.assertEquals(configuration.getNetworkType(),
            factory.getNetworkType().toFuture().get());

        Assertions.assertEquals(configuration.getGenerationHash(),
            factory.getGenerationHash().toFuture().get());

        Assertions.assertEquals(NetworkCurrency.CAT_HARVEST,
            factory.getHarvestCurrency().toFuture().get());

        Assertions.assertEquals(NetworkCurrency.CAT_CURRENCY,
            factory.getNetworkCurrency().toFuture().get());
    }

    @Test
    public void getGenerationHashFailWhenInvalidServer() {
        String baseUrl = "https://localhost:1934/path";

        RepositoryCallException e = Assertions.assertThrows(RepositoryCallException.class,
            () -> GeneratorUtils.propagate(
                () -> new RepositoryFactoryOkHttpImpl(baseUrl).getGenerationHash().toFuture()
                    .get()));

        Assertions.assertTrue(
            e.getMessage().contains("ApiException: java.net.ConnectException: Failed to connect"));
    }

}
