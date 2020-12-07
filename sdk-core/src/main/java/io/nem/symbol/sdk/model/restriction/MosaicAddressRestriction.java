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

import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.AddressKeyValueBuilder;
import io.nem.symbol.catapult.builders.AddressKeyValueSetBuilder;
import io.nem.symbol.catapult.builders.MosaicAddressRestrictionEntryBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.MosaicRestrictionEntryBuilder;
import io.nem.symbol.catapult.builders.MosaicRestrictionKeyDto;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/** Mosaic address restriction structure describes restriction information for an mosaic. */
public class MosaicAddressRestriction extends MosaicRestriction<BigInteger> {

  /** Target address */
  private final Address targetAddress;

  /**
   * constructor
   *
   * @param recordId the db id
   * @param version the version
   * @param compositeHash the restriction composite hash
   * @param entryType the entry type
   * @param mosaicId the mosaic id
   * @param targetAddress the target address.
   * @param restrictions the restrictions of the mosaic.
   */
  public MosaicAddressRestriction(
      String recordId,
      int version,
      String compositeHash,
      MosaicRestrictionEntryType entryType,
      MosaicId mosaicId,
      Address targetAddress,
      Map<BigInteger, BigInteger> restrictions) {
    super(recordId, version, compositeHash, entryType, mosaicId, restrictions);
    this.targetAddress = targetAddress;
  }

  public Address getTargetAddress() {
    return targetAddress;
  }

  /** @return serializes the state of this object. */
  public byte[] serialize() {
    MosaicIdDto mosaicId = SerializationUtils.toMosaicIdDto(getMosaicId());
    AddressDto targetAddress = SerializationUtils.toAddressDto(getTargetAddress());
    AddressKeyValueSetBuilder restrictions =
        AddressKeyValueSetBuilder.create(
            getRestrictions().entrySet().stream()
                .sorted(Comparator.comparing(Entry::getKey))
                .map(this::toAddressKeyValueBuilder)
                .collect(Collectors.toList()));
    MosaicAddressRestrictionEntryBuilder entry =
        MosaicAddressRestrictionEntryBuilder.create(mosaicId, targetAddress, restrictions);
    return MosaicRestrictionEntryBuilder.createAddress((short) getVersion(), entry).serialize();
  }

  private AddressKeyValueBuilder toAddressKeyValueBuilder(Entry<BigInteger, BigInteger> entry) {
    MosaicRestrictionKeyDto key = new MosaicRestrictionKeyDto(entry.getKey().longValue());
    return AddressKeyValueBuilder.create(key, entry.getValue().longValue());
  }
}
