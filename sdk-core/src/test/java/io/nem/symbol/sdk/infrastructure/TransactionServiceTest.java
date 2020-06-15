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

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ReceiptSource;
import io.nem.symbol.sdk.model.receipt.ResolutionEntry;
import io.nem.symbol.sdk.model.receipt.Statement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.FakeDeadline;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionInfo;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests of {@link TransactionService}.
 */
class TransactionServiceTest {


    private final NetworkType networkType = NetworkType.MIJIN_TEST;
    private TransactionServiceImpl service;
    private TransactionRepository transactionRepositoryMock;
    private ReceiptRepository receiptRepositoryMock;
    private Account account;
    private Listener listener;
    private final BigInteger height = BigInteger.TEN;
    private final MosaicId mosaicId1 = new MosaicId("AAAAAAAAAAAAAAA1");
    private final MosaicId mosaicId2 = new MosaicId("AAAAAAAAAAAAAAA2");
    private final MosaicId mosaicId3 = new MosaicId("AAAAAAAAAAAAAAA3");
    private final NamespaceId mosaicNamespace1 = NamespaceId.createFromName("mosaicnamespace1");
    private final NamespaceId mosaicNamespace2 = NamespaceId.createFromName("mosaicnamespace2");
    private final NamespaceId mosaicNamespace3 = NamespaceId.createFromName("mosaicnamespace3");
    private final Address address1 = Account.generateNewAccount(networkType).getAddress();
    private final Address address2 = Account.generateNewAccount(networkType).getAddress();
    private final Address address3 = Account.generateNewAccount(networkType).getAddress();
    private final NamespaceId addressNamespace1 = NamespaceId.createFromName("addressnamespace1");
    private final NamespaceId addressNamespace2 = NamespaceId.createFromName("addressnamespace2");
    private final NamespaceId addressNamespace3 = NamespaceId.createFromName("addressnamespace3");

    @BeforeEach
    void setup() {
        account = Account.generateNewAccount(networkType);

        RepositoryFactory factory = Mockito.mock(RepositoryFactory.class);
        transactionRepositoryMock = Mockito.mock(TransactionRepository.class);
        Mockito.when(factory.createTransactionRepository()).thenReturn(transactionRepositoryMock);

        receiptRepositoryMock = Mockito.mock(ReceiptRepository.class);
        Mockito.when(factory.createReceiptRepository()).thenReturn(receiptRepositoryMock);

        listener = Mockito.mock(Listener.class);
        service = new TransactionServiceImpl(factory);
    }

    @Test
    void announce() throws ExecutionException, InterruptedException {

        TransferTransaction transferTransaction = TransferTransactionFactory
            .create(networkType, Address.generateRandom(networkType), Collections.emptyList(), PlainMessage.Empty)
            .build();

        SignedTransaction signedTransaction = transferTransaction.signWith(account, "abc");
        TransactionAnnounceResponse transactionAnnounceResponse = new TransactionAnnounceResponse("Some Message");

        Mockito.when(transactionRepositoryMock.announce(Mockito.eq(signedTransaction)))
            .thenReturn(Observable.just(transactionAnnounceResponse));

        Mockito
            .when(listener.confirmedOrError(Mockito.eq(account.getAddress()), Mockito.eq(signedTransaction.getHash())))
            .thenReturn(Observable.just(transferTransaction));

        Observable<Transaction> announcedTransaction = service.announce(listener, signedTransaction);

        Assertions.assertEquals(transferTransaction, announcedTransaction.toFuture().get());

    }

    @Test
    void announceAggregateBonded() throws ExecutionException, InterruptedException {

        TransferTransaction transaction1 = TransferTransactionFactory
            .create(networkType, Address.generateRandom(networkType),
                Arrays.asList(new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                new PlainMessage("Some Message")).signer(account.getPublicAccount()).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_BONDED, networkType, Collections.singletonList(transaction1),
                Collections.emptyList()).deadline(new FakeDeadline()).build();

        String generationHash = "abc";
        SignedTransaction aggregateSignedTransaction = aggregateTransaction.signWith(account, generationHash);

        TransactionAnnounceResponse aggregateTransactionAnnounceResponse = new TransactionAnnounceResponse(
            "Aggregate Some Message");

        Mockito.when(transactionRepositoryMock.announceAggregateBonded(Mockito.eq(aggregateSignedTransaction)))
            .thenReturn(Observable.just(aggregateTransactionAnnounceResponse));

        Mockito.when(listener.aggregateBondedAddedOrError(Mockito.eq(account.getAddress()),
            Mockito.eq(aggregateSignedTransaction.getHash()))).thenReturn(Observable.just(aggregateTransaction));

        Observable<AggregateTransaction> announcedTransaction = service
            .announceAggregateBonded(listener, aggregateSignedTransaction);

        Assertions.assertEquals(aggregateTransaction, announcedTransaction.toFuture().get());

    }

    @Test
    void announceHashLockAggregateBonded() throws ExecutionException, InterruptedException {

        TransferTransaction transaction1 = TransferTransactionFactory
            .create(networkType, Address.generateRandom(networkType), Collections
                    .singletonList(new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                new PlainMessage("Some Message")).signer(account.getPublicAccount()).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_BONDED, networkType, Collections.singletonList(transaction1),
                Collections.emptyList()).deadline(new FakeDeadline()).build();

        String generationHash = "abc";
        SignedTransaction aggregateSignedTransaction = aggregateTransaction.signWith(account, generationHash);

        HashLockTransaction hashLockTransaction = HashLockTransactionFactory
            .create(networkType, NetworkCurrency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100), aggregateSignedTransaction.getHash()).build();

        SignedTransaction hashLockSignedTranscation = hashLockTransaction.signWith(account, generationHash);

        TransactionAnnounceResponse aggregateTransactionAnnounceResponse = new TransactionAnnounceResponse(
            "Aggregate Some Message");

        TransactionAnnounceResponse hashTransactionAnnounceResponse = new TransactionAnnounceResponse(
            "Hash Some Message");

        Mockito.when(transactionRepositoryMock.announceAggregateBonded(Mockito.eq(aggregateSignedTransaction)))
            .thenReturn(Observable.just(aggregateTransactionAnnounceResponse));

        Mockito.when(transactionRepositoryMock.announce(Mockito.eq(hashLockSignedTranscation)))
            .thenReturn(Observable.just(hashTransactionAnnounceResponse));

        Mockito.when(listener
            .confirmedOrError(Mockito.eq(account.getAddress()), Mockito.eq(hashLockSignedTranscation.getHash())))
            .thenReturn(Observable.just(hashLockTransaction));

        Mockito.when(listener.aggregateBondedAddedOrError(Mockito.eq(account.getAddress()),
            Mockito.eq(aggregateSignedTransaction.getHash()))).thenReturn(Observable.just(aggregateTransaction));

        Observable<AggregateTransaction> announcedTransaction = service
            .announceHashLockAggregateBonded(listener, hashLockSignedTranscation, aggregateSignedTransaction);

        Assertions.assertEquals(aggregateTransaction, announcedTransaction.toFuture().get());

    }

    @Test
    void transferTransactionResolveAlias() throws ExecutionException, InterruptedException {

        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(mosaicId1, BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(mosaicNamespace2, BigInteger.valueOf(2)));
        mosaics.add(new Mosaic(mosaicId3, BigInteger.valueOf(3)));

        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";

        TransactionFactory<TransferTransaction> factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, recipient, mosaics, PlainMessage.Empty)
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        simulateStatement(height, 1, 0);

        TransferTransaction transaction = factory.build();

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito.when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        TransferTransaction resolvedTransaction = (TransferTransaction) service.resolveAliases(hashes).toFuture().get()
            .get(0);

        Assertions.assertEquals(transaction.getMosaics().size(), resolvedTransaction.getMosaics().size());

        Assertions
            .assertEquals(transaction.getMosaics().get(0).getId(), resolvedTransaction.getMosaics().get(0).getId());

        Assertions.assertEquals(BigInteger.valueOf(1), resolvedTransaction.getMosaics().get(0).getAmount());

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaics().get(1).getId());

        Assertions.assertEquals(BigInteger.valueOf(2), resolvedTransaction.getMosaics().get(1).getAmount());

        Assertions
            .assertEquals(transaction.getMosaics().get(2).getId(), resolvedTransaction.getMosaics().get(2).getId());

        Assertions.assertEquals(BigInteger.valueOf(3), resolvedTransaction.getMosaics().get(2).getAmount());

        Assertions.assertEquals(address1, resolvedTransaction.getRecipient());

    }

    @Test
    void transferTransactionResolveAliasCannotAddressResolveAliases() {

        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(mosaicId1, BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(mosaicNamespace2, BigInteger.valueOf(2)));
        mosaics.add(new Mosaic(mosaicId3, BigInteger.valueOf(3)));

        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";

        TransactionFactory<TransferTransaction> factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, recipient, mosaics, PlainMessage.Empty)
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        simulateStatement(height, 0, 0);

        TransferTransaction transaction = factory.build();

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->

            ExceptionUtils.propagate(() -> service.resolveAliases(hashes).toFuture().get()));
        Assertions.assertEquals("Address could not be resolved for alias " + addressNamespace1.getIdAsHex(),
            exception.getMessage());

    }

    @Test
    void transferTransactionResolveAliasCannotMosaicAliasResolveAliases() {

        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(mosaicId1, BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(mosaicNamespace2, BigInteger.valueOf(2)));
        NamespaceId idontexist = NamespaceId.createFromName("idontexist");
        mosaics.add(new Mosaic(idontexist, BigInteger.valueOf(3)));

        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";

        TransactionFactory<TransferTransaction> factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, recipient, mosaics, PlainMessage.Empty)
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        simulateStatement(height, 1, 0);

        TransferTransaction transaction = factory.build();

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->

            ExceptionUtils.propagate(() -> service.resolveAliases(hashes).toFuture().get()));
        Assertions.assertEquals("MosaicId could not be resolved for alias " + idontexist.getIdAsHex(),
            exception.getMessage());

    }


    @Test
    void secretLockTransactionResolveAlias() throws ExecutionException, InterruptedException {

        Mosaic unresolvedMosaicId = new Mosaic(mosaicNamespace2, BigInteger.valueOf(2));
        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";
        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";

        TransactionFactory<SecretLockTransaction> factory = SecretLockTransactionFactory
            .create(NetworkType.MIJIN_TEST, unresolvedMosaicId, BigInteger.TEN, LockHashAlgorithmType.SHA3_256, secret,
                recipient).transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        simulateStatement(height, 1, 0);

        SecretLockTransaction transaction = factory.build();

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        SecretLockTransaction resolvedTransaction = (SecretLockTransaction) service.resolveAliases(hashes).toFuture()
            .get().get(0);

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaic().getId());

        Assertions.assertEquals(address1, resolvedTransaction.getRecipient());

    }

    @Test
    void secretProofTransactionResolveAlias() throws ExecutionException, InterruptedException {

        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";
        String secret = "1118ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        String proof = "2228ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";

        TransactionFactory<SecretProofTransaction> factory = SecretProofTransactionFactory
            .create(NetworkType.MIJIN_TEST, LockHashAlgorithmType.SHA3_256, recipient, secret, proof)
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        simulateStatement(height, 1, 0);

        SecretProofTransaction transaction = factory.build();

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        SecretProofTransaction resolvedTransaction = (SecretProofTransaction) service.resolveAliases(hashes).toFuture()
            .get().get(0);

        Assertions.assertEquals(address1, resolvedTransaction.getRecipient());

    }

    @Test
    void mosaicGlobalRestrictionTransactionResolveAlias() throws ExecutionException, InterruptedException {

        String transactionHash = "aaaa";

        MosaicGlobalRestrictionTransactionFactory mosaicGlobalRestrictionTransactionFactory = MosaicGlobalRestrictionTransactionFactory
            .create(NetworkType.MIJIN_TEST, mosaicNamespace2, BigInteger.TEN, BigInteger.ONE, MosaicRestrictionType.GT);
        mosaicGlobalRestrictionTransactionFactory.referenceMosaicId(mosaicNamespace3);
        mosaicGlobalRestrictionTransactionFactory.previousRestrictionType(MosaicRestrictionType.EQ);
        mosaicGlobalRestrictionTransactionFactory.previousRestrictionValue(BigInteger.valueOf(3));
        TransactionFactory<MosaicGlobalRestrictionTransaction> factory = mosaicGlobalRestrictionTransactionFactory
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        MosaicGlobalRestrictionTransaction transaction = factory.build();

        simulateStatement(height, 1, 0);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        MosaicGlobalRestrictionTransaction resolvedTransaction = (MosaicGlobalRestrictionTransaction) service
            .resolveAliases(hashes).toFuture().get().get(0);

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaicId());
        Assertions.assertEquals(mosaicId3, resolvedTransaction.getReferenceMosaicId());

    }

    @Test
    void mosaicAddressRestrictionTransactionResolveAlias() throws ExecutionException, InterruptedException {

        String transactionHash = "aaaa";

        TransactionFactory<MosaicAddressRestrictionTransaction> factory = MosaicAddressRestrictionTransactionFactory
            .create(NetworkType.MIJIN_TEST, mosaicNamespace2, BigInteger.TEN, addressNamespace3, BigInteger.ONE)
            .previousRestrictionValue(BigInteger.valueOf(3)).previousRestrictionValue(BigInteger.valueOf(3))
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        MosaicAddressRestrictionTransaction transaction = factory.build();

        simulateStatement(height, 1, 0);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        MosaicAddressRestrictionTransaction resolvedTransaction = (MosaicAddressRestrictionTransaction) service
            .resolveAliases(hashes).toFuture().get().get(0);

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaicId());
        Assertions.assertEquals(address3, resolvedTransaction.getTargetAddress());

    }

    @Test
    void accountMosaicRestrictionTransactionResolveAlias() throws ExecutionException, InterruptedException {

        String transactionHash = "aaaa";

        TransactionFactory<AccountMosaicRestrictionTransaction> factory = AccountMosaicRestrictionTransactionFactory
            .create(NetworkType.MIJIN_TEST, AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
                Arrays.asList(mosaicNamespace1, mosaicId2, mosaicNamespace2),
                Arrays.asList(mosaicNamespace2, mosaicNamespace3, mosaicId3))
            .transactionInfo(TransactionInfo.create(height, 4, "ABC", transactionHash, ""));

        AccountMosaicRestrictionTransaction transaction = factory.build();

        simulateStatement(height, 5, 0);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        AccountMosaicRestrictionTransaction resolvedTransaction = (AccountMosaicRestrictionTransaction) service
            .resolveAliases(hashes).toFuture().get().get(0);

        Assertions.assertEquals(Arrays.asList(mosaicId1, mosaicId2, mosaicId2),
            resolvedTransaction.getRestrictionAdditions());
        Assertions.assertEquals(Arrays.asList(mosaicId2, mosaicId3, mosaicId3),
            resolvedTransaction.getRestrictionDeletions());

    }

    @Test
    void mosaicMetadataTransactionResolveAlias() throws ExecutionException, InterruptedException {

        String transactionHash = "aaaa";

        TransactionFactory<MosaicMetadataTransaction> factory = MosaicMetadataTransactionFactory
            .create(NetworkType.MIJIN_TEST, Account.generateNewAccount(networkType).getAddress(), mosaicNamespace2,
                BigInteger.TEN, "Value").transactionInfo(TransactionInfo.create(height, 4, "ABC", transactionHash, ""));

        MosaicMetadataTransaction transaction = factory.build();

        simulateStatement(height, 5, 0);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        MosaicMetadataTransaction resolvedTransaction = (MosaicMetadataTransaction) service.resolveAliases(hashes)
            .toFuture().get().get(0);

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getTargetMosaicId());

    }

    @Test
    void mosaicSupplyChangeTransactionResolveAlias() throws ExecutionException, InterruptedException {

        String transactionHash = "aaaa";

        TransactionFactory<MosaicSupplyChangeTransaction> factory = MosaicSupplyChangeTransactionFactory
            .create(NetworkType.MIJIN_TEST, mosaicNamespace2, MosaicSupplyChangeActionType.INCREASE, BigInteger.ONE)
            .transactionInfo(TransactionInfo.create(height, 4, "ABC", transactionHash, ""));

        MosaicSupplyChangeTransaction transaction = factory.build();

        simulateStatement(height, 5, 0);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        MosaicSupplyChangeTransaction resolvedTransaction = (MosaicSupplyChangeTransaction) service
            .resolveAliases(hashes).toFuture().get().get(0);

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaicId());

    }

    @Test
    void mosaicDefinitionTransactionResolveAlias() throws ExecutionException, InterruptedException {

        String transactionHash = "aaaa";

        TransactionFactory<MosaicDefinitionTransaction> factory = MosaicDefinitionTransactionFactory
            .create(NetworkType.MIJIN_TEST, MosaicNonce.createFromBigInteger(new BigInteger("0")), mosaicId2,
                MosaicFlags.create(true, true, true), 4, new BlockDuration(10000))
            .transactionInfo(TransactionInfo.create(height, 4, "ABC", transactionHash, ""));

        MosaicDefinitionTransaction transaction = factory.build();

        simulateStatement(height, 5, 0);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        MosaicDefinitionTransaction resolvedTransaction = (MosaicDefinitionTransaction) service.resolveAliases(hashes)
            .toFuture().get().get(0);

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaicId());

    }

    @Test
    void accountAddressRestrictionTransactionResolveAlias() throws ExecutionException, InterruptedException {

        String transactionHash = "aaaa";

        TransactionFactory<AccountAddressRestrictionTransaction> factory = AccountAddressRestrictionTransactionFactory
            .create(NetworkType.MIJIN_TEST, AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
                Arrays.asList(addressNamespace1, address2, addressNamespace2),
                Arrays.asList(addressNamespace2, addressNamespace3, address3))
            .transactionInfo(TransactionInfo.create(height, 4, "ABC", transactionHash, ""));

        AccountAddressRestrictionTransaction transaction = factory.build();

        simulateStatement(height, 5, 0);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        AccountAddressRestrictionTransaction resolvedTransaction = (AccountAddressRestrictionTransaction) service
            .resolveAliases(hashes).toFuture().get().get(0);

        Assertions
            .assertEquals(Arrays.asList(address1, address2, address2), resolvedTransaction.getRestrictionAdditions());
        Assertions
            .assertEquals(Arrays.asList(address2, address3, address3), resolvedTransaction.getRestrictionDeletions());
    }

    @Test
    void hashLockTransactionResolveAlias() throws ExecutionException, InterruptedException {

        Mosaic unresolvedMosaicId = new Mosaic(mosaicNamespace2, BigInteger.valueOf(2));

        String transactionHash = "aaaa";

        TransactionFactory<HashLockTransaction> factory = HashLockTransactionFactory
            .create(NetworkType.MIJIN_TEST, unresolvedMosaicId, BigInteger.TEN, "SomeHash")
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        simulateStatement(height, 1, 0);

        HashLockTransaction transaction = factory.build();

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(transaction)));

        HashLockTransaction resolvedTransaction = (HashLockTransaction) service.resolveAliases(hashes).toFuture().get()
            .get(0);

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaic().getId());

    }


    @Test
    void aggregateTransferTransactionResolveAlias() throws ExecutionException, InterruptedException {

        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(mosaicId1, BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(mosaicNamespace2, BigInteger.valueOf(2)));
        mosaics.add(new Mosaic(mosaicId3, BigInteger.valueOf(3)));

        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";

        TransactionFactory<TransferTransaction> factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, recipient, mosaics, PlainMessage.Empty)
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", "BBBB", ""));

        //Extra transfer not aliases
        TransactionFactory<TransferTransaction> extraTransaction = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, address2,
                Collections.singletonList(new Mosaic(mosaicId1, BigInteger.valueOf(1))), PlainMessage.Empty)
            .transactionInfo(TransactionInfo.create(height, 1, "ABC", "CCCC", ""));

        TransferTransaction transferTransaction = factory.build();

        transferTransaction.toAggregate(Account.generateNewAccount(networkType).getPublicAccount());

        TransactionFactory<AggregateTransaction> aggregateTransactionFactory = AggregateTransactionFactory
            .createComplete(NetworkType.MIJIN_TEST, Arrays.asList(transferTransaction,
                extraTransaction.build().toAggregate(Account.generateNewAccount(networkType).getPublicAccount())))
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        AggregateTransaction aggregateTransaction = aggregateTransactionFactory.build();

        simulateStatement(height, 1, 1);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(aggregateTransaction)));

        AggregateTransaction aggregateResolvedTransaction = (AggregateTransaction) service.resolveAliases(hashes)
            .toFuture().get().get(0);

        TransferTransaction resolvedTransaction = (TransferTransaction) aggregateResolvedTransaction
            .getInnerTransactions().get(0);

        Assertions.assertEquals(transferTransaction.getMosaics().size(), resolvedTransaction.getMosaics().size());

        Assertions.assertEquals(transferTransaction.getMosaics().get(0).getId(),
            resolvedTransaction.getMosaics().get(0).getId());

        Assertions.assertEquals(BigInteger.valueOf(1), resolvedTransaction.getMosaics().get(0).getAmount());

        Assertions.assertEquals(mosaicId2, resolvedTransaction.getMosaics().get(1).getId());

        Assertions.assertEquals(BigInteger.valueOf(2), resolvedTransaction.getMosaics().get(1).getAmount());

        Assertions.assertEquals(transferTransaction.getMosaics().get(2).getId(),
            resolvedTransaction.getMosaics().get(2).getId());

        Assertions.assertEquals(BigInteger.valueOf(3), resolvedTransaction.getMosaics().get(2).getAmount());

        Assertions.assertEquals(address1, resolvedTransaction.getRecipient());

    }

    @Test
    void aggregateTransferTransactionResolveAliasFailWhenNoTransactionInfo() {

        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(mosaicId1, BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(mosaicNamespace2, BigInteger.valueOf(2)));
        mosaics.add(new Mosaic(mosaicId3, BigInteger.valueOf(3)));

        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";

        TransactionFactory<TransferTransaction> factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, recipient, mosaics, PlainMessage.Empty)
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", "BBBB", ""));

        //Extra transfer not aliases
        TransactionFactory<TransferTransaction> extraTransaction = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, address2,
                Collections.singletonList(new Mosaic(mosaicId1, BigInteger.valueOf(1))), PlainMessage.Empty);

        TransferTransaction transferTransaction = factory.build();

        transferTransaction.toAggregate(Account.generateNewAccount(networkType).getPublicAccount());

        TransactionFactory<AggregateTransaction> aggregateTransactionFactory = AggregateTransactionFactory
            .createComplete(NetworkType.MIJIN_TEST, Arrays.asList(transferTransaction,
                extraTransaction.build().toAggregate(Account.generateNewAccount(networkType).getPublicAccount())))
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        AggregateTransaction aggregateTransaction = aggregateTransactionFactory.build();

        simulateStatement(height, 1, 1);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(aggregateTransaction)));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ExceptionUtils.propagate(service.resolveAliases(hashes).toFuture()::get);
        });

        Assertions.assertEquals("TransactionIndex cannot be loaded from Transaction TRANSFER", exception.getMessage());
    }


    @Test
    void aggregateTransferTransactionResolveAliasFailWhenNoTransactionInfoInner() {

        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(mosaicId1, BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(mosaicNamespace2, BigInteger.valueOf(2)));
        mosaics.add(new Mosaic(mosaicId3, BigInteger.valueOf(3)));

        UnresolvedAddress recipient = addressNamespace1;

        String transactionHash = "aaaa";

        TransactionFactory<TransferTransaction> factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, recipient, mosaics, PlainMessage.Empty)
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", "BBBB", ""));

        //Extra transfer not aliases
        TransactionFactory<TransferTransaction> extraTransaction = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST, address2,
                Collections.singletonList(new Mosaic(mosaicId1, BigInteger.valueOf(1))), PlainMessage.Empty);

        TransferTransaction transferTransaction = factory.build();

        transferTransaction.toAggregate(Account.generateNewAccount(networkType).getPublicAccount());

        TransactionFactory<AggregateTransaction> aggregateTransactionFactory = AggregateTransactionFactory
            .createComplete(NetworkType.MIJIN_TEST, Arrays.asList(transferTransaction,
                extraTransaction.build().toAggregate(Account.generateNewAccount(networkType).getPublicAccount())))
            .transactionInfo(TransactionInfo.create(height, 0, "ABC", transactionHash, ""));

        AggregateTransaction aggregateTransaction = aggregateTransactionFactory.build();

        simulateStatement(height, 1, 1);

        List<String> hashes = Collections.singletonList(transactionHash);

        Mockito
            .when(transactionRepositoryMock.getTransactions(Mockito.eq(TransactionGroup.CONFIRMED), Mockito.eq(hashes)))
            .thenReturn(Observable.just(Collections.singletonList(aggregateTransaction)));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ExceptionUtils.propagate(service.resolveAliases(hashes).toFuture()::get);
        });

        Assertions.assertEquals("TransactionIndex cannot be loaded from Transaction TRANSFER", exception.getMessage());
    }

    private void simulateStatement(BigInteger height, int primaryId, int secondaryId) {
        List<TransactionStatement> transactionStatements = Collections.emptyList();

        Map<NamespaceId, Address> addressMap = new HashMap<>();
        addressMap.put(addressNamespace1, address1);
        addressMap.put(addressNamespace2, address2);
        addressMap.put(addressNamespace3, address3);

        Map<NamespaceId, MosaicId> mosaicMap = new HashMap<>();
        mosaicMap.put(mosaicNamespace1, mosaicId1);
        mosaicMap.put(mosaicNamespace2, mosaicId2);
        mosaicMap.put(mosaicNamespace3, mosaicId3);

        List<AddressResolutionStatement> addressResolutionStatements = addressMap.entrySet().stream().map(
            e -> new AddressResolutionStatement(height, e.getKey(), Collections
                .singletonList(ResolutionEntry.forAddress(e.getValue(), new ReceiptSource(primaryId, secondaryId)))))
            .collect(Collectors.toList());

        List<MosaicResolutionStatement> mosaicResolutionStatements = mosaicMap.entrySet().stream().map(
            e -> new MosaicResolutionStatement(height, e.getKey(), Collections
                .singletonList(ResolutionEntry.forMosaicId(e.getValue(), new ReceiptSource(primaryId, secondaryId)))))
            .collect(Collectors.toList());

        Statement statement = new Statement(transactionStatements, addressResolutionStatements,
            mosaicResolutionStatements);

        Mockito.when(receiptRepositoryMock.getBlockReceipts(Mockito.eq(height))).thenReturn(Observable.just(statement));

    }


}
