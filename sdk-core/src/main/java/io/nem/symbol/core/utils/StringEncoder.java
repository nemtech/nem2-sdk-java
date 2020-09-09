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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** Static class that contains utility functions for converting strings to and from UTF-8 bytes. */
public class StringEncoder {

  /** Private constructor for this utility class. */
  private StringEncoder() {}

  private static final Charset ENCODING_CHARSET = StandardCharsets.UTF_8;

  /**
   * Converts a string to a UTF-8 byte array.
   *
   * @param s The input string.
   * @return The output byte array.
   */
  public static byte[] getBytes(final String s) {
    return s.getBytes(ENCODING_CHARSET);
  }

  /**
   * Converts a UTF-8 byte array to a string.
   *
   * @param bytes The input byte array.
   * @return The output string.
   */
  public static String getString(final byte[] bytes) {
    return new String(bytes, ENCODING_CHARSET);
  }
}
