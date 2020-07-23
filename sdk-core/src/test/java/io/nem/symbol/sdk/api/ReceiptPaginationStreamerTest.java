package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the ReceiptPaginationStreamer
 */
public class ReceiptPaginationStreamerTest {

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


    private PaginationStreamerTester<TransactionStatement, TransactionStatementSearchCriteria> tester() {
        Searcher<TransactionStatement, TransactionStatementSearchCriteria> searcher = Mockito.mock(Searcher.class);
        ReceiptRepository repository = Mockito.mock(ReceiptRepository.class);
        Mockito.when(repository.searchReceipts(Mockito.any()))
            .thenAnswer(m -> searcher.search((TransactionStatementSearchCriteria) m.getArguments()[0]));
        PaginationStreamer<TransactionStatement, TransactionStatementSearchCriteria> streamer = ReceiptPaginationStreamer
            .transactions(repository);
        return new PaginationStreamerTester<>(streamer, TransactionStatement.class, searcher,
            new TransactionStatementSearchCriteria());
    }

}
