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
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.reactivex.Observable;

/** Service that provides useful aggregated transactions methods. */
public interface AggregateTransactionService {

  /**
   * Checks if an aggregate complete transaction has all cosignatories attached.
   *
   * @param signedTransaction The signed aggregate transaction (complete) to be verified
   * @return true if the aggregate transaction has all the consignatories attached.
   * @throws IllegalArgumentException if the signedTransaction is null or not related to an
   *     aggregate completed transaction.
   */
  Observable<Boolean> isComplete(SignedTransaction signedTransaction);

  /**
   * Gets total multisig account cosigner count.
   *
   * @param address address multisig account address
   * @return Observable of the total amount of cosigner
   */
  Observable<Integer> getMaxCosignatures(Address address);

  /**
   * Gets max cosignatures allowed per aggregate according to the current network properties.
   *
   * @return Obervable of the total allowed aggregate
   */
  Observable<Integer> getNetworkMaxCosignaturesPerAggregate();
}
