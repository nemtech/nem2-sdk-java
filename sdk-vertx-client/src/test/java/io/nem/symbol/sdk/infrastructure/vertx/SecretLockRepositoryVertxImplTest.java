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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.nem.symbol.sdk.openapi.vertx.model.LockHashAlgorithmEnum;
import io.nem.symbol.sdk.openapi.vertx.model.LockStatus;
import io.nem.symbol.sdk.openapi.vertx.model.MerkleStateInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.Pagination;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockEntryDTO;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockPage;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link SecretLockRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class SecretLockRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

  private SecretLockRepositoryVertxImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new SecretLockRepositoryVertxImpl(apiClientMock);
  }

  @Test
  public void shouldSearch() throws Exception {
    Address address = Address.generateRandom(this.networkType);
    Address recipientAddress = Address.generateRandom(this.networkType);
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);

    SecretLockEntryDTO lockHashDto = new SecretLockEntryDTO();
    lockHashDto.setOwnerAddress(encodeAddress(address));
    lockHashDto.setAmount(BigInteger.ONE);
    lockHashDto.setEndHeight(BigInteger.TEN);
    lockHashDto.setCompositeHash("ABC");
    lockHashDto.setRecipientAddress(encodeAddress(recipientAddress));
    lockHashDto.setMosaicId(mosaicId.getIdAsHex());
    lockHashDto.setStatus(LockStatus.NUMBER_1);
    lockHashDto.setSecret("someSecret");
    lockHashDto.setHashAlgorithm(LockHashAlgorithmEnum.NUMBER_2);
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
    Assertions.assertEquals(hashLockInfoDTO.getId(), resolvedSecretLockInfo.getRecordId().get());
    Assertions.assertEquals(address, resolvedSecretLockInfo.getOwnerAddress());
    Assertions.assertEquals(recipientAddress, resolvedSecretLockInfo.getRecipientAddress());
    Assertions.assertEquals(LockHashAlgorithm.HASH_256, resolvedSecretLockInfo.getHashAlgorithm());
    Assertions.assertEquals(
        lockHashDto.getCompositeHash(), resolvedSecretLockInfo.getCompositeHash());
    Assertions.assertEquals(
        io.nem.symbol.sdk.model.transaction.LockStatus.USED, resolvedSecretLockInfo.getStatus());
    Assertions.assertEquals(mosaicId, resolvedSecretLockInfo.getMosaicId());
    Assertions.assertEquals(lockHashDto.getAmount(), resolvedSecretLockInfo.getAmount());
    Assertions.assertEquals(lockHashDto.getEndHeight(), resolvedSecretLockInfo.getEndHeight());
  }

  @Test
  public void getSecretLock() throws Exception {
    Address address = Address.generateRandom(this.networkType);
    Address recipientAddress = Address.generateRandom(this.networkType);
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), address);

    SecretLockEntryDTO lockHashDto = new SecretLockEntryDTO();
    lockHashDto.setOwnerAddress(encodeAddress(address));
    lockHashDto.setAmount(BigInteger.ONE);
    lockHashDto.setEndHeight(BigInteger.TEN);
    lockHashDto.setCompositeHash("ABC");
    lockHashDto.setRecipientAddress(encodeAddress(recipientAddress));
    lockHashDto.setMosaicId(mosaicId.getIdAsHex());
    lockHashDto.setVersion(1);
    lockHashDto.setStatus(LockStatus.NUMBER_1);
    lockHashDto.setHashAlgorithm(LockHashAlgorithmEnum.NUMBER_2);
    lockHashDto.setSecret("ABC");

    SecretLockInfoDTO hashLockInfoDTO = new SecretLockInfoDTO();
    hashLockInfoDTO.setLock(lockHashDto);
    hashLockInfoDTO.setId("123");

    mockRemoteCall(hashLockInfoDTO);

    SecretLockInfo resolvedSecretLockInfo =
        repository.getSecretLock(lockHashDto.getCompositeHash()).toFuture().get();
    Assertions.assertEquals(
        hashLockInfoDTO.getLock().getSecret(), resolvedSecretLockInfo.getSecret());
    Assertions.assertEquals(address, resolvedSecretLockInfo.getOwnerAddress());
    Assertions.assertEquals(hashLockInfoDTO.getId(), resolvedSecretLockInfo.getRecordId().get());
    Assertions.assertEquals(address, resolvedSecretLockInfo.getOwnerAddress());
    Assertions.assertEquals(recipientAddress, resolvedSecretLockInfo.getRecipientAddress());
    Assertions.assertEquals(LockHashAlgorithm.HASH_256, resolvedSecretLockInfo.getHashAlgorithm());
    Assertions.assertEquals(
        lockHashDto.getCompositeHash(), resolvedSecretLockInfo.getCompositeHash());
    Assertions.assertEquals(
        io.nem.symbol.sdk.model.transaction.LockStatus.USED, resolvedSecretLockInfo.getStatus());
    Assertions.assertEquals(mosaicId, resolvedSecretLockInfo.getMosaicId());
    Assertions.assertEquals(lockHashDto.getAmount(), resolvedSecretLockInfo.getAmount());
    Assertions.assertEquals(lockHashDto.getEndHeight(), resolvedSecretLockInfo.getEndHeight());
    Assertions.assertEquals(lockHashDto.getSecret(), resolvedSecretLockInfo.getSecret());
  }

  @Test
  public void getSecretLockMerkle() throws Exception {
    mockRemoteCall(new MerkleStateInfoDTO().raw("abc"));
    MerkleStateInfo merkle = repository.getSecretLockMerkle("hash").toFuture().get();
    Assertions.assertEquals("abc", merkle.getRaw());
  }

  private SecretLockPage toPage(SecretLockInfoDTO dto) {
    return new SecretLockPage()
        .data(Collections.singletonList(dto))
        .pagination(new Pagination().pageNumber(1).pageSize(2));
  }
}
