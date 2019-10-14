/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
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
        Address address = this.config().getTestAccount().getAddress();
        List<AccountInfo> accountInfos =
            get(this.getAccountRepository(type)
                .getAccountsInfo(
                    Collections.singletonList(address)));

        assertEquals(1, accountInfos.size());
        assertEquals(this.getTestAccount().getAddress(), accountInfos.get(0).getAddress());
        assertEquals(AccountType.UNLINKED, accountInfos.get(0).getAccountType());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getAccountsNamesFromAddresses(RepositoryType type) {
        Address accountAddress = this.config().getTestAccount().getAddress();
        List<AccountNames> accountNames = get(
            this.getAccountRepository(type).getAccountsNames(
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
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'SCGEGBEHICF5PPOGIP2JSCQ5OYGZXOOJF7KUSUQJ'",
            exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMultisigAccountInfo(RepositoryType type) {
        MultisigAccountInfo multisigAccountInfo = get(this.getAccountRepository(type)
            .getMultisigAccountInfo(
                config().getMultisigAccount().getAddress())
        );
        assertEquals(
            config().getMultisigAccount().getPublicKey(),
            multisigAccountInfo.getAccount().getPublicKey().toHex());

        Assertions.assertTrue(multisigAccountInfo.isMultisig());
        Assertions.assertEquals(2, multisigAccountInfo.getCosignatories().size());
        Assertions.assertEquals(config().getCosignatory2Account().getAddress(),
            multisigAccountInfo.getCosignatories().get(0).getAddress());

        Assertions.assertEquals(config().getCosignatoryAccount().getAddress(),
            multisigAccountInfo.getCosignatories().get(1).getAddress());

        Assertions.assertEquals(1,
            multisigAccountInfo.getMinApproval());
        Assertions.assertEquals(1,
            multisigAccountInfo.getMinRemoval());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMultisigAccountGraphInfo(RepositoryType type) {
        MultisigAccountGraphInfo multisigAccountGraphInfos = get(this.getAccountRepository(type)
            .getMultisigAccountGraphInfo(
                config().getMultisigAccount().getAddress())
        );

        assertEquals(2,
            multisigAccountGraphInfos.getLevelsNumber().size());

        assertEquals(2,
            multisigAccountGraphInfos.getMultisigAccounts().size());

        assertEquals(1,
            multisigAccountGraphInfos.getMultisigAccounts().get(0).size());

        assertEquals(1,
            multisigAccountGraphInfos.getMultisigAccounts().get(0).size());

        assertEquals(2,
            multisigAccountGraphInfos.getMultisigAccounts().get(1).size());

        assertEquals(config().getMultisigAccount().getAddress(),
            multisigAccountGraphInfos.getMultisigAccounts().get(0).get(0).getAccount()
                .getAddress());

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void transactionsWithPagination(RepositoryType type) {
        Account account = this.config().getTestAccount();
        List<Transaction> transactions = get(
            this.getAccountRepository(type).transactions(account.getPublicAccount()));

        Assertions.assertTrue(transactions.size() > 1);

        System.out.println(transactions.size());
        List<Transaction> nextTransactions =
            get(this.getAccountRepository(type)
                .transactions(
                    account.getPublicAccount(),
                    new QueryParams(transactions.size() - 1,
                        transactions.get(0).getTransactionInfo().get().getId().get())));

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
                .transactions(this.getTestPublicAccount(), new QueryParams(100, null)));
        assertTrue(transactions.size() <= 100);

        transactions.forEach(transaction -> assertTransaction(transaction, false));
    }

    private void assertTransaction(Transaction transaction, boolean outgoingTransactions) {

        Assert.assertNotNull(transaction.getType());
        Assert.assertTrue(transaction.getTransactionInfo().isPresent());
        Assert.assertEquals(getNetworkType(), transaction.getNetworkType());
        if (outgoingTransactions) {
            Assert.assertEquals(getTestAccount().getAddress(),
                transaction.getSigner().get().getAddress());
        }

        Assert.assertTrue(transaction.getSignature().isPresent());
        Assert.assertNotNull(transaction.getMaxFee());
        Assert.assertNotNull(transaction.getVersion());
        Assert.assertNotNull(transaction.getDeadline());

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
                    Address.createFromRawAddress("SAAAAACB67D4HPGIMIHPNSRYRJRT7DOBGWZY"))));
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
