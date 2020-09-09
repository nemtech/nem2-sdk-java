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

import java.util.Optional;

/** Static class that contains string utility functions. */
public class StringUtils {

  /** Private constructor for this utility class. */
  private StringUtils() {}

  /**
   * Determines if the specified string is null or empty.
   *
   * @param str The string.
   * @return true if the string is null or empty.
   */
  public static boolean isNullOrEmpty(final String str) {
    return null == str || str.isEmpty();
  }

  /**
   * Determines if the specified string is null or whitespace.
   *
   * @param str The string.
   * @return true if the string is null or whitespace.
   */
  public static boolean isNullOrWhitespace(final String str) {
    if (isNullOrEmpty(str)) {
      return true;
    }

    for (int i = 0; i < str.length(); i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Replaces a variable contained in a string with a value. A variable is defined as ${variable}.
   * This pattern is replaced by the given value.
   *
   * @param string String that contains variables.
   * @param name Name of the variable to be replaced with its value.
   * @param value Value that will replace the variable.
   * @return string with value replacing the variable with the given name
   */
  public static String replaceVariable(final String string, final String name, final String value) {
    return string.replace(String.format("${%s}", name), value);
  }

  /**
   * Returns if both strings in the optionals exit and are the case insensitive equals. If one
   * optional is not present, the result is false;
   *
   * @param optionalString1 the first optional
   * @param optionalString2 the second optional
   * @return if both strings in the optionals exit and are the case insensitive equals. If one
   *     optional is not present, the result is false;
   */
  public static boolean equalsIgnoreCase(
      Optional<String> optionalString1, Optional<String> optionalString2) {
    if (!optionalString1.isPresent() || !optionalString2.isPresent()) {
      return false;
    }
    return optionalString1
        .flatMap(string1 -> optionalString2.filter(string1::equalsIgnoreCase))
        .isPresent();
  }
}
