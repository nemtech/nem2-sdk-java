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

import io.nem.symbol.catapult.builders.GlobalKeyValueBuilder;
import io.nem.symbol.catapult.builders.GlobalKeyValueSetBuilder;
import io.nem.symbol.catapult.builders.MosaicGlobalRestrictionEntryBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.MosaicRestrictionEntryBuilder;
import io.nem.symbol.catapult.builders.MosaicRestrictionKeyDto;
import io.nem.symbol.catapult.builders.MosaicRestrictionTypeDto;
import io.nem.symbol.catapult.builders.RestrictionRuleBuilder;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/** Mosaic global restriction structure describes restriction information for an mosaic. */
public class MosaicGlobalRestriction extends MosaicRestriction<MosaicGlobalRestrictionItem> {

  /**
   * constructor
   *
   * @param recordId the db id
   * @param version the version
   * @param compositeHash the restriction composite hash
   * @param entryType the entry type
   * @param mosaicId the mosaic id
   * @param restrictions the restrictions of the mosaic.
   */
  public MosaicGlobalRestriction(
      String recordId,
      int version,
      String compositeHash,
      MosaicRestrictionEntryType entryType,
      MosaicId mosaicId,
      Map<BigInteger, MosaicGlobalRestrictionItem> restrictions) {
    super(recordId, version, compositeHash, entryType, mosaicId, restrictions);
  }

  /** @return serializes the state of this object. */
  public byte[] serialize() {
    MosaicIdDto mosaicId = SerializationUtils.toMosaicIdDto(getMosaicId());
    GlobalKeyValueSetBuilder restrictions =
        GlobalKeyValueSetBuilder.create(
            getRestrictions().entrySet().stream()
                .sorted(Comparator.comparing(Entry::getKey))
                .map(this::toGlobalKeyValueSetBuilder)
                .collect(Collectors.toList()));
    MosaicGlobalRestrictionEntryBuilder entry =
        MosaicGlobalRestrictionEntryBuilder.create(mosaicId, restrictions);
    return MosaicRestrictionEntryBuilder.createGlobal((short) getVersion(), entry).serialize();
  }

  private GlobalKeyValueBuilder toGlobalKeyValueSetBuilder(
      Entry<BigInteger, MosaicGlobalRestrictionItem> entry) {
    MosaicRestrictionKeyDto key = new MosaicRestrictionKeyDto(entry.getKey().longValue());
    MosaicIdDto referenceMosaicId =
        SerializationUtils.toMosaicIdDto(entry.getValue().getReferenceMosaicId());
    long restrictionValue = entry.getValue().getRestrictionValue().longValue();
    MosaicRestrictionTypeDto restrictionType =
        MosaicRestrictionTypeDto.rawValueOf(entry.getValue().getRestrictionType().getValue());
    RestrictionRuleBuilder restrictionRule =
        RestrictionRuleBuilder.create(referenceMosaicId, restrictionValue, restrictionType);
    return GlobalKeyValueBuilder.create(key, restrictionRule);
  }
}
