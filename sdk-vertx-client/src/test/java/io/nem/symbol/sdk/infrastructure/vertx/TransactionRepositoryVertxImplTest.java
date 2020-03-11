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

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.openapi.vertx.model.AnnounceTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.Cosignature;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionMetaDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStateTypeEnum;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStatusDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStatusTypeEnum;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit Tests for {@link TransactionRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class TransactionRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private TransactionRepositoryVertxImpl repository;


    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new TransactionRepositoryVertxImpl(apiClientMock);
    }

    @Test
    public void shouldGetTransaction() throws Exception {

        TransactionInfoDTO transactionInfoDTO = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        mockRemoteCall(transactionInfoDTO);

        Transaction transaction = repository
            .getTransaction(transactionInfoDTO.getMeta().getHash()).toFuture().get();

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionInfoDTO.getMeta().getHash(),
            transaction.getTransactionInfo().get().getHash().get());
    }

    @Test
    public void exceptionWhenMapperFails() {

        TransactionInfoDTO transactionInfoDTO = new TransactionInfoDTO();
        TransactionMetaDTO meta = new TransactionMetaDTO();
        meta.setHash("ABC");
        transactionInfoDTO.setMeta(meta);

        mockRemoteCall(transactionInfoDTO);

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> {
                ExceptionUtils.propagateVoid(() -> {
                    repository
                        .getTransaction(transactionInfoDTO.getMeta().getHash()).toFuture().get();
                });
            });

        Assertions.assertTrue(exception.getMessage().contains(
            "Transaction cannot be mapped, object does not not have transaction type."));
    }

    @Test
    public void exceptionWhenRestCallFails() {

        TransactionInfoDTO transactionInfoDTO = new TransactionInfoDTO();
        TransactionMetaDTO meta = new TransactionMetaDTO();
        meta.setHash("ABC");
        transactionInfoDTO.setMeta(meta);

        mockErrorCode(400, "The error message");

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> {
                ExceptionUtils.propagateVoid(() -> {
                    repository
                        .getTransaction(transactionInfoDTO.getMeta().getHash()).toFuture().get();
                });
            });

        Assertions.assertTrue(exception.getMessage().contains(
            "The error message"));
    }

    @Test
    public void shouldGetTransactions() throws Exception {

        TransactionInfoDTO transactionInfoDTO = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        mockRemoteCall(Collections.singletonList(transactionInfoDTO));

        Transaction transaction = repository
            .getTransactions(Collections.singletonList(transactionInfoDTO.getMeta().getHash()))
            .toFuture().get().get(0);

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionInfoDTO.getMeta().getHash(),
            transaction.getTransactionInfo().get().getHash().get());
    }

    @Test
    public void shouldGetTransactionStatus() throws Exception {

        TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
        transactionStatusDTO.setGroup(TransactionStateTypeEnum.FAILED);
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO
            .setCode(TransactionStatusTypeEnum.FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS);
        transactionStatusDTO.setHash("someHash");
        mockRemoteCall(transactionStatusDTO);

        TransactionStatus transaction = repository
            .getTransactionStatus(transactionStatusDTO.getHash()).toFuture().get();

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionStatusDTO.getHash(), transaction.getHash());
        Assertions.assertEquals(5L, transaction.getDeadline().getInstant());
        Assertions.assertEquals(BigInteger.valueOf(6L), transaction.getHeight());
        Assertions.assertEquals("Failure_AccountLink_Link_Already_Exists", transaction.getCode());
        Assertions.assertEquals(transaction.getGroup().getValue(),
            transactionStatusDTO.getGroup().getValue());
    }

    @Test
    public void shouldGetTransactionStatuses() throws Exception {

        TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();

        transactionStatusDTO.setGroup(TransactionStateTypeEnum.FAILED);
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO
            .setCode(TransactionStatusTypeEnum.FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS);
        transactionStatusDTO.setHash("someHash");
        mockRemoteCall(Collections.singletonList(transactionStatusDTO));

        TransactionStatus transaction = repository
            .getTransactionStatuses(Collections.singletonList(transactionStatusDTO.getHash()))
            .toFuture().get().get(0);

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionStatusDTO.getHash(), transaction.getHash());
        Assertions.assertEquals(5L, transaction.getDeadline().getInstant());
        Assertions.assertEquals(BigInteger.valueOf(6L), transaction.getHeight());
        Assertions.assertEquals("Failure_AccountLink_Link_Already_Exists", transaction.getCode());
        Assertions.assertEquals(transaction.getGroup().getValue(),
            transactionStatusDTO.getGroup().getValue());
    }

    @Test
    public void shouldAnnounce() throws Exception {

        SignedTransaction signedTransaction = getSignedTransaction();

        AnnounceTransactionInfoDTO announceTransactionInfoDTO = new AnnounceTransactionInfoDTO();
        announceTransactionInfoDTO.setMessage("SomeMessage");
        mockRemoteCall(announceTransactionInfoDTO);

        TransactionAnnounceResponse response = repository.announce(signedTransaction)
            .toFuture().get();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(announceTransactionInfoDTO.getMessage(),
            announceTransactionInfoDTO.getMessage());
    }

    @Test
    public void shouldAnnounceAggregateBonded() throws Exception {

        SignedTransaction signedTransaction = getSignedTransaction();

        AnnounceTransactionInfoDTO announceTransactionInfoDTO = new AnnounceTransactionInfoDTO();
        announceTransactionInfoDTO.setMessage("SomeMessage");
        mockRemoteCall(announceTransactionInfoDTO);

        TransactionAnnounceResponse response = repository.announceAggregateBonded(signedTransaction)
            .toFuture().get();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(announceTransactionInfoDTO.getMessage(),
            announceTransactionInfoDTO.getMessage());
    }

    @Test
    public void announceAggregateBondedCosignature() throws Exception {

        CosignatureSignedTransaction signedTransaction = new CosignatureSignedTransaction(
            "aParentHash", "aSignature", "aSigner");

        AnnounceTransactionInfoDTO announceTransactionInfoDTO = new AnnounceTransactionInfoDTO();
        announceTransactionInfoDTO.setMessage("SomeMessage");
        ArgumentCaptor<Object> parameter = mockRemoteCall(announceTransactionInfoDTO);

        TransactionAnnounceResponse response = repository
            .announceAggregateBondedCosignature(signedTransaction)
            .toFuture().get();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(announceTransactionInfoDTO.getMessage(),
            announceTransactionInfoDTO.getMessage());

        Cosignature cosignature = (Cosignature) parameter.getValue();

        Assertions.assertEquals(signedTransaction.getParentHash(), cosignature.getParentHash());
        Assertions.assertEquals(signedTransaction.getSignature(), cosignature.getSignature());
        Assertions.assertEquals(signedTransaction.getSignerPublicKey(), cosignature.getSignerPublicKey());
    }


    private SignedTransaction getSignedTransaction() {

        String generationHash = "A94B1BE81F1D4C95D6D252AD7BA3FFFB1674991FD880B7A57DC3180AF8D69C32";

        Account account = Account.createFromPrivateKey(
            "063F36659A8BB01D5685826C19E2C2CA9D281465B642BD5E43CB69510408ECF7",
            this.networkType);

        Address recipientAddress =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                recipientAddress,
                Collections
                    .singletonList(createAbsolute(BigInteger.valueOf(1))),
                new PlainMessage("E2ETest:standaloneTransferTransaction:message")
            ).build();

        SignedTransaction signedTransaction = account.sign(transferTransaction, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(444, payload.length());
        return signedTransaction;
    }

    protected Mosaic createAbsolute(BigInteger amount) {
        return new Mosaic(NamespaceId.createFromName("xem.currency"),
            amount);
    }

}
