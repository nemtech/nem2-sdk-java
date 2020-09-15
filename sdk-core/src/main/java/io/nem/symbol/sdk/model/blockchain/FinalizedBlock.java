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
import java.util.Objects;
import org.apache.commons.lang3.Validate;

/** Finalized block information. */
public class FinalizedBlock {

  private final Long finalizationEpoch;

  private final Long finalizationPoint;

  private final BigInteger height;

  private final String hash;

  public FinalizedBlock(
      Long finalizationEpoch, Long finalizationPoint, BigInteger height, String hash) {
    Validate.notNull(finalizationEpoch, "finalizationEpoch is required");
    Validate.notNull(finalizationPoint, "finalizationPoint is required");
    Validate.notNull(height, "height is required");
    Validate.notNull(hash, "hash is required");
    this.finalizationEpoch = finalizationEpoch;
    this.finalizationPoint = finalizationPoint;
    this.height = height;
    this.hash = hash;
  }

  public Long getFinalizationEpoch() {
    return finalizationEpoch;
  }

  public Long getFinalizationPoint() {
    return finalizationPoint;
  }

  public BigInteger getHeight() {
    return height;
  }

  public String getHash() {
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FinalizedBlock that = (FinalizedBlock) o;
    return Objects.equals(finalizationEpoch, that.finalizationEpoch)
        && Objects.equals(finalizationPoint, that.finalizationPoint)
        && Objects.equals(height, that.height)
        && Objects.equals(hash, that.hash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(finalizationEpoch, finalizationPoint, height, hash);
  }
}
