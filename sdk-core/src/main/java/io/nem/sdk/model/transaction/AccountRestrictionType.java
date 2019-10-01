/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.transaction;

import java.util.Arrays;

/**
 * Type of account restriction (DTO):
 *
 * 0x01 (1 decimal) - Allow only incoming transactions from a given address.
 *
 * 0x02 (2 decimal) - Allow only incoming transactions containing a given mosaic identifier.
 *
 * 0x05 (5 decimal) - Account restriction sentinel.
 *
 * 0x41 (65 decimal) - Allow only outgoing transactions to a given address.
 *
 * 0x44 (68 decimal) - Allow only outgoing transactions with a given transaction type.
 *
 * 0x81 (129 decimal) - Block incoming transactions from a given address.
 *
 * 0x82 (130 decimal) - Block incoming transactions containing a given mosaic identifier.
 *
 * 0xC1 (193 decimal) - Block outgoing transactions to a given address.
 *
 * 0xC4 (196 decimal) - Block outgoing transactions with a given transaction type.
 */

public enum AccountRestrictionType {
    /**
     * Allow only incoming transactions from a given address.
     */
    ALLOW_INCOMING_ADDRESS(AccountRestrictionTypeValueOptions.ADDRESS_VALUE,
        AccountRestrictionTargetType.ADDRESS),

    /**
     * Allow only incoming transactions containing a a given mosaic identifier.
     */
    ALLOW_INCOMING_MOSAIC(AccountRestrictionTypeValueOptions.MOSAIC_VALUE,
        AccountRestrictionTargetType.MOSAIC_ID),

    /**
     * Allow only outgoing transactions from a given address.
     */
    ALLOW_OUTGOING_ADDRESS(AccountRestrictionTypeValueOptions.ADDRESS_VALUE
        + AccountRestrictionTypeValueOptions.OUTGOING_VALUE, AccountRestrictionTargetType.ADDRESS),

    /**
     * Allow only outgoing transactions of a given type.
     */
    ALLOW_OUTGOING_TRANSACTION_TYPE(AccountRestrictionTypeValueOptions.TRANSACTION_TYPE_VALUE
        + AccountRestrictionTypeValueOptions.OUTGOING_VALUE,
        AccountRestrictionTargetType.TRANSACTION_TYPE),

    /**
     * Account restriction type sentinel.
     */
    SENTINEL(AccountRestrictionTypeValueOptions.SENTINEL_VALUE,
        AccountRestrictionTargetType.ADDRESS),

    /**
     * Account restriction is interpreted as blocking address operation.
     */
    BLOCK_ADDRESS(AccountRestrictionTypeValueOptions.ADDRESS_VALUE
        + AccountRestrictionTypeValueOptions.BLOCK_VALUE, AccountRestrictionTargetType.ADDRESS),

    /**
     * Account restriction is interpreted as blocking mosaicId operation.
     */
    BLOCK_MOSAIC(AccountRestrictionTypeValueOptions.MOSAIC_VALUE
        + AccountRestrictionTypeValueOptions.BLOCK_VALUE, AccountRestrictionTargetType.MOSAIC_ID),

    /**
     * Block outgoing transactions for a given address.
     */
    BLOCK_OUTGOING_ADDRESS(AccountRestrictionTypeValueOptions.ADDRESS_VALUE
        + AccountRestrictionTypeValueOptions.BLOCK_VALUE
        + AccountRestrictionTypeValueOptions.OUTGOING_VALUE, AccountRestrictionTargetType.ADDRESS),

    /**
     * Block outgoing transactions for a given transactionType.
     */
    BLOCK_OUTGOING_TRANSACTION_TYPE(
        AccountRestrictionTypeValueOptions.TRANSACTION_TYPE_VALUE
            + AccountRestrictionTypeValueOptions.BLOCK_VALUE
            + AccountRestrictionTypeValueOptions.OUTGOING_VALUE,
        AccountRestrictionTargetType.TRANSACTION_TYPE);

    /**
     * Enum value.
     */
    private final int value;

    /**
     * The target type.
     */
    private final AccountRestrictionTargetType targetType;

    /**
     * Constructor.
     *
     * @param value Enum value.
     * @param targetType the target type
     */
    AccountRestrictionType(final int value, AccountRestrictionTargetType targetType) {
        this.value = value;
        this.targetType = targetType;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static AccountRestrictionType rawValueOf(final int value) {
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

    /**
     * @return the target type.
     */
    public AccountRestrictionTargetType getTargetType() {
        return targetType;
    }
}
