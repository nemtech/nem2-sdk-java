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

package io.nem.sdk.infrastructure.okhttp;

import static io.nem.sdk.infrastructure.okhttp.TestHelperOkHttp.loadTransactionInfoDTO;

import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.TransactionSearchCriteria;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link AccountRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private AccountRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new AccountRepositoryOkHttpImpl(apiClientMock);
    }


    @Test
    public void incomingTransactions() throws Exception {

        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(Collections.singletonList(transferTransactionDTO));

        List<Transaction> transactions = repository.incomingTransactions(publicAccount).toFuture()
            .get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());

        transactions = repository.incomingTransactions(publicAccount)
            .toFuture().get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());
    }

    @Test
    public void transactions() throws Exception {

        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(Collections.singletonList(transferTransactionDTO));

        List<Transaction> transactions = repository.transactions(publicAccount).toFuture()
            .get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());

        transactions = repository.transactions(publicAccount)
            .toFuture().get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());
    }

    @Test
    public void outgoingTransactions() throws Exception {

        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(Collections.singletonList(transferTransactionDTO));

        List<Transaction> transactions = repository.outgoingTransactions(publicAccount).toFuture()
            .get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());

        transactions = repository.outgoingTransactions(publicAccount)
            .toFuture().get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());
    }

    @Test
    public void unconfirmedTransactions() throws Exception {

        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(Collections.singletonList(transferTransactionDTO));

        List<Transaction> transactions = repository.unconfirmedTransactions(publicAccount)
            .toFuture()
            .get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());

        transactions = repository.unconfirmedTransactions(publicAccount)
            .toFuture().get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());
    }

    @Test
    public void aggregateBondedTransactions() throws Exception {

        TransactionInfoDTO aggregateTransferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateAggregateTransferTransaction.json"
        );

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(Collections.singletonList(aggregateTransferTransactionDTO));

        List<AggregateTransaction> transactions = repository
            .aggregateBondedTransactions(publicAccount).toFuture()
            .get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.AGGREGATE_COMPLETE, transactions.get(0).getType());

        transactions = repository
            .aggregateBondedTransactions(publicAccount)
            .toFuture().get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.AGGREGATE_COMPLETE, transactions.get(0).getType());
    }


    @Test
    public void shouldGetAccountInfo() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        mockRemoteCall(accountInfoDTO);

        AccountInfo resolvedAccountInfo = repository.getAccountInfo(address).toFuture().get();
        Assertions.assertEquals(address, resolvedAccountInfo.getAddress());
        Assertions.assertEquals(AccountType.MAIN, resolvedAccountInfo.getAccountType());
    }

    @Test
    public void partialTransactions() throws Exception {

        TransactionInfoDTO transferTransactionDTO = loadTransactionInfoDTO(
            "shouldCreateStandaloneTransferTransaction.json");

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        mockRemoteCall(Collections.singletonList(transferTransactionDTO));

        List<Transaction> transactions = repository.partialTransactions(publicAccount).toFuture()
            .get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());

        transactions = repository
            .incomingTransactions(publicAccount, new TransactionSearchCriteria())
            .toFuture().get();
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(TransactionType.TRANSFER, transactions.get(0).getType());
    }

    @Test
    public void shouldGetAccountsInfoFromAddresses() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        mockRemoteCall(Collections.singletonList(accountInfoDTO));

        List<AccountInfo> resolvedAccountInfos = repository
            .getAccountsInfo(Collections.singletonList(address)).toFuture().get();

        Assertions.assertEquals(1, resolvedAccountInfos.size());

        AccountInfo resolvedAccountInfo = resolvedAccountInfos.get(0);

        Assertions.assertEquals(address, resolvedAccountInfo.getAddress());
        Assertions.assertEquals(AccountType.MAIN, resolvedAccountInfo.getAccountType());
    }


    @Test
    public void shouldProcessExceptionWhenNotFound() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        mockErrorCode(404, "Account not found!");

        Assertions
            .assertEquals("ApiException: Not Found - 404 - Code Not Found - Account not found!",
                Assertions.assertThrows(RepositoryCallException.class, () -> {
                    ExceptionUtils
                        .propagate(() -> repository.getAccountInfo(address).toFuture().get());
                }).getMessage());

    }

    @Test
    public void shouldProcessExceptionWhenNotFoundInvalidResponse() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        mockErrorCodeRawResponse(400, "I'm a raw error, not json");

        Assertions
            .assertEquals("ApiException: Bad Request - 400 - I'm a raw error, not json",
                Assertions.assertThrows(RepositoryCallException.class, () -> {
                    ExceptionUtils
                        .propagate(() -> repository.getAccountInfo(address).toFuture().get());
                }).getMessage());
    }


    @Override
    public AccountRepositoryOkHttpImpl getRepository() {
        return repository;
    }

}
