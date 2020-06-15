package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.reactivex.Observable;
import java.util.List;

/**
 * Transaction interface repository.
 *
 * @since 1.0
 */
public interface TransactionStatusRepository {

    /**
     * Gets a transaction status for a transaction hash.
     *
     * @param transactionHash String
     * @return Observable of {@link TransactionStatus}
     */
    Observable<TransactionStatus> getTransactionStatus(String transactionHash);

    /**
     * Gets an list of transaction status for different transaction hashes.
     *
     * @param transactionHashes List of String
     * @return {@link Observable} of {@link TransactionStatus} List
     */
    Observable<List<TransactionStatus>> getTransactionStatuses(List<String> transactionHashes);
}
