/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.api;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.reactivex.Observable;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

/**
 * Created by fernando on 19/08/19.
 *
 * @author Fernando Boucquez
 */
public interface Listener extends Closeable {


    /**
     * @return a {@link CompletableFuture} that resolves when the websocket connection is opened
     */
    CompletableFuture<Void> open();

    /**
     * Close webSocket connection
     */
    void close();

    /**
     * @return the connection UID.
     */
    String getUid();

    /**
     * Returns an observable stream of BlockInfo. Each time a new Block is added into the
     * blockchain, it emits a new BlockInfo in the event stream.
     *
     * @return an observable stream of BlockInfo
     */
    Observable<BlockInfo> newBlock();

    /**
     * Returns an observable stream of Transaction for a specific address. Each time a transaction
     * is in confirmed state an it involves the address, it emits a new Transaction in the event
     * stream.
     *
     * @param address address we listen when a transaction is in confirmed state
     * @return an observable stream of Transaction with state confirmed
     */
    Observable<Transaction> confirmed(Address address);

    /**
     * Returns an observable stream of the transaction of the given transactionHash. This stream is
     * integrated with the status listener. If an error message for the given transaction hash and
     * signer address occurs while waiting for the confirmed transaction, a {@link
     * io.nem.sdk.model.transaction.TransactionStatusException} with the status error is raised.
     * This will help the caller identify errors faster, unlike the regular confirmed method that
     * will just time out.
     *
     * @param address address we listen when a transaction is in confirmed state
     * @param transactionHash the expected transaction hash
     * @return an observable stream of Transaction with given transaction hash and state confirmed.
     */
    Observable<Transaction> confirmed(Address address, String transactionHash);

    /**
     * Returns an observable stream of Transaction for a specific address. Each time a transaction
     * is in unconfirmed state an it involves the address, it emits a new Transaction in the event
     * stream.
     *
     * @param address address we listen when a transaction is in unconfirmed state
     * @return an observable stream of Transaction with state unconfirmed
     */
    Observable<Transaction> unconfirmedAdded(Address address);

    /**
     * Returns an observable stream of Transaction Hashes for specific address. Each time a
     * transaction with state unconfirmed changes its state, it emits a new message with the
     * transaction hash in the event stream.
     *
     * @param address address we listen when a transaction is removed from unconfirmed state
     * @return an observable stream of Strings with the transaction hash
     */
    Observable<String> unconfirmedRemoved(Address address);

    /**
     * Return an observable of {@link AggregateTransaction} for specific address. Each time an
     * aggregate bonded transaction is announced, it emits a new {@link AggregateTransaction} in the
     * event stream.
     *
     * @param address address we listen when a transaction with missing signatures state
     * @return an observable stream of AggregateTransaction with missing signatures state
     */
    Observable<AggregateTransaction> aggregateBondedAdded(Address address);

    /**
     * Return an observable of {@link AggregateTransaction} for an specific address and transcation
     * hash. Each time an aggregate bonded transaction is announced, it emits a new {@link
     * AggregateTransaction} in the event stream. If an error message for the given transaction hash
     * and signer address occurs while waiting for the confirmed transaction, a {@link
     * io.nem.sdk.model.transaction.TransactionStatusException} with the status error is raised.
     * This will help the caller identify errors faster, unlike the regular confirmed method that
     * will just time out.
     *
     * @param address address we listen when a transaction with missing signatures state.
     * @param transactionHash the expected transaction hash
     * @return an observable stream of AggregateTransaction with missing signatures state
     */
    Observable<AggregateTransaction> aggregateBondedAdded(Address address, String transactionHash);

    /**
     * Returns an observable stream of Transaction Hashes for specific address. Each time an
     * aggregate bonded transaction is announced, it emits a new message with the transaction hash
     * in the event stream.
     *
     * @param address address we listen when a transaction is confirmed or rejected
     * @return an observable stream of Strings with the transaction hash
     */
    Observable<String> aggregateBondedRemoved(Address address);

    /**
     * Returns an observable stream of {@link TransactionStatusError} for specific address. Each
     * time a transaction contains an error, it emits a new message with the transaction status
     * error in the event stream.
     *
     * @param address address we listen to be notified when some error happened
     * @return an observable stream of {@link TransactionStatusError}
     */
    Observable<TransactionStatusError> status(Address address);

    /**
     * Returns an observable stream of {@link CosignatureSignedTransaction} for specific address.
     * Each time a cosigner signs a transaction the address initialized, it emits a new message with
     * the cosignatory signed transaction in the even stream.
     *
     * @param address address we listen when a cosignatory is added to some transaction address
     * sent
     * @return an observable stream of {@link CosignatureSignedTransaction}
     */
    Observable<CosignatureSignedTransaction> cosignatureAdded(Address address);
}
