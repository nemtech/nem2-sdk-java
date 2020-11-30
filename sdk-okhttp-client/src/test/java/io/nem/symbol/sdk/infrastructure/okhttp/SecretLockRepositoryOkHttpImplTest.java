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

import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.LockHashAlgorithmEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.LockStatus;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockPage;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link SecretLockRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class SecretLockRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

  private SecretLockRepositoryOkHttpImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new SecretLockRepositoryOkHttpImpl(apiClientMock);
  }

  @Override
  protected AbstractRepositoryOkHttpImpl getRepository() {
    return repository;
  }

  @Test
  public void shouldSearch() throws Exception {
    Address address = Address.generateRandom(this.networkType);
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);
    Address recipientAddress = Address.generateRandom(this.networkType);

    SecretLockEntryDTO lockHashDto = new SecretLockEntryDTO();
    lockHashDto.setOwnerAddress(encodeAddress(address));
    lockHashDto.setAmount(BigInteger.ONE);
    lockHashDto.setEndHeight(BigInteger.TEN);
    lockHashDto.setCompositeHash("ABC");
    lockHashDto.setMosaicId(mosaicId.getIdAsHex());
    lockHashDto.setRecipientAddress(encodeAddress(recipientAddress));
    lockHashDto.setHashAlgorithm(LockHashAlgorithmEnum.NUMBER_2);
    lockHashDto.setStatus(LockStatus.NUMBER_1);
    lockHashDto.setSecret("someSecret");
    lockHashDto.setVersion(1);

    SecretLockInfoDTO hashLockInfoDTO = new SecretLockInfoDTO();
    hashLockInfoDTO.setLock(lockHashDto);
    hashLockInfoDTO.setId("123");

    mockRemoteCall(toPage(hashLockInfoDTO));

    List<SecretLockInfo> list =
        repository
            .search(new SecretLockSearchCriteria().address(address))
            .toFuture()
            .get()
            .getData();
    Assertions.assertEquals(1, list.size());
    SecretLockInfo resolvedSecretLockInfo = list.get(0);
    Assertions.assertEquals(address, resolvedSecretLockInfo.getOwnerAddress());
    Assertions.assertEquals(recipientAddress, resolvedSecretLockInfo.getRecipientAddress());
    Assertions.assertEquals(hashLockInfoDTO.getId(), resolvedSecretLockInfo.getRecordId().get());
    Assertions.assertEquals(address, resolvedSecretLockInfo.getOwnerAddress());
    Assertions.assertEquals(
        lockHashDto.getCompositeHash(), resolvedSecretLockInfo.getCompositeHash());
    Assertions.assertEquals(
        io.nem.symbol.sdk.model.transaction.LockStatus.USED, resolvedSecretLockInfo.getStatus());
    Assertions.assertEquals(mosaicId, resolvedSecretLockInfo.getMosaicId());
    Assertions.assertEquals(LockHashAlgorithm.HASH_256, resolvedSecretLockInfo.getHashAlgorithm());
    Assertions.assertEquals(lockHashDto.getAmount(), resolvedSecretLockInfo.getAmount());
    Assertions.assertEquals(lockHashDto.getEndHeight(), resolvedSecretLockInfo.getEndHeight());
    Assertions.assertEquals(lockHashDto.getSecret(), resolvedSecretLockInfo.getSecret());
  }

  private SecretLockPage toPage(SecretLockInfoDTO dto) {
    return new SecretLockPage()
        .data(Collections.singletonList(dto))
        .pagination(new Pagination().pageNumber(1).pageSize(2));
  }
}
