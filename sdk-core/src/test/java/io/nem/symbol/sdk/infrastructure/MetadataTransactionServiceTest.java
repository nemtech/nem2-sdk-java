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
import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.namespace.MosaicAlias;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MetadataTransactionServiceTest {

  private final NetworkType networkType = NetworkType.MIJIN_TEST;
  private MetadataTransactionServiceImpl service;
  private MetadataRepository metadataRepositoryMock;
  private PublicAccount targetAccount;
  private Address sourceAddress;
  private MosaicId mosaicId;
  private NamespaceId namespaceId;
  private final NamespaceId mosaicAlias = NamespaceId.createFromName("mosaicAlias1".toLowerCase());

  @BeforeEach
  void setup() {
    targetAccount = Account.generateNewAccount(networkType).getPublicAccount();
    sourceAddress = Account.generateNewAccount(networkType).getAddress();
    mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), targetAccount);
    namespaceId = NamespaceId.createFromId(BigInteger.TEN);
    RepositoryFactory factory = Mockito.mock(RepositoryFactory.class);
    metadataRepositoryMock = Mockito.mock(MetadataRepository.class);

    NamespaceRepository namespaceRepository = mock(NamespaceRepository.class);
    Mockito.when(factory.createMetadataRepository()).thenReturn(metadataRepositoryMock);
    Mockito.when(factory.getNetworkType()).thenReturn(Observable.just(networkType));
    Mockito.when(factory.getEpochAdjustment())
        .thenReturn(Observable.just(Duration.ofSeconds(100L)));
    when(factory.createNamespaceRepository()).thenReturn(namespaceRepository);
    service = new MetadataTransactionServiceImpl(factory);

    when(namespaceRepository.getNamespace(mosaicAlias))
        .thenReturn(Observable.just(createAlias(mosaicId)));
  }

  private NamespaceInfo createAlias(MosaicId mosaicId) {

    return new NamespaceInfo(
        "abc",
        1,
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

  @AfterEach
  void turnDown() {
    Mockito.verifyNoMoreInteractions(metadataRepositoryMock);
  }

  @Test
  void shouldCreateAccountMetadataTransactionFactory() throws Exception {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String oldValue = "The original Message";
    String newValue = "the new Message";

    Metadata metadata =
        new Metadata(
            "someId",
            1,
            "compositeHash",
            sourceAddress,
            targetAccount.getAddress(),
            metadataKey,
            MetadataType.ACCOUNT,
            oldValue,
            null);

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetAddress(targetAccount.getAddress())
            .metadataType(MetadataType.ACCOUNT);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.just(new Page<>(Collections.singletonList(metadata))));

    AccountMetadataTransactionFactory result =
        service
            .createAccountMetadataTransactionFactory(
                targetAccount.getAddress(), metadataKey, newValue, sourceAddress)
            .toFuture()
            .get();

    Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
    Assertions.assertNotEquals(oldValue, result.getValue());
    Assertions.assertNotEquals(newValue, result.getValue());
    Assertions.assertEquals(
        StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
        result.getValueSizeDelta());
    Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());

    Mockito.verify(metadataRepositoryMock).search(Mockito.eq(criteria));
  }

  @Test
  void shouldCreateAccountMetadataTransactionFactoryWhenNotFound() throws Exception {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetAddress(targetAccount.getAddress())
            .metadataType(MetadataType.ACCOUNT);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.just(new Page<>(Collections.emptyList())));

    AccountMetadataTransactionFactory result =
        service
            .createAccountMetadataTransactionFactory(
                targetAccount.getAddress(), metadataKey, newValue, sourceAddress)
            .toFuture()
            .get();

    Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
    Assertions.assertEquals(newValue, result.getValue());
    Assertions.assertEquals(StringEncoder.getBytes(newValue).length, result.getValueSizeDelta());
    Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());

    Mockito.verify(metadataRepositoryMock).search(Mockito.eq(criteria));
  }

  @Test
  void shouldNotCreateAccountMetadataTransactionFactoryWhenAnyOtherRemoteException() {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    RepositoryCallException expectedException =
        new RepositoryCallException("Some other problem.", 500, null);

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetAddress(targetAccount.getAddress())
            .metadataType(MetadataType.ACCOUNT);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.error(expectedException));

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .createAccountMetadataTransactionFactory(
                                targetAccount.getAddress(), metadataKey, newValue, sourceAddress)
                            .toFuture()
                            .get()));

    Assertions.assertEquals(expectedException, exception);

    Mockito.verify(metadataRepositoryMock).search(Mockito.eq(criteria));
  }

  @Test
  public void shouldNotCreateAccountMetadataTransactionFactoryWhenBug() {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    IllegalArgumentException expectedException =
        new IllegalArgumentException("Some unexpected error");

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetAddress(targetAccount.getAddress())
            .metadataType(MetadataType.ACCOUNT);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.error(expectedException));

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .createAccountMetadataTransactionFactory(
                                targetAccount.getAddress(), metadataKey, newValue, sourceAddress)
                            .toFuture()
                            .get()));

    Assertions.assertEquals(expectedException, exception);

    Mockito.verify(metadataRepositoryMock).search(Mockito.eq(criteria));
  }

  @Test
  void shouldCreateMosaicMetadataTransactionFactory() throws Exception {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String oldValue = "The original Message";
    String newValue = "the new Message";

    Metadata metadata =
        new Metadata(
            "someId",
            1,
            "compositeHash",
            sourceAddress,
            targetAccount.getAddress(),
            metadataKey,
            MetadataType.MOSAIC,
            oldValue,
            mosaicId.getIdAsHex());

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(mosaicId)
            .metadataType(MetadataType.MOSAIC);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.just(new Page<>(Collections.singletonList(metadata))));

    MosaicMetadataTransactionFactory result =
        service
            .createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(), metadataKey, newValue, sourceAddress, mosaicId)
            .toFuture()
            .get();

    Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
    Assertions.assertNotEquals(oldValue, result.getValue());
    Assertions.assertNotEquals(newValue, result.getValue());
    Assertions.assertEquals(
        StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
        result.getValueSizeDelta());
    Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
    Assertions.assertEquals(mosaicId.getId(), result.getTargetMosaicId().getId());

    Mockito.verify(metadataRepositoryMock).search(Mockito.eq(criteria));
  }

  @Test
  void shouldCreateMosaicMetadataTransactionFactoryUsingAlias() throws Exception {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String oldValue = "The original Message";
    String newValue = "the new Message";

    Metadata metadata =
        new Metadata(
            "someId",
            1,
            "compositeHash",
            sourceAddress,
            targetAccount.getAddress(),
            metadataKey,
            MetadataType.MOSAIC,
            oldValue,
            mosaicId.getIdAsHex());

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(mosaicId)
            .metadataType(MetadataType.MOSAIC);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.just(new Page<>(Collections.singletonList(metadata))));

    MosaicMetadataTransactionFactory result =
        service
            .createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(), metadataKey, newValue, sourceAddress, mosaicAlias)
            .toFuture()
            .get();

    Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
    Assertions.assertNotEquals(oldValue, result.getValue());
    Assertions.assertNotEquals(newValue, result.getValue());
    Assertions.assertEquals(
        StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
        result.getValueSizeDelta());
    Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
    Assertions.assertEquals(mosaicAlias, result.getTargetMosaicId());

    Mockito.verify(metadataRepositoryMock).search(Mockito.eq(criteria));
  }

  @Test
  void shouldCreateMosaicMetadataTransactionFactoryWhenNotFound() throws Exception {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(mosaicId)
            .metadataType(MetadataType.MOSAIC);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.just(new Page<>(Collections.emptyList())));

    MosaicMetadataTransactionFactory result =
        service
            .createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(), metadataKey, newValue, sourceAddress, mosaicId)
            .toFuture()
            .get();

    Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
    Assertions.assertEquals(newValue, result.getValue());
    Assertions.assertEquals(StringEncoder.getBytes(newValue).length, result.getValueSizeDelta());
    Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
    Assertions.assertEquals(mosaicId.getId(), result.getTargetMosaicId().getId());

    Mockito.verify(metadataRepositoryMock).search(Mockito.eq(criteria));
  }

  @Test
  void shouldNotCreateMosaicMetadataTransactionFactoryWhenAnyOtherRemoteException() {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    RepositoryCallException expectedException =
        new RepositoryCallException("Some other problem.", 500, null);

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(mosaicId)
            .metadataType(MetadataType.MOSAIC);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.error(expectedException));

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .createMosaicMetadataTransactionFactory(
                                targetAccount.getAddress(),
                                metadataKey,
                                newValue,
                                sourceAddress,
                                mosaicId)
                            .toFuture()
                            .get()));

    Assertions.assertEquals(expectedException, exception);

    Mockito.verify(metadataRepositoryMock).search(criteria);
  }

  @Test
  public void shouldNotCreateMosaicMetadataTransactionFactoryWhenBug() {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(mosaicId)
            .metadataType(MetadataType.MOSAIC);

    IllegalArgumentException expectedException =
        new IllegalArgumentException("Some unexpected error");
    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.error(expectedException));

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .createMosaicMetadataTransactionFactory(
                                targetAccount.getAddress(),
                                metadataKey,
                                newValue,
                                sourceAddress,
                                mosaicId)
                            .toFuture()
                            .get()));

    Assertions.assertEquals(expectedException, exception);

    Mockito.verify(metadataRepositoryMock).search(criteria);
  }

  @Test
  void shouldCreateNamespaceMetadataTransactionFactory() throws Exception {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String oldValue = "The original Message";
    String newValue = "the new Message";

    Metadata metadata =
        new Metadata(
            "someId",
            1,
            "compositeHash",
            sourceAddress,
            targetAccount.getAddress(),
            metadataKey,
            MetadataType.NAMESPACE,
            oldValue,
            namespaceId.getIdAsHex());

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(namespaceId)
            .metadataType(MetadataType.NAMESPACE);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.just(new Page<>(Collections.singletonList(metadata))));

    NamespaceMetadataTransactionFactory result =
        service
            .createNamespaceMetadataTransactionFactory(
                targetAccount.getAddress(), metadataKey, newValue, sourceAddress, namespaceId)
            .toFuture()
            .get();

    Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
    Assertions.assertNotEquals(oldValue, result.getValue());
    Assertions.assertNotEquals(newValue, result.getValue());
    Assertions.assertEquals(
        StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
        result.getValueSizeDelta());
    Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
    Assertions.assertEquals(namespaceId.getId(), result.getTargetNamespaceId().getId());

    Mockito.verify(metadataRepositoryMock).search(criteria);
  }

  @Test
  void shouldCreateNamespaceMetadataTransactionFactoryWhenNotFound() throws Exception {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(namespaceId)
            .metadataType(MetadataType.NAMESPACE);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.just(new Page<>(Collections.emptyList())));

    NamespaceMetadataTransactionFactory result =
        service
            .createNamespaceMetadataTransactionFactory(
                targetAccount.getAddress(), metadataKey, newValue, sourceAddress, namespaceId)
            .toFuture()
            .get();

    Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
    Assertions.assertEquals(newValue, result.getValue());
    Assertions.assertEquals(StringEncoder.getBytes(newValue).length, result.getValueSizeDelta());
    Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
    Assertions.assertEquals(namespaceId.getId(), result.getTargetNamespaceId().getId());

    Mockito.verify(metadataRepositoryMock).search(criteria);
  }

  @Test
  void shouldNotCreateNamespaceMetadataTransactionFactoryWhenAnyOtherRemoteException() {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    RepositoryCallException expectedException =
        new RepositoryCallException("Some other problem.", 500, null);

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(namespaceId)
            .metadataType(MetadataType.NAMESPACE);

    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.error(expectedException));

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .createNamespaceMetadataTransactionFactory(
                                targetAccount.getAddress(),
                                metadataKey,
                                newValue,
                                sourceAddress,
                                namespaceId)
                            .toFuture()
                            .get()));

    Assertions.assertEquals(expectedException, exception);

    Mockito.verify(metadataRepositoryMock).search(criteria);
  }

  @Test
  public void shouldNotCreateNamespaceMetadataTransactionFactoryWhenBug() {

    BigInteger metadataKey = BigInteger.valueOf(10);
    String newValue = "the new Message";

    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .sourceAddress(sourceAddress)
            .scopedMetadataKey(metadataKey)
            .targetId(namespaceId)
            .metadataType(MetadataType.NAMESPACE);

    IllegalArgumentException expectedException =
        new IllegalArgumentException("Some unexpected error");
    Mockito.when(metadataRepositoryMock.search(Mockito.eq(criteria)))
        .thenReturn(Observable.error(expectedException));

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () ->
                ExceptionUtils.propagate(
                    () ->
                        service
                            .createNamespaceMetadataTransactionFactory(
                                targetAccount.getAddress(),
                                metadataKey,
                                newValue,
                                sourceAddress,
                                namespaceId)
                            .toFuture()
                            .get()));

    Assertions.assertEquals(expectedException, exception);

    Mockito.verify(metadataRepositoryMock).search(criteria);
  }
}
