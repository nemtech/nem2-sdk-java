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

package io.nem.sdk.infrastructure;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.*;
import io.reactivex.functions.Function;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionMapping implements Function<JsonObject, Transaction> {
    @Override
    public Transaction apply(JsonObject input) {
        JsonObject transaction = input.getJsonObject("transaction");
        int type = transaction.getInteger("type");

        if (type == TransactionType.TRANSFER.getValue()) {
            return new TransferTransactionMapping().apply(input);
        } else if (type == TransactionType.REGISTER_NAMESPACE.getValue()) {
            return new NamespaceCreationTransactionMapping().apply(input);
        } else if (type == TransactionType.MOSAIC_DEFINITION.getValue()) {
            return new MosaicCreationTransactionMapping().apply(input);
        } else if (type == TransactionType.MOSAIC_SUPPLY_CHANGE.getValue()) {
            return new MosaicSupplyChangeTransactionMapping().apply(input);
        //} else if (type == TransactionType.MOSAIC_ALIAS.getValue()) {
        //    return new MosaicAliasTransactionMapping().apply(input);
        } else if (type == TransactionType.MODIFY_MULTISIG_ACCOUNT.getValue()) {
            return new MultisigModificationTransactionMapping().apply(input);
        } else if (type == TransactionType.AGGREGATE_COMPLETE.getValue() || type == TransactionType.AGGREGATE_BONDED.getValue()) {
            return new AggregateTransactionMapping().apply(input);
        } else if (type == TransactionType.LOCK.getValue()) {
            return new LockFundsTransactionMapping().apply(input);
        } else if (type == TransactionType.SECRET_LOCK.getValue()) {
            return new SecretLockTransactionMapping().apply(input);
        } else if (type == TransactionType.SECRET_PROOF.getValue()) {
            return new SecretProofTransactionMapping().apply(input);
        }else if (type == TransactionType.ACCOUNT_LINK.getValue()) {
            return new AccountLinkTransactionMapping().apply(input);
        }

        throw new UnsupportedOperationException("Unimplemented Transaction type");
    }

    BigInteger extractBigInteger(JsonArray input) {
        UInt64DTO uInt64DTO = new UInt64DTO();
        input.stream().forEach(item -> uInt64DTO.add(new Long(item.toString())));
        return uInt64DTO.extractIntArray();
    }

    BigInteger extractBigInteger(Long input) {
        return BigInteger.valueOf(input.intValue());
    }

    Integer extractTransactionVersion(int version) {
        return (int) Long.parseLong(Integer.toHexString(version).substring(2, 4), 16);
    }

    NetworkType extractNetworkType(int version) {
        int networkType = (int) Long.parseLong(Integer.toHexString(version).substring(0, 2), 16);
        return NetworkType.rawValueOf(networkType);
    }

    public TransactionInfo createTransactionInfo(JsonObject jsonObject) {
        if (jsonObject.containsKey("hash") && jsonObject.containsKey("id")) {
            return TransactionInfo.create(extractBigInteger(jsonObject.getJsonArray("height")),
                    jsonObject.getInteger("index"),
                    jsonObject.getString("id"),
                    jsonObject.getString("hash"),
                    jsonObject.getString("merkleComponentHash"));
        } else if (jsonObject.containsKey("aggregateHash") && jsonObject.containsKey("id")) {
            return TransactionInfo.createAggregate(extractBigInteger(jsonObject.getJsonArray("height")),
                    jsonObject.getInteger("index"),
                    jsonObject.getString("id"),
                    jsonObject.getString("aggregateHash"),
                    jsonObject.getString("aggregateId"));
        } else {
            return TransactionInfo.create(extractBigInteger(jsonObject.getJsonArray("height")),
                    jsonObject.getString("hash"),
                    jsonObject.getString("merkleComponentHash"));
        }
    }
}

class TransferTransactionMapping extends TransactionMapping {

    @Override
    public TransferTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        List<Mosaic> mosaics = new ArrayList<>();

        if (transaction.getJsonArray("mosaics") != null) {
            mosaics = transaction
                    .getJsonArray("mosaics")
                    .stream()
                    .map(item -> (JsonObject) item)
                    .map(mosaic -> new Mosaic(
                            new MosaicId(extractBigInteger(mosaic.getJsonArray("id"))),
                            extractBigInteger(mosaic.getJsonArray("amount"))))
                    .collect(Collectors.toList());
        }

        Message message = PlainMessage.Empty;
        if (transaction.getJsonObject("message") != null) {
            message = new PlainMessage(new String(Hex.decode(transaction.getJsonObject("message").getString("payload")), StandardCharsets.UTF_8));
        }

        return new TransferTransaction(
                extractNetworkType(transaction.getInteger("version")),
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                Address.createFromEncoded(transaction.getString("recipient")),
                mosaics,
                message,
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), extractNetworkType(transaction.getInteger("version"))),
                transactionInfo
        );
    }
}

class NamespaceCreationTransactionMapping extends TransactionMapping {

    @Override
    public RegisterNamespaceTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NamespaceType namespaceType = NamespaceType.rawValueOf(transaction.getInteger("namespaceType"));

        return new RegisterNamespaceTransaction(
                extractNetworkType(transaction.getInteger("version")),
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                transaction.getString("name"),
                new NamespaceId(extractBigInteger(transaction.getJsonArray("namespaceId"))),
                namespaceType,
                namespaceType == NamespaceType.RootNamespace ? Optional.of(extractBigInteger(transaction.getJsonArray("duration"))) : Optional.empty(),
                namespaceType == NamespaceType.SubNamespace ? Optional.of(new NamespaceId(extractBigInteger(transaction.getJsonArray("parentId")))) : Optional.empty(),
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), extractNetworkType(transaction.getInteger("version"))),
                transactionInfo
        );
    }
}

class MosaicCreationTransactionMapping extends TransactionMapping {

    @Override
    public MosaicDefinitionTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));


        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));

        JsonArray mosaicProperties = transaction.getJsonArray("properties");

        String flags = "00" + Integer.toBinaryString(extractBigInteger(mosaicProperties.getJsonObject(0).getJsonArray("value")).intValue());
        String bitMapFlags = flags.substring(flags.length() - 3);
        MosaicProperties properties = new MosaicProperties(bitMapFlags.charAt(2) == '1',
                bitMapFlags.charAt(1) == '1',
                bitMapFlags.charAt(0) == '1',
                extractBigInteger(mosaicProperties.getJsonObject(1).getJsonArray("value")).intValue(),
                mosaicProperties.size() == 3 ? extractBigInteger(mosaicProperties.getJsonObject(2).getJsonArray("value")) : BigInteger.valueOf(0));

        return new MosaicDefinitionTransaction(
                extractNetworkType(transaction.getInteger("version")),
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                // TODO Reconfirm mosaicNonce is a number (integer), not an array (uint64)
                //MosaicNonce.createFromBigInteger(extractBigInteger(transaction.getJsonArray("mosaicNonce"))),
                MosaicNonce.createFromBigInteger(extractBigInteger(transaction.getLong("mosaicNonce"))),
                new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))),
                properties,
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), extractNetworkType(transaction.getInteger("version"))),
                transactionInfo
        );
    }
}

class MosaicSupplyChangeTransactionMapping extends TransactionMapping {

    @Override
    public MosaicSupplyChangeTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));

        return new MosaicSupplyChangeTransaction(
                extractNetworkType(transaction.getInteger("version")),
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))),
                MosaicSupplyType.rawValueOf(transaction.getInteger("direction")),
                extractBigInteger(transaction.getJsonArray("delta")),
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), extractNetworkType(transaction.getInteger("version"))),
                transactionInfo
        );
    }
}

class MultisigModificationTransactionMapping extends TransactionMapping {

    @Override
    public ModifyMultisigAccountTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        List<MultisigCosignatoryModification> modifications = transaction.containsKey("modifications") ? transaction
                .getJsonArray("modifications")
                .stream()
                .map(item -> (JsonObject) item)
                .map(multisigModification -> new MultisigCosignatoryModification(
                        MultisigCosignatoryModificationType.rawValueOf(multisigModification.getInteger("type")),
                        PublicAccount.createFromPublicKey(multisigModification.getString("cosignatoryPublicKey"), networkType)))
                .collect(Collectors.toList()) : Collections.emptyList();

        return new ModifyMultisigAccountTransaction(
                networkType,
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                transaction.getInteger("minApprovalDelta"),
                transaction.getInteger("minRemovalDelta"),
                modifications,
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), networkType),
                transactionInfo
        );
    }
}

class AggregateTransactionMapping extends TransactionMapping {

    @Override
    public AggregateTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < transaction.getJsonArray("transactions").getList().size(); i++) {
            JsonObject innerTransaction = transaction.getJsonArray("transactions").getJsonObject(i);
            innerTransaction.getJsonObject("transaction").put("deadline", transaction.getJsonArray("deadline"));
            innerTransaction.getJsonObject("transaction").put("maxFee", transaction.getJsonArray("maxFee"));
            innerTransaction.getJsonObject("transaction").put("signature", transaction.getString("signature"));
            if (!innerTransaction.containsKey("meta")) {
                innerTransaction.put("meta", input.getJsonObject("meta"));
            }
            transactions.add(new TransactionMapping().apply(innerTransaction));
        }

        List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
        if (transaction.getJsonArray("cosignatures") != null) {
            cosignatures = transaction
                    .getJsonArray("cosignatures")
                    .stream()
                    .map(item -> (JsonObject) item)
                    .map(aggregateCosignature -> new AggregateTransactionCosignature(
                            aggregateCosignature.getString("signature"),
                            new PublicAccount(aggregateCosignature.getString("signer"), networkType)))
                    .collect(Collectors.toList());
        }

        return new AggregateTransaction(
                networkType,
                TransactionType.rawValueOf(transaction.getInteger("type")),
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                transactions,
                cosignatures,
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), networkType),
                transactionInfo
        );
    }
}

class LockFundsTransactionMapping extends TransactionMapping {

    @Override
    public LockFundsTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));
        Mosaic mosaic;
        if (transaction.containsKey("mosaicId")) {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))), extractBigInteger(transaction.getJsonArray("amount")));
        } else {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("id"))), extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("amount")));
        }
        return new LockFundsTransaction(
                networkType,
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                mosaic,
                extractBigInteger(transaction.getJsonArray("duration")),
                new SignedTransaction("", transaction.getString("hash"), TransactionType.AGGREGATE_BONDED),
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), networkType),
                transactionInfo
        );
    }
}

class SecretLockTransactionMapping extends TransactionMapping {

    @Override
    public SecretLockTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));
        Mosaic mosaic;
        if (transaction.containsKey("mosaicId")) {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))), extractBigInteger(transaction.getJsonArray("amount")));
        } else {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("id"))), extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("amount")));
        }
        return new SecretLockTransaction(
                networkType,
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                mosaic,
                extractBigInteger(transaction.getJsonArray("duration")),
                HashType.rawValueOf(transaction.getInteger("hashAlgorithm")),
                transaction.getString("secret"),
                Address.createFromEncoded(transaction.getString("recipient")),
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), networkType),
                transactionInfo
        );
    }
}

class SecretProofTransactionMapping extends TransactionMapping {

    @Override
    public SecretProofTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        return new SecretProofTransaction(
                networkType,
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("maxFee")),
                HashType.rawValueOf(transaction.getInteger("hashAlgorithm")),
                transaction.getString("secret"),
                transaction.getString("proof"),
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), networkType),
                transactionInfo
        );
    }
}

class AccountLinkTransactionMapping extends TransactionMapping{

    @Override
    public AccountLinkTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        return new AccountLinkTransaction(
                networkType,
                extractTransactionVersion(transaction.getInteger("version")),
                deadline,
                extractBigInteger(transaction.getJsonArray("fee")),
                transaction.getString("remoteAccountKey"),
                LinkActionType.rawValueOf(transaction.getInteger("linkAction")),
                transaction.getString("signature"),
                new PublicAccount(transaction.getString("signer"), networkType),
                transactionInfo
        );
    }
}
