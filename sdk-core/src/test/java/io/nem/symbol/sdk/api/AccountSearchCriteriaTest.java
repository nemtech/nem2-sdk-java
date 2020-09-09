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

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link AccountSearchCriteria} */
class AccountSearchCriteriaTest {

  @Test
  void shouldCreate() {
    AccountSearchCriteria criteria = new AccountSearchCriteria();
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertNull(criteria.getMosaicId());
    Assertions.assertNull(criteria.getOrderBy());
  }

  @Test
  void shouldSetValues() {

    AccountSearchCriteria criteria = new AccountSearchCriteria();
    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setOffset("abc");
    criteria.setOrderBy(AccountOrderBy.BALANCE);
    criteria.setMosaicId(new MosaicId(BigInteger.TEN));

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals(AccountOrderBy.BALANCE, criteria.getOrderBy());
    Assertions.assertEquals(BigInteger.TEN, criteria.getMosaicId().getId());
  }

  @Test
  void shouldUseBuilderMethods() {

    AccountSearchCriteria criteria = new AccountSearchCriteria();
    criteria.order(OrderBy.DESC);
    criteria.pageSize(10);
    criteria.pageNumber(5);
    criteria.offset("abc");
    criteria.orderBy(AccountOrderBy.BALANCE);
    criteria.mosaicId(new MosaicId(BigInteger.TEN));

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals(AccountOrderBy.BALANCE, criteria.getOrderBy());
    Assertions.assertEquals(BigInteger.TEN, criteria.getMosaicId().getId());
  }

  @Test
  void shouldBeEquals() {

    AccountSearchCriteria criteria1 = new AccountSearchCriteria();
    criteria1.order(OrderBy.DESC);
    criteria1.pageSize(10);
    criteria1.pageNumber(5);
    criteria1.offset("abc");
    criteria1.setOrderBy(AccountOrderBy.BALANCE);
    criteria1.setMosaicId(new MosaicId(BigInteger.TEN));

    AccountSearchCriteria criteria2 = new AccountSearchCriteria();
    criteria2.order(OrderBy.DESC);
    criteria2.pageSize(10);
    criteria2.pageNumber(5);
    criteria2.offset("abc");
    criteria2.setOrderBy(AccountOrderBy.BALANCE);
    criteria2.setMosaicId(new MosaicId(BigInteger.TEN));

    Assertions.assertEquals(new AccountSearchCriteria(), new AccountSearchCriteria());
    Assertions.assertEquals(criteria1, criteria2);
    Assertions.assertEquals(criteria1, criteria1);
    Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria1.pageNumber(30);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria2.pageNumber(100);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    Assertions.assertNotEquals("ABC", criteria2);
    Assertions.assertNotEquals(criteria2, "ABC");
  }
}
