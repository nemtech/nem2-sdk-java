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
import io.nem.symbol.sdk.api.HashLockRepository;
import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.HashLockRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HashLockPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.reactivex.Observable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Implements {@link HashLockRepository}
 *
 * @author Fernando Boucquez
 */
public class HashLockRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements HashLockRepository {

  private final HashLockRoutesApi client;

  public HashLockRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new HashLockRoutesApi(apiClient);
  }

  @Override
  public Observable<HashLockInfo> getHashLock(String hash) {
    Callable<HashLockInfoDTO> callback = () -> getClient().getHashLock(hash);
    return this.call(callback, this::toHashLockInfo);
  }

  private HashLockInfo toHashLockInfo(HashLockInfoDTO dto) {
    HashLockEntryDTO lock = dto.getLock();
    return new HashLockInfo(
        Optional.of(dto.getId()),
        MapperUtils.toAddress(lock.getOwnerAddress()),
        MapperUtils.toMosaicId(lock.getMosaicId()),
        lock.getAmount(),
        lock.getEndHeight(),
        lock.getStatus(),
        lock.getHash());
  }

  @Override
  public Observable<Page<HashLockInfo>> search(HashLockSearchCriteria criteria) {
    String address = toDto(criteria.getAddress());
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());
    Callable<HashLockPage> callback =
        () -> getClient().searchHashLock(address, pageSize, pageNumber, offset, order);
    return this.call(callback, this::toPage);
  }

  private Page<HashLockInfo> toPage(HashLockPage hashLockPage) {
    return toPage(
        hashLockPage.getPagination(),
        hashLockPage.getData().stream().map(this::toHashLockInfo).collect(Collectors.toList()));
  }

  public HashLockRoutesApi getClient() {
    return client;
  }
}
