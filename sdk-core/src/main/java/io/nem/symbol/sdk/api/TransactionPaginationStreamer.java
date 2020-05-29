package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.transaction.Transaction;

/**
 * A helper object that streams {@link Transaction} using the search.
 */
public class TransactionPaginationStreamer extends PaginationStreamer<Transaction, TransactionSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the Transaction repository that will perform the searches
     */
    public TransactionPaginationStreamer(Searcher<Transaction, TransactionSearchCriteria> searcher) {
        super(searcher);
    }
}
