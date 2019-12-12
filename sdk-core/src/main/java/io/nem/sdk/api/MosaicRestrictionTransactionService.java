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

package io.nem.sdk.api;

import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.reactivex.Observable;
import java.math.BigInteger;

/**
 * Service that helps clients setting up and updating mosaic restrictions.
 */
public interface MosaicRestrictionTransactionService {

    /**
     * Create a {@link MosaicGlobalRestrictionTransactionFactory} object without previous
     * restriction data
     *
     * @param mosaicId MosaicId
     * @param restrictionKey Restriction key
     * @param restrictionValue New restriction value
     * @param restrictionType New restriction type
     * @return MosaicGlobalRestrictionTransactionFactory of the transaction ready to be announced.
     */
    Observable<MosaicGlobalRestrictionTransactionFactory> createMosaicGlobalRestrictionTransactionFactory(
        UnresolvedMosaicId mosaicId,
        BigInteger restrictionKey,
        BigInteger restrictionValue,
        MosaicRestrictionType restrictionType);


    /**
     * Create a {@link MosaicAddressRestrictionTransactionFactory} object without previous
     * restriction data
     *
     * @param mosaicId MosaicId
     * @param restrictionKey Restriction key
     * @param targetAddress Target address
     * @param restrictionValue New restriction value
     * @return {@link MosaicAddressRestrictionTransactionFactory} object without previous
     * restriction data
     */
    Observable<MosaicAddressRestrictionTransactionFactory> createMosaicAddressRestrictionTransactionFactory(
        UnresolvedMosaicId mosaicId,
        BigInteger restrictionKey,
        UnresolvedAddress targetAddress,
        BigInteger restrictionValue);
}
