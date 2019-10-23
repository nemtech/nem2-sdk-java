/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.CosignatureTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings("squid:S1607")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//TODO BROKEN!
class ListenerIntegrationTest extends BaseIntegrationTest {

    private static final int TIMEOUT = 10;

    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    private Account account = config().getTestAccount();
    private Account multisigAccount = config().getMultisigAccount();
    private Account cosignatoryAccount = config().getCosignatoryAccount();
    private Account cosignatoryAccount2 = config().getCosignatory2Account();
    private String generationHash = this.getGenerationHash();


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldConnectToWebSocket(RepositoryType type)
        throws ExecutionException, InterruptedException {
        Listener listener = getRepositoryFactory(type).createListener();
        CompletableFuture<Void> connected = listener.open();
        connected.get();
        assertTrue(connected.isDone());
        assertNotNull(listener.getUid());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnNewBlockViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        this.announceStandaloneTransferTransaction(type);

        BlockInfo blockInfo = listener.newBlock().take(1).toFuture().get(TIMEOUT, TIMEOUT_UNIT);

        assertTrue(blockInfo.getHeight().intValue() > 0);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnConfirmedTransactionAddressSignerViaListener(
        RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        Transaction transaction =
            listener.confirmed(this.account.getAddress()).take(1).toFuture()
                .get(TIMEOUT, TIMEOUT_UNIT);
        assertEquals(
            signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnConfirmedTransactionAddressRecipientViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();
        Address recipient = this.getRecipient();
        Observable<Transaction> recipientConfirmedListener = listener.confirmed(recipient);

        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        Transaction transaction =
            recipientConfirmedListener.take(1).toFuture().get(TIMEOUT, TIMEOUT_UNIT);
        assertEquals(
            signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnUnconfirmedAddedTransactionViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        Transaction transaction =
            listener.unconfirmedAdded(this.account.getAddress()).take(1).toFuture()
                .get(TIMEOUT, TIMEOUT_UNIT);
        assertEquals(
            signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    @Disabled
    void shouldReturnUnconfirmedRemovedTransactionViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        String transactionHash =
            listener.unconfirmedRemoved(this.account.getAddress()).take(1).toFuture()
                .get(TIMEOUT, TIMEOUT_UNIT);
        assertEquals(signedTransaction.getHash(), transactionHash);
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnAggregateBondedAddedTransactionViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction = this.announceAggregateBondedTransaction(type);

        AggregateTransaction aggregateTransaction =
            listener.aggregateBondedAdded(this.account.getAddress()).take(1).toFuture()
                .get(TIMEOUT, TIMEOUT_UNIT);
        assertEquals(
            signedTransaction.getHash(), aggregateTransaction.getTransactionInfo().get().getHash());
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnAggregateBondedRemovedTransactionViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction = this.announceAggregateBondedTransaction(type);

        String transactionHash =
            listener.aggregateBondedRemoved(this.account.getAddress()).take(1).toFuture()
                .get(TIMEOUT, TIMEOUT_UNIT);
        assertEquals(signedTransaction.getHash(), transactionHash);
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnCosignatureAddedViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction = this.announceAggregateBondedTransaction(type);

        AggregateTransaction announcedTransaction =
            listener
                .aggregateBondedAdded(this.cosignatoryAccount.getAddress())
                .take(1).toFuture().get(TIMEOUT, TIMEOUT_UNIT);

        assertEquals(
            signedTransaction.getHash(), announcedTransaction.getTransactionInfo().get().getHash());

        List<AggregateTransaction> transactions =
            getAccountRepository(type)
                .aggregateBondedTransactions(this.cosignatoryAccount.getPublicAccount())
                .toFuture().get(TIMEOUT, TIMEOUT_UNIT);

        AggregateTransaction transactionToCosign = transactions.get(0);

        this.announceCosignatureTransaction(transactionToCosign, type);

        CosignatureSignedTransaction cosignatureSignedTransaction =
            listener.cosignatureAdded(this.cosignatoryAccount.getAddress()).take(1).toFuture()
                .get(TIMEOUT, TIMEOUT_UNIT);

        assertEquals(cosignatureSignedTransaction.getSigner(),
            this.cosignatoryAccount2.getPublicKey());
    }

    private AccountRepository getAccountRepository(RepositoryType type) {
        return getRepositoryFactory(type).createAccountRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnTransactionStatusGivenAddedViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction =
            this.announceStandaloneTransferTransactionWithInsufficientBalance(type);

        TransactionStatusError transactionHash =
            listener.status(this.account.getAddress()).take(1).toFuture()
                .get(TIMEOUT, TIMEOUT_UNIT);
        assertEquals(signedTransaction.getHash(), transactionHash.getHash());
    }

    private SignedTransaction announceStandaloneTransferTransaction(RepositoryType type) {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                this.getRecipient(),
                Arrays.asList(),
                PlainMessage.create("test-message")
            ).build();

        SignedTransaction signedTransaction = this.account
            .sign(transferTransaction, getGenerationHash());
        get(getTransactionRepository(type).announce(signedTransaction));
        return signedTransaction;
    }

    private TransactionRepository getTransactionRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createTransactionRepository();
    }

    private SignedTransaction announceStandaloneTransferTransactionWithInsufficientBalance(
        RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(NetworkType.MIJIN_TEST,
                new Address("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", NetworkType.MIJIN_TEST),
                Collections.singletonList(
                    NetworkCurrencyMosaic.createRelative(new BigInteger("100000000000"))),
                PlainMessage.create("test-message")
            ).build();

        SignedTransaction signedTransaction = this.account
            .sign(transferTransaction, getGenerationHash());
        getTransactionRepository(type).announce(signedTransaction).toFuture()
            .get(TIMEOUT, TIMEOUT_UNIT);
        return signedTransaction;
    }

    private SignedTransaction announceAggregateBondedTransaction(
        RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(NetworkType.MIJIN_TEST,
                new Address("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", NetworkType.MIJIN_TEST),
                Arrays.asList(),
                PlainMessage.create("test-message")
            ).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                NetworkType.MIJIN_TEST,
                Collections.singletonList(
                    transferTransaction.toAggregate(this.multisigAccount.getPublicAccount())))
                .build();

        SignedTransaction signedTransaction =
            this.cosignatoryAccount.sign(aggregateTransaction, getGenerationHash());

        getTransactionRepository(type).announceAggregateBonded(signedTransaction).toFuture()
            .get(TIMEOUT, TIMEOUT_UNIT);

        return signedTransaction;
    }

    private CosignatureSignedTransaction announceCosignatureTransaction(
        AggregateTransaction transactionToCosign,
        RepositoryType type) throws ExecutionException, InterruptedException, TimeoutException {
        CosignatureTransaction cosignatureTransaction = new CosignatureTransaction(
            transactionToCosign);

        CosignatureSignedTransaction cosignatureSignedTransaction =
            this.cosignatoryAccount2.signCosignatureTransaction(cosignatureTransaction);

        getTransactionRepository(type)
            .announceAggregateBondedCosignature(cosignatureSignedTransaction)
            .toFuture().get(TIMEOUT, TIMEOUT_UNIT);

        return cosignatureSignedTransaction;
    }
}
