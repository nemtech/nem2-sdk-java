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
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.MosaicRestrictionSearchCriteria;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicRestrictionEntryTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicRestrictionTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicRestrictionsPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link RestrictionMosaicRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class RestrictionMosaicRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

  private RestrictionMosaicRepositoryOkHttpImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new RestrictionMosaicRepositoryOkHttpImpl(apiClientMock);
  }

  @Override
  protected AbstractRepositoryOkHttpImpl getRepository() {
    return this.repository;
  }

  @Test
  public void shouldGetMosaicAddressRestrictions() throws Exception {
    Address address = Address.generateRandom(this.networkType);

    MosaicId mosaicId = MapperUtils.toMosaicId("123");

    MosaicAddressRestrictionDTO dto = new MosaicAddressRestrictionDTO();
    MosaicAddressRestrictionEntryWrapperDTO wrapperDTO =
        new MosaicAddressRestrictionEntryWrapperDTO();
    dto.setMosaicRestrictionEntry(wrapperDTO);

    MosaicAddressRestrictionEntryDTO entryDTO = new MosaicAddressRestrictionEntryDTO();
    entryDTO.setKey(ConvertUtils.toString(BigInteger.valueOf(1111)));
    entryDTO.setValue("2222");
    List<MosaicAddressRestrictionEntryDTO> restrictions = new ArrayList<>();
    restrictions.add(entryDTO);

    wrapperDTO.setCompositeHash("compositeHash");
    wrapperDTO.setMosaicId(mosaicId.getIdAsHex());
    wrapperDTO.setRestrictions(restrictions);
    wrapperDTO.setEntryType(MosaicRestrictionEntryTypeEnum.NUMBER_0);
    wrapperDTO.setTargetAddress(address.encoded());

    mockRemoteCall(toPage(dto));

    List<MosaicRestriction<?>> mosaicAddressRestrictions =
        repository.search(new MosaicRestrictionSearchCriteria()).toFuture().get().getData();

    Assertions.assertEquals(1, mosaicAddressRestrictions.size());
    MosaicAddressRestriction mosaicAddressRestriction =
        (MosaicAddressRestriction) mosaicAddressRestrictions.get(0);

    Assertions.assertEquals(
        wrapperDTO.getCompositeHash(), mosaicAddressRestriction.getCompositeHash());

    Assertions.assertEquals(
        MosaicRestrictionEntryType.ADDRESS, mosaicAddressRestriction.getEntryType());

    Assertions.assertEquals(mosaicId, mosaicAddressRestriction.getMosaicId());
    Assertions.assertEquals(address, mosaicAddressRestriction.getTargetAddress());
    Assertions.assertEquals(1, mosaicAddressRestriction.getRestrictions().size());
    Assertions.assertEquals(
        BigInteger.valueOf(2222),
        mosaicAddressRestriction.getRestrictions().get(BigInteger.valueOf(1111)));
  }

  @Test
  public void shouldGetMosaicGlobalRestrictions() throws Exception {

    MosaicId mosaicId = MapperUtils.toMosaicId("123");

    MosaicGlobalRestrictionDTO dto = new MosaicGlobalRestrictionDTO();
    MosaicGlobalRestrictionEntryWrapperDTO wrapperDTO =
        new MosaicGlobalRestrictionEntryWrapperDTO();
    dto.setMosaicRestrictionEntry(wrapperDTO);

    MosaicGlobalRestrictionEntryDTO entryDTO = new MosaicGlobalRestrictionEntryDTO();
    entryDTO.setKey(ConvertUtils.toString(BigInteger.valueOf(1111)));
    MosaicGlobalRestrictionEntryRestrictionDTO entryRestrictionDto =
        new MosaicGlobalRestrictionEntryRestrictionDTO();
    entryRestrictionDto.setRestrictionType(MosaicRestrictionTypeEnum.NUMBER_5);
    entryRestrictionDto.setReferenceMosaicId("456");
    entryRestrictionDto.setRestrictionValue(BigInteger.valueOf(3333));
    entryDTO.setRestriction(entryRestrictionDto);
    List<MosaicGlobalRestrictionEntryDTO> restrictions = new ArrayList<>();
    restrictions.add(entryDTO);

    wrapperDTO.setCompositeHash("compositeHash");
    wrapperDTO.setMosaicId(mosaicId.getIdAsHex());
    wrapperDTO.setRestrictions(restrictions);
    wrapperDTO.setEntryType(MosaicRestrictionEntryTypeEnum.NUMBER_1);

    mockRemoteCall(toPage(dto));

    List<MosaicRestriction<?>> mosaicGlobalRestrictions =
        repository.search(new MosaicRestrictionSearchCriteria()).toFuture().get().getData();

    Assertions.assertEquals(1, mosaicGlobalRestrictions.size());
    MosaicGlobalRestriction mosaicGlobalRestriction =
        (MosaicGlobalRestriction) mosaicGlobalRestrictions.get(0);

    Assertions.assertEquals(
        wrapperDTO.getCompositeHash(), mosaicGlobalRestriction.getCompositeHash());

    Assertions.assertEquals(
        MosaicRestrictionEntryType.GLOBAL, mosaicGlobalRestriction.getEntryType());

    Assertions.assertEquals(mosaicId, mosaicGlobalRestriction.getMosaicId());
    Assertions.assertEquals(1, mosaicGlobalRestriction.getRestrictions().size());
    Assertions.assertEquals(
        BigInteger.valueOf(3333),
        mosaicGlobalRestriction
            .getRestrictions()
            .get(BigInteger.valueOf(1111))
            .getRestrictionValue());
    Assertions.assertEquals(
        "0000000000000456",
        mosaicGlobalRestriction
            .getRestrictions()
            .get(BigInteger.valueOf(1111))
            .getReferenceMosaicId()
            .getIdAsHex());
    Assertions.assertEquals(
        MosaicRestrictionType.GT,
        mosaicGlobalRestriction
            .getRestrictions()
            .get(BigInteger.valueOf(1111))
            .getRestrictionType());
  }

  @Test
  public void shouldMosaicGlobalRestriction() throws Exception {

    MosaicId mosaicId = MapperUtils.toMosaicId("123");

    MosaicGlobalRestrictionDTO dto = new MosaicGlobalRestrictionDTO();
    MosaicGlobalRestrictionEntryWrapperDTO wrapperDTO =
        new MosaicGlobalRestrictionEntryWrapperDTO();
    dto.setMosaicRestrictionEntry(wrapperDTO);

    MosaicGlobalRestrictionEntryDTO entryDTO = new MosaicGlobalRestrictionEntryDTO();
    entryDTO.setKey(ConvertUtils.toString(BigInteger.valueOf(1111)));
    MosaicGlobalRestrictionEntryRestrictionDTO entryRestrictionDto =
        new MosaicGlobalRestrictionEntryRestrictionDTO();
    entryRestrictionDto.setRestrictionType(MosaicRestrictionTypeEnum.NUMBER_5);
    entryRestrictionDto.setReferenceMosaicId("456");
    entryRestrictionDto.setRestrictionValue(BigInteger.valueOf(3333));
    entryDTO.setRestriction(entryRestrictionDto);
    List<MosaicGlobalRestrictionEntryDTO> restrictions = new ArrayList<>();
    restrictions.add(entryDTO);

    wrapperDTO.setCompositeHash("compositeHash");
    wrapperDTO.setMosaicId(mosaicId.getIdAsHex());
    wrapperDTO.setRestrictions(restrictions);
    wrapperDTO.setEntryType(MosaicRestrictionEntryTypeEnum.NUMBER_1);

    mockRemoteCall(toPage(dto));

    MosaicGlobalRestriction mosaicGlobalRestriction =
        (MosaicGlobalRestriction)
            repository
                .search(new MosaicRestrictionSearchCriteria())
                .toFuture()
                .get()
                .getData()
                .get(0);

    Assertions.assertEquals(
        wrapperDTO.getCompositeHash(), mosaicGlobalRestriction.getCompositeHash());

    Assertions.assertEquals(
        MosaicRestrictionEntryType.GLOBAL, mosaicGlobalRestriction.getEntryType());

    Assertions.assertEquals(mosaicId, mosaicGlobalRestriction.getMosaicId());
    Assertions.assertEquals(1, mosaicGlobalRestriction.getRestrictions().size());
    Assertions.assertEquals(
        BigInteger.valueOf(3333),
        mosaicGlobalRestriction
            .getRestrictions()
            .get(BigInteger.valueOf(1111))
            .getRestrictionValue());
    Assertions.assertEquals(
        "0000000000000456",
        mosaicGlobalRestriction
            .getRestrictions()
            .get(BigInteger.valueOf(1111))
            .getReferenceMosaicId()
            .getIdAsHex());
    Assertions.assertEquals(
        MosaicRestrictionType.GT,
        mosaicGlobalRestriction
            .getRestrictions()
            .get(BigInteger.valueOf((1111)))
            .getRestrictionType());
  }

  @Test
  public void shouldGetMosaicAddressRestriction() throws Exception {
    Address address = Address.generateRandom(this.networkType);

    MosaicId mosaicId = MapperUtils.toMosaicId("123");

    MosaicAddressRestrictionDTO dto = new MosaicAddressRestrictionDTO();
    MosaicAddressRestrictionEntryWrapperDTO wrapperDTO =
        new MosaicAddressRestrictionEntryWrapperDTO();
    dto.setMosaicRestrictionEntry(wrapperDTO);

    MosaicAddressRestrictionEntryDTO entryDTO = new MosaicAddressRestrictionEntryDTO();
    entryDTO.setKey(ConvertUtils.toString(BigInteger.valueOf(1111)));
    entryDTO.setValue("2222");
    List<MosaicAddressRestrictionEntryDTO> restrictions = new ArrayList<>();
    restrictions.add(entryDTO);

    wrapperDTO.setCompositeHash("compositeHash");
    wrapperDTO.setMosaicId(mosaicId.getIdAsHex());
    wrapperDTO.setRestrictions(restrictions);
    wrapperDTO.setEntryType(MosaicRestrictionEntryTypeEnum.NUMBER_0);
    wrapperDTO.setTargetAddress(address.encoded());

    mockRemoteCall(toPage(dto));

    MosaicAddressRestriction mosaicAddressRestriction =
        (MosaicAddressRestriction)
            repository
                .search(
                    new MosaicRestrictionSearchCriteria()
                        .targetAddress(address)
                        .mosaicId(mosaicId)
                        .entryType(MosaicRestrictionEntryType.ADDRESS))
                .toFuture()
                .get()
                .getData()
                .get(0);

    Assertions.assertEquals(
        wrapperDTO.getCompositeHash(), mosaicAddressRestriction.getCompositeHash());

    Assertions.assertEquals(
        MosaicRestrictionEntryType.ADDRESS, mosaicAddressRestriction.getEntryType());

    Assertions.assertEquals(mosaicId, mosaicAddressRestriction.getMosaicId());
    Assertions.assertEquals(address, mosaicAddressRestriction.getTargetAddress());
    Assertions.assertEquals(1, mosaicAddressRestriction.getRestrictions().size());
    Assertions.assertEquals(
        BigInteger.valueOf(2222),
        mosaicAddressRestriction.getRestrictions().get((BigInteger.valueOf(1111))));
  }

  private MosaicRestrictionsPage toPage(Object dto) {
    return new MosaicRestrictionsPage()
        .data(Collections.singletonList(dto))
        .pagination(new Pagination().pageNumber(1).pageSize(2));
  }
}
