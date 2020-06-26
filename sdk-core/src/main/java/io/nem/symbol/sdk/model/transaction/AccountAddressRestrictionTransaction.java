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

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import java.util.List;

public class AccountAddressRestrictionTransaction extends Transaction {

    private final AccountAddressRestrictionFlags restrictionFlags;

    private final List<UnresolvedAddress> restrictionAdditions;

    private final List<UnresolvedAddress> restrictionDeletions;

    AccountAddressRestrictionTransaction(
        AccountAddressRestrictionTransactionFactory factory) {
        super(factory);
        this.restrictionFlags = factory.getRestrictionFlags();
        this.restrictionAdditions = factory.getRestrictionAdditions();
        this.restrictionDeletions = factory.getRestrictionDeletions();
    }

    /**
     * Get account restriction flags
     *
     * @return {@link AccountAddressRestrictionFlags}
     */
    public AccountAddressRestrictionFlags getRestrictionFlags() {
        return this.restrictionFlags;
    }

    /**
     * @return List of addresses that are going to be added to the restriction.
     */
    public List<UnresolvedAddress> getRestrictionAdditions() {
        return restrictionAdditions;
    }

    /**
     * @return List of addresses that are going to be removed from the restriction.
     */
    public List<UnresolvedAddress> getRestrictionDeletions() {
        return restrictionDeletions;
    }
}
