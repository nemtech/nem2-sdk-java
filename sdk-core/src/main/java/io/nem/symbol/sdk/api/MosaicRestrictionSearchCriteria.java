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
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import java.util.Objects;

/** The criteria used to search {@link io.nem.symbol.sdk.model.restriction.MosaicRestriction} */
public class MosaicRestrictionSearchCriteria
    extends SearchCriteria<MosaicRestrictionSearchCriteria> {

  /** Search restrictions for a specific mosaic; */
  private MosaicId mosaicId;

  /** Filter restriction by type. */
  private MosaicRestrictionEntryType entryType;

  /** Filter restriction by target address. */
  private Address targetAddress;

  public MosaicId getMosaicId() {
    return mosaicId;
  }

  public void setMosaicId(MosaicId mosaicId) {
    this.mosaicId = mosaicId;
  }

  public MosaicRestrictionEntryType getEntryType() {
    return entryType;
  }

  public MosaicRestrictionSearchCriteria mosaicId(MosaicId mosaicId) {
    this.mosaicId = mosaicId;
    return this;
  }

  public void setEntryType(MosaicRestrictionEntryType entryType) {
    this.entryType = entryType;
  }

  public MosaicRestrictionSearchCriteria entryType(MosaicRestrictionEntryType entryType) {
    this.entryType = entryType;
    return this;
  }

  public Address getTargetAddress() {
    return targetAddress;
  }

  public void setTargetAddress(Address targetAddress) {
    this.targetAddress = targetAddress;
  }

  public MosaicRestrictionSearchCriteria targetAddress(Address targetAddress) {
    this.targetAddress = targetAddress;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    MosaicRestrictionSearchCriteria that = (MosaicRestrictionSearchCriteria) o;
    return Objects.equals(mosaicId, that.mosaicId)
        && entryType == that.entryType
        && Objects.equals(targetAddress, that.targetAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), mosaicId, entryType, targetAddress);
  }
}
