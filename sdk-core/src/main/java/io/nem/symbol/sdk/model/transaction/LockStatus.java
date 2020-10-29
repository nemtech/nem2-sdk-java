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

/** It defines the state of the lock entry. */
public enum LockStatus {

  /** lock is unused. */
  UNUSED((byte) 0),

  /** lock was already used. */
  USED((byte) 1);

  /** Enum value. */
  private final byte value;

  /**
   * Constructor.
   *
   * @param value Enum value.
   */
  LockStatus(final byte value) {
    this.value = value;
  }

  /**
   * Gets enum value.
   *
   * @param value Raw value of the enum.
   * @return Enum value.
   */
  public static LockStatus rawValueOf(final byte value) {
    return Arrays.stream(values())
        .filter(e -> e.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  /**
   * Gets the value of the enum.
   *
   * @return Value of the enum.
   */
  public byte getValue() {
    return this.value;
  }
}
