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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountNames;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.reactivex.Observable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// TODO BROKEN!
class AccountRepositoryIntegrationTest extends BaseIntegrationTest {

  public AccountRepository getAccountRepository(RepositoryType type) {
    return getRepositoryFactory(type).createAccountRepository();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getAccountInfo(RepositoryType type) {
    Account testAccount = this.config().getDefaultAccount();
    Observable<AccountInfo> accountInfo1 =
        this.getAccountRepository(type).getAccountInfo(testAccount.getPublicAccount().getAddress());
    AccountInfo accountInfo = get(accountInfo1);

    assertEquals(testAccount.getPublicKey(), accountInfo.getPublicKey());
    assertEquals(AccountType.UNLINKED, accountInfo.getAccountType());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getAccountsInfoFromAddresses(RepositoryType type) {
    Account account = config().getDefaultAccount();
    Address address = account.getAddress();
    List<AccountInfo> accountInfos =
        get(this.getAccountRepository(type).getAccountsInfo(Collections.singletonList(address)));

    assertEquals(1, accountInfos.size());
    assertEquals(account.getAddress(), accountInfos.get(0).getAddress());
    assertEquals(AccountType.UNLINKED, accountInfos.get(0).getAccountType());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getAccountsNamesFromAddresses(RepositoryType type) {
    Address accountAddress = config().getTestAccount().getAddress();
    List<AccountNames> accountNames =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getAccountsNames(Collections.singletonList(accountAddress)));

    System.out.println(jsonHelper().print(accountNames));
    assertEquals(1, accountNames.size());
    assertEquals(accountAddress, accountNames.get(0).getAddress());
    assertNotNull(accountNames.get(0).getNames());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getAccountInfoNotExisting(RepositoryType type) {
    AccountRepository accountHttp = getRepositoryFactory(type).createAccountRepository();
    Address addressObject =
        Address.createFromPublicKey(
            "67F69FA4BFCD158F6E1AF1ABC82F725F5C5C4710D6E29217B12BE66397435DFB", getNetworkType());

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class, () -> get(accountHttp.getAccountInfo(addressObject)));
    Assertions.assertTrue(
        exception
            .getMessage()
            .contains(
                "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id"));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMultipleTransactions(RepositoryType type) {
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();
    Account account = config().getDefaultAccount();
    List<TransactionType> transactionTypes =
        Arrays.asList(
            TransactionType.TRANSFER,
            TransactionType.AGGREGATE_COMPLETE,
            TransactionType.NAMESPACE_METADATA);
    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .signerPublicKey(account.getPublicAccount().getPublicKey())
                    .transactionTypes(transactionTypes)))
            .getData();
    Assertions.assertFalse(transactions.isEmpty());

    transactions.forEach(t -> Assertions.assertTrue(transactionTypes.contains(t.getType())));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getTransactionById(RepositoryType type) {
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();
    Account account = config().getDefaultAccount();
    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .signerPublicKey(account.getPublicAccount().getPublicKey())
                    .pageSize(10)))
            .getData();
    Assertions.assertTrue(transactions.size() > 0);

    String id = transactions.get(1).getTransactionInfo().get().getId().get();
    List<Transaction> transactions2 =
        get(transactionRepository.getTransactions(TransactionGroup.CONFIRMED, Arrays.asList(id)));

    Assertions.assertEquals(1, transactions2.size());
    transactions2.forEach(
        t -> Assertions.assertEquals(id, t.getTransactionInfo().get().getId().get()));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void outgoingTransactionsById(RepositoryType type) {
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();
    Account account = config().getDefaultAccount();

    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .signerPublicKey(account.getPublicAccount().getPublicKey())
                    .pageSize(10)))
            .getData();

    Assertions.assertTrue(transactions.size() > 1);

    String id = transactions.get(1).getTransactionInfo().get().getId().get();
    List<Transaction> transactions2 =
        get(transactionRepository.getTransactions(TransactionGroup.CONFIRMED, Arrays.asList(id)));

    Assertions.assertEquals(1, transactions2.size());
    transactions2.forEach(
        t -> Assertions.assertEquals(id, t.getTransactionInfo().get().getId().get()));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMosaicGlobalRegistration(RepositoryType type) {
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();
    Account account = config().getDefaultAccount();
    TransactionType transactionType = TransactionType.MOSAIC_GLOBAL_RESTRICTION;
    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .transactionTypes(Collections.singletonList(transactionType))
                    .signerPublicKey(account.getPublicAccount().getPublicKey())
                    .pageSize(10)))
            .getData();

    System.out.println(transactions.size());
    Assertions.assertFalse(transactions.isEmpty());

    transactions.forEach(t -> Assertions.assertEquals(transactionType, t.getType()));
  }

  private void assertTransaction(Transaction transaction, Address signer) {

    Assertions.assertNotNull(transaction.getType());
    Assertions.assertTrue(transaction.getTransactionInfo().isPresent());
    Assertions.assertEquals(getNetworkType(), transaction.getNetworkType());
    if (signer != null) {
      Assertions.assertEquals(signer, transaction.getSigner().get().getAddress());
    }

    Assertions.assertTrue(transaction.getSignature().isPresent());
    Assertions.assertNotNull(transaction.getMaxFee());
    Assertions.assertNotNull(transaction.getVersion());
    Assertions.assertNotNull(transaction.getDeadline());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void outgoingTransactions(RepositoryType type) {
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();
    PublicAccount recipient = this.helper().getTestAccount(type).getLeft().getPublicAccount();
    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .transactionTypes(Collections.singletonList(TransactionType.TRANSFER))
                    .recipientAddress(recipient.getAddress())))
            .getData();
    System.out.println(transactions.size());
    transactions.forEach(
        transaction -> {
          assertTransaction(transaction, null);

          TransferTransaction transferTransaction = (TransferTransaction) transaction;
          Assertions.assertEquals(recipient.getAddress(), transferTransaction.getRecipient());
        });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void unconfirmedTransactions(RepositoryType type) {
    Account testAccount = this.config().getDefaultAccount();
    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();
    PublicAccount publicAccount = this.helper().getTestAccount(type).getLeft().getPublicAccount();
    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED)
                    .signerPublicKey(publicAccount.getPublicKey())))
            .getData();
    System.out.println(transactions.size());
    transactions.forEach(transaction -> assertTransaction(transaction, testAccount.getAddress()));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void throwExceptionWhenAddressDoesNotExist(RepositoryType type) {
    Address address = Address.generateRandom(getNetworkType());
    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () -> get(this.getAccountRepository(type).getAccountInfo(address)));
    Assertions.assertEquals(
        "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '"
            + address.plain()
            + "'",
        exception.getMessage());
  }
}
