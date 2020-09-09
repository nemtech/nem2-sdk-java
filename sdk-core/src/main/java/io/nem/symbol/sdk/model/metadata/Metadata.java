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
package io.nem.symbol.sdk.model.metadata;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.Optional;

/**
 * A mosaic describes an instance of a mosaic definition. Mosaics can be transferred by means of a
 * transfer transaction.
 */
public class Metadata implements Stored {

  /** The stored database. */
  private final Optional<String> recordId;
  /** The composite hash */
  private final String compositeHash;
  /** The metadata source address */
  private final Address sourceAddress;

  /** The metadata target address */
  private final Address targetAddress;

  /** The key scoped to source, target and type */
  private final BigInteger scopedMetadataKey;

  /** The metadata type */
  private final MetadataType metadataType;

  /** The metadata value */
  private final String value;

  /**
   * The target {@link MosaicId} (when metadata type is MOSAIC)
   *
   * <p>or
   *
   * <p>{@link NamespaceId} (when metadata type is NAMESPACE)
   */
  private final Optional<Object> targetId;

  @SuppressWarnings("squid:S00107")
  public Metadata(
      String recordId,
      String compositeHash,
      Address sourceAddress,
      Address targetAddress,
      BigInteger scopedMetadataKey,
      MetadataType metadataType,
      String value,
      Optional<String> targetId) {
    this.recordId = Optional.ofNullable(recordId);
    this.compositeHash = compositeHash;
    this.sourceAddress = sourceAddress;
    this.targetAddress = targetAddress;
    this.scopedMetadataKey = scopedMetadataKey;
    this.metadataType = metadataType;
    this.value = value;
    this.targetId = resolveTargetId(targetId, metadataType);
  }

  private Optional<Object> resolveTargetId(Optional<String> targetId, MetadataType metadataType) {
    if (!targetId.isPresent() && metadataType == MetadataType.ACCOUNT) {
      return Optional.empty();
    }
    if (metadataType == MetadataType.NAMESPACE) {
      return targetId.map(MapperUtils::toNamespaceId);
    }
    if (metadataType == MetadataType.MOSAIC) {
      return targetId.map(MapperUtils::toMosaicId);
    }
    return Optional.empty();
  }

  public String getCompositeHash() {
    return compositeHash;
  }

  public Address getSourceAddress() {
    return sourceAddress;
  }

  public Address getTargetAddress() {
    return targetAddress;
  }

  public BigInteger getScopedMetadataKey() {
    return scopedMetadataKey;
  }

  public MetadataType getMetadataType() {
    return metadataType;
  }

  public String getValue() {
    return value;
  }

  public Optional<Object> getTargetId() {
    return targetId;
  }

  @Override
  public Optional<String> getRecordId() {
    return recordId;
  }
}
