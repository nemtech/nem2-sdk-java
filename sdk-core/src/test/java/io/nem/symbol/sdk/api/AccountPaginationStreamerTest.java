package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.AccountInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the AccountPaginationStreamer
 */
public class AccountPaginationStreamerTest {

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


    private PaginationStreamerTester<AccountInfo, AccountSearchCriteria> tester() {
        AccountRepository repository = Mockito.mock(AccountRepository.class);
        AccountPaginationStreamer streamer = new AccountPaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, AccountInfo.class, repository, new AccountSearchCriteria());
    }

}
