package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.metadata.Metadata;

/**
 * A helper object that streams {@link Metadata} objects using the search.
 */
public class MetadataPaginationStreamer extends PaginationStreamer<Metadata, MetadataSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the Account repository that will perform the searches
     */
    public MetadataPaginationStreamer(Searcher<Metadata, MetadataSearchCriteria> searcher) {
        super(searcher);
    }
}
