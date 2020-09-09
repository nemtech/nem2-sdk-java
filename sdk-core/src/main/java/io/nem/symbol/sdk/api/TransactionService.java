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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionStatusException;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.reactivex.Observable;
import java.util.List;

/** Utility service that simplifies how transactions are announced and validated. */
public interface TransactionService {

  /**
   * This method announces a transaction while waiting for being completed by listing to the
   * /completed web socket. If an error to the given transaction is sent to the /status web socket,
   * a {@link TransactionStatusException} is raised.
   *
   * <p>Steps:
   *
   * <p>1) It announces the transaction to the {@link TransactionRepository}
   *
   * <p>2) It calls the {@link Listener}'s completed method waiting for the transaction to be
   * completed.
   *
   * <p>3) It class the {@link Listener}'s status method waiting for an error to occurred.
   *
   * @param listener the web socket listener used to detect completed transaction or status errors
   *     coming from the catapult server.
   * @param signedTransaction the signed transaction to be announced.
   * @return an Observable of the completed transaction or an observable that raises a {@link
   *     TransactionStatusException} if the transaction has failed.
   */
  Observable<Transaction> announce(Listener listener, SignedTransaction signedTransaction);

  /**
   * This method announces an aggregate bonded transaction while waiting for being added by listing
   * to the /aggregateBondedAdded web socket. If an error to the given transaction is sent to the
   * /status web socket, a {@link TransactionStatusException} is raised.
   *
   * <p>Steps:
   *
   * <p>1) It announceAggregateBonded the transaction to the {@link TransactionRepository}
   *
   * <p>2) It calls the {@link Listener}'s aggregateBondedAdded method waiting for the transaction
   * to be completed.
   *
   * <p>3) It class the {@link Listener}'s status method waiting for an error to occurred.
   *
   * @param listener the web socket listener used to detect aggregateBondedAdded transaction or
   *     status errors coming from the catapult server.
   * @param signedAggregateTransaction the signed aggregate bonded transaction to be announced.
   * @return an Observable of the added aggregate bonded transaction or an observable that raises a
   *     {@link TransactionStatusException} if the transaction has failed.
   */
  Observable<AggregateTransaction> announceAggregateBonded(
      Listener listener, SignedTransaction signedAggregateTransaction);

  /**
   * This method announces an a hash lock transaction followed by a aggregate bonded transaction
   * while waiting for being completed by listing to the /completed and /aggregateBondedAdded web
   * socket. If an error is sent while processing any of the given transaction a {@link
   * TransactionStatusException} is raised.
   *
   * @param listener the web socket listener used to detect completed, aggregateBondedAdded
   *     transaction or status errors coming from the catapult server.
   * @param signedHashLockTransaction the signed hash lock transaction
   * @param signedAggregateTransaction the signed aggregate bonded transaction that will be
   *     announced after the signed hash lock transaction is completed
   * @return an Observable of the added aggregate bonded (second) transaction or an observable that
   *     raises a {@link TransactionStatusException} if any transaction has failed.
   */
  Observable<AggregateTransaction> announceHashLockAggregateBonded(
      Listener listener,
      SignedTransaction signedHashLockTransaction,
      SignedTransaction signedAggregateTransaction);

  /**
   * This method loads all transactions with the given hashes from the catapult server where all the
   * aliases have been resolved.
   *
   * <p>For example, if a {@link TransferTransaction}'s recipient was 'customerone' {@link
   * NamespaceId}, this method will return the same {@link TransferTransaction} where the recipient
   * is the real {@link Address}.
   *
   * <p>Similarly, if {@link MosaicMetadataTransaction} was announced using the {@link NamespaceId}
   * alias of the mosaic, the returned {@link MosaicMetadataTransaction} will reference the real
   * {@link MosaicId}.
   *
   * <p>All the {@link UnresolvedMosaicId} and {@link UnresolvedAddress} in the returned transaction
   * should be instances of {@link MosaicId} and {@link Address}
   *
   * @param transactionHashes the transaction hashes of all the transaction that you want to load
   *     and resolve.
   * @return an {@link Observable} list of {@link Transaction} where all the aliases have been
   *     resolved.
   */
  Observable<List<Transaction>> resolveAliases(List<String> transactionHashes);
}
