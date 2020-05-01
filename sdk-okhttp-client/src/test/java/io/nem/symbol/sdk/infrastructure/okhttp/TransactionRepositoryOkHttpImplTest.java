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

package io.nem.symbol.sdk.infrastructure.okhttp;

import static io.nem.symbol.sdk.infrastructure.okhttp.TestHelperOkHttp.loadTransactionInfoDTO;
import static io.nem.symbol.sdk.infrastructure.okhttp.TestHelperOkHttp.loadTransactionInfoExtendedDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.api.TransactionSearchGroup;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AnnounceTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Cosignature;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionGroupEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoExtendedDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatusDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatusEnum;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit Tests for {@link TransactionRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class TransactionRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private TransactionRepositoryOkHttpImpl repository;


    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new TransactionRepositoryOkHttpImpl(apiClientMock);
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

        TransactionInfoExtendedDTO transactionInfoDTO = loadTransactionInfoExtendedDTO(
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
        transactionStatusDTO.setGroup(TransactionGroupEnum.FAILED);
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO
            .setCode(TransactionStatusEnum.FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS);
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
        transactionStatusDTO.setGroup(TransactionGroupEnum.FAILED);
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO
            .setCode(TransactionStatusEnum.FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS);
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

    private SignedTransaction getSignedTransaction() {

        String generationHash = "A94B1BE81F1D4C95D6D252AD7BA3FFFB1674991FD880B7A57DC3180AF8D69C32";

        Account account = Account.createFromPrivateKey(
            "063F36659A8BB01D5685826C19E2C2CA9D281465B642BD5E43CB69510408ECF7", networkType);

        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(NetworkType.MIJIN_TEST,
                address, Collections.singletonList(createAbsolute(BigInteger.valueOf(1))),
                new PlainMessage("E2ETest:standaloneTransferTransaction:message")
            ).build();

        SignedTransaction signedTransaction = account.sign(transferTransaction, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(444, payload.length());
        return signedTransaction;
    }

    protected Mosaic createAbsolute(BigInteger amount) {
        return new Mosaic(NamespaceId.createFromName("cat.currency"),
            amount);
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


    @Test
    public void searchTransactions() throws Exception {

        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(toPage(transferTransactionDTO));

        Page<Transaction> transactions = repository.searchTransactions(
            new TransactionSearchCriteria().signerPublicKey(publicAccount.getPublicKey()).group(
                TransactionSearchGroup.UNCONFIRMED))
            .toFuture()
            .get();
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.getData().get(0).getType());
        Assertions.assertEquals(1, transactions.getData().size());
        Assertions.assertEquals(1, transactions.getPageNumber());
        Assertions.assertEquals(2, transactions.getPageSize());
        Assertions.assertEquals(3, transactions.getTotalEntries());
        Assertions.assertEquals(4, transactions.getTotalPages());

    }

    @Test
    public void searchTransactionsTransactionTypes() throws Exception {

        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(toPage(transferTransactionDTO));

        TransactionSearchCriteria criteria = new TransactionSearchCriteria().transactionTypes(
            Arrays.asList(TransactionType.NAMESPACE_METADATA, TransactionType.AGGREGATE_COMPLETE));

        Page<Transaction> transactions = repository.searchTransactions(
            criteria)
            .toFuture()
            .get();
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.getData().get(0).getType());
        Assertions.assertEquals(1, transactions.getData().size());
        Assertions.assertEquals(1, transactions.getPageNumber());
        Assertions.assertEquals(2, transactions.getPageSize());
        Assertions.assertEquals(3, transactions.getTotalEntries());
        Assertions.assertEquals(4, transactions.getTotalPages());

    }

    private TransactionPage toPage(TransactionInfoDTO dto) {
        return new TransactionPage()
            .data(Collections.singletonList(jsonHelper.parse(jsonHelper.print(dto),
                TransactionInfoExtendedDTO.class)))
            .pagination(new Pagination().pageNumber(1).pageSize(2).totalEntries(3).totalPages(4));
    }


    @Override
    public TransactionRepositoryOkHttpImpl getRepository() {
        return repository;
    }

}
