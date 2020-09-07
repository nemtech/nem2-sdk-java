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
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RepositoryFactoryConfiguration;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencyBuilder;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.network.ChainProperties;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
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
        Assertions.assertNotNull(factory.createTransactionStatusRepository());
        Assertions.assertNotNull(factory.createMetadataRepository());
        Assertions.assertNotNull(factory.createRestrictionAccountRepository());
        Assertions.assertNotNull(factory.createRestrictionMosaicRepository());
        Assertions.assertNotNull(factory.createHashLockRepository());
        Assertions.assertNotNull(factory.createSecretLockRepository());
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
            protected Observable<NetworkCurrency> loadNetworkCurrency() {
                return Observable.just(NetworkCurrency.CAT_CURRENCY);
            }

            @Override
            protected Observable<NetworkCurrency> loadHarvestCurrency() {
                return Observable.just(NetworkCurrency.CAT_HARVEST);
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
    public void getRestProvidedNetworkCurrenciesUsingNetworkProperties() throws Exception {
        String baseUrl = "https://localhost:1934/path";
        RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(baseUrl);
        configuration.withGenerationHash("abc");
        configuration.withNetworkType(NetworkType.MAIN_NET);
        NetworkRepository networkRepositoryMock = Mockito.mock(NetworkRepository.class);

        NetworkConfiguration networkConfiguration = Mockito.mock(NetworkConfiguration.class);
        ChainProperties chainProperties = Mockito.mock(ChainProperties.class);
        Mockito.when(chainProperties.getCurrencyMosaicId()).thenReturn("0x62EF'46FD'6555'AAAA");
        Mockito.when(chainProperties.getHarvestingMosaicId()).thenReturn("0x62EF'46FD'6555'BBBB");
        Mockito.when(networkConfiguration.getChain()).thenReturn(chainProperties);

        Mockito.when(networkRepositoryMock.getNetworkProperties())
            .thenReturn(Observable.just(networkConfiguration));

        MosaicRepository mosaicRepositoryMock = Mockito.mock(MosaicRepository.class);

        MosaicId networkMosaicId = new MosaicId("62EF46FD6555AAAA");
        MosaicInfo networkMosaic = new MosaicInfo("abc", networkMosaicId, BigInteger.valueOf(1),
            BigInteger.valueOf(2),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(), 4L,
            MosaicFlags.create(true, false, true), 5, BigInteger.valueOf(10));
        Mockito.when(mosaicRepositoryMock.getMosaic(networkMosaicId))
            .thenReturn(Observable.just(networkMosaic));

        MosaicId harvestMosaicId = new MosaicId("62EF46FD6555BBBB");
        MosaicInfo harvestMosaic = new MosaicInfo("abc", harvestMosaicId, BigInteger.valueOf(1),
            BigInteger.valueOf(2),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(), 10L,
            MosaicFlags.create(false, true, false), 4, BigInteger.valueOf(20));
        Mockito.when(mosaicRepositoryMock.getMosaic(harvestMosaicId))
            .thenReturn(Observable.just(harvestMosaic));

        NamespaceRepository namespaceRepository = Mockito.mock(NamespaceRepository.class);
        NamespaceName networkNamespaceName = new NamespaceName("network.xym");
        List<MosaicNames> networkNames = Collections
            .singletonList(new MosaicNames(networkMosaicId, Collections.singletonList(
                networkNamespaceName)));
        Mockito
            .when(namespaceRepository.getMosaicsNames(Collections.singletonList(networkMosaicId)))
            .thenReturn(Observable.just(networkNames));

        NamespaceName harvestNamespaceNetwork = new NamespaceName("harvest.xym");
        List<MosaicNames> harvestNames = Collections
            .singletonList(new MosaicNames(harvestMosaicId, Collections.singletonList(
                harvestNamespaceNetwork)));
        Mockito
            .when(namespaceRepository.getMosaicsNames(Collections.singletonList(harvestMosaicId)))
            .thenReturn(Observable.just(harvestNames));

        RepositoryFactory factory = new RepositoryFactoryOkHttpImpl(configuration) {

            @Override
            public NetworkRepository createNetworkRepository() {
                return networkRepositoryMock;
            }

            @Override
            public MosaicRepository createMosaicRepository() {
                return mosaicRepositoryMock;
            }

            @Override
            public NamespaceRepository createNamespaceRepository() {
                return namespaceRepository;
            }
        };

        Assertions.assertEquals(configuration.getNetworkType(),
            factory.getNetworkType().toFuture().get());

        Assertions.assertEquals(configuration.getGenerationHash(),
            factory.getGenerationHash().toFuture().get());

        Assertions.assertEquals(new NetworkCurrencyBuilder(networkMosaicId, 5)
                .withNamespaceId(networkNamespaceName.getNamespaceId()).withSupplyMutable(true)
                .withTransferable(false).build(),
            factory.getNetworkCurrency().toFuture().get());

        Assertions.assertEquals(new NetworkCurrencyBuilder(harvestMosaicId, 4)
                .withNamespaceId(harvestNamespaceNetwork.getNamespaceId()).withSupplyMutable(false)
                .withTransferable(true).build(),
            factory.getHarvestCurrency().toFuture().get());

    }

    @Test
    public void getRestProvidedNetworkCurrenciesUsingNetworkPropertiesSameCurrency()
        throws Exception {
        String baseUrl = "https://localhost:1934/path";
        RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(baseUrl);
        configuration.withGenerationHash("abc");
        configuration.withNetworkType(NetworkType.MAIN_NET);
        NetworkRepository networkRepositoryMock = Mockito.mock(NetworkRepository.class);

        NetworkConfiguration networkConfiguration = Mockito.mock(NetworkConfiguration.class);
        ChainProperties chainProperties = Mockito.mock(ChainProperties.class);
        Mockito.when(chainProperties.getCurrencyMosaicId()).thenReturn("0x62EF'46FD'6555'AAAA");
        Mockito.when(chainProperties.getHarvestingMosaicId()).thenReturn("0x62EF'46FD'6555'AAAA");
        Mockito.when(networkConfiguration.getChain()).thenReturn(chainProperties);

        Mockito.when(networkRepositoryMock.getNetworkProperties())
            .thenReturn(Observable.just(networkConfiguration));

        MosaicRepository mosaicRepositoryMock = Mockito.mock(MosaicRepository.class);

        MosaicId networkMosaicId = new MosaicId("62EF46FD6555AAAA");
        MosaicInfo networkMosaic = new MosaicInfo("abc", networkMosaicId, BigInteger.valueOf(1),
            BigInteger.valueOf(2),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(), 4L,
            MosaicFlags.create(true, false, true), 5, BigInteger.valueOf(10));
        Mockito.when(mosaicRepositoryMock.getMosaic(networkMosaicId))
            .thenReturn(Observable.just(networkMosaic));

        NamespaceRepository namespaceRepository = Mockito.mock(NamespaceRepository.class);
        NamespaceName networkNamespaceName = new NamespaceName("network.xym");
        List<MosaicNames> networkNames = Collections
            .singletonList(new MosaicNames(networkMosaicId, Collections.singletonList(
                networkNamespaceName)));
        Mockito
            .when(namespaceRepository.getMosaicsNames(Collections.singletonList(networkMosaicId)))
            .thenReturn(Observable.just(networkNames));

        RepositoryFactory factory = new RepositoryFactoryOkHttpImpl(configuration) {

            @Override
            public NetworkRepository createNetworkRepository() {
                return networkRepositoryMock;
            }

            @Override
            public MosaicRepository createMosaicRepository() {
                return mosaicRepositoryMock;
            }

            @Override
            public NamespaceRepository createNamespaceRepository() {
                return namespaceRepository;
            }
        };

        Assertions.assertEquals(configuration.getNetworkType(),
            factory.getNetworkType().toFuture().get());

        Assertions.assertEquals(configuration.getGenerationHash(),
            factory.getGenerationHash().toFuture().get());

        Assertions.assertEquals(new NetworkCurrencyBuilder(networkMosaicId, 5)
                .withNamespaceId(networkNamespaceName.getNamespaceId()).withSupplyMutable(true)
                .withTransferable(false).build(),
            factory.getNetworkCurrency().toFuture().get());

        Assertions.assertEquals(new NetworkCurrencyBuilder(networkMosaicId, 5)
                .withNamespaceId(networkNamespaceName.getNamespaceId()).withSupplyMutable(true)
                .withTransferable(false).build(),
            factory.getHarvestCurrency().toFuture().get());

        Assertions.assertEquals(factory.getNetworkCurrency().toFuture().get(),
            factory.getHarvestCurrency().toFuture().get());

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
