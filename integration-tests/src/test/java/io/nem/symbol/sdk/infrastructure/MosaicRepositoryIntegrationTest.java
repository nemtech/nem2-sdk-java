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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.api.MosaicPaginationStreamer;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransactionFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MosaicRepositoryIntegrationTest extends BaseIntegrationTest {

  private Account testAccount;
  private final List<MosaicId> mosaicIds = new ArrayList<>();
  private MosaicId mosaicId;
  private String alias;
  private NamespaceId namespaceId;

  @BeforeAll
  void setup() {
    testAccount = helper().createTestAccount(TestHelper.DEFAULT_REPOSITORY_TYPE);
    alias = ("MosaicRepositoryIntegrationTest" + RandomUtils.generateRandomInt()).toLowerCase();
    mosaicId = helper().createMosaic(testAccount, DEFAULT_REPOSITORY_TYPE, BigInteger.ZERO, alias);
    mosaicIds.add(mosaicId);
    namespaceId = NamespaceId.createFromName(this.alias);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMosaicsFromAccount(RepositoryType type) {
    List<MosaicInfo> mosaicInfos =
        get(getMosaicRepository(type)
                .search(new MosaicSearchCriteria().ownerAddress(testAccount.getAddress())))
            .getData();
    Assertions.assertTrue(mosaicInfos.size() > 0);
    mosaicInfos.forEach(this::assertMosaic);
    Assertions.assertTrue(
        mosaicInfos.stream().anyMatch(mosaicInfo -> mosaicInfo.getMosaicId().equals(mosaicId)));
  }

  private void assertMosaic(MosaicInfo m) {
    Assertions.assertEquals(testAccount.getAddress(), m.getOwnerAddress());
    Assertions.assertNotNull(m.getMosaicId());
    Assertions.assertNotNull(m.getStartHeight());
    Assertions.assertNotNull(m.getDuration());
    Assertions.assertNotNull(m.getRevision());
    Assertions.assertNotNull(m.getSupply());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMosaicViaMosaicId(RepositoryType type) {
    MosaicInfo mosaicInfo = get(getMosaicRepository(type).getMosaic(mosaicId));
    assertEquals(mosaicId, mosaicInfo.getMosaicId());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMosaicsNames(RepositoryType type) {
    {
      List<MosaicNames> mosaicNames =
          get(
              getRepositoryFactory(type)
                  .createNamespaceRepository()
                  .getMosaicsNames(Collections.singletonList(mosaicId)));
      assertEquals(1, mosaicNames.size());
      assertEquals(mosaicId, mosaicNames.get(0).getMosaicId());
      assertEquals(
          Collections.singletonList(this.alias),
          mosaicNames.get(0).getNames().stream()
              .map(NamespaceName::getName)
              .collect(Collectors.toList()));
    }

    System.out.println(mosaicId.getIdAsHex());
    {
      announceAggregateAndValidate(
          type,
          MosaicAliasTransactionFactory.create(
                  getNetworkType(), getDeadline(), AliasAction.UNLINK, this.namespaceId, mosaicId)
              .maxFee(maxFee)
              .build(),
          this.testAccount);

      sleep(2000);
      List<MosaicNames> mosaicNames =
          get(
              getRepositoryFactory(type)
                  .createNamespaceRepository()
                  .getMosaicsNames(Collections.singletonList(mosaicId)));
      assertEquals(1, mosaicNames.size());
      assertEquals(mosaicId, mosaicNames.get(0).getMosaicId());
      assertEquals(0, mosaicNames.get(0).getNames().size());
    }

    {
      announceAggregateAndValidate(
          type,
          MosaicAliasTransactionFactory.create(
                  getNetworkType(), getDeadline(), AliasAction.LINK, this.namespaceId, mosaicId)
              .maxFee(maxFee)
              .build(),
          this.testAccount);

      sleep(2000);
      List<MosaicNames> mosaicNames =
          get(
              getRepositoryFactory(type)
                  .createNamespaceRepository()
                  .getMosaicsNames(Collections.singletonList(mosaicId)));
      assertEquals(1, mosaicNames.size());
      assertEquals(mosaicId, mosaicNames.get(0).getMosaicId());
      assertEquals(
          Collections.singletonList(this.alias),
          mosaicNames.get(0).getNames().stream()
              .map(NamespaceName::getName)
              .collect(Collectors.toList()));
    }
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMosaicsViaMosaicId(RepositoryType type) {
    List<MosaicInfo> mosaicsInfo = get(getMosaicRepository(type).getMosaics(mosaicIds));

    assertEquals(mosaicIds.size(), mosaicsInfo.size());
    assertEquals(mosaicIds.get(0), mosaicsInfo.get(0).getMosaicId());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void throwExceptionWhenMosaicDoesNotExists(RepositoryType type) {
    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () -> get(getMosaicRepository(type).getMosaic(new MosaicId("AAAAAE18BE375DA2"))));
    Assertions.assertEquals(
        "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'AAAAAE18BE375DA2'",
        exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchByOwnerAddress(RepositoryType type) {
    MosaicSearchCriteria criteria = new MosaicSearchCriteria();
    Address address = testAccount.getAddress();
    criteria.ownerAddress(address);
    MosaicPaginationStreamer streamer = new MosaicPaginationStreamer(getMosaicRepository(type));
    List<MosaicInfo> mosaics = get(streamer.search(criteria).toList().toObservable());
    mosaics.forEach(m -> Assertions.assertEquals(address, m.getOwnerAddress()));
    Assertions.assertFalse(mosaics.isEmpty());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchByOwnerAddressInvalid(RepositoryType type) {
    MosaicSearchCriteria criteria = new MosaicSearchCriteria();
    Address address = Account.generateNewAccount(getNetworkType()).getAddress();
    criteria.ownerAddress(address);
    MosaicPaginationStreamer streamer = new MosaicPaginationStreamer(getMosaicRepository(type));
    List<MosaicInfo> mosaics = get(streamer.search(criteria).toList().toObservable());
    Assertions.assertTrue(mosaics.isEmpty());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearch(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(null);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchSize50(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(50);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchBlock(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(null);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchBlockPageSize50(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(50);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByIdAsc(RepositoryType type) {
    getPaginationTester(type).searchOrderByIdAsc();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByIdDesc(RepositoryType type) {
    getPaginationTester(type).searchOrderByIdDesc();
  }

  private PaginationTester<MosaicInfo, MosaicSearchCriteria> getPaginationTester(
      RepositoryType type) {
    return new PaginationTester<>(MosaicSearchCriteria::new, getMosaicRepository(type)::search);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void usingBigPageSize(RepositoryType type) {
    getPaginationTester(type).usingBigPageSize();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchUsingOffset(RepositoryType type) {
    MosaicRepository mosaicRepository = getMosaicRepository(type);
    MosaicPaginationStreamer streamer = new MosaicPaginationStreamer(mosaicRepository);
    MosaicSearchCriteria criteria = new MosaicSearchCriteria();
    criteria.setPageSize(10);
    int offsetIndex = 2;
    List<MosaicInfo> mosaicsWithoutOffset = get(streamer.search(criteria).toList().toObservable());
    criteria.setOffset(mosaicsWithoutOffset.get(offsetIndex).getRecordId().get());

    List<MosaicInfo> mosaicFromOffsets = get(streamer.search(criteria).toList().toObservable());
    PaginationTester.sameEntities(
        mosaicsWithoutOffset.stream().skip(offsetIndex + 1).collect(Collectors.toList()),
        mosaicFromOffsets);
  }

  private MosaicRepository getMosaicRepository(RepositoryType type) {
    return getRepositoryFactory(type).createMosaicRepository();
  }
}
