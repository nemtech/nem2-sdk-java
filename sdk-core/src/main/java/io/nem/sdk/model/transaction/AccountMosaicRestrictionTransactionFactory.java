/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package io.nem.sdk.model.transaction;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link AccountMosaicRestrictionTransaction}
 */
public class AccountMosaicRestrictionTransactionFactory extends
    TransactionFactory<AccountMosaicRestrictionTransaction> {

    private final AccountRestrictionType restrictionType;

    private final List<UnresolvedMosaicId> restrictionAdditions;

    private final List<UnresolvedMosaicId> restrictionDeletions;

    private AccountMosaicRestrictionTransactionFactory(
        final NetworkType networkType,
        final AccountRestrictionType restrictionType,
        final List<UnresolvedMosaicId> restrictionAdditions,
        final List<UnresolvedMosaicId> restrictionDeletions) {
        super(TransactionType.ACCOUNT_MOSAIC_RESTRICTION, networkType);
        Validate.notNull(restrictionType, "RestrictionType must not be null");
        Validate.notNull(restrictionAdditions, "RestrictionAdditions must not be null");
        Validate.notNull(restrictionDeletions, "RestrictionDeletions must not be null");
        this.restrictionType = restrictionType;
        this.restrictionAdditions = restrictionAdditions;
        this.restrictionDeletions = restrictionDeletions;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param restrictionType Restriction type.
     * @param restrictionAdditions List of mosaic ids that are going to be added to the restriction.
     * @param restrictionDeletions List of mosaic ids that are going to be removed from the restriction.
     * @return Account mosaic restriction transaction.
     */
    public static AccountMosaicRestrictionTransactionFactory create(NetworkType networkType,
        AccountRestrictionType restrictionType, final List<UnresolvedMosaicId> restrictionAdditions,
        final List<UnresolvedMosaicId> restrictionDeletions) {
        return new AccountMosaicRestrictionTransactionFactory(networkType, restrictionType,
            restrictionAdditions, restrictionDeletions);
    }

    /**
     * Get account restriction type
     *
     * @return {@link AccountRestrictionType}
     */
    public AccountRestrictionType getRestrictionType() {
        return this.restrictionType;
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
