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

package io.nem.symbol.sdk.model.receipt;

import java.util.Arrays;

/**
 * Enum containing resolution type constants.
 */
public enum ResolutionType {

    ADDRESS(0),
    MOSAIC(1);

    private final int value;

    ResolutionType(int value) {
        this.value = value;
    }

    /**
     * Static constructor converting resolution type raw value to enum instance.
     *
     * @param value the low level int value.
     * @return {@link ReceiptType}
     */
    public static ResolutionType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
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
