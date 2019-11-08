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
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link AccountOperationRestrictionTransaction}
 */
public class AccountOperationRestrictionTransactionFactory extends
    TransactionFactory<AccountOperationRestrictionTransaction> {

    private final AccountRestrictionType restrictionType;

    private final List<TransactionType> restrictionAdditions;

    private final List<TransactionType> restrictionDeletions;

    private AccountOperationRestrictionTransactionFactory(
        final NetworkType networkType,
        final AccountRestrictionType restrictionType,
        List<TransactionType> restrictionAdditions,
        List<TransactionType> restrictionDeletions) {
        super(TransactionType.ACCOUNT_OPERATION_RESTRICTION, networkType);
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
     * @param restrictionAdditions List of transaction types that are going to be added to the
     * restriction.
     * @param restrictionDeletions List of transaction types that are going to be removed from the
     * restriction.
     * @return Account operation restriction transaction.
     */
    public static AccountOperationRestrictionTransactionFactory create(NetworkType networkType,
        AccountRestrictionType restrictionType,
        List<TransactionType> restrictionAdditions,
        List<TransactionType> restrictionDeletions) {
        return new AccountOperationRestrictionTransactionFactory(networkType, restrictionType,
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
