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
package io.nem.symbol.sdk.model.namespace;

import java.util.Arrays;

/**
 * Enum containing namespace registration type.
 *
 * @since 1.0
 */
public enum NamespaceRegistrationType {
  /** Root namespace */
  ROOT_NAMESPACE(0),
  /** Sub namespace */
  SUB_NAMESPACE(1);

  private final int value;

  NamespaceRegistrationType(int value) {
    this.value = value;
  }

  public static NamespaceRegistrationType rawValueOf(int value) {
    return Arrays.stream(values())
        .filter(e -> e.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  public int getValue() {
    return value;
  }
}
