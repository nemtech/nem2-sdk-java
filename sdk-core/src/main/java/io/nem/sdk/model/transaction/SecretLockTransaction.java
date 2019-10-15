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
import io.nem.catapult.builders.BlockDurationDto;
import io.nem.catapult.builders.EmbeddedSecretLockTransactionBuilder;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.LockHashAlgorithmDto;
import io.nem.catapult.builders.SecretLockTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.Mosaic;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.bouncycastle.util.encoders.Hex;

public class SecretLockTransaction extends Transaction {

    private final Mosaic mosaic;
    private final BigInteger duration;
    private final LockHashAlgorithmType hashAlgorithm;
    private final String secret;
    private final UnresolvedAddress recipient;

    /**
     * Contructor of this transaction using the factory.
     *
     * @param factory the factory.
     */
    SecretLockTransaction(SecretLockTransactionFactory factory) {
        super(factory);
        this.mosaic = factory.getMosaic();
        this.duration = factory.getDuration();
        this.hashAlgorithm = factory.getHashAlgorithm();
        this.secret = factory.getSecret();
        this.recipient = factory.getRecipient();
    }


    /**
     * Returns locked mosaic.
     *
     * @return locked mosaic.
     */
    public Mosaic getMosaic() {
        return mosaic;
    }

    /**
     * Returns duration for the funds to be released or returned.
     *
     * @return duration for the funds to be released or returned.
     */
    public BigInteger getDuration() {
        return duration;
    }

    /**
     * Returns the hash algorithm, secret is generated with.
     *
     * @return the hash algorithm, secret is generated with.
     */
    public LockHashAlgorithmType getHashAlgorithm() {
        return hashAlgorithm;
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
     * Returns the recipient of the funds.
     *
     * @return the recipient of the funds.
     */
    public UnresolvedAddress getRecipient() {
        return recipient;
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

        SecretLockTransactionBuilder txBuilder =
            SecretLockTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(mosaic.getId().getIdAsLong()),
                    new AmountDto(mosaic.getAmount().longValue())),
                new BlockDurationDto(duration.longValue()),
                LockHashAlgorithmDto.rawValueOf((byte) hashAlgorithm.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(
                    SerializationUtils.fromUnresolvedAddressToByteBuffer(getRecipient())));
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedSecretLockTransactionBuilder txBuilder =
            EmbeddedSecretLockTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(mosaic.getId().getIdAsLong()),
                    new AmountDto(mosaic.getAmount().longValue())),
                new BlockDurationDto(duration.longValue()),
                LockHashAlgorithmDto.rawValueOf((byte) hashAlgorithm.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(
                    SerializationUtils.fromUnresolvedAddressToByteBuffer(getRecipient())));
        return txBuilder.serialize();
    }

    /**
     * Gets secret buffer.
     *
     * @return Secret buffer.
     */
    private ByteBuffer getSecretBuffer() {
        final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
        secretBuffer.put(Hex.decode(secret));
        return secretBuffer;
    }
}
