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
import io.nem.catapult.builders.EmbeddedSecretProofTransactionBuilder;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.LockHashAlgorithmDto;
import io.nem.catapult.builders.SecretProofTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.UnresolvedAddress;
import java.nio.ByteBuffer;
import org.bouncycastle.util.encoders.Hex;

/**
 * Secret proof transaction.
 */
public class SecretProofTransaction extends Transaction {

    private final LockHashAlgorithmType hashType;
    private final String secret;
    private final String proof;
    private final UnresolvedAddress recipient;


    /**
     * The transaction contructor using the factory.
     *
     * @param factory the factory with the configured data.
     */
    public SecretProofTransaction(SecretProofTransactionFactory factory) {
        super(factory);
        this.hashType = factory.getHashType();
        this.secret = factory.getSecret();
        this.proof = factory.getProof();
        this.recipient = factory.getRecipient();
    }


    /**
     * Returns the hash algorithm secret is generated with.
     *
     * @return the hash algorithm secret is generated with.
     */
    public LockHashAlgorithmType getHashType() {
        return hashType;
    }

    /**
     * Returns the proof hashed.
     *
     * @return the proof hashed.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Returns proof.
     *
     * @return proof.
     */
    public String getProof() {
        return proof;
    }

    /**
     * Serialized the transaction.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        SecretProofTransactionBuilder txBuilder =
            SecretProofTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                LockHashAlgorithmDto.rawValueOf((byte) hashType.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(
                    SerializationUtils.fromUnresolvedAddressToByteBuffer(this.getRecipient())),
                getProofBuffer());
        return txBuilder.serialize();
    }

    /**
     * @return the recipient
     */
    public UnresolvedAddress getRecipient() {
        return recipient;
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedSecretProofTransactionBuilder txBuilder =
            EmbeddedSecretProofTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                LockHashAlgorithmDto.rawValueOf((byte) hashType.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(
                    SerializationUtils.fromUnresolvedAddressToByteBuffer(this.getRecipient())),
                getProofBuffer());
        return txBuilder.serialize();
    }

    private ByteBuffer getSecretBuffer() {
        final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
        secretBuffer.put(Hex.decode(secret));
        return secretBuffer;
    }

    /**
     * Gets proof buffer
     *
     * @return Proof buffer.
     */
    private ByteBuffer getProofBuffer() {
        final byte[] proofBytes = Hex.decode(proof);
        return ByteBuffer.wrap(proofBytes);
    }
}
