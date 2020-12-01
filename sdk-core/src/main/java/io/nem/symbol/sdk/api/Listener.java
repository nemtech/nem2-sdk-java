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
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import io.nem.symbol.sdk.model.transaction.TransactionStatusException;
import io.reactivex.Observable;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

/**
 * Created by fernando on 19/08/19.
 *
 * @author Fernando Boucquez
 */
public interface Listener extends Closeable {

  /** @return a {@link CompletableFuture} that resolves when the websocket connection is opened */
  CompletableFuture<Void> open();

  /** Close webSocket connection */
  void close();

  /** @return the connection UID. */
  String getUid();

  /**
   * Returns an observable stream of BlockInfo. Each time a new Block is added into the blockchain,
   * it emits a new BlockInfo in the event stream.
   *
   * @return an observable stream of BlockInfo
   */
  Observable<BlockInfo> newBlock();

  /**
   * Returns an observable stream of FinalizedBlock. Each time a new block is finalized in the
   * blockchain, it emits a new FinalizedBlock in the event stream.
   *
   * @return an observable stream of FinalizedBlock
   */
  Observable<FinalizedBlock> finalizedBlock();

  /**
   * Returns an observable stream of Transaction for a specific unresolvedAddress. Each time a
   * transaction is in confirmed state an it involves the unresolvedAddress, it emits a new
   * Transaction in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is in confirmed state
   * @return an observable stream of Transaction with state confirmed
   */
  default Observable<Transaction> confirmed(UnresolvedAddress unresolvedAddress) {
    return this.confirmed(unresolvedAddress, null);
  }

  /**
   * Returns an observable stream of the transaction for the given unresolvedAddress and
   * transactionHash.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is in confirmed state
   * @param transactionHash the expected transaction hash
   * @return an observable stream of Transaction with given transaction hash and state confirmed.
   */
  Observable<Transaction> confirmed(UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Returns an observable stream of the transaction of the given transactionHash. This stream is
   * integrated with the status listener. If an error message for the given transaction hash and
   * signer unresolvedAddress occurs while waiting for the confirmed transaction, a {@link
   * TransactionStatusException} with the status error is raised. This will help the caller identify
   * errors faster, unlike the regular confirmed method that will just time out.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is in confirmed state
   * @param transactionHash the expected transaction hash
   * @return an observable stream of Transaction with given transaction hash and state confirmed.
   */
  Observable<Transaction> confirmedOrError(
      UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Returns an observable stream of Transaction for a specific unresolvedAddress. Each time a
   * transaction is in unconfirmed state an it involves the unresolvedAddress, it emits a new
   * Transaction in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is in unconfirmed state
   * @return an observable stream of Transaction with state unconfirmed
   */
  default Observable<Transaction> unconfirmedAdded(UnresolvedAddress unresolvedAddress) {
    return this.unconfirmedAdded(unresolvedAddress, null);
  }

  /**
   * Returns an observable stream of Transaction for a specific unresolvedAddress. Each time a
   * transaction is in unconfirmed state an it involves the unresolvedAddress, it emits a new
   * Transaction in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is in unconfirmed state
   * @param transactionHash the expected transaction hash
   * @return an observable stream of Transaction with state unconfirmed
   */
  Observable<Transaction> unconfirmedAdded(
      UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Returns an observable stream of Transaction Hashes for specific unresolvedAddress. Each time a
   * transaction with state unconfirmed changes its state, it emits a new message with the
   * transaction hash in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is removed from
   *     unconfirmed state
   * @return an observable stream of Strings with the transaction hash
   */
  default Observable<String> unconfirmedRemoved(UnresolvedAddress unresolvedAddress) {
    return this.unconfirmedRemoved(unresolvedAddress, null);
  }

  /**
   * Returns an observable stream of Transaction Hashes for specific unresolvedAddress. Each time a
   * transaction with state unconfirmed changes its state, it emits a new message with the
   * transaction hash in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is removed from
   *     unconfirmed state
   * @param transactionHash the expected transaction hash
   * @return an observable stream of Strings with the transaction hash
   */
  Observable<String> unconfirmedRemoved(
      UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Return an observable of {@link AggregateTransaction} for specific unresolvedAddress. Each time
   * an aggregate bonded transaction is announced, it emits a new {@link AggregateTransaction} in
   * the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction with missing signatures
   *     state
   * @return an observable stream of AggregateTransaction with missing signatures state
   */
  default Observable<AggregateTransaction> aggregateBondedAdded(
      UnresolvedAddress unresolvedAddress) {
    return this.aggregateBondedAdded(unresolvedAddress, null);
  }

  /**
   * Return an observable of {@link AggregateTransaction} for an specific unresolvedAddress and
   * transcation hash. Each time an aggregate bonded transaction is announced, it emits a new {@link
   * AggregateTransaction} in the event stream. If an error message for the given transaction hash
   * and signer unresolvedAddress occurs while waiting for the confirmed transaction, a {@link
   * TransactionStatusException} with the status error is raised. This will help the caller identify
   * errors faster, unlike the regular confirmed method that will just time out.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction with missing signatures
   *     state.
   * @param transactionHash the expected transaction hash
   * @return an observable stream of AggregateTransaction with missing signatures state
   */
  Observable<AggregateTransaction> aggregateBondedAddedOrError(
      UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Return an observable of {@link AggregateTransaction} for specific unresolvedAddress and hash.
   * Each time an aggregate bonded transaction is announced, it emits a new {@link
   * AggregateTransaction} in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction with missing signatures
   *     state
   * @param transactionHash the expected transaction hash
   * @return an observable stream of AggregateTransaction with missing signatures state
   */
  Observable<AggregateTransaction> aggregateBondedAdded(
      UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Returns an observable stream of Transaction Hashes for specific unresolvedAddress. Each time an
   * aggregate bonded transaction is announced, it emits a new message with the transaction hash in
   * the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is confirmed or
   *     rejected
   * @return an observable stream of Strings with the transaction hash
   */
  default Observable<String> aggregateBondedRemoved(UnresolvedAddress unresolvedAddress) {
    return this.aggregateBondedRemoved(unresolvedAddress, null);
  }

  /**
   * Returns an observable stream of of the hash for specific unresolvedAddress. Each time an
   * aggregate bonded transaction is announced, it emits a new message with the transaction hash in
   * the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a transaction is confirmed or
   *     rejected
   * @param transactionHash the expected transaction hash (optional)
   * @return an observable stream of Strings with the transaction hash
   */
  Observable<String> aggregateBondedRemoved(
      UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Returns an observable stream of {@link TransactionStatusError} for specific unresolvedAddress.
   * Each time a transaction contains an error, it emits a new message with the transaction status
   * error in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen to be notified when some error happened
   * @return an observable stream of {@link TransactionStatusError}
   */
  default Observable<TransactionStatusError> status(UnresolvedAddress unresolvedAddress) {
    return this.status(unresolvedAddress, null);
  }

  /**
   * Returns an observable stream of {@link TransactionStatusError} for specific unresolvedAddress
   * and hash. Each time a transaction contains an error, it emits a new message with the
   * transaction status error in the event stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen to be notified when some error happened
   * @param transactionHash filter by transaction hash (optional)
   * @return an observable stream of {@link TransactionStatusError}
   */
  Observable<TransactionStatusError> status(
      UnresolvedAddress unresolvedAddress, String transactionHash);

  /**
   * Returns an observable stream of {@link CosignatureSignedTransaction} for specific
   * unresolvedAddress. Each time a cosigner signs a transaction the unresolvedAddress initialized,
   * it emits a new message with the cosignatory signed transaction in the even stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a cosignatory is added to some
   *     transaction unresolvedAddress sent
   * @return an observable stream of {@link CosignatureSignedTransaction}
   */
  default Observable<CosignatureSignedTransaction> cosignatureAdded(
      UnresolvedAddress unresolvedAddress) {
    return this.cosignatureAdded(unresolvedAddress, null);
  }

  /**
   * Returns an observable stream of {@link CosignatureSignedTransaction} for specific
   * unresolvedAddress. Each time a cosigner signs a transaction the unresolvedAddress initialized,
   * it emits a new message with the cosignatory signed transaction in the even stream.
   *
   * @param unresolvedAddress unresolvedAddress we listen when a cosignatory is added to some
   *     transaction unresolvedAddress sent
   * @param parentTransactionHash filter by parent transaction hash (optional)
   * @return an observable stream of {@link CosignatureSignedTransaction}
   */
  Observable<CosignatureSignedTransaction> cosignatureAdded(
      UnresolvedAddress unresolvedAddress, String parentTransactionHash);
}
