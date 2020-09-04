package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.reactivex.Observable;

/**
 * Repository used to retrieves lock hashes.
 */
public interface LockHashRepository extends Searcher<HashLockInfo, HashLockSearchCriteria> {

    /**
     * Returns a lock hash info based on the hash
     *
     * @param hash the hash
     * @return an observable of {@link HashLockInfo}
     */
    Observable<HashLockInfo> getLockHash(String hash);


}
