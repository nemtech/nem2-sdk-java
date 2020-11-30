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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Static class containing helper functions for formatting. */
public class FormatUtils {

  /** Private constructor for this utility class. */
  private FormatUtils() {}

  /**
   * Gets a default decimal format that should be used for formatting decimal values.
   *
   * @return A default decimal format.
   */
  public static DecimalFormat getDefaultDecimalFormat() {
    final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    decimalFormatSymbols.setDecimalSeparator('.');
    final DecimalFormat format = new DecimalFormat("#0.000", decimalFormatSymbols);
    format.setGroupingUsed(false);
    return format;
  }

  /**
   * Gets a decimal format that with the desired number of decimal places.
   *
   * @param decimalPlaces The number of decimal places.
   * @return The desired decimal format.
   */
  public static DecimalFormat getDecimalFormat(final int decimalPlaces) {
    if (decimalPlaces < 0) {
      throw new IllegalArgumentException("decimalPlaces must be non-negative");
    }

    final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    decimalFormatSymbols.setDecimalSeparator('.');
    final StringBuilder builder = new StringBuilder();
    builder.append("#0");

    if (decimalPlaces > 0) {
      builder.append('.');
      final char[] zeros = new char[decimalPlaces];
      Arrays.fill(zeros, '0');
      builder.append(zeros);
    }

    final DecimalFormat format = new DecimalFormat(builder.toString(), decimalFormatSymbols);
    format.setGroupingUsed(false);
    return format;
  }

  /**
   * Formats a double value with a given number of decimal places.
   *
   * @param value The value to format.
   * @param decimalPlaces The desired number of decimal places.
   * @return The formatted string.
   */
  public static String format(final double value, final int decimalPlaces) {
    final DecimalFormat formatter = getDecimalFormat(decimalPlaces);
    return formatter.format(value);
  }

  /**
   * It parse a server time/duration configuration like: - 1000ms 1000 milliseconds - 15s 15 seconds
   * - 5m 5 minutes - 2h 2 hours - 10d 10 days
   *
   * <p>into a @{@link Duration} object
   *
   * @param serverValue time.
   * @return an instant from that value.
   */
  public static Duration parseServerDuration(String serverValue) {
    String preprocessedValue = serverValue.replace("'", "").trim();
    Pattern periodPattern = Pattern.compile("([0-9]+)([hdms]+)[:\\s]?");
    Matcher matcher = periodPattern.matcher(preprocessedValue);

    Duration duration = Duration.ofMillis(0);
    Set<String> usedTypes = new HashSet<>();
    int expected = 0;
    while (matcher.find()) {
      if (expected != matcher.start()) {
        throw new IllegalArgumentException("Duration value format is not recognized.");
      }
      expected = matcher.end();
      int num = Integer.parseInt(matcher.group(1));
      String type = matcher.group(2);
      if (!usedTypes.add(type)) {
        throw new IllegalArgumentException("Duration value format is not recognized.");
      }
      switch (type) {
        case "ms":
          duration = duration.plus(Duration.ofMillis(num));
          break;
        case "s":
          duration = duration.plus(Duration.ofSeconds(num));
          break;
        case "m":
          duration = duration.plus(Duration.ofMinutes(num));
          break;
        case "h":
          duration = duration.plus(Duration.ofHours(num));
          break;
        case "d":
          duration = duration.plus(Duration.ofDays(num));
          break;
        default:
          throw new IllegalArgumentException("Duration value format is not recognized.");
      }
    }
    if (usedTypes.isEmpty()) {
      throw new IllegalArgumentException("Duration value format is not recognized.");
    }
    return duration;
  }

  /**
   * It converts a server Hex like 0x017D'1694'0477'B3F5 to 017D16940477B3F5
   *
   * @param serverHex the server hex
   * @return a regular hex string.
   */
  public static String toSimpleHex(String serverHex) {
    return serverHex.replace("'", "").replaceAll("^(0x)", "");
  }
}
