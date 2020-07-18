package io.nem.symbol.sdk.api;

import io.reactivex.Observable;

/**
 *  Objects of this interface know how to search symbol objects based on a criteria returning a page of these objects.
 * @param <E> The entity model type
 * @param <C> The type of the criteria with the search filter
 */
public interface Searcher<E, C extends SearchCriteria<C>> {

    /**
     * It searches entities of a type based on a criteria.
     *
     * @param criteria the criteria
     * @return a page of entities.
     */
    Observable<Page<E>> search(C criteria);

}
