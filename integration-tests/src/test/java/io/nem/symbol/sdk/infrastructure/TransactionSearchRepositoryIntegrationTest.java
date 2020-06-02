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

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.api.TransactionPaginationStreamer;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.api.TransactionSearchGroup;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionSearchRepositoryIntegrationTest extends BaseIntegrationTest {

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearch(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(null);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearchSize50(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(50);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearchTransaction(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(null);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearchTransactionPageSize50(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(50);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchOrderByIdAsc(RepositoryType type) {
        getPaginationTester(type).searchOrderByIdAsc();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchOrderByIdDesc(RepositoryType type) {
        getPaginationTester(type).searchOrderByIdDesc();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchUsingOffset(RepositoryType type) {
        TransactionRepository transactionRepository = getTransactionRepository(type);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(transactionRepository);
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        criteria.setPageSize(10);
        int offsetIndex = 2;
        List<Transaction> transactionsWithoutOffset = get(streamer.search(criteria).toList().toObservable());
        criteria.setOffset(transactionsWithoutOffset.get(offsetIndex).getRecordId().get());
        List<Transaction> transactionFromOffsets = get(streamer.search(criteria).toList().toObservable());
        PaginationTester
            .sameEntities(transactionsWithoutOffset.stream().skip(offsetIndex + 1).collect(Collectors.toList()),
                transactionFromOffsets);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchBySignerPublicKey(RepositoryType type) {
        TransactionRepository transactionRepository = getTransactionRepository(type);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(transactionRepository);
        Transaction transaction1 = get(streamer.search(new TransactionSearchCriteria()).take(1));
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        PublicKey expectedSignerPublicKey = transaction1.getSigner().get().getPublicKey();
        criteria.setSignerPublicKey(expectedSignerPublicKey);
        List<Transaction> transactions = get(streamer.search(criteria).toList().toObservable());
        transactions
            .forEach(b -> Assertions.assertEquals(expectedSignerPublicKey, b.getSigner().get().getPublicKey()));
        Assertions.assertFalse(transactions.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchByTransactionType(RepositoryType type) {
        TransactionRepository transactionRepository = getTransactionRepository(type);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(transactionRepository);
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        criteria.transactionTypes(Collections.singletonList(TransactionType.TRANSFER));
        List<Transaction> transactions = get(streamer.search(criteria).toList().toObservable());
        transactions
            .forEach(b -> Assertions.assertEquals(TransactionType.TRANSFER, b.getType()));
        Assertions.assertFalse(transactions.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchByTransactionHeight(RepositoryType type) {
        TransactionRepository transactionRepository = getTransactionRepository(type);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(transactionRepository);
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        criteria.height(BigInteger.ONE);
        criteria.embedded(true);
        List<Transaction> transactions = get(streamer.search(criteria).toList().toObservable());
        transactions
            .forEach(b -> Assertions.assertEquals(BigInteger.ONE, b.getTransactionInfo().get().getHeight()));
        Assertions.assertFalse(transactions.isEmpty());
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchByGroup(RepositoryType type) {
        TransactionRepository transactionRepository = getTransactionRepository(type);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(transactionRepository);
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        criteria.group(TransactionSearchGroup.CONFIRMED);
        List<Transaction> transactions = get(streamer.search(criteria).toList().toObservable());
        transactions
            .forEach(b -> Assertions.assertNotNull(b.getTransactionInfo().get().getHeight()));
        Assertions.assertFalse(transactions.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchBySignerPublicKeyInvalid(RepositoryType type) {
        TransactionRepository transactionRepository = getTransactionRepository(type);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(transactionRepository);
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        PublicKey expectedSignerPublicKey = Account.generateNewAccount(getNetworkType()).getPublicAccount()
            .getPublicKey();
        criteria.setSignerPublicKey(expectedSignerPublicKey);
        List<Transaction> transactions = get(streamer.search(criteria).toList().toObservable());
        Assertions.assertTrue(transactions.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchEmbedded(RepositoryType type) {
        TransactionRepository transactionRepository = getTransactionRepository(type);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(transactionRepository);
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        criteria.setEmbedded(true);
        List<Transaction> transactions = get(streamer.search(criteria).toList().toObservable());
        Assertions.assertFalse(transactions.isEmpty());

        transactions.forEach(t -> {
            Assertions.assertTrue(t.getTransactionInfo().isPresent());
            Assertions.assertTrue(
                t.getTransactionInfo().get().getHash().isPresent() || t.getTransactionInfo().get().getAggregateId()
                    .isPresent());
            Assertions.assertTrue(t.getTransactionInfo().get().getId().isPresent());
            Assertions.assertNotNull(t.getTransactionInfo().get().getHeight());
            Assertions.assertTrue(t.getTransactionInfo().get().getIndex().isPresent());
            if (t.getTransactionInfo().get().getHash().isPresent()) {
                Assertions.assertTrue(t.getTransactionInfo().get().getHash().isPresent());
                Assertions.assertTrue(t.getTransactionInfo().get().getMerkleComponentHash().isPresent());
                Assertions.assertFalse(t.getTransactionInfo().get().getAggregateHash().isPresent());
                Assertions.assertFalse(t.getTransactionInfo().get().getAggregateId().isPresent());
            }

            if (t.getTransactionInfo().get().getAggregateHash().isPresent()) {
                Assertions.assertFalse(t.getTransactionInfo().get().getHash().isPresent());
                Assertions.assertFalse(t.getTransactionInfo().get().getMerkleComponentHash().isPresent());
                Assertions.assertTrue(t.getTransactionInfo().get().getAggregateHash().isPresent());
                Assertions.assertTrue(t.getTransactionInfo().get().getAggregateId().isPresent());
            }
            if (t.getType() == TransactionType.AGGREGATE_BONDED || t.getType() == TransactionType.AGGREGATE_COMPLETE) {
                Assertions.assertThrows(IllegalArgumentException.class, t::getSize);
            } else {
                Assertions.assertTrue(t.getSize() > 0);
            }
        });
    }


    private PaginationTester<Transaction, TransactionSearchCriteria> getPaginationTester(RepositoryType type) {
        return new PaginationTester<>(
            TransactionSearchCriteria::new, getTransactionRepository(type)::search);
    }


    private TransactionRepository getTransactionRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createTransactionRepository();
    }
}
