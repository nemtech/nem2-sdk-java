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

import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link LockHashRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class LockHashRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private LockHashRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new LockHashRepositoryOkHttpImpl(apiClientMock);
    }

    @Override
    protected AbstractRepositoryOkHttpImpl getRepository() {
        return repository;
    }

    @Test
    public void shouldGetLockHashInfo() throws Exception {
        Address address = Address.generateRandom(this.networkType);
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);

        HashLockEntryDTO lockHashDto = new HashLockEntryDTO();
        lockHashDto.setOwnerAddress(encodeAddress(address));
        lockHashDto.setAmount(BigInteger.ONE);
        lockHashDto.setEndHeight(BigInteger.TEN);
        lockHashDto.setHash("ABC");
        lockHashDto.setMosaicId(mosaicId.getIdAsHex());
        lockHashDto.setStatus(2);

        HashLockInfoDTO hashLockInfoDTO = new HashLockInfoDTO();
        hashLockInfoDTO.setLock(lockHashDto);
        hashLockInfoDTO.setId("123");

        mockRemoteCall(hashLockInfoDTO);

        HashLockInfo resolvedLockHashInfo = repository.getLockHash("abc").toFuture().get();
        Assertions.assertEquals(address, resolvedLockHashInfo.getOwnerAddress());
        Assertions.assertEquals(hashLockInfoDTO.getId(), resolvedLockHashInfo.getRecordId().get());
        Assertions.assertEquals(address, resolvedLockHashInfo.getOwnerAddress());
        Assertions.assertEquals(lockHashDto.getHash(), resolvedLockHashInfo.getHash());
        Assertions.assertEquals(lockHashDto.getStatus(), resolvedLockHashInfo.getStatus());
        Assertions.assertEquals(mosaicId, resolvedLockHashInfo.getMosaicId());
        Assertions.assertEquals(lockHashDto.getAmount(), resolvedLockHashInfo.getAmount());
        Assertions.assertEquals(lockHashDto.getEndHeight(), resolvedLockHashInfo.getEndHeight());
    }

    @Test
    public void shouldSearch() throws Exception {
        Address address = Address.generateRandom(this.networkType);
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);

        HashLockEntryDTO lockHashDto = new HashLockEntryDTO();
        lockHashDto.setOwnerAddress(encodeAddress(address));
        lockHashDto.setAmount(BigInteger.ONE);
        lockHashDto.setEndHeight(BigInteger.TEN);
        lockHashDto.setHash("ABC");
        lockHashDto.setMosaicId(mosaicId.getIdAsHex());
        lockHashDto.setStatus(2);

        HashLockInfoDTO hashLockInfoDTO = new HashLockInfoDTO();
        hashLockInfoDTO.setLock(lockHashDto);
        hashLockInfoDTO.setId("123");

        mockRemoteCall(toPage(hashLockInfoDTO));

        List<HashLockInfo> list = repository.search(new HashLockSearchCriteria(address)).toFuture().get().getData();
        Assertions.assertEquals(1, list.size());
        HashLockInfo resolvedLockHashInfo = list.get(0);
        Assertions.assertEquals(address, resolvedLockHashInfo.getOwnerAddress());
        Assertions.assertEquals(hashLockInfoDTO.getId(), resolvedLockHashInfo.getRecordId().get());
        Assertions.assertEquals(address, resolvedLockHashInfo.getOwnerAddress());
        Assertions.assertEquals(lockHashDto.getHash(), resolvedLockHashInfo.getHash());
        Assertions.assertEquals(lockHashDto.getStatus(), resolvedLockHashInfo.getStatus());
        Assertions.assertEquals(mosaicId, resolvedLockHashInfo.getMosaicId());
        Assertions.assertEquals(lockHashDto.getAmount(), resolvedLockHashInfo.getAmount());
        Assertions.assertEquals(lockHashDto.getEndHeight(), resolvedLockHashInfo.getEndHeight());
    }


    private HashLockPage toPage(HashLockInfoDTO dto) {
        return new HashLockPage().data(Collections.singletonList(dto))
            .pagination(new Pagination().pageNumber(1).pageSize(2));
    }


}
