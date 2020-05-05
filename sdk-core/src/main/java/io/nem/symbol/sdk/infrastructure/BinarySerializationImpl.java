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

package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.catapult.builders.AccountAddressRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountLinkTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountMetadataTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountMosaicRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountOperationRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountRestrictionFlagsDto;
import io.nem.symbol.catapult.builders.AddressAliasTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.AggregateTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AliasActionDto;
import io.nem.symbol.catapult.builders.AmountDto;
import io.nem.symbol.catapult.builders.BlockDurationDto;
import io.nem.symbol.catapult.builders.CosignatureBuilder;
import io.nem.symbol.catapult.builders.EmbeddedTransactionBuilder;
import io.nem.symbol.catapult.builders.EntityTypeDto;
import io.nem.symbol.catapult.builders.Hash256Dto;
import io.nem.symbol.catapult.builders.HashLockTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.KeyDto;
import io.nem.symbol.catapult.builders.LinkActionDto;
import io.nem.symbol.catapult.builders.LockHashAlgorithmDto;
import io.nem.symbol.catapult.builders.MosaicAddressRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicAliasTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicDefinitionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicFlagsDto;
import io.nem.symbol.catapult.builders.MosaicGlobalRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.MosaicMetadataTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicNonceDto;
import io.nem.symbol.catapult.builders.MosaicRestrictionTypeDto;
import io.nem.symbol.catapult.builders.MosaicSupplyChangeActionDto;
import io.nem.symbol.catapult.builders.MosaicSupplyChangeTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MultisigAccountModificationTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.NamespaceIdDto;
import io.nem.symbol.catapult.builders.NamespaceMetadataTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.NamespaceRegistrationTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.NetworkTypeDto;
import io.nem.symbol.catapult.builders.SecretLockTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.SecretProofTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.Serializer;
import io.nem.symbol.catapult.builders.SignatureDto;
import io.nem.symbol.catapult.builders.TimestampDto;
import io.nem.symbol.catapult.builders.TransactionBuilder;
import io.nem.symbol.catapult.builders.TransactionBuilderFactory;
import io.nem.symbol.catapult.builders.TransferTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.UnresolvedAddressDto;
import io.nem.symbol.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.symbol.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.api.BinarySerialization;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.message.MessageType;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountLinkAction;
import io.nem.symbol.sdk.model.transaction.AccountLinkTransaction;
import io.nem.symbol.sdk.model.transaction.AccountLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransaction;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.symbol.sdk.model.transaction.MetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;


/**
 * Implementation of BinarySerialization. It uses the catbuffer generated builders to deserialize an object.
 */
public class BinarySerializationImpl implements BinarySerialization {

    /**
     * Cached instance.
     */
    public static final BinarySerialization INSTANCE = new BinarySerializationImpl();

    /**
     * The serializers, one per {@link TransactionType} must be registered.
     */
    private final Map<TransactionType, TransactionSerializer<?>> serializers = new EnumMap<>(
        TransactionType.class);

    /**
     * Constructor
     */
    public BinarySerializationImpl() {
        register(new TransferTransactionSerializer());
        register(new MosaicSupplyChangeTransactionSerializer());
        register(new MosaicDefinitionTransactionSerializer());
        register(new AccountLinkTransactionSerializer());
        register(new AccountMetadataTransactionSerializer());
        register(new MosaicMetadataTransactionSerializer());
        register(new NamespaceMetadataTransactionSerializer());
        register(new NamespaceRegistrationTransactionSerializer());
        register(new SecretLockTransactionSerializer());
        register(new SecretProofTransactionSerializer());
        register(new AddressAliasTransactionSerializer());
        register(new MosaicAliasTransactionSerializer());
        register(new HashLockTransactionSerializer());
        register(new MultisigAccountModificationTransactionSerializer());
        register(new MosaicAddressRestrictionTransactionSerializer());
        register(new MosaicGlobalRestrictionTransactionSerializer());
        register(new AccountMosaicRestrictionTransactionSerializer());
        register(new AccountOperationRestrictionTransactionSerializer());
        register(new AccountAddressRestrictionTransactionSerializer());
        register(new AggregateTransactionSerializer(TransactionType.AGGREGATE_COMPLETE, this));
        register(new AggregateTransactionSerializer(TransactionType.AGGREGATE_BONDED, this));
    }

    /**
     * @param serializer the serializer to be registered.
     */
    private void register(TransactionSerializer serializer) {
        if (serializers.put(serializer.getTransactionType(), serializer) != null) {
            throw new IllegalArgumentException(
                "TransactionSerializer for type " + serializer.getTransactionType()
                    + " was already registered!");
        }
    }

    /**
     * It returns the registered {@link TransactionSerializer} for the given {@link TransactionType}.
     *
     * @param transactionType the transaction type.
     * @param <T> the transaction type
     * @return the {@link TransactionSerializer}
     */
    <T extends Transaction> TransactionSerializer<T> resolveSerializer(
        TransactionType transactionType) {
        @SuppressWarnings("unchecked")
        TransactionSerializer<T> mapper = (TransactionSerializer<T>) serializers
            .get(transactionType);
        if (mapper == null) {
            throw new UnsupportedOperationException(
                "Unimplemented Transaction type " + transactionType);
        }
        return mapper;
    }

    /**
     * Serialization basic implementation, it just delegates the work to the transactions.
     *
     * @param transaction the transaction
     * @return the serialized transaction.
     */
    @Override
    public <T extends Transaction> byte[] serialize(T transaction) {
        Validate.notNull(transaction, "Transaction must not be null");
        TransactionBuilder transactionBuilder = getTransactionBuilder(transaction);
        return serializeTransaction(transactionBuilder.serialize(), transaction);
    }


    /**
     * Serialized the transfer transaction to embedded bytes.
     *
     * @param transaction the transaction
     * @param <T> the transaction class
     * @return bytes of the transaction.
     */
    public <T extends Transaction> byte[] serializeEmbedded(T transaction) {
        Validate.notNull(transaction, "Transaction must not be null");
        EmbeddedTransactionBuilder embeddedTransactionBuilder = EmbeddedTransactionBuilder.create(
            new KeyDto(getRequiredSignerBytes(transaction.getSigner())),
            transaction.getVersion().byteValue(),
            NetworkTypeDto.rawValueOf((byte) transaction.getNetworkType().getValue()),
            EntityTypeDto.rawValueOf((short) transaction.getType().getValue()));
        return serializeTransaction(embeddedTransactionBuilder.serialize(), transaction);
    }

    /**
     * Creates the right {@link EmbeddedTransactionBuilder} from the transaction
     *
     * @param transaction the transaction
     * @param <T> the transaction class
     * @return the {@link EmbeddedTransactionBuilder}
     */
    private <T extends Transaction> EmbeddedTransactionBuilder toEmbeddedTransactionBuilder(
        T transaction) {
        return TransactionBuilderFactory.createEmbeddedTransactionBuilder(
            SerializationUtils.toDataInput(serializeEmbedded(transaction)));
    }

    /**
     * Gets the top level {@link TransactionBuilder} for the given transaction
     *
     * @param transaction the transaction
     * @return the top level {@link TransactionBuilder}
     */
    private TransactionBuilder getTransactionBuilder(Transaction transaction) {

        final SignatureDto signatureDto = transaction.getSignature()
            .map(SerializationUtils::toSignatureDto)
            .orElseGet(() -> new SignatureDto(ByteBuffer.allocate(64)));

        final ByteBuffer signerBuffer = transaction.getSigner()
            .map(SerializationUtils::toByteBuffer).orElseGet(() -> ByteBuffer.allocate(32));

        return TransactionBuilder.create(
            signatureDto,
            new KeyDto(signerBuffer),
            transaction.getVersion().byteValue(),
            NetworkTypeDto.rawValueOf((byte) transaction.getNetworkType().getValue()),
            EntityTypeDto.rawValueOf((short) transaction.getType().getValue()),
            new AmountDto(transaction.getMaxFee().longValue()),
            new TimestampDto(transaction.getDeadline().getInstant()));
    }

    /**
     * This method concats the common bytes to the serialized specific transaction.
     *
     * @param <T> the type of the transaction
     * @param commonBytes the common byte array.
     * @param transaction the transaction.
     * @return the serialized transaction.
     */
    private <T extends Transaction> byte[] serializeTransaction(byte[] commonBytes, T transaction) {
        TransactionSerializer<T> transactionSerializer = resolveSerializer(transaction.getType());
        Validate.isTrue(
            transactionSerializer.getTransactionClass().isAssignableFrom(transaction.getClass()),
            "Invalid TransactionSerializer's transaction class.");
        byte[] transactionBytes = transactionSerializer.toBodyBuilder(transaction).serialize();
        return SerializationUtils.concat(commonBytes, transactionBytes);
    }

    /**
     * It returns the transaction's byte array size useful to calculate its fee.
     *
     * @param <T> the type of the transaction
     * @param transaction the transaction
     * @return the size of the transaction.
     */
    @Override
    public <T extends Transaction> int getSize(T transaction) {
        return getTransactionBuilder(transaction).getSize() + resolveSerializer(
            transaction.getType()).toBodyBuilder(transaction)
            .getSize();
    }

    /**
     * Returns the transaction creator public account.
     *
     * @return the signer public account
     */
    private ByteBuffer getRequiredSignerBytes(Optional<PublicAccount> signer) {
        return signer.map(SerializationUtils::toByteBuffer)
            .orElseThrow(() -> new IllegalStateException("SignerBytes is required"));
    }

    /**
     * Deserialization of transactions. All the code related to the deserialization is handled in the class and its
     * helpers. Transaction Model Objects are not polluted with deserialization functionality.
     *
     * @param payload the byte array payload
     * @return the {@link Transaction}
     */
    @Override
    @SuppressWarnings("squid:S1192")
    public Transaction deserialize(byte[] payload) {
        Validate.notNull(payload, "Payload must not be null");
        DataInputStream stream = SerializationUtils.toDataInput(payload);
        TransactionBuilder builder = TransactionBuilderFactory
            .createTransactionBuilder(stream);

        return toTransaction(builder);
    }

    /**
     * It converts a {@link TransactionBuilder} to a {@link Transaction}
     *
     * @param builder the builder
     * @return the {@link Transaction} model.
     */
    private Transaction toTransaction(TransactionBuilder builder) {
        TransactionType transactionType = TransactionType
            .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
        NetworkType networkType = NetworkType
            .rawValueOf(SerializationUtils.byteToUnsignedInt(builder.getNetwork().getValue()));

        Deadline deadline = new Deadline(
            SerializationUtils.toUnsignedBigInteger(builder.getDeadline().getTimestamp()));

        TransactionFactory<?> factory = resolveSerializer(transactionType)
            .fromBodyBuilder(networkType, builder.getBody());

        factory.version(SerializationUtils.byteToUnsignedInt(builder.getVersion()));
        factory.maxFee(SerializationUtils.toUnsignedBigInteger(builder.getFee()));
        factory.deadline(deadline);
        if (!areAllZeros(builder.getSignature().getSignature().array())) {
            factory
                .signature(SerializationUtils.toHexString(builder.getSignature().getSignature()));
        }
        if (!areAllZeros(builder.getSignerPublicKey().getKey().array())) {
            factory.signer(
                SerializationUtils.toPublicAccount(builder.getSignerPublicKey(), networkType));
        }

        return factory.build();
    }


    /**
     * @param array the byte array.
     * @return if all the values in the array are zeros
     */
    private boolean areAllZeros(byte[] array) {
        for (byte b : array) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * It deserializes a payload of a embedded transaction into a transact model.
     *
     * @param payload the payload as {@link DataInputStream}
     * @return the {@link Transaction} model.
     */
    public Transaction deserializeEmbedded(DataInputStream payload) {

        return ExceptionUtils.propagate(() -> {
            Validate.notNull(payload, "Payload must not be null");
            EmbeddedTransactionBuilder builder = TransactionBuilderFactory
                .createEmbeddedTransactionBuilder(payload);
            return toTransaction(builder);
        });
    }

    /**
     * It converts a {@link EmbeddedTransactionBuilder} to a {@link Transaction}
     *
     * @param builder the builder
     * @return the {@link Transaction} model.
     */
    private Transaction toTransaction(EmbeddedTransactionBuilder builder) {
        TransactionType transactionType = TransactionType
            .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
        NetworkType networkType = NetworkType
            .rawValueOf(SerializationUtils.byteToUnsignedInt(builder.getNetwork().getValue()));
        TransactionFactory<?> factory = resolveSerializer(transactionType)
            .fromBodyBuilder(networkType, builder.getBody());
        factory
            .signer(
                SerializationUtils.toPublicAccount(builder.getSignerPublicKey(), networkType));
        factory.version(SerializationUtils.byteToUnsignedInt(builder.getVersion()));
        return factory.build();
    }


    /**
     * Interface of the serializer helper classes that know how to serialize/deserialize one type of transaction from a
     * payload.
     */
    interface TransactionSerializer<T extends Transaction> {

        /**
         * @return the {@link TransactionType} of the transaction this helper handles.
         */
        TransactionType getTransactionType();

        /**
         * @return the transaction class this serializer handles.
         */
        Class<T> getTransactionClass();

        /**
         * Subclasses would need to create the {@link TransactionFactory} for the handled {@link TransactionType} with
         * just the specific transaction values. Common values like maxFee and deadline are handled at top level,
         * subclasses won't need to duplicate the deserialization efforts.
         *
         * @param networkType the network type
         * @param transactionBuilder the stream containing just the specific transaction values in the right order.
         * @return the TransactionFactory of the transaction type this object handles.
         */
        TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder);

        /**
         * Subclasses would need to know how to serialize the internal components of a transaction, the bytes that are
         * serialized after the common attributes like max fee and duration. Subclasses would use catbuffer's
         * transaction body builders.
         *
         * @param transaction the transaction to be serialized
         * @return the catbuffer {@link Serializer}.
         */
        Serializer toBodyBuilder(T transaction);

    }

    private static class TransferTransactionSerializer implements
        TransactionSerializer<TransferTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.TRANSFER;
        }

        @Override
        public Class getTransactionClass() {
            return TransferTransaction.class;
        }

        @Override
        public TransactionFactory<?> fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            TransferTransactionBodyBuilder builder = ((TransferTransactionBodyBuilder) transactionBuilder);
            byte[] messageArray = builder.getMessage().array();
            MessageType messageType = MessageType
                .rawValueOf(SerializationUtils.byteToUnsignedInt(messageArray[0]));
            String messageHex = ConvertUtils.toHex(messageArray).substring(2);
            UnresolvedAddress recipient = SerializationUtils
                .toUnresolvedAddress(builder.getRecipientAddress());
            List<Mosaic> mosaics = builder.getMosaics().stream()
                .map(SerializationUtils::toMosaic)
                .collect(Collectors.toList());
            Message message = Message.createFromPayload(messageType, messageHex);
            return TransferTransactionFactory.create(networkType,
                recipient, mosaics, message);
        }

        @Override
        public Serializer toBodyBuilder(TransferTransaction transaction) {
            return TransferTransactionBodyBuilder.create(
                new UnresolvedAddressDto(
                    SerializationUtils
                        .fromUnresolvedAddressToByteBuffer(transaction.getRecipient(),
                            transaction.getNetworkType())),
                getUnresolvedMosaicArray(transaction),
                getMessageBuffer(transaction));

        }

        /**
         * Gets mosaic array.
         *
         * @return Mosaic array.
         */
        private List<UnresolvedMosaicBuilder> getUnresolvedMosaicArray(
            TransferTransaction transaction) {
            // Create Mosaics
            final List<UnresolvedMosaicBuilder> unresolvedMosaicList =
                new ArrayList<>(transaction.getMosaics().size());
            //Sort mosaics first
            final List<Mosaic> sortedMosaics = transaction.getMosaics().stream()
                .sorted(Comparator.comparing(m -> m.getId().getId()))
                .collect(Collectors.toList());

            for (final Mosaic mosaic : sortedMosaics) {
                final UnresolvedMosaicBuilder mosaicBuilder =
                    UnresolvedMosaicBuilder.create(
                        new UnresolvedMosaicIdDto(mosaic.getId().getIdAsLong()),
                        new AmountDto(mosaic.getAmount().longValue()));
                unresolvedMosaicList.add(mosaicBuilder);
            }
            return unresolvedMosaicList;
        }

        /**
         * Gets message buffer.
         *
         * @return Message buffer.
         */
        private ByteBuffer getMessageBuffer(TransferTransaction transaction) {
            final byte byteMessageType = (byte) transaction.getMessage().getType().getValue();
            final byte[] bytePayload = transaction.getMessage().getPayload()
                .getBytes(StandardCharsets.UTF_8);
            final ByteBuffer messageBuffer =
                ByteBuffer.allocate(bytePayload.length + 1 /* for the message type */);
            messageBuffer.put(byteMessageType);
            messageBuffer.put(bytePayload);
            return messageBuffer;
        }

    }

    private static class MosaicSupplyChangeTransactionSerializer implements
        TransactionSerializer<MosaicSupplyChangeTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_SUPPLY_CHANGE;
        }

        @Override
        public Class<MosaicSupplyChangeTransaction> getTransactionClass() {
            return MosaicSupplyChangeTransaction.class;
        }


        @Override
        public TransactionFactory<?> fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            MosaicSupplyChangeTransactionBodyBuilder builder = ((MosaicSupplyChangeTransactionBodyBuilder) transactionBuilder);
            UnresolvedMosaicId mosaicId = SerializationUtils
                .toUnresolvedMosaicId(builder.getMosaicId());
            MosaicSupplyChangeActionType action = MosaicSupplyChangeActionType
                .rawValueOf(builder.getAction().getValue());
            BigInteger delta = SerializationUtils.toUnsignedBigInteger(builder.getDelta());
            return MosaicSupplyChangeTransactionFactory
                .create(networkType, mosaicId, action, delta);
        }

        @Override
        public Serializer toBodyBuilder(MosaicSupplyChangeTransaction transaction) {
            return MosaicSupplyChangeTransactionBodyBuilder.create(
                new UnresolvedMosaicIdDto(transaction.getMosaicId().getId().longValue()),
                new AmountDto(transaction.getDelta().longValue()),
                MosaicSupplyChangeActionDto
                    .rawValueOf((byte) transaction.getAction().getValue()));

        }

    }

    private static class MosaicDefinitionTransactionSerializer implements
        TransactionSerializer<MosaicDefinitionTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_DEFINITION;
        }

        @Override
        public Class<MosaicDefinitionTransaction> getTransactionClass() {
            return MosaicDefinitionTransaction.class;
        }

        @Override
        public TransactionFactory<?> fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            MosaicDefinitionTransactionBodyBuilder builder = (MosaicDefinitionTransactionBodyBuilder) transactionBuilder;
            MosaicNonce mosaicNonce = MosaicNonce
                .createFromInteger(builder.getNonce().getMosaicNonce());
            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getId());
            MosaicFlags mosaicFlags = MosaicFlags
                .create(builder.getFlags().contains(MosaicFlagsDto.SUPPLY_MUTABLE),
                    builder.getFlags().contains(MosaicFlagsDto.TRANSFERABLE),
                    builder.getFlags().contains(MosaicFlagsDto.RESTRICTABLE));
            int divisibility = SerializationUtils.byteToUnsignedInt(builder.getDivisibility());
            BlockDuration blockDuration = new BlockDuration(
                builder.getDuration().getBlockDuration());
            return MosaicDefinitionTransactionFactory
                .create(networkType, mosaicNonce, mosaicId, mosaicFlags, divisibility,
                    blockDuration);
        }

        @Override
        public Serializer toBodyBuilder(MosaicDefinitionTransaction transaction) {
            return MosaicDefinitionTransactionBodyBuilder.create(
                new MosaicIdDto(transaction.getMosaicId().getId().longValue()),
                new BlockDurationDto(transaction.getBlockDuration().getDuration()),
                new MosaicNonceDto(transaction.getMosaicNonce().getNonceAsInt()),
                getMosaicFlagsEnumSet(transaction),
                (byte) transaction.getDivisibility());

        }

        /**
         * Get the mosaic flags.
         *
         * @return Mosaic flags
         */
        private EnumSet<MosaicFlagsDto> getMosaicFlagsEnumSet(
            MosaicDefinitionTransaction transaction) {
            EnumSet<MosaicFlagsDto> mosaicFlagsBuilder = EnumSet.of(MosaicFlagsDto.NONE);
            if (transaction.getMosaicFlags().isSupplyMutable()) {
                mosaicFlagsBuilder.add(MosaicFlagsDto.SUPPLY_MUTABLE);
            }
            if (transaction.getMosaicFlags().isTransferable()) {
                mosaicFlagsBuilder.add(MosaicFlagsDto.TRANSFERABLE);
            }
            if (transaction.getMosaicFlags().isRestrictable()) {
                mosaicFlagsBuilder.add(MosaicFlagsDto.RESTRICTABLE);
            }
            return mosaicFlagsBuilder;
        }
    }

    private static class AccountLinkTransactionSerializer implements
        TransactionSerializer<AccountLinkTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_LINK;
        }

        @Override
        public Class<AccountLinkTransaction> getTransactionClass() {
            return AccountLinkTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            AccountLinkTransactionBodyBuilder builder = (AccountLinkTransactionBodyBuilder) transactionBuilder;
            PublicAccount remoteAccount = SerializationUtils
                .toPublicAccount(builder.getRemotePublicKey(), networkType);
            AccountLinkAction linkAction = AccountLinkAction
                .rawValueOf(builder.getLinkAction().getValue());
            return AccountLinkTransactionFactory
                .create(networkType, remoteAccount, linkAction);
        }

        @Override
        public Serializer toBodyBuilder(AccountLinkTransaction transaction) {
            return AccountLinkTransactionBodyBuilder.create(
                SerializationUtils.toKeyDto(transaction.getRemoteAccount().getPublicKey()),
                LinkActionDto.rawValueOf(transaction.getLinkAction().getValue()));

        }

    }

    private static class AccountMetadataTransactionSerializer implements
        TransactionSerializer<AccountMetadataTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_METADATA;
        }

        @Override
        public Class<AccountMetadataTransaction> getTransactionClass() {
            return AccountMetadataTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            AccountMetadataTransactionBodyBuilder builder = (AccountMetadataTransactionBodyBuilder) transactionBuilder;
            PublicAccount targetAccount = SerializationUtils
                .toPublicAccount(builder.getTargetPublicKey(), networkType);
            BigInteger scopedMetadataKey = SerializationUtils
                .toUnsignedBigInteger(builder.getScopedMetadataKey());
            String value = SerializationUtils.toString(builder.getValue());
            return AccountMetadataTransactionFactory
                .create(networkType, targetAccount, scopedMetadataKey, value)
                .valueSizeDelta(SerializationUtils.shortToUnsignedInt(builder.getValueSizeDelta()));
        }

        @Override
        public Serializer toBodyBuilder(AccountMetadataTransaction transaction) {
            return AccountMetadataTransactionBodyBuilder.create(
                SerializationUtils.toKeyDto(transaction.getTargetAccount().getPublicKey()),
                transaction.getScopedMetadataKey().longValue(),
                (short) transaction.getValueSizeDelta(),
                ByteBuffer.wrap(MetadataTransaction.toByteArray(transaction.getValue()))
            );

        }

    }

    private static class MosaicMetadataTransactionSerializer implements
        TransactionSerializer<MosaicMetadataTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_METADATA;
        }

        @Override
        public Class<MosaicMetadataTransaction> getTransactionClass() {
            return MosaicMetadataTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            MosaicMetadataTransactionBodyBuilder builder = (MosaicMetadataTransactionBodyBuilder) transactionBuilder;
            PublicAccount targetAccount = SerializationUtils
                .toPublicAccount(builder.getTargetPublicKey(), networkType);
            BigInteger scopedMetadataKey = SerializationUtils
                .toUnsignedBigInteger(builder.getScopedMetadataKey());
            String value = StringEncoder.getString(builder.getValue().array());
            UnresolvedMosaicId targetMosaicId = SerializationUtils
                .toUnresolvedMosaicId(builder.getTargetMosaicId());
            return MosaicMetadataTransactionFactory
                .create(networkType, targetAccount, targetMosaicId, scopedMetadataKey, value)
                .valueSizeDelta(SerializationUtils.shortToUnsignedInt(builder.getValueSizeDelta()));
        }

        @Override
        public Serializer toBodyBuilder(MosaicMetadataTransaction transaction) {
            return MosaicMetadataTransactionBodyBuilder.create(
                SerializationUtils.toKeyDto(transaction.getTargetAccount().getPublicKey()),
                transaction.getScopedMetadataKey().longValue(),
                SerializationUtils.toUnresolvedMosaicIdDto(transaction.getTargetMosaicId()),
                (short) transaction.getValueSizeDelta(),
                ByteBuffer.wrap(MetadataTransaction.toByteArray(transaction.getValue()))
            );

        }

    }


    private static class NamespaceMetadataTransactionSerializer implements
        TransactionSerializer<NamespaceMetadataTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.NAMESPACE_METADATA;
        }

        @Override
        public Class<NamespaceMetadataTransaction> getTransactionClass() {
            return NamespaceMetadataTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            NamespaceMetadataTransactionBodyBuilder builder = (NamespaceMetadataTransactionBodyBuilder) transactionBuilder;
            PublicAccount targetAccount = SerializationUtils
                .toPublicAccount(builder.getTargetPublicKey(), networkType);
            BigInteger scopedMetadataKey = SerializationUtils
                .toUnsignedBigInteger(builder.getScopedMetadataKey());
            String value = StringEncoder.getString(builder.getValue().array());
            NamespaceId targetNamespaceId = SerializationUtils
                .toNamespaceId(builder.getTargetNamespaceId());
            return NamespaceMetadataTransactionFactory
                .create(networkType, targetAccount, targetNamespaceId, scopedMetadataKey, value)
                .valueSizeDelta(SerializationUtils.shortToUnsignedInt(builder.getValueSizeDelta()));
        }

        @Override
        public Serializer toBodyBuilder(NamespaceMetadataTransaction transaction) {
            return NamespaceMetadataTransactionBodyBuilder.create(
                new KeyDto(transaction.getTargetAccount().getPublicKey().getByteBuffer()),
                transaction.getScopedMetadataKey().longValue(),
                new NamespaceIdDto(transaction.getTargetNamespaceId().getId().longValue()),
                (short) transaction.getValueSizeDelta(),
                ByteBuffer.wrap(MetadataTransaction.toByteArray(transaction.getValue())
                ));

        }

    }

    private static class NamespaceRegistrationTransactionSerializer implements
        TransactionSerializer<NamespaceRegistrationTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.NAMESPACE_REGISTRATION;
        }

        @Override
        public Class<NamespaceRegistrationTransaction> getTransactionClass() {
            return NamespaceRegistrationTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            NamespaceRegistrationTransactionBodyBuilder builder = (NamespaceRegistrationTransactionBodyBuilder) transactionBuilder;

            NamespaceRegistrationType namespaceRegistrationType = NamespaceRegistrationType
                .rawValueOf(builder.getRegistrationType().getValue());
            String namespaceName = StringEncoder.getString(builder.getName().array());
            NamespaceId namespaceId = SerializationUtils
                .toNamespaceId(builder.getId());

            Optional<BigInteger> duration =
                namespaceRegistrationType == NamespaceRegistrationType.ROOT_NAMESPACE ? Optional
                    .ofNullable(builder.getDuration()).map(
                        BlockDurationDto::getBlockDuration)
                    .map(SerializationUtils::toUnsignedBigInteger)
                    : Optional.empty();

            Optional<NamespaceId> parentId =
                namespaceRegistrationType == NamespaceRegistrationType.SUB_NAMESPACE ? Optional
                    .of(builder.getParentId())
                    .map(SerializationUtils::toNamespaceId)
                    : Optional.empty();

            return NamespaceRegistrationTransactionFactory
                .create(networkType, namespaceName, namespaceId, namespaceRegistrationType,
                    duration, parentId);
        }

        @Override
        public Serializer toBodyBuilder(NamespaceRegistrationTransaction transaction) {
            NamespaceRegistrationTransactionBodyBuilder txBuilder;
            ByteBuffer namespaceNameByteBuffer = ByteBuffer
                .wrap(StringEncoder.getBytes(transaction.getNamespaceName()));
            NamespaceIdDto namespaceIdDto = new NamespaceIdDto(
                transaction.getNamespaceId().getId().longValue());

            if (transaction.getNamespaceRegistrationType()
                == NamespaceRegistrationType.ROOT_NAMESPACE) {
                txBuilder =
                    NamespaceRegistrationTransactionBodyBuilder.createRoot(
                        new BlockDurationDto(transaction.getDuration()
                            .orElseThrow(() -> new IllegalStateException("Duration is required"))
                            .longValue()),
                        namespaceIdDto,
                        namespaceNameByteBuffer);

            } else {
                txBuilder =
                    NamespaceRegistrationTransactionBodyBuilder.createChild(
                        new NamespaceIdDto(transaction.getParentId()
                            .orElseThrow(() -> new IllegalStateException("ParentId is required"))
                            .getId().longValue()),
                        namespaceIdDto,
                        namespaceNameByteBuffer);
            }
            return txBuilder;
        }

    }

    private static class SecretLockTransactionSerializer implements
        TransactionSerializer<SecretLockTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.SECRET_LOCK;
        }

        @Override
        public Class<SecretLockTransaction> getTransactionClass() {
            return SecretLockTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            SecretLockTransactionBodyBuilder builder = (SecretLockTransactionBodyBuilder) transactionBuilder;

            Mosaic mosaic = SerializationUtils.toMosaic(builder.getMosaic());
            BigInteger duration = SerializationUtils
                .toUnsignedBigInteger(builder.getDuration().getBlockDuration());
            LockHashAlgorithmType hashAlgorithm = LockHashAlgorithmType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getHashAlgorithm().getValue()));
            String secret = SerializationUtils.toHexString(builder.getSecret());
            UnresolvedAddress recipient = SerializationUtils
                .toUnresolvedAddress(builder.getRecipientAddress());
            return SecretLockTransactionFactory
                .create(networkType, mosaic, duration, hashAlgorithm, secret, recipient);
        }

        @Override
        public Serializer toBodyBuilder(SecretLockTransaction transaction) {
            UnresolvedMosaicIdDto mosaicId = new UnresolvedMosaicIdDto(
                transaction.getMosaic().getId().getIdAsLong());
            AmountDto amount = new AmountDto(transaction.getMosaic().getAmount().longValue());
            UnresolvedMosaicBuilder unresolvedMosaicBuilder = UnresolvedMosaicBuilder.create(
                mosaicId,
                amount);
            return SecretLockTransactionBodyBuilder
                .create(new Hash256Dto(getSecretBuffer(transaction)),
                    unresolvedMosaicBuilder,
                    new BlockDurationDto(transaction.getDuration().longValue()),
                    LockHashAlgorithmDto
                        .rawValueOf((byte) transaction.getHashAlgorithm().getValue()),
                    new UnresolvedAddressDto(
                        SerializationUtils
                            .fromUnresolvedAddressToByteBuffer(transaction.getRecipient(),
                                transaction.getNetworkType())))
                ;
        }

        /**
         * Gets secret buffer.
         *
         * @param transaction the transaction.
         * @return Secret buffer.
         */
        private ByteBuffer getSecretBuffer(SecretLockTransaction transaction) {
            final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
            secretBuffer.put(ConvertUtils.fromHexToBytes(transaction.getSecret()));
            return secretBuffer;
        }
    }

    private static class SecretProofTransactionSerializer implements
        TransactionSerializer<SecretProofTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.SECRET_PROOF;
        }

        @Override
        public Class<SecretProofTransaction> getTransactionClass() {
            return SecretProofTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            SecretProofTransactionBodyBuilder builder = (SecretProofTransactionBodyBuilder) transactionBuilder;

            LockHashAlgorithmType hashType = LockHashAlgorithmType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getHashAlgorithm().getValue()));
            UnresolvedAddress recipient = SerializationUtils
                .toUnresolvedAddress(builder.getRecipientAddress());
            String secret = SerializationUtils.toHexString(builder.getSecret());
            String proof = SerializationUtils.toHexString(builder.getProof());
            return SecretProofTransactionFactory
                .create(networkType, hashType, recipient, secret, proof);
        }

        @Override
        public Serializer toBodyBuilder(SecretProofTransaction transaction) {
            return SecretProofTransactionBodyBuilder.create(
                new Hash256Dto(getSecretBuffer(transaction)),
                LockHashAlgorithmDto.rawValueOf((byte) transaction.getHashType().getValue()),
                new UnresolvedAddressDto(
                    SerializationUtils
                        .fromUnresolvedAddressToByteBuffer(transaction.getRecipient(),
                            transaction.getNetworkType())),
                getProofBuffer(transaction));
        }

        /**
         * Gets proof buffer
         *
         * @param transaction the transaction.
         * @return the secret buffer.
         */
        private ByteBuffer getSecretBuffer(SecretProofTransaction transaction) {
            final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
            secretBuffer.put(ConvertUtils.fromHexToBytes(transaction.getSecret()));
            return secretBuffer;
        }

        /**
         * Gets proof buffer
         *
         * @param transaction the transaction.
         * @return Proof buffer.
         */
        private ByteBuffer getProofBuffer(SecretProofTransaction transaction) {
            final byte[] proofBytes = ConvertUtils.fromHexToBytes(transaction.getProof());
            return ByteBuffer.wrap(proofBytes);
        }
    }

    private static class AddressAliasTransactionSerializer implements
        TransactionSerializer<AddressAliasTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ADDRESS_ALIAS;
        }

        @Override
        public Class<AddressAliasTransaction> getTransactionClass() {
            return AddressAliasTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            AddressAliasTransactionBodyBuilder builder = (AddressAliasTransactionBodyBuilder) transactionBuilder;

            AliasAction aliasAction = AliasAction
                .rawValueOf(builder.getAliasAction().getValue());
            NamespaceId namespaceId = SerializationUtils
                .toNamespaceId(builder.getNamespaceId());
            Address address = SerializationUtils.toAddress(builder.getAddress());

            return AddressAliasTransactionFactory
                .create(networkType, aliasAction, namespaceId, address);
        }

        @Override
        public Serializer toBodyBuilder(AddressAliasTransaction transaction) {
            NamespaceIdDto namespaceIdDto = new NamespaceIdDto(
                transaction.getNamespaceId().getIdAsLong());
            AliasActionDto aliasActionDto = AliasActionDto
                .rawValueOf(transaction.getAliasAction().getValue());
            Address address = transaction.getAddress();
            AddressDto addressDto = SerializationUtils.toAddressDto(address, transaction.getNetworkType());
            return AddressAliasTransactionBodyBuilder
                .create(namespaceIdDto, addressDto, aliasActionDto);
        }



    }

    private static class MosaicAliasTransactionSerializer implements
        TransactionSerializer<MosaicAliasTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_ALIAS;
        }

        @Override
        public Class<MosaicAliasTransaction> getTransactionClass() {
            return MosaicAliasTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            MosaicAliasTransactionBodyBuilder builder = (MosaicAliasTransactionBodyBuilder) transactionBuilder;

            AliasAction aliasAction = AliasAction
                .rawValueOf(builder.getAliasAction().getValue());
            NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getNamespaceId());
            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getMosaicId());

            return MosaicAliasTransactionFactory
                .create(networkType, aliasAction, namespaceId, mosaicId);
        }

        @Override
        public Serializer toBodyBuilder(MosaicAliasTransaction transaction) {
            return MosaicAliasTransactionBodyBuilder.create(
                new NamespaceIdDto(transaction.getNamespaceId().getIdAsLong()),
                new MosaicIdDto(transaction.getMosaicId().getIdAsLong()),
                AliasActionDto.rawValueOf(transaction.getAliasAction().getValue())
            );
        }

    }

    private static class HashLockTransactionSerializer implements
        TransactionSerializer<HashLockTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.HASH_LOCK;
        }

        @Override
        public Class<HashLockTransaction> getTransactionClass() {
            return HashLockTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            HashLockTransactionBodyBuilder builder = (HashLockTransactionBodyBuilder) transactionBuilder;

            Mosaic mosaic = SerializationUtils.toMosaic(builder.getMosaic());
            BigInteger duration = SerializationUtils
                .toUnsignedBigInteger(builder.getDuration().getBlockDuration());
            return HashLockTransactionFactory
                .create(networkType, mosaic, duration,
                    SerializationUtils.toHexString(builder.getHash()));
        }

        @Override
        public Serializer toBodyBuilder(HashLockTransaction transaction) {
            return HashLockTransactionBodyBuilder.create(
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(transaction.getMosaic().getId().getIdAsLong()),
                    new AmountDto(transaction.getMosaic().getAmount().longValue())),
                new BlockDurationDto(transaction.getDuration().longValue()),
                new Hash256Dto(getHashBuffer(transaction)));
        }


        /**
         * Gets hash buffer.
         *
         * @param transaction the transaction.
         * @return Hash buffer.
         */
        private ByteBuffer getHashBuffer(HashLockTransaction transaction) {
            return ByteBuffer.wrap(ConvertUtils.fromHexToBytes(transaction.getHash()));
        }
    }


    private static class AccountAddressRestrictionTransactionSerializer implements
        TransactionSerializer<AccountAddressRestrictionTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_ADDRESS_RESTRICTION;
        }

        @Override
        public Class<AccountAddressRestrictionTransaction> getTransactionClass() {
            return AccountAddressRestrictionTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            AccountAddressRestrictionTransactionBodyBuilder builder = (AccountAddressRestrictionTransactionBodyBuilder) transactionBuilder;

            long restrictionFlagsValue = builder.getRestrictionFlags().stream()
                .mapToLong(AccountRestrictionFlagsDto::getValue).sum();

            AccountRestrictionFlags restrictionFlags = AccountRestrictionFlags
                .rawValueOf((int) restrictionFlagsValue);

            List<UnresolvedAddress> restrictionAdditions = builder.getRestrictionAdditions()
                .stream()
                .map(SerializationUtils::toUnresolvedAddress)
                .collect(Collectors.toList());

            List<UnresolvedAddress> restrictionDeletions = builder.getRestrictionDeletions()
                .stream()
                .map(SerializationUtils::toUnresolvedAddress)
                .collect(Collectors.toList());

            return AccountAddressRestrictionTransactionFactory
                .create(networkType, restrictionFlags, restrictionAdditions, restrictionDeletions);
        }

        @Override
        public Serializer toBodyBuilder(AccountAddressRestrictionTransaction transaction) {

            EnumSet<AccountRestrictionFlagsDto> flags = transaction.getRestrictionFlags().getFlags()
                .stream().map(f -> AccountRestrictionFlagsDto.rawValueOf(
                    (short) f.getValue())).collect(Collectors
                    .toCollection(() -> EnumSet.noneOf(AccountRestrictionFlagsDto.class)));

            List<UnresolvedAddressDto> restrictionAdditions = transaction.getRestrictionAdditions()
                .stream()
                .map(
                    a -> SerializationUtils.toUnresolvedAddress(a, transaction.getNetworkType()))
                .collect(Collectors.toList());

            List<UnresolvedAddressDto> restrictionDeletions = transaction.getRestrictionDeletions()
                .stream()
                .map(
                    a -> SerializationUtils.toUnresolvedAddress(a, transaction.getNetworkType()))
                .collect(Collectors.toList());

            return AccountAddressRestrictionTransactionBodyBuilder.create(
                flags,
                restrictionAdditions, restrictionDeletions);
        }


    }

    private static class AccountMosaicRestrictionTransactionSerializer implements
        TransactionSerializer<AccountMosaicRestrictionTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_MOSAIC_RESTRICTION;
        }

        @Override
        public Class<AccountMosaicRestrictionTransaction> getTransactionClass() {
            return AccountMosaicRestrictionTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            AccountMosaicRestrictionTransactionBodyBuilder builder = (AccountMosaicRestrictionTransactionBodyBuilder) transactionBuilder;
            long restrictionFlagsValues = builder.getRestrictionFlags().stream()
                .mapToLong(AccountRestrictionFlagsDto::getValue).sum();

            AccountRestrictionFlags restrictionFlags = AccountRestrictionFlags
                .rawValueOf((int) restrictionFlagsValues);

            List<UnresolvedMosaicId> restrictionAdditions = builder.getRestrictionAdditions()
                .stream().map(SerializationUtils::toUnresolvedMosaicId)
                .collect(Collectors.toList());

            List<UnresolvedMosaicId> restrictionDeletions = builder.getRestrictionDeletions()
                .stream().map(SerializationUtils::toUnresolvedMosaicId)
                .collect(Collectors.toList());

            return AccountMosaicRestrictionTransactionFactory
                .create(networkType, restrictionFlags, restrictionAdditions, restrictionDeletions);
        }

        @Override
        public Serializer toBodyBuilder(AccountMosaicRestrictionTransaction transaction) {

            EnumSet<AccountRestrictionFlagsDto> flags = transaction.getRestrictionFlags().getFlags()
                .stream().map(f -> AccountRestrictionFlagsDto.rawValueOf(
                    (short) f.getValue())).collect(Collectors
                    .toCollection(() -> EnumSet.noneOf(AccountRestrictionFlagsDto.class)));

            List<UnresolvedMosaicIdDto> restrictionAdditions = transaction.getRestrictionAdditions()
                .stream().map(a -> new UnresolvedMosaicIdDto(a.getIdAsLong()))
                .collect(Collectors.toList());

            List<UnresolvedMosaicIdDto> restrictionDeletions = transaction.getRestrictionDeletions()
                .stream().map(a -> new UnresolvedMosaicIdDto(a.getIdAsLong()))
                .collect(Collectors.toList());

            return AccountMosaicRestrictionTransactionBodyBuilder.create(
                flags,
                restrictionAdditions, restrictionDeletions);
        }

    }

    private static class AccountOperationRestrictionTransactionSerializer implements
        TransactionSerializer<AccountOperationRestrictionTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_OPERATION_RESTRICTION;
        }

        @Override
        public Class<AccountOperationRestrictionTransaction> getTransactionClass() {
            return AccountOperationRestrictionTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            AccountOperationRestrictionTransactionBodyBuilder builder = (AccountOperationRestrictionTransactionBodyBuilder) transactionBuilder;

            long restrictionFlagsValue = builder.getRestrictionFlags().stream()
                .mapToLong(AccountRestrictionFlagsDto::getValue).sum();

            AccountRestrictionFlags restrictionFlags = AccountRestrictionFlags
                .rawValueOf((int) restrictionFlagsValue);

            List<TransactionType> restrictionAdditions = builder.getRestrictionAdditions()
                .stream()
                .map(op -> TransactionType.rawValueOf(op.getValue()))
                .collect(Collectors.toList());

            List<TransactionType> restrictionDeletions = builder.getRestrictionDeletions()
                .stream()
                .map(op -> TransactionType.rawValueOf(op.getValue()))
                .collect(Collectors.toList());

            return AccountOperationRestrictionTransactionFactory
                .create(networkType, restrictionFlags, restrictionAdditions, restrictionDeletions);
        }

        @Override
        public Serializer toBodyBuilder(AccountOperationRestrictionTransaction transaction) {

            EnumSet<AccountRestrictionFlagsDto> flags = transaction.getRestrictionFlags().getFlags()
                .stream().map(f -> AccountRestrictionFlagsDto.rawValueOf(
                    (short) f.getValue())).collect(Collectors
                    .toCollection(() -> EnumSet.noneOf(AccountRestrictionFlagsDto.class)));

            List<EntityTypeDto> restrictionAdditions = transaction.getRestrictionAdditions()
                .stream()
                .map(
                    a -> EntityTypeDto.rawValueOf((short) a.getValue()))
                .collect(Collectors.toList());

            List<EntityTypeDto> restrictionDeletions = transaction.getRestrictionDeletions()
                .stream()
                .map(
                    a -> EntityTypeDto.rawValueOf((short) a.getValue()))
                .collect(Collectors.toList());

            return AccountOperationRestrictionTransactionBodyBuilder.create(
                flags,
                restrictionAdditions, restrictionDeletions);
        }

    }

    private static class MosaicAddressRestrictionTransactionSerializer implements
        TransactionSerializer<MosaicAddressRestrictionTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_ADDRESS_RESTRICTION;
        }

        @Override
        public Class<MosaicAddressRestrictionTransaction> getTransactionClass() {
            return MosaicAddressRestrictionTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            MosaicAddressRestrictionTransactionBodyBuilder builder = (MosaicAddressRestrictionTransactionBodyBuilder) transactionBuilder;

            UnresolvedMosaicId mosaicId = SerializationUtils
                .toUnresolvedMosaicId(builder.getMosaicId());
            BigInteger restrictionKey = SerializationUtils
                .toUnsignedBigInteger(builder.getRestrictionKey());
            UnresolvedAddress targetAddress = SerializationUtils
                .toUnresolvedAddress(builder.getTargetAddress());
            BigInteger newRestrictionValue = SerializationUtils
                .toUnsignedBigInteger(builder.getNewRestrictionValue());
            BigInteger previousRestrictionValue = SerializationUtils
                .toUnsignedBigInteger(builder.getPreviousRestrictionValue());
            return MosaicAddressRestrictionTransactionFactory
                .create(networkType, mosaicId, restrictionKey, targetAddress, newRestrictionValue)
                .previousRestrictionValue(previousRestrictionValue);
        }

        @Override
        public Serializer toBodyBuilder(MosaicAddressRestrictionTransaction transaction) {
            UnresolvedAddressDto unresolvedAddressDto = SerializationUtils
                .toUnresolvedAddress(transaction.getTargetAddress(),
                    transaction.getNetworkType());
            return MosaicAddressRestrictionTransactionBodyBuilder.create(
                new UnresolvedMosaicIdDto(transaction.getMosaicId().getIdAsLong()),
                transaction.getRestrictionKey().longValue(),
                transaction.getPreviousRestrictionValue().longValue(),
                transaction.getNewRestrictionValue().longValue(), unresolvedAddressDto
            );
        }

    }

    private static class MosaicGlobalRestrictionTransactionSerializer implements
        TransactionSerializer<MosaicGlobalRestrictionTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_GLOBAL_RESTRICTION;
        }

        @Override
        public Class<MosaicGlobalRestrictionTransaction> getTransactionClass() {
            return MosaicGlobalRestrictionTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            MosaicGlobalRestrictionTransactionBodyBuilder builder = (MosaicGlobalRestrictionTransactionBodyBuilder) transactionBuilder;
            UnresolvedMosaicId mosaicId = SerializationUtils
                .toUnresolvedMosaicId(builder.getMosaicId());
            BigInteger restrictionKey = SerializationUtils
                .toUnsignedBigInteger(builder.getRestrictionKey());
            BigInteger newRestrictionValue = SerializationUtils
                .toUnsignedBigInteger(builder.getNewRestrictionValue());
            BigInteger previousRestrictionValue = SerializationUtils
                .toUnsignedBigInteger(builder.getPreviousRestrictionValue());
            MosaicRestrictionType newRestrictionType = MosaicRestrictionType
                .rawValueOf(builder.getNewRestrictionType().getValue());
            MosaicRestrictionType previousRestrictionType = MosaicRestrictionType
                .rawValueOf(builder.getPreviousRestrictionType().getValue());
            return MosaicGlobalRestrictionTransactionFactory
                .create(networkType, mosaicId, restrictionKey, newRestrictionValue,
                    newRestrictionType)
                .referenceMosaicId(
                    SerializationUtils.toUnresolvedMosaicId(builder.getReferenceMosaicId()))
                .previousRestrictionValue(previousRestrictionValue)
                .previousRestrictionType(previousRestrictionType);
        }

        @Override
        public Serializer toBodyBuilder(MosaicGlobalRestrictionTransaction transaction) {
            return MosaicGlobalRestrictionTransactionBodyBuilder.create(
                new UnresolvedMosaicIdDto(transaction.getMosaicId().getIdAsLong()),
                new UnresolvedMosaicIdDto(transaction.getReferenceMosaicId().getIdAsLong()),
                transaction.getRestrictionKey().longValue(),
                transaction.getPreviousRestrictionValue().longValue(),
                transaction.getNewRestrictionValue().longValue(),
                MosaicRestrictionTypeDto
                    .rawValueOf(transaction.getPreviousRestrictionType().getValue()),
                MosaicRestrictionTypeDto
                    .rawValueOf(transaction.getNewRestrictionType().getValue()));
        }

    }


    private static class MultisigAccountModificationTransactionSerializer implements
        TransactionSerializer<MultisigAccountModificationTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MULTISIG_ACCOUNT_MODIFICATION;
        }

        @Override
        public Class<MultisigAccountModificationTransaction> getTransactionClass() {
            return MultisigAccountModificationTransaction.class;
        }

        @Override
        public TransactionFactory fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {

            MultisigAccountModificationTransactionBodyBuilder builder = (MultisigAccountModificationTransactionBodyBuilder) transactionBuilder;
            byte minApprovalDelta = builder.getMinApprovalDelta();
            byte minRemovalDelta = builder.getMinRemovalDelta();

            List<PublicAccount> publicKeyAdditions = builder.getPublicKeyAdditions()
                .stream()
                .map(op -> SerializationUtils.toPublicAccount(op, networkType))
                .collect(Collectors.toList());

            List<PublicAccount> publicKeyDeletions = builder.getPublicKeyDeletions()
                .stream()
                .map(op -> SerializationUtils.toPublicAccount(op, networkType))
                .collect(Collectors.toList());

            return MultisigAccountModificationTransactionFactory
                .create(networkType, minApprovalDelta, minRemovalDelta, publicKeyAdditions,
                    publicKeyDeletions);
        }

        @Override
        public Serializer toBodyBuilder(MultisigAccountModificationTransaction transaction) {

            List<KeyDto> publicKeyAdditions = transaction.getPublicKeyAdditions()
                .stream()
                .map(
                    a -> SerializationUtils.toKeyDto(a.getPublicKey()))
                .collect(Collectors.toList());

            List<KeyDto> publicKeyDeletions = transaction.getPublicKeyDeletions()
                .stream()
                .map(
                    a -> SerializationUtils.toKeyDto(a.getPublicKey()))
                .collect(Collectors.toList());

            return MultisigAccountModificationTransactionBodyBuilder.create(
                transaction.getMinRemovalDelta(),
                transaction.getMinApprovalDelta(),

                publicKeyAdditions, publicKeyDeletions);
        }


    }


    private static class AggregateTransactionSerializer implements
        TransactionSerializer<AggregateTransaction> {

        private final TransactionType transactionType;

        private final BinarySerializationImpl transactionSerialization;

        public AggregateTransactionSerializer(TransactionType transactionType,
            BinarySerializationImpl transactionSerialization) {
            this.transactionType = transactionType;
            this.transactionSerialization = transactionSerialization;
        }

        @Override
        public TransactionType getTransactionType() {
            return transactionType;
        }

        @Override
        public Class<AggregateTransaction> getTransactionClass() {
            return AggregateTransaction.class;
        }

        @Override
        public TransactionFactory<?> fromBodyBuilder(NetworkType networkType,
            Serializer transactionBuilder) {
            AggregateTransactionBodyBuilder builder = (AggregateTransactionBodyBuilder) transactionBuilder;
            List<Transaction> transactions = builder.getTransactions().stream()
                .map(transactionSerialization::toTransaction
                ).collect(Collectors.toList());

            List<AggregateTransactionCosignature> cosignatures = builder.getCosignatures().stream()
                .map(cosignatureBuilder -> getAggregateTransactionCosignature(networkType,
                    cosignatureBuilder)).collect(Collectors.toList());
            return AggregateTransactionFactory.create(
                getTransactionType(), networkType,
                SerializationUtils.toHexString(builder.getTransactionsHash()), transactions,
                cosignatures);
        }

        private AggregateTransactionCosignature getAggregateTransactionCosignature(
            NetworkType networkType, CosignatureBuilder cosignatureBuilder) {
            PublicAccount signer =
                SerializationUtils.toPublicAccount(
                    cosignatureBuilder.getSignerPublicKey(), networkType);
            String cosignature = SerializationUtils
                .toHexString(cosignatureBuilder.getSignature().getSignature());
            return new AggregateTransactionCosignature(cosignature, signer);
        }


        @Override
        public Serializer toBodyBuilder(AggregateTransaction transaction) {

            List<EmbeddedTransactionBuilder> transactions = transaction.getInnerTransactions()
                .stream()
                .map(transactionSerialization::toEmbeddedTransactionBuilder
                ).collect(Collectors.toList());

            List<CosignatureBuilder> cosignatures = transaction.getCosignatures().stream()
                .map(this::getCosignatureBuilder).collect(Collectors.toList());

            return AggregateTransactionBodyBuilder
                .create(SerializationUtils.toHash256Dto(transaction.getTransactionsHash()),
                    transactions, cosignatures);
        }

        private CosignatureBuilder getCosignatureBuilder(AggregateTransactionCosignature c) {
            return CosignatureBuilder
                .create(SerializationUtils.toKeyDto(c.getSigner().getPublicKey()),
                    SerializationUtils.toSignatureDto(c.getSignature()));
        }
    }

}
