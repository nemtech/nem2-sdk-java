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

import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link ResolutionStatementSearchCriteria} */
class ResolutionStatementSearchCriteriaTest {

  @Test
  void shouldCreate() {
    ResolutionStatementSearchCriteria criteria = new ResolutionStatementSearchCriteria();
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertNull(criteria.getHeight());
  }

  @Test
  void shouldSetValues() {

    ResolutionStatementSearchCriteria criteria = new ResolutionStatementSearchCriteria();

    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setOffset("abc");
    criteria.setHeight(BigInteger.ONE);

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
    Assertions.assertEquals("abc", criteria.getOffset());
  }

  @Test
  void shouldUseBuilderMethods() {

    ResolutionStatementSearchCriteria criteria =
        new ResolutionStatementSearchCriteria().height(BigInteger.ONE);
    criteria.order(OrderBy.ASC).pageSize(10).pageNumber(5);

    criteria.offset("abc");
    Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
    Assertions.assertEquals("abc", criteria.getOffset());
  }

  @Test
  void shouldBeEquals() {

    ResolutionStatementSearchCriteria criteria1 =
        new ResolutionStatementSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .height(BigInteger.ONE);
    criteria1.offset("abc");

    ResolutionStatementSearchCriteria criteria2 =
        new ResolutionStatementSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .height(BigInteger.ONE);
    criteria2.offset("abc");

    Assertions.assertEquals(
        new ResolutionStatementSearchCriteria(), new ResolutionStatementSearchCriteria());
    Assertions.assertEquals(criteria1, criteria2);
    Assertions.assertEquals(criteria1, criteria1);
    Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria1.setHeight(BigInteger.TEN);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    Assertions.assertNotEquals("ABC", criteria2);
  }
}
