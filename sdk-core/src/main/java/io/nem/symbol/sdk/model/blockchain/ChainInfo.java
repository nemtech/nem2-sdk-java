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
package io.nem.symbol.sdk.model.blockchain;

import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

public class ChainInfo {

  private final BigInteger height;
  private final BigInteger scoreLow;
  private final BigInteger scoreHigh;
  private final FinalizedBlock latestFinalizedBlock;

  public ChainInfo(
      BigInteger height,
      BigInteger scoreLow,
      BigInteger scoreHigh,
      FinalizedBlock latestFinalizedBlock) {
    Validate.notNull(height, "height must not be null");
    Validate.notNull(scoreLow, "scoreLow must not be null");
    Validate.notNull(scoreHigh, "scoreHigh must not be null");
    Validate.notNull(latestFinalizedBlock, "latestFinalizedBlock must not be null");
    this.height = height;
    this.scoreLow = scoreLow;
    this.scoreHigh = scoreHigh;
    this.latestFinalizedBlock = latestFinalizedBlock;
  }

  /**
   * Get scoreLow
   *
   * @return BigInteger
   */
  public BigInteger getScoreLow() {
    return this.scoreLow;
  }

  /**
   * Get scoreHigh
   *
   * @return BigInteger
   */
  public BigInteger getScoreHigh() {
    return this.scoreHigh;
  }

  /**
   * Get the chain height
   *
   * @return BigInteger
   */
  public BigInteger getHeight() {
    return height;
  }

  /**
   * Get the finalized block information
   *
   * @return the Finalized Block information.
   */
  public FinalizedBlock getLatestFinalizedBlock() {
    return latestFinalizedBlock;
  }
}
