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
package io.nem.symbol.sdk.model.account;

import java.util.Arrays;

/** Account type enum mapped from the rest account type. */
public enum AccountType {

  /** Unlinked. */
  UNLINKED(0),

  /** Balance-holding account that is linked to a remote harvester account. */
  MAIN(1),

  /** Remote harvester account that is linked to a balance-holding account. */
  REMOTE(2),

  /** Remote harvester eligible account that is unlinked. */
  REMOTE_UNLINKED(3);

  /** The server value of the enum. */
  private final int value;

  AccountType(int value) {
    this.value = value;
  }

  /**
   * Static constructor converting role type raw value to enum instance.
   *
   * @param value number value of the enum.
   * @return {@link AccountType}
   */
  public static AccountType rawValueOf(int value) {
    return Arrays.stream(AccountType.values())
        .filter(t -> t.getValue() == value)
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("There is no AccountType for value " + value));
  }

  /**
   * Returns enum value.
   *
   * @return enum value
   */
  public int getValue() {
    return this.value;
  }
}
