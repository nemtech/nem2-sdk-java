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

import io.nem.symbol.core.crypto.Hasher;
import io.nem.symbol.core.crypto.Hashes;
import java.util.Arrays;

/**
 * Enum containing hash type.
 *
 * @since 1.0
 */
public enum SecretHashAlgorithm implements Hasher {

  /** hashed using SHA3-256 (Catapult Native) */
  SHA3_256(0, Hashes::sha3_256),
  /** hashed twice: first with SHA-256 and then with RIPEMD-160 (BTC Compat) */
  HASH_160(1, Hashes::hash160),
  /** Hashed twice with SHA-256 (BTC Compat) */
  HASH_256(2, Hashes::hash256);

  /** The regex used to validate a hashed value. */
  public static final String VALIDATOR_REGEX = "-?[0-9a-fA-F]+";
  /** The default size used for secret hashes. */
  public static final int DEFAULT_SECRET_BYTE_ARRAY_SIZE = 32;

  /** The default size used for secret hashes. */
  public static final int DEFAULT_SECRET_HEX_SIZE = DEFAULT_SECRET_BYTE_ARRAY_SIZE * 2;

  /** The catbuffer and open api value of this type */
  private final int value;

  /** The {@link Hasher} that should be used when hashing values for this given Algorithm Type */
  private final Hasher delegate;

  SecretHashAlgorithm(int value, Hasher delegate) {
    this.value = value;
    this.delegate = delegate;
  }

  public static SecretHashAlgorithm rawValueOf(int value) {
    return Arrays.stream(values())
        .filter(e -> e.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  /**
   * Validate hash algorithm and hash have desired format
   *
   * @param hashType Hash type
   * @param input Input hashed
   * @return boolean when format is correct
   */
  public static boolean validator(SecretHashAlgorithm hashType, String input) {
    if (!input.matches(VALIDATOR_REGEX)) {
      return false;
    }
    if (hashType == SecretHashAlgorithm.HASH_160) {
      return input.length() == DEFAULT_SECRET_HEX_SIZE || input.length() == 40;
    }
    return input.length() == DEFAULT_SECRET_HEX_SIZE;
  }

  /**
   * Use this method to just hash values when the LockHashAlgorithmType is known. Users don't need
   * to if/switch per algorithm.
   *
   * @param values the values to be hashed
   * @return the hashed value using the algorithm.
   */
  @Override
  public byte[] hash(byte[]... values) {
    return this.delegate.hash(values);
  }

  public int getValue() {
    return value;
  }
}
