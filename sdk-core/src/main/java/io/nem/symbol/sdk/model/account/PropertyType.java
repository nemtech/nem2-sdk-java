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
 * Account property type 0x01 The property type is an address. 0x02 The property type is mosaic id.
 * 0x03 The property type is a transaction type. 0x04 Property type sentinel. 0x80 + type The
 * property is interpreted as a blocking operation.
 */
public enum PropertyType {
    ALLOW_ADDRESS(0x01),
    ALLOW_MOSAIC(0x02),
    ALLOW_TRANSACTION(0x04),
    SENTINEL(0x05),
    BLOCK_ADDRESS(0x80 + 0x01),
    BLOCK_MOSAIC(0x80 + 0x02),
    BLOCK_TRANSACTION(0x80 + 0x04);

    private Integer value;

    PropertyType(int value) {
        this.value = value;
    }

    public static PropertyType rawValueOf(String stringValue) {
        try {
            int value = Integer.decode(stringValue);
            return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(stringValue + " is not a valid value"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(stringValue + " is not a valid value");
        }

    }

    /**
     * Returns enum value.
     *
     * @return enum value
     */
    public Integer getValue() {
        return this.value;
    }
}
