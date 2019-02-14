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
import io.nem.core.crypto.Signer;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The aggregate innerTransactions contain multiple innerTransactions that can be initiated by different accounts.
 *
 * @since 1.0
 */
public class AggregateTransaction extends Transaction {
    private final List<Transaction> innerTransactions;
    private final List<AggregateTransactionCosignature> cosignatures;
    private final Schema schema = new AggregateTransactionSchema();

    public AggregateTransaction(NetworkType networkType, TransactionType transactionType, Integer version, Deadline deadline, BigInteger fee, List<Transaction> innerTransactions, List<AggregateTransactionCosignature> cosignatures, String signature, PublicAccount signer, TransactionInfo transactionInfo) {
        this(networkType, transactionType, version, deadline, fee, innerTransactions, cosignatures, Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
    }

    public AggregateTransaction(NetworkType networkType, TransactionType transactionType, Integer version, Deadline deadline, BigInteger fee, List<Transaction> innerTransactions, List<AggregateTransactionCosignature> cosignatures) {
        this(networkType, transactionType, version, deadline, fee, innerTransactions, cosignatures, Optional.empty(), Optional.empty(), Optional.empty());
    }

    private AggregateTransaction(NetworkType networkType, TransactionType transactionType, Integer version, Deadline deadline, BigInteger fee, List<Transaction> innerTransactions, List<AggregateTransactionCosignature> cosignatures, Optional<String> signature, Optional<PublicAccount> signer, Optional<TransactionInfo> transactionInfo) {
        super(transactionType, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(innerTransactions, "InnerTransactions must not be null");
        Validate.notNull(cosignatures, "Cosignatures must not be null");
        this.innerTransactions = innerTransactions;
        this.cosignatures = cosignatures;
    }

    /**
     * Create an aggregate complete transaction object
     *
     * @param deadline          The deadline to include the transaction.
     * @param innerTransactions The list of inner innerTransactions.
     * @param networkType       The network type.
     * @return {@link AggregateTransaction}
     */
    public static AggregateTransaction createComplete(Deadline deadline, List<Transaction> innerTransactions, NetworkType networkType) {
        return new AggregateTransaction(networkType, TransactionType.AGGREGATE_COMPLETE,2, deadline, BigInteger.valueOf(0), innerTransactions, new ArrayList<>());
    }

    /**
     * Create an aggregate bonded transaction object
     *
     * @param deadline          The deadline to include the transaction.
     * @param innerTransactions The list of inner innerTransactions.
     * @param networkType       The network type.
     * @return {@link AggregateTransaction}
     */
    public static AggregateTransaction createBonded(Deadline deadline, List<Transaction> innerTransactions, NetworkType networkType) {
        return new AggregateTransaction(networkType, TransactionType.AGGREGATE_BONDED,2, deadline, BigInteger.valueOf(0), innerTransactions, new ArrayList<>());
    }

    /**
     * Returns list of innerTransactions included in the aggregate transaction.
     *
     * @return List of innerTransactions included in the aggregate transaction.
     */
    public List<Transaction> getInnerTransactions() { return innerTransactions;}

    /**
     * Returns list of transaction cosigners signatures.
     *
     * @return List of transaction cosigners signatures.
     */
    public List<AggregateTransactionCosignature> getCosignatures() { return cosignatures; }

    byte[] generateBytes() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        BigInteger deadlineBigInt = BigInteger.valueOf(getDeadline().getInstant());
        int[] fee = new int[]{0, 0};
        int version = (int) Long.parseLong(Integer.toHexString(getNetworkType().getValue()) + "0" + Integer.toHexString(getVersion()), 16);

        byte[] transactionsBytes = new byte[0];
        for (Transaction innerTransaction : innerTransactions) {
            byte[] transactionBytes = innerTransaction.toAggregateTransactionBytes();
            transactionsBytes = ArrayUtils.addAll(transactionsBytes, transactionBytes);
        }

        // Create Vectors
        int signatureVector = AggregateTransactionBuffer.createSignatureVector(builder, new byte[64]);
        int signerVector = AggregateTransactionBuffer.createSignerVector(builder, new byte[32]);
        int deadlineVector = AggregateTransactionBuffer.createDeadlineVector(builder, UInt64.fromBigInteger(deadlineBigInt));
        int feeVector = AggregateTransactionBuffer.createFeeVector(builder, fee);
        int transactionsVector = AggregateTransactionBuffer.createTransactionsVector(builder, transactionsBytes);

        AggregateTransactionBuffer.startAggregateTransactionBuffer(builder);
        AggregateTransactionBuffer.addSize(builder, 120 + 4 + transactionsBytes.length);
        AggregateTransactionBuffer.addSignature(builder, signatureVector);
        AggregateTransactionBuffer.addSigner(builder, signerVector);
        AggregateTransactionBuffer.addVersion(builder, version);
        AggregateTransactionBuffer.addType(builder, getType().getValue());
        AggregateTransactionBuffer.addFee(builder, feeVector);
        AggregateTransactionBuffer.addDeadline(builder, deadlineVector);
        AggregateTransactionBuffer.addTransactionsSize(builder, transactionsBytes.length);
        AggregateTransactionBuffer.addTransactions(builder, transactionsVector);

        int codedTransaction = AggregateTransactionBuffer.endAggregateTransactionBuffer(builder);
        builder.finish(codedTransaction);

        return schema.serialize(builder.sizedByteArray());
    }

    /**
     * Sign transaction with cosignatories creating a new SignedTransaction.
     *
     * @param initiatorAccount Initiator account
     * @param cosignatories    The list of accounts that will cosign the transaction
     * @return {@link SignedTransaction}
     */
    public SignedTransaction signTransactionWithCosigners(Account initiatorAccount, List<Account> cosignatories) {
        SignedTransaction signedTransaction = this.signWith(initiatorAccount);
        String payload = signedTransaction.getPayload();

        for (Account cosignatory : cosignatories) {
            Signer signer = new Signer(cosignatory.getKeyPair());
            byte[] bytes = Hex.decode(signedTransaction.getHash());
            byte[] signatureBytes = signer.sign(bytes).getBytes();
            payload += cosignatory.getPublicKey() + Hex.toHexString(signatureBytes);
        }

        byte[] payloadBytes = Hex.decode(payload);

        byte[] size = BigInteger.valueOf(payloadBytes.length).toByteArray();
        ArrayUtils.reverse(size);

        System.arraycopy(size, 0, payloadBytes, 0, size.length);

        return new SignedTransaction(Hex.toHexString(payloadBytes), signedTransaction.getHash(), getType());
    }

    /**
     * Check if account has signed transaction.
     *
     * @param publicAccount - Signer public account
     * @return boolean
     */
    public boolean signedByAccount(PublicAccount publicAccount) {
        return this.getSigner().get().equals(publicAccount) || this.getCosignatures().stream().anyMatch(o -> o.getSigner().equals(publicAccount));
    }
}