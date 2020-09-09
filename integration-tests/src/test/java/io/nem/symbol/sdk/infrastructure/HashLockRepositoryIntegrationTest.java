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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.api.HashLockRepository;
import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HashLockRepositoryIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getHashLockWhenDoesNotExist(RepositoryType type) {
    HashLockRepository HashLockRepository = getRepositoryFactory(type).createHashLockRepository();
    String hash = "671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96";

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class, () -> get(HashLockRepository.getHashLock(hash)));

    Assertions.assertTrue(
        exception
            .getMessage()
            .contains(
                "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id"));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getHashLockWhenInvalid(RepositoryType type) {
    HashLockRepository HashLockRepository = getRepositoryFactory(type).createHashLockRepository();
    String hash = "invalid!";

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class, () -> get(HashLockRepository.getHashLock(hash)));

    Assertions.assertEquals(
        "ApiException: Conflict - 409 - InvalidArgument - hash has an invalid format",
        exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchWhenInvalidAddress(RepositoryType type) {
    HashLockRepository repository = getRepositoryFactory(type).createHashLockRepository();
    Address address = Address.generateRandom(getNetworkType());
    Page<HashLockInfo> page = get(repository.search(new HashLockSearchCriteria(address)));
    Assertions.assertTrue(page.isLast());
    Assertions.assertTrue(page.getData().isEmpty());
    Assertions.assertEquals(20, page.getPageSize());
  }
}
