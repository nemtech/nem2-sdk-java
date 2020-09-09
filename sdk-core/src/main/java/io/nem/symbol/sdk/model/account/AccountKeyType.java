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

/**
 * Type of account key: * 0 - Unset. * 1 - Linked account public key. * 2 - Node public key on which
 * remote is allowed to harvest. * 4 - VRF public key.
 */
public enum AccountKeyType {

  /** unset key. */
  UNSET(0),

  /**
   * linked account public key \note this can be either a remote or main account public key
   * depending on context.
   */
  LINKED(1),

  /** node public key on which remote is allowed to harvest. */
  NODE(2),

  /** VRF public key. */
  VRF(4);

  private final int value;

  AccountKeyType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static AccountKeyType rawValueOf(int value) {
    return Arrays.stream(values())
        .filter(e -> e.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }
}
