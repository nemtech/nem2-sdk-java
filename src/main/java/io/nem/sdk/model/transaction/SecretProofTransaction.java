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
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang3.Validate;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Optional;

public class SecretProofTransaction extends Transaction {
    private final HashType hashType;
    private final String secret;
    private final String proof;
    private final Schema schema = new SecretProofTransactionSchema();

    public SecretProofTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, HashType hashType, String secret, String proof, String signature, PublicAccount signer, TransactionInfo transactionInfo) {
        this(networkType, version, deadline, fee, hashType, secret, proof, Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
    }

    public SecretProofTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, HashType hashType, String secret, String proof) {
        this(networkType, version, deadline, fee, hashType, secret, proof, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public SecretProofTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, HashType hashType, String secret, String proof, Optional<String> signature, Optional<PublicAccount> signer, Optional<TransactionInfo> transactionInfo) {
        super(TransactionType.SECRET_PROOF, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(secret, "Secret must not be null");
        Validate.notNull(proof, "Proof must not be null");
        if (!HashType.Validator(hashType, secret)) {
            throw new IllegalArgumentException("HashType and Secret have incompatible length or not hexadecimal string");
        }
        this.hashType = hashType;
        this.secret = secret;
        this.proof = proof;
    }

    /**
     * Create a secret proof transaction object.
     *
     * @param deadline          The deadline to include the transaction.
     * @param hashType          The hash algorithm secret is generated with.
     * @param secret            The seed proof hashed.
     * @param proof             The seed proof.
     * @param networkType       The network type.
     *
     * @return a SecretLockTransaction instance
     */
    public static SecretProofTransaction create(Deadline deadline, HashType hashType, String secret, String proof, NetworkType networkType) {
        return new SecretProofTransaction(networkType, 3, deadline, BigInteger.valueOf(0), hashType, secret, proof);
    }

    /**
     * Returns the hash algorithm secret is generated with.
     *
     * @return the hash algorithm secret is generated with.
     */
    public HashType getHashType() { return hashType; }

    /**
     * Returns the proof hashed.
     *
     * @return the proof hashed.
     */
    public String getSecret() { return secret; }

    /**
     * Returns proof.
     *
     * @return proof.
     */
    public String getProof() { return proof; }


    @Override
    byte[] generateBytes() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        BigInteger deadlineBigInt = BigInteger.valueOf(getDeadline().getInstant());
        int[] fee = new int[]{0, 0};
        int version = (int) Long.parseLong(Integer.toHexString(getNetworkType().getValue()) + "0" + Integer.toHexString(getVersion()), 16);

        // Create Vectors
        int signatureVector = SecretProofTransactionBuffer.createSignatureVector(builder, new byte[64]);
        int signerVector = SecretProofTransactionBuffer.createSignerVector(builder, new byte[32]);
        int deadlineVector = SecretProofTransactionBuffer.createDeadlineVector(builder, UInt64.fromBigInteger(deadlineBigInt));
        int feeVector = SecretProofTransactionBuffer.createFeeVector(builder, fee);
        int secretVector = SecretProofTransactionBuffer.createSecretVector(builder, Hex.decode(secret));
        int proofVector = SecretProofTransactionBuffer.createProofVector(builder, Hex.decode(proof));

        SecretProofTransactionBuffer.startSecretProofTransactionBuffer(builder);
        SecretProofTransactionBuffer.addSize(builder, 187 + Hex.decode(proof).length);
        SecretProofTransactionBuffer.addSignature(builder, signatureVector);
        SecretProofTransactionBuffer.addSigner(builder, signerVector);
        SecretProofTransactionBuffer.addVersion(builder, version);
        SecretProofTransactionBuffer.addType(builder, getType().getValue());
        SecretProofTransactionBuffer.addFee(builder, feeVector);
        SecretProofTransactionBuffer.addDeadline(builder, deadlineVector);
        SecretProofTransactionBuffer.addHashAlgorithm(builder, hashType.getValue());
        SecretProofTransactionBuffer.addSecret(builder, secretVector);
        SecretProofTransactionBuffer.addProofSize(builder, Hex.decode(proof).length);
        SecretProofTransactionBuffer.addProof(builder, proofVector);

        int codedSecretProof = SecretProofTransactionBuffer.endSecretProofTransactionBuffer(builder);
        builder.finish(codedSecretProof);

        return schema.serialize(builder.sizedByteArray());
    }
}
