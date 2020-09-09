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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.reactivex.Observable;

/** Service used to resolve aliases. */
public interface AliasService {

  /**
   * This method returns the resolved {@link MosaicId} from the {@link UnresolvedMosaicId}. If the
   * {@link UnresolvedMosaicId} is an alias, it finds the real {@link MosaicId} by using the
   * namespace endpoints.
   *
   * @param unresolvedMosaicId the unresolvedMosaicId
   * @return {@link MosaicId} from the namespace endpoint if it's an alias or Mosaic Id if it's an
   *     mosaic id.
   */
  Observable<MosaicId> resolveMosaicId(UnresolvedMosaicId unresolvedMosaicId);

  /**
   * This method returns the resolved {@link Address} from the {@link UnresolvedAddress}. If the
   * {@link UnresolvedAddress} is an alias, it finds the real {@link Address} by using the namespace
   * endpoints.
   *
   * @param unresolvedAddress the unresolvedAddress
   * @return {@link Address} from the namespace endpoint if it's an alias or Address if it's an
   *     address.
   */
  Observable<Address> resolveAddress(UnresolvedAddress unresolvedAddress);
}
