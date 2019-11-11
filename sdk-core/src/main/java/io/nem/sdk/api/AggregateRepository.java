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

import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.reactivex.Observable;

/**
 * Aggregate transaction repository used to load information from the aggregate route.
 */
public interface AggregateRepository {


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
