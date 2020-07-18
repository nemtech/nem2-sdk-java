package io.nem.symbol.sdk.api;


import org.bouncycastle.asn1.cms.MetaData;

/**
 * A helper object that streams {@link MetaData} objects using the search.
 */
public class MetadataPaginationStreamer extends PaginationStreamer<MetaData, MetadataSearchCriteria> {

    /**
     * Constructor
     *
     * @param searcher the Account repository that will perform the searches
     */
    public MetadataPaginationStreamer(Searcher<MetaData, MetadataSearchCriteria> searcher) {
        super(searcher);
    }
}
