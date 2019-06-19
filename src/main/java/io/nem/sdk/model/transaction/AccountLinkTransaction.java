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

import com.google.flatbuffers.FlatBufferBuilder;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.util.Optional;

public class AccountLinkTransaction extends Transaction{

    private final String remoteAccountKey;
    private final LinkActionType linkAction;
    private final Schema schema = new AccountLinkTransactionSchema();

    public AccountLinkTransaction(NetworkType networkType,
                                  Integer version, Deadline deadline, BigInteger fee,
                                  String remoteAccountKey,
                                  LinkActionType linkAction,
                                  String signature,
                                  PublicAccount signer,
                                  TransactionInfo transactionInfo) {
        this(networkType, version, deadline, fee, remoteAccountKey, linkAction, Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
    }

    public AccountLinkTransaction(NetworkType networkType,
                                  Integer version, Deadline deadline, BigInteger fee,
                                  String remoteAccountKey,
                                  LinkActionType linkAction) {
        this(networkType, version, deadline, fee, remoteAccountKey, linkAction, Optional.empty(), Optional.empty(), Optional.empty());
    }

    private AccountLinkTransaction(NetworkType networkType,
                                   Integer version, Deadline deadline, BigInteger fee,
                                   String remoteAccountKey,
                                   LinkActionType linkAction,
                                   Optional<String> signature,
                                   Optional<PublicAccount> signer,
                                   Optional<TransactionInfo> transactionInfo) {
        super(TransactionType.ACCOUNT_LINK, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(remoteAccountKey, "remoteAccountKey must not be null");
        Validate.notNull(linkAction, "linkAction must not be null");
        this.remoteAccountKey = remoteAccountKey;
        this.linkAction = linkAction;
    }

    public static AccountLinkTransaction create(Deadline deadline,
                                                String remoteAccountKey,
                                                LinkActionType linkAction,
                                                NetworkType networkType) {
        return new AccountLinkTransaction(networkType, 2,deadline, BigInteger.valueOf(0), remoteAccountKey, linkAction);
    }

    public String getRemoteAccountKey() {
        return remoteAccountKey;
    }

    public LinkActionType getLinkAction() {
        return linkAction;
    }

    @Override
    byte[] generateBytes() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        BigInteger deadlineBigInt = BigInteger.valueOf(getDeadline().getInstant());
        int[] fee = new int[]{0, 0};
        int version = (int) Long.parseLong(Integer.toHexString(getNetworkType().getValue()) + "0" + Integer.toHexString(getVersion()), 16);

        // Create Vectors
        int signatureVector = AccountLinkTransactionBuffer.createSignatureVector(builder, new byte[64]);
        int signerVector = AccountLinkTransactionBuffer.createSignerVector(builder, new byte[32]);
        int deadlineVector = AccountLinkTransactionBuffer.createDeadlineVector(builder, UInt64.fromBigInteger(deadlineBigInt));
        int feeVector = AccountLinkTransactionBuffer.createFeeVector(builder, fee);


        AccountLinkTransactionBuffer.startAccountLinkTransactionBuffer(builder);
        AccountLinkTransactionBuffer.addSize(builder, 200 + remoteAccountKey.length());
        AccountLinkTransactionBuffer.addSignature(builder, signatureVector);
        AccountLinkTransactionBuffer.addSigner(builder, signerVector);
        AccountLinkTransactionBuffer.addVersion(builder, version);
        AccountLinkTransactionBuffer.addType(builder, getType().getValue());
        AccountLinkTransactionBuffer.addFee(builder, feeVector);
        AccountLinkTransactionBuffer.addDeadline(builder, deadlineVector);

        int codedTransaction = AccountLinkTransactionBuffer.endAccountLinkTransactionBuffer(builder);
        builder.finish(codedTransaction);

        return schema.serialize(builder.sizedByteArray());
    }
}
