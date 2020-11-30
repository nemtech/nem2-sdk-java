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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleStateInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link MetadataRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class MetadataRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

  private MetadataRepositoryOkHttpImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new MetadataRepositoryOkHttpImpl(apiClientMock);
  }

  @Test
  public void search() throws Exception {
    Address address = Address.generateRandom(networkType);
    MetadataPage dto = getMetadataEntriesDTO();
    mockRemoteCall(dto);
    List<Metadata> resultList =
        repository
            .search(new MetadataSearchCriteria().sourceAddress(address))
            .toFuture()
            .get()
            .getData();
    assertMetadataList(dto, resultList);
  }

  @Test
  public void getMetadata() throws Exception {
    MetadataInfoDTO metadataDTO = getMetadataEntriesDTO().getData().get(0);
    mockRemoteCall(metadataDTO);
    Metadata metadata = repository.getMetadata("abc").toFuture().get();
    assertMetadata(metadataDTO, metadata);
  }

  private void assertMetadataList(MetadataPage expected, List<Metadata> resultList) {
    int index = 0;
    Assertions.assertEquals(expected.getData().size(), resultList.size());

    Assertions.assertEquals(3, resultList.size());

    for (Metadata metadata : resultList) {
      MetadataInfoDTO metadataDTO = expected.getData().get(index++);
      assertMetadata(metadataDTO, metadata);
    }
  }

  private void assertMetadata(MetadataInfoDTO expected, Metadata result) {
    Assertions.assertEquals(expected.getId(), result.getRecordId().get());
    Assertions.assertEquals(
        expected.getMetadataEntry().getCompositeHash(), result.getCompositeHash());
    Assertions.assertEquals(
        expected.getMetadataEntry().getSourceAddress(), result.getSourceAddress().encoded());
    Assertions.assertEquals(
        expected.getMetadataEntry().getTargetAddress(), result.getTargetAddress().encoded());
    Assertions.assertEquals(
        expected.getMetadataEntry().getMetadataType(),
        MetadataTypeEnum.fromValue(result.getMetadataType().getValue()));

    Assertions.assertEquals(
        ConvertUtils.fromHexToString(expected.getMetadataEntry().getValue()), result.getValue());

    if (expected.getMetadataEntry().getTargetId() != null) {
      Assertions.assertTrue(result.getTargetId().isPresent());
      BigInteger expectedTargetId = new BigInteger(expected.getMetadataEntry().getTargetId(), 16);
      if (expected.getMetadataEntry().getMetadataType() == MetadataTypeEnum.NUMBER_1) {
        Assertions.assertEquals(expectedTargetId, ((MosaicId) result.getTargetId().get()).getId());
      }

      if (expected.getMetadataEntry().getMetadataType() == MetadataTypeEnum.NUMBER_2) {
        Assertions.assertEquals(
            expectedTargetId, ((NamespaceId) result.getTargetId().get()).getId());
      }
    } else {
      Assertions.assertFalse(result.getTargetId().isPresent());
    }
  }

  private MetadataPage getMetadataEntriesDTO() {
    MetadataPage metadataPage = new MetadataPage();
    metadataPage.setPagination(new Pagination().pageNumber(1).pageSize(2));

    List<MetadataInfoDTO> data = new ArrayList<>();
    data.add(
        createMetadataDto(
            ConvertUtils.toSize16Hex(BigInteger.valueOf(10)), MetadataTypeEnum.NUMBER_0, null));
    data.add(
        createMetadataDto(
            ConvertUtils.toSize16Hex(BigInteger.valueOf(20)), MetadataTypeEnum.NUMBER_1, "11111"));
    data.add(
        createMetadataDto(
            ConvertUtils.toSize16Hex(BigInteger.valueOf(30)), MetadataTypeEnum.NUMBER_2, "22222"));
    metadataPage.setData(data);
    return metadataPage;
  }

  private MetadataInfoDTO createMetadataDto(String name, MetadataTypeEnum type, String targetId) {
    MetadataInfoDTO dto = new MetadataInfoDTO();
    dto.setId(name);

    Address sourceAddress = Account.generateNewAccount(networkType).getAddress();
    Address targetAddress = Account.generateNewAccount(networkType).getAddress();

    MetadataEntryDTO metadataEntry = new MetadataEntryDTO();
    metadataEntry.setVersion(1);
    metadataEntry.setCompositeHash("ompositeHash " + name);
    metadataEntry.setMetadataType(type);
    metadataEntry.setScopedMetadataKey("10");
    metadataEntry.sourceAddress(sourceAddress.encoded());
    metadataEntry.setTargetId(targetId);
    metadataEntry.setTargetAddress(targetAddress.encoded());
    metadataEntry.setValue(ConvertUtils.fromStringToHex(name + " message"));
    dto.setMetadataEntry(metadataEntry);
    return dto;
  }

  @Test
  public void getMetadataMerkle() throws Exception {
    mockRemoteCall(new MerkleStateInfoDTO().raw("abc"));
    MerkleStateInfo merkle = repository.getMetadataMerkle("abc").toFuture().get();
    Assertions.assertEquals("abc", merkle.getRaw());
  }

  @Override
  protected MetadataRepositoryOkHttpImpl getRepository() {
    return repository;
  }
}
