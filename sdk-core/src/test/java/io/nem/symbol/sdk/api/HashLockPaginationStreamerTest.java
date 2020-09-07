package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the HashLockPaginationStreamer
 */
public class HashLockPaginationStreamerTest {

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


    private PaginationStreamerTester<HashLockInfo, HashLockSearchCriteria> tester() {
        HashLockRepository repository = Mockito.mock(HashLockRepository.class);
        HashLockPaginationStreamer streamer = new HashLockPaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, HashLockInfo.class, repository,
            new HashLockSearchCriteria(Address.generateRandom(NetworkType.MIJIN_TEST)));
    }

}
