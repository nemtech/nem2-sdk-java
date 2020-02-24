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

package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.reactivex.Observable;
import java.math.BigInteger;

/**
 * Service that helps clients setting up and updating mosaic restrictions.
 */
public interface MosaicRestrictionTransactionService {

    /**
     * Create a {@link MosaicGlobalRestrictionTransactionFactory} object that can be used to create
     * or update a mosaic global restriction.
     *
     * This service checks the current global restriction value using rest and set the corresponding
     * previousRestrictionType and previousRestrictionValue so an update can be performed.
     *
     *
     * When using aliases, the service will resolve them in order to query the mosaic restriction
     * rest endpoint. This could add some overhead. If you know the real mosaic id, it's recommended
     * to use it.
     *
     * @param mosaicId the mosaic id or an alias. If an alias is sent, the service will resolve it
     * in order to retrieve the current global restriction value.
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
     * Create a {@link MosaicAddressRestrictionTransactionFactory} object that can be used to create
     * or update a mosaic address restriction.
     *
     * This service checks the current address restriction value using rest and set the
     * corresponding previousRestrictionValue so an update can be performed.
     *
     * When using aliases, the service will resolve them in order to query the mosaic restriction
     * rest endpoint. This could add some overhead. If you know the real mosaic id and target
     * address, it's recommended to use them.
     *
     * @param mosaicId the mosaic id or an alias. If an alias is sent, the service will resolve it *
     * in order to retrieve the current mosaic address restriction value.
     * @param restrictionKey Restriction key
     * @param targetAddress the target address or an alias. If an alias is sent, the service will
     * resolve it in order to retrieve the current mosaic address restriction value.
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
