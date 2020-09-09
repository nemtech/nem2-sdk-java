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
package io.nem.symbol.sdk.model.account;

import java.math.BigInteger;

/** Account activity bucket. */
public class ActivityBucket {

  /** Start height. */
  private final BigInteger startHeight;

  /** Total fees paid. */
  private final BigInteger totalFeesPaid;

  /** Beneficiary count. */
  private final long beneficiaryCount;

  /** Raw score. */
  private final BigInteger rawScore;

  /**
   * Constructor
   *
   * @param startHeight Total fees paid.
   * @param totalFeesPaid Total fees paid.
   * @param beneficiaryCount Beneficiary count.
   * @param rawScore Raw score.
   */
  public ActivityBucket(
      BigInteger startHeight,
      BigInteger totalFeesPaid,
      long beneficiaryCount,
      BigInteger rawScore) {
    this.startHeight = startHeight;
    this.totalFeesPaid = totalFeesPaid;
    this.beneficiaryCount = beneficiaryCount;
    this.rawScore = rawScore;
  }

  public BigInteger getStartHeight() {
    return startHeight;
  }

  public BigInteger getTotalFeesPaid() {
    return totalFeesPaid;
  }

  public long getBeneficiaryCount() {
    return beneficiaryCount;
  }

  public BigInteger getRawScore() {
    return rawScore;
  }
}
