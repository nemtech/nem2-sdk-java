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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link SearchCriteria} */
class SearchCriteriaTest {

  private static class TestSearchCriteria extends SearchCriteria<TestSearchCriteria> {}

  @Test
  void shouldCreate() {
    TestSearchCriteria criteria = new TestSearchCriteria();
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
  }

  @Test
  void shouldSetValues() {
    TestSearchCriteria criteria = new TestSearchCriteria();
    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setOffset("ABC");
    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("ABC", criteria.getOffset());
  }

  @Test
  void shouldUseBuilderMethods() {
    TestSearchCriteria criteria = new TestSearchCriteria().offset("ABC");
    criteria.order(OrderBy.ASC).pageSize(10).pageNumber(5);
    Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("ABC", criteria.getOffset());
  }

  @Test
  void shouldBeEquals() {
    TestSearchCriteria criteria1 = new TestSearchCriteria();
    criteria1.order(OrderBy.ASC).pageSize(10).pageNumber(5).offset("ABC");
    TestSearchCriteria criteria2 = new TestSearchCriteria();
    criteria2.order(OrderBy.ASC).pageSize(10).pageNumber(5).offset("ABC");
    Assertions.assertEquals(new TestSearchCriteria(), new TestSearchCriteria());
    Assertions.assertEquals(criteria1, criteria1);
    Assertions.assertEquals(criteria1.hashCode(), criteria1.hashCode());
    Assertions.assertEquals(criteria1, criteria2);
    Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());
    criteria1.pageNumber(20);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());
  }
}
