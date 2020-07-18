package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;

/**
 * Factory for recipient streamers.
 */
public class ReceiptPaginationStreamer {

    /**
     * It creates a transaction statement streamer of TransactionStatement objects.
     *
     * @param repository the {@link ReceiptRepository} repository
     * @return a new Pagination Streamer.
     */
    public static PaginationStreamer<TransactionStatement, TransactionStatementSearchCriteria> transactions(
        ReceiptRepository repository) {
        return new PaginationStreamer<>(repository::searchReceipts);
    }

    /**
     * It creates a transaction statement streamer of AddressResolutionStatement objects.
     *
     * @param repository the {@link ReceiptRepository} repository
     * @return a new Pagination Streamer.
     */
    public static PaginationStreamer<AddressResolutionStatement, ResolutionStatementSearchCriteria> addresses(
        ReceiptRepository repository) {
        return new PaginationStreamer<>(repository::searchAddressResolutionStatements);
    }

    /**
     * It creates a mosaic resolution statement streamer of MosaicResolutionStatement objects.
     *
     * @param repository the {@link ReceiptRepository} repository
     * @return a new Pagination Streamer.
     */
    public static PaginationStreamer<MosaicResolutionStatement, ResolutionStatementSearchCriteria> mosaics(
        ReceiptRepository repository) {
        return new PaginationStreamer<>(repository::searchMosaicResolutionStatements);
    }

}
