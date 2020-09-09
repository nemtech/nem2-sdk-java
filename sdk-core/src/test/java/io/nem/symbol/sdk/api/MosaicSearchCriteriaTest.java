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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link MosaicSearchCriteria} */
class MosaicSearchCriteriaTest {

  @Test
  void shouldCreate() {
    MosaicSearchCriteria criteria = new MosaicSearchCriteria();
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertNull(criteria.getOwnerAddress());
  }

  @Test
  void shouldSetValues() {

    Address address1 = Address.generateRandom(NetworkType.MIJIN_TEST);

    MosaicSearchCriteria criteria = new MosaicSearchCriteria();

    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setOffset("abc");
    criteria.setOwnerAddress(address1);

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(address1, criteria.getOwnerAddress());
    Assertions.assertEquals("abc", criteria.getOffset());
  }

  @Test
  void shouldUseBuilderMethods() {

    Address address1 = Address.generateRandom(NetworkType.MIJIN_TEST);

    MosaicSearchCriteria criteria =
        new MosaicSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .ownerAddress(address1);

    criteria.offset("abc");
    Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(address1, criteria.getOwnerAddress());
    Assertions.assertEquals("abc", criteria.getOffset());
  }

  @Test
  void shouldBeEquals() {

    Address address1 = Address.generateRandom(NetworkType.MIJIN_TEST);

    MosaicSearchCriteria criteria1 =
        new MosaicSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .ownerAddress(address1);
    criteria1.offset("abc");

    MosaicSearchCriteria criteria2 =
        new MosaicSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .ownerAddress(address1);
    criteria2.offset("abc");

    Assertions.assertEquals(new MosaicSearchCriteria(), new MosaicSearchCriteria());
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
  }
}
