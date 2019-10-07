/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.nem.core.crypto.Hashes;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.mosaic.NetworkHarvestMosaic;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.AccountLinkTransactionFactory;
import io.nem.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.CosignatoryModificationActionType;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Tests for transaction model serialization to JSON using Gson.
 *
 * @author Ravi Shanker
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionModelToJsonTest {

    private Account account;
    private Address recipientAddress;
    private Account multisigAccount;
    private Account cosignatoryAccount;
    private NamespaceId namespaceId;
    private MosaicId mosaicId;
    private MosaicNonce mosaicNonce;
    private String generationHash;
    private JsonHelper jsonHelper;
    private Gson gson;

    @BeforeAll
    void setup() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        jsonHelper = new JsonHelperGson(gson);

        generationHash = "A94B1BE81F1D4C95D6D252AD7BA3FFFB1674991FD880B7A57DC3180AF8D69C32";

        account = Account
            .createFromPrivateKey("D9C23EDDC694D81107E6BB0B2AAB6E3C22C0C59F128F1B8FE0A1094736960074", NetworkType.MIJIN_TEST);
        recipientAddress = Account
            .createFromPrivateKey("063F36659A8BB01D5685826C19E2C2CA9D281465B642BD5E43CB69510408ECF7", NetworkType.MIJIN_TEST)
            .getAddress();
        multisigAccount = Account
            .createFromPrivateKey("90F3C67116A4D84085FD40AC63CDDEA369948C93E265FC24703475EDE5368D1B", NetworkType.MIJIN_TEST);
        cosignatoryAccount = Account
            .createFromPrivateKey("9D17F0C7C146D1857A41F075D8A89C24E5521C63F59E4110D3F51F5F1292A73F", NetworkType.MIJIN_TEST);

        mosaicNonce = MosaicNonce.createFromBigInteger(BigInteger.ONE);
        mosaicId = new MosaicId(BigInteger.ONE);
        namespaceId = NamespaceId.createFromId(BigInteger.ONE);
    }

    @Test
    void shouldCreateAccountLinkTransaction() {

        AccountLinkTransaction transaction =
            AccountLinkTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    account.getPublicAccount(),
                    AccountLinkAction.LINK)
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_LINK.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(account.getPublicKey(), json.getAsJsonObject("transaction").get("remotePublicKey").getAsString());
        assertEquals(AccountLinkAction.LINK.getValue(), json.getAsJsonObject("transaction").get("linkAction").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateAccountAddressRestrictionTransaction() {

        AccountRestrictionModification addressRestrictionFilter =
            AccountRestrictionModification.createForAddress(
                AccountRestrictionModificationAction.ADD, recipientAddress);

        AccountAddressRestrictionTransaction transaction =
            AccountAddressRestrictionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
                    Arrays.asList(addressRestrictionFilter))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_ADDRESS_RESTRICTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(AccountRestrictionType.ALLOW_INCOMING_ADDRESS.getValue(), json.getAsJsonObject("transaction").get("restrictionType").getAsInt());
        assertEquals(1, json.getAsJsonObject("transaction").getAsJsonArray("modifications").size());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateAccountMosaicRestrictionTransaction() {

        AccountRestrictionModification mosaicRestrictionFilter =
            AccountRestrictionModification.createForMosaic(
                AccountRestrictionModificationAction.ADD, mosaicId);

        AccountMosaicRestrictionTransaction transaction =
            AccountMosaicRestrictionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    AccountRestrictionType.ALLOW_INCOMING_MOSAIC,
                    Arrays.asList(mosaicRestrictionFilter))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_MOSAIC_RESTRICTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(AccountRestrictionType.ALLOW_INCOMING_MOSAIC.getValue(), json.getAsJsonObject("transaction").get("restrictionType").getAsInt());
        assertEquals(1, json.getAsJsonObject("transaction").getAsJsonArray("modifications").size());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateAccountOperationRestrictionTransaction() {

        TransactionType operation = TransactionType.ADDRESS_ALIAS;

        AccountRestrictionModification operationRestrictionFilter =
            AccountRestrictionModification.createForTransactionType(
                AccountRestrictionModificationAction.ADD, operation);

        AccountOperationRestrictionTransaction transaction =
            AccountOperationRestrictionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    AccountRestrictionType.ALLOW_OUTGOING_TRANSACTION_TYPE,
                    Arrays.asList(operationRestrictionFilter))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_OPERATION_RESTRICTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(AccountRestrictionType.ALLOW_OUTGOING_TRANSACTION_TYPE.getValue(), json.getAsJsonObject("transaction").get("restrictionType").getAsInt());
        assertEquals(1, json.getAsJsonObject("transaction").getAsJsonArray("modifications").size());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateAddressAliasTransaction() {

        AddressAliasTransaction transaction =
            AddressAliasTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    AliasAction.LINK,
                    namespaceId,
                    recipientAddress)
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.ADDRESS_ALIAS.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(AliasAction.LINK.getValue(), json.getAsJsonObject("transaction").get("aliasAction").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMosaicAliasTransaction() {

        MosaicAliasTransaction transaction =
            MosaicAliasTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    AliasAction.LINK,
                    namespaceId,
                    mosaicId)
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_ALIAS.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(AliasAction.LINK.getValue(), json.getAsJsonObject("transaction").get("aliasAction").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMosaicDefinitionTransaction() {

        MosaicDefinitionTransaction transaction =
            MosaicDefinitionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    mosaicNonce,
                    mosaicId,
                    MosaicFlags.create(true, true, true),
                    5,
                    new BlockDuration(BigInteger.valueOf(1000)))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_DEFINITION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(7, json.getAsJsonObject("transaction").get("flags").getAsInt());
        assertEquals(5, json.getAsJsonObject("transaction").get("divisibility").getAsInt());
        assertEquals("1000", json.getAsJsonObject("transaction").get("duration").getAsString());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMosaicDefinitionTransactionWithoutDuration() {

        MosaicDefinitionTransaction transaction =
            MosaicDefinitionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    mosaicNonce,
                    mosaicId,
                    MosaicFlags.create(true, false),
                    3,
                    new BlockDuration(BigInteger.valueOf(0)))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_DEFINITION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(1, json.getAsJsonObject("transaction").get("flags").getAsInt());
        assertEquals(3, json.getAsJsonObject("transaction").get("divisibility").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMosaicSupplyChangeTransaction() {

        MosaicSupplyChangeTransaction transaction =
            MosaicSupplyChangeTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    mosaicId,
                    MosaicSupplyChangeActionType.INCREASE,
                    BigInteger.TEN)
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_SUPPLY_CHANGE.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(MosaicSupplyChangeActionType.INCREASE.getValue(), json.getAsJsonObject("transaction").get("direction").getAsInt());
        assertEquals("10", json.getAsJsonObject("transaction").get("delta").getAsString());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateTransferTransaction() {

        TransferTransaction transaction =
            TransferTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    Optional.of(recipientAddress),
                    Optional.empty(),
                    Arrays.asList(
                    NetworkHarvestMosaic.createAbsolute(BigInteger.valueOf(100)),
                    NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(100))),
                    PlainMessage.create("test-message"))
                .build();

        JsonObject json = (JsonObject)jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.TRANSFER.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(2, json.getAsJsonObject("transaction").getAsJsonArray("mosaics").size());
        assertEquals("test-message", json.getAsJsonObject("transaction").getAsJsonObject("message").get("payload").getAsString());
        assertEquals(0, json.getAsJsonObject("transaction").getAsJsonObject("message").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateSecretLockTransaction() {

        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);

        SecretLockTransaction transaction =
            SecretLockTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    NetworkCurrencyMosaic.createAbsolute(BigInteger.TEN),
                    BigInteger.valueOf(100),
                    LockHashAlgorithmType.SHA3_256,
                    secret,
                    recipientAddress)
                .build();

        JsonObject json = (JsonObject)jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.SECRET_LOCK.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(LockHashAlgorithmType.SHA3_256.getValue(), json.getAsJsonObject("transaction").get("hashAlgorithm").getAsInt());
        assertEquals(secret, json.getAsJsonObject("transaction").get("secret").getAsString());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateSecretProofTransaction() {

        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        String proof = Hex.encodeHexString(secretBytes);

        SecretProofTransaction transaction =
            SecretProofTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    LockHashAlgorithmType.SHA3_256,
                    recipientAddress,
                    secret,
                    proof)
                .build();

        JsonObject json = (JsonObject)jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.SECRET_PROOF.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(LockHashAlgorithmType.SHA3_256.getValue(), json.getAsJsonObject("transaction").get("hashAlgorithm").getAsInt());
        assertEquals(secret, json.getAsJsonObject("transaction").get("secret").getAsString());
        assertEquals(proof, json.getAsJsonObject("transaction").get("proof").getAsString());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMultisigCosignatoryModificationTransaction() {

        MultisigCosignatoryModification multisigCosignatoryModification =
            MultisigCosignatoryModification.create(CosignatoryModificationActionType.ADD, cosignatoryAccount.getPublicAccount());

        MultisigAccountModificationTransaction transaction =
            MultisigAccountModificationTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    (byte) 2,
                    (byte) 1,
                    Arrays.asList(multisigCosignatoryModification))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MODIFY_MULTISIG_ACCOUNT.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(2, json.getAsJsonObject("transaction").get("minApprovalDelta").getAsInt());
        assertEquals(1, json.getAsJsonObject("transaction").get("minRemovalDelta").getAsInt());
        assertEquals(1, json.getAsJsonObject("transaction").getAsJsonArray("modifications").size());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateAggregateTransactionComplete() {

        TransferTransaction transferTransaction =
            TransferTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    Optional.of(recipientAddress),
                    Optional.empty(),
                    Arrays.asList(),
                    PlainMessage.create("test-message"))
                .build();

        MultisigCosignatoryModification multisigCosignatoryModification =
            MultisigCosignatoryModification.create(CosignatoryModificationActionType.ADD, cosignatoryAccount.getPublicAccount());

        MultisigAccountModificationTransaction multisigAccountModificationTransaction =
            MultisigAccountModificationTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    (byte) 2,
                    (byte) 1,
                    Arrays.asList(multisigCosignatoryModification))
                .build();

        AggregateTransaction transaction =
            AggregateTransactionFactory
                .createComplete(NetworkType.MIJIN_TEST,
                    Arrays.asList(
                        transferTransaction.toAggregate(account.getPublicAccount()),
                        multisigAccountModificationTransaction.toAggregate(multisigAccount.getPublicAccount())))
                .build();

        JsonObject json = (JsonObject)jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.AGGREGATE_COMPLETE.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(2, json.getAsJsonObject("transaction").getAsJsonArray("transactions").size());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateAggregateTransactionBonded() {

        TransferTransaction transferTransaction =
            TransferTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    Optional.of(recipientAddress),
                    Optional.empty(),
                    Arrays.asList(),
                    PlainMessage.create("test-message"))
                .build();

        MultisigCosignatoryModification multisigCosignatoryModification =
            MultisigCosignatoryModification.create(CosignatoryModificationActionType.ADD, cosignatoryAccount.getPublicAccount());

        MultisigAccountModificationTransaction multisigAccountModificationTransaction =
            MultisigAccountModificationTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    (byte) 2,
                    (byte) 1,
                    Arrays.asList(multisigCosignatoryModification))
                .build();

        AggregateTransaction transaction =
            AggregateTransactionFactory
                .createBonded(NetworkType.MIJIN_TEST,
                    Arrays.asList(
                        transferTransaction.toAggregate(account.getPublicAccount()),
                        multisigAccountModificationTransaction.toAggregate(multisigAccount.getPublicAccount())))
                .build();

        JsonObject json = (JsonObject)jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.AGGREGATE_BONDED.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(2, json.getAsJsonObject("transaction").getAsJsonArray("transactions").size());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateHashLockTransaction() {

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory
                .createBonded(NetworkType.MIJIN_TEST, Arrays.asList())
                .build();

        SignedTransaction signedTransaction = account.sign(aggregateTransaction, generationHash);

        HashLockTransaction transaction =
            HashLockTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    NetworkCurrencyMosaic.createAbsolute(BigInteger.TEN),
                    BigInteger.valueOf(10),
                    signedTransaction)
                .build();

        JsonObject json = (JsonObject)jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.LOCK.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        assertEquals(signedTransaction.getHash(), json.getAsJsonObject("transaction").get("hash").getAsString());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateRootNamespaceRegistrationTransaction() {

        NamespaceRegistrationTransaction transaction =
            NamespaceRegistrationTransactionFactory
                .createRootNamespace(NetworkType.MIJIN_TEST,
                    "root-test-namespace",
                    BigInteger.valueOf(1000))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.REGISTER_NAMESPACE.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateSubNamespaceRegistrationTransaction() {

        NamespaceRegistrationTransaction transaction =
            NamespaceRegistrationTransactionFactory
                .createSubNamespace(NetworkType.MIJIN_TEST,
                    "sub-test-namespace",
                    namespaceId)
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.REGISTER_NAMESPACE.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateAccountMetadataTransaction() {

        AccountMetadataTransaction transaction =
            AccountMetadataTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    account.getPublicAccount(),
                    BigInteger.ONE,
                    2,
                    21,
                    "test-account-metadata")
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_METADATA_TRANSACTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMosaicMetadataTransaction() {

        MosaicMetadataTransaction transaction =
            MosaicMetadataTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    account.getPublicAccount(),
                    mosaicId,
                    BigInteger.ONE,
                    2,
                    21,
                    "test-mosaic-metadata")
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_METADATA_TRANSACTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateNamespaceMetadataTransaction() {

        NamespaceMetadataTransaction transaction =
            NamespaceMetadataTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    account.getPublicAccount(),
                    namespaceId,
                    BigInteger.ONE,
                    2,
                    21,
                    "test-namespace-metadata")
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.NAMESPACE_METADATA_TRANSACTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMosaicAddressRestrictionTransaction() {

        MosaicAddressRestrictionTransaction transaction =
            MosaicAddressRestrictionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    mosaicId,
                    BigInteger.ONE,
                    account.getAddress(),
                    BigInteger.valueOf(9),
                    BigInteger.valueOf(8))
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_ADDRESS_RESTRICTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    @Test
    void shouldCreateMosaicGlobalRestrictionTransaction() {

        MosaicGlobalRestrictionTransaction transaction =
            MosaicGlobalRestrictionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    mosaicId, // restrictedMosaicId
                    new MosaicId(new BigInteger("2")), // referenceMosaicId
                    BigInteger.valueOf(1),    // restrictionKey
                    BigInteger.valueOf(9),    // previousRestrictionValue
                    MosaicRestrictionType.EQ, // previousRestrictionType
                    BigInteger.valueOf(8),    // newRestrictionValue
                    MosaicRestrictionType.GE)  // newRestrictionType
                .build();

        JsonObject json = (JsonObject) jsonHelper.toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_GLOBAL_RESTRICTION.getValue(), json.getAsJsonObject("transaction").get("type").getAsInt());
        validateJsonPretty(json, transaction);
    }

    void validateJsonPretty(JsonObject jsonObject, Transaction transaction) {
        // validate json string
        String json = jsonObject.toString();
        assertNotNull(json);
        assertEquals(json, jsonHelper.toJSON(transaction));
        // validate json pretty
        String jsonPretty = gson.toJson(jsonObject);
        String actual;
        actual = jsonHelper.toJSONPretty(jsonObject.toString());
        assertEquals(jsonPretty, actual);
        actual = jsonHelper.toJSONPretty(jsonHelper.toJSONPretty(transaction));
        assertEquals(jsonPretty, actual);
    }
}
