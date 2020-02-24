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

public enum MosaicRestrictionType {

    /** Uninitialized value indicating no restriction. */
    NONE((byte) 0),
    /** Allow if equal. */
    EQ((byte) 1),
    /** Allow if not equal. */
    NE((byte) 2),
    /** Allow if less than. */
    LT((byte) 3),
    /** Allow if less than or equal. */
    LE((byte) 4),
    /** Allow if greater than. */
    GT((byte) 5),
    /** Allow if greater than or equal. */
    GE((byte) 6);

    /** Enum value. */
    private final byte value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
    MosaicRestrictionType(final byte value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static MosaicRestrictionType rawValueOf(final byte value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Returns enum value.
     *
     * @return byte
     */
    public byte getValue() {
        return value;
    }
}
