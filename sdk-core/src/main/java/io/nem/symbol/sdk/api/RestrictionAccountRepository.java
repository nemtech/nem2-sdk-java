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

import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.reactivex.Observable;

/**
 * Restriction interface repository.
 *
 * @since 1.0
 */
public interface RestrictionAccountRepository
    extends SearcherRepository<AccountRestrictions, AccountRestrictionSearchCriteria> {

  /**
   * Returns the account restrictions for a given account.
   *
   * @param address the address
   * @return Observable of {@link AccountRestrictions}
   */
  Observable<AccountRestrictions> getAccountRestrictions(Address address);

  /**
   * Returns the account restrictions merkle for a given account.
   *
   * @param address the address
   * @return Observable of {@link MerkleStateInfo}
   */
  Observable<MerkleStateInfo> getAccountRestrictionsMerkle(Address address);
}
