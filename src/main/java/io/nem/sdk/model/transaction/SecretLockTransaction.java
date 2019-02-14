/*
 * Copyright 2018 NEM
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

import com.google.flatbuffers.FlatBufferBuilder;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.Validate;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SecretLockTransaction extends Transaction {
    private final Mosaic mosaic;
    private final BigInteger duration;
    private final HashType hashType;
    private final String secret;
    private final Address recipient;
    private final Schema schema = new SecretLockTransactionSchema();


    public SecretLockTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Mosaic mosaic, BigInteger duration, HashType hashType, String secret, Address recipient, String signature, PublicAccount signer, TransactionInfo transactionInfo) {
        this(networkType, version, deadline, fee, mosaic, duration, hashType, secret, recipient, Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
    }

    public SecretLockTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Mosaic mosaic, BigInteger duration, HashType hashType, String secret, Address recipient) {
        this(networkType, version, deadline, fee, mosaic, duration, hashType, secret, recipient, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public SecretLockTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Mosaic mosaic, BigInteger duration, HashType hashType, String secret, Address recipient, Optional<String> signature, Optional<PublicAccount> signer, Optional<TransactionInfo> transactionInfo) {
        super(TransactionType.SECRET_LOCK, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(mosaic, "Mosaic must not be null");
        Validate.notNull(duration, "Duration must not be null");
        Validate.notNull(secret, "Secret must not be null");
        Validate.notNull(recipient, "Recipient must not be null");
        if (!HashType.Validator(hashType, secret)) {
            throw new IllegalArgumentException("HashType and Secret have incompatible length or not hexadecimal string");
        }
        this.mosaic = mosaic;
        this.duration = duration;
        this.hashType = hashType;
        this.secret = secret;
        this.recipient = recipient;
    }

    /**
     * Create a secret lock transaction object.
     *
     * @param deadline          The deadline to include the transaction.
     * @param mosaic            The locked mosaic.
     * @param duration          The duration for the funds to be released or returned.
     * @param hashType          The hash algorithm secret is generated with.
     * @param secret            The proof hashed.
     * @param recipient         The recipient of the funds.
     * @param networkType       The network type.
     *
     * @return a SecretLockTransaction instance
     */
    public static SecretLockTransaction create(Deadline deadline, Mosaic mosaic, BigInteger duration, HashType hashType, String secret, Address recipient, NetworkType networkType) {
        return new SecretLockTransaction(networkType, 3, deadline, BigInteger.valueOf(0), mosaic, duration, hashType, secret, recipient);
    }

    /**
     * Returns locked mosaic.
     *
     * @return locked mosaic.
     */
    public Mosaic getMosaic() { return mosaic; }

    /**
     * Returns duration for the funds to be released or returned.
     *
     * @return duration for the funds to be released or returned.
     */
    public BigInteger getDuration() { return duration; }

    /**
     * Returns the hash algorithm, secret is generated with.
     *
     * @return the hash algorithm, secret is generated with.
     */
    public HashType getHashType() { return hashType; }

    /**
     * Returns the proof hashed.
     *
     * @return the proof hashed.
     */
    public String getSecret() { return secret; }

    /**
     * Returns the recipient of the funds.
     *
     * @return the recipient of the funds.
     */
    public Address getRecipient() { return recipient; }

    @Override
    byte[] generateBytes() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        BigInteger deadlineBigInt = BigInteger.valueOf(getDeadline().getInstant());
        int[] fee = new int[]{0, 0};
        int version = (int) Long.parseLong(Integer.toHexString(getNetworkType().getValue()) + "0" + Integer.toHexString(getVersion()), 16);

        // Create Vectors
        int signatureVector = SecretLockTransactionBuffer.createSignatureVector(builder, new byte[64]);
        int signerVector = SecretLockTransactionBuffer.createSignerVector(builder, new byte[32]);
        int deadlineVector = SecretLockTransactionBuffer.createDeadlineVector(builder, UInt64.fromBigInteger(deadlineBigInt));
        int feeVector = SecretLockTransactionBuffer.createFeeVector(builder, fee);
        int mosaicIdVector = SecretLockTransactionBuffer.createMosaicIdVector(builder, UInt64.fromBigInteger(mosaic.getId().getId()));
        int mosaicAmountVector = SecretLockTransactionBuffer.createMosaicAmountVector(builder, UInt64.fromBigInteger(mosaic.getAmount()));
        int durationVector = SecretLockTransactionBuffer.createDurationVector(builder, UInt64.fromBigInteger(duration));
        int secretVector = SecretLockTransactionBuffer.createSecretVector(builder, Hex.decode(secret));

        byte[] address = new Base32().decode(getRecipient().plain().getBytes(StandardCharsets.UTF_8));
        int recipientVector = SecretLockTransactionBuffer.createRecipientVector(builder, address);

        SecretLockTransactionBuffer.startSecretLockTransactionBuffer(builder);
        SecretLockTransactionBuffer.addSize(builder, 234);
        SecretLockTransactionBuffer.addSignature(builder, signatureVector);
        SecretLockTransactionBuffer.addSigner(builder, signerVector);
        SecretLockTransactionBuffer.addVersion(builder, version);
        SecretLockTransactionBuffer.addType(builder, getType().getValue());
        SecretLockTransactionBuffer.addFee(builder, feeVector);
        SecretLockTransactionBuffer.addDeadline(builder, deadlineVector);
        SecretLockTransactionBuffer.addMosaicId(builder, mosaicIdVector);
        SecretLockTransactionBuffer.addMosaicAmount(builder, mosaicAmountVector);
        SecretLockTransactionBuffer.addDuration(builder, durationVector);
        SecretLockTransactionBuffer.addHashAlgorithm(builder, hashType.getValue());
        SecretLockTransactionBuffer.addSecret(builder, secretVector);
        SecretLockTransactionBuffer.addRecipient(builder, recipientVector);

        int codedSecretLock = SecretLockTransactionBuffer.endSecretLockTransactionBuffer(builder);
        builder.finish(codedSecretLock);

        return schema.serialize(builder.sizedByteArray());
    }
}
