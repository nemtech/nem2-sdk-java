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

/**
 * Type of the repository:
 *
 * <p>* 0x243 (579 decimal) - accountStatePath.
 *
 * <p>* 0x248 (584 decimal) - hashLockStatePath.
 *
 * <p>* 0x252 (594 decimal) - secretLockStatePath.
 *
 * <p>* 0x244 (580 decimal) - metadataStatePath.
 *
 * <p>* 0x24D (589 decimal) - mosaicStatePath.
 *
 * <p>* 0x255 (597 decimal) - multisigStatePath.
 *
 * <p>* 0x24E (590 decimal) - namespaceStatePath.
 *
 * <p>* 0x250 (592 decimal) - accountRestrictionsStatePath.
 *
 * <p>* 0x251 (593 decimal) - mosaicRestrictionsStatePath.
 */
public enum StatePacketType {
  ACCOUNT_STATE_PATH(0x243),

  HASH_LOCK_STATE_PATH(0x248),

  SECRET_LOCK_STATE_PATH(0x252),

  METADATA_STATE_PATH(0x244),

  MOSAIC_STATE_PATH(0x24D),

  MULTISIG_STATE_PATH(0x255),

  NAMESPACE_STATE_PATH(0x24E),

  ACCOUNT_RESTRICTIONS_STATE_PATH(0x250),

  MOSAIC_RESTRICTIONS_STATE_PATH(0x251);

  /** The catbuffer value of the state path. */
  public final int value;

  StatePacketType(int value) {
    this.value = value;
  }

  public static StatePacketType rawValueOf(int value) {
    return Arrays.stream(values())
        .filter(e -> e.getValue() == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  /** @return the catbuffer state path value. */
  public int getValue() {
    return value;
  }
}
