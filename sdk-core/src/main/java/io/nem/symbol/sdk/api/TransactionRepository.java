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

import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.reactivex.Observable;
import java.util.List;

/**
 * Transaction interface repository.
 *
 * @since 1.0
 */
public interface TransactionRepository extends Searcher<Transaction, TransactionSearchCriteria> {

  /**
   * Gets a transaction for a given hash.
   *
   * @param group the group the transaction belongs.
   * @param transactionHash String
   * @return Observable of {@link Transaction}
   */
  Observable<Transaction> getTransaction(TransactionGroup group, String transactionHash);

  /**
   * Gets an list of transactions for different transaction hashes.
   *
   * @param group the group the transaction belongs.
   * @param transactionHashes List of String
   * @return {@link Observable} of {@link Transaction} List
   */
  Observable<List<Transaction>> getTransactions(
      TransactionGroup group, List<String> transactionHashes);

  /**
   * Send a signed transaction.
   *
   * @param signedTransaction SignedTransaction
   * @return Observable of TransactionAnnounceResponse
   */
  Observable<TransactionAnnounceResponse> announce(SignedTransaction signedTransaction);

  /**
   * Send a signed transaction with missing signatures.
   *
   * @param signedTransaction SignedTransaction
   * @return Observable of TransactionAnnounceResponse
   */
  Observable<TransactionAnnounceResponse> announceAggregateBonded(
      SignedTransaction signedTransaction);

  /**
   * Send a cosignature signed transaction of an already announced transaction.
   *
   * @param cosignatureSignedTransaction CosignatureSignedTransaction
   * @return Observable of TransactionAnnounceResponse
   */
  Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(
      CosignatureSignedTransaction cosignatureSignedTransaction);
}
