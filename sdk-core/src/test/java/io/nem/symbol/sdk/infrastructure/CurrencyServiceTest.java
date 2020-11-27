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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.nem.symbol.core.utils.FormatUtils;
import io.nem.symbol.sdk.api.CurrencyService;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.network.ChainProperties;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Tests of {@link CurrencyServiceImpl} */
class CurrencyServiceTest {

  private NamespaceRepository namespaceRepository;
  private MosaicRepository mosaicRepository;
  private NetworkRepository networkRepository;
  private CurrencyService service;

  @BeforeEach
  void setup() {

    RepositoryFactory factory = mock(RepositoryFactory.class);

    namespaceRepository = mock(NamespaceRepository.class);
    when(factory.createNamespaceRepository()).thenReturn(namespaceRepository);

    mosaicRepository = mock(MosaicRepository.class);
    when(factory.createMosaicRepository()).thenReturn(mosaicRepository);

    networkRepository = mock(NetworkRepository.class);
    when(factory.createNetworkRepository()).thenReturn(networkRepository);

    service = new CurrencyServiceImpl(factory);
  }

  @Test
  void getNetworkCurrencies() throws Exception {

    NetworkConfiguration networkConfiguration = Mockito.mock(NetworkConfiguration.class);
    ChainProperties chainProperties = Mockito.mock(ChainProperties.class);
    String currencyMosaicIdHex = "0x62EF'46FD'6555'AAAA";
    MosaicId currencyMosaicId = new MosaicId(FormatUtils.toSimpleHex(currencyMosaicIdHex));
    Mockito.when(chainProperties.getCurrencyMosaicId()).thenReturn(currencyMosaicIdHex);
    String harvestMosaicIdHex = "0x62EF'46FD'6555'BBBB";
    MosaicId harvestMosaicId = new MosaicId(FormatUtils.toSimpleHex(harvestMosaicIdHex));
    Mockito.when(chainProperties.getHarvestingMosaicId()).thenReturn(harvestMosaicIdHex);
    Mockito.when(networkConfiguration.getChain()).thenReturn(chainProperties);

    Mockito.when(networkRepository.getNetworkProperties())
        .thenReturn(Observable.just(networkConfiguration));

    Address account = Address.generateRandom(NetworkType.MIJIN_TEST);
    MosaicInfo currencyMosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            currencyMosaicId,
            BigInteger.valueOf(100),
            BigInteger.ONE,
            account,
            4L,
            MosaicFlags.create(false, true, false),
            10,
            BigInteger.TEN);

    MosaicInfo harvestMosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            harvestMosaicId,
            BigInteger.valueOf(200),
            BigInteger.ONE,
            account,
            4L,
            MosaicFlags.create(true, false, true),
            3,
            BigInteger.TEN);

    Mockito.when(
            mosaicRepository.getMosaics(
                Mockito.eq(Arrays.asList(currencyMosaicId, harvestMosaicId))))
        .thenReturn(Observable.just(Arrays.asList(currencyMosaicInfo, harvestMosaicInfo)));

    Mockito.when(
            namespaceRepository.getMosaicsNames(
                Mockito.eq(Arrays.asList(currencyMosaicId, harvestMosaicId))))
        .thenReturn(Observable.just(Collections.emptyList()));

    NetworkCurrencies networkCurrencies = service.getNetworkCurrencies().toFuture().get();

    Assertions.assertEquals(currencyMosaicInfo.toCurrency(), networkCurrencies.getCurrency());
    Assertions.assertEquals(harvestMosaicInfo.toCurrency(), networkCurrencies.getHarvest());
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenNoNamespaceError() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            mosaicId,
            supply,
            BigInteger.ONE,
            account.getAddress(),
            4L,
            MosaicFlags.create(true, true, true),
            10,
            BigInteger.TEN);

    Mockito.when(mosaicRepository.getMosaics(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Collections.singletonList(mosaicInfo)));

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.error(new RepositoryCallException("Not found", 404, null)));

    Currency currency = service.getCurrency(mosaicId).toFuture().get();

    Assertions.assertEquals(10, currency.getDivisibility());
    Assertions.assertEquals(mosaicId, currency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, currency.getMosaicId().get());
    Assertions.assertFalse(currency.getNamespaceId().isPresent());
    Assertions.assertTrue(currency.isTransferable());
    Assertions.assertTrue(currency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenNoNamespace() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            mosaicId,
            supply,
            BigInteger.ONE,
            account.getAddress(),
            4L,
            MosaicFlags.create(true, true, true),
            10,
            BigInteger.TEN);

    Mockito.when(mosaicRepository.getMosaics(Mockito.eq(Arrays.asList(mosaicId))))
        .thenReturn(Observable.just(Arrays.asList(mosaicInfo)));

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Collections.emptyList()));

    Currency currency = service.getCurrency(mosaicId).toFuture().get();

    Assertions.assertEquals(10, currency.getDivisibility());
    Assertions.assertEquals(mosaicId, currency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, currency.getMosaicId().get());
    Assertions.assertFalse(currency.getNamespaceId().isPresent());
    Assertions.assertTrue(currency.isTransferable());
    Assertions.assertTrue(currency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenEmptyNames() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            mosaicId,
            supply,
            BigInteger.ONE,
            account.getAddress(),
            4L,
            MosaicFlags.create(true, true, true),
            10,
            BigInteger.TEN);

    Mockito.when(mosaicRepository.getMosaics(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Collections.singletonList(mosaicInfo)));

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(
            Observable.just(
                Collections.singletonList(new MosaicNames(mosaicId, Collections.emptyList()))));

    Currency currency = service.getCurrency(mosaicId).toFuture().get();

    Assertions.assertEquals(10, currency.getDivisibility());
    Assertions.assertEquals(mosaicId, currency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, currency.getMosaicId().get());
    Assertions.assertFalse(currency.getNamespaceId().isPresent());
    Assertions.assertTrue(currency.isTransferable());
    Assertions.assertTrue(currency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenNamespaceIsPresent() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            mosaicId,
            supply,
            BigInteger.ONE,
            account.getAddress(),
            4L,
            MosaicFlags.create(true, true, true),
            10,
            BigInteger.TEN);

    Mockito.when(mosaicRepository.getMosaics(Mockito.eq(Arrays.asList(mosaicId))))
        .thenReturn(Observable.just(Arrays.asList(mosaicInfo)));

    String name = "some.alias";
    NamespaceId namespaceId = NamespaceId.createFromName(name);
    MosaicNames mosaicNames =
        new MosaicNames(
            mosaicId, Arrays.asList(new NamespaceName(name), new NamespaceName("some.alias2")));

    MosaicNames mosaicNames2 =
        new MosaicNames(
            mosaicId,
            Arrays.asList(new NamespaceName("some.alias2"), new NamespaceName("some.alias3")));

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Arrays.asList(mosaicNames, mosaicNames2)));

    Currency currency = service.getCurrency(mosaicId).toFuture().get();

    Assertions.assertEquals(10, currency.getDivisibility());
    Assertions.assertEquals(mosaicId, currency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, currency.getMosaicId().get());
    Assertions.assertEquals(namespaceId, currency.getNamespaceId().get());
    Assertions.assertEquals("some.alias", currency.getNamespaceId().get().getFullName().get());
    Assertions.assertTrue(currency.isTransferable());
    Assertions.assertTrue(currency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromNamespaceId() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            mosaicId,
            supply,
            BigInteger.ONE,
            account.getAddress(),
            4L,
            MosaicFlags.create(true, true, true),
            10,
            BigInteger.TEN);

    Mockito.when(mosaicRepository.getMosaic(Mockito.eq(mosaicId)))
        .thenReturn(Observable.just(mosaicInfo));

    String name = "some.alias";
    NamespaceId namespaceId = NamespaceId.createFromName(name);

    MosaicNames mosaicNames =
        new MosaicNames(
            mosaicId, Arrays.asList(new NamespaceName(name), new NamespaceName("some.alias2")));

    Mockito.when(namespaceRepository.getLinkedMosaicId(Mockito.eq(namespaceId)))
        .thenReturn(Observable.just(mosaicId));

    Mockito.when(mosaicRepository.getMosaics(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Collections.singletonList(mosaicInfo)));

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Collections.singletonList(mosaicNames)));

    Currency currency = service.getCurrencyFromNamespaceId(namespaceId).toFuture().get();

    Assertions.assertEquals(10, currency.getDivisibility());
    Assertions.assertEquals(mosaicId, currency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, currency.getMosaicId().get());
    Assertions.assertEquals(namespaceId, currency.getNamespaceId().get());
    Assertions.assertEquals("some.alias", currency.getNamespaceId().get().getFullName().get());
    Assertions.assertTrue(currency.isTransferable());
    Assertions.assertTrue(currency.isSupplyMutable());
  }
}
