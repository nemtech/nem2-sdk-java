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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountNames;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//TODO BROKEN!
class AccountRepositoryIntegrationTest extends BaseIntegrationTest {


    public AccountRepository getAccountRepository(RepositoryType type) {
        return getRepositoryFactory(type).createAccountRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getAccountInfo(RepositoryType type) {
        AccountInfo accountInfo =
            get(this.getAccountRepository(type)
                .getAccountInfo(this.getTestAccount().getPublicAccount().getAddress()));

        assertEquals(this.getTestAccount().getPublicKey(), accountInfo.getPublicKey());
        assertEquals(AccountType.UNLINKED, accountInfo.getAccountType());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getAccountsInfoFromAddresses(RepositoryType type) {
        Account account = this.config().getDefaultAccount();
        Address address = account.getAddress();
        List<AccountInfo> accountInfos =
            get(this.getAccountRepository(type)
                .getAccountsInfo(
                    Collections.singletonList(address)));

        assertEquals(1, accountInfos.size());
        assertEquals(account.getAddress(), accountInfos.get(0).getAddress());
        assertEquals(AccountType.UNLINKED, accountInfos.get(0).getAccountType());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getAccountsNamesFromAddresses(RepositoryType type) {
        Address accountAddress = this.config().getTestAccount().getAddress();
        List<AccountNames> accountNames = get(
            this.getRepositoryFactory(type).createNamespaceRepository().getAccountsNames(
                Collections.singletonList(accountAddress)));

        System.out.println(jsonHelper().print(accountNames));
        assertEquals(1, accountNames.size());
        assertEquals(accountAddress,
            accountNames.get(0).getAddress());
        assertNotNull(accountNames.get(0).getNames());
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getAccountInfoNotExisting(RepositoryType type) {
        AccountRepository accountHttp = getRepositoryFactory(type).createAccountRepository();
        Address addressObject = Address
            .createFromPublicKey("67F69FA4BFCD158F6E1AF1ABC82F725F5C5C4710D6E29217B12BE66397435DFB",
                getNetworkType());

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class,
                () -> get(accountHttp.getAccountInfo(addressObject)));
        Assertions.assertTrue(
            exception.getMessage().contains(
                "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id"));
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getTransferTransactions(RepositoryType type) {
        AccountRepository accountHttp = getRepositoryFactory(type).createAccountRepository();
        Account account = config().getDefaultAccount();
        TransactionType transactionType = TransactionType.TRANSFER;
        List<Transaction> transactions = get(accountHttp.transactions(account.getPublicAccount(),
            new TransactionSearchCriteria().transactionTypes(
                Collections.singletonList(transactionType))));
        Assertions.assertFalse(transactions.isEmpty());

        transactions.forEach(t -> Assertions.assertEquals(transactionType, t.getType()));
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getTransactionById(RepositoryType type) {
        AccountRepository accountHttp = getRepositoryFactory(type).createAccountRepository();
        Account account = config().getDefaultAccount();
        List<Transaction> transactions = get(accountHttp.transactions(account.getPublicAccount(),
            new TransactionSearchCriteria().pageSize(10).order("id")));
        Assertions.assertTrue(transactions.size() > 0);

        String lastOne = transactions.get(0).getTransactionInfo().get().getId().get();
        String id = transactions.get(1).getTransactionInfo().get().getId().get();
        List<Transaction> transactions2 = get(accountHttp.transactions(account.getPublicAccount(),
            new TransactionSearchCriteria()
                .id(id)));

        Assertions.assertEquals(1, transactions2.size());
        transactions2
            .forEach(
                t -> Assertions.assertEquals(lastOne, t.getTransactionInfo().get().getId().get()));
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void outgoingTransactionsById(RepositoryType type) {
        AccountRepository accountHttp = getRepositoryFactory(type).createAccountRepository();
        Account account = config().getDefaultAccount();
        List<Transaction> transactions = get(
            accountHttp.outgoingTransactions(account.getPublicAccount(),
                new TransactionSearchCriteria().pageSize(10).order("id")));
        Assertions.assertTrue(transactions.size() > 1);

        String lastOne = transactions.get(0).getTransactionInfo().get().getId().get();
        String id = transactions.get(1).getTransactionInfo().get().getId().get();
        List<Transaction> transactions2 = get(
            accountHttp.outgoingTransactions(account.getPublicAccount(),
                new TransactionSearchCriteria()
                    .id(id)));

        Assertions.assertEquals(1, transactions2.size());
        transactions2
            .forEach(
                t -> Assertions.assertEquals(lastOne, t.getTransactionInfo().get().getId().get()));
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicGlobalRegistration(RepositoryType type) {
        AccountRepository accountHttp = getRepositoryFactory(type).createAccountRepository();
        Account account = config().getDefaultAccount();
        TransactionType transactionType = TransactionType.MOSAIC_GLOBAL_RESTRICTION;
        List<Transaction> transactions = get(accountHttp.transactions(account.getPublicAccount(),
            new TransactionSearchCriteria()
                .transactionTypes(Collections.singletonList(transactionType))));
        System.out.println(transactions.size());
        Assertions.assertFalse(transactions.isEmpty());

        transactions.forEach(t -> Assertions.assertEquals(transactionType, t.getType()));
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void transactionsWithPagination(RepositoryType type) {
        Account account = this.config().getDefaultAccount();
        List<Transaction> transactions = get(
            this.getAccountRepository(type).transactions(account.getPublicAccount()));

        Assertions.assertTrue(transactions.size() > 0);

        System.out.println(transactions.size());
        List<Transaction> nextTransactions =
            get(this.getAccountRepository(type)
                .transactions(
                    account.getPublicAccount(),
                    new TransactionSearchCriteria()
                        .id(transactions.get(0).getTransactionInfo().get().getId().get())));

        System.out.println(nextTransactions.size());
        assertEquals(
            transactions.get(1).getTransactionInfo().get().getHash(),
            nextTransactions.get(0).getTransactionInfo().get().getHash());

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void transactionsWithPaginationManyTransactions(RepositoryType type) {
        //Testing that many transaction can be at at least parsed.
        List<Transaction> transactions =
            get(this.getAccountRepository(type)
                .transactions(this.getTestPublicAccount(),
                    new TransactionSearchCriteria().pageSize(100)));
        assertTrue(transactions.size() <= 100);

        transactions.forEach(transaction -> assertTransaction(transaction, false));
    }

    private void assertTransaction(Transaction transaction, boolean outgoingTransactions) {

        Assertions.assertNotNull(transaction.getType());
        Assertions.assertTrue(transaction.getTransactionInfo().isPresent());
        Assertions.assertEquals(getNetworkType(), transaction.getNetworkType());
        if (outgoingTransactions) {
            Assertions.assertEquals(getTestAccount().getAddress(),
                transaction.getSigner().get().getAddress());
        }

        Assertions.assertTrue(transaction.getSignature().isPresent());
        Assertions.assertNotNull(transaction.getMaxFee());
        Assertions.assertNotNull(transaction.getVersion());
        Assertions.assertNotNull(transaction.getDeadline());

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void incomingTransactions(RepositoryType type) {
        List<Transaction> transactions = get(
            this.getAccountRepository(type).incomingTransactions(this.getTestPublicAccount()));

        transactions.forEach(transaction -> assertTransaction(transaction, false));
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void outgoingTransactions(RepositoryType type) {
        List<Transaction> transactions = get(
            this.getAccountRepository(type).outgoingTransactions(this.getTestPublicAccount()));
        System.out.println(transactions.size());
        transactions.forEach(transaction -> assertTransaction(transaction, true));
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateBondedTransactions(RepositoryType type) {
        List<AggregateTransaction> transactions = get(this.getAccountRepository(type)
            .aggregateBondedTransactions(this.getTestPublicAccount())
        );

        assertEquals(0, transactions.size());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void unconfirmedTransactions(RepositoryType type) {
        List<Transaction> transactions = get(this.getAccountRepository(type)
            .unconfirmedTransactions(this.getTestPublicAccount()));
        assertEquals(0, transactions.size());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenBlockDoesNotExists(RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(this.getAccountRepository(type)
                .getAccountInfo(
                    Address.createFromRawAddress("SCJFR55L7KWHERD2VW6C3NR2MBZLVDQWDHCH1111"))));
        Assertions.assertEquals(
            "ApiException: Conflict - 409 - InvalidArgument - accountId has an invalid format",
            exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenInvalidFormat(RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(this.getAccountRepository(type)
                .getAccountInfo(
                    Address.createFromRawAddress("SARDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY"))));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'SARDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY'",
            exception.getMessage());
    }


}
