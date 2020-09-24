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
package io.nem.symbol.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.api.TransactionStatusRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionRepositoryIntegrationTest extends BaseIntegrationTest {

  private String transactionHash;

  private final String invalidTransactionHash =
      "AAAAAADBDA00BA39D06B9E67AE5B43162366C862D9B8F656F7E7068D327377BE";

  @BeforeAll
  void setup() {
    RepositoryType type = RepositoryType.VERTX;
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();

    Address recipient = getRecipient();

    String message = "someMessage";
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                recipient,
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))))
            .message(new PlainMessage(message))
            .maxFee(maxFee)
            .build();

    TransferTransaction processed =
        announceAndValidate(type, config().getDefaultAccount(), transferTransaction);

    Assertions.assertEquals(message, processed.getMessage().get().getText());

    PublicAccount account = config().getDefaultAccount().getPublicAccount();
    List<Transaction> allTransactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .order(OrderBy.DESC)
                    .signerPublicKey(account.getPublicKey())))
            .getData();
    List<Transaction> transactions =
        allTransactions.stream()
            .filter(t -> t.getType() == TransactionType.TRANSFER)
            .collect(Collectors.toList());
    Assertions.assertTrue(allTransactions.size() > 0);
    Assertions.assertTrue(transactions.size() > 0);
    transactionHash = transactions.get(0).getTransactionInfo().get().getHash().get();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void getTransaction(RepositoryType type) {
    Transaction transaction =
        get(
            getTransactionRepository(type)
                .getTransaction(TransactionGroup.CONFIRMED, transactionHash));

    assertEquals(TransactionType.TRANSFER, transaction.getType());
    assertEquals(transactionHash, transaction.getTransactionInfo().get().getHash().get());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void getTransactions(RepositoryType type) throws ExecutionException, InterruptedException {
    List<Transaction> transaction =
        getTransactionRepository(type)
            .getTransactions(TransactionGroup.CONFIRMED, Collections.singletonList(transactionHash))
            .toFuture()
            .get();

    assertEquals(TransactionType.TRANSFER, transaction.get(0).getType());
    assertEquals(transactionHash, transaction.get(0).getTransactionInfo().get().getHash().get());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void getTransactionStatus(RepositoryType type) {
    TransactionStatus transactionStatus =
        get(getTransactionStatusRepository(type).getTransactionStatus(transactionHash));

    assertEquals(transactionHash, transactionStatus.getHash());
    assertNotNull(transactionStatus.getCode());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void getTransactionsStatuses(RepositoryType type) {

    TransactionStatusRepository transactionRepository = getTransactionStatusRepository(type);

    List<TransactionStatus> transactionStatuses =
        get(
            transactionRepository.getTransactionStatuses(
                Collections.singletonList(transactionHash)));

    assertEquals(transactionHash, transactionStatuses.get(0).getHash());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void throwExceptionWhenTransactionStatusOfATransactionDoesNotExists(RepositoryType type) {
    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                get(
                    getTransactionStatusRepository(type)
                        .getTransactionStatus(invalidTransactionHash)));
    Assertions.assertEquals(
        "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '"
            + invalidTransactionHash
            + "'",
        exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void throwExceptionWhenTransactionDoesNotExists(RepositoryType type) {
    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                get(
                    getTransactionRepository(type)
                        .getTransaction(TransactionGroup.CONFIRMED, invalidTransactionHash)));
    Assertions.assertEquals(
        "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '"
            + invalidTransactionHash
            + "'",
        exception.getMessage());
  }

  private TransactionRepository getTransactionRepository(RepositoryType type) {
    return getRepositoryFactory(type).createTransactionRepository();
  }

  private TransactionStatusRepository getTransactionStatusRepository(RepositoryType type) {
    return getRepositoryFactory(type).createTransactionStatusRepository();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getBlockTransactions(RepositoryType type) {
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();

    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .height(BigInteger.ONE)
                    .pageNumber(1)))
            .getData();

    assertEquals(20, transactions.size());

    List<Transaction> nextTransactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .height(BigInteger.ONE)
                    .pageNumber(2)))
            .getData();
    assertTrue(nextTransactions.size() > 0);
    assertNotEquals(
        transactions.get(1).getTransactionInfo().get().getHash(),
        nextTransactions.get(0).getTransactionInfo().get().getHash());
  }
}
