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

/**
 * Enum containing receipt version constants.
 *
 * @see <a href="https://github.com/nemtech/catapult-server/blob/master/src/catapult/model/ReceiptType.h"></a>
 * @see <a href="https://github.com/nemtech/catapult-server/blob/master/src/catapult/model/ReceiptType.cpp"></a>
 */
public enum ReceiptVersion {
    /**
     * Balance transfer receipt version.
     */
    BALANCE_TRANSFER(1),
    /**
     * Balance change receipt version.
     */
    BALANCE_CHANGE(1),
    /**
     * Artifact expiry receipt version.
     */
    ARTIFACT_EXPIRY(1),
    /**
     * Transaction statement receipt version.
     */
    TRANSACTION_STATEMENT(1),
    /**
     * Resolution statement receipt version.
     */
    RESOLUTION_STATEMENT(1),
    /**
     * Inflation receipt receipt version.
     */
    INFLATION_RECEIPT(1);

    private final int value;

    ReceiptVersion(int value) {
        this.value = value;
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
