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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;

public class AccountRestrictionModification<T> {

    private final AccountRestrictionModificationAction modificationAction;
    private final T value;

    private AccountRestrictionModification(
        AccountRestrictionModificationAction modificationAction, T value) {
        this.modificationAction = modificationAction;
        this.value = value;
    }

    /**
     * @param modificationAction the modification type.
     * @param address the affected unresolved address.
     * @return AccountRestrictionModification of {@link Address}
     */
    public static AccountRestrictionModification<UnresolvedAddress> createForAddress(
        AccountRestrictionModificationAction modificationAction, UnresolvedAddress address) {
        return new AccountRestrictionModification<>(modificationAction, address);
    }

    /**
     * @param modificationAction the modification type.
     * @param mosaicId the affected unresolved mosaic id.
     * @return AccountRestrictionModification {@link UnresolvedMosaicId}
     */
    public static AccountRestrictionModification<UnresolvedMosaicId> createForMosaic(
        AccountRestrictionModificationAction modificationAction, UnresolvedMosaicId mosaicId) {
        return new AccountRestrictionModification<>(modificationAction, mosaicId);
    }

    /**
     * @param modificationAction the modification type.
     * @param transactionType the affected transaction type.
     * @return AccountRestrictionModification of {@link TransactionType}
     */
    public static AccountRestrictionModification<TransactionType> createForTransactionType(
        AccountRestrictionModificationAction modificationAction, TransactionType transactionType) {
        return new AccountRestrictionModification<>(modificationAction, transactionType);
    }

    /**
     * Get modification value
     *
     * @return value
     */
    public T getValue() {
        return value;
    }

    /**
     * Get modification type
     *
     * @return AccountRestrictionModificationAction
     */
    public AccountRestrictionModificationAction getModificationAction() {
        return this.modificationAction;
    }
}
