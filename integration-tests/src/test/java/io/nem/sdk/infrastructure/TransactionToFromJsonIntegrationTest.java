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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.AccountLinkTransactionFactory;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import io.vertx.core.json.JsonObject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Tests for transaction serialization to JSON and deserialization from JSON.
 *
 * @author Ravi Shanker
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionToFromJsonIntegrationTest extends BaseIntegrationTest {

    private Account account;
    private Address recipient;
    private Account multisigAccount;
    private Account cosignatoryAccount;
    private Account cosignatoryAccount2;
    private NamespaceId rootNamespaceId;
    private MosaicId mosaicId;
    private String generationHash;
    private long timeoutSeconds;

    @BeforeAll
    void setup() {
        account = this.getTestAccount();
        recipient = this.getRecipient();
        multisigAccount = this.getTestMultisigAccount();
        cosignatoryAccount = this.getTestCosignatoryAccount();
        cosignatoryAccount2 = this.getTestCosignatoryAccount2();
        generationHash = this.getGenerationHash();
        timeoutSeconds = this.getTimeoutSeconds();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateAccountLinkTransaction(RepositoryType type) {

        AccountLinkTransaction transaction =
            AccountLinkTransactionFactory.create(NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                AccountLinkAction.LINK
            ).build();

        String json = this.jsonHelper().toJSONPretty(transaction);
        assertNotNull(json);
        System.out.println(json);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateAccountAddressRestrictionTransaction(RepositoryType type) {

        Address address = Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC");

        AccountRestrictionModification addressRestrictionFilter =
            AccountRestrictionModification.createForAddress(
                AccountRestrictionModificationAction.ADD, address);

        AccountAddressRestrictionTransaction transaction =
            AccountAddressRestrictionTransactionFactory.create(NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
                Arrays.asList(addressRestrictionFilter)
            ).build();

        JsonObject json = (JsonObject) this.jsonHelper().toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_ADDRESS_RESTRICTION.getValue(), json.getJsonObject("transaction").getInteger("type").intValue());
        assertEquals(AccountRestrictionType.ALLOW_INCOMING_ADDRESS.getValue(), json.getJsonObject("transaction").getInteger("restrictionType").intValue());
        assertEquals(1, transaction.getModifications().size());
        System.out.println(json.encodePrettily());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateAccountMosaicRestrictionTransaction(RepositoryType type) {

        Address address = Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC");
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createFromBigInteger(0L), account.getPublicAccount());

        AccountRestrictionModification mosaicRestrictionFilter =
            AccountRestrictionModification.createForMosaic(
                AccountRestrictionModificationAction.ADD, mosaicId);

        AccountMosaicRestrictionTransaction transaction =
            AccountMosaicRestrictionTransactionFactory.create(NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_MOSAIC,
                Arrays.asList(mosaicRestrictionFilter)
            ).build();

        JsonObject json = (JsonObject) this.jsonHelper().toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_MOSAIC_RESTRICTION.getValue(), json.getJsonObject("transaction").getInteger("type").intValue());
        assertEquals(AccountRestrictionType.ALLOW_INCOMING_MOSAIC.getValue(), json.getJsonObject("transaction").getInteger("restrictionType").intValue());
        assertEquals(1, transaction.getModifications().size());
        System.out.println(json.encodePrettily());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateAccountOperationRestrictionTransaction(RepositoryType type) {

        TransactionType operation = TransactionType.ADDRESS_ALIAS;

        AccountRestrictionModification operationRestrictionFilter =
            AccountRestrictionModification.createForTransactionType(
                AccountRestrictionModificationAction.ADD, operation);

        AccountOperationRestrictionTransaction transaction =
            AccountOperationRestrictionTransactionFactory.create(NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_TRANSACTION_TYPE,
                Arrays.asList(operationRestrictionFilter)
            ).build();

        JsonObject json = (JsonObject) this.jsonHelper().toJsonObject(transaction);
        assertEquals(TransactionType.ACCOUNT_OPERATION_RESTRICTION.getValue(), json.getJsonObject("transaction").getInteger("type").intValue());
        assertEquals(AccountRestrictionType.ALLOW_INCOMING_TRANSACTION_TYPE.getValue(), json.getJsonObject("transaction").getInteger("restrictionType").intValue());
        assertEquals(1, transaction.getModifications().size());
        System.out.println(json.encodePrettily());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateAddressAliasTransaction(RepositoryType type) {

        NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.ONE);
        Address address = Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC");
        AddressAliasTransaction transaction =
            AddressAliasTransactionFactory.create(NetworkType.MIJIN_TEST,
                AliasAction.LINK,
                namespaceId,
                address
            ).build();

        JsonObject json = (JsonObject) this.jsonHelper().toJsonObject(transaction);
        assertEquals(TransactionType.ADDRESS_ALIAS.getValue(), json.getJsonObject("transaction").getInteger("type").intValue());
        assertEquals(AliasAction.LINK.getValue(), json.getJsonObject("transaction").getInteger("aliasAction").intValue());
        System.out.println(json);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateMosaicAliasTransaction(RepositoryType type) {

        NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.ONE);
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createFromBigInteger(0L), account.getPublicAccount());
        MosaicAliasTransaction transaction =
            MosaicAliasTransactionFactory.create(NetworkType.MIJIN_TEST,
                AliasAction.LINK,
                namespaceId,
                mosaicId
            ).build();

        JsonObject json = (JsonObject) this.jsonHelper().toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_ALIAS.getValue(), json.getJsonObject("transaction").getInteger("type").intValue());
        assertEquals(AliasAction.LINK.getValue(), json.getJsonObject("transaction").getInteger("aliasAction").intValue());
        System.out.println(json);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateMosaicDefinitionTransaction(RepositoryType type) {

        MosaicNonce mosaicNonce = MosaicNonce.createFromBigInteger(BigInteger.ONE);
        MosaicId mosaicId = MosaicId.createFromNonce(mosaicNonce, account.getPublicAccount());
        MosaicDefinitionTransaction transaction =
            MosaicDefinitionTransactionFactory.create(NetworkType.MIJIN_TEST,
                mosaicNonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                5,
                new BlockDuration(BigInteger.valueOf(1000))
            ).build();

        JsonObject json = (JsonObject) this.jsonHelper().toJsonObject(transaction);
        assertEquals(TransactionType.MOSAIC_DEFINITION.getValue(), json.getJsonObject("transaction").getInteger("type").intValue());
        assertEquals(7, json.getJsonObject("transaction").getInteger("flags").intValue());
        assertEquals(5, json.getJsonObject("transaction").getInteger("divisibility").intValue());
        assertEquals("1000", json.getJsonObject("transaction").getString("duration"));
        System.out.println(json);
    }


    ///////////////////////////////////////

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void createATransferTransactionViaStaticConstructor(RepositoryType type) {

        TransferTransaction transferTx =
            TransferTransactionFactory.create(NetworkType.MIJIN_TEST,
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
                    NetworkType.MIJIN_TEST),
                Arrays.asList(),
                PlainMessage.Empty
            ).build();

        assertEquals(NetworkType.MIJIN_TEST, transferTx.getNetworkType());
        assertTrue(1 == transferTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(transferTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), transferTx.getMaxFee());
        assertEquals(
            new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST),
            transferTx.getRecipient().get());
        assertEquals(0, transferTx.getMosaics().size());
        assertNotNull(transferTx.getMessage());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldCreateTransferTransaction(RepositoryType type) {

        TransferTransaction transferTx =
            TransferTransactionFactory.create(NetworkType.MIJIN_TEST,
                Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC"),
                Arrays.asList(
                    NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(100)),
                    NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(200))),
                PlainMessage.create("test-message")
            ).build();

        JsonObject jsonObject = (JsonObject)this.jsonHelper().toJsonObject(transferTx);
        String json = this.jsonHelper().toJSON(transferTx);
        assertEquals(jsonObject.toString(), json);
        String prettyJson = this.jsonHelper().toJSONPretty(json);
        String jsonPretty = this.jsonHelper().toJSONPretty(transferTx);
        assertEquals(prettyJson, jsonPretty);
        assertNotNull(json);
        System.out.println(prettyJson);
    }
}
