package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.blockchain.BlockInfo;

/**
 * A helper object that streams {@link BlockInfo} using the search.
 */
public class BlockPaginationStreamer extends PaginationStreamer<BlockInfo, BlockSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the block repository that will perform the searches
     */
    public BlockPaginationStreamer(Searcher<BlockInfo, BlockSearchCriteria>  searcher) {
        super(searcher);
    }
}
