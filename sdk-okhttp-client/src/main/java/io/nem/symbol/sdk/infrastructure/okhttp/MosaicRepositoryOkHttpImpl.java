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

import static io.nem.symbol.core.utils.MapperUtils.toMosaicId;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.MosaicRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicPage;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Mosaic http repository.
 *
 * @since 1.0
 */
public class MosaicRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements MosaicRepository {

  private final MosaicRoutesApi client;

  private final Observable<NetworkType> networkTypeObservable;

  public MosaicRepositoryOkHttpImpl(
      ApiClient apiClient, Observable<NetworkType> networkTypeObservable) {
    super(apiClient);
    this.client = new MosaicRoutesApi(apiClient);
    this.networkTypeObservable = networkTypeObservable;
  }

  public MosaicRoutesApi getClient() {
    return client;
  }

  @Override
  public Observable<MosaicInfo> getMosaic(MosaicId mosaicId) {
    Callable<MosaicInfoDTO> callback = () -> getClient().getMosaic(mosaicId.getIdAsHex());
    return exceptionHandling(
        networkTypeObservable.flatMap(
            networkType ->
                call(callback).map(mosaicInfoDTO -> createMosaicInfo(mosaicInfoDTO, networkType))));
  }

  @Override
  public Observable<List<MosaicInfo>> getMosaics(List<MosaicId> ids) {
    MosaicIds mosaicIds = new MosaicIds();
    mosaicIds.mosaicIds(ids.stream().map(MosaicId::getIdAsHex).collect(Collectors.toList()));
    Callable<List<MosaicInfoDTO>> callback = () -> getClient().getMosaics(mosaicIds);
    return exceptionHandling(
        networkTypeObservable.flatMap(
            networkType ->
                call(callback)
                    .flatMapIterable(item -> item)
                    .map(mosaicInfoDTO -> createMosaicInfo(mosaicInfoDTO, networkType))
                    .toList()
                    .toObservable()));
  }

  @Override
  public Observable<Page<MosaicInfo>> search(MosaicSearchCriteria criteria) {
    Callable<MosaicPage> callback =
        () ->
            getClient()
                .searchMosaics(
                    toDto(criteria.getOwnerAddress()),
                    criteria.getPageSize(),
                    criteria.getPageNumber(),
                    criteria.getOffset(),
                    toDto(criteria.getOrder()));

    return exceptionHandling(
        networkTypeObservable.flatMap(
            networkType ->
                call(callback)
                    .map(
                        mosaicPage ->
                            this.toPage(
                                mosaicPage.getPagination(),
                                mosaicPage.getData().stream()
                                    .map(dto -> this.createMosaicInfo(dto, networkType))
                                    .collect(Collectors.toList())))));
  }

  private MosaicInfo createMosaicInfo(MosaicInfoDTO mosaicInfoDTO, NetworkType networkType) {
    return createMosaicInfo(mosaicInfoDTO.getMosaic(), mosaicInfoDTO.getId());
  }

  private MosaicInfo createMosaicInfo(MosaicDTO mosaic, String recordId) {
    return new MosaicInfo(
        recordId,
        toMosaicId(mosaic.getId()),
        mosaic.getSupply(),
        mosaic.getStartHeight(),
        MapperUtils.toAddress(mosaic.getOwnerAddress()),
        mosaic.getRevision(),
        extractMosaicFlags(mosaic),
        mosaic.getDivisibility(),
        mosaic.getDuration());
  }

  private MosaicFlags extractMosaicFlags(MosaicDTO mosaicDTO) {
    return MosaicFlags.create(mosaicDTO.getFlags());
  }
}
