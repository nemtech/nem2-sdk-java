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
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link MosaicRestrictionSearchCriteria} */
class MosaicRestrictionSearchCriteriaTest {

  @Test
  void shouldCreate() {
    MosaicRestrictionSearchCriteria criteria = new MosaicRestrictionSearchCriteria();
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertNull(criteria.getMosaicId());
    Assertions.assertNull(criteria.getEntryType());
    Assertions.assertNull(criteria.getMosaicId());
    Assertions.assertNull(criteria.getTargetAddress());
  }

  @Test
  void shouldSetValues() {

    Address address = Address.generateRandom(NetworkType.TEST_NET);
    MosaicRestrictionEntryType entryType = MosaicRestrictionEntryType.GLOBAL;
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);
    MosaicRestrictionSearchCriteria criteria = new MosaicRestrictionSearchCriteria();
    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setOffset("abc");
    criteria.setMosaicId(mosaicId);
    criteria.setTargetAddress(address);
    criteria.setEntryType(entryType);

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals(mosaicId, criteria.getMosaicId());
    Assertions.assertEquals(address, criteria.getTargetAddress());
    Assertions.assertEquals(entryType, criteria.getEntryType());
  }

  @Test
  void shouldUseBuilderMethods() {
    Address address = Address.generateRandom(NetworkType.TEST_NET);
    MosaicRestrictionEntryType entryType = MosaicRestrictionEntryType.GLOBAL;
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);
    MosaicRestrictionSearchCriteria criteria = new MosaicRestrictionSearchCriteria();
    criteria.order(OrderBy.DESC);
    criteria.pageSize(10);
    criteria.pageNumber(5);
    criteria.offset("abc");
    criteria.mosaicId(mosaicId);
    criteria.targetAddress(address);
    criteria.entryType(entryType);

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals(mosaicId, criteria.getMosaicId());
    Assertions.assertEquals(address, criteria.getTargetAddress());
    Assertions.assertEquals(entryType, criteria.getEntryType());
  }

  @Test
  void shouldBeEquals() {

    Address address = Address.generateRandom(NetworkType.TEST_NET);
    MosaicRestrictionEntryType entryType = MosaicRestrictionEntryType.GLOBAL;
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);

    MosaicRestrictionSearchCriteria criteria1 = new MosaicRestrictionSearchCriteria();
    criteria1.order(OrderBy.DESC);
    criteria1.pageSize(10);
    criteria1.pageNumber(5);
    criteria1.offset("abc");
    criteria1.mosaicId(mosaicId);
    criteria1.targetAddress(address);
    criteria1.entryType(entryType);

    MosaicRestrictionSearchCriteria criteria2 = new MosaicRestrictionSearchCriteria();
    criteria2.setOrder(OrderBy.DESC);
    criteria2.setPageSize(10);
    criteria2.setPageNumber(5);
    criteria2.setOffset("abc");
    criteria2.setMosaicId(mosaicId);
    criteria2.setTargetAddress(address);
    criteria2.setEntryType(entryType);

    Assertions.assertEquals(
        new MosaicRestrictionSearchCriteria(), new MosaicRestrictionSearchCriteria());
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
