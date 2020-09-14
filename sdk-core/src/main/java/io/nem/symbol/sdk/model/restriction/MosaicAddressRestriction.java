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
package io.nem.symbol.sdk.model.restriction;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.Map;

/** Mosaic address restriction structure describes restriction information for an mosaic. */
public class MosaicAddressRestriction extends MosaicRestriction<BigInteger> {

  /** Target address */
  private final Address targetAddress;

  /**
   * constructor
   *
   * @param compositeHash the restriction composite hash
   * @param entryType the entry type
   * @param mosaicId the mosaic id
   * @param targetAddress the target address.
   * @param restrictions the restrictions of the mosaic.
   */
  public MosaicAddressRestriction(
      String compositeHash,
      MosaicRestrictionEntryType entryType,
      MosaicId mosaicId,
      Address targetAddress,
      Map<BigInteger, BigInteger> restrictions) {
    super(compositeHash, entryType, mosaicId, restrictions);
    this.targetAddress = targetAddress;
  }

  public Address getTargetAddress() {
    return targetAddress;
  }
}
