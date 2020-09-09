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
package io.nem.symbol.core.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {

  /** Private constructor for this utility class. */
  private ByteUtils() {}

  /**
   * Converts a BigInteger value into an array of size bytes.
   *
   * @param x The BigInteger.
   * @param size The number of bytes in the returned byte array.
   * @return The bytes.
   */
  public static byte[] bigIntToBytesOfSize(final BigInteger x, final int size) {
    byte[] bytes = x.toByteArray();
    int maxSize = bytes.length;
    return Arrays.copyOfRange(
        bytes, (maxSize <= size) ? 0 : maxSize - size, Math.max(maxSize, size));
  }

  /**
   * Converts a BigInteger to a byte array of the given size leading with zeros.
   *
   * @param value The value to convert.
   * @param numBytes The number of bytes in the destination array.
   * @return a byte array of size numBytes
   */
  public static byte[] bigIntToByteArrayLeadingZeros(final BigInteger value, final int numBytes) {
    return byteArrayLeadingZeros(value.toByteArray(), numBytes);
  }

  /**
   * It completes the byte array to a byte array of the given size leading with zeros.
   *
   * @param valueByteArray the source byte array.
   * @param numBytes The number of bytes in the destination array.
   * @return a byte array of size numBytes
   */
  public static byte[] byteArrayLeadingZeros(byte[] valueByteArray, int numBytes) {
    if (valueByteArray.length > numBytes) {
      throw new IllegalArgumentException(
          "value size " + valueByteArray.length + " is bigger than expected " + numBytes);
    }
    if (valueByteArray.length == numBytes) {
      return valueByteArray;
    }
    final byte[] outputBytes = new byte[numBytes];
    System.arraycopy(
        valueByteArray,
        0,
        outputBytes,
        outputBytes.length - valueByteArray.length,
        valueByteArray.length);
    return outputBytes;
  }

  /**
   * Converts a BigInteger value into an array of 8 bytes.
   *
   * @param x The BigInteger.
   * @return The bytes.
   */
  public static byte[] bigIntToBytes(final BigInteger x) {
    return ByteUtils.longToBytes(x.longValue());
  }

  /**
   * Converts an array of 8 bytes into a long.
   *
   * @param bytes The bytes.
   * @return The long.
   */
  public static long bytesToLong(final byte[] bytes) {
    final ByteBuffer buffer = ByteBuffer.allocate(8);
    buffer.put(bytes, 0, 8);
    buffer.flip();
    return buffer.getLong();
  }

  /**
   * Converts a long value into an array of 8 bytes.
   *
   * @param x The long.
   * @return The bytes.
   */
  public static byte[] longToBytes(final long x) {
    final ByteBuffer buffer = ByteBuffer.allocate(8);
    buffer.putLong(x);
    return buffer.array();
  }

  /**
   * Converts an array of 4 bytes into a int.
   *
   * @param bytes The bytes.
   * @return The int.
   */
  public static int bytesToInt(final byte[] bytes) {
    final ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.put(bytes, 0, 4);
    buffer.flip();
    return buffer.getInt();
  }

  /**
   * Converts an int value into an array of 4 bytes.
   *
   * @param x The int.
   * @return The bytes.
   */
  public static byte[] intToBytes(final int x) {
    final ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.putInt(x);
    return buffer.array();
  }

  /**
   * Converts an short value into an array of 4 bytes.
   *
   * @param x The short.
   * @return The bytes.
   */
  public static byte[] shortToBytes(final short x) {
    final ByteBuffer buffer = ByteBuffer.allocate(2);
    buffer.putShort(x);
    return buffer.array();
  }

  /**
   * Converts an int array into a byte array. Assumes each integer value fits in a byte. If integer
   * value uses more than 1 byte then only the lowest byte
   *
   * @param intArray The int array.
   * @return The bytes.
   */
  public static byte[] intArrayToByteArray(final int[] intArray) {
    byte[] bytes = new byte[intArray.length];
    for (int i = 0; i < intArray.length; i++) {
      bytes[i] = (byte) intArray[i];
    }
    return bytes;
  }

  /**
   * Constant-time byte comparison. The constant time behavior eliminates side channel attacks.
   *
   * @param b One byte.
   * @param c Another byte.
   * @return 1 if b and c are equal, 0 otherwise.
   */
  public static int isEqualConstantTime(final int b, final int c) {
    int result = 0;
    final int xor = b ^ c;
    for (int i = 0; i < 8; i++) {
      result |= xor >> i;
    }

    return (result ^ 0x01) & 0x01;
  }

  /**
   * Constant-time check if byte is negative. The constant time behavior eliminates side channel
   * attacks.
   *
   * @param b The byte to check.
   * @return 1 if the byte is negative, 0 otherwise.
   */
  public static int isNegativeConstantTime(final int b) {
    return (b >> 8) & 1;
  }

  /**
   * Creates a human readable representation of an array of bytes.
   *
   * @param bytes The bytes.
   * @return An string representation of the bytes.
   */
  public static String toString(final byte[] bytes) {
    final StringBuilder builder = new StringBuilder();
    builder.append("{ ");
    for (final byte b : bytes) {
      builder.append(String.format("%02X ", (byte) (0xFF & b)));
    }

    builder.append("}");
    return builder.toString();
  }

  /**
   * Reverse and copy to a new array.
   *
   * @param array Array to copy.
   * @return Reverse array.
   */
  public static byte[] reverseCopy(final byte[] array) {
    final byte[] reverseArray = new byte[array.length];

    for (int i = 0, j = array.length - 1; i < array.length; i++, j--) {
      reverseArray[j] = array[i];
    }
    return reverseArray;
  }
}
