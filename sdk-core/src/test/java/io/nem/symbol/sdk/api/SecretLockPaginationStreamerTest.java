package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the SecretLockPaginationStreamer
 */
public class SecretLockPaginationStreamerTest {

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


    private PaginationStreamerTester<SecretLockInfo, SecretLockSearchCriteria> tester() {
        SecretLockRepository repository = Mockito.mock(SecretLockRepository.class);
        SecretLockPaginationStreamer streamer = new SecretLockPaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, SecretLockInfo.class, repository,
            new SecretLockSearchCriteria(Address.generateRandom(NetworkType.MIJIN_TEST)));
    }

}
