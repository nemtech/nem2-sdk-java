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
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AnnounceTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Cosignature;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionPage;
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

  private final Deadline deadline = new Deadline(BigInteger.ONE);

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new TransactionRepositoryOkHttpImpl(apiClientMock);
  }

  @Test
  public void shouldGetTransaction() throws Exception {

    TransactionInfoDTO transactionInfoDTO =
        loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json", TransactionInfoDTO.class);

    String hash = jsonHelper.getString(transactionInfoDTO, "meta", "hash");
    mockRemoteCall(transactionInfoDTO);

    Transaction transaction =
        repository.getTransaction(TransactionGroup.CONFIRMED, hash).toFuture().get();

    Assertions.assertNotNull(transaction);

    Assertions.assertEquals(hash, transaction.getTransactionInfo().get().getHash().get());
    Assertions.assertEquals(TransactionGroup.CONFIRMED, transaction.getGroup().get());
  }

  @Test
  public void shouldGetTransactionPartial() throws Exception {

    TransactionInfoDTO transactionInfoDTO =
        loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json", TransactionInfoDTO.class);

    String hash = jsonHelper.getString(transactionInfoDTO, "meta", "hash");
    mockRemoteCall(transactionInfoDTO);

    Transaction transaction =
        repository.getTransaction(TransactionGroup.PARTIAL, hash).toFuture().get();

    Assertions.assertNotNull(transaction);

    Assertions.assertEquals(hash, transaction.getTransactionInfo().get().getHash().get());
    Assertions.assertEquals(TransactionGroup.PARTIAL, transaction.getGroup().get());
  }

  @Test
  public void shouldGetTransactionUnconfirmed() throws Exception {

    TransactionInfoDTO transactionInfoDTO =
        loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json", TransactionInfoDTO.class);

    String hash = jsonHelper.getString(transactionInfoDTO, "meta", "hash");
    mockRemoteCall(transactionInfoDTO);

    Transaction transaction =
        repository.getTransaction(TransactionGroup.UNCONFIRMED, hash).toFuture().get();

    Assertions.assertNotNull(transaction);

    Assertions.assertEquals(hash, transaction.getTransactionInfo().get().getHash().get());
    Assertions.assertEquals(TransactionGroup.UNCONFIRMED, transaction.getGroup().get());
  }

  @Test
  public void shouldGetTransactions() throws Exception {

    TransactionInfoDTO transactionInfoDTO =
        loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json", TransactionInfoDTO.class);
    String hash = jsonHelper.getString(transactionInfoDTO, "meta", "hash");
    mockRemoteCall(Collections.singletonList(transactionInfoDTO));
    Transaction transaction =
        repository
            .getTransactions(TransactionGroup.CONFIRMED, Collections.singletonList(hash))
            .toFuture()
            .get()
            .get(0);
    Assertions.assertNotNull(transaction);
    Assertions.assertEquals(hash, transaction.getTransactionInfo().get().getHash().get());
    Assertions.assertEquals(TransactionGroup.CONFIRMED, transaction.getGroup().get());
  }

  @Test
  public void shouldAnnounce() throws Exception {
    SignedTransaction signedTransaction = getSignedTransaction();
    AnnounceTransactionInfoDTO announceTransactionInfoDTO = new AnnounceTransactionInfoDTO();
    announceTransactionInfoDTO.setMessage("SomeMessage");
    mockRemoteCall(announceTransactionInfoDTO);
    TransactionAnnounceResponse response = repository.announce(signedTransaction).toFuture().get();
    Assertions.assertNotNull(response);
    Assertions.assertEquals(
        announceTransactionInfoDTO.getMessage(), announceTransactionInfoDTO.getMessage());
  }

  private SignedTransaction getSignedTransaction() {

    String generationHash = "A94B1BE81F1D4C95D6D252AD7BA3FFFB1674991FD880B7A57DC3180AF8D69C32";

    Account account =
        Account.createFromPrivateKey(
            "063F36659A8BB01D5685826C19E2C2CA9D281465B642BD5E43CB69510408ECF7", networkType);

    Address address = Address.generateRandom(networkType);

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                deadline,
                address,
                Collections.singletonList(createAbsolute(BigInteger.valueOf(1))))
            .message(new PlainMessage("E2ETest:standaloneTransferTransaction:message"))
            .build();

    SignedTransaction signedTransaction = account.sign(transferTransaction, generationHash);
    String payload = signedTransaction.getPayload();
    assertEquals(444, payload.length());
    return signedTransaction;
  }

  protected Mosaic createAbsolute(BigInteger amount) {
    return new Mosaic(NamespaceId.createFromName("cat.currency"), amount);
  }

  @Test
  public void shouldAnnounceAggregateBonded() throws Exception {

    SignedTransaction signedTransaction = getSignedTransaction();

    AnnounceTransactionInfoDTO announceTransactionInfoDTO = new AnnounceTransactionInfoDTO();
    announceTransactionInfoDTO.setMessage("SomeMessage");
    mockRemoteCall(announceTransactionInfoDTO);

    TransactionAnnounceResponse response =
        repository.announceAggregateBonded(signedTransaction).toFuture().get();

    Assertions.assertNotNull(response);

    Assertions.assertEquals(
        announceTransactionInfoDTO.getMessage(), announceTransactionInfoDTO.getMessage());
  }

  @Test
  public void announceAggregateBondedCosignature() throws Exception {

    Account signer = Account.generateNewAccount(networkType);
    BigInteger version = AggregateTransactionCosignature.DEFAULT_VERSION;
    CosignatureSignedTransaction signedTransaction =
        new CosignatureSignedTransaction(
            version, "aParentHash", "aSignature", signer.getPublicAccount());

    AnnounceTransactionInfoDTO announceTransactionInfoDTO = new AnnounceTransactionInfoDTO();
    announceTransactionInfoDTO.setMessage("SomeMessage");
    ArgumentCaptor<Object> parameter = mockRemoteCall(announceTransactionInfoDTO);

    TransactionAnnounceResponse response =
        repository.announceAggregateBondedCosignature(signedTransaction).toFuture().get();

    Assertions.assertNotNull(response);

    Assertions.assertEquals(
        announceTransactionInfoDTO.getMessage(), announceTransactionInfoDTO.getMessage());

    Cosignature cosignature = (Cosignature) parameter.getValue();

    Assertions.assertEquals(signedTransaction.getParentHash(), cosignature.getParentHash());
    Assertions.assertEquals(signedTransaction.getSignature(), cosignature.getSignature());
    Assertions.assertEquals(
        signedTransaction.getSigner().getPublicKey().toHex(), cosignature.getSignerPublicKey());
  }

  @Test
  public void searchTransactions() throws Exception {

    BigInteger version = AggregateTransactionCosignature.DEFAULT_VERSION;
    TransactionInfoDTO transferTransactionDTO =
        loadTransactionInfoDTO("standaloneTransferTransaction.json", TransactionInfoDTO.class);

    PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

    mockRemoteCall(toPage(transferTransactionDTO));

    Page<Transaction> transactions =
        repository
            .search(
                new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED)
                    .signerPublicKey(publicAccount.getPublicKey()))
            .toFuture()
            .get();
    Assertions.assertEquals(TransactionType.TRANSFER, transactions.getData().get(0).getType());
    Assertions.assertEquals(
        TransactionGroup.UNCONFIRMED, transactions.getData().get(0).getGroup().get());
    Assertions.assertEquals(1, transactions.getData().size());
    Assertions.assertEquals(1, transactions.getPageNumber());
    Assertions.assertEquals(2, transactions.getPageSize());
  }

  @Test
  public void searchTransactionsTransactionTypes() throws Exception {

    TransactionInfoDTO transferTransactionDTO =
        loadTransactionInfoDTO("standaloneTransferTransaction.json", TransactionInfoDTO.class);

    PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

    mockRemoteCall(toPage(transferTransactionDTO));

    TransactionSearchCriteria criteria =
        new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
            .transactionTypes(
                Arrays.asList(
                    TransactionType.NAMESPACE_METADATA, TransactionType.AGGREGATE_COMPLETE));

    Page<Transaction> transactions =
        repository.search(criteria.address(publicAccount.getAddress())).toFuture().get();
    Assertions.assertEquals(TransactionType.TRANSFER, transactions.getData().get(0).getType());
    Assertions.assertEquals(
        TransactionGroup.CONFIRMED, transactions.getData().get(0).getGroup().get());
    Assertions.assertEquals(1, transactions.getData().size());
    Assertions.assertEquals(1, transactions.getPageNumber());
    Assertions.assertEquals(2, transactions.getPageSize());
  }

  @Test
  public void searchTransactionsTransactionTypesPartial() throws Exception {

    TransactionInfoDTO transferTransactionDTO =
        loadTransactionInfoDTO("standaloneTransferTransaction.json", TransactionInfoDTO.class);

    PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

    mockRemoteCall(toPage(transferTransactionDTO));

    TransactionSearchCriteria criteria =
        new TransactionSearchCriteria(TransactionGroup.PARTIAL)
            .transactionTypes(
                Arrays.asList(
                    TransactionType.NAMESPACE_METADATA, TransactionType.AGGREGATE_COMPLETE));

    Page<Transaction> transactions =
        repository.search(criteria.address(publicAccount.getAddress())).toFuture().get();
    Assertions.assertEquals(TransactionType.TRANSFER, transactions.getData().get(0).getType());
    Assertions.assertEquals(
        TransactionGroup.PARTIAL, transactions.getData().get(0).getGroup().get());
    Assertions.assertEquals(1, transactions.getData().size());
    Assertions.assertEquals(1, transactions.getPageNumber());
    Assertions.assertEquals(2, transactions.getPageSize());
  }

  private TransactionPage toPage(TransactionInfoDTO dto) {
    return new TransactionPage()
        .data(
            Collections.singletonList(
                jsonHelper.parse(jsonHelper.print(dto), TransactionInfoDTO.class)))
        .pagination(new Pagination().pageNumber(1).pageSize(2));
  }

  @Override
  public TransactionRepositoryOkHttpImpl getRepository() {
    return repository;
  }
}
