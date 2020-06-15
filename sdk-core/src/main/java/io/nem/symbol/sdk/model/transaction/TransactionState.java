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

/**
 * State of the transaction.
 */
public enum TransactionState {

    CONFIRMED("confirmed"),

    UNCONFIRMED("unconfirmed"),

    FAILED("failed"),

    PARTIAL("partial");

    private final String value;

    TransactionState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static TransactionState rawValueOf(String value) {
        return Arrays.stream(values()).filter(e -> e.value.equals(value)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }
}

