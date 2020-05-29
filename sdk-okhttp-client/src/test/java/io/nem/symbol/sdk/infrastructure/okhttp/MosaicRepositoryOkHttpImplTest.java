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

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link MosaicRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class MosaicRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private MosaicRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new MosaicRepositoryOkHttpImpl(apiClientMock, networkTypeObservable);
    }

    @Test
    public void shouldGetMosaics() throws Exception {
        MosaicId mosaicId = MapperUtils.toMosaicId("481110499AAA");

        MosaicDTO mosaicDto = new MosaicDTO();
        MosaicInfoDTO mosaicInfoDto = new MosaicInfoDTO();

        mosaicDto
            .setOwnerPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        mosaicDto.setId("481110499AAA");
        mosaicDto.setRevision(123);

        mosaicDto.setFlags(5);
        mosaicDto.setDivisibility(6);
        mosaicDto.setDuration(BigInteger.valueOf(7));

        mosaicInfoDto.setMosaic(mosaicDto);
        mockRemoteCall(Collections.singletonList(mosaicInfoDto));

        List<MosaicInfo> resolvedList = repository
            .getMosaics(Collections.singletonList(mosaicId))
            .toFuture().get();

        Assertions.assertEquals(1, resolvedList.size());

        MosaicInfo mosaicInfo = resolvedList.get(0);
        Assertions.assertEquals(mosaicId, mosaicInfo.getMosaicId());
        Assertions.assertEquals(mosaicDto.getRevision(), mosaicInfo.getRevision());
        Assertions
            .assertEquals(mosaicDto.getOwnerPublicKey(),
                mosaicInfo.getOwner().getPublicKey().toHex());

        Assertions.assertFalse(mosaicInfo.isTransferable());
        Assertions.assertEquals(6, mosaicInfo.getDivisibility());
        Assertions.assertEquals(BigInteger.valueOf(7), mosaicInfo.getDuration());
    }

    @Test
    public void shouldGetMosaic() throws Exception {

        MosaicId mosaicId = MapperUtils.toMosaicId("481110499AAA");

        MosaicDTO mosiacMetaDto = new MosaicDTO();
        MosaicDTO mosaicDto = new MosaicDTO();
        MosaicInfoDTO mosaicInfoDto = new MosaicInfoDTO();

        mosaicDto
            .setOwnerPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        mosaicDto.setId("481110499AAA");
        mosaicDto.setRevision(123);

        mosaicDto.setFlags(5);
        mosaicDto.setDivisibility(6);
        mosaicDto.setDuration(BigInteger.valueOf(7));

        mosaicInfoDto.setMosaic(mosaicDto);
        mockRemoteCall(mosaicInfoDto);

        MosaicInfo mosaicInfo = repository
            .getMosaic(mosaicId)
            .toFuture().get();

        Assertions.assertEquals(mosaicId, mosaicInfo.getMosaicId());
        Assertions.assertEquals(mosaicDto.getRevision(), mosaicInfo.getRevision());
        Assertions
            .assertEquals(mosaicDto.getOwnerPublicKey(),
                mosaicInfo.getOwner().getPublicKey().toHex());

        Assertions.assertFalse(mosaicInfo.isTransferable());
        Assertions.assertEquals(6, mosaicInfo.getDivisibility());
        Assertions.assertEquals(BigInteger.valueOf(7), mosaicInfo.getDuration());
    }

    @Test
    public void shouldGetMosaicsFromAccount() throws Exception {

        PublicAccount publicAccount = Account.generateNewAccount(networkType).getPublicAccount();

        MosaicId mosaicId = MapperUtils.toMosaicId("481110499AAA");

        MosaicDTO mosaicDto = new MosaicDTO();

        mosaicDto
            .setOwnerPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        mosaicDto.setId("481110499AAA");
        mosaicDto.setRevision(123);

        mosaicDto.setFlags(5);
        mosaicDto.setDivisibility(6);
        mosaicDto.setDuration(BigInteger.valueOf(7));

        mockRemoteCall(toPage(new MosaicInfoDTO().mosaic(mosaicDto).id("ABC")));

        List<MosaicInfo> resolvedList = repository
            .search(new MosaicSearchCriteria().ownerAddress(publicAccount.getAddress())).toFuture().get()
            .getData();

        Assertions.assertEquals(1, resolvedList.size());

        MosaicInfo mosaicInfo = resolvedList.get(0);
        Assertions.assertEquals(mosaicId, mosaicInfo.getMosaicId());
        Assertions.assertEquals(mosaicDto.getRevision(), mosaicInfo.getRevision());
        Assertions
            .assertEquals(mosaicDto.getOwnerPublicKey(),
                mosaicInfo.getOwner().getPublicKey().toHex());

        Assertions.assertFalse(mosaicInfo.isTransferable());
        Assertions.assertEquals(6, mosaicInfo.getDivisibility());
        Assertions.assertEquals(BigInteger.valueOf(7), mosaicInfo.getDuration());
    }

    private MosaicPage toPage(MosaicInfoDTO dto) {
        return new MosaicPage()
            .data(Collections.singletonList(dto))
            .pagination(new Pagination().pageNumber(1).pageSize(2).totalEntries(3).totalPages(4));
    }

    @Override
    public MosaicRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
