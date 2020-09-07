package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.transaction.SecretLockInfo;

/**
 * A helper object that streams {@link SecretLockInfo} using the search.
 */
public class SecretLockPaginationStreamer extends PaginationStreamer<SecretLockInfo, SecretLockSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the SecretLockInfo repository that will perform the searches
     */
    public SecretLockPaginationStreamer(Searcher<SecretLockInfo, SecretLockSearchCriteria> searcher) {
        super(searcher);
    }
}
