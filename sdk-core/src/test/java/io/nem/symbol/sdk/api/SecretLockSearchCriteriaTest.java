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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link SecretLockSearchCriteria} */
class SecretLockSearchCriteriaTest {

  private final Account account1 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
  private final Account account2 = Account.generateNewAccount(NetworkType.MIJIN_TEST);

  @Test
  void shouldCreate() {
    SecretLockSearchCriteria criteria = new SecretLockSearchCriteria(account1.getAddress());
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getSecret());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertEquals(account1.getAddress(), criteria.getAddress());
  }

  @Test
  void shouldSetValues() {

    SecretLockSearchCriteria criteria = new SecretLockSearchCriteria(account1.getAddress());
    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setSecret("someSecret");
    criteria.setOffset("abc");
    Assertions.assertEquals(account1.getAddress(), criteria.getAddress());

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals("someSecret", criteria.getSecret());
  }

  @Test
  void shouldUseBuilderMethods() {

    SecretLockSearchCriteria criteria =
        new SecretLockSearchCriteria(account1.getAddress())
            .order(OrderBy.ASC)
            .pageSize(10)
            .secret("someSecret")
            .pageNumber(5);

    criteria.offset("abc");
    Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals(account1.getAddress(), criteria.getAddress());
    Assertions.assertEquals("someSecret", criteria.getSecret());
  }

  @Test
  void shouldBeEquals() {

    SecretLockSearchCriteria criteria1 =
        new SecretLockSearchCriteria(account1.getAddress()).secret("someSecret");

    SecretLockSearchCriteria criteria2 =
        new SecretLockSearchCriteria(account1.getAddress()).secret("someSecret");

    Assertions.assertEquals(
        new SecretLockSearchCriteria(account1.getAddress()),
        new SecretLockSearchCriteria(account1.getAddress()));
    Assertions.assertNotEquals(
        new SecretLockSearchCriteria(account1.getAddress()),
        new SecretLockSearchCriteria(account2.getAddress()));
    Assertions.assertEquals(criteria1, criteria2);
    Assertions.assertEquals(criteria1, criteria1);
    Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria1.pageNumber(30);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria2.secret("someSecret");
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    Assertions.assertNotEquals("ABC", criteria2);
  }
}
