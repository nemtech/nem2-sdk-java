package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the TransactionPaginationStreamer
 */
public class TransactionPaginationStreamerTest {

    @Test
    void testMultiplePageStreamer() {
        tester().basicMultiPageTest();
    }

    @Test
    void singlePageTest() {
        tester().basicSinglePageTest();
    }

    @Test
    void multipageWithLimit() {
        tester().multipageWithLimit();
    }


    @Test
    void limitToTwoPages() {
        tester().limitToTwoPages();
    }


    private PaginationStreamerTester<Transaction, TransactionSearchCriteria> tester() {
        TransactionRepository repository = Mockito.mock(TransactionRepository.class);
        TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, Transaction.class, repository, new TransactionSearchCriteria());
    }

}
