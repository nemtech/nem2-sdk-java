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

import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import java.util.List;

public class AccountMosaicRestrictionTransaction extends Transaction {

    private final AccountRestrictionType restrictionType;

    private final List<UnresolvedMosaicId> restrictionAdditions;

    private final List<UnresolvedMosaicId> restrictionDeletions;

    AccountMosaicRestrictionTransaction(
        AccountMosaicRestrictionTransactionFactory factory) {
        super(factory);
        this.restrictionType = factory.getRestrictionType();
        this.restrictionAdditions = factory.getRestrictionAdditions();
        this.restrictionDeletions = factory.getRestrictionDeletions();
    }

    /**
     * Get account restriction type
     *
     * @return {@link AccountRestrictionType}
     */
    public AccountRestrictionType getRestrictionType() {
        return this.restrictionType;
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
