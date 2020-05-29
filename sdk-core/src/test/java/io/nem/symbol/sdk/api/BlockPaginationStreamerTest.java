package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the BlockPaginationStreamer
 */
public class BlockPaginationStreamerTest {

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


    private PaginationStreamerTester<BlockInfo, BlockSearchCriteria> tester() {
        BlockRepository repository = Mockito.mock(BlockRepository.class);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, BlockInfo.class, repository, new BlockSearchCriteria());
    }

}
