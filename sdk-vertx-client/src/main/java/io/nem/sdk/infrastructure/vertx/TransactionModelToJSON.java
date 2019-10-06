package io.nem.sdk.infrastructure.vertx;

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
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Convert transaction model to JSON.
 *
 * @author Ravi Shanker
 */
class TransactionModelToJSON {

    /**
     * Serializes transaction to json object.
     *
     * @param object
     * @return JsonObject
     */
    JsonObject convert(Object object) {
        Transaction transaction = (Transaction) object;
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("type", transaction.getType().getValue());
        jsonObject.put("networkType", transaction.getNetworkType().getValue());
        jsonObject.put("version", transaction.getTransactionVersion());
        jsonObject.put("maxFee", transaction.getMaxFee().toString());
        jsonObject.put("deadline", String.valueOf(transaction.getDeadline().getInstant()));
        jsonObject.put("signature", (transaction.getSignature().isPresent() ? transaction.getSignature().get() : ""));
        transaction.getSigner().ifPresent(signer ->jsonObject.put("signerPublicKey", signer.getPublicKey().toString()));

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

    private JsonObject toJSONObject(MosaicSupplyChangeTransaction transaction, JsonObject jsonObject) {
        jsonObject.put("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.put("direction", transaction.getAction().getValue());
        jsonObject.put("delta", transaction.getDelta().toString());
        return jsonObject;
    }

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

    private JsonObject toJSONObject(HashLockTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("mosaicId", transaction.getMosaic().getId().getIdAsHex());
        jsonObject.put("amount", transaction.getMosaic().getAmount().toString());
        jsonObject.put("duration", transaction.getDuration().toString());
        jsonObject.put("hash", transaction.getSignedTransaction().getHash());
        return jsonObject;
    }

    private JsonObject toJSONObject(SecretLockTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("mosaicId", transaction.getMosaic().getId().getIdAsHex());
        jsonObject.put("amount", transaction.getMosaic().getAmount().toString());
        jsonObject.put("duration", transaction.getDuration().toString());
        jsonObject.put("hashAlgorithm", transaction.getHashAlgorithm().getValue());
        jsonObject.put("secret", transaction.getSecret());
        JsonObject recipientAddress = new JsonObject();
        recipientAddress.put("address", transaction.getRecipient().plain());
        recipientAddress.put("networkType", transaction.getRecipient().getNetworkType().getValue());
        jsonObject.put("recipientAddress", recipientAddress);
        return jsonObject;
    }

    private JsonObject toJSONObject(SecretProofTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("hashAlgorithm", transaction.getHashType().getValue());
        jsonObject.put("secret", transaction.getSecret());
        JsonObject recipientAddress = new JsonObject();
        recipientAddress.put("address", transaction.getRecipient().plain());
        recipientAddress.put("networkType", transaction.getRecipient().getNetworkType().getValue());
        jsonObject.put("recipientAddress", recipientAddress);
        jsonObject.put("proof", transaction.getProof());
        return jsonObject;
    }

    private JsonObject toJSONObject(MultisigAccountModificationTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("minApprovalDelta", transaction.getMinApprovalDelta());
        jsonObject.put("minRemovalDelta", transaction.getMinRemovalDelta());
        List<HashMap> modifications = new ArrayList<>();
        for (MultisigCosignatoryModification mod : transaction.getModifications()) {
            HashMap modificationMap = new HashMap();
            modificationMap.put("cosignatoryPublicKey", mod.getCosignatoryPublicAccount().getPublicKey().toString());
            modificationMap.put("modificationType", mod.getModificationAction().getValue());
            modifications.add(modificationMap);
        }
        jsonObject.put("modifications", modifications);
        return jsonObject;
    }

    private JsonObject toJSONObject(AggregateTransaction transaction, JsonObject jsonObject) {

        List<JsonObject> transactions = new ArrayList<>();
        for (Transaction innerTransaction : transaction.getInnerTransactions()) {
            transactions.add(convert(innerTransaction));
        }
        jsonObject.put("transactions", transactions);

        List<HashMap> cosignatures = new ArrayList<>();
        for (AggregateTransactionCosignature cosignature : transaction.getCosignatures()) {
            HashMap cosignatureMap = new HashMap();
            cosignatureMap.put("signature", cosignature.getSignature());
            cosignatureMap.put("signerPublicKey", cosignature.getSigner().getPublicKey().toString());
            cosignatures.add(cosignatureMap);
        }
        jsonObject.put("cosignatures", cosignatures);

        return jsonObject;
    }

    private JsonObject toJSONObject(NamespaceRegistrationTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("registrationType", transaction.getNamespaceRegistrationType().getValue());
        jsonObject.put("namespaceName", transaction.getNamespaceName());
        jsonObject.put("id", transaction.getNamespaceId().getIdAsHex());
        transaction.getDuration().ifPresent(duration -> jsonObject.put("duration", duration.toString()));
        transaction.getParentId().ifPresent(parentId -> jsonObject.put("parentId", parentId.getIdAsHex()));

        return jsonObject;
    }

    private JsonObject toJSONObject(AccountMetadataTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("targetPublicKey", transaction.getTargetAccount().getPublicKey().toString());
        jsonObject.put("scopedMetadataKey", transaction.getScopedMetadataKey().toString());
        jsonObject.put("valueSizeDelta", transaction.getValueSizeDelta());
        jsonObject.put("valueSize", transaction.getValueSize());
        jsonObject.put("value", transaction.getValue());

        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicMetadataTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("targetPublicKey", transaction.getTargetAccount().getPublicKey().toString());
        jsonObject.put("scopedMetadataKey", transaction.getScopedMetadataKey().toString());
        jsonObject.put("valueSizeDelta", transaction.getValueSizeDelta());
        jsonObject.put("targetMosaicId", transaction.getTargetMosaicId().getIdAsHex());
        jsonObject.put("valueSize", transaction.getValueSize());
        jsonObject.put("value", transaction.getValue());

        return jsonObject;
    }

    private JsonObject toJSONObject(NamespaceMetadataTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("targetPublicKey", transaction.getTargetAccount().getPublicKey().toString());
        jsonObject.put("scopedMetadataKey", transaction.getScopedMetadataKey().toString());
        jsonObject.put("valueSizeDelta", transaction.getValueSizeDelta());
        jsonObject.put("targetNamespaceId", transaction.getTargetNamespaceId().getIdAsHex());
        jsonObject.put("valueSize", transaction.getValueSize());
        jsonObject.put("value", transaction.getValue());

        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicAddressRestrictionTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.put("restrictionKey", transaction.getRestrictionKey().toString());
        JsonObject targetAddress = new JsonObject();
        targetAddress.put("address", transaction.getTargetAddress().plain());
        targetAddress.put("networkType", transaction.getTargetAddress().getNetworkType().getValue());
        jsonObject.put("targetAddress", targetAddress);
        jsonObject.put("previousRestrictionValue", transaction.getPreviousRestrictionValue().toString());
        jsonObject.put("newRestrictionValue", transaction.getNewRestrictionValue().toString());

        return jsonObject;
    }

    private JsonObject toJSONObject(MosaicGlobalRestrictionTransaction transaction, JsonObject jsonObject) {

        jsonObject.put("mosaicId", transaction.getMosaicId().getIdAsHex());
        jsonObject.put("referenceMosaicId", transaction.getReferenceMosaicId().getIdAsHex());
        jsonObject.put("restrictionKey", transaction.getRestrictionKey().toString());
        jsonObject.put("previousRestrictionValue", transaction.getPreviousRestrictionValue().toString());
        jsonObject.put("previousRestrictionType", transaction.getPreviousRestrictionType().getValue());
        jsonObject.put("newRestrictionValue", transaction.getNewRestrictionValue().toString());
        jsonObject.put("newRestrictionType", transaction.getNewRestrictionType().getValue());

        return jsonObject;
    }
}
