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

/**
 * Values of restriction for Catbuffer. Values can be composed.
 *
 * For example, Block (0X80) + Outgoing (0x40) + Operation Type (0x04) = 0xC4
 *
 *
 * Restriction type is an address. ADDRESS(1),
 *
 * Restriction type is a mosaic identifier. MOSAIC_ID(2),
 *
 * Restriction type is a transaction type. TRANSACTION_TYPE(4),
 *
 * Restriction is interpreted as outgoing. OUTGOING(16384),
 *
 * Restriction is interpreted as blocking operation. BLOCK(32768);
 */

public enum AccountRestrictionFlag {

    ADDRESS_VALUE(1),
    MOSAIC_VALUE(2),
    TRANSACTION_TYPE_VALUE(4),
    OUTGOING_VALUE(16384),
    BLOCK_VALUE(32768);

    /**
     * The value.
     */
    private final int value;

    AccountRestrictionFlag(int value) {
        this.value = value;
    }

    /**
     * @return the low level value.
     */
    public int getValue() {
        return value;
    }
}
