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
package io.nem.symbol.sdk.model.blockchain;

import java.util.Arrays;

/** Types of blocks in symbol. */
public enum BlockType {
  NEMESIS_BLOCK(0x8043),

  NORMAL_BLOCK(0x8143),

  IMPORTANCE_BLOCK(0x8243);

  /** The catbuffer value of the block type. */
  private final int value;

  BlockType(int value) {
    this.value = value;
  }

  public static BlockType rawValueOf(int value) {
    return Arrays.stream(values())
        .filter(e -> e.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  public int getValue() {
    return value;
  }
}
