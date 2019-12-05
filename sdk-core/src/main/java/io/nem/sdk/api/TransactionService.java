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

package io.nem.sdk.api;

import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.Observable;

/**
 * Utility service that simplifies how transactions are announced and validated.
 */
public interface TransactionService {

    /**
     * This method announces a transaction while waiting for being completed by listing to the
     * /completed web socket. If an error to the given transaction is sent to the /status web
     * socket, a {@link io.nem.sdk.model.transaction.TransactionStatusException} is raised.
     *
     * Steps:
     *
     * 1) It announces the transaction to the {@link TransactionRepository}
     *
     * 2) It calls the {@link Listener}'s completed method waiting for the transaction to be
     * completed.
     *
     * 3) It class the {@link Listener}'s status method waiting for an error to occurred.
     *
     * @param listener the web socket listener used to detect completed transaction or status errors
     * coming from the catapult server.
     * @param signedTransaction the signed transaction to be announced.
     * @return an Observable of the completed transaction or an observable that raises a {@link
     * io.nem.sdk.model.transaction.TransactionStatusException} if the transaction has failed.
     */
    Observable<Transaction> announce(Listener listener,
        SignedTransaction signedTransaction);


    /**
     * This method announces an aggregate bonded transaction while waiting for being added by
     * listing to the /aggregateBondedAdded web socket. If an error to the given transaction is sent
     * to the /status web socket, a {@link io.nem.sdk.model.transaction.TransactionStatusException}
     * is raised.
     *
     * Steps:
     *
     * 1) It announceAggregateBonded the transaction to the {@link TransactionRepository}
     *
     * 2) It calls the {@link Listener}'s aggregateBondedAdded method waiting for the transaction to
     * be completed.
     *
     * 3) It class the {@link Listener}'s status method waiting for an error to occurred.
     *
     * @param listener the web socket listener used to detect aggregateBondedAdded transaction or
     * status errors coming from the catapult server.
     * @param signedAggregateTransaction the signed aggregate bonded transaction to be announced.
     * @return an Observable of the added aggregate bonded transaction or an observable that raises
     * a {@link io.nem.sdk.model.transaction.TransactionStatusException} if the transaction has
     * failed.
     */
    Observable<AggregateTransaction> announceAggregateBonded(
        Listener listener, SignedTransaction signedAggregateTransaction);


    /**
     * This method announces an a hash lock transaction followed by a aggregate bonded transaction
     * while waiting for being completed by listing to the /completed and /aggregateBondedAdded web
     * socket. If an error is sent while processing any of the given transaction a {@link
     * io.nem.sdk.model.transaction.TransactionStatusException} is raised.
     *
     * @param listener the web socket listener used to detect completed, aggregateBondedAdded
     * transaction or status errors coming from the catapult server.
     * @param signedHashLockTransaction the signed hash lock transaction
     * @param signedAggregateTransaction the signed aggregate bonded transaction that will be
     * announced after the signed hash lock transaction is completed
     * @return an Observable of the added aggregate bonded (second) transaction or an observable
     * that raises a {@link io.nem.sdk.model.transaction.TransactionStatusException} if any
     * transaction has failed.
     */
    Observable<AggregateTransaction> announceHashLockAggregateBonded(
        Listener listener, SignedTransaction signedHashLockTransaction,
        SignedTransaction signedAggregateTransaction);
}
