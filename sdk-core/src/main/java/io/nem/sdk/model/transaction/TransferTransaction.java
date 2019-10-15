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
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.mosaic.Mosaic;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The transfer transactions object contain data about transfers of mosaics and message to another
 * account.
 */
public class TransferTransaction extends Transaction {

    private final UnresolvedAddress recipient;
    private final List<Mosaic> mosaics;
    private final Message message;

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
    }

    /**
     * Returns address of the recipient.
     *
     * @return recipient address
     */
    public UnresolvedAddress getRecipient() {
        return recipient;
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
            .sorted(Comparator.comparing(m -> m.getId().getId()))
            .collect(Collectors.toList());

        for (final Mosaic mosaic : sortedMosaics) {
            final UnresolvedMosaicBuilder mosaicBuilder =
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(mosaic.getId().getIdAsLong()),
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

        return getRecipient().getByteBuffer();
    }



}
