package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.reactivex.Observable;

/**
 * Repository used to retrieves secrets lock.
 */
public interface SecretLockRepository extends Searcher<SecretLockInfo, SecretLockSearchCriteria> {

    /**
     * Returns a lock Secret info based on the Secret
     *
     * @param secret the Secret
     * @return an observable of {@link SecretLockInfo}
     */
    Observable<SecretLockInfo> getSecretLock(String secret);


}
