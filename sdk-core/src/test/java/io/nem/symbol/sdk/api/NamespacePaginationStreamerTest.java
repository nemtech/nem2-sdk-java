package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of the NamespacePaginationStreamer
 */
public class NamespacePaginationStreamerTest {

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


    private PaginationStreamerTester<NamespaceInfo, NamespaceSearchCriteria> tester() {
        NamespaceRepository repository = Mockito.mock(NamespaceRepository.class);
        NamespacePaginationStreamer streamer = new NamespacePaginationStreamer(repository);
        return new PaginationStreamerTester<>(streamer, NamespaceInfo.class, repository, new NamespaceSearchCriteria());
    }

}
