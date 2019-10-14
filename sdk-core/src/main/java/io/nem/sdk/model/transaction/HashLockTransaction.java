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
import io.nem.catapult.builders.EmbeddedHashLockTransactionBuilder;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.HashLockTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.mosaic.Mosaic;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.bouncycastle.util.encoders.Hex;

/**
 * Lock funds transaction is used before sending an Aggregate bonded transaction, as a deposit to
 * announce the transaction. When aggregate bonded transaction is confirmed funds are returned to
 * HashLockTransaction signer.
 *
 * @since 1.0
 */
public class HashLockTransaction extends Transaction {

    private final Mosaic mosaic;
    private final BigInteger duration;
    private final SignedTransaction signedTransaction;

    /**
     * It creates a {@link HashLockTransaction} based on the factory.
     *
     * @param factory the factory with the configured information.
     */
    HashLockTransaction(HashLockTransactionFactory factory) {
        super(factory);
        this.mosaic = factory.getMosaic();
        this.duration = factory.getDuration();
        this.signedTransaction = factory.getSignedTransaction();
        if (signedTransaction.getType() != TransactionType.AGGREGATE_BONDED) {
            throw new IllegalArgumentException(
                "Signed transaction must be Aggregate Bonded Transaction");
        }
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
     * Returns funds lock duration in number of blocks.
     *
     * @return funds lock duration in number of blocks.
     */
    public BigInteger getDuration() {
        return duration;
    }

    /**
     * Returns signed transaction for which funds are locked.
     *
     * @return signed transaction for which funds are locked.
     */
    public SignedTransaction getSignedTransaction() {
        return signedTransaction;
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

        HashLockTransactionBuilder txBuilder =
            HashLockTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(getMosaic().getId().getIdAsLong()),
                    new AmountDto(getMosaic().getAmount().longValue())),
                new BlockDurationDto(getDuration().longValue()),
                new Hash256Dto(getHashBuffer()));
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedHashLockTransactionBuilder txBuilder =
            EmbeddedHashLockTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(getMosaic().getId().getIdAsLong()),
                    new AmountDto(getMosaic().getAmount().longValue())),
                new BlockDurationDto(getDuration().longValue()),
                new Hash256Dto(getHashBuffer()));
        return txBuilder.serialize();
    }

    /**
     * Gets hash buffer.
     *
     * @return Hash buffer.
     */
    private ByteBuffer getHashBuffer() {
        final byte[] hashBytes = Hex.decode(signedTransaction.getHash());
        return ByteBuffer.wrap(hashBytes);
    }
}
