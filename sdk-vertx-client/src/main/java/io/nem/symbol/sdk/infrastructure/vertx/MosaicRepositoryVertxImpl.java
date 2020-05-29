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

import static io.nem.symbol.core.utils.MapperUtils.toMosaicId;

import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.openapi.vertx.api.MosaicRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.MosaicRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicIds;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicPage;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Mosaic http repository.
 *
 * @since 1.0
 */
public class MosaicRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    MosaicRepository {

    private final MosaicRoutesApi client;

    private final Observable<NetworkType> networkTypeObservable;

    public MosaicRepositoryVertxImpl(ApiClient apiClient,
        Observable<NetworkType> networkTypeObservable) {
        super(apiClient);
        this.client = new MosaicRoutesApiImpl(apiClient);
        this.networkTypeObservable = networkTypeObservable;
    }

    public MosaicRoutesApi getClient() {
        return client;
    }

    @Override
    public Observable<MosaicInfo> getMosaic(MosaicId mosaicId) {

        Consumer<Handler<AsyncResult<MosaicInfoDTO>>> callback = handler -> getClient()
            .getMosaic(mosaicId.getIdAsHex(), handler);
        return exceptionHandling(networkTypeObservable.flatMap(networkType -> call(callback).map(
            mosaicInfoDTO -> createMosaicInfo(mosaicInfoDTO, networkType))));
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaics(List<MosaicId> ids) {

        MosaicIds mosaicIds = new MosaicIds();
        mosaicIds.mosaicIds(ids.stream()
            .map(MosaicId::getIdAsHex)
            .collect(Collectors.toList()));
        Consumer<Handler<AsyncResult<List<MosaicInfoDTO>>>> callback = handler -> getClient()
            .getMosaics(mosaicIds, handler);
        return exceptionHandling(networkTypeObservable.flatMap(networkType ->
            call(callback).flatMapIterable(item -> item).map(
                mosaicInfoDTO -> createMosaicInfo(mosaicInfoDTO, networkType))
                .toList()
                .toObservable()));
    }


    @Override
    public Observable<Page<MosaicInfo>> search(MosaicSearchCriteria criteria) {
        Consumer<Handler<AsyncResult<MosaicPage>>> callback = handler -> getClient()
            .searchMosaics(toDto(criteria.getOwnerAddress()),
                criteria.getPageSize(),
                criteria.getPageNumber(), criteria.getOffset(),
                toDto(criteria.getOrder()), handler);

        return exceptionHandling(networkTypeObservable.flatMap(networkType ->
            call(callback).map(mosaicPage -> this.toPage(mosaicPage.getPagination(),
                mosaicPage.getData().stream().map(dto -> this.createMosaicInfo(dto, networkType)).collect(
                    Collectors.toList())))));
    }


    private MosaicInfo createMosaicInfo(MosaicInfoDTO mosaicInfoDTO, NetworkType networkType) {
        return createMosaicInfo(mosaicInfoDTO.getMosaic(),mosaicInfoDTO.getId(), networkType);
    }

    private MosaicInfo createMosaicInfo(MosaicDTO mosaic, String recordId, NetworkType networkType) {
        return new MosaicInfo(
            recordId,
            toMosaicId(mosaic.getId()),
            mosaic.getSupply(),
            mosaic.getStartHeight(),
            new PublicAccount(mosaic.getOwnerPublicKey(), networkType),
            mosaic.getRevision(),
            extractMosaicFlags(mosaic),
            mosaic.getDivisibility(),
            mosaic.getDuration());
    }

    private static MosaicFlags extractMosaicFlags(MosaicDTO mosaicDTO) {
        return MosaicFlags.create(mosaicDTO.getFlags());
    }
}
