/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.reactivex.Observable;

/** Repository used to retrieves lock hashes. */
public interface HashLockRepository extends Searcher<HashLockInfo, HashLockSearchCriteria> {

  /**
   * Returns a lock hash info based on the hash
   *
   * @param hash the hash
   * @return an observable of {@link HashLockInfo}
   */
  Observable<HashLockInfo> getHashLock(String hash);

  /**
   * Returns a lock hash merkle info based on the hash
   *
   * @param hash the hash
   * @return an observable of {@link MerkleStateInfo}
   */
  Observable<MerkleStateInfo> getHashLockMerkle(String hash);
}
