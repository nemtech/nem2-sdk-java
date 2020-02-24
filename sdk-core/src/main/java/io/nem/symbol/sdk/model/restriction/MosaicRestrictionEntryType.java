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

package io.nem.symbol.sdk.model.restriction;

import java.util.Arrays;

public enum MosaicRestrictionEntryType {

    /**
     * Mosaic address restriction
     */
    ADDRESS(0),

    /**
     * Mosaic global restriction
     */
    GLOBAL(1);

    /**
     * The int representation
     */
    private int value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
    MosaicRestrictionEntryType(int value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static MosaicRestrictionEntryType rawValueOf(final int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Returns enum value.
     *
     * @return byte
     */
    public int getValue() {
        return value;
    }
}
