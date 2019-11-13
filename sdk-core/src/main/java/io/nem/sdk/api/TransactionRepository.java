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

package io.nem.sdk.api;

import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionStatus;
import io.reactivex.Observable;
import java.util.List;

/**
 * Transaction interface repository.
 *
 * @since 1.0
 */
public interface TransactionRepository {

    /**
     * Gets a transaction for a given hash.
     *
     * @param transactionHash String
     * @return Observable of {@link Transaction}
     */
    Observable<Transaction> getTransaction(String transactionHash);

    /**
     * Gets an list of transactions for different transaction hashes.
     *
     * @param transactionHashes List of String
     * @return {@link Observable} of {@link Transaction} List
     */
    Observable<List<Transaction>> getTransactions(List<String> transactionHashes);

    /**
     * Gets a transaction status for a transaction hash.
     *
     * @param transactionHash String
     * @return Observable of {@link TransactionStatus}
     */
    Observable<TransactionStatus> getTransactionStatus(String transactionHash);

    /**
     * Gets an list of transaction status for different transaction hashes.
     *
     * @param transactionHashes List of String
     * @return {@link Observable} of {@link TransactionStatus} List
     */
    Observable<List<TransactionStatus>> getTransactionStatuses(List<String> transactionHashes);

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
