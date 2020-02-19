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

package io.nem.sdk.model.blockchain;

import java.util.Arrays;

/**
 * Position relative to the proofHash being evaluated.
 */
public enum Position {

    LEFT("left"),

    RIGHT("right");

    private String value;

    Position(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Static constructor converting {@link Position} to enum instance.
     *
     * @param value the low level int value.
     * @return {@link NetworkType}
     */
    public static Position rawValueOf(String value) {
        return Arrays.stream(values()).filter(e -> e.value.equals(value)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

}
