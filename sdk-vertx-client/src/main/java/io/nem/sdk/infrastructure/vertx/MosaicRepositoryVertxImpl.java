/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.openapi.vertx.api.MosaicRoutesApi;
import io.nem.sdk.openapi.vertx.api.MosaicRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.MosaicDTO;
import io.nem.sdk.openapi.vertx.model.MosaicIds;
import io.nem.sdk.openapi.vertx.model.MosaicInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Mosaic http repository.
 *
 * @since 1.0
 */
public class MosaicRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    MosaicRepository {

    private final MosaicRoutesApi client;

    public MosaicRepositoryVertxImpl(ApiClient apiClient, Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
        client = new MosaicRoutesApiImpl(apiClient);
    }

    public MosaicRoutesApi getClient() {
        return client;
    }

    @Override
    public Observable<MosaicInfo> getMosaic(MosaicId mosaicId) {

        Consumer<Handler<AsyncResult<MosaicInfoDTO>>> callback = handler -> getClient()
            .getMosaic((mosaicId.getIdAsHex()), handler);
        return exceptionHandling(call(callback).map(this::createMosaicInfo));
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaics(List<MosaicId> ids) {

        MosaicIds mosaicIds = new MosaicIds();
        mosaicIds.mosaicIds(ids.stream()
            .map(MosaicId::getIdAsHex)
            .collect(Collectors.toList()));
        Consumer<Handler<AsyncResult<List<MosaicInfoDTO>>>> callback = handler -> getClient()
            .getMosaics(mosaicIds, handler);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::createMosaicInfo).toList()
                .toObservable());
    }

    private MosaicInfo createMosaicInfo(MosaicInfoDTO mosaicInfoDTO) {
        NetworkType networkType = getNetworkTypeBlocking();
        return MosaicInfo.create(
            toMosaicId(mosaicInfoDTO.getMosaic().getId()),
            mosaicInfoDTO.getMosaic().getSupply(),
            mosaicInfoDTO.getMosaic().getStartHeight(),
            new PublicAccount(mosaicInfoDTO.getMosaic().getOwnerPublicKey(), networkType),
            mosaicInfoDTO.getMosaic().getRevision(),
            extractMosaicFlags(mosaicInfoDTO.getMosaic()),
            mosaicInfoDTO.getMosaic().getDivisibility(),
            mosaicInfoDTO.getMosaic().getDuration());
    }




    public static MosaicFlags extractMosaicFlags(MosaicDTO mosaicDTO) {
        return MosaicFlags.create(mosaicDTO.getFlags().intValue());
    }
}
