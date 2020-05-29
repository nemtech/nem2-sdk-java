package io.nem.symbol.sdk.api;

import io.reactivex.Observable;

/**
 * Utility helper that stream pages of searches into an Observable.
 *
 * A streamer will help users to walk through searches without knowing the underlying pagination implementation.
 */
public class PaginationStreamer<E, C extends SearchCriteria> {

    /**
     * The search method, likely to be the search method of entity's repository
     */
    private final Searcher<E, C> searcher;

    /**
     * Constructor
     *
     * @param searcher the searcher repository
     */
    public PaginationStreamer(Searcher<E, C> searcher) {
        this.searcher = searcher;
    }

    /**
     * Main method of the helper, it streams the results in observable only loading the pages when necessary.
     *
     * @param criteria the criteria
     * @return the observable of entities.
     */
    public Observable<E> search(C criteria) {
        return this.search(criteria, 1);
    }

    private Observable<E> search(C criteria,
        Integer pageNumber) {
        criteria.pageNumber(pageNumber);
        return Observable.defer(() -> searcher.search(criteria)).flatMap(page -> {
            if (page.isLast()) {
                return Observable.fromIterable(page.getData());
            } else {
                return Observable.fromIterable(page.getData())
                    .concatWith(this.search(criteria, pageNumber + 1));
            }
        });
    }

}
