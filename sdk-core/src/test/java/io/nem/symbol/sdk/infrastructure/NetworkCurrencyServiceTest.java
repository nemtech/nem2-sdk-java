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

import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NetworkCurrencyService;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Tests of {@link NetworkCurrencyServiceImpl} */
class NetworkCurrencyServiceTest {

  private NamespaceRepository namespaceRepository;
  private MosaicRepository mosaicRepository;
  private NetworkCurrencyService service;

  @BeforeEach
  void setup() {

    RepositoryFactory factory = mock(RepositoryFactory.class);

    namespaceRepository = mock(NamespaceRepository.class);
    when(factory.createNamespaceRepository()).thenReturn(namespaceRepository);

    mosaicRepository = mock(MosaicRepository.class);
    when(factory.createMosaicRepository()).thenReturn(mosaicRepository);

    service = new NetworkCurrencyServiceImpl(factory);
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenNoNamespaceError() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
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

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.error(new RepositoryCallException("Not found", 404, null)));

    NetworkCurrency networkCurrency =
        service.getNetworkCurrencyFromMosaicId(mosaicId).toFuture().get();

    Assertions.assertEquals(10, networkCurrency.getDivisibility());
    Assertions.assertEquals(mosaicId, networkCurrency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, networkCurrency.getMosaicId().get());
    Assertions.assertFalse(networkCurrency.getNamespaceId().isPresent());
    Assertions.assertTrue(networkCurrency.isTransferable());
    Assertions.assertTrue(networkCurrency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenNoNamespace() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
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

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Collections.emptyList()));

    NetworkCurrency networkCurrency =
        service.getNetworkCurrencyFromMosaicId(mosaicId).toFuture().get();

    Assertions.assertEquals(10, networkCurrency.getDivisibility());
    Assertions.assertEquals(mosaicId, networkCurrency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, networkCurrency.getMosaicId().get());
    Assertions.assertFalse(networkCurrency.getNamespaceId().isPresent());
    Assertions.assertTrue(networkCurrency.isTransferable());
    Assertions.assertTrue(networkCurrency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenEmptyNames() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
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

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(
            Observable.just(
                Collections.singletonList(new MosaicNames(mosaicId, Collections.emptyList()))));

    NetworkCurrency networkCurrency =
        service.getNetworkCurrencyFromMosaicId(mosaicId).toFuture().get();

    Assertions.assertEquals(10, networkCurrency.getDivisibility());
    Assertions.assertEquals(mosaicId, networkCurrency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, networkCurrency.getMosaicId().get());
    Assertions.assertFalse(networkCurrency.getNamespaceId().isPresent());
    Assertions.assertTrue(networkCurrency.isTransferable());
    Assertions.assertTrue(networkCurrency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromMosaicIdWhenNamespaceIsPresent() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
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

    MosaicNames mosaicNames2 =
        new MosaicNames(
            mosaicId,
            Arrays.asList(new NamespaceName("some.alias2"), new NamespaceName("some.alias3")));

    Mockito.when(
            namespaceRepository.getMosaicsNames(Mockito.eq(Collections.singletonList(mosaicId))))
        .thenReturn(Observable.just(Arrays.asList(mosaicNames, mosaicNames2)));

    NetworkCurrency networkCurrency =
        service.getNetworkCurrencyFromMosaicId(mosaicId).toFuture().get();

    Assertions.assertEquals(10, networkCurrency.getDivisibility());
    Assertions.assertEquals(mosaicId, networkCurrency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, networkCurrency.getMosaicId().get());
    Assertions.assertEquals(namespaceId, networkCurrency.getNamespaceId().get());
    Assertions.assertTrue(networkCurrency.isTransferable());
    Assertions.assertTrue(networkCurrency.isSupplyMutable());
  }

  @Test
  void getNetworkCurrencyFromNamespaceId() throws Exception {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Account account = Account.generateNewAccount(NetworkType.MAIN_NET);
    BigInteger supply = BigInteger.valueOf(12);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
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

    Mockito.when(namespaceRepository.getLinkedMosaicId(Mockito.eq(namespaceId)))
        .thenReturn(Observable.just(mosaicId));

    Mockito.when(mosaicRepository.getMosaic(Mockito.eq(mosaicId)))
        .thenReturn(Observable.just(mosaicInfo));

    NetworkCurrency networkCurrency =
        service.getNetworkCurrencyFromNamespaceId(namespaceId).toFuture().get();

    Assertions.assertEquals(10, networkCurrency.getDivisibility());
    Assertions.assertEquals(mosaicId, networkCurrency.getUnresolvedMosaicId());
    Assertions.assertEquals(mosaicId, networkCurrency.getMosaicId().get());
    Assertions.assertEquals(namespaceId, networkCurrency.getNamespaceId().get());
    Assertions.assertTrue(networkCurrency.isTransferable());
    Assertions.assertTrue(networkCurrency.isSupplyMutable());
  }
}
