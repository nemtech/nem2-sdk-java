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
package io.nem.symbol.sdk.model.network;
/**
 * Information about the average, median, highest and lower fee multiplier over the last
 * "numBlocksTransactionFeeStats".
 */
public class TransactionFees {

  /** Average fee multiplier over the last "numBlocksTransactionFeeStats". */
  private final Long averageFeeMultiplier;

  /** Median fee multiplier over the last "numBlocksTransactionFeeStats". */
  private final Long medianFeeMultiplier;

  /** Lowest fee multiplier over the last "numBlocksTransactionFeeStats". */
  private final Long lowestFeeMultiplier;

  /** Highest fee multiplier over the last "numBlocksTransactionFeeStats". */
  private final Long highestFeeMultiplier;

  public TransactionFees(
      Long averageFeeMultiplier,
      Long medianFeeMultiplier,
      Long lowestFeeMultiplier,
      Long highestFeeMultiplier) {
    this.averageFeeMultiplier = averageFeeMultiplier;
    this.medianFeeMultiplier = medianFeeMultiplier;
    this.lowestFeeMultiplier = lowestFeeMultiplier;
    this.highestFeeMultiplier = highestFeeMultiplier;
  }

  public Long getAverageFeeMultiplier() {
    return averageFeeMultiplier;
  }

  public Long getMedianFeeMultiplier() {
    return medianFeeMultiplier;
  }

  public Long getLowestFeeMultiplier() {
    return lowestFeeMultiplier;
  }

  public Long getHighestFeeMultiplier() {
    return highestFeeMultiplier;
  }
}
