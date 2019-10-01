package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TransactionModelMapper {

    JsonObject toJSONObject(Transaction transaction) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("type", transaction.getType().getValue());
        jsonObject.put("networkType", transaction.getNetworkType().getValue());
        jsonObject.put("version", transaction.getTransactionVersion());
        jsonObject.put("maxFee", transaction.getMaxFee().toString());
        jsonObject.put("deadline", String.valueOf(transaction.getDeadline().getInstant()));
        jsonObject.put("signature", (transaction.getSignature().isPresent() ? transaction.getSignature().get() : ""));
        if (transaction.getSigner().isPresent()) jsonObject.put("signerPublicKey", transaction.getSigner().get().getPublicKey().toString());

        JsonObject transactionJson = new JsonObject();
        transactionJson.put("transaction", toJSONObject(transaction, jsonObject));
        return transactionJson;
    }

    private JsonObject toJSONObject(Transaction transaction, JsonObject jsonObject) {
        switch (transaction.getType()) {
            case ACCOUNT_LINK:
                AccountLinkTransaction accountLinkTransaction = (AccountLinkTransaction) transaction;
                toJSONObject(accountLinkTransaction, jsonObject);
                break;
            case ACCOUNT_ADDRESS_RESTRICTION:
                AccountAddressRestrictionTransaction accountAddressRestrictionTransaction = (AccountAddressRestrictionTransaction) transaction;
                toJSONObject(accountAddressRestrictionTransaction, jsonObject);
                break;
            case ACCOUNT_MOSAIC_RESTRICTION:
                AccountMosaicRestrictionTransaction accountMosaicRestrictionTransaction = (AccountMosaicRestrictionTransaction) transaction;
                toJSONObject(accountMosaicRestrictionTransaction, jsonObject);
                break;
            case ACCOUNT_OPERATION_RESTRICTION:
                AccountOperationRestrictionTransaction accountOperationRestrictionTransaction = (AccountOperationRestrictionTransaction) transaction;
                toJSONObject(accountOperationRestrictionTransaction, jsonObject);
                break;
            case ADDRESS_ALIAS:
                AddressAliasTransaction addressAliasTransaction = (AddressAliasTransaction) transaction;
                toJSONObject(addressAliasTransaction, jsonObject);
                break;
            case MOSAIC_ALIAS:
                MosaicAliasTransaction mosaicAliasTransaction = (MosaicAliasTransaction) transaction;
                toJSONObject(mosaicAliasTransaction, jsonObject);
                break;
            case MOSAIC_DEFINITION:
                MosaicDefinitionTransaction mosaicDefinitionTransaction = (MosaicDefinitionTransaction) transaction;
                toJSONObject(mosaicDefinitionTransaction, jsonObject);
                break;
            case MOSAIC_SUPPLY_CHANGE:
                break;
            case REGISTER_NAMESPACE:
                break;
            case TRANSFER:
                TransferTransaction transferTransaction = (TransferTransaction) transaction;
                toJSONObject(transferTransaction, jsonObject);
                break;
            case MODIFY_MULTISIG_ACCOUNT:
                break;
            case AGGREGATE_COMPLETE:
                break;
            case AGGREGATE_BONDED:
                break;
            case LOCK:
                break;
            case SECRET_LOCK:
                break;
            case SECRET_PROOF:
                break;
            case ACCOUNT_METADATA_TRANSACTION:
                break;
            case MOSAIC_METADATA_TRANSACTION:
                break;
            case NAMESPACE_METADATA_TRANSACTION:
                break;
            case MOSAIC_ADDRESS_RESTRICTION:
                break;
            case MOSAIC_GLOBAL_RESTRICTION:
                break;
        }
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountLinkTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("remotePublicKey", transaction.getRemoteAccount().getPublicKey().toString());
        jsonObject.put("linkAction", transaction.getLinkAction().getValue());
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountAddressRestrictionTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("restrictionType", transaction.getRestrictionType().getValue());
        List<HashMap> modifications = new ArrayList<>();
        for (AccountRestrictionModification<Address> mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("value", mod.getValue().plain());
            modificationMap.put("modificationAction", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.put("modifications", modifications);
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountMosaicRestrictionTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("restrictionType", transaction.getRestrictionType().getValue());
        List<HashMap> modifications = new ArrayList<>();
        for (AccountRestrictionModification<MosaicId> mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("value", mod.getValue().getId());
            modificationMap.put("modificationAction", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.put("modifications", modifications);
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountOperationRestrictionTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("restrictionType", transaction.getRestrictionType().getValue());
        List<HashMap> modifications = new ArrayList<>();
        for (AccountRestrictionModification<TransactionType> mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("value", mod.getValue().getValue());
            modificationMap.put("modificationAction", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.put("modifications", modifications);
        return jsonObject;
    }

    private JsonObject toJSONObject(AddressAliasTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("aliasAction", transaction.getAliasAction().getValue());
        jsonObject.put("namespaceId", transaction.getNamespaceId().getIdAsHex());
        JsonObject address = new JsonObject();
        address.put("address", transaction.getAddress().plain());
        address.put("networkType", transaction.getAddress().getNetworkType().getValue());
        jsonObject.put("address", address);
        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicAliasTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("aliasAction", transaction.getAliasAction().getValue());
        jsonObject.put("namespaceId", transaction.getNamespaceId().getIdAsHex());
        jsonObject.put("mosaicId", transaction.getMosaicId().getIdAsHex());
        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicDefinitionTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("nonce", transaction.getMosaicNonce().getNonceAsInt());
        jsonObject.put("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.put("flags", transaction.getMosaicFlags().getValue());
        jsonObject.put("divisibility", transaction.getDivisibility());
        jsonObject.put("duration", transaction.getBlockDuration().toString());
        return jsonObject;
    }



    ////////////////////


    private JsonObject toJSONObject(TransferTransaction transaction, JsonObject jsonObject) {
        JsonObject recipientAddress = new JsonObject();
        recipientAddress.put("address", transaction.getRecipient().get().plain());
        recipientAddress.put("networkType", transaction.getRecipient().get().getNetworkType().getValue());
        jsonObject.put("recipientAddress", recipientAddress);

        List<HashMap> mosaics = new ArrayList<>();
        for (Mosaic mosaic : transaction.getMosaics()) {
            HashMap mosaicMap = new HashMap();
            mosaicMap.put("amount", mosaic.getAmount().toString());
            mosaicMap.put("id", mosaic.getId().getIdAsHex());
            mosaics.add(mosaicMap);
        }
        jsonObject.put("mosaics", mosaics);

        JsonObject message = new JsonObject();
        message.put("type", transaction.getMessage().getType());
        message.put("payload", transaction.getMessage().getPayload());
        jsonObject.put("message", message);
        return jsonObject;
    }
}
