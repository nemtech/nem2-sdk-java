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

package io.nem.sdk.infrastructure;

import io.nem.catapult.builders.AggregateTransactionBuilder;
import io.nem.catapult.builders.EmbeddedTransactionBuilder;
import io.nem.catapult.builders.TransactionBuilder;
import io.nem.catapult.builders.TransferTransactionBodyBuilder;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.BinarySerialization;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.Message;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BinarySerializationImpl implements BinarySerialization {

    private final Map<TransactionType, TransactionDeserializer> deserializers = new HashMap<>();

    public BinarySerializationImpl() {
        register(new TransferTransactionDeserializer());
        register(new AggregateTransactionDeserializer(TransactionType.AGGREGATE_COMPLETE, this));
        register(new AggregateTransactionDeserializer(TransactionType.AGGREGATE_BONDED, this));

    }

    private void register(TransactionDeserializer deserializer) {
        if (deserializers.put(deserializer.getTransactionType(), deserializer) != null) {
            throw new IllegalArgumentException(
                "TransactionDeserializer for type " + deserializer.getTransactionType()
                    + " was already registered!");
        }
    }

    private TransactionDeserializer resolveMapper(TransactionType transactionType) {
        TransactionDeserializer mapper = deserializers.get(transactionType);
        if (mapper == null) {
            throw new UnsupportedOperationException(
                "Unimplemented Transaction type " + transactionType);
        }
        return mapper;
    }

    @Override
    public byte[] serialize(Transaction transaction) {
        return transaction.serialize();
    }

    @Override
    public Transaction deserialize(byte[] payload) {

        DataInput stream = toDataInput(payload);
        TransactionBuilder builder = TransactionBuilder.loadFromBinary(stream);
        TransactionType transactionType = TransactionType
            .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
        int networkVersion = SerializationUtils.shortToUnsignedInt(builder.getVersion());
        NetworkType networkType = MapperUtils.extractNetworkType(networkVersion);
        int version = MapperUtils.extractTransactionVersion(networkVersion);
        Deadline deadline = new Deadline(BigInteger.valueOf(builder.getDeadline().getTimestamp()));

        TransactionFactory<?> factory = resolveMapper(transactionType)
            .fromStream(networkType, stream);
        factory.signer(SerializationUtils.toPublicAccount(builder.getSigner(), networkType));
        factory.version(version);
        factory.maxFee(BigInteger.valueOf(builder.getFee().getAmount()));
        factory.deadline(deadline);
        return factory.build();
    }

    public Transaction deserializeEmbedded(ByteBuffer payload) {

        DataInput stream = new DataInputStream(new ByteBufferBackedInputStream(payload));
        EmbeddedTransactionBuilder builder = EmbeddedTransactionBuilder.loadFromBinary(stream);
        TransactionType transactionType = TransactionType
            .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
        int networkVersion = SerializationUtils.shortToUnsignedInt(builder.getVersion());
        NetworkType networkType = MapperUtils.extractNetworkType(networkVersion);
        int version = MapperUtils.extractTransactionVersion(networkVersion);

        TransactionFactory<?> factory = resolveMapper(transactionType)
            .fromStream(networkType, stream);
        factory.signer(SerializationUtils.toPublicAccount(builder.getSigner(), networkType));
        factory.version(version);
        return factory.build();
    }

    private DataInput toDataInput(byte[] payload) {
        return new DataInputStream(new ByteArrayInputStream(payload));
    }

    interface TransactionDeserializer {

        TransactionType getTransactionType();

        TransactionFactory fromStream(NetworkType networkType, DataInput stream);

    }

    public static class TransferTransactionDeserializer implements TransactionDeserializer {


        @Override
        public TransactionType getTransactionType() {
            return TransactionType.TRANSFER;
        }

        @Override
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInput stream) {
            TransferTransactionBodyBuilder builder = TransferTransactionBodyBuilder
                .loadFromBinary(stream);
            byte[] messageArray = builder.getMessage().array();
            int messageType = messageArray[0];
            String messageHex = ConvertUtils.toHex(messageArray).substring(2);
            Address recipient = MapperUtils
                .toAddressFromUnresolved(
                    ConvertUtils.toHex(builder.getRecipient().getUnresolvedAddress().array()));
            List<Mosaic> mosaics = builder.getMosaics().stream()
                .map(SerializationUtils::toMosaic)
                .collect(Collectors.toList());
            Message message = new PlainMessage(messageHex);
            return TransferTransactionFactory.create(networkType,
                recipient, mosaics, message);
        }

    }

    public static abstract class AbstractTransactionDeserializer implements
        TransactionDeserializer {

    }

    public static class AggregateTransactionDeserializer implements
        TransactionDeserializer {


        private final TransactionType transactionType;

        private final BinarySerializationImpl transactionSerialization;

        public AggregateTransactionDeserializer(
            TransactionType transactionType,
            BinarySerializationImpl transactionSerialization) {
            this.transactionType = transactionType;
            this.transactionSerialization = transactionSerialization;
        }

        @Override
        public TransactionType getTransactionType() {
            return transactionType;
        }

        @Override
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInput stream) {
            AggregateTransactionBuilder builder = AggregateTransactionBuilder
                .loadFromBinary(stream);

            List<Transaction> transactions = new ArrayList<>();
            ByteBuffer transactionByteByteBuffer = builder.getTransactions();
            while (transactionByteByteBuffer.hasRemaining()) {
                transactions
                    .add(transactionSerialization.deserializeEmbedded(transactionByteByteBuffer));
            }

            String[] consignatureArray = ConvertUtils.toHex(builder.getCosignatures().array())
                .split("/.{1,192}/g");

            List<AggregateTransactionCosignature> cosignatures = Arrays.stream(consignatureArray)
                .map(s -> {
                    PublicAccount signer = PublicAccount
                        .createFromPublicKey(s.substring(0, 64), networkType);
                    String cosignature = s.substring(64, 192);
                    return new AggregateTransactionCosignature(cosignature, signer);
                }).collect(Collectors.toList());

            return AggregateTransactionFactory
                .create(getTransactionType(), networkType, transactions, cosignatures);
        }

    }
}
