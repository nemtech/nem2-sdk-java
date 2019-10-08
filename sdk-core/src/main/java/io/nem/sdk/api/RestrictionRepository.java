/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.api;


import io.nem.sdk.model.account.AccountRestrictions;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.sdk.model.restriction.MosaicGlobalRestriction;
import io.reactivex.Observable;
import java.util.List;

/**
 * Restriction interface repository.
 *
 * @since 1.0
 */
public interface RestrictionRepository {

    /**
     * Returns the account restrictions for a given account.
     *
     * @param address the address
     * @return Observable of {@link AccountRestrictions}
     */
    Observable<AccountRestrictions> getAccountRestrictions(Address address);

    /**
     * Returns the account restrictions for a given array of addresses.
     *
     * @param addresses {@link List} of {@link Address}
     * @return Observable {@link List} of {@link AccountRestrictions}
     */
    Observable<List<AccountRestrictions>> getAccountsRestrictions(List<Address> addresses);

    /**
     * Get mosaic address restrictions for a given mosaic and account identifier.
     *
     * @param mosaicId Mosaic identifier.
     * @param address address
     * @return Observable of {@link MosaicAddressRestriction}
     */
    Observable<MosaicAddressRestriction> getMosaicAddressRestriction(MosaicId mosaicId,
        Address address);

    /**
     * Get mosaic address restrictions for a given mosaic and account identifiers array
     *
     * @param mosaicId Mosaic identifier.
     * @param addresses list of addresses
     * @return Observable {@link List} of {@link MosaicAddressRestriction}.
     */
    Observable<List<MosaicAddressRestriction>> getMosaicAddressRestrictions(MosaicId mosaicId,
        List<Address> addresses);

    /**
     * Get mosaic global restrictions for a given mosaic identifier.
     *
     * @param mosaicId Mosaic identifier.
     * @return Observable of {@link MosaicGlobalRestriction}
     */
    Observable<MosaicGlobalRestriction> getMosaicGlobalRestriction(MosaicId mosaicId);

    /**
     * Get mosaic global restrictions for a given list of mosaics.
     *
     * @param mosaicIds List of mosaic identifier.
     * @return Observable {@link List} of {@link MosaicGlobalRestriction}.
     */
    Observable<List<MosaicGlobalRestriction>> getMosaicGlobalRestrictions(List<MosaicId> mosaicIds);
}
