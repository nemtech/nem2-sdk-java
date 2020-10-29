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

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.namespace.AddressAlias;
import io.nem.symbol.sdk.model.namespace.MosaicAlias;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Tests of {@link AliasServiceImpl} */
public class AliasServiceTest {

  private NetworkType networkType = NetworkType.MIJIN_TEST;
  private AliasServiceImpl service;

  private NamespaceRepository namespaceRepository;

  private Account account1 = Account.generateNewAccount(networkType);
  private Account account2 = Account.generateNewAccount(networkType);
  private NamespaceId accountAlias1 = NamespaceId.createFromName("accountAlias1".toLowerCase());
  private NamespaceId accountAlias2 = NamespaceId.createFromName("accountAlias2".toLowerCase());

  private MosaicId mosaicId1 =
      MosaicId.createFromNonce(MosaicNonce.createRandom(), account1.getPublicAccount());
  private MosaicId mosaicId2 =
      MosaicId.createFromNonce(MosaicNonce.createRandom(), account2.getPublicAccount());

  private NamespaceId mosaicAlias1 = NamespaceId.createFromName("mosaicAlias1".toLowerCase());
  private NamespaceId mosaicAlias2 = NamespaceId.createFromName("mosaicAlias2".toLowerCase());

  @BeforeEach
  void setup() {

    RepositoryFactory factory = mock(RepositoryFactory.class);

    namespaceRepository = mock(NamespaceRepository.class);
    when(factory.createNamespaceRepository()).thenReturn(namespaceRepository);

    when(factory.getNetworkType()).thenReturn(Observable.just(networkType));
    service = new AliasServiceImpl(factory);

    when(namespaceRepository.getNamespace(Mockito.any()))
        .thenReturn(Observable.error(new IllegalStateException("Alias does not exist")));

    when(namespaceRepository.getNamespace(accountAlias1))
        .thenReturn(Observable.just(createAlias(account1.getAddress())));

    when(namespaceRepository.getNamespace(accountAlias2))
        .thenReturn(Observable.just(createAlias(account2.getAddress())));

    when(namespaceRepository.getNamespace(mosaicAlias1))
        .thenReturn(Observable.just(createAlias(mosaicId1)));

    when(namespaceRepository.getNamespace(mosaicAlias2))
        .thenReturn(Observable.just(createAlias(mosaicId2)));
  }

  private NamespaceInfo createAlias(Address address) {

    return new NamespaceInfo(
        "abc",
        true,
        0,
        NamespaceRegistrationType.ROOT_NAMESPACE,
        1,
        Collections.emptyList(),
        null,
        Address.generateRandom(networkType),
        BigInteger.ONE,
        BigInteger.TEN,
        new AddressAlias(address));
  }

  private NamespaceInfo createAlias(MosaicId mosaicId) {

    return new NamespaceInfo(
        "abc",
        true,
        0,
        NamespaceRegistrationType.ROOT_NAMESPACE,
        1,
        Collections.emptyList(),
        null,
        Address.generateRandom(networkType),
        BigInteger.ONE,
        BigInteger.TEN,
        new MosaicAlias(mosaicId));
  }

  @Test
  void resolveAddress() throws ExecutionException, InterruptedException {

    Assertions.assertEquals(
        account1.getAddress(), service.resolveAddress(accountAlias1).toFuture().get());

    Assertions.assertEquals(
        account2.getAddress(), service.resolveAddress(accountAlias2).toFuture().get());

    Assertions.assertEquals(
        account1.getAddress(), service.resolveAddress(account1.getAddress()).toFuture().get());
  }

  @Test
  void resolveMosaicId() throws ExecutionException, InterruptedException {

    Assertions.assertEquals(mosaicId1, service.resolveMosaicId(mosaicAlias1).toFuture().get());

    Assertions.assertEquals(mosaicId2, service.resolveMosaicId(mosaicAlias2).toFuture().get());

    Assertions.assertEquals(mosaicId1, service.resolveMosaicId(mosaicId1).toFuture().get());
  }

  @Test
  void resolveAddressWhenDoesNotExist() {

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .resolveAddress(NamespaceId.createFromName("invalidaddressaslias"))
                            .toFuture()
                            .get()));

    Assertions.assertEquals(
        "Address could not be resolved from alias 98CC55CCA3F13503", exception.getMessage());
  }

  @Test
  void resolveMosaicIdWhenDoesNotExist() {

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .resolveMosaicId(NamespaceId.createFromName("invalidaddressaslias"))
                            .toFuture()
                            .get()));

    Assertions.assertEquals(
        "MosaicId could not be resolved from alias 98CC55CCA3F13503", exception.getMessage());
  }
}
