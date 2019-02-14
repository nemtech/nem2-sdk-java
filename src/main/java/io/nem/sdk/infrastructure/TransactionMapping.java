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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.mosaic.MosaicSupplyType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.*;
import io.reactivex.functions.Function;
import org.spongycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TransactionMapping implements Function<JsonObject, Transaction> {
    @Override
    public Transaction apply(JsonObject input) {
        JsonObject transaction = input.getAsJsonObject("transaction");
        int type = transaction.get("type").getAsInt();

        if (type == TransactionType.TRANSFER.getValue()) {
            return new TransferTransactionMapping().apply(input);
        } else if (type == TransactionType.REGISTER_NAMESPACE.getValue()) {
            return new NamespaceCreationTransactionMapping().apply(input);
        } else if (type == TransactionType.MOSAIC_DEFINITION.getValue()) {
            return new MosaicCreationTransactionMapping().apply(input);
        } else if (type == TransactionType.MOSAIC_SUPPLY_CHANGE.getValue()) {
            return new MosaicSupplyChangeTransactionMapping().apply(input);
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
        }

        throw new UnsupportedOperationException("Unimplemented Transaction type");
    }

    BigInteger extractBigInteger(JsonArray input) {
        UInt64DTO uInt64DTO = new UInt64DTO();
        StreamSupport.stream(input.spliterator(), false).forEach(item -> uInt64DTO.add(new Long(item.toString())));
        return uInt64DTO.extractIntArray();
    }

    Integer extractTransactionVersion(int version) {
        return (int) Long.parseLong(Integer.toHexString(version).substring(2, 4), 16);
    }

    NetworkType extractNetworkType(int version) {
        int networkType = (int) Long.parseLong(Integer.toHexString(version).substring(0, 2), 16);
        return NetworkType.rawValueOf(networkType);
    }

    public TransactionInfo createTransactionInfo(JsonObject jsonObject) {
        if (jsonObject.has("hash") && jsonObject.has("id")) {
            return TransactionInfo.create(extractBigInteger(jsonObject.getAsJsonArray("height")),
                    jsonObject.get("index").getAsInt(),
                    jsonObject.get("id").getAsString(),
                    jsonObject.get("hash").getAsString(),
                    jsonObject.get("merkleComponentHash").getAsString());
        } else if (jsonObject.has("aggregateHash") && jsonObject.has("id")) {
            return TransactionInfo.createAggregate(extractBigInteger(jsonObject.getAsJsonArray("height")),
                    jsonObject.get("index").getAsInt(),
                    jsonObject.get("id").getAsString(),
                    jsonObject.get("aggregateHash").getAsString(),
                    jsonObject.get("aggregateId").getAsString());
        } else {
            return TransactionInfo.create(extractBigInteger(jsonObject.getAsJsonArray("height")),
                    jsonObject.get("hash").getAsString(),
                    jsonObject.get("merkleComponentHash").getAsString());
        }
    }
}

class TransferTransactionMapping extends TransactionMapping {

    @Override
    public TransferTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));
        List<Mosaic> mosaics = new ArrayList<>();

        if (transaction.getAsJsonArray("mosaics") != null) {
            mosaics = StreamSupport.stream(transaction
                    .getAsJsonArray("mosaics").spliterator(), false)
                    .map(item -> (JsonObject) item)
                    .map(mosaic -> new Mosaic(
                            new MosaicId(extractBigInteger(mosaic.getAsJsonArray("id"))),
                            extractBigInteger(mosaic.getAsJsonArray("amount"))))
                    .collect(Collectors.toList());
        }

        Message message = PlainMessage.Empty;
        if (transaction.getAsJsonObject("message") != null) {
            try {
                message = new PlainMessage(new String(Hex.decode(transaction.getAsJsonObject("message").get("payload").getAsString()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                message = new PlainMessage(transaction.getAsJsonObject("message").get("payload").getAsString());
            }
        }

        return new TransferTransaction(
                extractNetworkType(transaction.get("version").getAsInt()),
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                Address.createFromEncoded(transaction.get("recipient").getAsString()),
                mosaics,
                message,
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), extractNetworkType(transaction.get("version").getAsInt())),
                transactionInfo
        );
    }
}

class NamespaceCreationTransactionMapping extends TransactionMapping {

    @Override
    public RegisterNamespaceTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));
        NamespaceType namespaceType = NamespaceType.rawValueOf(transaction.get("namespaceType").getAsInt());

        return new RegisterNamespaceTransaction(
                extractNetworkType(transaction.get("version").getAsInt()),
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                transaction.get("name").getAsString(),
                new NamespaceId(extractBigInteger(transaction.getAsJsonArray("namespaceId"))),
                namespaceType,
                namespaceType == NamespaceType.RootNamespace ? Optional.of(extractBigInteger(transaction.getAsJsonArray("duration"))) : Optional.empty(),
                namespaceType == NamespaceType.SubNamespace ? Optional.of(new NamespaceId(extractBigInteger(transaction.getAsJsonArray("parentId")))) : Optional.empty(),
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), extractNetworkType(transaction.get("version").getAsInt())),
                transactionInfo
        );
    }
}

class MosaicCreationTransactionMapping extends TransactionMapping {

    @Override
    public MosaicDefinitionTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));


        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));

        JsonArray mosaicProperties = transaction.getAsJsonArray("properties");

        String flags = "00" + Integer.toBinaryString(extractBigInteger(mosaicProperties.get(0).getAsJsonObject().getAsJsonArray("value")).intValue());
        String bitMapFlags = flags.substring(flags.length() - 3, flags.length());
        MosaicProperties properties = new MosaicProperties(bitMapFlags.charAt(2) == '1',
                bitMapFlags.charAt(1) == '1',
                bitMapFlags.charAt(0) == '1',
                extractBigInteger(mosaicProperties.get(1).getAsJsonObject().getAsJsonArray("value")).intValue(),
                mosaicProperties.size() == 3 ? extractBigInteger(mosaicProperties.get(2).getAsJsonObject().getAsJsonArray("value")) : BigInteger.valueOf(0));

        return new MosaicDefinitionTransaction(
                extractNetworkType(transaction.get("version").getAsInt()),
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                transaction.get("name").getAsString(),
                new NamespaceId(extractBigInteger(transaction.getAsJsonArray("parentId"))),
                new MosaicId(extractBigInteger(transaction.getAsJsonArray("mosaicId"))),
                properties,
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), extractNetworkType(transaction.get("version").getAsInt())),
                transactionInfo
        );
    }
}

class MosaicSupplyChangeTransactionMapping extends TransactionMapping {

    @Override
    public MosaicSupplyChangeTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));

        return new MosaicSupplyChangeTransaction(
                extractNetworkType(transaction.get("version").getAsInt()),
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                new MosaicId(extractBigInteger(transaction.getAsJsonArray("mosaicId"))),
                MosaicSupplyType.rawValueOf(transaction.get("direction").getAsInt()),
                extractBigInteger(transaction.getAsJsonArray("delta")),
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), extractNetworkType(transaction.get("version").getAsInt())),
                transactionInfo
        );
    }
}

class MultisigModificationTransactionMapping extends TransactionMapping {

    @Override
    public ModifyMultisigAccountTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.get("version").getAsInt());

        List<MultisigCosignatoryModification> modifications = transaction.has("modifications") ?
                StreamSupport.stream(transaction.getAsJsonArray("modifications").spliterator(), false)
                .map(item -> (JsonObject) item)
                .map(multisigModification -> new MultisigCosignatoryModification(
                        MultisigCosignatoryModificationType.rawValueOf(multisigModification.get("type").getAsInt()),
                        PublicAccount.createFromPublicKey(multisigModification.get("cosignatoryPublicKey").getAsString(), networkType)))
                .collect(Collectors.toList()) : Collections.emptyList();

        return new ModifyMultisigAccountTransaction(
                networkType,
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                transaction.get("minApprovalDelta").getAsInt(),
                transaction.get("minRemovalDelta").getAsInt(),
                modifications,
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), networkType),
                transactionInfo
        );
    }
}

class AggregateTransactionMapping extends TransactionMapping {

    @Override
    public AggregateTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.get("version").getAsInt());

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < transaction.getAsJsonArray("transactions").size(); i++) {
            JsonObject innerTransaction = transaction.getAsJsonArray("transactions").get(i).getAsJsonObject();
            innerTransaction.getAsJsonObject("transaction").add("deadline", transaction.getAsJsonArray("deadline"));
            innerTransaction.getAsJsonObject("transaction").add("fee", transaction.getAsJsonArray("fee"));
            innerTransaction.getAsJsonObject("transaction").addProperty("signature", transaction.get("signature").getAsString());
            if (!innerTransaction.has("meta")) {
                innerTransaction.add("meta", input.getAsJsonObject("meta"));
            }
            transactions.add(new TransactionMapping().apply(innerTransaction));
        }

        List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
        if (transaction.getAsJsonArray("cosignatures") != null) {
            cosignatures = StreamSupport.stream(transaction.getAsJsonArray("cosignatures").spliterator(), false)
                    .map(item -> (JsonObject) item)
                    .map(aggregateCosignature -> new AggregateTransactionCosignature(
                            aggregateCosignature.get("signature").getAsString(),
                            new PublicAccount(aggregateCosignature.get("signer").getAsString(), networkType)))
                    .collect(Collectors.toList());
        }

        return new AggregateTransaction(
                networkType,
                TransactionType.rawValueOf(transaction.get("type").getAsInt()),
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                transactions,
                cosignatures,
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), networkType),
                transactionInfo
        );
    }
}

class LockFundsTransactionMapping extends TransactionMapping {

    @Override
    public LockFundsTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.get("version").getAsInt());
        Mosaic mosaic;
        if (transaction.has("mosaicId")) {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getAsJsonArray("mosaicId"))), extractBigInteger(transaction.getAsJsonArray("amount")));
        } else {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getAsJsonObject("mosaic").getAsJsonArray("id"))), extractBigInteger(transaction.getAsJsonObject("mosaic").getAsJsonArray("amount")));
        }
        return new LockFundsTransaction(
                networkType,
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                mosaic,
                extractBigInteger(transaction.getAsJsonArray("duration")),
                new SignedTransaction("", transaction.get("hash").getAsString(), TransactionType.AGGREGATE_BONDED),
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), networkType),
                transactionInfo
        );
    }
}

class SecretLockTransactionMapping extends TransactionMapping {

    @Override
    public SecretLockTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.get("version").getAsInt());
        Mosaic mosaic;
        if (transaction.has("mosaicId")) {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getAsJsonArray("mosaicId"))), extractBigInteger(transaction.getAsJsonArray("amount")));
        } else {
            mosaic = new Mosaic(new MosaicId(extractBigInteger(transaction.getAsJsonObject("mosaic").getAsJsonArray("id"))), extractBigInteger(transaction.getAsJsonObject("mosaic").getAsJsonArray("amount")));
        }
        return new SecretLockTransaction(
                networkType,
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                mosaic,
                extractBigInteger(transaction.getAsJsonArray("duration")),
                HashType.rawValueOf(transaction.get("hashAlgorithm").getAsInt()),
                transaction.get("secret").getAsString(),
                Address.createFromEncoded(transaction.get("recipient").getAsString()),
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), networkType),
                transactionInfo
        );
    }
}

class SecretProofTransactionMapping extends TransactionMapping {

    @Override
    public SecretProofTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getAsJsonObject("meta"));

        JsonObject transaction = input.getAsJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getAsJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.get("version").getAsInt());

        return new SecretProofTransaction(
                networkType,
                extractTransactionVersion(transaction.get("version").getAsInt()),
                deadline,
                extractBigInteger(transaction.getAsJsonArray("fee")),
                HashType.rawValueOf(transaction.get("hashAlgorithm").getAsInt()),
                transaction.get("secret").getAsString(),
                transaction.get("proof").getAsString(),
                transaction.get("signature").getAsString(),
                new PublicAccount(transaction.get("signer").getAsString(), networkType),
                transactionInfo
        );
    }
}

