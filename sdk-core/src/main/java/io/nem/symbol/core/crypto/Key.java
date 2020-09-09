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

import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.commons.lang3.Validate;

/**
 * Represents a generic key with a specific size.
 *
 * <p>This class helps developers to migrate from one format to another (hex, byte array, big
 * integer)
 */
public class Key {

  private final byte[] value;

  /**
   * Creates a new public key.
   *
   * @param bytes The raw key value.
   * @param expectedSize the expected byte array size.
   */
  public Key(final byte[] bytes, int expectedSize) {
    Validate.notNull(bytes, "bytes must not be null");
    Validate.isTrue(
        bytes.length == expectedSize,
        "Bytes Array size " + bytes.length + " is not " + expectedSize);
    this.value = bytes;
  }

  /**
   * Creates a new key from an big integer with leading zeros if necessary.
   *
   * @param value The numeric key value.
   * @param expectedSize the expected byte array size.
   */
  public Key(BigInteger value, int expectedSize) {
    this(ByteUtils.bigIntToByteArrayLeadingZeros(value, expectedSize), expectedSize);
  }

  /**
   * Creates a new key from an hex completing with leading zeros if necessary.
   *
   * @param hex The hey key value.
   * @param expectedSize the expected byte array size.
   */
  public Key(String hex, int expectedSize) {
    this(ByteUtils.byteArrayLeadingZeros(ConvertUtils.getBytes(hex), expectedSize), expectedSize);
  }

  /**
   * Returns the size of the key
   *
   * @return The size of the key.
   */
  public int getSize() {
    return this.value.length;
  }

  /**
   * Creates the a key value.
   *
   * @return The raw public key value.
   */
  public byte[] getBytes() {
    return this.value;
  }

  /**
   * Gets raw public key value.
   *
   * @return The raw public key value.
   */
  public ByteBuffer getByteBuffer() {
    return ByteBuffer.wrap(this.value);
  }

  /**
   * Gets the raw key value as BigInteger.
   *
   * @return The raw key value.
   */
  public BigInteger getRaw() {
    return new BigInteger(this.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Key publicKey = (Key) o;
    return Arrays.equals(value, publicKey.value);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(value);
  }

  /** @return the hex representation of the public key. */
  public String toHex() {
    return ConvertUtils.toHex(this.value).toUpperCase();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + toHex() + '}';
  }
}
