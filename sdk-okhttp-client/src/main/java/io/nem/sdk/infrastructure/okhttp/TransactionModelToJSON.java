package io.nem.sdk.infrastructure.okhttp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Convert transaction model to JSON.
 *
 * @author Ravi Shanker
 */
class TransactionModelToJSON {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Serializes transaction to json object.
     *
     * @param object
     * @return JsonObject
     */
    JsonObject convert(Object object) {
        Transaction transaction = (Transaction) object;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", transaction.getType().getValue());
        jsonObject.addProperty("networkType", transaction.getNetworkType().getValue());
        jsonObject.addProperty("version", transaction.getTransactionVersion());
        jsonObject.addProperty("maxFee", transaction.getMaxFee().toString());
        jsonObject.addProperty("deadline", String.valueOf(transaction.getDeadline().getInstant()));
        jsonObject.addProperty("signature", (transaction.getSignature().isPresent() ? transaction.getSignature().get() : ""));
        if (transaction.getSigner().isPresent()) jsonObject.addProperty("signerPublicKey", transaction.getSigner().get().getPublicKey().toString());

        JsonObject transactionJson = new JsonObject();
        transactionJson.add("transaction", toJSONObject(transaction, jsonObject));
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
                MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction = (MosaicSupplyChangeTransaction) transaction;
                toJSONObject(mosaicSupplyChangeTransaction, jsonObject);
                break;
            case TRANSFER:
                TransferTransaction transferTransaction = (TransferTransaction) transaction;
                toJSONObject(transferTransaction, jsonObject);
                break;
            case LOCK:
                HashLockTransaction hashLockTransaction = (HashLockTransaction) transaction;
                toJSONObject(hashLockTransaction, jsonObject);
                break;
            case SECRET_LOCK:
                SecretLockTransaction secretLockTransaction = (SecretLockTransaction) transaction;
                toJSONObject(secretLockTransaction, jsonObject);
                break;
            case SECRET_PROOF:
                SecretProofTransaction secretProofTransaction = (SecretProofTransaction) transaction;
                toJSONObject(secretProofTransaction, jsonObject);
                break;
            case MODIFY_MULTISIG_ACCOUNT:
                MultisigAccountModificationTransaction accountModificationTransaction = (MultisigAccountModificationTransaction) transaction;
                toJSONObject(accountModificationTransaction, jsonObject);
                break;
            case AGGREGATE_BONDED:
            case AGGREGATE_COMPLETE:
                AggregateTransaction aggregateTransaction = (AggregateTransaction) transaction;
                toJSONObject(aggregateTransaction, jsonObject);
                break;
            case REGISTER_NAMESPACE:
                NamespaceRegistrationTransaction namespaceRegistrationTransaction = (NamespaceRegistrationTransaction) transaction;
                toJSONObject(namespaceRegistrationTransaction, jsonObject);
                break;
            case ACCOUNT_METADATA_TRANSACTION:
                AccountMetadataTransaction accountMetadataTransaction = (AccountMetadataTransaction) transaction;
                toJSONObject(accountMetadataTransaction, jsonObject);
                break;
            case MOSAIC_METADATA_TRANSACTION:
                MosaicMetadataTransaction mosaicMetadataTransaction = (MosaicMetadataTransaction) transaction;
                toJSONObject(mosaicMetadataTransaction, jsonObject);
                break;
            case NAMESPACE_METADATA_TRANSACTION:
                NamespaceMetadataTransaction namespaceMetadataTransaction = (NamespaceMetadataTransaction) transaction;
                toJSONObject(namespaceMetadataTransaction, jsonObject);
                break;
            case MOSAIC_ADDRESS_RESTRICTION:
                MosaicAddressRestrictionTransaction mosaicAddressRestrictionTransaction = (MosaicAddressRestrictionTransaction) transaction;
                toJSONObject(mosaicAddressRestrictionTransaction, jsonObject);
                break;
            case MOSAIC_GLOBAL_RESTRICTION:
                MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction = (MosaicGlobalRestrictionTransaction) transaction;
                toJSONObject(mosaicGlobalRestrictionTransaction, jsonObject);
                break;
            default:
                throw new IllegalStateException("Serialization to JSON for transaction type " + transaction.getType() + " not yet implemented.");
        }
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountLinkTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("remotePublicKey", transaction.getRemoteAccount().getPublicKey().toString());
        jsonObject.addProperty("linkAction", transaction.getLinkAction().getValue());
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountAddressRestrictionTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("restrictionType", transaction.getRestrictionType().getValue());
        List<HashMap> modifications = new ArrayList<>();
        for (AccountRestrictionModification<Address> mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("value", mod.getValue().plain());
            modificationMap.put("modificationAction", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.add("modifications", gson.toJsonTree(modifications));
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountMosaicRestrictionTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("restrictionType", transaction.getRestrictionType().getValue());
        List<HashMap> modifications = new ArrayList<>();
        for (AccountRestrictionModification<MosaicId> mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("value", mod.getValue().getId());
            modificationMap.put("modificationAction", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.add("modifications", gson.toJsonTree(modifications));
        return jsonObject;
    }

    private JsonObject toJSONObject(AccountOperationRestrictionTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("restrictionType", transaction.getRestrictionType().getValue());
        List<HashMap> modifications = new ArrayList<>();
        for (AccountRestrictionModification<TransactionType> mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("value", mod.getValue().getValue());
            modificationMap.put("modificationAction", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.add("modifications", gson.toJsonTree(modifications));
        return jsonObject;
    }

    private JsonObject toJSONObject(AddressAliasTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("aliasAction", transaction.getAliasAction().getValue());
        jsonObject.addProperty("namespaceId", transaction.getNamespaceId().getIdAsHex());
        JsonObject address = new JsonObject();
        address.addProperty("address", transaction.getAddress().plain());
        address.addProperty("networkType", transaction.getAddress().getNetworkType().getValue());
        jsonObject.add("address", address);
        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicAliasTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("aliasAction", transaction.getAliasAction().getValue());
        jsonObject.addProperty("namespaceId", transaction.getNamespaceId().getIdAsHex());
        jsonObject.addProperty("mosaicId", transaction.getMosaicId().getIdAsHex());
        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicDefinitionTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("nonce", transaction.getMosaicNonce().getNonceAsInt());
        jsonObject.addProperty("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.addProperty("flags", transaction.getMosaicFlags().getValue());
        jsonObject.addProperty("divisibility", transaction.getDivisibility());
        jsonObject.addProperty("duration", transaction.getBlockDuration().toString());
        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicSupplyChangeTransaction transaction, JsonObject jsonObject) {
        jsonObject.addProperty("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.addProperty("direction", transaction.getAction().getValue());
        jsonObject.addProperty("delta", transaction.getDelta().toString());
        return jsonObject;
    }

    private JsonObject toJSONObject(TransferTransaction transaction, JsonObject jsonObject) {
        JsonObject recipientAddress = new JsonObject();
        recipientAddress.addProperty("address", transaction.getRecipient().get().plain());
        recipientAddress.addProperty("networkType", transaction.getRecipient().get().getNetworkType().getValue());
        jsonObject.add("recipientAddress", recipientAddress);

        List<HashMap> mosaics = new ArrayList<>();
        for (Mosaic mosaic : transaction.getMosaics()) {
            HashMap mosaicMap = new HashMap();
            mosaicMap.put("amount", mosaic.getAmount().toString());
            mosaicMap.put("id", mosaic.getId().getIdAsHex());
            mosaics.add(mosaicMap);
        }
        jsonObject.add("mosaics", gson.toJsonTree(mosaics));

        JsonObject message = new JsonObject();
        message.addProperty("type", transaction.getMessage().getType());
        message.addProperty("payload", transaction.getMessage().getPayload());
        jsonObject.add("message", message);
        return jsonObject;
    }

    private JsonObject toJSONObject(HashLockTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("mosaicId", transaction.getMosaic().getId().getIdAsHex());
        jsonObject.addProperty("amount", transaction.getMosaic().getAmount().toString());
        jsonObject.addProperty("duration", transaction.getDuration().toString());
        jsonObject.addProperty("hash", transaction.getSignedTransaction().getHash());
        return jsonObject;
    }

    private JsonObject toJSONObject(SecretLockTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("mosaicId", transaction.getMosaic().getId().getIdAsHex());
        jsonObject.addProperty("amount", transaction.getMosaic().getAmount().toString());
        jsonObject.addProperty("duration", transaction.getDuration().toString());
        jsonObject.addProperty("hashAlgorithm", transaction.getHashAlgorithm().getValue());
        jsonObject.addProperty("secret", transaction.getSecret());
        JsonObject recipientAddress = new JsonObject();
        recipientAddress.addProperty("address", transaction.getRecipient().plain());
        recipientAddress.addProperty("networkType", transaction.getRecipient().getNetworkType().getValue());
        jsonObject.add("recipientAddress", recipientAddress);
        return jsonObject;
    }

    private JsonObject toJSONObject(SecretProofTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("hashAlgorithm", transaction.getHashType().getValue());
        jsonObject.addProperty("secret", transaction.getSecret());
        JsonObject recipientAddress = new JsonObject();
        recipientAddress.addProperty("address", transaction.getRecipient().plain());
        recipientAddress.addProperty("networkType", transaction.getRecipient().getNetworkType().getValue());
        jsonObject.add("recipientAddress", recipientAddress);
        jsonObject.addProperty("proof", transaction.getProof());
        return jsonObject;
    }

    private JsonObject toJSONObject(MultisigAccountModificationTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("minApprovalDelta", transaction.getMinApprovalDelta());
        jsonObject.addProperty("minRemovalDelta", transaction.getMinRemovalDelta());
        List<HashMap> modifications = new ArrayList<>();
        for (MultisigCosignatoryModification mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("cosignatoryPublicKey", mod.getCosignatoryPublicAccount().getPublicKey().toString());
            modificationMap.put("modificationType", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.add("modifications", gson.toJsonTree(modifications));
        return jsonObject;
    }

    private JsonObject toJSONObject(AggregateTransaction transaction, JsonObject jsonObject) {

        List<JsonObject> transactions = new ArrayList<>();
        for (Transaction innerTransaction : transaction.getInnerTransactions()) {
            transactions.add(convert(innerTransaction));
        }
        jsonObject.add("transactions", gson.toJsonTree(transactions));

        List<HashMap> cosignatures = new ArrayList<>();
        for (AggregateTransactionCosignature cosignature : transaction.getCosignatures()) {
            HashMap cosignatureMap = new HashMap();
            cosignatureMap.put("signature", cosignature.getSignature());
            cosignatureMap.put("signerPublicKey", cosignature.getSigner().getPublicKey().toString());
            cosignatures.add(cosignatureMap);
        }
        jsonObject.add("cosignatures", gson.toJsonTree(cosignatures));

        return jsonObject;
    }

    private JsonObject toJSONObject(NamespaceRegistrationTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("registrationType", transaction.getNamespaceRegistrationType().getValue());
        jsonObject.addProperty("namespaceName", transaction.getNamespaceName());
        jsonObject.addProperty("id", transaction.getNamespaceId().getIdAsHex());
        if (transaction.getDuration().isPresent()) jsonObject.addProperty("duration", transaction.getDuration().get().toString());
        if (transaction.getParentId().isPresent()) jsonObject.addProperty("parentId", transaction.getParentId().get().getIdAsHex());

        return jsonObject;
    }

    private JsonObject toJSONObject(AccountMetadataTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("targetPublicKey", transaction.getTargetAccount().getPublicKey().toString());
        jsonObject.addProperty("scopedMetadataKey", transaction.getScopedMetadataKey().toString());
        jsonObject.addProperty("valueSizeDelta", transaction.getValueSizeDelta());
        jsonObject.addProperty("valueSize", transaction.getValueSize());
        jsonObject.addProperty("value", transaction.getValue());

        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicMetadataTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("targetPublicKey", transaction.getTargetAccount().getPublicKey().toString());
        jsonObject.addProperty("scopedMetadataKey", transaction.getScopedMetadataKey().toString());
        jsonObject.addProperty("valueSizeDelta", transaction.getValueSizeDelta());
        jsonObject.addProperty("targetMosaicId", transaction.getTargetMosaicId().getIdAsHex());
        jsonObject.addProperty("valueSize", transaction.getValueSize());
        jsonObject.addProperty("value", transaction.getValue());

        return jsonObject;
    }

    private JsonObject toJSONObject(NamespaceMetadataTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("targetPublicKey", transaction.getTargetAccount().getPublicKey().toString());
        jsonObject.addProperty("scopedMetadataKey", transaction.getScopedMetadataKey().toString());
        jsonObject.addProperty("valueSizeDelta", transaction.getValueSizeDelta());
        jsonObject.addProperty("targetNamespaceId", transaction.getTargetNamespaceId().getIdAsHex());
        jsonObject.addProperty("valueSize", transaction.getValueSize());
        jsonObject.addProperty("value", transaction.getValue());

        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicAddressRestrictionTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.addProperty("restrictionKey", transaction.getRestrictionKey().toString());
        JsonObject targetAddress = new JsonObject();
        targetAddress.addProperty("address", transaction.getTargetAddress().plain());
        targetAddress.addProperty("networkType", transaction.getTargetAddress().getNetworkType().getValue());
        jsonObject.add("targetAddress", targetAddress);
        jsonObject.addProperty("previousRestrictionValue", transaction.getPreviousRestrictionValue().toString());
        jsonObject.addProperty("newRestrictionValue", transaction.getNewRestrictionValue().toString());

        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicGlobalRestrictionTransaction transaction, JsonObject jsonObject) {

        jsonObject.addProperty("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.addProperty("referenceMosaicId", transaction.getReferenceMosaicId().getIdAsHex());
        jsonObject.addProperty("restrictionKey", transaction.getRestrictionKey().toString());
        jsonObject.addProperty("previousRestrictionValue", transaction.getPreviousRestrictionValue().toString());
        jsonObject.addProperty("previousRestrictionType", transaction.getPreviousRestrictionType().getValue());
        jsonObject.addProperty("newRestrictionValue", transaction.getNewRestrictionValue().toString());
        jsonObject.addProperty("newRestrictionType", transaction.getNewRestrictionType().getValue());

        return jsonObject;
    }
}
