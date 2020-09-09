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
package io.nem.symbol.sdk.model.receipt;

import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.AddressResolutionEntryBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.MosaicResolutionEntryBuilder;
import io.nem.symbol.catapult.builders.ReceiptSourceBuilder;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;

public class ResolutionEntry<T> {

  private final T resolved;
  private final ReceiptSource receiptSource;
  private final ReceiptType type;

  /**
   * Private constructor
   *
   * @param resolved A resolved address or resolved mosaicId alias (MosaicId| Address).
   * @param receiptSource The receipt source.
   */
  ResolutionEntry(T resolved, ReceiptSource receiptSource, ReceiptType type) {
    this.receiptSource = receiptSource;
    this.resolved = resolved;
    this.type = type;
    this.validateReceiptType(type);
    this.validateResolvedType();
  }

  /**
   * It creates a {@link ResolutionEntry} of an {@link Address}
   *
   * @param resolved the address
   * @param receiptSource the recipient source
   * @return the {@link ResolutionEntry}.
   */
  public static ResolutionEntry<Address> forAddress(Address resolved, ReceiptSource receiptSource) {
    return new ResolutionEntry<>(resolved, receiptSource, ReceiptType.ADDRESS_ALIAS_RESOLUTION);
  }

  /**
   * It creates a {@link ResolutionEntry} of an {@link MosaicId}
   *
   * @param resolved the {@link MosaicId}
   * @param receiptSource the recipient source
   * @return the {@link ResolutionEntry}.
   */
  public static ResolutionEntry<MosaicId> forMosaicId(
      MosaicId resolved, ReceiptSource receiptSource) {
    return new ResolutionEntry<>(resolved, receiptSource, ReceiptType.MOSAIC_ALIAS_RESOLUTION);
  }

  /**
   * Returns the resolution
   *
   * @return resolution (MosaicId| Address)
   */
  public T getResolved() {
    return this.resolved;
  }

  /**
   * Returns the receipt type
   *
   * @return receipt type
   */
  public ReceiptType getType() {
    return this.type;
  }

  /**
   * Returns receipt source
   *
   * @return receipt source
   */
  public ReceiptSource getReceiptSource() {
    return this.receiptSource;
  }

  /** Validate receipt type */
  private void validateReceiptType(ReceiptType type) {
    if (!ReceiptType.RESOLUTION_STATEMENT.contains(type)) {
      throw new IllegalArgumentException("Receipt type: [" + type.name() + "] is not valid.");
    }
  }

  /**
   * Serialize receipt and returns receipt bytes
   *
   * @return receipt bytes
   */
  public byte[] serialize() {
    ReceiptSourceBuilder receiptSourceBuilder =
        ReceiptSourceBuilder.create(
            (int) getReceiptSource().getPrimaryId(), (int) getReceiptSource().getSecondaryId());
    Class<?> resolutionClass = this.resolved.getClass();
    if (Address.class.isAssignableFrom(resolutionClass)) {
      AddressDto addressBuilder =
          new AddressDto(SerializationUtils.fromAddressToByteBuffer((Address) getResolved()));
      AddressResolutionEntryBuilder builder =
          AddressResolutionEntryBuilder.create(receiptSourceBuilder, addressBuilder);
      return builder.serialize();
    } else {
      MosaicIdDto mosaicIdDto = SerializationUtils.toMosaicIdDto((MosaicId) getResolved());
      MosaicResolutionEntryBuilder builder =
          MosaicResolutionEntryBuilder.create(receiptSourceBuilder, mosaicIdDto);
      return builder.serialize();
    }
  }

  /** Validate resolved type (MosaicId | Address) */
  private void validateResolvedType() {
    validateType(ReceiptType.ADDRESS_ALIAS_RESOLUTION, Address.class);
    validateType(ReceiptType.MOSAIC_ALIAS_RESOLUTION, MosaicId.class);
  }

  /** Validate resolved type (MosaicId | Address) */
  private void validateType(ReceiptType givenRecipientType, Class<?> expectedType) {
    if (!expectedType.isAssignableFrom(this.resolved.getClass())
        && getType() == givenRecipientType) {
      throw new IllegalArgumentException(
          "Resolved type: ["
              + expectedType.getName()
              + "] is not valid for this ResolutionEntry of type ["
              + getType()
              + "]");
    }
  }
}
