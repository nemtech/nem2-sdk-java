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

import io.nem.catapult.builders.AccountAddressRestrictionModificationBuilder;
import io.nem.catapult.builders.AccountAddressRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.AccountLinkTransactionBodyBuilder;
import io.nem.catapult.builders.AccountMetadataTransactionBodyBuilder;
import io.nem.catapult.builders.AccountMosaicRestrictionModificationBuilder;
import io.nem.catapult.builders.AccountMosaicRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.AccountOperationRestrictionModificationBuilder;
import io.nem.catapult.builders.AccountOperationRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.AddressAliasTransactionBodyBuilder;
import io.nem.catapult.builders.AggregateTransactionBuilder;
import io.nem.catapult.builders.BlockDurationDto;
import io.nem.catapult.builders.CosignatoryModificationBuilder;
import io.nem.catapult.builders.CosignatureBuilder;
import io.nem.catapult.builders.EmbeddedTransactionBuilder;
import io.nem.catapult.builders.HashLockTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicAddressRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicAliasTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicDefinitionTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicFlagsDto;
import io.nem.catapult.builders.MosaicGlobalRestrictionTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicMetadataTransactionBodyBuilder;
import io.nem.catapult.builders.MosaicSupplyChangeTransactionBodyBuilder;
import io.nem.catapult.builders.MultisigAccountModificationTransactionBodyBuilder;
import io.nem.catapult.builders.NamespaceMetadataTransactionBodyBuilder;
import io.nem.catapult.builders.NamespaceRegistrationTransactionBodyBuilder;
import io.nem.catapult.builders.SecretLockTransactionBodyBuilder;
import io.nem.catapult.builders.SecretProofTransactionBodyBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TransactionBuilder;
import io.nem.catapult.builders.TransferTransactionBodyBuilder;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.api.BinarySerialization;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.message.MessageType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountLinkTransactionFactory;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.CosignatoryModificationActionType;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.io.DataInput;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;


/**
 * Implementation of BinarySerialization. It uses the catbuffer generated builders to deserialize an
 * object.
 */
public class BinarySerializationImpl implements BinarySerialization {

    /**
     * The deserializers, one per {@link TransactionType} must be registered.
     */
    private final Map<TransactionType, TransactionDeserializer> deserializers = new HashMap<>();

    public BinarySerializationImpl() {
        register(new TransferTransactionDeserializer());
        register(new MosaicSupplyChangeTransactionDeserializer());
        register(new MosaicDefinitionTransactionDeserializer());
        register(new AccountLinkTransactionDeserializer());
        register(new AccountMetadataTransactionDeserializer());
        register(new MosaicMetadataTransactionDeserializer());
        register(new NamespaceMetadataTransactionDeserializer());
        register(new NamespaceRegistrationTransactionDeserializer());
        register(new SecretLockTransactionDeserializer());
        register(new SecretProofTransactionDeserializer());
        register(new AddressAliasTransactionDeserializer());
        register(new MosaicAliasTransactionDeserializer());
        register(new HashLockTransactionDeserializer());
        register(new MultisigAccountModificationTransactionDeserializer());
        register(new MosaicAddressRestrictionTransactionDeserializer());
        register(new MosaicGlobalRestrictionTransactionDeserializer());
        register(new AccountMosaicRestrictionTransactionDeserializer());
        register(new AccountOperationRestrictionTransactionDeserializer());
        register(new AccountAddressRestrictionTransactionDeserializer());
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

    TransactionDeserializer resolveMapper(TransactionType transactionType) {
        TransactionDeserializer mapper = deserializers.get(transactionType);
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
    public byte[] serialize(Transaction transaction) {
        Validate.notNull(transaction, "Transaction must not be null");
        return transaction.serialize();
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
    public Transaction deserialize(byte[] payload) {
        Validate.notNull(payload, "Payload must not be null");
        DataInput stream = SerializationUtils.toDataInput(payload);
        TransactionBuilder builder = TransactionBuilder.loadFromBinary(stream);
        TransactionType transactionType = TransactionType
            .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
        int networkVersion = SerializationUtils.shortToUnsignedInt(builder.getVersion());
        NetworkType networkType = MapperUtils.extractNetworkType(networkVersion);
        int version = MapperUtils.extractTransactionVersion(networkVersion);
        Deadline deadline = new Deadline(
            SerializationUtils.toUnsignedBigInteger(builder.getDeadline().getTimestamp()));
        TransactionFactory<?> factory = resolveMapper(transactionType)
            .fromStream(networkType, stream, payload);
        factory.version(version);
        factory.maxFee(SerializationUtils.toUnsignedBigInteger(builder.getFee()));
        factory.deadline(deadline);
        if (!areAllZeros(builder.getSignature().getSignature().array())) {
            factory.signature(SerializationUtils.toHexString(builder.getSignature()));
        }
        if (!areAllZeros(builder.getSigner().getKey().array())) {
            factory.signer(SerializationUtils.toPublicAccount(builder.getSigner(), networkType));
        }

        return factory.build();
    }

    private boolean areAllZeros(byte[] array) {
        for (byte b : array) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }

    public Transaction deserializeEmbedded(byte[] payload) {
        return deserializeEmbedded(SerializationUtils.toDataInput(payload));
    }

    public Transaction deserializeEmbedded(ByteBuffer payload) {
        return deserializeEmbedded(new DataInputStream(new ByteBufferBackedInputStream(payload)));
    }

    public Transaction deserializeEmbedded(DataInput stream) {
        EmbeddedTransactionBuilder builder = EmbeddedTransactionBuilder.loadFromBinary(stream);
        TransactionType transactionType = TransactionType
            .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
        int networkVersion = SerializationUtils.shortToUnsignedInt(builder.getVersion());
        NetworkType networkType = MapperUtils.extractNetworkType(networkVersion);
        int version = MapperUtils.extractTransactionVersion(networkVersion);
        TransactionFactory<?> factory = resolveMapper(transactionType)
            .fromStream(networkType, stream, null);
        factory.signer(SerializationUtils.toPublicAccount(builder.getSigner(), networkType));
        factory.version(version);
        return factory.build();
    }


    /**
     * Interface of the deserializer helper classes that know how to deserialize one type of
     * transaction from a payload.
     */
    interface TransactionDeserializer {

        /**
         * @return the {@link TransactionType} of the transaction this helper handles.
         */
        TransactionType getTransactionType();

        /**
         * Subclasses would need to create the {@link TransactionFactory} for the handled {@link
         * TransactionType} with just the specific transaction values. Common values like maxFee and
         * deadline are handled at top level, subclasses won't need to duplicate the deserialization
         * efforts.
         *
         * @param networkType the network type
         * @param stream the stream containing just the specific transaction values in the right
         * order.
         * @param originalPayload the full original payload, used when deserializing Aggregate
         * Transactions.
         * @return the TransactionFactory of the transaction type this object handles.
         */
        TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload);

    }

    private static class TransferTransactionDeserializer implements TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.TRANSFER;
        }

        @Override
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
            TransferTransactionBodyBuilder builder = TransferTransactionBodyBuilder
                .loadFromBinary(stream);
            byte[] messageArray = builder.getMessage().array();
            MessageType messageType = MessageType
                .rawValueOf(SerializationUtils.byteToUnsignedInt(messageArray[0]));
            String messageHex = ConvertUtils.toHex(messageArray).substring(2);
            Address recipient = MapperUtils
                .toAddressFromUnresolved(
                    ConvertUtils.toHex(builder.getRecipient().getUnresolvedAddress().array()));
            List<Mosaic> mosaics = builder.getMosaics().stream()
                .map(SerializationUtils::toMosaic)
                .collect(Collectors.toList());
            Message message = Message.createFromPayload(messageType, messageHex);
            return TransferTransactionFactory.create(networkType,
                recipient, mosaics, message);

        }

    }

    private static class MosaicSupplyChangeTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_SUPPLY_CHANGE;
        }

        @Override
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
            MosaicSupplyChangeTransactionBodyBuilder builder = MosaicSupplyChangeTransactionBodyBuilder
                .loadFromBinary(stream);
            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getMosaicId());
            MosaicSupplyChangeActionType action = MosaicSupplyChangeActionType
                .rawValueOf(builder.getAction().getValue());
            BigInteger delta = SerializationUtils.toUnsignedBigInteger(builder.getDelta());
            return MosaicSupplyChangeTransactionFactory
                .create(networkType, mosaicId, action, delta);
        }
    }

    private static class MosaicDefinitionTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_DEFINITION;
        }

        @Override
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
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
    }

    private static class AccountLinkTransactionDeserializer implements TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_LINK;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
            AccountLinkTransactionBodyBuilder builder = AccountLinkTransactionBodyBuilder
                .loadFromBinary(stream);
            PublicAccount remoteAccount = SerializationUtils
                .toPublicAccount(builder.getRemoteAccountPublicKey(), networkType);
            AccountLinkAction linkAction = AccountLinkAction
                .rawValueOf(builder.getLinkAction().getValue());
            return AccountLinkTransactionFactory
                .create(networkType, remoteAccount, linkAction);
        }
    }

    private static class AccountMetadataTransactionDeserializer implements TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_METADATA_TRANSACTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
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
    }

    private static class MosaicMetadataTransactionDeserializer implements TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_METADATA_TRANSACTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
            MosaicMetadataTransactionBodyBuilder builder = MosaicMetadataTransactionBodyBuilder
                .loadFromBinary(stream);
            PublicAccount targetAccount = SerializationUtils
                .toPublicAccount(builder.getTargetPublicKey(), networkType);
            BigInteger scopedMetadataKey = SerializationUtils
                .toUnsignedBigInteger(builder.getScopedMetadataKey());
            String value = StringEncoder.getString(builder.getValue().array());
            MosaicId targetMosaicId = SerializationUtils.toMosaicId(builder.getTargetMosaicId());
            return MosaicMetadataTransactionFactory
                .create(networkType, targetAccount, targetMosaicId, scopedMetadataKey, value)
                .valueSizeDelta(SerializationUtils.shortToUnsignedInt(builder.getValueSizeDelta()));
        }
    }


    private static class NamespaceMetadataTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.NAMESPACE_METADATA_TRANSACTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
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
    }

    private static class NamespaceRegistrationTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.REGISTER_NAMESPACE;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            NamespaceRegistrationTransactionBodyBuilder builder = NamespaceRegistrationTransactionBodyBuilder
                .loadFromBinary(stream);

            NamespaceRegistrationType namespaceRegistrationType = NamespaceRegistrationType
                .rawValueOf(builder.getRegistrationType().getValue());
            String namespaceName = StringEncoder.getString(builder.getName().array());
            NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getId());

            Optional<BigInteger> duration =
                namespaceRegistrationType == NamespaceRegistrationType.ROOT_NAMESPACE ? Optional
                    .ofNullable(builder.getDuration()).map(
                        BlockDurationDto::getBlockDuration)
                    .map(SerializationUtils::toUnsignedBigInteger)
                    : Optional.empty();

            Optional<NamespaceId> parentId =
                namespaceRegistrationType == NamespaceRegistrationType.SUB_NAMESPACE ? Optional
                    .of(builder.getParentId())
                    .map(SerializationUtils::toNamespaceId) : Optional.empty();

            return NamespaceRegistrationTransactionFactory
                .create(networkType, namespaceName, namespaceId, namespaceRegistrationType,
                    duration, parentId);
        }
    }

    private static class SecretLockTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.SECRET_LOCK;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            SecretLockTransactionBodyBuilder builder = SecretLockTransactionBodyBuilder
                .loadFromBinary(stream);

            Mosaic mosaic = SerializationUtils.toMosaic(builder.getMosaic());
            BigInteger duration = SerializationUtils
                .toUnsignedBigInteger(builder.getDuration().getBlockDuration());
            LockHashAlgorithmType hashAlgorithm = LockHashAlgorithmType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getHashAlgorithm().getValue()));
            String secret = SerializationUtils.toHexString(builder.getSecret());
            Address recipient = SerializationUtils.toAddress(builder.getRecipient());
            return SecretLockTransactionFactory
                .create(networkType, mosaic, duration, hashAlgorithm, secret, recipient);
        }
    }

    private static class SecretProofTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.SECRET_PROOF;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            SecretProofTransactionBodyBuilder builder = SecretProofTransactionBodyBuilder
                .loadFromBinary(stream);

            LockHashAlgorithmType hashType = LockHashAlgorithmType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getHashAlgorithm().getValue()));
            Address recipient = SerializationUtils.toAddress(builder.getRecipient());
            String secret = SerializationUtils.toHexString(builder.getSecret());
            String proof = ConvertUtils.toHex(builder.getProof().array());

            return SecretProofTransactionFactory
                .create(networkType, hashType, recipient, secret, proof);
        }
    }

    private static class AddressAliasTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ADDRESS_ALIAS;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            AddressAliasTransactionBodyBuilder builder = AddressAliasTransactionBodyBuilder
                .loadFromBinary(stream);

            AliasAction aliasAction = AliasAction.rawValueOf(builder.getAliasAction().getValue());
            NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getNamespaceId());
            Address address = SerializationUtils.toAddress(builder.getAddress());

            return AddressAliasTransactionFactory
                .create(networkType, aliasAction, namespaceId, address);
        }
    }

    private static class MosaicAliasTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_ALIAS;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            MosaicAliasTransactionBodyBuilder builder = MosaicAliasTransactionBodyBuilder
                .loadFromBinary(stream);

            AliasAction aliasAction = AliasAction.rawValueOf(builder.getAliasAction().getValue());
            NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getNamespaceId());
            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getMosaicId());

            return MosaicAliasTransactionFactory
                .create(networkType, aliasAction, namespaceId, mosaicId);
        }
    }

    private static class HashLockTransactionDeserializer implements TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.LOCK;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

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
    }


    private static class AccountAddressRestrictionTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_ADDRESS_RESTRICTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            AccountAddressRestrictionTransactionBodyBuilder builder = AccountAddressRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

            AccountRestrictionType restrictionType = AccountRestrictionType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getRestrictionType().getValue()));
            List<AccountRestrictionModification<Address>> modifications = builder.getModifications()
                .stream().map(this::toAccountRestrictionModificationAddress)
                .collect(Collectors.toList());
            return AccountAddressRestrictionTransactionFactory
                .create(networkType, restrictionType, modifications);
        }

        private AccountRestrictionModification<Address> toAccountRestrictionModificationAddress(
            AccountAddressRestrictionModificationBuilder builder) {
            AccountRestrictionModificationAction modificationType = AccountRestrictionModificationAction
                .rawValueOf(builder.getModificationAction().getValue());
            Address address = SerializationUtils.toAddress(builder.getValue());
            return AccountRestrictionModification.createForAddress(modificationType, address);
        }
    }

    private static class AccountMosaicRestrictionTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_MOSAIC_RESTRICTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            AccountMosaicRestrictionTransactionBodyBuilder builder = AccountMosaicRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

            AccountRestrictionType restrictionType = AccountRestrictionType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getRestrictionType().getValue()));
            List<AccountRestrictionModification<MosaicId>> modifications = builder
                .getModifications()
                .stream().map(this::toAccountRestrictionModificationMosaic)
                .collect(Collectors.toList());
            return AccountMosaicRestrictionTransactionFactory
                .create(networkType, restrictionType, modifications);
        }

        private AccountRestrictionModification<MosaicId> toAccountRestrictionModificationMosaic(
            AccountMosaicRestrictionModificationBuilder builder) {
            AccountRestrictionModificationAction modificationType = AccountRestrictionModificationAction
                .rawValueOf(builder.getModificationAction().getValue());
            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getValue());
            return AccountRestrictionModification.createForMosaic(modificationType, mosaicId);
        }
    }

    private static class AccountOperationRestrictionTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.ACCOUNT_OPERATION_RESTRICTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            AccountOperationRestrictionTransactionBodyBuilder builder = AccountOperationRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

            AccountRestrictionType restrictionType = AccountRestrictionType.rawValueOf(
                SerializationUtils.byteToUnsignedInt(builder.getRestrictionType().getValue()));
            List<AccountRestrictionModification<TransactionType>> modifications = builder
                .getModifications()
                .stream().map(this::toAccountRestrictionModificationOperation)
                .collect(Collectors.toList());
            return AccountOperationRestrictionTransactionFactory
                .create(networkType, restrictionType, modifications);
        }

        private AccountRestrictionModification<TransactionType> toAccountRestrictionModificationOperation(
            AccountOperationRestrictionModificationBuilder builder) {
            AccountRestrictionModificationAction modificationType = AccountRestrictionModificationAction
                .rawValueOf(builder.getModificationAction().getValue());
            TransactionType transactionType = TransactionType
                .rawValueOf(SerializationUtils.shortToUnsignedInt(builder.getValue().getValue()));
            return AccountRestrictionModification
                .createForTransactionType(modificationType, transactionType);
        }
    }

    private static class MosaicAddressRestrictionTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_ADDRESS_RESTRICTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            MosaicAddressRestrictionTransactionBodyBuilder builder = MosaicAddressRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);

            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getMosaicId());
            BigInteger restrictionKey = SerializationUtils
                .toUnsignedBigInteger(builder.getRestrictionKey());
            Address targetAddress = SerializationUtils.toAddress(builder.getTargetAddress());
            BigInteger newRestrictionValue = SerializationUtils
                .toUnsignedBigInteger(builder.getNewRestrictionValue());
            BigInteger previousRestrictionValue = SerializationUtils
                .toUnsignedBigInteger(builder.getPreviousRestrictionValue());
            return MosaicAddressRestrictionTransactionFactory
                .create(networkType, mosaicId, restrictionKey, targetAddress, newRestrictionValue)
                .previousRestrictionValue(previousRestrictionValue);
        }

    }

    private static class MosaicGlobalRestrictionTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MOSAIC_GLOBAL_RESTRICTION;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            MosaicGlobalRestrictionTransactionBodyBuilder builder = MosaicGlobalRestrictionTransactionBodyBuilder
                .loadFromBinary(stream);
            MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getMosaicId());
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
                .referenceMosaicId(SerializationUtils.toMosaicId(builder.getReferenceMosaicId()))
                .previousRestrictionValue(previousRestrictionValue)
                .previousRestrictionType(previousRestrictionType);
        }
    }


    private static class MultisigAccountModificationTransactionDeserializer implements
        TransactionDeserializer {

        @Override
        public TransactionType getTransactionType() {
            return TransactionType.MODIFY_MULTISIG_ACCOUNT;
        }

        @Override
        public TransactionFactory fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {

            MultisigAccountModificationTransactionBodyBuilder builder = MultisigAccountModificationTransactionBodyBuilder
                .loadFromBinary(stream);

            byte minApprovalDelta = builder.getMinApprovalDelta();
            byte minRemovalDelta = builder.getMinRemovalDelta();
            List<MultisigCosignatoryModification> modifications = builder.getModifications()
                .stream().map(builder1 -> toMultisigCosignatoryModification(builder1, networkType))
                .collect(Collectors.toList());

            return MultisigAccountModificationTransactionFactory
                .create(networkType, minApprovalDelta, minRemovalDelta, modifications);
        }

        private MultisigCosignatoryModification toMultisigCosignatoryModification(
            CosignatoryModificationBuilder builder,
            NetworkType networkType) {
            CosignatoryModificationActionType modificationAction = CosignatoryModificationActionType
                .rawValueOf(SerializationUtils
                    .byteToUnsignedInt(builder.getModificationAction().getValue()));
            PublicAccount cosignatoryPublicAccount = SerializationUtils
                .toPublicAccount(builder.getCosignatoryPublicKey(), networkType);
            return new MultisigCosignatoryModification(modificationAction,
                cosignatoryPublicAccount);
        }
    }


    private static class AggregateTransactionDeserializer implements
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
        public TransactionFactory<?> fromStream(NetworkType networkType, DataInput stream,
            byte[] originalPayload) {
            AggregateTransactionBuilder builder = AggregateTransactionBuilder
                .loadFromBinary(SerializationUtils.toDataInput(originalPayload));

            List<Transaction> transactions = new ArrayList<>();
            ByteBuffer transactionByteByteBuffer = builder.getTransactions();
            while (transactionByteByteBuffer.hasRemaining()) {
                transactions
                    .add(transactionSerialization.deserializeEmbedded(transactionByteByteBuffer));
            }

            List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
            ByteBuffer cosignaturesByteBuffer = builder.getCosignatures();
            while (cosignaturesByteBuffer.hasRemaining()) {
                try {
                    CosignatureBuilder cosignatureBuilder = CosignatureBuilder.loadFromBinary(
                        new DataInputStream(
                            new ByteBufferBackedInputStream(cosignaturesByteBuffer)));
                    PublicAccount signer = SerializationUtils
                        .toPublicAccount(cosignatureBuilder.getSigner(), networkType);
                    String cosignature = ConvertUtils
                        .toHex(cosignatureBuilder.getSignature().getSignature().array());
                    cosignatures.add(new AggregateTransactionCosignature(cosignature, signer));
                } catch (Exception e) {
                    throw new IllegalStateException(ExceptionUtils.getMessage(e), e);
                }
            }
            return AggregateTransactionFactory
                .create(getTransactionType(), networkType, transactions, cosignatures);
        }

    }
}
