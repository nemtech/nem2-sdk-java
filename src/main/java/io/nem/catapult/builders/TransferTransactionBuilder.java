/**
*** Copyright (c) 2016-present,
*** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
***
*** This file is part of Catapult.
***
*** Catapult is free software: you can redistribute it and/or modify
*** it under the terms of the GNU Lesser General Public License as published by
*** the Free Software Foundation, either version 3 of the License, or
*** (at your option) any later version.
***
*** Catapult is distributed in the hope that it will be useful,
*** but WITHOUT ANY WARRANTY; without even the implied warranty of
*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*** GNU Lesser General Public License for more details.
***
*** You should have received a copy of the GNU Lesser General Public License
*** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
**/

package io.nem.catapult.builders;

import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.model.mosaic.Mosaic;

import java.io.DataInput;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/** Binary layout for a non-embedded transfer transaction. */
public final class TransferTransactionBuilder extends TransactionBuilder {
    /** Transfer transaction body. */
    private final TransferTransactionBodyBuilder transferTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected TransferTransactionBuilder(final DataInput stream) {
        super(stream);
        this.transferTransactionBody = TransferTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction maxFee.
     * @param deadline Transaction deadline.
     * @param recipient Transaction recipient.
     * @param message Transaction message.
     * @param mosaics Attached mosaics.
     */
    protected TransferTransactionBuilder(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final UnresolvedAddressDto recipient, final ByteBuffer message, final ArrayList<UnresolvedMosaicBuilder> mosaics) {
        super(signature, signer, version, type, fee, deadline);
        this.transferTransactionBody = TransferTransactionBodyBuilder.create(recipient, message, mosaics);
    }

    /**
     * Creates an instance of TransferTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction maxFee.
     * @param deadline Transaction deadline.
     * @param recipient Transaction recipient.
     * @param message Transaction message.
     * @param mosaics Attached mosaics.
     * @return Instance of TransferTransactionBuilder.
     */
    public static TransferTransactionBuilder create(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final UnresolvedAddressDto recipient, final ByteBuffer message, final ArrayList<UnresolvedMosaicBuilder> mosaics) {
        return new TransferTransactionBuilder(signature, signer, version, type, fee, deadline, recipient, message, mosaics);
    }

    /**
     * Creates an instance of TransferTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction maxFee.
     * @param deadline Transaction deadline.
     * @param recipient Transaction recipient.
     * @param message Transaction message.
     * @param mosaics Attached mosaics.
     * @return Instance of TransferTransactionBuilder.
     */
    public static TransferTransactionBuilder create(final String signature, final String signer, final short version, final short type, final long fee, final long deadline, final String recipient, final String message, final List<Mosaic> mosaics) {
        SignatureDto signatureDto = SignatureDto.create(signature);
        KeyDto signerDto = KeyDto.create(signer);
        EntityTypeDto entityTypeDto = EntityTypeDto.rawValueOf(type);
        AmountDto amountDto = new AmountDto(fee);
        TimestampDto timestampDto = new TimestampDto(deadline);
        UnresolvedAddressDto unresolvedAddressDto = UnresolvedAddressDto.create(recipient);
        ByteBuffer messageBuffer = StringEncoder.getByteBuffer(message);
        ArrayList<UnresolvedMosaicBuilder> unresolvedMosaicBuilders =
                (ArrayList<UnresolvedMosaicBuilder>) mosaics.stream()
                        .map(Mosaic::getUnresolvedMosaicBuilder)
                        .collect(toList());

        return new TransferTransactionBuilder(signatureDto, signerDto, version, entityTypeDto, amountDto, timestampDto, unresolvedAddressDto, messageBuffer, unresolvedMosaicBuilders);
    }

    public static TransferTransactionBuilder create(final String signature, final String signer, final byte transactionVersion, final byte networkType, final short type, final long fee, final long deadline, final String recipient, final String message, final List<Mosaic> mosaics) {
        short version = TransactionBuilder.getVersion(transactionVersion, networkType);
        return TransferTransactionBuilder.create(signature, signer, version, type, fee, deadline, recipient, message, mosaics);
    }

        /**
         * Gets transaction recipient.
         *
         * @return Transaction recipient.
         */
    public UnresolvedAddressDto getRecipient() {
        return this.transferTransactionBody.getRecipient();
    }

    /**
     * Gets transaction message.
     *
     * @return Transaction message.
     */
    public ByteBuffer getMessage() {
        return this.transferTransactionBody.getMessage();
    }

    /**
     * Gets transaction message as plain text.
     *
     * @return plain text message.
     */
    public String getMessageAsString() {
        return this.transferTransactionBody.getMessageAsString();
    }

    /**
     * Gets attached mosaics.
     *
     * @return Attached mosaics.
     */
    public ArrayList<UnresolvedMosaicBuilder> getMosaics() {
        return this.transferTransactionBody.getMosaics();
    }

    /**
     * Gets attached mosaics as a string.
     *
     * @return String.
     */
    public String getMosaicsAsString() {
        return this.transferTransactionBody.getMosaicsAsString();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.transferTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of TransferTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of TransferTransactionBuilder.
     */
    public static TransferTransactionBuilder loadFromBinary(final DataInput stream) {
        return new TransferTransactionBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] superBytes = super.serialize();
            dataOutputStream.write(superBytes, 0, superBytes.length);
            final byte[] transferTransactionBodyBytes = this.transferTransactionBody.serialize();
            dataOutputStream.write(transferTransactionBodyBytes, 0, transferTransactionBodyBytes.length);
        });
    }

    public String asString() {
        StringBuilder sb = new StringBuilder(super.asString());
        sb.append("\nRecipient: "+this.getRecipient().asString());
        sb.append("\nMessage: "+this.getMessageAsString());
        sb.append("\nMosaics[MosaicId,Amount]: "+this.getMosaicsAsString());

        return sb.toString();
    }
}