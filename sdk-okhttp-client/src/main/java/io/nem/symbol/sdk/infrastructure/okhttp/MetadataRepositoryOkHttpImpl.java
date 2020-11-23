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
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.MetadataRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MetadataTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/** Implementation of {@link MetadataRepository} */
public class MetadataRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements MetadataRepository {

  private final MetadataRoutesApi client;

  public MetadataRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new MetadataRoutesApi(apiClient);
  }

  @Override
  public Observable<Page<Metadata>> search(MetadataSearchCriteria criteria) {

    String sourceAddress = toDto(criteria.getSourceAddress());
    String targetAddress = toDto(criteria.getTargetAddress());
    String scopedMetadataKey = toDto(criteria.getScopedMetadataKey());
    String targetId = criteria.getTargetId();
    MetadataTypeEnum metadataType =
        criteria.getMetadataType() == null
            ? null
            : MetadataTypeEnum.fromValue(criteria.getMetadataType().getValue());
    String offset = criteria.getOffset();
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    Order order = toDto(criteria.getOrder());

    Callable<MetadataPage> callback =
        () ->
            getClient()
                .searchMetadataEntries(
                    sourceAddress,
                    targetAddress,
                    scopedMetadataKey,
                    targetId,
                    metadataType,
                    pageSize,
                    pageNumber,
                    offset,
                    order);

    return call(
        callback,
        page ->
            this.toPage(
                page.getPagination(),
                page.getData().stream().map(this::toMetadata).collect(Collectors.toList())));
  }

  public MetadataRoutesApi getClient() {
    return client;
  }

  /**
   * It converts the {@link MetadataInfoDTO} into a model {@link Metadata}.
   *
   * @param dto the {@link MetadataInfoDTO}
   * @return the {@link Metadata}
   */
  private Metadata toMetadata(MetadataInfoDTO dto) {

    MetadataEntryDTO entryDto = dto.getMetadataEntry();
    return new Metadata(
        dto.getId(),
        entryDto.getCompositeHash(),
        MapperUtils.toAddress(entryDto.getSourceAddress()),
        MapperUtils.toAddress(entryDto.getTargetAddress()),
        new BigInteger(entryDto.getScopedMetadataKey(), 16),
        MetadataType.rawValueOf(entryDto.getMetadataType().getValue()),
        ConvertUtils.fromHexToString(entryDto.getValue()),
        Optional.ofNullable(Objects.toString(entryDto.getTargetId(), null)));
  }

  @Override
  public Observable<Metadata> getMetadata(String compositeHash) {
    return call(() -> this.client.getMetadata(compositeHash), this::toMetadata);
  }

  @Override
  public Observable<MerkleStateInfo> getMetadataMerkle(String compositeHash) {
    return call(() -> this.client.getMetadataMerkle(compositeHash), this::toMerkleStateInfo);
  }
}
