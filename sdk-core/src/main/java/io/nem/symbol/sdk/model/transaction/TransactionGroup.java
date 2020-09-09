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

import java.util.Arrays;

/** A stored transaction could be classified in the following groups: */
public enum TransactionGroup {

  /**
   * The transaction reached * the P2P network. At this point, it is not guaranteed that the
   * transaction will be included in a block.
   */
  UNCONFIRMED("unconfirmed"),

  /** Confirmed: The transaction is included in a block. */
  CONFIRMED("confirmed"),

  /**
   * The transaction requires to be cosigned by other transaction participants in order to be
   * included in a block.
   */
  PARTIAL("partial");

  /** Refernce value. */
  private final String value;

  TransactionGroup(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static TransactionGroup rawValueOf(String value) {
    return Arrays.stream(values())
        .filter(e -> e.value.equals(value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }
}
