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
import io.nem.symbol.catapult.builders.BalanceTransferReceiptBuilder;
import io.nem.symbol.catapult.builders.MosaicBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.ReceiptTypeDto;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.AddressAlias;
import java.math.BigInteger;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

public class BalanceTransferReceipt extends Receipt {

  private final Address senderAddress;
  private final Address recipientAddress;
  private final MosaicId mosaicId;
  private final BigInteger amount;

  /**
   * Constructor
   *
   * @param senderAddress Sender's Address
   * @param recipientAddress Recipient Address
   * @param mosaicId Mosaic Id
   * @param amount Amount
   * @param type Receipt Type
   * @param version Receipt Version
   * @param size Receipt Size
   */
  public BalanceTransferReceipt(
      Address senderAddress,
      Address recipientAddress,
      MosaicId mosaicId,
      BigInteger amount,
      ReceiptType type,
      ReceiptVersion version,
      Optional<Integer> size) {
    super(type, version, size);
    Validate.notNull(senderAddress, "sender must not be null");
    Validate.notNull(recipientAddress, "recipient must not be null");
    Validate.notNull(amount, "amount must not be null");
    Validate.notNull(mosaicId, "mosaicId must not be null");
    this.senderAddress = senderAddress;
    this.recipientAddress = recipientAddress;
    this.amount = amount;
    this.mosaicId = mosaicId;
    this.validateRecipientType();
    this.validateReceiptType(type);
  }

  /**
   * Constructor BalanceTransferReceipt
   *
   * @param senderAddress Sender's Public Account
   * @param recipientAddress Recipient Address
   * @param mosaicId Mosaic Id
   * @param amount Amount
   * @param type Receipt Type
   * @param version Receipt Version
   */
  public BalanceTransferReceipt(
      Address senderAddress,
      Address recipientAddress,
      MosaicId mosaicId,
      BigInteger amount,
      ReceiptType type,
      ReceiptVersion version) {
    this(senderAddress, recipientAddress, mosaicId, amount, type, version, Optional.empty());
  }

  /**
   * Returns sender's Public Account
   *
   * @return sender's Public Account
   */
  public Address getSenderAddress() {
    return this.senderAddress;
  }

  /**
   * Returns recipient's address or addressAlias
   *
   * @return recipient's address or addressAlias
   */
  public Address getRecipientAddress() {
    return this.recipientAddress;
  }

  /**
   * Returns mosaicId
   *
   * @return account
   */
  public MosaicId getMosaicId() {
    return this.mosaicId;
  }

  /**
   * Returns amount
   *
   * @return amount
   */
  public BigInteger getAmount() {
    return this.amount;
  }

  /**
   * Serialize receipt and returns receipt bytes
   *
   * @return receipt bytes
   */
  @Override
  public byte[] serialize() {
    final short version = (short) getVersion().getValue();
    final ReceiptTypeDto type = ReceiptTypeDto.rawValueOf((short) getType().getValue());
    final MosaicBuilder mosaic =
        MosaicBuilder.create(
            new MosaicIdDto(getMosaicId().getIdAsLong()), SerializationUtils.toAmount(getAmount()));
    final AddressDto senderAddress = SerializationUtils.toAddressDto(getSenderAddress());
    final AddressDto recipientAddress = SerializationUtils.toAddressDto(getRecipientAddress());
    return BalanceTransferReceiptBuilder.create(
            version, type, mosaic, senderAddress, recipientAddress)
        .serialize();
  }

  /**
   * Validate receipt type
   *
   * @return void
   */
  private void validateReceiptType(ReceiptType type) {
    if (!ReceiptType.BALANCE_TRANSFER.contains(type)) {
      throw new IllegalArgumentException("Receipt type: [" + type.name() + "] is not valid.");
    }
  }

  /**
   * Validate recipient type (MosaicId | NamespaceId)
   *
   * @return void
   */
  private void validateRecipientType() {
    Class recipientClass = this.recipientAddress.getClass();
    if (!Address.class.isAssignableFrom(recipientClass)
        && !AddressAlias.class.isAssignableFrom(recipientClass)) {
      throw new IllegalArgumentException(
          "Recipient type: ["
              + recipientClass.getName()
              + "] is not valid for BalanceTransferReceipt");
    }
  }
}
