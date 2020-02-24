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
import io.nem.symbol.catapult.builders.AmountDto;
import io.nem.symbol.catapult.builders.BalanceTransferReceiptBuilder;
import io.nem.symbol.catapult.builders.KeyDto;
import io.nem.symbol.catapult.builders.MosaicBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.ReceiptTypeDto;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.AddressAlias;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;

public class BalanceTransferReceipt extends Receipt {

    private final PublicAccount sender;
    private final UnresolvedAddress recipient;
    private final MosaicId mosaicId;
    private final BigInteger amount;

    /**
     * Constructor
     *
     * @param sender Sender's Public Account
     * @param recipient Recipient (Address | AddressAlias)
     * @param mosaicId Mosaic Id
     * @param amount Amount
     * @param type Receipt Type
     * @param version Receipt Version
     * @param size Receipt Size
     */
    public BalanceTransferReceipt(
        PublicAccount sender,
        UnresolvedAddress recipient,
        MosaicId mosaicId,
        BigInteger amount,
        ReceiptType type,
        ReceiptVersion version,
        Optional<Integer> size) {
        super(type, version, size);
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.mosaicId = mosaicId;
        this.validateRecipientType();
        this.validateReceiptType(type);
    }

    /**
     * Constructor BalanceTransferReceipt
     *
     * @param sender Sender's Public Account
     * @param recipient Recipient (Address | AddressAlias)
     * @param mosaicId Mosaic Id
     * @param amount Amount
     * @param type Receipt Type
     * @param version Receipt Version
     */
    public BalanceTransferReceipt(
        PublicAccount sender,
        UnresolvedAddress recipient,
        MosaicId mosaicId,
        BigInteger amount,
        ReceiptType type,
        ReceiptVersion version) {
        this(sender,
            recipient,
            mosaicId,
            amount,
            type,
            version, Optional.empty());
    }

    /**
     * Returns sender's Public Account
     *
     * @return sender's Public Account
     */
    public PublicAccount getSender() {
        return this.sender;
    }

    /**
     * Returns recipient's address or addressAlias
     *
     * @return recipient's address or addressAlias
     */
    public UnresolvedAddress getRecipient() {
        return this.recipient;
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
        ByteBuffer recipientBytes = SerializationUtils
            .fromUnresolvedAddressToByteBuffer(getRecipient(),
                getSender().getAddress().getNetworkType());

        final short version = (short) getVersion().getValue();
        final ReceiptTypeDto type = ReceiptTypeDto.rawValueOf((short) getType().getValue());
        final MosaicBuilder mosaic = MosaicBuilder
            .create(new MosaicIdDto(getMosaicId().getIdAsLong()),
                new AmountDto(getAmount().longValue()));

        final KeyDto senderPublicKey = SerializationUtils.toKeyDto(getSender().getPublicKey());

        final AddressDto recipientAddress = new AddressDto(recipientBytes);

        return BalanceTransferReceiptBuilder
            .create(version, type, mosaic, senderPublicKey, recipientAddress).serialize();


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
        Class recipientClass = this.recipient.getClass();
        if (!Address.class.isAssignableFrom(recipientClass)
            && !AddressAlias.class.isAssignableFrom(recipientClass)) {
            throw new IllegalArgumentException(
                "Recipient type: ["
                    + recipientClass.getName()
                    + "] is not valid for BalanceTransferReceipt");
        }
    }
}
