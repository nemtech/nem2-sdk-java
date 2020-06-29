package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.reactivex.Observable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;

public class TestHelper {

    private final Config config;

    public TestHelper() {
        this.config = new Config();
        System.out.println("Running tests against server: " + config().getApiUrl());

    }

    public Config config() {
        return config;
    }

    /**
     * An utility method that executes a rest call though the Observable. It simplifies and unifies the executions of
     * rest calls.
     *
     * This methods adds the necessary timeouts and exception handling,
     *
     * @param observable the observable, typically the one that performs a rest call.
     * @param <T> the observable type
     * @return the response from the rest call.
     */
    public <T> T get(Observable<T> observable) {
        return get(observable.toFuture());
    }

    /**
     * An utility method that executes a rest call though the Observable. It simplifies and unifies the executions of
     * rest calls.
     *
     * This methods adds the necessary timeouts and exception handling,
     *
     * @param future the future, typically the one that performs a rest call.
     * @param <T> the future type
     * @return the response from the rest call.
     */
    public <T> T get(Future<T> future) {
        return ExceptionUtils.propagate(() -> future.get(config.getTimeoutSeconds(), TimeUnit.SECONDS));
    }

    public void assertById(TransactionRepository transactionRepository, TransactionGroup group,
        List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return;
        }

        transactions.forEach(t -> {
            Assertions.assertNotNull(get(transactionRepository.getTransaction(group, t.getRecordId().get())));
        });

        transactions.forEach(t -> {
            Assertions.assertNotNull(
                get(transactionRepository.getTransaction(group, t.getTransactionInfo().get().getHash().get())));
        });

        List<Transaction> transactionsByIds = get(transactionRepository.getTransactions(group,
            transactions.stream().map(t -> t.getRecordId().get()).collect(Collectors.toList())));
        assertSameRecordList(transactionsByIds, transactions);

        List<Transaction> transactionsByHashes = get(transactionRepository.getTransactions(group,
            transactions.stream().map(t -> t.getTransactionInfo().get().getHash().get()).collect(Collectors.toList())));
        assertSameRecordList(transactionsByHashes, transactions);

    }


    public <T extends Stored> void assertSameRecordList(List<T> list1, List<T> list2) {
        Set<String> records1 = list1.stream().map(r -> r.getRecordId().get()).collect(Collectors.toSet());
        Set<String> records2 = list2.stream().map(r -> r.getRecordId().get()).collect(Collectors.toSet());
        Assertions.assertEquals(records1, records2);
    }


}
