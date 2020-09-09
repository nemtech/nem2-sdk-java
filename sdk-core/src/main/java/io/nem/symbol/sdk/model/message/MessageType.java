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
package io.nem.symbol.sdk.model.message;

import java.util.Arrays;

/**
 * The Message type. Supported supply types are: 0: PlainMessage 1: EncryptedMessage. 254:
 * Persistent harvesting delegation.
 */
public enum MessageType {
  PLAIN_MESSAGE(0),

  ENCRYPTED_MESSAGE(1),

  PERSISTENT_HARVESTING_DELEGATION_MESSAGE(254);

  private final int value;

  MessageType(int value) {
    this.value = value;
  }

  /**
   * Gets enum value based on the int value.
   *
   * @param value Raw value of the enum.
   * @return Enum value.
   */
  public static MessageType rawValueOf(final int value) {
    return Arrays.stream(values())
        .filter(e -> e.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  /**
   * Returns enum value.
   *
   * @return int
   */
  public int getValue() {
    return value;
  }
}
