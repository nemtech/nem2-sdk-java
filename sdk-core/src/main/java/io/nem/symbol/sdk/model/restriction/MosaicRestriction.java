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

import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

/**
 * Super class of mosaic restriction objects.
 *
 * @param <T> then restriction item.
 */
public abstract class MosaicRestriction<T> implements Stored {

  private final String recordId;

  private final int version;

  /** composite hash */
  public final String compositeHash;

  /** Mosaic restriction entry type. */
  public final MosaicRestrictionEntryType entryType;

  /** Mosaic identifier. */
  private final MosaicId mosaicId;

  /** Mosaic restriction items */
  private final Map<BigInteger, T> restrictions;

  /**
   * constructor
   *
   * @param recordId the db id
   * @param version the versoin
   * @param compositeHash the composite hash
   * @param entryType the entry type
   * @param mosaicId the mosaic id
   * @param restrictions the restrictions
   */
  protected MosaicRestriction(
      String recordId,
      int version,
      String compositeHash,
      MosaicRestrictionEntryType entryType,
      MosaicId mosaicId,
      Map<BigInteger, T> restrictions) {
    this.recordId = recordId;
    this.version = version;
    this.compositeHash = compositeHash;
    this.entryType = entryType;
    this.mosaicId = mosaicId;
    this.restrictions = restrictions;
  }

  public String getCompositeHash() {
    return compositeHash;
  }

  public MosaicRestrictionEntryType getEntryType() {
    return entryType;
  }

  public MosaicId getMosaicId() {
    return mosaicId;
  }

  public Map<BigInteger, T> getRestrictions() {
    return restrictions;
  }

  public abstract byte[] serialize();

  public int getVersion() {
    return version;
  }

  @Override
  public Optional<String> getRecordId() {
    return Optional.ofNullable(recordId);
  }
}
