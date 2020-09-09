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

public class BlockchainScore {

  private final BigInteger scoreLow;
  private final BigInteger scoreHigh;

  /**
   * Constructor
   *
   * @param scoreLow the score low.
   * @param scoreHigh the score high.
   */
  public BlockchainScore(BigInteger scoreLow, BigInteger scoreHigh) {
    this.scoreHigh = scoreHigh;
    this.scoreLow = scoreLow;
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
}
