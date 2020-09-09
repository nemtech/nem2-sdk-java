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
package io.nem.symbol.core.crypto;

import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;

/** Represents a public key. */
public class PublicKey extends Key {

  /** The size of Symbol's public keys. */
  public static final int SIZE = 32;

  /**
   * Creates a new key from the byte array.
   *
   * @param bytes The raw public key value.
   */
  public PublicKey(byte[] bytes) {
    super(bytes, SIZE);
  }

  /**
   * Creates a new key from a big int value
   *
   * @param value the value
   */
  public PublicKey(BigInteger value) {
    super(value, SIZE);
  }

  /**
   * Creates a new key from an hex.
   *
   * @param hex the hex.
   */
  public PublicKey(String hex) {
    super(hex, SIZE);
  }

  /**
   * Creates a public key from a hex string.
   *
   * @param hex The hex string.
   * @return The new public key.
   */
  public static PublicKey fromHexString(final String hex) {
    return new PublicKey(hex);
  }

  /**
   * Generates a random public key.
   *
   * @return The new public key.
   */
  public static PublicKey generateRandom() {
    return new PublicKey(RandomUtils.generateRandomBytes(SIZE));
  }
}
