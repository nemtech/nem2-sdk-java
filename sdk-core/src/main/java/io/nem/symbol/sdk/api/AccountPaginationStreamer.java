package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.account.AccountInfo;

/**
 * A helper object that streams {@link AccountInfo} using the search.
 */
public class AccountPaginationStreamer extends PaginationStreamer<AccountInfo, AccountSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the Account repository that will perform the searches
     */
    public AccountPaginationStreamer(Searcher<AccountInfo, AccountSearchCriteria> searcher) {
        super(searcher);
    }
}
