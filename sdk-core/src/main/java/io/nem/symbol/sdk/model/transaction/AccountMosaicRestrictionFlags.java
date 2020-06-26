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
 * The valid combinations of {@link AccountRestrictionFlag} that creates a {@link AccountMosaicRestrictionFlags}.
 *
 * Type of account restriction types:
 *
 * 0x0002 (2 decimal) - Allow only incoming transactions containing a given mosaic identifier.
 *
 * 0x8002 (32770 decimal) - Block incoming transactions containing a given mosaic identifier.
 */

public enum AccountMosaicRestrictionFlags implements AccountRestrictionFlags {

    /**
     * Allow only incoming transactions containing a a given mosaic identifier.
     */
    ALLOW_INCOMING_MOSAIC(AccountRestrictionFlag.MOSAIC_VALUE),

    /**
     * Account restriction is interpreted as blocking mosaicId operation.
     */
    BLOCK_MOSAIC(AccountRestrictionFlag.MOSAIC_VALUE, AccountRestrictionFlag.BLOCK_VALUE);

    private final List<AccountRestrictionFlag> flags;
    /**
     * Enum value.
     */
    private final int value;


    /**
     * Constructor.
     *
     * @param flags the values this type is composed of.
     */
    AccountMosaicRestrictionFlags(AccountRestrictionFlag... flags) {
        this.flags = Arrays.asList(flags);
        this.value = this.flags.stream().mapToInt(AccountRestrictionFlag::getValue).sum();
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static AccountMosaicRestrictionFlags rawValueOf(final int value) {
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
        return AccountRestrictionTargetType.MOSAIC_ID;
    }
}
