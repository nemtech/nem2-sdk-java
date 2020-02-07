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

package io.nem.sdk.api;

import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import io.nem.sdk.model.namespace.NamespaceId;
import io.reactivex.Observable;
import java.util.List;

/**
 * Service related to network currencies.
 *
 */
public interface NetworkCurrencyService {

    /**
     * This method returns the list of {@link NetworkCurrency} found in block 1.
     *
     * The intent of this method is to resolve the configured main (like cat.currency or symbol.xym)
     * and harvest currencies (cat.harvest). More currencies may be defined in the block one.
     *
     * @return the list of {@link NetworkCurrency} found in block 1.
     */
    Observable<List<NetworkCurrency>> getNetworkCurrenciesFromNemesis();

    /**
     * This method resolves a {@link NetworkCurrency} from a known {@link MosaicId} using rest. The
     * NetworkCurrency will contain the first resolved alias / namespace id if exist.
     *
     * @param mosaicId the mosaic id
     * @return the NetworkCurrency of the given mosaic id.
     */
    Observable<NetworkCurrency> getNetworkCurrencyFromMosaicId(MosaicId mosaicId);

    /**
     * This method resolves a {@link NetworkCurrency} from a known {@link NamespaceId} using rest.
     *
     * @param namespaceId the namespace id
     * @return the NetworkCurrency of the given namespace id.
     */
    Observable<NetworkCurrency> getNetworkCurrencyFromNamespaceId(NamespaceId namespaceId);

}
