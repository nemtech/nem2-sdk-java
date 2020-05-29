package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.mosaic.MosaicInfo;

/**
 * A helper object that streams {@link MosaicInfo} using the search.
 */
public class MosaicPaginationStreamer extends PaginationStreamer<MosaicInfo, MosaicSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the Mosaic repository that will perform the searches
     */
    public MosaicPaginationStreamer(Searcher<MosaicInfo, MosaicSearchCriteria> searcher) {
        super(searcher);
    }
}
