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

import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link AccountOperationRestrictionTransaction}
 */
public class AccountOperationRestrictionTransactionFactory extends
    TransactionFactory<AccountOperationRestrictionTransaction> {

    private final AccountRestrictionFlags restrictionFlags;

    private final List<TransactionType> restrictionAdditions;

    private final List<TransactionType> restrictionDeletions;

    private AccountOperationRestrictionTransactionFactory(
        final NetworkType networkType,
        final AccountRestrictionFlags restrictionFlags,
        List<TransactionType> restrictionAdditions,
        List<TransactionType> restrictionDeletions) {
        super(TransactionType.ACCOUNT_OPERATION_RESTRICTION, networkType);
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
     * @param restrictionFlags Restriction flags.
     * @param restrictionAdditions List of transaction types that are going to be added to the
     * restriction.
     * @param restrictionDeletions List of transaction types that are going to be removed from the
     * restriction.
     * @return Account operation restriction transaction.
     */
    public static AccountOperationRestrictionTransactionFactory create(NetworkType networkType,
        AccountRestrictionFlags restrictionFlags,
        List<TransactionType> restrictionAdditions,
        List<TransactionType> restrictionDeletions) {
        return new AccountOperationRestrictionTransactionFactory(networkType, restrictionFlags,
            restrictionAdditions, restrictionDeletions);
    }

    /**
     * Get account restriction falgs.
     *
     * @return {@link AccountRestrictionFlags}
     */
    public AccountRestrictionFlags getRestrictionFlags() {
        return this.restrictionFlags;
    }


    @Override
    public AccountOperationRestrictionTransaction build() {
        return new AccountOperationRestrictionTransaction(this);
    }

    /**
     * @return List of transaction types that are going to be added to the restriction.
     */
    public List<TransactionType> getRestrictionAdditions() {
        return restrictionAdditions;
    }

    /**
     * @return List of transaction types that are going to be removed from the restriction.
     */
    public List<TransactionType> getRestrictionDeletions() {
        return restrictionDeletions;
    }
}
