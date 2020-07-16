package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.namespace.NamespaceInfo;

/**
 * A helper object that streams {@link NamespaceInfo} using the search.
 */
public class NamespacePaginationStreamer extends PaginationStreamer<NamespaceInfo, NamespaceSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the Namespace repository that will perform the searches
     */
    public NamespacePaginationStreamer(Searcher<NamespaceInfo, NamespaceSearchCriteria> searcher) {
        super(searcher);
    }
}
