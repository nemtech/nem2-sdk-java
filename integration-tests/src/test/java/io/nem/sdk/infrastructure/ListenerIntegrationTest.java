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
import io.nem.sdk.api.Listener;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.api.TransactionService;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.message.PlainMessage;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings("squid:S1607")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
//TODO BROKEN!
class ListenerIntegrationTest extends BaseIntegrationTest {

    private Account account = config().getDefaultAccount();
    private Account multisigAccount = config().getMultisigAccount();
    private Account cosignatoryAccount = config().getCosignatoryAccount();
    private Account cosignatoryAccount2 = config().getCosignatory2Account();


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
    void shouldReturnNewBlockViaListener(RepositoryType type) {
        Listener listener = getListener(type);

        this.announceStandaloneTransferTransaction(type);

        BlockInfo blockInfo = get(listener.newBlock().take(1));

        assertTrue(blockInfo.getHeight().intValue() > 0);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnConfirmedTransactionAddressSignerViaListener(RepositoryType type) {
        Listener listener = getListener(type);

//        listener.unconfirmedAdded(getRecipient()).forEach(t -> System.out.println(toJson(t)));
//        listener.unconfirmedAdded(this.account.getAddress())
//            .forEach(t -> System.out.println(toJson(t)));

        Observable<Transaction> confirmed = listener
            .confirmed(this.account.getAddress());

        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        Transaction transaction = get(confirmed);

        assertEquals(signedTransaction.getHash(),
            transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnConfirmedTransactionAddressRecipientViaListener(RepositoryType type) {
        Listener listener = getListener(type);
        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        Transaction transaction = get(
            listener.confirmed(this.getRecipient(), signedTransaction.getHash()));

        assertEquals(
            signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnUnconfirmedAddedTransactionViaListener(RepositoryType type) {
        Listener listener = getListener(type);

        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        Transaction transaction = get(listener.unconfirmedAdded(this.account.getAddress()));
        assertEquals(
            signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    @Disabled
    void shouldReturnUnconfirmedRemovedTransactionViaListener(RepositoryType type)
        throws ExecutionException, InterruptedException {
        Listener listener = getRepositoryFactory(type).createListener();
        listener.open().get();

        SignedTransaction signedTransaction = this.announceStandaloneTransferTransaction(type);

        String transactionHash =
            get(listener.unconfirmedRemoved(this.account.getAddress()));
        assertEquals(signedTransaction.getHash(), transactionHash);
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnAggregateBondedAddedTransactionViaListener(RepositoryType type) {

        TransactionService transactionService = new TransactionServiceImpl(
            getRepositoryFactory(type));

        SignedTransaction signedTransaction = this.createAggregateBondedTransaction();

        AggregateTransaction aggregateTransaction = get(
            transactionService.announceAggregateBonded(getListener(type), signedTransaction));

        assertEquals(
            signedTransaction.getHash(), aggregateTransaction.getTransactionInfo().get().getHash());
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnAggregateBondedRemovedTransactionViaListener(RepositoryType type) {
        Listener listener = getListener(type);

        SignedTransaction signedTransaction = this.createAggregateBondedTransaction();

        String transactionHash = get(listener.aggregateBondedRemoved(this.account.getAddress()));
        assertEquals(signedTransaction.getHash(), transactionHash);
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnCosignatureAddedViaListener(RepositoryType type) {
        Listener listener = getListener(type);

        SignedTransaction signedTransaction = this.createAggregateBondedTransaction();

        TransactionService transactionService = new TransactionServiceImpl(
            getRepositoryFactory(type));

        AggregateTransaction announcedTransaction = get(
            transactionService.announceAggregateBonded(listener, signedTransaction));

        assertEquals(
            signedTransaction.getHash(), announcedTransaction.getTransactionInfo().get().getHash());

        List<AggregateTransaction> transactions = get(getAccountRepository(type)
            .aggregateBondedTransactions(this.cosignatoryAccount.getPublicAccount()));

        AggregateTransaction transactionToCosign = transactions.get(0);

        this.announceCosignatureTransaction(transactionToCosign, type);

        CosignatureSignedTransaction cosignatureSignedTransaction = get(
            listener.cosignatureAdded(this.cosignatoryAccount.getAddress()));

        assertEquals(cosignatureSignedTransaction.getSigner(),
            this.cosignatoryAccount2.getPublicKey());
    }

    private AccountRepository getAccountRepository(RepositoryType type) {
        return getRepositoryFactory(type).createAccountRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldReturnTransactionStatusGivenAddedViaListener(RepositoryType type) {
        Listener listener = getListener(type);

        SignedTransaction signedTransaction =
            this.announceStandaloneTransferTransactionWithInsufficientBalance(type);

        TransactionStatusError transactionHash = get(listener.status(this.account.getAddress()));
        assertEquals(signedTransaction.getHash(), transactionHash.getHash());
    }

    private SignedTransaction announceStandaloneTransferTransaction(RepositoryType type) {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                this.getRecipient(),
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(10000L))),
                PlainMessage.create("test-message")
            ).maxFee(this.maxFee).build();

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
        RepositoryType type) {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(getNetworkType(),
                new Address("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", getNetworkType()),
                Collections.singletonList(
                    getNetworkCurrency().createRelative(new BigInteger("100000000000"))),
                PlainMessage.create("test-message")
            ).maxFee(this.maxFee).build();

        SignedTransaction signedTransaction = this.account
            .sign(transferTransaction, getGenerationHash());
        get(getTransactionRepository(type).announce(signedTransaction));
        return signedTransaction;
    }

    private SignedTransaction createAggregateBondedTransaction() {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(getNetworkType(),
                new Address("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", getNetworkType()),
                Collections.emptyList(),
                PlainMessage.create("test-message")
            ).maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Collections.singletonList(
                    transferTransaction.toAggregate(this.multisigAccount.getPublicAccount())))
                .maxFee(this.maxFee).build();

        SignedTransaction signedTransaction =
            this.cosignatoryAccount.sign(aggregateTransaction, getGenerationHash());

        return signedTransaction;
    }

    private CosignatureSignedTransaction announceCosignatureTransaction(
        AggregateTransaction transactionToCosign, RepositoryType type) {
        CosignatureTransaction cosignatureTransaction = new CosignatureTransaction(
            transactionToCosign);

        CosignatureSignedTransaction cosignatureSignedTransaction =
            this.cosignatoryAccount2.signCosignatureTransaction(cosignatureTransaction);

        get(getRepositoryFactory(type).createTransactionRepository()
            .announceAggregateBondedCosignature(cosignatureSignedTransaction));

        return cosignatureSignedTransaction;
    }
}
