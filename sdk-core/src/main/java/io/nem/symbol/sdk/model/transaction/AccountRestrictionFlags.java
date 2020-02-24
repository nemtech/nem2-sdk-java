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
import java.util.List;

/**
 * The valid combinations of {@link AccountRestrictionFlag} that creates a {@link
 * AccountRestrictionFlags}.
 *
 * Type of account restriction types:
 *
 * 0x0001 (1 decimal) - Allow only incoming transactions from a given address.
 *
 * 0x0002 (2 decimal) - Allow only incoming transactions containing a given mosaic identifier.
 *
 * 0x4001 (16385 decimal) - Allow only outgoing transactions to a given address.
 *
 * 0x4004 (16388 decimal) - Allow only outgoing transactions with a given transaction type.
 *
 * 0x8001 (32769 decimal) - Block incoming transactions from a given address.
 *
 * 0x8002 (32770 decimal) - Block incoming transactions containing a given mosaic identifier.
 *
 * 0xC001 (49153 decimal) - Block outgoing transactions to a given address.
 *
 * 0xC004 (49156 decimal) - Block outgoing transactions with a given transaction type.
 */

public enum AccountRestrictionFlags {
    /**
     * Allow only incoming transactions from a given address.
     */
    ALLOW_INCOMING_ADDRESS(AccountRestrictionTargetType.ADDRESS,
        AccountRestrictionFlag.ADDRESS_VALUE
    ),

    /**
     * Allow only incoming transactions containing a a given mosaic identifier.
     */
    ALLOW_INCOMING_MOSAIC(AccountRestrictionTargetType.MOSAIC_ID,
        AccountRestrictionFlag.MOSAIC_VALUE
    ),

    /**
     * Allow only outgoing transactions from a given address.
     */
    ALLOW_OUTGOING_ADDRESS(AccountRestrictionTargetType.ADDRESS,
        AccountRestrictionFlag.ADDRESS_VALUE
        , AccountRestrictionFlag.OUTGOING_VALUE),

    /**
     * Allow only outgoing transactions of a given type.
     */
    ALLOW_OUTGOING_TRANSACTION_TYPE(AccountRestrictionTargetType.TRANSACTION_TYPE,
        AccountRestrictionFlag.TRANSACTION_TYPE_VALUE
        , AccountRestrictionFlag.OUTGOING_VALUE
    ),

    /**
     * Account restriction is interpreted as blocking address operation.
     */
    BLOCK_ADDRESS(AccountRestrictionTargetType.ADDRESS, AccountRestrictionFlag.ADDRESS_VALUE
        , AccountRestrictionFlag.BLOCK_VALUE),

    /**
     * Account restriction is interpreted as blocking mosaicId operation.
     */
    BLOCK_MOSAIC(AccountRestrictionTargetType.MOSAIC_ID, AccountRestrictionFlag.MOSAIC_VALUE
        , AccountRestrictionFlag.BLOCK_VALUE),

    /**
     * Block outgoing transactions for a given address.
     */
    BLOCK_OUTGOING_ADDRESS(AccountRestrictionTargetType.ADDRESS,
        AccountRestrictionFlag.ADDRESS_VALUE
        , AccountRestrictionFlag.BLOCK_VALUE
        , AccountRestrictionFlag.OUTGOING_VALUE),

    /**
     * Block outgoing transactions for a given transactionType.
     */
    BLOCK_OUTGOING_TRANSACTION_TYPE(
        AccountRestrictionTargetType.TRANSACTION_TYPE, AccountRestrictionFlag.TRANSACTION_TYPE_VALUE
        , AccountRestrictionFlag.BLOCK_VALUE
        , AccountRestrictionFlag.OUTGOING_VALUE
    );

    private List<AccountRestrictionFlag> flags;
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
     * @param targetType the target type
     * @param flags the values this type is composed of.
     */
    AccountRestrictionFlags(AccountRestrictionTargetType targetType,
        AccountRestrictionFlag... flags) {
        this.flags = Arrays.asList(flags);
        this.value = this.flags.stream().mapToInt(AccountRestrictionFlag::getValue).sum();
        this.targetType = targetType;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static AccountRestrictionFlags rawValueOf(final int value) {
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
     * @return a list with the individual flags.
     */
    public List<AccountRestrictionFlag> getFlags() {
        return flags;
    }

    /**
     * @return the target type.
     */
    public AccountRestrictionTargetType getTargetType() {
        return targetType;
    }
}
