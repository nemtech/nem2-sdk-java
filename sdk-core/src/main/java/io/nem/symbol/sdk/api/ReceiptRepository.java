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

import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.reactivex.Observable;

public interface ReceiptRepository {

    /**
     * Returns a transaction statements page based on the criteria.
     *
     * @param criteria the criteria
     * @return a page of {@link TransactionStatement}
     */
    Observable<Page<TransactionStatement>> searchReceipts(TransactionStatementSearchCriteria criteria);

    /**
     * Returns an addresses resolution statements page based on the criteria.
     *
     * @param criteria the criteria
     * @return a page of {@link AddressResolutionStatement}
     */
    Observable<Page<AddressResolutionStatement>> searchAddressResolutionStatements(
        ResolutionStatementSearchCriteria criteria);

    /**
     * Returns an mosaic resoslution statements page based on the criteria.
     *
     * @param criteria the criteria
     * @return a page of {@link MosaicResolutionStatement}
     */
    Observable<Page<MosaicResolutionStatement>> searchMosaicResolutionStatements(
        ResolutionStatementSearchCriteria criteria);



}
