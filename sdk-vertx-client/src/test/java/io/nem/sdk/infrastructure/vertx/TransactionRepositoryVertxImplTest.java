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

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionStatus;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import io.nem.sdk.openapi.vertx.model.AnnounceTransactionInfoDTO;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.sdk.openapi.vertx.model.TransactionStatusDTO;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        repository = new TransactionRepositoryVertxImpl(apiClientMock, networkType);
    }

    @Test
    public void shouldGetTransaction() throws Exception {

        TransactionInfoDTO transactionInfoDTO = loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        mockRemoteCall(transactionInfoDTO);

        Transaction transaction = repository
            .getTransaction(transactionInfoDTO.getMeta().getHash()).toFuture().get();

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionInfoDTO.getMeta().getHash(),
            transaction.getTransactionInfo().get().getHash().get());
    }

    @Test
    public void shouldGetTransactions() throws Exception {

        TransactionInfoDTO transactionInfoDTO = loadTransactionInfoDTO(
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
        transactionStatusDTO.setGroup("someGorup");
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO.setStatus("SomeStatus");
        transactionStatusDTO.setHash("someHash");
        mockRemoteCall(transactionStatusDTO);

        TransactionStatus transaction = repository
            .getTransactionStatus(transactionStatusDTO.getHash()).toFuture().get();

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionStatusDTO.getHash(), transaction.getHash());
        Assertions.assertEquals(5L, transaction.getDeadline().getInstant());
        Assertions.assertEquals(BigInteger.valueOf(6L), transaction.getHeight());
        Assertions.assertEquals(transactionStatusDTO.getStatus(), transactionStatusDTO.getStatus());
    }

    @Test
    public void shouldGetTransactionStatuses() throws Exception {

        TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
        transactionStatusDTO.setGroup("someGorup");
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO.setStatus("SomeStatus");
        transactionStatusDTO.setHash("someHash");
        mockRemoteCall(Collections.singletonList(transactionStatusDTO));

        TransactionStatus transaction = repository
            .getTransactionStatuses(Collections.singletonList(transactionStatusDTO.getHash()))
            .toFuture().get().get(0);

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionStatusDTO.getHash(), transaction.getHash());
        Assertions.assertEquals(5L, transaction.getDeadline().getInstant());
        Assertions.assertEquals(BigInteger.valueOf(6L), transaction.getHeight());
        Assertions.assertEquals(transactionStatusDTO.getStatus(), transactionStatusDTO.getStatus());
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

    private SignedTransaction getSignedTransaction() {

        String generationHash = "A94B1BE81F1D4C95D6D252AD7BA3FFFB1674991FD880B7A57DC3180AF8D69C32";

        Account account = Account.createFromPrivateKey(
            "063F36659A8BB01D5685826C19E2C2CA9D281465B642BD5E43CB69510408ECF7",
            this.networkType.get());

        Address recipientAddress =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                recipientAddress,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                new PlainMessage("E2ETest:standaloneTransferTransaction:message")
            ).build();

        SignedTransaction signedTransaction = account.sign(transferTransaction, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(444, payload.length());
        return signedTransaction;
    }


}
