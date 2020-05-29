package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the MosaicPaginationStreamer
 */
public class MosaicPaginationStreamerTest {

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


    private PaginationStreamerTester<MosaicInfo, MosaicSearchCriteria> tester() {
        MosaicRepository repository = Mockito.mock(MosaicRepository.class);
        MosaicPaginationStreamer streamer = new MosaicPaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, MosaicInfo.class, repository, new MosaicSearchCriteria());
    }

}
