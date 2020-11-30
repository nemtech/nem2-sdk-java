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
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.LockStatus;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.SecretLockRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockPage;
import io.reactivex.Observable;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Implements {@link SecretLockRepository}
 *
 * @author Fernando Boucquez
 */
public class SecretLockRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements SecretLockRepository {

  private final SecretLockRoutesApi client;

  public SecretLockRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new SecretLockRoutesApi(apiClient);
  }

  private SecretLockInfo toSecretLockInfo(SecretLockInfoDTO dto) {
    SecretLockEntryDTO lock = dto.getLock();
    MosaicId mosaicId = MapperUtils.toMosaicId(lock.getMosaicId());
    return new SecretLockInfo(
        dto.getId(),
        lock.getVersion(),
        MapperUtils.toAddress(lock.getOwnerAddress()),
        mosaicId,
        lock.getAmount(),
        lock.getEndHeight(),
        LockStatus.rawValueOf(lock.getStatus().getValue().byteValue()),
        LockHashAlgorithm.rawValueOf(lock.getHashAlgorithm().getValue()),
        lock.getSecret(),
        MapperUtils.toAddress(lock.getRecipientAddress()),
        lock.getCompositeHash());
  }

  @Override
  public Observable<Page<SecretLockInfo>> search(SecretLockSearchCriteria criteria) {
    String address = toDto(criteria.getAddress());
    String secret = criteria.getSecret();
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());
    Callable<SecretLockPage> callback =
        () -> getClient().searchSecretLock(address, secret, pageSize, pageNumber, offset, order);
    return this.call(callback, this::toPage);
  }

  private Page<SecretLockInfo> toPage(SecretLockPage SecretLockPage) {
    return toPage(
        SecretLockPage.getPagination(),
        SecretLockPage.getData().stream().map(this::toSecretLockInfo).collect(Collectors.toList()));
  }

  @Override
  public Observable<SecretLockInfo> getSecretLock(String compositeHash) {
    return this.call(() -> getClient().getSecretLock(compositeHash), this::toSecretLockInfo);
  }

  @Override
  public Observable<MerkleStateInfo> getSecretLockMerkle(String compositeHash) {
    return this.call(() -> getClient().getSecretLockMerkle(compositeHash), this::toMerkleStateInfo);
  }

  public SecretLockRoutesApi getClient() {
    return client;
  }
}
