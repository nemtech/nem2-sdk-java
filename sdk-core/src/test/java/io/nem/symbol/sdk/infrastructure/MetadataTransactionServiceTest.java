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
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataEntry;
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
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MetadataTransactionServiceTest {

    private NetworkType networkType = NetworkType.MIJIN_TEST;
    private MetadataTransactionServiceImpl service;
    private MetadataRepository metadataRepositoryMock;
    private PublicAccount targetAccount;
    private Address sourceAddress;
    private MosaicId mosaicId;
    private NamespaceId namespaceId;
    private NamespaceId mosaicAlias = NamespaceId.createFromName("mosaicAlias1".toLowerCase());

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
        when(factory.createNamespaceRepository()).thenReturn(namespaceRepository);
        service = new MetadataTransactionServiceImpl(factory);

        when(namespaceRepository.getNamespace(mosaicAlias))
            .thenReturn(Observable.just(createAlias(mosaicId)));
    }

    private NamespaceInfo createAlias(MosaicId mosaicId) {

        return new NamespaceInfo(true, 0, "metadaId", NamespaceRegistrationType.ROOT_NAMESPACE, 1,
            Collections.emptyList(), null, null, BigInteger.ONE, BigInteger.TEN,
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

        Metadata metadata = new Metadata("someId",
            new MetadataEntry("compositeHash", sourceAddress,
                targetAccount.getAddress(),
                metadataKey, MetadataType.ACCOUNT,
                oldValue, Optional.of(targetAccount.getAddress().encoded())));

        Mockito.when(
            metadataRepositoryMock
                .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.just(metadata));

        AccountMetadataTransactionFactory result =
            service.createAccountMetadataTransactionFactory(
                targetAccount.getAddress(),
                metadataKey,
                newValue, sourceAddress).toFuture().get();

        Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
        Assertions.assertNotEquals(oldValue, result.getValue());
        Assertions.assertNotEquals(newValue, result.getValue());
        Assertions.assertEquals(
            StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
            result.getValueSizeDelta());
        Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());

        Mockito.verify(metadataRepositoryMock)
            .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldCreateAccountMetadataTransactionFactoryWhenNotFound() throws Exception {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        Mockito.when(
            metadataRepositoryMock
                .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(new RepositoryCallException("Not Found", 404,
                null)));

        AccountMetadataTransactionFactory result =
            service.createAccountMetadataTransactionFactory(
                targetAccount.getAddress(),
                metadataKey,
                newValue, sourceAddress).toFuture().get();

        Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
        Assertions.assertEquals(newValue, result.getValue());
        Assertions
            .assertEquals(StringEncoder.getBytes(newValue).length, result.getValueSizeDelta());
        Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());

        Mockito.verify(metadataRepositoryMock)
            .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldNotCreateAccountMetadataTransactionFactoryWhenAnyOtherRemoteException() {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        RepositoryCallException expectedException = new RepositoryCallException(
            "Some other problem.",
            500,
            null);
        Mockito.when(
            metadataRepositoryMock
                .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(expectedException));

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> ExceptionUtils.propagate(() ->
                service.createAccountMetadataTransactionFactory(
                    targetAccount.getAddress(),
                    metadataKey,
                    newValue, sourceAddress).toFuture().get()));

        Assertions.assertEquals(expectedException, exception);

        Mockito.verify(metadataRepositoryMock)
            .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    public void shouldNotCreateAccountMetadataTransactionFactoryWhenBug() {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        IllegalArgumentException expectedException = new IllegalArgumentException(
            "Some unexpected error");
        Mockito.when(
            metadataRepositoryMock
                .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(expectedException));

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.propagate(() ->
                service.createAccountMetadataTransactionFactory(
                    targetAccount.getAddress(),
                    metadataKey,
                    newValue, sourceAddress).toFuture().get()));

        Assertions.assertEquals(expectedException, exception);

        Mockito.verify(metadataRepositoryMock)
            .getAccountMetadataByKeyAndSender(Mockito.eq(targetAccount.getAddress()),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldCreateMosaicMetadataTransactionFactory() throws Exception {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String oldValue = "The original Message";
        String newValue = "the new Message";

        Metadata metadata = new Metadata("someId",
            new MetadataEntry("compositeHash", sourceAddress,
                targetAccount.getAddress(),
                metadataKey, MetadataType.MOSAIC,
                oldValue, Optional.of(targetAccount.getAddress().encoded())));

        Mockito.when(
            metadataRepositoryMock
                .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.just(metadata));

        MosaicMetadataTransactionFactory result =
            service.createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(),
                metadataKey,
                newValue, sourceAddress,
                mosaicId).toFuture().get();

        Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
        Assertions.assertNotEquals(oldValue, result.getValue());
        Assertions.assertNotEquals(newValue, result.getValue());
        Assertions.assertEquals(
            StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
            result.getValueSizeDelta());
        Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
        Assertions.assertEquals(mosaicId.getId(), result.getTargetMosaicId().getId());

        Mockito.verify(metadataRepositoryMock)
            .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldCreateMosaicMetadataTransactionFactoryUsingAlias() throws Exception {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String oldValue = "The original Message";
        String newValue = "the new Message";

        Metadata metadata = new Metadata("someId",
            new MetadataEntry("compositeHash", sourceAddress,
                targetAccount.getAddress(),
                metadataKey, MetadataType.MOSAIC,
                oldValue, Optional.of(targetAccount.getAddress().encoded())));

        Mockito.when(
            metadataRepositoryMock
                .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.just(metadata));

        MosaicMetadataTransactionFactory result =
            service.createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(),
                metadataKey,
                newValue, sourceAddress,
                mosaicAlias).toFuture().get();

        Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
        Assertions.assertNotEquals(oldValue, result.getValue());
        Assertions.assertNotEquals(newValue, result.getValue());
        Assertions.assertEquals(
            StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
            result.getValueSizeDelta());
        Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
        Assertions.assertEquals(mosaicAlias, result.getTargetMosaicId());

        Mockito.verify(metadataRepositoryMock)
            .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldCreateMosaicMetadataTransactionFactoryWhenNotFound() throws Exception {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        Mockito.when(
            metadataRepositoryMock
                .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(new RepositoryCallException("Not Found", 404,
                null)));

        MosaicMetadataTransactionFactory result =
            service.createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(),
                metadataKey,
                newValue, sourceAddress,
                mosaicId).toFuture().get();

        Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
        Assertions.assertEquals(newValue, result.getValue());
        Assertions
            .assertEquals(StringEncoder.getBytes(newValue).length, result.getValueSizeDelta());
        Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
        Assertions.assertEquals(mosaicId.getId(), result.getTargetMosaicId().getId());

        Mockito.verify(metadataRepositoryMock)
            .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldNotCreateMosaicMetadataTransactionFactoryWhenAnyOtherRemoteException() {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        RepositoryCallException expectedException = new RepositoryCallException(
            "Some other problem.",
            500,
            null);
        Mockito.when(
            metadataRepositoryMock
                .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(expectedException));

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> ExceptionUtils.propagate(() ->
                service.createMosaicMetadataTransactionFactory(
                    targetAccount.getAddress(),
                    metadataKey,
                    newValue, sourceAddress, mosaicId).toFuture().get()));

        Assertions.assertEquals(expectedException, exception);

        Mockito.verify(metadataRepositoryMock)
            .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    public void shouldNotCreateMosaicMetadataTransactionFactoryWhenBug() {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        IllegalArgumentException expectedException = new IllegalArgumentException(
            "Some unexpected error");
        Mockito.when(
            metadataRepositoryMock
                .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(expectedException));

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.propagate(() ->
                service.createMosaicMetadataTransactionFactory(
                    targetAccount.getAddress(),
                    metadataKey,
                    newValue, sourceAddress, mosaicId).toFuture().get()));

        Assertions.assertEquals(expectedException, exception);

        Mockito.verify(metadataRepositoryMock)
            .getMosaicMetadataByKeyAndSender(Mockito.eq(mosaicId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldCreateNamespaceMetadataTransactionFactory() throws Exception {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String oldValue = "The original Message";
        String newValue = "the new Message";

        Metadata metadata = new Metadata("someId",
            new MetadataEntry("compositeHash", sourceAddress,
                targetAccount.getAddress(),
                metadataKey, MetadataType.NAMESPACE,
                oldValue, Optional.of(targetAccount.getAddress().encoded())));

        Mockito.when(
            metadataRepositoryMock
                .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.just(metadata));

        NamespaceMetadataTransactionFactory result =
            service.createNamespaceMetadataTransactionFactory(
                targetAccount.getAddress(),
                metadataKey,
                newValue, sourceAddress, namespaceId).toFuture().get();

        Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
        Assertions.assertNotEquals(oldValue, result.getValue());
        Assertions.assertNotEquals(newValue, result.getValue());
        Assertions.assertEquals(
            StringEncoder.getBytes(newValue).length - StringEncoder.getBytes(oldValue).length,
            result.getValueSizeDelta());
        Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
        Assertions.assertEquals(namespaceId.getId(), result.getTargetNamespaceId().getId());

        Mockito.verify(metadataRepositoryMock)
            .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldCreateNamespaceMetadataTransactionFactoryWhenNotFound() throws Exception {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        Mockito.when(
            metadataRepositoryMock
                .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(new RepositoryCallException("Not Found", 404,
                null)));

        NamespaceMetadataTransactionFactory result =
            service.createNamespaceMetadataTransactionFactory(
                targetAccount.getAddress(),
                metadataKey,
                newValue, sourceAddress, namespaceId).toFuture().get();

        Assertions.assertEquals(metadataKey, result.getScopedMetadataKey());
        Assertions.assertEquals(newValue, result.getValue());
        Assertions
            .assertEquals(StringEncoder.getBytes(newValue).length, result.getValueSizeDelta());
        Assertions.assertEquals(targetAccount.getAddress(), result.getTargetAddress());
        Assertions.assertEquals(namespaceId.getId(), result.getTargetNamespaceId().getId());

        Mockito.verify(metadataRepositoryMock)
            .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    void shouldNotCreateNamespaceMetadataTransactionFactoryWhenAnyOtherRemoteException() {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        RepositoryCallException expectedException = new RepositoryCallException(
            "Some other problem.",
            500,
            null);
        Mockito.when(
            metadataRepositoryMock
                .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(expectedException));

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> ExceptionUtils.propagate(() ->
                service.createNamespaceMetadataTransactionFactory(
                    targetAccount.getAddress(),
                    metadataKey,
                    newValue, sourceAddress, namespaceId).toFuture().get()));

        Assertions.assertEquals(expectedException, exception);

        Mockito.verify(metadataRepositoryMock)
            .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

    @Test
    public void shouldNotCreateNamespaceMetadataTransactionFactoryWhenBug() {

        BigInteger metadataKey = BigInteger.valueOf(10);
        String newValue = "the new Message";

        IllegalArgumentException expectedException = new IllegalArgumentException(
            "Some unexpected error");
        Mockito.when(
            metadataRepositoryMock
                .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                    Mockito.eq(metadataKey),
                    Mockito.eq(sourceAddress)))
            .thenReturn(Observable.error(expectedException));

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.propagate(() ->
                service.createNamespaceMetadataTransactionFactory(
                    targetAccount.getAddress(),
                    metadataKey,
                    newValue, sourceAddress, namespaceId).toFuture().get()));

        Assertions.assertEquals(expectedException, exception);

        Mockito.verify(metadataRepositoryMock)
            .getNamespaceMetadataByKeyAndSender(Mockito.eq(namespaceId),
                Mockito.eq(metadataKey),
                Mockito.eq(sourceAddress));
    }

}
