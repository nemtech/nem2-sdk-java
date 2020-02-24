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

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.namespace.AddressAlias;
import io.nem.symbol.sdk.model.namespace.MosaicAlias;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestrictionItem;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests of {@link MosaicRestrictionTransactionServiceImpl}
 */
public class MosaicRestrictionTransactionServiceTest {


    private NetworkType networkType = NetworkType.MIJIN_TEST;
    private MosaicRestrictionTransactionServiceImpl service;
    private RestrictionMosaicRepository restrictionMosaicRepository;

    private NamespaceRepository namespaceRepository;

    private Account account1 = Account.generateNewAccount(networkType);
    private Account account2 = Account.generateNewAccount(networkType);
    private NamespaceId accountAlias1 = NamespaceId.createFromName("accountAlias1".toLowerCase());
    private NamespaceId accountAlias2 = NamespaceId.createFromName("accountAlias2".toLowerCase());


    private MosaicId mosaicId1 = MosaicId
        .createFromNonce(MosaicNonce.createRandom(), account1.getPublicAccount());
    private MosaicId mosaicId2 = MosaicId
        .createFromNonce(MosaicNonce.createRandom(), account2.getPublicAccount());

    private NamespaceId mosaicAlias1 = NamespaceId.createFromName("mosaicAlias1".toLowerCase());
    private NamespaceId mosaicAlias2 = NamespaceId.createFromName("mosaicAlias2".toLowerCase());

    private MosaicId mosaicIdWrongKey = new MosaicId("AAAAAAAAAAAAAAAA");
    private MosaicId mosaicIdNotFound = new MosaicId("BBBBBBBBBBBBBBBB");

    private BigInteger restrictionKey = BigInteger.TEN;

    @BeforeEach
    void setup() {

        RepositoryFactory factory = mock(RepositoryFactory.class);
        restrictionMosaicRepository = mock(RestrictionMosaicRepository.class);
        when(factory.createRestrictionMosaicRepository()).thenReturn(
            restrictionMosaicRepository);

        namespaceRepository = mock(NamespaceRepository.class);
        when(factory.createNamespaceRepository()).thenReturn(namespaceRepository);

        when(factory.getNetworkType()).thenReturn(Observable.just(networkType));
        service = new MosaicRestrictionTransactionServiceImpl(factory);

        when(restrictionMosaicRepository
            .getMosaicGlobalRestriction(eq(mosaicId1)))
            .thenReturn(Observable.just(mockGlobalRestriction()));

        when(restrictionMosaicRepository
            .getMosaicGlobalRestriction(eq(mosaicId2)))
            .thenReturn(Observable.just(mockGlobalRestriction()));

        when(restrictionMosaicRepository
            .getMosaicGlobalRestriction(eq(mosaicIdWrongKey)))
            .thenReturn(Observable.error(() -> new IllegalStateException("Not a nice mosaic id")));

        when(restrictionMosaicRepository
            .getMosaicGlobalRestriction(eq(mosaicIdNotFound)))
            .thenReturn(Observable
                .error(() -> new RepositoryCallException("NotFound", 404,
                    new IllegalStateException())));

        when(restrictionMosaicRepository
            .getMosaicAddressRestriction(eq(mosaicId1), eq(account1.getAddress())))
            .thenReturn(Observable.just(mockAddressRestriction()));

        when(restrictionMosaicRepository
            .getMosaicAddressRestriction(eq(mosaicId2), eq(account1.getAddress())))
            .thenReturn(Observable
                .error(() -> new RepositoryCallException("NotFound", 404,
                    new IllegalStateException())));

        when(restrictionMosaicRepository
            .getMosaicAddressRestriction(eq(mosaicIdWrongKey), eq(account1.getAddress())))
            .thenReturn(Observable.error(() -> new IllegalStateException("Not a nice mosaic id")));

        when(restrictionMosaicRepository
            .getMosaicAddressRestriction(eq(mosaicIdNotFound), eq(account1.getAddress())))
            .thenReturn(Observable
                .error(() -> new RepositoryCallException("NotFound", 404,
                    new IllegalStateException())));

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

        return new NamespaceInfo(true, 0, "metadaId", NamespaceRegistrationType.ROOT_NAMESPACE, 1,
            Collections.emptyList(), null, null, BigInteger.ONE, BigInteger.TEN,
            new AddressAlias(address));
    }

    private NamespaceInfo createAlias(MosaicId mosaicId) {

        return new NamespaceInfo(true, 0, "metadaId", NamespaceRegistrationType.ROOT_NAMESPACE, 1,
            Collections.emptyList(), null, null, BigInteger.ONE, BigInteger.TEN,
            new MosaicAlias(mosaicId));
    }


    @Test
    void createMosaicGlobalRestrictionTransactionFactoryWhenExist() throws Exception {
        MosaicGlobalRestrictionTransaction transaction = service
            .createMosaicGlobalRestrictionTransactionFactory(mosaicId1, restrictionKey,
                BigInteger.valueOf(30), MosaicRestrictionType.GE).toFuture().get().build();

        Assertions.assertEquals(networkType, transaction.getNetworkType());
        Assertions.assertEquals(mosaicId1, transaction.getMosaicId());
        Assertions.assertEquals(restrictionKey, transaction.getRestrictionKey());
        Assertions.assertEquals(new MosaicId(BigInteger.ZERO), transaction.getReferenceMosaicId());
        Assertions.assertEquals(BigInteger.valueOf(30), transaction.getNewRestrictionValue());
        Assertions.assertEquals(MosaicRestrictionType.GE, transaction.getNewRestrictionType());
        Assertions.assertEquals(BigInteger.valueOf(20), transaction.getPreviousRestrictionValue());
        Assertions.assertEquals(MosaicRestrictionType.EQ, transaction.getPreviousRestrictionType());
        Assertions.assertEquals(networkType, transaction.getNetworkType());
    }

    @Test
    void createMosaicGlobalRestrictionTransactionFactoryWhenExistUsingAlias() throws Exception {
        MosaicGlobalRestrictionTransaction transaction = service
            .createMosaicGlobalRestrictionTransactionFactory(mosaicAlias1, restrictionKey,
                BigInteger.valueOf(30), MosaicRestrictionType.GE).toFuture().get().build();

        Assertions.assertEquals(networkType, transaction.getNetworkType());
        Assertions.assertEquals(mosaicAlias1, transaction.getMosaicId());
        Assertions.assertEquals(restrictionKey, transaction.getRestrictionKey());
        Assertions.assertEquals(new MosaicId(BigInteger.ZERO), transaction.getReferenceMosaicId());
        Assertions.assertEquals(BigInteger.valueOf(30), transaction.getNewRestrictionValue());
        Assertions.assertEquals(MosaicRestrictionType.GE, transaction.getNewRestrictionType());
        Assertions.assertEquals(BigInteger.valueOf(20), transaction.getPreviousRestrictionValue());
        Assertions.assertEquals(MosaicRestrictionType.EQ, transaction.getPreviousRestrictionType());
        Assertions.assertEquals(networkType, transaction.getNetworkType());
    }

    @Test
    void createMosaicGlobalRestrictionTransactionFactoryWhenDoesNotExist() throws Exception {
        MosaicGlobalRestrictionTransaction transaction = service
            .createMosaicGlobalRestrictionTransactionFactory(mosaicIdNotFound,
                restrictionKey,
                BigInteger.valueOf(30), MosaicRestrictionType.GE).toFuture().get().build();

        Assertions.assertEquals(networkType, transaction.getNetworkType());
        Assertions.assertEquals(mosaicIdNotFound, transaction.getMosaicId());
        Assertions.assertEquals(restrictionKey, transaction.getRestrictionKey());
        Assertions.assertEquals(new MosaicId(BigInteger.ZERO), transaction.getReferenceMosaicId());
        Assertions.assertEquals(BigInteger.valueOf(30), transaction.getNewRestrictionValue());
        Assertions.assertEquals(MosaicRestrictionType.GE, transaction.getNewRestrictionType());
        Assertions.assertEquals(BigInteger.ZERO, transaction.getPreviousRestrictionValue());
        Assertions
            .assertEquals(MosaicRestrictionType.NONE, transaction.getPreviousRestrictionType());
        Assertions.assertEquals(networkType, transaction.getNetworkType());
    }

    @Test
    void createMosaicGlobalRestrictionTransactionFactoryWhenError() throws Exception {

        IllegalStateException exception = Assertions
            .assertThrows(IllegalStateException.class, () -> ExceptionUtils.propagate(() ->
                service
                    .createMosaicGlobalRestrictionTransactionFactory(mosaicIdWrongKey,
                        restrictionKey,
                        BigInteger.valueOf(30), MosaicRestrictionType.GE).toFuture().get()));

        Assertions.assertEquals("Not a nice mosaic id", exception.getMessage());

    }

    @Test
    void createMosaicGlobalRestrictionTransactionFactoryWhenAliasDoesNotExist() throws Exception {

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.propagate(() ->
                service
                    .createMosaicGlobalRestrictionTransactionFactory(
                        NamespaceId.createFromName("invalid"),
                        restrictionKey,
                        BigInteger.valueOf(30), MosaicRestrictionType.GE).toFuture().get()));

        Assertions.assertEquals("MosaicId could not be resolved from alias E6F4C6CB5111B127",
            exception.getMessage());

    }


    @Test
    void createMosaicAddressRestrictionTransactionFactoryWhenExist() throws Exception {
        MosaicAddressRestrictionTransaction transaction = service
            .createMosaicAddressRestrictionTransactionFactory(mosaicId1,
                restrictionKey, account1.getAddress(),
                BigInteger.valueOf(40)).toFuture().get().build();

        Assertions.assertEquals(networkType, transaction.getNetworkType());
        Assertions.assertEquals(mosaicId1, transaction.getMosaicId());
        Assertions.assertEquals(restrictionKey, transaction.getRestrictionKey());
        Assertions.assertEquals(account1.getAddress(), transaction.getTargetAddress());
        Assertions.assertEquals(BigInteger.valueOf(40), transaction.getNewRestrictionValue());
        Assertions.assertEquals(BigInteger.valueOf(30), transaction.getPreviousRestrictionValue());
        Assertions.assertEquals(networkType, transaction.getNetworkType());
    }

    @Test
    void createMosaicAddressRestrictionTransactionFactoryWhenExistUsingAlias() throws Exception {
        MosaicAddressRestrictionTransaction transaction = service
            .createMosaicAddressRestrictionTransactionFactory(mosaicAlias1,
                restrictionKey, accountAlias1,
                BigInteger.valueOf(40)).toFuture().get().build();

        Assertions.assertEquals(networkType, transaction.getNetworkType());
        Assertions.assertEquals(mosaicAlias1, transaction.getMosaicId());
        Assertions.assertEquals(restrictionKey, transaction.getRestrictionKey());
        Assertions.assertEquals(accountAlias1, transaction.getTargetAddress());
        Assertions.assertEquals(BigInteger.valueOf(40), transaction.getNewRestrictionValue());
        Assertions.assertEquals(BigInteger.valueOf(30), transaction.getPreviousRestrictionValue());
        Assertions.assertEquals(networkType, transaction.getNetworkType());
    }

    @Test
    void createMosaicAddressRestrictionTransactionFactoryWhenDoesNotExist() throws Exception {
        MosaicAddressRestrictionTransaction transaction = service
            .createMosaicAddressRestrictionTransactionFactory(mosaicId2,
                restrictionKey, account1.getAddress(),
                BigInteger.valueOf(40)).toFuture().get().build();

        Assertions.assertEquals(networkType, transaction.getNetworkType());
        Assertions.assertEquals(mosaicId2, transaction.getMosaicId());
        Assertions.assertEquals(restrictionKey, transaction.getRestrictionKey());
        Assertions.assertEquals(account1.getAddress(), transaction.getTargetAddress());
        Assertions.assertEquals(BigInteger.valueOf(40), transaction.getNewRestrictionValue());
        Assertions.assertEquals(new BigInteger("FFFFFFFFFFFFFFFF", 16),
            transaction.getPreviousRestrictionValue());
        Assertions.assertEquals(networkType, transaction.getNetworkType());
    }

    @Test
    void createMosaicAddressRestrictionTransactionFactoryWhenGlobalNotFound() throws Exception {

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.propagate(() ->
                service
                    .createMosaicAddressRestrictionTransactionFactory(mosaicIdNotFound,
                        restrictionKey, account1.getAddress(),
                        BigInteger.valueOf(30)).toFuture().get()));

        Assertions.assertEquals("Global restriction is not valid for RestrictionKey: 10",
            exception.getMessage());

    }

    @Test
    void createMosaicAddressRestrictionTransactionFactoryWhenAliasesNotFound() throws Exception {

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.propagate(() ->
                service
                    .createMosaicAddressRestrictionTransactionFactory(
                        NamespaceId.createFromName("invalidmosaicalias"),
                        restrictionKey, NamespaceId.createFromName("invalidaddressaslias"),
                        BigInteger.valueOf(30)).toFuture().get()));

        Assertions.assertEquals("MosaicId could not be resolved from alias 9CA319E451849811",
            exception.getMessage());

    }

    @Test
    void createMosaicAddressRestrictionTransactionFactoryWhenAddressAliasNotFound() {

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> ExceptionUtils.propagate(() ->
                service
                    .createMosaicAddressRestrictionTransactionFactory(
                        mosaicAlias1,
                        restrictionKey, NamespaceId.createFromName("invalidaddressaslias"),
                        BigInteger.valueOf(30)).toFuture().get()));

        Assertions.assertEquals("Address could not be resolved from alias 98CC55CCA3F13503",
            exception.getMessage());

    }

    @Test
    void createMosaicAddressRestrictionTransactionFactoryWhenError() throws Exception {

        IllegalStateException exception = Assertions
            .assertThrows(IllegalStateException.class, () -> ExceptionUtils.propagate(() ->
                service
                    .createMosaicAddressRestrictionTransactionFactory(mosaicIdWrongKey,
                        restrictionKey, account1.getAddress(),
                        BigInteger.valueOf(30)).toFuture().get()));

        Assertions.assertEquals("Not a nice mosaic id", exception.getMessage());

    }


    private MosaicGlobalRestriction mockGlobalRestriction() {
        return new MosaicGlobalRestriction(
            "AAAA",
            MosaicRestrictionEntryType.GLOBAL,
            mosaicId1,
            Collections.singletonMap(restrictionKey,
                new MosaicGlobalRestrictionItem(mosaicId1, BigInteger.valueOf(20),
                    MosaicRestrictionType.EQ)));
    }

    private MosaicAddressRestriction mockAddressRestriction() {
        return new MosaicAddressRestriction(
            "BBBB",
            MosaicRestrictionEntryType.GLOBAL,
            mosaicId1,
            account1.getAddress(),
            Collections.singletonMap(restrictionKey, BigInteger.valueOf(30)));
    }

}
