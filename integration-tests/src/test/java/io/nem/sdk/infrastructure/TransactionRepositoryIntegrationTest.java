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

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatus;
import io.nem.sdk.model.transaction.TransactionType;
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

    private String invalidTransactionHash = "AAAAAADBDA00BA39D06B9E67AE5B43162366C862D9B8F656F7E7068D327377BE";

    @BeforeAll
    void setup() {
        AccountRepository accountRepository = getRepositoryFactory(RepositoryType.VERTX)
            .createAccountRepository();
        PublicAccount account = config().getTestAccount().getPublicAccount();
        List<Transaction> transactions = get(
            accountRepository.transactions(account, new QueryParams(100, null)))
            .stream().filter(t -> t.getType() == TransactionType.TRANSFER).collect(
                Collectors.toList());
        Assertions.assertTrue(transactions.size() > 0);
        transactionHash = transactions.get(0).getTransactionInfo().get().getHash().get();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransaction(RepositoryType type) {
        Transaction transaction = get(
            getTransactionRepository(type).getTransaction(transactionHash));

        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(transactionHash, transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransactions(RepositoryType type)
        throws ExecutionException, InterruptedException {
        List<Transaction> transaction =
            getTransactionRepository(type)
                .getTransactions(Collections.singletonList(transactionHash))
                .toFuture()
                .get();

        assertEquals(TransactionType.TRANSFER, transaction.get(0).getType());
        assertEquals(transactionHash,
            transaction.get(0).getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransactionStatus(RepositoryType type) {
        TransactionStatus transactionStatus =
            get(getTransactionRepository(type).getTransactionStatus(transactionHash));

        assertEquals(transactionHash, transactionStatus.getHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransactionsStatuses(RepositoryType type) {

        TransactionRepository transactionRepository = getTransactionRepository(type);

        List<TransactionStatus> transactionStatuses = get(transactionRepository
            .getTransactionStatuses(Collections.singletonList(transactionHash)));

        assertEquals(transactionHash, transactionStatuses.get(0).getHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void throwExceptionWhenTransactionStatusOfATransactionDoesNotExists(
        RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () ->
                get(getTransactionRepository(type)
                    .getTransactionStatus(invalidTransactionHash)));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '"
                + invalidTransactionHash + "'", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void throwExceptionWhenTransactionDoesNotExists(
        RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(getTransactionRepository(type)
                .getTransaction(invalidTransactionHash)));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '"
                + invalidTransactionHash + "'", exception.getMessage());
    }

    private TransactionRepository getTransactionRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createTransactionRepository();
    }
}
