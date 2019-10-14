/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedTransferTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.TransferTransactionBuilder;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The transfer transactions object contain data about transfers of mosaics and message to another
 * account.
 */
public class TransferTransaction extends Transaction {

    private final Optional<Address> recipient;
    private final List<Mosaic> mosaics;
    private final Message message;
    private final Optional<NamespaceId> namespaceId;

    /**
     * Constructor of the transfer transaction using the factory.
     *
     * @param factory the factory;
     */
    TransferTransaction(TransferTransactionFactory factory) {
        super(factory);
        this.recipient = factory.getRecipient();
        this.mosaics = factory.getMosaics();
        this.message = factory.getMessage();
        this.namespaceId = factory.getNamespaceId();
    }

    /**
     * Returns address of the recipient.
     *
     * @return recipient address
     */
    public Optional<Address> getRecipient() {
        return recipient;
    }

    /**
     * Gets namespace id alias for the address of the recipient.
     *
     * @return Namespace id.
     */
    public Optional<NamespaceId> getNamespaceId() {
        return namespaceId;
    }

    /**
     * Returns list of mosaic objects.
     *
     * @return List of {@link Mosaic}
     */
    public List<Mosaic> getMosaics() {
        return mosaics;
    }

    /**
     * Returns transaction message.
     *
     * @return Message.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Serialized the transfer transaction.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        final TransferTransactionBuilder txBuilder =
            TransferTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new UnresolvedAddressDto(getUnresolveAddressBuffer()),
                getMessageBuffer(),
                getUnresolvedMosaicArray());
        return txBuilder.serialize();
    }

    /**
     * Serialized the transfer transaction to embedded bytes.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedTransferTransactionBuilder txBuilder =
            EmbeddedTransferTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new UnresolvedAddressDto(getUnresolveAddressBuffer()),
                getMessageBuffer(),
                getUnresolvedMosaicArray());
        return txBuilder.serialize();
    }

    /**
     * Gets mosaic array.
     *
     * @return Mosaic array.
     */
    private ArrayList<UnresolvedMosaicBuilder> getUnresolvedMosaicArray() {
        // Create Mosaics
        final ArrayList<UnresolvedMosaicBuilder> unresolvedMosaicArrayList =
            new ArrayList<>(mosaics.size());
        //Sort mosaics first
        final List<Mosaic> sortedMosaics = mosaics.stream()
            .sorted((m1, m2) -> Long.compareUnsigned(m1.getId().getIdAsLong(), m2.getId().getIdAsLong()))
            .collect(Collectors.toList());

        for (final Mosaic mosaic : sortedMosaics) {
            final UnresolvedMosaicBuilder mosaicBuilder =
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(mosaic.getId().getId().longValue()),
                    new AmountDto(mosaic.getAmount().longValue()));
            unresolvedMosaicArrayList.add(mosaicBuilder);
        }
        return unresolvedMosaicArrayList;
    }

    /**
     * Gets message buffer.
     *
     * @return Message buffer.
     */
    private ByteBuffer getMessageBuffer() {
        final byte byteMessageType = (byte) message.getType().getValue();
        final byte[] bytePayload = message.getPayload().getBytes(StandardCharsets.UTF_8);
        final ByteBuffer messageBuffer =
            ByteBuffer.allocate(bytePayload.length + 1 /* for the message type */);
        messageBuffer.put(byteMessageType);
        messageBuffer.put(bytePayload);
        return messageBuffer;
    }

    /**
     * Gets unresolve address buffer.
     *
     * @return Unresolve address buffer
     */
    private ByteBuffer getUnresolveAddressBuffer() {

        return getRecipient().map(Address::getByteBuffer).orElseGet(
            () -> getNamespaceId()
                .map(this::getNamespaceIdAsUnresolveAddressBuffer).orElseThrow(
                    () -> new IllegalStateException("Address or namespace alias must be set."))
        );
    }

    /**
     * Gets the namespace id as unresolve address.
     *
     * @param namespaceId the namespace id.
     * @return Unresolve address buffer.
     */
    private ByteBuffer getNamespaceIdAsUnresolveAddressBuffer(NamespaceId namespaceId) {
        final ByteBuffer namespaceIdAlias = ByteBuffer.allocate(25);
        final byte firstByte = 0x01;
        namespaceIdAlias.order(ByteOrder.LITTLE_ENDIAN);
        namespaceIdAlias.put(firstByte);
        namespaceIdAlias.putLong(namespaceId.getIdAsLong());
        return namespaceIdAlias;
    }

}
