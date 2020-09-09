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

import java.math.BigInteger;

/**
 * The transaction status contains basic of a transaction announced to the blockchain.
 *
 * @since 1.0
 */
public class TransactionStatus {

  private final TransactionState group;
  private final String code;
  private final String hash;
  private final Deadline deadline;
  private final BigInteger height;

  public TransactionStatus(
      final TransactionState group,
      final String code,
      final String hash,
      final Deadline deadline,
      final BigInteger height) {
    this.group = group;
    this.code = code;
    this.hash = hash;
    this.deadline = deadline;
    this.height = height;
  }

  /**
   * Returns transaction status group "failed", "unconfirmed", "confirmed", etc...
   *
   * @return transaction group name
   */
  public TransactionState getGroup() {
    return group;
  }

  /**
   * Returns transaction status being the error name in case of failure and success otherwise.
   *
   * @return transaction status
   */
  public String getCode() {
    return code;
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
   * Returns transaction deadline.
   *
   * @return transaction deadline
   */
  public Deadline getDeadline() {
    return deadline;
  }

  /**
   * Returns height of the block at which it was confirmed or rejected.
   *
   * @return block height
   */
  public BigInteger getHeight() {
    return height;
  }
}
