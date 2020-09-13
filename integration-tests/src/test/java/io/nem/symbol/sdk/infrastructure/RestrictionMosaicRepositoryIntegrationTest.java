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

import io.nem.symbol.sdk.api.MosaicRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestrictionMosaicRepositoryIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchAddressRestriction(RepositoryType type) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    RestrictionMosaicRepository repository = repositoryFactory.createRestrictionMosaicRepository();

    MosaicRestrictionEntryType entryType = MosaicRestrictionEntryType.ADDRESS;
    Page<MosaicRestriction<?>> page =
        get(repository.search(new MosaicRestrictionSearchCriteria().entryType(entryType)));

    System.out.println(page.getData().size());
    page.getData()
        .forEach(
            restriction -> {
              Assertions.assertTrue(restriction instanceof MosaicAddressRestriction);
              Assertions.assertEquals(entryType, restriction.getEntryType());
              Assertions.assertNotNull(restriction.getMosaicId());
              Assertions.assertNotNull(restriction.getCompositeHash());
              Assertions.assertFalse(restriction.getRestrictions().isEmpty());
              Assertions.assertNotNull(((MosaicAddressRestriction) restriction).getTargetAddress());
            });
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchGlobalRestriction(RepositoryType type) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    RestrictionMosaicRepository repository = repositoryFactory.createRestrictionMosaicRepository();

    MosaicRestrictionEntryType entryType = MosaicRestrictionEntryType.GLOBAL;
    Page<MosaicRestriction<?>> page =
        get(repository.search(new MosaicRestrictionSearchCriteria().entryType(entryType)));

    System.out.println(page.getData().size());
    page.getData()
        .forEach(
            restriction -> {
              Assertions.assertTrue(restriction instanceof MosaicGlobalRestriction);
              Assertions.assertEquals(entryType, restriction.getEntryType());
              Assertions.assertNotNull(restriction.getMosaicId());
              Assertions.assertNotNull(restriction.getCompositeHash());
              Assertions.assertFalse(restriction.getRestrictions().isEmpty());
            });
  }
}
