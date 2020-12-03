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

import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.reactivex.Observable;
import java.util.List;

/**
 * Account interface repository.
 *
 * @since 1.0
 */
public interface AccountRepository extends SearcherRepository<AccountInfo, AccountSearchCriteria> {

  /**
   * Gets an AccountInfo for an account.
   *
   * @param address Address
   * @return Observable {@link AccountInfo}
   */
  Observable<AccountInfo> getAccountInfo(Address address);

  /**
   * Gets AccountsInfo for different accounts based on their addresses.
   *
   * @param addresses {@link List} of {@link Address}
   * @return Observable {@link List} of {@link AccountInfo}
   */
  Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses);

  /**
   * Returns the merkle information of the given account.
   *
   * @param address Address
   * @return Observable {@link MerkleStateInfo}
   */
  Observable<MerkleStateInfo> getAccountInfoMerkle(Address address);
}
