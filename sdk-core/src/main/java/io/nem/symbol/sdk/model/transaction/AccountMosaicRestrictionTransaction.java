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

import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.util.List;

public class AccountMosaicRestrictionTransaction extends Transaction {

    private final AccountMosaicRestrictionFlags restrictionFlags;

    private final List<UnresolvedMosaicId> restrictionAdditions;

    private final List<UnresolvedMosaicId> restrictionDeletions;

    AccountMosaicRestrictionTransaction(
        AccountMosaicRestrictionTransactionFactory factory) {
        super(factory);
        this.restrictionFlags = factory.getRestrictionFlags();
        this.restrictionAdditions = factory.getRestrictionAdditions();
        this.restrictionDeletions = factory.getRestrictionDeletions();
    }

    /**
     * Get account restriction flags
     *
     * @return {@link AccountMosaicRestrictionFlags}
     */
    public AccountMosaicRestrictionFlags getRestrictionFlags() {
        return this.restrictionFlags;
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
