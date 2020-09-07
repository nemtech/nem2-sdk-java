package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.transaction.HashLockInfo;

/**
 * A helper object that streams {@link HashLockInfo} using the search.
 */
public class HashLockPaginationStreamer extends PaginationStreamer<HashLockInfo, HashLockSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the HashLockInfo repository that will perform the searches
     */
    public HashLockPaginationStreamer(Searcher<HashLockInfo, HashLockSearchCriteria> searcher) {
        super(searcher);
    }
}
