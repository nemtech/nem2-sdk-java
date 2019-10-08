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

import io.nem.catapult.builders.AggregateTransactionBuilder;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.CosignatureBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.core.crypto.CryptoEngines;
import io.nem.core.crypto.DsaSigner;
import io.nem.core.utils.ExceptionUtils;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;

/**
 * The aggregate innerTransactions contain multiple innerTransactions that can be initiated by
 * different accounts.
 *
 * @since 1.0
 */
public class AggregateTransaction extends Transaction {

    private final List<Transaction> innerTransactions;

    private final List<AggregateTransactionCosignature> cosignatures;

    /**
     * AggregateTransaction constructor using factory.
     */
    AggregateTransaction(AggregateTransactionFactory factory) {
        super(factory);
        this.innerTransactions = factory.getInnerTransactions();
        this.cosignatures = factory.getCosignatures();
    }

    /**
     * Returns list of innerTransactions included in the aggregate transaction.
     *
     * @return List of innerTransactions included in the aggregate transaction.
     */
    public List<Transaction> getInnerTransactions() {
        return innerTransactions;
    }

    /**
     * Returns list of transaction cosigners signatures.
     *
     * @return List of transaction cosigners signatures.
     */
    public List<AggregateTransactionCosignature> getCosignatures() {
        return cosignatures;
    }

    /**
     * Serialized the transaction.
     *
     * @return bytes of the transaction.
     */
    @Override
    public byte[] generateBytes() {
        return ExceptionUtils.propagate(
            () -> {
                byte[] transactionsBytes = new byte[0];
                for (Transaction innerTransaction : innerTransactions) {
                    final byte[] transactionBytes = innerTransaction.toAggregateTransactionBytes();
                    transactionsBytes = ArrayUtils.addAll(transactionsBytes, transactionBytes);
                }
                final ByteBuffer transactionsBuffer = ByteBuffer.wrap(transactionsBytes);

                byte[] cosignaturesBytes = new byte[0];
                for (AggregateTransactionCosignature cosignature : cosignatures) {
                    final byte[] signerBytes = cosignature.getSigner().getPublicKey().getBytes();
                    final byte[] signatureBytes = ConvertUtils.getBytes(cosignature.getSignature());
                    final ByteBuffer signerBuffer = ByteBuffer.wrap(signerBytes);
                    final ByteBuffer signatureBuffer = ByteBuffer.wrap(signatureBytes);

                    final CosignatureBuilder cosignatureBuilder = CosignatureBuilder
                        .create(new KeyDto(signerBuffer),
                            new SignatureDto(signatureBuffer));
                    cosignaturesBytes = ArrayUtils
                        .addAll(transactionsBytes, cosignatureBuilder.serialize());
                }
                final ByteBuffer cosignaturesBuffer = ByteBuffer.wrap(cosignaturesBytes);

                // Add place holders to the signer and signature until actually signed
                final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
                final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

                AggregateTransactionBuilder txBuilder =
                    AggregateTransactionBuilder.create(
                        new SignatureDto(signatureBuffer),
                        new KeyDto(signerBuffer),
                        getNetworkVersion(),
                        EntityTypeDto.rawValueOf((short) getType().getValue()),
                        new AmountDto(getMaxFee().longValue()),
                        new TimestampDto(getDeadline().getInstant()),
                        transactionsBuffer,
                        cosignaturesBuffer);
                return txBuilder.serialize();
            });
    }

    /**
     * Fail if this method is called.
     */
    @Override
    protected byte[] generateEmbeddedBytes() {
        throw new IllegalStateException(
            "Aggregate class cannot generate bytes for an embedded transaction.");
    }

    /**
     * Sign transaction with cosignatories creating a new SignedTransaction.
     *
     * @param initiatorAccount Initiator account
     * @param cosignatories The list of accounts that will cosign the transaction
     * @return {@link SignedTransaction}
     */
    public SignedTransaction signTransactionWithCosigners(
        final Account initiatorAccount,
        final List<Account> cosignatories,
        final String generationHash) {
        SignedTransaction signedTransaction = this.signWith(initiatorAccount, generationHash);
        StringBuilder payload = new StringBuilder(signedTransaction.getPayload());

        for (Account cosignatory : cosignatories) {
            final DsaSigner signer = CryptoEngines.defaultEngine()
                .createDsaSigner(cosignatory.getKeyPair(),
                    cosignatory.getNetworkType().resolveSignSchema());
            byte[] bytes = Hex.decode(signedTransaction.getHash());
            byte[] signatureBytes = signer.sign(bytes).getBytes();
            payload.append(cosignatory.getPublicKey()).append(Hex.toHexString(signatureBytes));
        }

        byte[] payloadBytes = Hex.decode(payload.toString());

        byte[] size = BigInteger.valueOf(payloadBytes.length).toByteArray();
        ArrayUtils.reverse(size);

        System.arraycopy(size, 0, payloadBytes, 0, size.length);

        return new SignedTransaction(
            Hex.toHexString(payloadBytes), signedTransaction.getHash(), getType());
    }

    /**
     * Check if account has signed transaction.
     *
     * @param publicAccount - Signer public account
     * @return boolean
     */
    public boolean signedByAccount(PublicAccount publicAccount) {
        return this.getSigner().filter(a -> a.equals(publicAccount)).isPresent()
            || this.getCosignatures().stream().anyMatch(o -> o.getSigner().equals(publicAccount));
    }
}
