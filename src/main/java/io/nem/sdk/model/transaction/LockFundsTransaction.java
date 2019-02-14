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
import io.nem.sdk.model.mosaic.Mosaic;
import org.apache.commons.lang3.Validate;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Lock funds transaction is used before sending an Aggregate bonded transaction, as a deposit to announce the transaction.
 * When aggregate bonded transaction is confirmed funds are returned to LockFundsTransaction signer.
 *
 * @since 1.0
 */
public class LockFundsTransaction extends Transaction {
    private final Mosaic mosaic;
    private final BigInteger duration;
    private final SignedTransaction signedTransaction;
    private final Schema schema = new LockFundsTransactionSchema();

    public LockFundsTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Mosaic mosaic, BigInteger duration, SignedTransaction signedTransaction, String signature, PublicAccount signer, TransactionInfo transactionInfo) {
        this(networkType, version, deadline, fee, mosaic, duration, signedTransaction, Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
    }

    public LockFundsTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Mosaic mosaic, BigInteger duration, SignedTransaction signedTransaction) {
        this(networkType, version, deadline, fee, mosaic, duration, signedTransaction, Optional.empty(), Optional.empty(), Optional.empty());
    }

    private LockFundsTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Mosaic mosaic, BigInteger duration, SignedTransaction signedTransaction, Optional<String> signature, Optional<PublicAccount> signer, Optional<TransactionInfo> transactionInfo) {
        super(TransactionType.LOCK, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(mosaic, "Mosaic must not be null");
        Validate.notNull(duration, "Duration must not be null");
        Validate.notNull(signedTransaction, "Signed transaction must not be null");
        this.mosaic = mosaic;
        this.duration = duration;
        this.signedTransaction = signedTransaction;
        if (signedTransaction.getType() != TransactionType.AGGREGATE_BONDED) {
            throw new IllegalArgumentException("Signed transaction must be Aggregate Bonded Transaction");
        }
    }

    /**
     * Create a lock funds transaction object.
     * @param deadline          The deadline to include the transaction.
     * @param mosaic            The locked mosaic.
     * @param duration          The funds lock duration.
     * @param signedTransaction The signed transaction for which funds are locked.
     * @param networkType       The network type.
     * @return a LockFundsTransaction instance
     */
    public static LockFundsTransaction create(Deadline deadline, Mosaic mosaic, BigInteger duration, SignedTransaction signedTransaction, NetworkType networkType) {
        return new LockFundsTransaction(networkType, 3, deadline, BigInteger.valueOf(0), mosaic, duration, signedTransaction);
    }

    /**
     * Returns locked mosaic.
     *
     * @return locked mosaic.
     */
    public Mosaic getMosaic() { return mosaic; }

    /**
     * Returns funds lock duration in number of blocks.
     *
     * @return funds lock duration in number of blocks.
     */
    public BigInteger getDuration() { return duration; }

    /**
     * Returns signed transaction for which funds are locked.
     *
     * @return signed transaction for which funds are locked.
     */
    public SignedTransaction getSignedTransaction() { return signedTransaction; }

    @Override
    byte[] generateBytes() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        BigInteger deadlineBigInt = BigInteger.valueOf(getDeadline().getInstant());
        int[] fee = new int[]{0, 0};
        int version = (int) Long.parseLong(Integer.toHexString(getNetworkType().getValue()) + "0" + Integer.toHexString(getVersion()), 16);

        // Create Vectors
        int signatureVector = LockFundsTransactionBuffer.createSignatureVector(builder, new byte[64]);
        int signerVector = LockFundsTransactionBuffer.createSignerVector(builder, new byte[32]);
        int deadlineVector = LockFundsTransactionBuffer.createDeadlineVector(builder, UInt64.fromBigInteger(deadlineBigInt));
        int feeVector = LockFundsTransactionBuffer.createFeeVector(builder, fee);
        int mosaicIdVector = LockFundsTransactionBuffer.createMosaicIdVector(builder, UInt64.fromBigInteger(mosaic.getId().getId()));
        int mosaicAmountVector = LockFundsTransactionBuffer.createMosaicAmountVector(builder, UInt64.fromBigInteger(mosaic.getAmount()));
        int durationVector = LockFundsTransactionBuffer.createDurationVector(builder, UInt64.fromBigInteger(duration));

        int hashVector = LockFundsTransactionBuffer.createHashVector(builder, Hex.decode(signedTransaction.getHash()));

        LockFundsTransactionBuffer.startLockFundsTransactionBuffer(builder);
        LockFundsTransactionBuffer.addSize(builder, 176);
        LockFundsTransactionBuffer.addSignature(builder, signatureVector);
        LockFundsTransactionBuffer.addSigner(builder, signerVector);
        LockFundsTransactionBuffer.addVersion(builder, version);
        LockFundsTransactionBuffer.addType(builder, getType().getValue());
        LockFundsTransactionBuffer.addFee(builder, feeVector);
        LockFundsTransactionBuffer.addDeadline(builder, deadlineVector);
        LockFundsTransactionBuffer.addMosaicId(builder, mosaicIdVector);
        LockFundsTransactionBuffer.addMosaicAmount(builder, mosaicAmountVector);
        LockFundsTransactionBuffer.addDuration(builder, durationVector);
        LockFundsTransactionBuffer.addHash(builder, hashVector);

        int codedLockFunds = LockFundsTransactionBuffer.endLockFundsTransactionBuffer(builder);
        builder.finish(codedLockFunds);

        return schema.serialize(builder.sizedByteArray());
    }
}
