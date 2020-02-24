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
 * The alias type. Supported types are: 0: No alias. 1: Mosaic id alias. 2: Address alias.
 *
 */
public enum AliasType {
    NONE(0),
    MOSAIC(1),
    ADDRESS(2);

    private final Integer value;

    AliasType(int value) {
        this.value = value;
    }

    public static AliasType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Returns enum value.
     *
     * @return enum value
     */
    public final Integer getValue() {
        return this.value;
    }
}
