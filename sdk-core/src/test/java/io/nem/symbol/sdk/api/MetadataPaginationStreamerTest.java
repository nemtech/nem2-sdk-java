package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.metadata.Metadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the MetadataPaginationStreamer
 */
public class MetadataPaginationStreamerTest {

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


    private PaginationStreamerTester<Metadata, MetadataSearchCriteria> tester() {
        MetadataRepository repository = Mockito.mock(MetadataRepository.class);
        MetadataPaginationStreamer streamer = new MetadataPaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, Metadata.class, repository, new MetadataSearchCriteria());
    }

}
