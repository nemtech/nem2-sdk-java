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

import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link AccountMosaicRestrictionTransaction}
 */
public class AccountMosaicRestrictionTransactionFactory extends
    TransactionFactory<AccountMosaicRestrictionTransaction> {

    private final AccountRestrictionFlags restrictionFlags;

    private final List<UnresolvedMosaicId> restrictionAdditions;

    private final List<UnresolvedMosaicId> restrictionDeletions;

    private AccountMosaicRestrictionTransactionFactory(
        final NetworkType networkType,
        final AccountRestrictionFlags restrictionFlags,
        final List<UnresolvedMosaicId> restrictionAdditions,
        final List<UnresolvedMosaicId> restrictionDeletions) {
        super(TransactionType.ACCOUNT_MOSAIC_RESTRICTION, networkType);
        Validate.notNull(restrictionFlags, "RestrictionType must not be null");
        Validate.notNull(restrictionAdditions, "RestrictionAdditions must not be null");
        Validate.notNull(restrictionDeletions, "RestrictionDeletions must not be null");
        this.restrictionFlags = restrictionFlags;
        this.restrictionAdditions = restrictionAdditions;
        this.restrictionDeletions = restrictionDeletions;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param restrictionFlags Restriction falgs.
     * @param restrictionAdditions List of mosaic ids that are going to be added to the restriction.
     * @param restrictionDeletions List of mosaic ids that are going to be removed from the restriction.
     * @return Account mosaic restriction transaction.
     */
    public static AccountMosaicRestrictionTransactionFactory create(NetworkType networkType,
        AccountRestrictionFlags restrictionFlags, final List<UnresolvedMosaicId> restrictionAdditions,
        final List<UnresolvedMosaicId> restrictionDeletions) {
        return new AccountMosaicRestrictionTransactionFactory(networkType, restrictionFlags,
            restrictionAdditions, restrictionDeletions);
    }

    /**
     * Get account restriction type
     *
     * @return {@link AccountRestrictionFlags}
     */
    public AccountRestrictionFlags getRestrictionFlags() {
        return this.restrictionFlags;
    }


    @Override
    public AccountMosaicRestrictionTransaction build() {
        return new AccountMosaicRestrictionTransaction(this);
    }

    /**
     * @return List of mosaic ids that are going to be added to the restriction.
     */
    public List<UnresolvedMosaicId> getRestrictionAdditions() {
        return restrictionAdditions;
    }

    /**
     * @return List of mosaic ids that are going to be removed from the restriction.
     */
    public List<UnresolvedMosaicId> getRestrictionDeletions() {
        return restrictionDeletions;
    }
}
