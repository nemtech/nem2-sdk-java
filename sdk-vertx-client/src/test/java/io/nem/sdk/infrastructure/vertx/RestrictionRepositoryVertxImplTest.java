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

package io.nem.sdk.infrastructure.vertx;

import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.AccountRestrictions;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionTypeEnum;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionsDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionsInfoDTO;
import io.nem.sdk.openapi.vertx.model.MosaicAddressRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicAddressRestrictionEntryDTO;
import io.nem.sdk.openapi.vertx.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.sdk.openapi.vertx.model.MosaicGlobalRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryDTO;
import io.nem.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.sdk.openapi.vertx.model.MosaicRestrictionEntryTypeEnum;
import io.nem.sdk.openapi.vertx.model.MosaicRestrictionTypeEnum;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link RestrictionRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class RestrictionRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private RestrictionRepositoryVertxImpl repository;


    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new RestrictionRepositoryVertxImpl(apiClientMock, networkType);
    }


    @Test
    public void shouldGetAccountRestrictions() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        AccountRestrictionsDTO dto = new AccountRestrictionsDTO();
        dto.setAddress(address.encoded());
        AccountRestrictionDTO restriction = new AccountRestrictionDTO();
        restriction.setRestrictionType(AccountRestrictionTypeEnum.NUMBER_2);
        restriction.setValues(Arrays.asList("9636553580561478212"));
        dto.setRestrictions(Collections.singletonList(restriction));

        AccountRestrictionsInfoDTO info = new AccountRestrictionsInfoDTO();
        info.setAccountRestrictions(dto);
        mockRemoteCall(info);

        AccountRestrictions accountRestrictions = repository
            .getAccountRestrictions(address).toFuture().get();

        Assertions.assertEquals(address, accountRestrictions.getAddress());
        Assertions.assertEquals(1, accountRestrictions.getRestrictions().size());
        Assertions.assertEquals(AccountRestrictionType.ALLOW_INCOMING_MOSAIC,
            accountRestrictions.getRestrictions().get(0).getRestrictionType());
        Assertions.assertEquals(
            Arrays.asList(MapperUtils.toMosaicId("9636553580561478212")),
            accountRestrictions.getRestrictions().get(0).getValues());

    }

    @Test
    public void shouldGetAccountsRestrictionsFromAddresses() throws Exception {
        Address address =
            Address.createFromEncoded(
                "9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");

        AccountRestrictionsDTO dto = new AccountRestrictionsDTO();
        dto.setAddress(address.encoded());
        AccountRestrictionDTO restriction = new AccountRestrictionDTO();
        restriction.setRestrictionType(AccountRestrictionTypeEnum.NUMBER_1);
        restriction.setValues(Arrays.asList("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142"));
        dto.setRestrictions(Collections.singletonList(restriction));

        AccountRestrictionsInfoDTO info = new AccountRestrictionsInfoDTO();
        info.setAccountRestrictions(dto);
        mockRemoteCall(Collections.singletonList(info));

        AccountRestrictions accountRestrictions = repository
            .getAccountsRestrictions(Collections.singletonList(address)).toFuture()
            .get().get(0);

        Assertions.assertEquals(address, accountRestrictions.getAddress());
        Assertions.assertEquals(1, accountRestrictions.getRestrictions().size());
        Assertions.assertEquals(AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
            accountRestrictions.getRestrictions().get(0).getRestrictionType());
        Assertions.assertEquals(Collections.singletonList(MapperUtils
                .toAddressFromUnresolved("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142")),
            accountRestrictions.getRestrictions().get(0).getValues());

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
        entryDTO.setKey(ConvertUtils.toSize16Hex(BigInteger.valueOf(1111)));
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
        entryDTO.setKey(ConvertUtils.toSize16Hex(BigInteger.valueOf(1111)));
        MosaicGlobalRestrictionEntryRestrictionDTO entryRestrictionDto = new MosaicGlobalRestrictionEntryRestrictionDTO();
        entryRestrictionDto.setRestrictionType(MosaicRestrictionTypeEnum.NUMBER_5);
        entryRestrictionDto.setReferenceMosaicId("456");
        entryRestrictionDto.setRestrictionValue("3333");
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
        entryDTO.setKey(ConvertUtils.toSize16Hex(BigInteger.valueOf(1111)));
        MosaicGlobalRestrictionEntryRestrictionDTO entryRestrictionDto = new MosaicGlobalRestrictionEntryRestrictionDTO();
        entryRestrictionDto.setRestrictionType(MosaicRestrictionTypeEnum.NUMBER_5);
        entryRestrictionDto.setReferenceMosaicId("456");
        entryRestrictionDto.setRestrictionValue("3333");
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
        entryDTO.setKey(ConvertUtils.toSize16Hex(BigInteger.valueOf(1111)));
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

}
