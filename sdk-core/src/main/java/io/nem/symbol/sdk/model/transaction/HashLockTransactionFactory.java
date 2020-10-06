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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/** Factory of {@link HashLockTransaction} */
public class HashLockTransactionFactory extends TransactionFactory<HashLockTransaction> {

  private final Mosaic mosaic;
  private final BigInteger duration;
  private final String hash;

  private HashLockTransactionFactory(
      NetworkType networkType, Deadline deadline, Mosaic mosaic, BigInteger duration, String hash) {
    super(TransactionType.HASH_LOCK, networkType, deadline);
    Validate.notNull(mosaic, "Mosaic must not be null");
    Validate.notNull(duration, "Duration must not be null");
    Validate.notNull(hash, "Hash must not be null");
    ConvertUtils.validateNotNegative(duration);
    this.mosaic = mosaic;
    this.duration = duration;
    this.hash = hash;
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param deadline the deadline.
   * @param mosaic Mosaic.
   * @param duration Duration.
   * @param hash the transaction hash.
   * @return Hash lock transaction.
   */
  public static HashLockTransactionFactory create(
      NetworkType networkType, Deadline deadline, Mosaic mosaic, BigInteger duration, String hash) {
    return new HashLockTransactionFactory(networkType, deadline, mosaic, duration, hash);
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param deadline Deadline
   * @param mosaic Mosaic.
   * @param duration Duration.
   * @param signedTransaction Signed transaction.
   * @return Hash lock transaction.
   */
  public static HashLockTransactionFactory create(
      NetworkType networkType,
      Deadline deadline,
      Mosaic mosaic,
      BigInteger duration,
      SignedTransaction signedTransaction) {
    if (signedTransaction.getType() != TransactionType.AGGREGATE_BONDED) {
      throw new IllegalArgumentException("Signed transaction must be Aggregate Bonded Transaction");
    }
    return new HashLockTransactionFactory(
        networkType, deadline, mosaic, duration, signedTransaction.getHash());
  }

  /**
   * Returns locked mosaic.
   *
   * @return locked mosaic.
   */
  public Mosaic getMosaic() {
    return mosaic;
  }

  /**
   * Returns funds lock duration in number of blocks.
   *
   * @return funds lock duration in number of blocks.
   */
  public BigInteger getDuration() {
    return duration;
  }

  /**
   * Returns signed transaction hash for which funds are locked.
   *
   * @return signed transaction hash for which funds are locked.
   */
  public String getHash() {
    return hash;
  }

  @Override
  public HashLockTransaction build() {
    return new HashLockTransaction(this);
  }
}
