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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.nem.core.crypto.Hashes;
import io.nem.sdk.infrastructure.okhttp.JsonHelperGson;
import io.nem.sdk.infrastructure.vertx.JsonHelperJackson2;
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
 * Cross-check transaction model serialization to JSON using Jackson with the same using Gson.
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
    private JsonHelper jsonHelperJackson;
    private JsonHelper jsonHelperGson;

    @BeforeAll
    void setup() {
        jsonHelperJackson = new JsonHelperJackson2(JsonHelperJackson2.configureMapper(new ObjectMapper()));
        jsonHelperGson = new JsonHelperGson(new Gson());

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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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
        
        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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
        
        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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
        
        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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
        
        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
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

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }

    @Test
    void shouldCreateRootNamespaceRegistrationTransaction() {

        NamespaceRegistrationTransaction transaction =
            NamespaceRegistrationTransactionFactory
                .createRootNamespace(NetworkType.MIJIN_TEST,
                    "root-test-namespace",
                    BigInteger.valueOf(1000))
                .build();

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }

    @Test
    void shouldCreateSubNamespaceRegistrationTransaction() {

        NamespaceRegistrationTransaction transaction =
            NamespaceRegistrationTransactionFactory
                .createSubNamespace(NetworkType.MIJIN_TEST,
                    "sub-test-namespace",
                    namespaceId)
                .build();

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }

    @Test
    void shouldCreateAccountMetadataTransaction() {

        AccountMetadataTransaction transaction =
            AccountMetadataTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    account.getPublicAccount(),
                    BigInteger.ONE,
                    "test-account-metadata")
                .build();
        
        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }

    @Test
    void shouldCreateMosaicMetadataTransaction() {

        MosaicMetadataTransaction transaction =
            MosaicMetadataTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    account.getPublicAccount(),
                    mosaicId,
                    BigInteger.ONE,
                    "test-mosaic-metadata")
                .build();

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }

    @Test
    void shouldCreateNamespaceMetadataTransaction() {

        NamespaceMetadataTransaction transaction =
            NamespaceMetadataTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    account.getPublicAccount(),
                    namespaceId,
                    BigInteger.ONE,
                    "test-namespace-metadata")
                .build();

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }

    @Test
    void shouldCreateMosaicAddressRestrictionTransaction() {

        MosaicAddressRestrictionTransaction transaction =
            MosaicAddressRestrictionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    mosaicId,
                    BigInteger.ONE,
                    account.getAddress(),
                    BigInteger.valueOf(8))
                .build();

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }

    @Test
    void shouldCreateMosaicGlobalRestrictionTransaction() {

        MosaicGlobalRestrictionTransaction transaction =
            MosaicGlobalRestrictionTransactionFactory
                .create(NetworkType.MIJIN_TEST,
                    mosaicId, // restrictedMosaicId
                    BigInteger.valueOf(1),    // restrictionKey
                    BigInteger.valueOf(8),    // newRestrictionValue
                    MosaicRestrictionType.GE)  // newRestrictionType
                .build();

        assertEquals(jsonHelperJackson.toJSON(transaction), jsonHelperGson.toJSON(transaction));
        assertEquals(false, transaction.isInnerTransaction());
    }
}
