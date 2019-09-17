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

package io.nem.sdk.infrastructure.vertx;

import static io.nem.sdk.infrastructure.vertx.TestHelperVertx.loadTransactionInfoDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.openapi.vertx.model.AggregateTransactionBodyDTO;
import io.nem.sdk.openapi.vertx.model.HashLockTransactionDTO;
import io.nem.sdk.openapi.vertx.model.Mosaic;
import io.nem.sdk.openapi.vertx.model.MosaicDefinitionTransactionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicSupplyChangeTransactionDTO;
import io.nem.sdk.openapi.vertx.model.MultisigAccountModificationTransactionDTO;
import io.nem.sdk.openapi.vertx.model.NamespaceRegistrationTransactionDTO;
import io.nem.sdk.openapi.vertx.model.SecretLockTransactionDTO;
import io.nem.sdk.openapi.vertx.model.SecretProofTransactionDTO;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.sdk.openapi.vertx.model.TransferTransactionDTO;
import io.vertx.core.json.Json;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
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
        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        Transaction transferTransaction = map(transferTransactionDTO);

        validateStandaloneTransaction(transferTransaction, transferTransactionDTO);
    }

    @Test
    void shouldCreateAggregateTransferTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateAggregateTransferTransaction.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);
        validateAggregateTransaction((AggregateTransaction) aggregateTransferTransaction,
            aggregateTransferTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneRootNamespaceCreationTransaction() {
        TransactionInfoDTO namespaceCreationTransactionDTO =
            loadTransactionInfoDTO("shouldCreateStandaloneRootNamespaceCreationTransaction.json"
            );

        Transaction namespaceCreationTransaction = map(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateRootNamespaceCreationTransaction() {
        TransactionInfoDTO aggregateNamespaceCreationTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateRootNamespaceCreationTransaction.json"
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
            loadTransactionInfoDTO("shouldCreateStandaloneSubNamespaceCreationTransaction.json"
            );

        Transaction namespaceCreationTransaction =
            map(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSubNamespaceCreationTransaction() {
        TransactionInfoDTO aggregateNamespaceCreationTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateSubNamespaceCreationTransaction.json"
            );

        Transaction aggregateNamespaceCreationTransaction =
            map(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateNamespaceCreationTransaction,
            aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicCreationTransaction() {
        TransactionInfoDTO mosaicCreationTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneMosaicCreationTransaction.json");

        Transaction mosaicCreationTransaction = map(mosaicCreationTransactionDTO);

        validateStandaloneTransaction(mosaicCreationTransaction, mosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicCreationTransaction() {
        TransactionInfoDTO aggregateMosaicCreationTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateMosaicCreationTransaction.json"
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
            loadTransactionInfoDTO("shouldCreateStandaloneMosaicSupplyChangeTransaction.json"
            );

        Transaction mosaicSupplyChangeTransaction =
            map(mosaicSupplyChangeTransactionDTO);

        validateStandaloneTransaction(mosaicSupplyChangeTransaction,
            mosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicSupplyChangeTransaction() {
        TransactionInfoDTO aggregateMosaicSupplyChangeTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateMosaicSupplyChangeTransaction.json"
            );

        Transaction aggregateMosaicSupplyChangeTransaction =
            map(aggregateMosaicSupplyChangeTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicSupplyChangeTransaction,
            aggregateMosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMultisigModificationTransaction() {
        TransactionInfoDTO multisigModificationTransactionDTO =
            loadTransactionInfoDTO("shouldCreateStandaloneMultisigModificationTransaction.json"
            );

        Transaction multisigModificationTransaction =
            map(multisigModificationTransactionDTO);

        validateStandaloneTransaction(
            multisigModificationTransaction, multisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMultisigModificationTransaction() {
        TransactionInfoDTO aggregateMultisigModificationTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateMultisigModificationTransaction.json"
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
            loadTransactionInfoDTO("shouldCreateStandaloneLockFundsTransaction.json");

        Transaction lockFundsTransaction = map(lockFundsTransactionDTO);

        validateStandaloneTransaction(lockFundsTransaction, lockFundsTransactionDTO);
    }

    @Test
    void shouldCreateAggregateLockFundsTransaction() {
        TransactionInfoDTO aggregateLockFundsTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateLockFundsTransaction.json"
            );

        Transaction lockFundsTransaction =
            map(aggregateLockFundsTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) lockFundsTransaction, aggregateLockFundsTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretLockTransaction() {
        TransactionInfoDTO secretLockTransactionDTO =
            loadTransactionInfoDTO("shouldCreateStandaloneSecretLockTransaction.json"
            );

        Transaction secretLockTransaction = map(secretLockTransactionDTO);

        validateStandaloneTransaction(secretLockTransaction, secretLockTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretLockTransaction() {
        TransactionInfoDTO aggregateSecretLockTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateSecretLockTransaction.json");

        Transaction aggregateSecretLockTransaction = map(aggregateSecretLockTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateSecretLockTransaction,
            aggregateSecretLockTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretProofTransaction() {
        TransactionInfoDTO secretProofTransactionDTO =
            loadTransactionInfoDTO("shouldCreateStandaloneSecretProofTransaction.json");

        Transaction secretProofTransaction = map(secretProofTransactionDTO);
        validateStandaloneTransaction(secretProofTransaction, secretProofTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretProofTransaction() {
        TransactionInfoDTO aggregateSecretProofTransactionDTO =
            loadTransactionInfoDTO("shouldCreateAggregateSecretProofTransaction.json");

        Transaction aggregateSecretProofTransaction =
            map(aggregateSecretProofTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateSecretProofTransaction,
            aggregateSecretProofTransactionDTO);
    }

    private Transaction map(TransactionInfoDTO jsonObject) {
        return new GeneralTransactionMapper(jsonHelper, SignSchema.DEFAULT).map(jsonObject);
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
        if (transaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getId(),
                transaction.getTransactionInfo().get().getId().get());
        }
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
            transaction.getSigner().get().getPublicKey().toString());
        assertEquals(transaction.getType().getValue(),
            (int) jsonHelper.getInteger(transactionDTO.getTransaction(), "type"));
        int version =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDTO.getTransaction(), "version"))
                        .substring(2, 4),
                    16);
        assertTrue(transaction.getVersion() == version);
        int networkType =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDTO.getTransaction(), "version"))
                        .substring(0, 2),
                    16);
        assertEquals(transaction.getNetworkType().getValue(), networkType);
        assertEquals(
            jsonHelper.getBigInteger(parentTransaction.getTransaction(), "maxFee"),
            transaction.getFee());
        assertNotNull(transaction.getDeadline());

        if (transaction.getType() == TransactionType.TRANSFER) {
            validateTransferTx((TransferTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.REGISTER_NAMESPACE) {
            validateNamespaceCreationTx((NamespaceRegistrationTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_DEFINITION) {
            validateMosaicCreationTx((MosaicDefinitionTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_SUPPLY_CHANGE) {
            validateMosaicSupplyChangeTx((MosaicSupplyChangeTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.MODIFY_MULTISIG_ACCOUNT) {
            validateMultisigModificationTx((MultisigAccountModificationTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.LOCK) {
            validateLockFundsTx((HashLockTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_LOCK) {
            validateSecretLockTx((SecretLockTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_PROOF) {
            validateSecretProofTx((SecretProofTransaction) transaction, transactionDTO);
        }
    }

    @Test
    void shouldCreateAggregateAddressAliasTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateAggregateAddressAliasTransaction.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);

        AddressAliasTransaction transaction = (AddressAliasTransaction) ((AggregateTransaction) aggregateTransferTransaction)
            .getInnerTransactions().get(0);

        Assert.assertEquals("SDT4THYNVUQK2GM6XXYTWHZXSPE3AUA2GTDPM2XA",
            transaction.getAddress().plain());
        Assert.assertEquals(AliasAction.LINK, transaction.getAliasAction());
        Assert.assertEquals(new BigInteger("307262000798378"),
            transaction.getNamespaceId().getId());
    }

    @Test
    void shouldCreateAggregateMosaicAliasTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicAliasTransaction.json"
        );

        Transaction aggregateTransferTransaction = map(aggregateTransferTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);

        MosaicAliasTransaction transaction = (MosaicAliasTransaction) ((AggregateTransaction) aggregateTransferTransaction)
            .getInnerTransactions().get(0);

        Assert
            .assertEquals(new BigInteger("884562898459306"), transaction.getMosaicId().getId());
        Assert.assertEquals(AliasAction.LINK, transaction.getAliasAction());
        Assert.assertEquals(new BigInteger("307262000798378"),
            transaction.getNamespaceId().getId());
    }


    void validateAggregateTransaction(
        AggregateTransaction aggregateTransaction, TransactionInfoDTO transactionDto) {

        AggregateTransactionBodyDTO aggregateTransactionBodyDTO = jsonHelper
            .convert(transactionDto.getTransaction(), AggregateTransactionBodyDTO.class);
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
        if (aggregateTransaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(
                transactionDto.getMeta().getId(),
                aggregateTransaction.getTransactionInfo().get().getId().get());
        }

        assertEquals(
            jsonHelper.getString(transactionDto.getTransaction(), "signature"),
            aggregateTransaction.getSignature().get());
        assertEquals(
            jsonHelper.getString(transactionDto.getTransaction(), "signerPublicKey"),
            aggregateTransaction.getSigner().get().getPublicKey().toString());
        int version =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDto.getTransaction(), "version"))
                        .substring(2, 4),
                    16);
        assertEquals((int) aggregateTransaction.getVersion(), version);
        int networkType =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDto.getTransaction(), "version"))
                        .substring(0, 2),
                    16);
        assertEquals(aggregateTransaction.getNetworkType().getValue(), networkType);
        assertEquals(aggregateTransaction.getType().getValue(),
            (int) jsonHelper.getInteger(transactionDto.getTransaction(), "type"));
        assertEquals(
            jsonHelper.getBigInteger(transactionDto.getTransaction(), "maxFee"),
            aggregateTransaction.getFee());
        assertNotNull(aggregateTransaction.getDeadline());

        assertEquals(
            aggregateTransactionBodyDTO.getCosignatures().get(0).getSignature(),
            aggregateTransaction.getCosignatures().get(0).getSignature());
        assertEquals(
            aggregateTransactionBodyDTO.getCosignatures().get(0).getSignerPublicKey(),
            aggregateTransaction.getCosignatures().get(0).getSigner().getPublicKey().toString());

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
            Address.createFromEncoded(
                transferTransaction.getRecipientAddress()),
            transaction.getRecipient().get());

        List<Mosaic> mosaicsDTO = transferTransaction.getMosaics();
        if (mosaicsDTO != null && mosaicsDTO.size() > 0) {
            assertEquals(
                MapperUtils.fromHex(mosaicsDTO.get(0).getId()),
                transaction.getMosaics().get(0).getId().getId());
            assertEquals(
                mosaicsDTO.get(0).getAmount(),
                transaction.getMosaics().get(0).getAmount());
        }

        assertEquals(
            new String(
                Hex.decode(
                    transferTransaction.getMessage().getPayload()),
                StandardCharsets.UTF_8),
            transaction.getMessage().getPayload());

        assertEquals((int) transferTransaction.getMessage().getType().getValue(),
            transaction.getMessage().getType());
    }

    void validateNamespaceCreationTx(
        NamespaceRegistrationTransaction transaction, TransactionInfoDTO transactionDTO) {

        NamespaceRegistrationTransactionDTO registerNamespaceTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), NamespaceRegistrationTransactionDTO.class);

        assertEquals((int) registerNamespaceTransaction.getRegistrationType().getValue(),
            transaction.getNamespaceType().getValue());
        assertEquals(
            registerNamespaceTransaction.getName(),
            transaction.getNamespaceName());
        assertEquals(
            MapperUtils.fromHex(registerNamespaceTransaction.getId()),
            transaction.getNamespaceId().getId());

        if (transaction.getNamespaceType() == NamespaceType.ROOT_NAMESPACE) {
            assertEquals(
                registerNamespaceTransaction.getDuration(),
                transaction.getDuration().get());
        } else {
            assertEquals(
                MapperUtils.fromHex(registerNamespaceTransaction.getParentId()),
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
        assertEquals(transaction.getMosaicProperties().getDivisibility(),
            mosaicDefinitionTransactionDTO.getDivisibility().intValue());
        assertEquals(
            mosaicDefinitionTransactionDTO.getDuration().longValue(),
            transaction.getMosaicProperties().getDuration().longValue());
        assertTrue(transaction.getMosaicProperties().isSupplyMutable());
        assertTrue(transaction.getMosaicProperties().isTransferable());
    }

    void validateMosaicSupplyChangeTx(
        MosaicSupplyChangeTransaction transaction, TransactionInfoDTO transactionDTO) {
        MosaicSupplyChangeTransactionDTO mosaicSupplyChangeTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), MosaicSupplyChangeTransactionDTO.class);
        assertEquals(MapperUtils.fromHex(mosaicSupplyChangeTransaction.getMosaicId()),
            transaction.getMosaicId().getId());
        assertEquals(mosaicSupplyChangeTransaction.getDelta(), transaction.getDelta());
        assertEquals(transaction.getMosaicSupplyType().getValue(),
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
            modifyMultisigAccountTransaction.getModifications().get(0).getCosignatoryPublicKey(),
            transaction
                .getModifications()
                .get(0)
                .getCosignatoryPublicAccount()
                .getPublicKey()
                .toString());
        assertEquals(
            (int) modifyMultisigAccountTransaction.getModifications().get(0).getModificationAction()
                .getValue(), transaction.getModifications().get(0).getType().getValue());
    }

    void validateLockFundsTx(HashLockTransaction transaction, TransactionInfoDTO transactionDTO) {

        HashLockTransactionDTO hashLockTransactionDTO = jsonHelper
            .convert(transactionDTO.getTransaction(), HashLockTransactionDTO.class);

        assertEquals(
            MapperUtils.fromHex(hashLockTransactionDTO.getMosaic().getId()),
            transaction.getMosaic().getId().getId());
        assertEquals(
            hashLockTransactionDTO.getMosaic().getAmount(),
            transaction.getMosaic().getAmount());
        assertEquals(
            hashLockTransactionDTO.getDuration(),
            transaction.getDuration());
        assertEquals(
            hashLockTransactionDTO.getHash(),
            transaction.getSignedTransaction().getHash());
    }

    void validateSecretLockTx(SecretLockTransaction transaction,
        TransactionInfoDTO transactionDTO) {
        SecretLockTransactionDTO secretLockTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), SecretLockTransactionDTO.class);
        assertEquals(
            MapperUtils.fromHex(secretLockTransaction.getMosaicId()),
            transaction.getMosaic().getId().getId());
        assertEquals(
            secretLockTransaction.getAmount(),
            transaction.getMosaic().getAmount());
        assertEquals(
            secretLockTransaction.getDuration(),
            transaction.getDuration());
        assertEquals((int) secretLockTransaction.getHashAlgorithm().getValue(),
            transaction.getHashType().getValue());
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
