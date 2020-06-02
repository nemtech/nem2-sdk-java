/*
 * Copyright 2020 NEM
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

package io.nem.symbol.sdk.infrastructure.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.openapi.vertx.model.AggregateTransactionBodyExtendedDTO;
import io.nem.symbol.sdk.openapi.vertx.model.HashLockTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicDefinitionTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicSupplyChangeTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigAccountModificationTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceRegistrationTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.SecretProofTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransferTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.UnresolvedMosaic;
import io.vertx.core.json.Json;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionMapperVertxTest {


    private final JsonHelper jsonHelper = new JsonHelperJackson2(
        JsonHelperJackson2.configureMapper(Json.mapper));


    @Test
    void shouldFailWhenNotTransactionType() {
        TransactionInfoDTO transaction = new TransactionInfoDTO();

        Assertions.assertEquals(
            "Transaction cannot be mapped, object does not not have transaction type.",
            Assertions.assertThrows(IllegalArgumentException.class, () -> map(transaction))
                .getMessage());
    }


    @Test
    void shouldCreateStandaloneTransferTransaction() {
        TransactionInfoDTO transferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "standaloneTransferTransaction.json");

        Transaction transferTransaction = map(transferTransactionDTO);

        validateStandaloneTransaction(transferTransaction, transferTransactionDTO);
    }


    @Test
    void shouldCreateTransferEmptyMessage() {
        TransactionInfoDTO transferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "transferEmptyMessage.json");

        TransferTransaction transferTransaction = (TransferTransaction) map(transferTransactionDTO);

        validateStandaloneTransaction(transferTransaction, transferTransactionDTO);
        Assertions.assertEquals("", transferTransaction.getMessage().getPayload());
    }

    @Test
    void shouldCreateAggregateTransferTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateTransferTransaction.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);
        validateAggregateTransaction((AggregateTransaction) aggregateTransferTransaction,
            aggregateTransferTransactionDTO);
    }

    @Test
    void shouldCreateAggregateTransferTransactionUsingAlias() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateTransferTransactionUsingAlias.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);
        validateAggregateTransaction((AggregateTransaction) aggregateTransferTransaction,
            aggregateTransferTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneRootNamespaceCreationTransaction() {
        TransactionInfoDTO namespaceCreationTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("standaloneRootNamespaceCreationTransaction.json"
            );

        Transaction namespaceCreationTransaction = map(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateRootNamespaceCreationTransaction() {
        TransactionInfoDTO aggregateNamespaceCreationTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateRootNamespaceCreationTransaction.json"
            );

        Transaction aggregateNamespaceCreationTransaction =
            map(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateNamespaceCreationTransaction,
            aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSubNamespaceCreationTransaction() {
        TransactionInfoDTO namespaceCreationTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("standaloneSubNamespaceCreationTransaction.json"
            );

        Transaction namespaceCreationTransaction =
            map(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSubNamespaceCreationTransaction() {
        TransactionInfoDTO aggregateNamespaceCreationTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateSubNamespaceCreationTransaction.json"
            );

        Transaction aggregateNamespaceCreationTransaction =
            map(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateNamespaceCreationTransaction,
            aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicCreationTransaction() {
        TransactionInfoDTO mosaicCreationTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "standaloneMosaicCreationTransaction.json");

        Transaction mosaicCreationTransaction = map(mosaicCreationTransactionDTO);

        validateStandaloneTransaction(mosaicCreationTransaction, mosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicCreationTransaction() {
        TransactionInfoDTO aggregateMosaicCreationTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json"
            );

        Transaction aggregateMosaicCreationTransaction =
            map(aggregateMosaicCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicCreationTransaction,
            aggregateMosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicSupplyChangeTransaction() {
        TransactionInfoDTO mosaicSupplyChangeTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("standaloneMosaicSupplyChangeTransaction.json"
            );

        Transaction mosaicSupplyChangeTransaction =
            map(mosaicSupplyChangeTransactionDTO);

        validateStandaloneTransaction(mosaicSupplyChangeTransaction,
            mosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicSupplyChangeTransaction() {
        TransactionInfoDTO aggregateMosaicSupplyChangeTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateMosaicSupplyChangeTransaction.json"
            );

        Transaction aggregateMosaicSupplyChangeTransaction =
            map(aggregateMosaicSupplyChangeTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicSupplyChangeTransaction,
            aggregateMosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicAddressRestrictionTransaction() {
        TransactionInfoDTO mosaicAddressRestrictionTransactionDTO = TestHelperVertx
            .loadTransactionInfoDTO(
            "standaloneMosaicAddressRestrictionTransaction.json");

        Transaction mosaicAddressRestrictionTransaction = map(
            mosaicAddressRestrictionTransactionDTO);

        validateStandaloneTransaction(mosaicAddressRestrictionTransaction,
            mosaicAddressRestrictionTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicAddressRestrictionTransaction() {
        TransactionInfoDTO aggregateMosaicAddressRestrictionTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateMosaicAddressRestrictionTransaction.json"
            );

        Transaction aggregateMosaicAddressRestrictionTransaction =
            map(aggregateMosaicAddressRestrictionTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicAddressRestrictionTransaction,
            aggregateMosaicAddressRestrictionTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicGlobalRestrictionTransaction() {
        TransactionInfoDTO mosaicGlobalRestrictionTransactionDTO = TestHelperVertx
            .loadTransactionInfoDTO(
            "standaloneMosaicGlobalRestrictionTransaction.json");

        Transaction mosaicGlobalRestrictionTransaction = map(mosaicGlobalRestrictionTransactionDTO);

        validateStandaloneTransaction(mosaicGlobalRestrictionTransaction,
            mosaicGlobalRestrictionTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicGlobalRestrictionTransaction() {
        TransactionInfoDTO aggregateMosaicGlobalRestrictionTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateMosaicGlobalRestrictionTransaction.json"
            );

        Transaction aggregateMosaicGlobalRestrictionTransaction =
            map(aggregateMosaicGlobalRestrictionTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicGlobalRestrictionTransaction,
            aggregateMosaicGlobalRestrictionTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMultisigModificationTransaction() {
        TransactionInfoDTO multisigModificationTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("standaloneMultisigModificationTransaction.json"
            );

        Transaction multisigModificationTransaction =
            map(multisigModificationTransactionDTO);

        validateStandaloneTransaction(
            multisigModificationTransaction, multisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMultisigModificationTransaction() {
        TransactionInfoDTO aggregateMultisigModificationTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateMultisigModificationTransaction.json"
            );

        Transaction aggregateMultisigModificationTransaction =
            map(aggregateMultisigModificationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMultisigModificationTransaction,
            aggregateMultisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneLockFundsTransaction() {
        TransactionInfoDTO lockFundsTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("standaloneLockFundsTransaction.json");

        Transaction lockFundsTransaction = map(lockFundsTransactionDTO);

        validateStandaloneTransaction(lockFundsTransaction, lockFundsTransactionDTO);
    }

    @Test
    void shouldCreateAggregateLockFundsTransaction() {
        TransactionInfoDTO aggregateLockFundsTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateLockFundsTransaction.json"
            );

        Transaction lockFundsTransaction =
            map(aggregateLockFundsTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) lockFundsTransaction, aggregateLockFundsTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretLockTransaction() {
        TransactionInfoDTO secretLockTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("standaloneSecretLockTransaction.json"
            );

        Transaction secretLockTransaction = map(secretLockTransactionDTO);

        validateStandaloneTransaction(secretLockTransaction, secretLockTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretLockTransaction() {
        TransactionInfoDTO aggregateSecretLockTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateSecretLockTransaction.json");

        Transaction aggregateSecretLockTransaction = map(aggregateSecretLockTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateSecretLockTransaction,
            aggregateSecretLockTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretProofTransaction() {
        TransactionInfoDTO secretProofTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("standaloneSecretProofTransaction.json");

        Transaction secretProofTransaction = map(secretProofTransactionDTO);
        validateStandaloneTransaction(secretProofTransaction, secretProofTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretProofTransaction() {
        TransactionInfoDTO aggregateSecretProofTransactionDTO =
            TestHelperVertx.loadTransactionInfoDTO("aggregateSecretProofTransaction.json");

        Transaction aggregateSecretProofTransaction =
            map(aggregateSecretProofTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateSecretProofTransaction,
            aggregateSecretProofTransactionDTO);
    }

    private Transaction map(TransactionInfoDTO jsonObject) {
        return new GeneralTransactionMapper(jsonHelper).mapFromDto(jsonObject);
    }

    void validateStandaloneTransaction(Transaction transaction, TransactionInfoDTO transactionDTO) {
        validateStandaloneTransaction(transaction, transactionDTO, transactionDTO);
    }

    void validateStandaloneTransaction(Transaction transaction,
        TransactionInfoDTO transactionDTO,
        TransactionInfoDTO parentTransaction) {
        assertEquals(
            transactionDTO.getMeta().getHeight(),
            transaction.getTransactionInfo().get().getHeight());
        if (transaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getHash(),
                transaction.getTransactionInfo().get().getHash().get());
        }
        if (transaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getMerkleComponentHash(),
                transaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (transaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(
                transaction.getTransactionInfo().get().getIndex().get(),
                transactionDTO.getMeta().getIndex());
        }
//        if (transaction.getTransactionInfo().get().getId().isPresent()) {
//            assertEquals(
//                transactionDTO.getMeta().getId(),
//                transaction.getTransactionInfo().get().getId().get());
//        }
//        if (transaction.getTransactionInfo().get().getAggregateHash().isPresent()) {
//            assertEquals(
//                transactionDTO.getMeta().getAggregateHash(),
//                transaction.getTransactionInfo().get().getAggregateHash().get());
//        }
//        if (transaction.getTransactionInfo().get().getAggregateId().isPresent()) {
//            assertEquals(
//                transactionDTO.getMeta().getAggregateId(),
//                transaction.getTransactionInfo().get().getAggregateId().get());
//        }

        assertEquals(
            jsonHelper.getString(parentTransaction.getTransaction(), "signature"),
            transaction.getSignature().get());
        assertEquals(
            jsonHelper.getString(transactionDTO.getTransaction(), "signerPublicKey"),
            transaction.getSigner().get().getPublicKey().toHex());
        assertEquals(transaction.getType().getValue(),
            (int) jsonHelper.getInteger(transactionDTO.getTransaction(), "type"));
        int version = jsonHelper.getInteger(transactionDTO.getTransaction(), "version");
        assertEquals((int) transaction.getVersion(), version);
        int networkType = jsonHelper.getInteger(transactionDTO.getTransaction(), "network");
        assertEquals(transaction.getNetworkType().getValue(), networkType);
        assertEquals(
            jsonHelper.getBigInteger(parentTransaction.getTransaction(), "maxFee"),
            transaction.getMaxFee());
        assertNotNull(transaction.getDeadline());

        if (transaction.getType() == TransactionType.TRANSFER) {
            validateTransferTx((TransferTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.NAMESPACE_REGISTRATION) {
            validateNamespaceCreationTx((NamespaceRegistrationTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_DEFINITION) {
            validateMosaicCreationTx((MosaicDefinitionTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_SUPPLY_CHANGE) {
            validateMosaicSupplyChangeTx((MosaicSupplyChangeTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.MULTISIG_ACCOUNT_MODIFICATION) {
            validateMultisigModificationTx((MultisigAccountModificationTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.HASH_LOCK) {
            validateLockFundsTx((HashLockTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_LOCK) {
            validateSecretLockTx((SecretLockTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_PROOF) {
            validateSecretProofTx((SecretProofTransaction) transaction, transactionDTO);
        }
    }

    @Test
    void shouldCreateAggregateAddressAliasTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateAddressAliasTransaction.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);

        AddressAliasTransaction transaction = (AddressAliasTransaction) ((AggregateTransaction) aggregateTransferTransaction)
            .getInnerTransactions().get(0);

        Assertions.assertEquals("SDT4THYNVUQK2GM6XXYTWHZXSPE3AUA2GTDPM2XA",
            transaction.getAddress().plain());
        Assertions.assertEquals(AliasAction.LINK, transaction.getAliasAction());
        Assertions.assertEquals(new BigInteger("307262000798378"),
            transaction.getNamespaceId().getId());
    }


    @Test
    void shouldCreateAggregateMosaicAliasTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateMosaicAliasTransaction.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);

        MosaicAliasTransaction transaction = (MosaicAliasTransaction) ((AggregateTransaction) aggregateTransferTransaction)
            .getInnerTransactions().get(0);

        Assertions
            .assertEquals(new BigInteger("884562898459306"), transaction.getMosaicId().getId());
        Assertions.assertEquals(AliasAction.UNLINK, transaction.getAliasAction());
        Assertions.assertEquals(new BigInteger("307262000798378"),
            transaction.getNamespaceId().getId());
    }

    @Test
    void shouldCreateAggregateMosaicMetadataTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateMosaicMetadataTransaction.json"
        );

        AggregateTransaction aggregateTransferTransaction = (AggregateTransaction) map(
            aggregateTransferTransactionDTO);

        validateAggregateTransaction(aggregateTransferTransaction, aggregateTransferTransactionDTO);

        MosaicMetadataTransaction transaction = (MosaicMetadataTransaction) aggregateTransferTransaction
            .getInnerTransactions().get(0);

        Assertions.assertEquals("SDT4THYNVUQK2GM6XXYTWHZXSPE3AUA2GTDPM2XA",
            transaction.getTargetAccount().getAddress().plain());

        Assertions.assertEquals(1, transaction.getValueSizeDelta());
        Assertions.assertEquals(BigInteger.valueOf(3), transaction.getScopedMetadataKey());
        Assertions
            .assertEquals("This is the message for this account! 汉字89664", transaction.getValue());
        Assertions.assertEquals("0003070467832AAA", transaction.getTargetMosaicId().getIdAsHex());
    }

    @Test
    void shouldCreateAggregateNamespaceMetadataTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateNamespaceMetadataTransaction.json"
        );

        AggregateTransaction aggregateTransferTransaction = (AggregateTransaction) map(
            aggregateTransferTransactionDTO);

        validateAggregateTransaction(aggregateTransferTransaction, aggregateTransferTransactionDTO);

        NamespaceMetadataTransaction transaction = (NamespaceMetadataTransaction) aggregateTransferTransaction
            .getInnerTransactions().get(0);

        Assertions.assertEquals("SDT4THYNVUQK2GM6XXYTWHZXSPE3AUA2GTDPM2XA",
            transaction.getTargetAccount().getAddress().plain());

        Assertions.assertEquals(1, transaction.getValueSizeDelta());
        Assertions.assertEquals(BigInteger.valueOf(3), transaction.getScopedMetadataKey());
        Assertions
            .assertEquals("This is the message for this account! 汉字89664", transaction.getValue());
        Assertions
            .assertEquals("0003070467832AAA", transaction.getTargetNamespaceId().getIdAsHex());
    }

    @Test
    void shouldCreateAggregateAccountMetadataTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateAccountMetadataTransaction.json"
        );

        AggregateTransaction aggregateTransferTransaction = (AggregateTransaction) map(
            aggregateTransferTransactionDTO);

        validateAggregateTransaction(aggregateTransferTransaction, aggregateTransferTransactionDTO);

        AccountMetadataTransaction transaction = (AccountMetadataTransaction) aggregateTransferTransaction
            .getInnerTransactions().get(0);

        Assertions.assertEquals("SDT4THYNVUQK2GM6XXYTWHZXSPE3AUA2GTDPM2XA",
            transaction.getTargetAccount().getAddress().plain());

        Assertions.assertEquals(1, transaction.getValueSizeDelta());
        Assertions.assertEquals(BigInteger.valueOf(3), transaction.getScopedMetadataKey());
        Assertions
            .assertEquals("This is the message for this account! 汉字89664", transaction.getValue());
    }


    @Test
    public void shouldCreateAccountAddressRestriction() throws Exception {

        TransactionInfoDTO transactionInfoDTO = TestHelperVertx.loadTransactionInfoDTO(
            "accountAddressRestrictionTransaction.json");

        AccountAddressRestrictionTransaction transaction = (AccountAddressRestrictionTransaction) map(
            transactionInfoDTO);

        validateStandaloneTransaction(transaction, transactionInfoDTO);

        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            transaction.getRestrictionFlags());
        Assertions.assertEquals(1, transaction.getRestrictionAdditions().size());
        Assertions.assertEquals(
            MapperUtils.toAddressFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC16501111"),
            transaction.getRestrictionAdditions().get(0));

        Assertions.assertEquals(
            MapperUtils.toAddressFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC16502222"),
            transaction.getRestrictionDeletions().get(0));

    }

    @Test
    public void shouldCreateAccountMosaicRestriction() throws Exception {

        TransactionInfoDTO transactionInfoDTO = TestHelperVertx.loadTransactionInfoDTO(
            "accountMosaicRestrictionTransaction.json");

        AccountMosaicRestrictionTransaction transaction = (AccountMosaicRestrictionTransaction) map(
            transactionInfoDTO);

        validateStandaloneTransaction(transaction, transactionInfoDTO);

        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
            transaction.getRestrictionFlags());
        Assertions.assertEquals(1, transaction.getRestrictionAdditions().size());
        Assertions.assertEquals("00003646934825AA",
            transaction.getRestrictionAdditions().get(0).getIdAsHex());
    }

    @Test
    public void shouldCreateAccountOperationRestriction() throws Exception {

        TransactionInfoDTO transactionInfoDTO = TestHelperVertx.loadTransactionInfoDTO(
            "accountOperationRestrictionTransaction.json");

        AccountOperationRestrictionTransaction transaction = (AccountOperationRestrictionTransaction) map(
            transactionInfoDTO);

        validateStandaloneTransaction(transaction, transactionInfoDTO);

        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
            transaction.getRestrictionFlags());
        Assertions.assertEquals(1, transaction.getRestrictionAdditions().size());
        Assertions.assertEquals(TransactionType.MOSAIC_METADATA,
            transaction.getRestrictionAdditions().get(0));
    }


    @Test
    void shouldCreateAggregateAccountKeyLinkTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = TestHelperVertx.loadTransactionInfoDTO(
            "aggregateAccountKeyLinkTransaction.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);

        AccountKeyLinkTransaction transaction = (AccountKeyLinkTransaction) ((AggregateTransaction) aggregateTransferTransaction)
            .getInnerTransactions().get(0);

        Assertions.assertEquals(LinkAction.LINK, transaction.getLinkAction());
        Assertions.assertEquals("SARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJETM3ZSP",
            PublicAccount.createFromPublicKey(transaction.getLinkedPublicKey().toHex(), transaction.getNetworkType())
                .getAddress().plain());
    }


    void validateAggregateTransaction(
        AggregateTransaction aggregateTransaction, TransactionInfoDTO transactionDto) {

        AggregateTransactionBodyExtendedDTO aggregateTransactionBodyDTO = jsonHelper
            .convert(transactionDto.getTransaction(), AggregateTransactionBodyExtendedDTO.class);
        assertEquals(
            transactionDto.getMeta().getHeight(),
            aggregateTransaction.getTransactionInfo().get().getHeight());
        if (aggregateTransaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(
                transactionDto.getMeta().getHash(),
                aggregateTransaction.getTransactionInfo().get().getHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(
                transactionDto.getMeta().getMerkleComponentHash(),
                aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(
                aggregateTransaction.getTransactionInfo().get().getIndex().get(),
                transactionDto.getMeta().getIndex());
        }
//        if (aggregateTransaction.getTransactionInfo().get().getId().isPresent()) {
//            assertEquals(
//                transactionDto.getMeta().getId(),
//                aggregateTransaction.getTransactionInfo().get().getId().get());
//        }

        assertEquals(
            jsonHelper.getString(transactionDto.getTransaction(), "signature"),
            aggregateTransaction.getSignature().get());
        assertEquals(
            jsonHelper.getString(transactionDto.getTransaction(), "signerPublicKey"),
            aggregateTransaction.getSigner().get().getPublicKey().toHex());
        int version = jsonHelper.getInteger(transactionDto.getTransaction(), "version");
        assertEquals((int) aggregateTransaction.getVersion(), version);
        int networkType = jsonHelper.getInteger(transactionDto.getTransaction(), "network");
        assertEquals(aggregateTransaction.getNetworkType().getValue(), networkType);
        assertEquals(aggregateTransaction.getType().getValue(),
            (int) jsonHelper.getInteger(transactionDto.getTransaction(), "type"));
        assertEquals(
            jsonHelper.getBigInteger(transactionDto.getTransaction(), "maxFee"),
            aggregateTransaction.getMaxFee());
        assertNotNull(aggregateTransaction.getDeadline());

        assertEquals(
            aggregateTransactionBodyDTO.getCosignatures().get(0).getSignature(),
            aggregateTransaction.getCosignatures().get(0).getSignature());
        assertEquals(
            aggregateTransactionBodyDTO.getCosignatures().get(0).getSignerPublicKey(),
            aggregateTransaction.getCosignatures().get(0).getSigner().getPublicKey().toHex());

        Transaction innerTransaction = aggregateTransaction.getInnerTransactions().get(0);
        validateStandaloneTransaction(
            innerTransaction,
            jsonHelper.convert(aggregateTransactionBodyDTO.getTransactions().get(0),
                TransactionInfoDTO.class), transactionDto);
    }

    void validateTransferTx(TransferTransaction transaction, TransactionInfoDTO transactionDTO) {
        TransferTransactionDTO transferTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), TransferTransactionDTO.class);

        assertEquals(
            MapperUtils.toUnresolvedAddress(
                transferTransaction.getRecipientAddress()),
            transaction.getRecipient());

        List<UnresolvedMosaic> mosaicsDTO = transferTransaction.getMosaics();
        if (mosaicsDTO != null && mosaicsDTO.size() > 0) {
            assertEquals(
                MapperUtils.fromHexToBigInteger(mosaicsDTO.get(0).getId()),
                transaction.getMosaics().get(0).getId().getId());
            assertEquals(
                mosaicsDTO.get(0).getAmount(),
                transaction.getMosaics().get(0).getAmount());
        }

        if (transaction.getMessage().getPayload().isEmpty()) {
            assertEquals("", transaction.getMessage().getPayload());
        } else {
            assertEquals(
                new String(
                    ConvertUtils.fromHexToBytes(
                        transferTransaction.getMessage().getPayload()),
                    StandardCharsets.UTF_8),
                transaction.getMessage().getPayload());
        }

        assertEquals((int) transferTransaction.getMessage().getType().getValue(),
            transaction.getMessage().getType().getValue());
    }

    void validateNamespaceCreationTx(
        NamespaceRegistrationTransaction transaction, TransactionInfoDTO transactionDTO) {

        NamespaceRegistrationTransactionDTO registerNamespaceTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), NamespaceRegistrationTransactionDTO.class);

        assertEquals((int) registerNamespaceTransaction.getRegistrationType().getValue(),
            transaction.getNamespaceRegistrationType().getValue());
        assertEquals(
            registerNamespaceTransaction.getName(),
            transaction.getNamespaceName());
        assertEquals(
            MapperUtils.fromHexToBigInteger(registerNamespaceTransaction.getId()),
            transaction.getNamespaceId().getId());

        if (transaction.getNamespaceRegistrationType()
            == NamespaceRegistrationType.ROOT_NAMESPACE) {
            assertEquals(
                registerNamespaceTransaction.getDuration(),
                transaction.getDuration().get());
        } else {
            assertEquals(
                MapperUtils.fromHexToBigInteger(registerNamespaceTransaction.getParentId()),
                transaction.getParentId().get().getId());
        }
    }

    void validateMosaicCreationTx(
        MosaicDefinitionTransaction transaction, TransactionInfoDTO transactionDTO) {
        // assertEquals((transactionDTO.getJsonObject("transaction").getJsonArray("parentId")),
        //        transaction.getNamespaceId().getId());
        MosaicDefinitionTransactionDTO mosaicDefinitionTransactionDTO = jsonHelper
            .convert(transactionDTO.getTransaction(), MosaicDefinitionTransactionDTO.class);
        assertEquals(
            MapperUtils.toMosaicId(mosaicDefinitionTransactionDTO.getId()),
            transaction.getMosaicId());
        // assertEquals(transactionDTO.getJsonObject("transaction").getString("name"),
        //        transaction.getMosaicName());
        assertEquals(transaction.getDivisibility(),
            mosaicDefinitionTransactionDTO.getDivisibility().intValue());
        assertEquals(
            mosaicDefinitionTransactionDTO.getDuration().longValue(),
            transaction.getBlockDuration().getDuration());
        assertTrue(transaction.getMosaicFlags().isSupplyMutable());
        assertTrue(transaction.getMosaicFlags().isTransferable());
    }

    void validateMosaicSupplyChangeTx(
        MosaicSupplyChangeTransaction transaction, TransactionInfoDTO transactionDTO) {
        MosaicSupplyChangeTransactionDTO mosaicSupplyChangeTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), MosaicSupplyChangeTransactionDTO.class);
        assertEquals(MapperUtils.fromHexToBigInteger(mosaicSupplyChangeTransaction.getMosaicId()),
            transaction.getMosaicId().getId());
        assertEquals(mosaicSupplyChangeTransaction.getDelta(), transaction.getDelta());
        assertEquals(transaction.getAction().getValue(),
            mosaicSupplyChangeTransaction.getAction().getValue().intValue());
    }

    void validateMultisigModificationTx(
        MultisigAccountModificationTransaction transaction, TransactionInfoDTO transactionDTO) {

        MultisigAccountModificationTransactionDTO modifyMultisigAccountTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(),
                MultisigAccountModificationTransactionDTO.class);
        assertEquals(transaction.getMinApprovalDelta(),
            (int) modifyMultisigAccountTransaction.getMinApprovalDelta());
        assertEquals(transaction.getMinRemovalDelta(),
            (int) modifyMultisigAccountTransaction.getMinRemovalDelta());
        assertEquals(
            modifyMultisigAccountTransaction.getPublicKeyAdditions().get(0),
            transaction
                .getPublicKeyAdditions()
                .get(0)
                .getPublicKey()
                .toHex());
    }

    void validateLockFundsTx(HashLockTransaction transaction, TransactionInfoDTO transactionDTO) {

        HashLockTransactionDTO hashLockTransactionDTO = jsonHelper
            .convert(transactionDTO.getTransaction(), HashLockTransactionDTO.class);

        assertEquals(
            MapperUtils.fromHexToBigInteger(hashLockTransactionDTO.getMosaicId()),
            transaction.getMosaic().getId().getId());
        assertEquals(
            hashLockTransactionDTO.getAmount(),
            transaction.getMosaic().getAmount());
        assertEquals(
            hashLockTransactionDTO.getDuration(),
            transaction.getDuration());
        assertEquals(
            hashLockTransactionDTO.getHash(),
            transaction.getHash());
    }

    void validateSecretLockTx(SecretLockTransaction transaction,
        TransactionInfoDTO transactionDTO) {
        SecretLockTransactionDTO secretLockTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), SecretLockTransactionDTO.class);
        assertEquals(
            MapperUtils.fromHexToBigInteger(secretLockTransaction.getMosaicId()),
            transaction.getMosaic().getId().getId());
        assertEquals(
            secretLockTransaction.getAmount(),
            transaction.getMosaic().getAmount());
        assertEquals(
            secretLockTransaction.getDuration(),
            transaction.getDuration());
        assertEquals((int) secretLockTransaction.getHashAlgorithm().getValue(),
            transaction.getHashAlgorithm().getValue());
        assertEquals(
            secretLockTransaction.getSecret(),
            transaction.getSecret());
        assertEquals(
            Address.createFromEncoded(
                secretLockTransaction.getRecipientAddress()),
            transaction.getRecipient());
    }

    void validateSecretProofTx(SecretProofTransaction transaction,
        TransactionInfoDTO transactionDTO) {
        SecretProofTransactionDTO secretProofTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), SecretProofTransactionDTO.class);
        assertEquals((int) secretProofTransaction.getHashAlgorithm().getValue(),
            transaction.getHashType().getValue());
        assertEquals(
            secretProofTransaction.getSecret(),
            transaction.getSecret());
        assertEquals(
            secretProofTransaction.getProof(), transaction.getProof());
    }

}
