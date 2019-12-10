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

import io.nem.core.crypto.CryptoEngines;
import io.nem.core.crypto.DsaSigner;
import io.nem.core.crypto.SignSchema;
import io.nem.core.crypto.Signature;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.api.BinarySerialization;
import io.nem.sdk.infrastructure.BinarySerializationImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import java.util.Optional;
import org.bouncycastle.util.encoders.Hex;

/**
 * An abstract transaction class that serves as the base class of all NEM transactions.
 *
 * @since 1.0
 */
public abstract class Transaction {

    /**
     * The BinarySerialization object.
     */
    private static final BinarySerialization BINARY_SERIALIZATION = BinarySerializationImpl.INSTANCE;

    private final TransactionType type;
    private final NetworkType networkType;
    private final Integer version;
    private final Deadline deadline;
    private final BigInteger maxFee;
    private final Optional<String> signature;
    private final Optional<TransactionInfo> transactionInfo;
    private Optional<PublicAccount> signer;

    /**
     * Abstract constructors of all transactions.
     */
    Transaction(TransactionFactory<?> factory) {
        this.type = factory.getType();
        this.networkType = factory.getNetworkType();
        this.version = factory.getVersion();
        this.deadline = factory.getDeadline();
        this.maxFee = factory.getMaxFee();
        this.signature = factory.getSignature();
        this.signer = factory.getSigner();
        this.transactionInfo = factory.getTransactionInfo();
    }


    /**
     * Returns the transaction type.
     *
     * @return transaction type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Returns the network type.
     *
     * @return the network type
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Returns the transaction version.
     *
     * @return transaction version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Returns the deadline to include the transaction.
     *
     * @return deadline to include transaction into a block.
     */
    public Deadline getDeadline() {
        return deadline;
    }

    /**
     * Returns the fee for the transaction. The higher the fee, the higher the priority of the
     * transaction. Transactions with high priority get included in a block before transactions with
     * lower priority.
     *
     * @return fee amount
     */
    public BigInteger getMaxFee() {
        return maxFee;
    }

    /**
     * Returns the transaction signature (missing if part of an aggregate transaction).
     *
     * @return transaction signature
     */
    public Optional<String> getSignature() {
        return signature;
    }

    /**
     * Returns the transaction creator public account.
     *
     * @return signer public account
     */
    public Optional<PublicAccount> getSigner() {
        return signer;
    }

    /**
     * Returns meta data object contains additional information about the transaction.
     *
     * @return transaction meta data info.
     */
    public Optional<TransactionInfo> getTransactionInfo() {
        return transactionInfo;
    }

    /**
     * Serialises a transaction model into binary (unsigned payload). Gets the serialised bytes for
     * a transaction.
     *
     * @return bytes of the transaction
     */
    public byte[] serialize() {
        return BINARY_SERIALIZATION.serialize(this);
    }


    /**
     * It returns the transaction's byte array size useful to calculate its fee.
     *
     * @return the size of the transaction.
     */
    public int getSize() {
        return BINARY_SERIALIZATION.getSize(this);
    }


    /**
     * Generates hash for a serialized transaction payload.
     *
     * @param transactionPayload Transaction payload
     * @param generationHashBytes the generation hash.
     * @return generated transaction hash.
     */
    public String createTransactionHash(
        String transactionPayload, final byte[] generationHashBytes) {
        byte[] bytes = Hex.decode(transactionPayload);
        final byte[] dataBytes = getSignBytes(bytes, generationHashBytes);
        byte[] signingBytes = new byte[dataBytes.length + 64];
        System.arraycopy(bytes, 8, signingBytes, 0, 32);
        System.arraycopy(bytes, 72, signingBytes, 32, 32);
        System.arraycopy(dataBytes, 0, signingBytes, 64, dataBytes.length);
        byte[] result = SignSchema.toHash32Bytes(SignSchema.SHA3, signingBytes);
        return Hex.toHexString(result).toUpperCase();
    }

    /**
     * Get the bytes required for signing.
     *
     * @param payloadBytes Payload bytes.
     * @param generationHashBytes Generation hash bytes.
     * @return Bytes to sign.
     */
    public byte[] getSignBytes(final byte[] payloadBytes, final byte[] generationHashBytes) {
        final short headerSize = 4 + 32 + 64 + 8;
        final byte[] signingBytes = new byte[payloadBytes.length + generationHashBytes.length
            - headerSize];
        System.arraycopy(generationHashBytes, 0, signingBytes, 0, generationHashBytes.length);
        System.arraycopy(payloadBytes, headerSize, signingBytes, generationHashBytes.length,
            payloadBytes.length - headerSize);
        return signingBytes;
    }


    /**
     * Serialize and sign transaction creating a new SignedTransaction.
     *
     * @param account The account to sign the transaction.
     * @param generationHash The generation hash for the network.
     * @return {@link SignedTransaction}
     */
    public SignedTransaction signWith(final Account account, final String generationHash) {
        final DsaSigner theSigner = CryptoEngines.defaultEngine()
            .createDsaSigner(account.getKeyPair(), getNetworkType().resolveSignSchema());
        final byte[] bytes = this.serialize();
        final byte[] generationHashBytes = ConvertUtils.getBytes(generationHash);
        final byte[] signingBytes = getSignBytes(bytes, generationHashBytes);
        final Signature theSignature = theSigner.sign(signingBytes);

        final byte[] payload = new byte[bytes.length];
        System.arraycopy(bytes, 0, payload, 0, 8); // Size
        System.arraycopy(theSignature.getBytes(), 0, payload, 8,
            theSignature.getBytes().length); // Signature
        System.arraycopy(
            account.getKeyPair().getPublicKey().getBytes(), 0, payload, 64 + 8,
            account.getKeyPair().getPublicKey().getBytes().length); // Signer
        System.arraycopy(bytes, 104, payload, 104, bytes.length - 104);

        final String hash = createTransactionHash(Hex.toHexString(payload), generationHashBytes);
        return new SignedTransaction(account.getPublicAccount(),
            Hex.toHexString(payload).toUpperCase(), hash, type);
    }

    /**
     * Convert an aggregate transaction to an inner transaction including transaction signer.
     *
     * @param signer Transaction signer.
     * @return instance of Transaction with signer
     */
    public Transaction toAggregate(final PublicAccount signer) {
        this.signer = Optional.of(signer);
        return this;
    }

    /**
     * Returns if a transaction is pending to be included in a block.
     *
     * @return if a transaction is pending to be included in a block
     */
    public boolean isUnconfirmed() {
        return getTransactionInfo().filter(info -> info.getHeight().equals(BigInteger.valueOf(0))
            && info.getHash().equals(info.getMerkleComponentHash())).isPresent();

    }

    /**
     * Return if a transaction is included in a block.
     *
     * @return if a transaction is included in a block
     */
    public boolean isConfirmed() {
        return this.getTransactionInfo().filter(info -> info.getHeight().intValue() > 0)
            .isPresent();
    }

    /**
     * Returns if a transaction has missing signatures.
     *
     * @return if a transaction has missing signatures
     */
    public boolean hasMissingSignatures() {
        return this.getTransactionInfo()
            .filter(info -> info.getHeight().equals(BigInteger.valueOf(0))
                && !info.getHash().equals(info.getMerkleComponentHash())).isPresent();
    }

    /**
     * Returns if a transaction is not known by the network.
     *
     * @return if a transaction is not known by the network
     */
    public boolean isUnannounced() {
        return !this.getTransactionInfo().isPresent();
    }

}
