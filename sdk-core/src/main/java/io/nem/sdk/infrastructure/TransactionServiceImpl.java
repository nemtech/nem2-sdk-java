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
import io.nem.sdk.api.TransactionService;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionType;
import io.reactivex.Observable;
import org.apache.commons.lang3.Validate;

/**
 * Implementation of {@link TransactionService}. It  uses the repository interfaces. It works for
 * the different low level implementations like Vertx and Okhttp.
 */
public class TransactionServiceImpl implements TransactionService {


    /**
     * The @{@link TransactionRepository} used to query and announce the different transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * The constructor
     *
     * @param repositoryFactory the {@link RepositoryFactory} with the catapult server connection.
     */
    public TransactionServiceImpl(RepositoryFactory repositoryFactory) {
        this.transactionRepository = repositoryFactory.createTransactionRepository();
    }

    @Override
    public Observable<Transaction> announce(Listener listener,
        SignedTransaction signedTransaction) {
        Validate.notNull(signedTransaction, "signedTransaction is required");
        Observable<TransactionAnnounceResponse> announce = transactionRepository
            .announce(signedTransaction);
        return announce.flatMap(
            r -> listener.confirmed(signedTransaction.getSigner().getAddress(),
                signedTransaction.getHash()));
    }

    @Override
    public Observable<AggregateTransaction> announceAggregateBonded(
        Listener listener, SignedTransaction signedAggregateTransaction) {
        Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
        Validate.isTrue(signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
            "signedAggregateTransaction type must be AGGREGATE_BONDED");
        Observable<TransactionAnnounceResponse> announce = transactionRepository
            .announceAggregateBonded(signedAggregateTransaction);
        return announce.flatMap(
            r -> listener.aggregateBondedAdded(signedAggregateTransaction.getSigner().getAddress(),
                signedAggregateTransaction.getHash()));
    }

    @Override
    public Observable<AggregateTransaction> announceHashLockAggregateBonded(
        Listener listener, SignedTransaction signedHashLockTransaction,
        SignedTransaction signedAggregateTransaction) {
        Validate.notNull(signedHashLockTransaction, "signedHashLockTransaction is required");
        Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
        Validate.isTrue(signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
            "signedAggregateTransaction type must be AGGREGATE_BONDED");
        Validate.isTrue(signedHashLockTransaction.getType() == TransactionType.LOCK,
            "signedHashLockTransaction type must be LOCK");
        return announce(listener, signedHashLockTransaction)
            .flatMap(t -> announceAggregateBonded(listener, signedAggregateTransaction));
    }

}
