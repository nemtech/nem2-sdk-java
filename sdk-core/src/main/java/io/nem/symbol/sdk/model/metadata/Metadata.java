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

import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.MetadataEntryBuilder;
import io.nem.symbol.catapult.builders.MetadataTypeDto;
import io.nem.symbol.catapult.builders.MetadataValueBuilder;
import io.nem.symbol.catapult.builders.ScopedMetadataKeyDto;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.MetadataTransaction;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * A mosaic describes an instance of a mosaic definition. Mosaics can be transferred by means of a
 * transfer transaction.
 */
public class Metadata implements Stored {

  /** The stored database. */
  private final String recordId;
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
  private final Object targetId;

  @SuppressWarnings("squid:S00107")
  public Metadata(
      String recordId,
      String compositeHash,
      Address sourceAddress,
      Address targetAddress,
      BigInteger scopedMetadataKey,
      MetadataType metadataType,
      String value,
      String targetId) {
    this.recordId = recordId;
    this.compositeHash = compositeHash;
    Validate.notNull(sourceAddress, "sourceAddress is required");
    Validate.notNull(targetAddress, "targetAddress is required");
    Validate.notNull(scopedMetadataKey, "scopedMetadataKey is required");
    Validate.notNull(scopedMetadataKey, "scopedMetadataKey is required");
    Validate.notNull(metadataType, "metadataType is required");
    this.sourceAddress = sourceAddress;
    this.targetAddress = targetAddress;
    this.scopedMetadataKey = scopedMetadataKey;
    this.metadataType = metadataType;
    this.value = value;
    this.targetId = resolveTargetId(targetId, metadataType);
  }

  private Object resolveTargetId(String targetId, MetadataType metadataType) {
    if (metadataType == MetadataType.ACCOUNT) {
      return null;
    }
    Validate.notNull(targetId, "targetId is required when metadata type is  " + metadataType);
    if (metadataType == MetadataType.NAMESPACE) {
      return MapperUtils.toNamespaceId(targetId);
    }
    if (metadataType == MetadataType.MOSAIC) {
      return MapperUtils.toMosaicId(targetId);
    }
    throw new IllegalArgumentException("Invalid metadata type " + metadataType);
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
    return Optional.ofNullable(targetId);
  }

  @Override
  public Optional<String> getRecordId() {
    return Optional.ofNullable(recordId);
  }

  /** @return serializes the state of this object. */
  public byte[] serialize() {

    AddressDto sourceAddress = SerializationUtils.toAddressDto(getSourceAddress());
    AddressDto targetAddress = SerializationUtils.toAddressDto(getTargetAddress());
    ScopedMetadataKeyDto scopedMetadataKey =
        new ScopedMetadataKeyDto(getScopedMetadataKey().longValue());
    long targetId = getTargetId().map(this::toTargetId).orElse(0L);
    MetadataTypeDto metadataType = MetadataTypeDto.rawValueOf((byte) getMetadataType().getValue());

    MetadataValueBuilder value = toMetadataValueBuilder(getValue());

    return MetadataEntryBuilder.create(
            sourceAddress, targetAddress, scopedMetadataKey, targetId, metadataType, value)
        .serialize();
  }

  public static MetadataValueBuilder toMetadataValueBuilder(String value) {
    ByteBuffer rawValue = ByteBuffer.wrap(MetadataTransaction.toByteArray(value));
    return MetadataValueBuilder.create(rawValue);
  }

  private Long toTargetId(Object o) {
    if (o instanceof MosaicId) {
      return ((MosaicId) o).getIdAsLong();
    }
    if (o instanceof NamespaceId) {
      return ((NamespaceId) o).getIdAsLong();
    }
    return 0L;
  }
}
