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

import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.reactivex.Observable;
import java.util.List;

/** A service that allows you loading Network currencies for mosaic creation. */
public interface CurrencyService {

  /**
   * This method load network currencies (main currency and harvest).
   *
   * @return the network currencies.
   */
  Observable<NetworkCurrencies> getNetworkCurrencies();

  /**
   * This method resolves a {@link Currency} from a known {@link MosaicId} using rest. The
   * NetworkCurrency will contain the first resolved alias / namespace id if exist.
   *
   * @param mosaicId the mosaic id
   * @return the NetworkCurrency of the given mosaic id.
   */
  Observable<Currency> getCurrency(MosaicId mosaicId);

  /**
   * This method resolves a {@link Currency} objects from a known {@link MosaicId} ids using rest.
   * The * NetworkCurrency will contain the first resolved alias / namespace id if exist.
   *
   * @param mosaicIds the mosaic ids
   * @return the resolved currencies.
   */
  Observable<List<Currency>> getCurrencies(List<MosaicId> mosaicIds);

  /**
   * This method resolves a {@link Currency} from a known {@link NamespaceId} using rest.
   *
   * @param namespaceId the namespace id
   * @return the NetworkCurrency of the given namespace id.
   */
  Observable<Currency> getCurrencyFromNamespaceId(NamespaceId namespaceId);
}
