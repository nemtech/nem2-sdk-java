/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicRestrictionEntryTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicRestrictionTypeEnum;
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


    @Test
    public void shouldGetMosaicAddressRestrictions() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        MosaicId mosaicId = MapperUtils.toMosaicId("123");

        MosaicAddressRestrictionDTO dto = new MosaicAddressRestrictionDTO();
        MosaicAddressRestrictionEntryWrapperDTO wrapperDTO = new MosaicAddressRestrictionEntryWrapperDTO();
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

        List<MosaicAddressRestrictionDTO> list = new ArrayList<>();
        list.add(dto);

        mockRemoteCall(list);

        List<MosaicAddressRestriction> mosaicAddressRestrictions = repository
            .getMosaicAddressRestrictions(mosaicId, Collections.singletonList(address)).toFuture()
            .get();

        Assertions.assertEquals(1, mosaicAddressRestrictions.size());
        MosaicAddressRestriction mosaicAddressRestriction = mosaicAddressRestrictions.get(0);

        Assertions.assertEquals(wrapperDTO.getCompositeHash(),
            mosaicAddressRestriction.getCompositeHash());

        Assertions.assertEquals(MosaicRestrictionEntryType.ADDRESS,
            mosaicAddressRestriction.getEntryType());

        Assertions.assertEquals(mosaicId, mosaicAddressRestriction.getMosaicId());
        Assertions.assertEquals(address, mosaicAddressRestriction.getTargetAddress());
        Assertions.assertEquals(1, mosaicAddressRestriction.getRestrictions().size());
        Assertions
            .assertEquals(BigInteger.valueOf(2222),
                mosaicAddressRestriction.getRestrictions().get(BigInteger.valueOf(1111)));

    }

    @Test
    public void shouldGetMosaicGlobalRestrictions() throws Exception {

        MosaicId mosaicId = MapperUtils.toMosaicId("123");

        MosaicGlobalRestrictionDTO dto = new MosaicGlobalRestrictionDTO();
        MosaicGlobalRestrictionEntryWrapperDTO wrapperDTO = new MosaicGlobalRestrictionEntryWrapperDTO();
        dto.setMosaicRestrictionEntry(wrapperDTO);

        MosaicGlobalRestrictionEntryDTO entryDTO = new MosaicGlobalRestrictionEntryDTO();
        entryDTO.setKey(ConvertUtils.toString(BigInteger.valueOf(1111)));
        MosaicGlobalRestrictionEntryRestrictionDTO entryRestrictionDto = new MosaicGlobalRestrictionEntryRestrictionDTO();
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

        List<MosaicGlobalRestrictionDTO> list = new ArrayList<>();
        list.add(dto);

        mockRemoteCall(list);

        List<MosaicGlobalRestriction> mosaicGlobalRestrictions = repository
            .getMosaicGlobalRestrictions(Collections.singletonList(mosaicId)).toFuture()
            .get();

        Assertions.assertEquals(1, mosaicGlobalRestrictions.size());
        MosaicGlobalRestriction mosaicGlobalRestriction = mosaicGlobalRestrictions.get(0);

        Assertions.assertEquals(wrapperDTO.getCompositeHash(),
            mosaicGlobalRestriction.getCompositeHash());

        Assertions.assertEquals(MosaicRestrictionEntryType.GLOBAL,
            mosaicGlobalRestriction.getEntryType());

        Assertions.assertEquals(mosaicId, mosaicGlobalRestriction.getMosaicId());
        Assertions.assertEquals(1, mosaicGlobalRestriction.getRestrictions().size());
        Assertions
            .assertEquals(BigInteger.valueOf(3333),
                mosaicGlobalRestriction.getRestrictions().get(BigInteger.valueOf(1111))
                    .getRestrictionValue());
        Assertions
            .assertEquals("0000000000000456",
                mosaicGlobalRestriction.getRestrictions().get(BigInteger.valueOf(1111))
                    .getReferenceMosaicId()
                    .getIdAsHex());
        Assertions
            .assertEquals(MosaicRestrictionType.GT,
                mosaicGlobalRestriction.getRestrictions().get(BigInteger.valueOf(1111))
                    .getRestrictionType());

    }

    @Test
    public void shouldMosaicGlobalRestriction() throws Exception {

        MosaicId mosaicId = MapperUtils.toMosaicId("123");

        MosaicGlobalRestrictionDTO dto = new MosaicGlobalRestrictionDTO();
        MosaicGlobalRestrictionEntryWrapperDTO wrapperDTO = new MosaicGlobalRestrictionEntryWrapperDTO();
        dto.setMosaicRestrictionEntry(wrapperDTO);

        MosaicGlobalRestrictionEntryDTO entryDTO = new MosaicGlobalRestrictionEntryDTO();
        entryDTO.setKey(ConvertUtils.toString(BigInteger.valueOf(1111)));
        MosaicGlobalRestrictionEntryRestrictionDTO entryRestrictionDto = new MosaicGlobalRestrictionEntryRestrictionDTO();
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

        mockRemoteCall(dto);

        MosaicGlobalRestriction mosaicGlobalRestriction = repository
            .getMosaicGlobalRestriction(mosaicId).toFuture()
            .get();

        Assertions.assertEquals(wrapperDTO.getCompositeHash(),
            mosaicGlobalRestriction.getCompositeHash());

        Assertions.assertEquals(MosaicRestrictionEntryType.GLOBAL,
            mosaicGlobalRestriction.getEntryType());

        Assertions.assertEquals(mosaicId, mosaicGlobalRestriction.getMosaicId());
        Assertions.assertEquals(1, mosaicGlobalRestriction.getRestrictions().size());
        Assertions
            .assertEquals(BigInteger.valueOf(3333),
                mosaicGlobalRestriction.getRestrictions().get(BigInteger.valueOf(1111))
                    .getRestrictionValue());
        Assertions
            .assertEquals("0000000000000456",
                mosaicGlobalRestriction.getRestrictions().get(BigInteger.valueOf(1111))
                    .getReferenceMosaicId()
                    .getIdAsHex());
        Assertions
            .assertEquals(MosaicRestrictionType.GT,
                mosaicGlobalRestriction.getRestrictions().get(BigInteger.valueOf((1111)))
                    .getRestrictionType());

    }


    @Test
    public void shouldGetMosaicAddressRestriction() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        MosaicId mosaicId = MapperUtils.toMosaicId("123");

        MosaicAddressRestrictionDTO dto = new MosaicAddressRestrictionDTO();
        MosaicAddressRestrictionEntryWrapperDTO wrapperDTO = new MosaicAddressRestrictionEntryWrapperDTO();
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

        mockRemoteCall(dto);

        MosaicAddressRestriction mosaicAddressRestriction = repository
            .getMosaicAddressRestriction(mosaicId, address).toFuture()
            .get();

        Assertions.assertEquals(wrapperDTO.getCompositeHash(),
            mosaicAddressRestriction.getCompositeHash());

        Assertions.assertEquals(MosaicRestrictionEntryType.ADDRESS,
            mosaicAddressRestriction.getEntryType());

        Assertions.assertEquals(mosaicId, mosaicAddressRestriction.getMosaicId());
        Assertions.assertEquals(address, mosaicAddressRestriction.getTargetAddress());
        Assertions.assertEquals(1, mosaicAddressRestriction.getRestrictions().size());
        Assertions
            .assertEquals(BigInteger.valueOf(2222),
                mosaicAddressRestriction.getRestrictions().get((BigInteger.valueOf(1111))));

    }

    @Override
    public RestrictionMosaicRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
