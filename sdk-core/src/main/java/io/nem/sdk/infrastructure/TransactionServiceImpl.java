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
 * Implementation of {@link TransactionService}.
 */
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository transactionRepository;

    private final Listener listener;

    public TransactionServiceImpl(RepositoryFactory repositoryFactory,
        Listener listener) {
        this.transactionRepository = repositoryFactory.createTransactionRepository();
        this.listener = listener;
    }

    @Override
    public Observable<Transaction> announce(SignedTransaction signedTransaction) {
        Validate.notNull(signedTransaction, "signedTransaction is required");
        Observable<TransactionAnnounceResponse> announce = transactionRepository
            .announce(signedTransaction);
        return announce.flatMap(
            r -> listener.confirmed(signedTransaction.getSigner(), signedTransaction.getHash()));
    }

    @Override
    public Observable<AggregateTransaction> announceAggregateBonded(
        SignedTransaction signedAggregateTransaction) {
        Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
        Validate.isTrue(signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
            "signedAggregateTransaction type must be AGGREGATE_BONDED");
        Observable<TransactionAnnounceResponse> announce = transactionRepository
            .announceAggregateBonded(signedAggregateTransaction);
        return announce.flatMap(
            r -> listener.aggregateBondedAdded(signedAggregateTransaction.getSigner(),
                signedAggregateTransaction.getHash()));
    }

    @Override
    public Observable<AggregateTransaction> announceHashLockAggregateBonded(
        SignedTransaction signedHashLockTransaction, SignedTransaction signedAggregateTransaction) {
        Validate.notNull(signedHashLockTransaction, "signedHashLockTransaction is required");
        Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
        Validate.isTrue(signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
            "signedAggregateTransaction type must be AGGREGATE_BONDED");
        Validate.isTrue(signedHashLockTransaction.getType() == TransactionType.LOCK,
            "signedHashLockTransaction type must be LOCK");
        return announce(signedHashLockTransaction)
            .flatMap(t -> announceAggregateBonded(signedAggregateTransaction));
    }

}
