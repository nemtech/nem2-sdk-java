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
package io.nem.symbol.sdk.model.mosaic;

import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;
import java.util.Objects;

/** Mosaic nonce class */
public class MosaicNonce {

  /** Mosaic nonce */
  private final int nonce;

  /**
   * Create MosaicNonce from int
   *
   * @param nonce the nonce as byte array.
   */
  public MosaicNonce(int nonce) {
    this.nonce = nonce;
  }
  /**
   * Create a random MosaicNonce
   *
   * @return MosaicNonce nonce
   */
  public static MosaicNonce createRandom() {
    return new MosaicNonce(RandomUtils.generateRandomInt());
  }

  /**
   * Create a MosaicNonce from a BigInteger.
   *
   * @param number the nonce as number.
   * @return MosaicNonce
   */
  public static MosaicNonce createFromBigInteger(BigInteger number) {
    return new MosaicNonce(number.intValue());
  }

  /**
   * Create a MosaicNonce from a Integer.
   *
   * @param number the nonce as number.
   * @return MosaicNonce
   */
  public static MosaicNonce createFromInteger(Integer number) {
    return new MosaicNonce(number);
  }

  /** @return nonce int */
  public int getNonceAsInt() {
    return this.nonce;
  }

  /** @return nonce long */
  public long getNonceAsLong() {
    return Integer.toUnsignedLong(getNonceAsInt());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MosaicNonce that = (MosaicNonce) o;
    return nonce == that.nonce;
  }

  @Override
  public int hashCode() {
    return Objects.hash(nonce);
  }
}
