/*
 * Copyright 2019 NEM
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

import io.nem.sdk.api.Listener;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.FakeDeadline;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests of {@link io.nem.sdk.api.TransactionService}.
 */
public class TransactionServiceTest {


    private NetworkType networkType = NetworkType.MIJIN_TEST;
    private TransactionServiceImpl service;
    private TransactionRepository transactionRepositoryMock;
    private Account account;
    private Listener listener;


    @BeforeEach
    void setup() {
        account = Account.generateNewAccount(networkType);

        RepositoryFactory factory = Mockito.mock(RepositoryFactory.class);
        transactionRepositoryMock = Mockito.mock(TransactionRepository.class);
        Mockito.when(factory.createTransactionRepository()).thenReturn(transactionRepositoryMock);

        listener = Mockito.mock(Listener.class);
        service = new TransactionServiceImpl(factory);
    }

    @Test
    void announce() throws ExecutionException, InterruptedException {

        TransferTransaction transferTransaction = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST,
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
                    networkType),
                Collections.emptyList(),
                PlainMessage.Empty
            ).build();

        SignedTransaction signedTransaction = transferTransaction.signWith(account, "abc");
        TransactionAnnounceResponse transactionAnnounceResponse = new TransactionAnnounceResponse(
            "Some Message");

        Mockito.when(transactionRepositoryMock.announce(Mockito.eq(signedTransaction))).thenReturn(
            Observable.just(transactionAnnounceResponse));

        Mockito
            .when(listener.confirmed(Mockito.eq(account.getAddress()),
                Mockito.eq(signedTransaction.getHash())))
            .thenReturn(Observable.just(transferTransaction));

        Observable<Transaction> announcedTransaction = service
            .announce(listener, signedTransaction);

        Assertions.assertEquals(transferTransaction, announcedTransaction.toFuture().get());

    }

    @Test
    void announceAggregateBonded() throws ExecutionException, InterruptedException {

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_BONDED, networkType, Collections.emptyList(),
                Collections.emptyList()).deadline(new FakeDeadline()).build();

        String generationHash = "abc";
        SignedTransaction aggregateSignedTransaction = aggregateTransaction
            .signWith(account, generationHash);

        TransactionAnnounceResponse aggregateTransactionAnnounceResponse = new TransactionAnnounceResponse(
            "Aggregate Some Message");

        Mockito.when(transactionRepositoryMock
            .announceAggregateBonded(Mockito.eq(aggregateSignedTransaction))).thenReturn(
            Observable.just(aggregateTransactionAnnounceResponse));

        Mockito
            .when(listener.aggregateBondedAdded(Mockito.eq(account.getAddress()),
                Mockito.eq(aggregateSignedTransaction.getHash())))
            .thenReturn(Observable.just(aggregateTransaction));

        Observable<AggregateTransaction> announcedTransaction = service
            .announceAggregateBonded(listener, aggregateSignedTransaction);

        Assertions.assertEquals(aggregateTransaction, announcedTransaction.toFuture().get());

    }

    @Test
    void announceHashLockAggregateBonded() throws ExecutionException, InterruptedException {

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_BONDED, networkType, Collections.emptyList(),
                Collections.emptyList()).deadline(new FakeDeadline()).build();

        String generationHash = "abc";
        SignedTransaction aggregateSignedTransaction = aggregateTransaction
            .signWith(account, generationHash);

        HashLockTransaction hashLockTransaction =
            HashLockTransactionFactory.create(
                networkType,
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                aggregateSignedTransaction.getHash())
                .build();

        SignedTransaction hashLockSignedTranscation = hashLockTransaction
            .signWith(account, generationHash);

        TransactionAnnounceResponse aggregateTransactionAnnounceResponse = new TransactionAnnounceResponse(
            "Aggregate Some Message");

        TransactionAnnounceResponse hashTransactionAnnounceResponse = new TransactionAnnounceResponse(
            "Hash Some Message");

        Mockito.when(transactionRepositoryMock
            .announceAggregateBonded(Mockito.eq(aggregateSignedTransaction))).thenReturn(
            Observable.just(aggregateTransactionAnnounceResponse));

        Mockito.when(transactionRepositoryMock.announce(Mockito.eq(hashLockSignedTranscation)))
            .thenReturn(
                Observable.just(hashTransactionAnnounceResponse));

        Mockito
            .when(listener.confirmed(Mockito.eq(account.getAddress()),
                Mockito.eq(hashLockSignedTranscation.getHash())))
            .thenReturn(Observable.just(hashLockTransaction));

        Mockito
            .when(listener.aggregateBondedAdded(Mockito.eq(account.getAddress()),
                Mockito.eq(aggregateSignedTransaction.getHash())))
            .thenReturn(Observable.just(aggregateTransaction));

        Observable<AggregateTransaction> announcedTransaction = service
            .announceHashLockAggregateBonded(listener,
                hashLockSignedTranscation, aggregateSignedTransaction);

        Assertions.assertEquals(aggregateTransaction, announcedTransaction.toFuture().get());

    }

}
