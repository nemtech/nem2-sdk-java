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
package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.UnresolvedAddress;

/** The transaction status error model returned by listeners. */
public class TransactionStatusError {

  private final UnresolvedAddress address;
  private final String hash;
  private final String status;
  private final Deadline deadline;

  public TransactionStatusError(
      UnresolvedAddress address, String hash, String status, Deadline deadline) {
    this.address = address;
    this.hash = hash;
    this.status = status;
    this.deadline = deadline;
  }

  /** @return the address that fires the transaction status error. */
  public UnresolvedAddress getAddress() {
    return address;
  }

  /**
   * Returns transaction hash.
   *
   * @return transaction hash
   */
  public String getHash() {
    return hash;
  }

  /**
   * Returns transaction status error when transaction fails.
   *
   * @return transaction status error
   */
  public String getStatus() {
    return status;
  }

  /**
   * Returns transaction deadline.
   *
   * @return transaction deadline
   */
  public Deadline getDeadline() {
    return deadline;
  }
}
