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

import io.nem.catapult.builders.AccountAddressRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.AccountLinkActionDto;
import io.nem.catapult.builders.AccountLinkTransactionBodyBuilder;
import io.nem.catapult.builders.AccountMetadataTransactionBodyBuilder;
import io.nem.catapult.builders.AccountMosaicRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.AccountOperationRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.AccountRestrictionFlagsDto;
import io.nem.catapult.builders.AddressAliasTransactionBodyBuilder;
import io.nem.catapult.builders.AddressDto;
import io.nem.catapult.builders.AliasActionDto;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.BlockDurationDto;
import io.nem.catapult.builders.CosignatureBuilder;
import io.nem.catapult.builders.EmbeddedTransactionBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.GeneratorUtils;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.HashLockTransactionBodyBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.LockHashAlgorithmDto;
import io.nem.catapult.builders.MosaicAddressRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicAliasTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicDefinitionTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicFlagsDto;
import io.nem.catapult.builders.MosaicGlobalRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicIdDto;
import io.nem.catapult.builders.MosaicMetadataTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicNonceDto;
import io.nem.catapult.builders.MosaicRestrictionTypeDto;
import io.nem.catapult.builders.MosaicSupplyChangeActionDto;
import io.nem.catapult.builders.MosaicSupplyChangeTransactionBodyBuilder;
import io.nem.catapult.builders.MultisigAccountModificationTransactionBodyBuilder;
import io.nem.catapult.builders.NamespaceIdDto;
import io.nem.catapult.builders.NamespaceMetadataTransactionBodyBuilder;
import io.nem.catapult.builders.NamespaceRegistrationTransactionBodyBuilder;
import io.nem.catapult.builders.NetworkTypeDto;
import io.nem.catapult.builders.SecretLockTransactionBodyBuilder;
import io.nem.catapult.builders.SecretProofTransactionBodyBuilder;
import io.nem.catapult.builders.Serializer;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.TransactionBuilder;
import io.nem.catapult.builders.TransferTransactionBodyBuilder;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.ExceptionUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.api.BinarySerialization;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.message.MessageType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.AccountLinkTransactionFactory;
import io.nem.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.sdk.model.transaction.MetadataTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.util.encoders.Hex;


/**
 * Implementation of BinarySerialization. It uses the catbuffer generated builders to deserialize an
 * object.
 */
public class BinarySerializationImpl implements BinarySerialization {

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
     * It returns the registered {@link TransactionSerializer} for the given {@link
     * TransactionType}.
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
        EmbeddedTransactionBuilder embeddedTransactionBuilder = getEmbeddedTransactionBuilder(
            transaction);
        return serializeTransaction(embeddedTransactionBuilder.serialize(), transaction);
    }

    private <T extends Transaction> EmbeddedTransactionBuilder getEmbeddedTransactionBuilder(
        T transaction) {
        return EmbeddedTransactionBuilder.create(
            new KeyDto(getRequiredSignerBytes(transaction.getSigner())),
            transaction.getVersion().byteValue(),
            NetworkTypeDto.rawValueOf((byte) transaction.getNetworkType().getValue()),
            EntityTypeDto.rawValueOf((short) transaction.getType().getValue()));
    }

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
        byte[] transactionBytes = transactionSerializer.getBodyBuilder(transaction).serialize();
        return SerializationUtils.concat(commonBytes, transactionBytes);
    }


    @Override
    public <T extends Transaction> int getSize(T transaction) {
        return getTransactionBuilder(transaction).getSize() + resolveSerializer(
            transaction.getType()).getBodyBuilder(transaction)
            .getSize();
    }

    private <T extends Transaction> int getEmbeddedSize(T transaction) {
        return getEmbeddedTransactionBuilder(transaction).getSize() + resolveSerializer(
            transaction.getType()).getBodyBuilder(transaction)
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
     * Deserialization of transactions. All the code related to the deserialization is handled in
     * the class and its helpers. Transaction Model Objects are not polluted with deserialization
     * functionality.
     *
     * @param payload the byte array payload
     * @return the {@link Transaction}
     */
    @Override
    @SuppressWarnings("squid:S1192")
    public Transaction deserialize(byte[] payload) {
        Validate.notNull(payload, "Payload must not be null");
        DataInputStream stream = SerializationUtils.toDataInput(payload);
        TransactionBuilder builder = TransactionBuilder.loadFromBinary(stream);
        TransactionType transactionType = TransactionType
            .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
        NetworkType networkType = NetworkType
            .rawValueOf(SerializationUtils.byteToUnsignedInt(builder.getNetwork().getValue()));

        Deadline deadline = new Deadline(
            SerializationUtils.toUnsignedBigInteger(builder.getDeadline().getTimestamp()));
        TransactionFactory<?> factory = resolveSerializer(transactionType)
            .fromStream(networkType, stream);
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
     * @param payload the payload as byte array.
     * @return the {@link Transaction} model.
     */
    public Transaction deserializeEmbedded(byte[] payload) {
        Validate.notNull(payload, "Payload must not be null");
        return deserializeEmbedded(SerializationUtils.toDataInput(payload));
    }

    /**
     * It deserializes a payload of a embedded transaction into a transact model.
     *
     * @param payload the payload as {@link ByteBuffer}. This buffer is the continuation of the top
     * level payload.
     * @return the {@link Transaction} model.
     */
    public Transaction deserializeEmbedded(ByteBuffer payload) {
        Validate.notNull(payload, "Payload must not be null");
        return deserializeEmbedded(new DataInputStream(new ByteBufferBackedInputStream(payload)));
    }

    /**
     * It deserializes a payload of a embedded transaction into a transact model.
     *
     * @param payload the payload as {@link DataInputStream}
     * @return the {@link Transaction} model.
     */
    private Transaction deserializeEmbedded(DataInputStream payload) {

        return ExceptionUtils.propagate(() -> {
            Validate.notNull(payload, "Payload must not be null");
            EmbeddedTransactionBuilder builder = EmbeddedTransactionBuilder.loadFromBinary(payload);
            TransactionType transactionType = TransactionType
                .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
            NetworkType networkType = NetworkType
                .rawValueOf(SerializationUtils.byteToUnsignedInt(builder.getNetwork().getValue()));
            TransactionFactory<?> factory = resolveSerializer(transactionType)
                .fromStream(networkType, payload);
            factory
                .signer(
                    SerializationUtils.toPublicAccount(builder.getSignerPublicKey(), networkType));
            factory.version(SerializationUtils.byteToUnsignedInt(builder.getVersion()));
            return factory.build();
        });
    }


    /**
     * Interface of the serializer helper classes that know how to serialize/deserialize one type of
     * transaction from a payload.
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
         * Subclasses would need to create the {@link TransactionFactory} for the handled {@link
         * TransactionType} with just the specific transaction values. Common values like maxFee and
         * deadline are handled at top level, subclasses won't need to duplicate the deserialization
         * efforts.
         *
         * @param networkType the network type
         * @param stream the stream containing just the specific transaction values in the right
         * order.
         * @return the TransactionFactory of the transaction type this object handles.
         */
        TransactionFactory fromStream(NetworkType networkType, DataInputStream stream);

        /**
         * Subclasses would need to know how to serialize the internal components of a transaction,
         * the bytes that are serialized after the common attributes like max fee and duration.
         * Subclasses would use catbuffer's transaction body builders.
         *
         * @param transaction the transaction to be serialized
         * @return the catbuffer {@link Serializer}.
         */
        Serializer getBodyBuilder(T transaction);

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
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInputStream stream) {
            TransferTransactionBodyBuilder builder = TransferTransactionBodyBuilder
                .loadFromBinary(stream);
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
        public Serializer getBodyBuilder(TransferTransaction transaction) {
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
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInputStream stream) {
            MosaicSupplyChangeTransactionBodyBuilder builder = MosaicSupplyChangeTransactionBodyBuilder
                .loadFromBinary(stream);
            UnresolvedMosaicId mosaicId = SerializationUtils
                .toUnresolvedMosaicId(builder.getMosaicId());
            MosaicSupplyChangeActionType action = MosaicSupplyChangeActionType
                .rawValueOf(builder.getAction().getValue());
            BigInteger delta = SerializationUtils.toUnsignedBigInteger(builder.getDelta());
            return MosaicSupplyChangeTransactionFactory
                .create(networkType, mosaicId, action, delta);
        }

        @Override
        public Serializer getBodyBuilder(MosaicSupplyChangeTransaction transaction) {
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
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInputStream stream) {
            MosaicDefinitionTransactionBodyBuilder builder = MosaicDefinitionTransactionBodyBuilder
                .loadFromBinary(stream);
            MosaicNonce mosaicNonce = MosaicNonce
                .createFromBigInteger((long) builder.getNonce().getMosaicNonce());
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
        public Serializer getBodyBuilder(MosaicDefinitionTransaction transaction) {
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {
            AccountLinkTransactionBodyBuilder builder = AccountLinkTransactionBodyBuilder
                .loadFromBinary(stream);
            PublicAccount remoteAccount = SerializationUtils
                .toPublicAccount(builder.getRemotePublicKey(), networkType);
            AccountLinkAction linkAction = AccountLinkAction
                .rawValueOf(builder.getLinkAction().getValue());
            return AccountLinkTransactionFactory
                .create(networkType, remoteAccount, linkAction);
        }

        @Override
        public Serializer getBodyBuilder(AccountLinkTransaction transaction) {
            return AccountLinkTransactionBodyBuilder.create(
                SerializationUtils.toKeyDto(transaction.getRemoteAccount().getPublicKey()),
                AccountLinkActionDto.rawValueOf(transaction.getLinkAction().getValue()))
                ;

        }

    }

    private static class AccountMetadataTransactionSerializer implements
        TransactionSerializer<AccountMetadataTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_METADATA_TRANSACTION;
        }

        @Override
        public Class<AccountMetadataTransaction> getTransactionClass() {
            return AccountMetadataTransaction.class;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {
            AccountMetadataTransactionBodyBuilder builder = AccountMetadataTransactionBodyBuilder
                .loadFromBinary(stream);
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
        public Serializer getBodyBuilder(AccountMetadataTransaction transaction) {
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
            return TransactionType.MOSAIC_METADATA_TRANSACTION;
        }

        @Override
        public Class<MosaicMetadataTransaction> getTransactionClass() {
            return MosaicMetadataTransaction.class;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {
            MosaicMetadataTransactionBodyBuilder builder = MosaicMetadataTransactionBodyBuilder
                .loadFromBinary(stream);
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
        public Serializer getBodyBuilder(MosaicMetadataTransaction transaction) {
            return MosaicMetadataTransactionBodyBuilder.create(
                SerializationUtils.toKeyDto(transaction.getTargetAccount().getPublicKey()),
                transaction.getScopedMetadataKey().longValue(),
                new UnresolvedMosaicIdDto(transaction.getTargetMosaicId().getId().longValue()),
                (short) transaction.getValueSizeDelta(),
                ByteBuffer.wrap(MetadataTransaction.toByteArray(transaction.getValue()))
            );

        }

    }


    private static class NamespaceMetadataTransactionSerializer implements
        TransactionSerializer<NamespaceMetadataTransaction> {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.NAMESPACE_METADATA_TRANSACTION;
        }

        @Override
        public Class<NamespaceMetadataTransaction> getTransactionClass() {
            return NamespaceMetadataTransaction.class;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {
            NamespaceMetadataTransactionBodyBuilder builder = NamespaceMetadataTransactionBodyBuilder
                .loadFromBinary(stream);
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
        public Serializer getBodyBuilder(NamespaceMetadataTransaction transaction) {
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
            return TransactionType.REGISTER_NAMESPACE;
        }

        @Override
        public Class<NamespaceRegistrationTransaction> getTransactionClass() {
            return NamespaceRegistrationTransaction.class;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            NamespaceRegistrationTransactionBodyBuilder builder = NamespaceRegistrationTransactionBodyBuilder
                .loadFromBinary(stream);

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
        public Serializer getBodyBuilder(NamespaceRegistrationTransaction transaction) {
            NamespaceRegistrationTransactionBodyBuilder txBuilder;
            ByteBuffer namespaceNameByteBuffer = ByteBuffer
                .wrap(StringEncoder.getBytes(transaction.getNamespaceName()));
            NamespaceIdDto namespaceIdDto = new NamespaceIdDto(
                transaction.getNamespaceId().getId().longValue());

            if (transaction.getNamespaceRegistrationType()
                == NamespaceRegistrationType.ROOT_NAMESPACE) {
                txBuilder =
                    NamespaceRegistrationTransactionBodyBuilder.create(
                        new BlockDurationDto(transaction.getDuration()
                            .orElseThrow(() -> new IllegalStateException("Duration is required"))
                            .longValue()),
                        namespaceIdDto,
                        namespaceNameByteBuffer);

            } else {
                txBuilder =
                    NamespaceRegistrationTransactionBodyBuilder.create(
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            SecretLockTransactionBodyBuilder builder = SecretLockTransactionBodyBuilder
                .loadFromBinary(stream);

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
        public Serializer getBodyBuilder(SecretLockTransaction transaction) {
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
            secretBuffer.put(Hex.decode(transaction.getSecret()));
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            SecretProofTransactionBodyBuilder builder = SecretProofTransactionBodyBuilder
                .loadFromBinary(stream);

            LockHashAlgorithmType hashType = LockHashAlgorithmType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getHashAlgorithm().getValue()));
            UnresolvedAddress recipient = SerializationUtils
                .toUnresolvedAddress(builder.getRecipientAddress());
            String secret = SerializationUtils.toHexString(builder.getSecret());
            String proof = ConvertUtils.toHex(builder.getProof().array());

            return SecretProofTransactionFactory
                .create(networkType, hashType, recipient, secret, proof);
        }

        @Override
        public Serializer getBodyBuilder(SecretProofTransaction transaction) {
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
            secretBuffer.put(Hex.decode(transaction.getSecret()));
            return secretBuffer;
        }

        /**
         * Gets proof buffer
         *
         * @param transaction the transaction.
         * @return Proof buffer.
         */
        private ByteBuffer getProofBuffer(SecretProofTransaction transaction) {
            final byte[] proofBytes = Hex.decode(transaction.getProof());
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            AddressAliasTransactionBodyBuilder builder = AddressAliasTransactionBodyBuilder
                .loadFromBinary(stream);

            AliasAction aliasAction = AliasAction
                .rawValueOf(builder.getAliasAction().getValue());
            NamespaceId namespaceId = SerializationUtils
                .toNamespaceId(builder.getNamespaceId());
            Address address = SerializationUtils.toAddress(builder.getAddress());

            return AddressAliasTransactionFactory
                .create(networkType, aliasAction, namespaceId, address);
        }

        @Override
        public Serializer getBodyBuilder(AddressAliasTransaction transaction) {
            NamespaceIdDto namespaceIdDto = new NamespaceIdDto(
                transaction.getNamespaceId().getIdAsLong());
            AliasActionDto aliasActionDto = AliasActionDto
                .rawValueOf(transaction.getAliasAction().getValue());
            AddressDto addressDto = new AddressDto(SerializationUtils
                .fromUnresolvedAddressToByteBuffer(transaction.getAddress(),
                    transaction.getNetworkType()));
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            MosaicAliasTransactionBodyBuilder builder = MosaicAliasTransactionBodyBuilder
                .loadFromBinary(stream);

            AliasAction aliasAction = AliasAction
                .rawValueOf(builder.getAliasAction().getValue());
            NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getNamespaceId());
            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getMosaicId());

            return MosaicAliasTransactionFactory
                .create(networkType, aliasAction, namespaceId, mosaicId);
        }

        @Override
        public Serializer getBodyBuilder(MosaicAliasTransaction transaction) {
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
            return TransactionType.LOCK;
        }

        @Override
        public Class<HashLockTransaction> getTransactionClass() {
            return HashLockTransaction.class;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            HashLockTransactionBodyBuilder builder = HashLockTransactionBodyBuilder
                .loadFromBinary(stream);

            Mosaic mosaic = SerializationUtils.toMosaic(builder.getMosaic());
            BigInteger duration = SerializationUtils
                .toUnsignedBigInteger(builder.getDuration().getBlockDuration());
            SignedTransaction signedTransaction = new SignedTransaction(null,
                SerializationUtils.toHexString(builder.getHash()),
                TransactionType.AGGREGATE_BONDED);
            return HashLockTransactionFactory
                .create(networkType, mosaic, duration, signedTransaction);
        }

        @Override
        public Serializer getBodyBuilder(HashLockTransaction transaction) {
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
            return ByteBuffer.wrap(Hex.decode(transaction.getSignedTransaction().getHash()));
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            AccountAddressRestrictionTransactionBodyBuilder builder = AccountAddressRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

            long restrictionTypeValue = builder.getRestrictionFlags().stream()
                .mapToLong(AccountRestrictionFlagsDto::getValue).sum();

            AccountRestrictionType restrictionType = AccountRestrictionType
                .rawValueOf((int) restrictionTypeValue);

            List<UnresolvedAddress> restrictionAdditions = builder.getRestrictionAdditions()
                .stream()
                .map(SerializationUtils::toUnresolvedAddress)
                .collect(Collectors.toList());

            List<UnresolvedAddress> restrictionDeletions = builder.getRestrictionDeletions()
                .stream()
                .map(SerializationUtils::toUnresolvedAddress)
                .collect(Collectors.toList());

            return AccountAddressRestrictionTransactionFactory
                .create(networkType, restrictionType, restrictionAdditions, restrictionDeletions);
        }

        @Override
        public Serializer getBodyBuilder(AccountAddressRestrictionTransaction transaction) {

            EnumSet<AccountRestrictionFlagsDto> flags = transaction.getRestrictionType().getFlags()
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            AccountMosaicRestrictionTransactionBodyBuilder builder = AccountMosaicRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

            long restrictionTypeValue = builder.getRestrictionFlags().stream()
                .mapToLong(AccountRestrictionFlagsDto::getValue).sum();

            AccountRestrictionType restrictionType = AccountRestrictionType
                .rawValueOf((int) restrictionTypeValue);

            List<UnresolvedMosaicId> restrictionAdditions = builder.getRestrictionAdditions()
                .stream()
                .map(SerializationUtils::toUnresolvedMosaicId)
                .collect(Collectors.toList());

            List<UnresolvedMosaicId> restrictionDeletions = builder.getRestrictionDeletions()
                .stream()
                .map(SerializationUtils::toUnresolvedMosaicId)
                .collect(Collectors.toList());

            return AccountMosaicRestrictionTransactionFactory
                .create(networkType, restrictionType, restrictionAdditions, restrictionDeletions);
        }

        @Override
        public Serializer getBodyBuilder(AccountMosaicRestrictionTransaction transaction) {

            EnumSet<AccountRestrictionFlagsDto> flags = transaction.getRestrictionType().getFlags()
                .stream().map(f -> AccountRestrictionFlagsDto.rawValueOf(
                    (short) f.getValue())).collect(Collectors
                    .toCollection(() -> EnumSet.noneOf(AccountRestrictionFlagsDto.class)));

            List<UnresolvedMosaicIdDto> restrictionAdditions = transaction.getRestrictionAdditions()
                .stream()
                .map(
                    a -> new UnresolvedMosaicIdDto(
                        a.getIdAsLong()))
                .collect(Collectors.toList());

            List<UnresolvedMosaicIdDto> restrictionDeletions = transaction.getRestrictionDeletions()
                .stream()
                .map(a -> new UnresolvedMosaicIdDto(a.getIdAsLong()))
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            AccountOperationRestrictionTransactionBodyBuilder builder = AccountOperationRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

            long restrictionTypeValue = builder.getRestrictionFlags().stream()
                .mapToLong(AccountRestrictionFlagsDto::getValue).sum();

            AccountRestrictionType restrictionType = AccountRestrictionType
                .rawValueOf((int) restrictionTypeValue);

            List<TransactionType> restrictionAdditions = builder.getRestrictionAdditions()
                .stream()
                .map(op -> TransactionType.rawValueOf(op.getValue()))
                .collect(Collectors.toList());

            List<TransactionType> restrictionDeletions = builder.getRestrictionDeletions()
                .stream()
                .map(op -> TransactionType.rawValueOf(op.getValue()))
                .collect(Collectors.toList());

            return AccountOperationRestrictionTransactionFactory
                .create(networkType, restrictionType, restrictionAdditions, restrictionDeletions);
        }

        @Override
        public Serializer getBodyBuilder(AccountOperationRestrictionTransaction transaction) {

            EnumSet<AccountRestrictionFlagsDto> flags = transaction.getRestrictionType().getFlags()
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            MosaicAddressRestrictionTransactionBodyBuilder builder = MosaicAddressRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

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
        public Serializer getBodyBuilder(MosaicAddressRestrictionTransaction transaction) {
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
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            MosaicGlobalRestrictionTransactionBodyBuilder builder = MosaicGlobalRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);
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
        public Serializer getBodyBuilder(MosaicGlobalRestrictionTransaction transaction) {
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
            return TransactionType.MODIFY_MULTISIG_ACCOUNT;
        }

        @Override
        public Class<MultisigAccountModificationTransaction> getTransactionClass() {
            return MultisigAccountModificationTransaction.class;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInputStream stream) {

            MultisigAccountModificationTransactionBodyBuilder builder = MultisigAccountModificationTransactionBodyBuilder
                .loadFromBinary(stream);
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
        public Serializer getBodyBuilder(MultisigAccountModificationTransaction transaction) {

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
        public TransactionFactory<?> fromStream(NetworkType networkType,
            DataInputStream stream) {
            return ExceptionUtils.propagate(
                () -> {
                    final String transactionsHash = SerializationUtils
                        .toHexString(Hash256Dto.loadFromBinary(stream).getHash256());
                    final int payloadSize = Integer.reverseBytes(stream.readInt());
                    Integer.reverseBytes(stream.readInt());
                    final ByteBuffer transactionByteByteBuffer = ByteBuffer.allocate(payloadSize);
                    stream.read(transactionByteByteBuffer.array());
                    List<Transaction> transactions = new ArrayList<>();
                    while (transactionByteByteBuffer.hasRemaining()) {
                        transactions.add(
                            transactionSerialization
                                .deserializeEmbedded(transactionByteByteBuffer));
                    }

                    List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
                    while (stream.available() > 0) {
                        try {
                            CosignatureBuilder cosignatureBuilder = CosignatureBuilder
                                .loadFromBinary(stream);
                            PublicAccount signer =
                                SerializationUtils.toPublicAccount(
                                    cosignatureBuilder.getSignerPublicKey(), networkType);
                            String cosignature =
                                ConvertUtils.toHex(
                                    cosignatureBuilder.getSignature().getSignature().array());
                            cosignatures
                                .add(new AggregateTransactionCosignature(cosignature, signer));
                        } catch (Exception e) {
                            throw new IllegalStateException(
                                org.apache.commons.lang3.exception.ExceptionUtils.getMessage(e), e);
                        }
                    }
                    return AggregateTransactionFactory.create(
                        getTransactionType(), networkType, transactionsHash, transactions,
                        cosignatures);
                });
        }


        @Override
        public Serializer getBodyBuilder(AggregateTransaction transaction) {
            return io.nem.core.utils.ExceptionUtils.propagate(
                () -> {
                    byte[] transactionsBytes = getTransactionBytes(transaction);
                    byte[] cosignaturesBytes = getConsignaturesBytes(transaction);
                    byte[] serialize = GeneratorUtils.serialize(
                        dataOutputStream -> {
                            final byte[] hashBytes = ConvertUtils
                                .getBytes(transaction.getTransactionsHash());
                            final ByteBuffer hashBuffer = ByteBuffer.wrap(hashBytes);
                            dataOutputStream
                                .write(hashBuffer.array(), 0, hashBuffer.array().length);
                            dataOutputStream
                                .writeInt(Integer.reverseBytes(transactionsBytes.length));
                            final Integer aggregateTransactionHeader_Reserved1 = 0;
                            dataOutputStream.writeInt(
                                Integer.reverseBytes(aggregateTransactionHeader_Reserved1));
                            dataOutputStream.write(transactionsBytes);
                            dataOutputStream.write(cosignaturesBytes);
                        });
                    return new Serializer() {
                        @Override
                        public byte[] serialize() {
                            return serialize;
                        }

                        @Override
                        public int getSize() {
                            return serialize.length;
                        }
                    };
                });
        }

        private byte[] getTransactionBytes(AggregateTransaction transaction) {
            byte[] transactionsBytes = new byte[0];
            for (Transaction innerTransaction : transaction.getInnerTransactions()) {
                final byte[] transactionBytes = transactionSerialization
                    .serializeEmbedded(innerTransaction);
                transactionsBytes = ArrayUtils.addAll(transactionsBytes, transactionBytes);
            }
            return transactionsBytes;
        }

        private byte[] getConsignaturesBytes(AggregateTransaction transaction) {
            byte[] cosignaturesBytes = new byte[0];
            for (AggregateTransactionCosignature cosignature : transaction.getCosignatures()) {
                final byte[] signerBytes = cosignature.getSigner().getPublicKey()
                    .getBytes();

                final ByteBuffer signerBuffer = ByteBuffer.wrap(signerBytes);
                final CosignatureBuilder cosignatureBuilder = CosignatureBuilder
                    .create(new KeyDto(signerBuffer),
                        SerializationUtils.toSignatureDto(cosignature.getSignature()));
                byte[] consignaturePayload = cosignatureBuilder.serialize();
                cosignaturesBytes = ArrayUtils
                    .addAll(cosignaturesBytes, consignaturePayload);
            }
            return cosignaturesBytes;
        }


    }


}
