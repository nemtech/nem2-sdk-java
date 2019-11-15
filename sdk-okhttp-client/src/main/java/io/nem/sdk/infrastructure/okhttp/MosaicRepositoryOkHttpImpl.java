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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.openapi.okhttp_gson.api.MosaicRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicInfoDTO;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Mosaic http repository.
 *
 * @since 1.0
 */
public class MosaicRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    MosaicRepository {

    private final MosaicRoutesApi client;

    private final Observable<NetworkType> networkTypeObservable;

    public MosaicRepositoryOkHttpImpl(ApiClient apiClient,
        Observable<NetworkType> networkTypeObservable) {
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
        return exceptionHandling(networkTypeObservable.flatMap(networkType -> call(callback).map(
            mosaicInfoDTO -> createMosaicInfo(mosaicInfoDTO, networkType))));
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaics(List<MosaicId> ids) {
        MosaicIds mosaicIds = new MosaicIds();
        mosaicIds.mosaicIds(ids.stream()
            .map(MosaicId::getIdAsHex)
            .collect(Collectors.toList()));
        Callable<List<MosaicInfoDTO>> callback = () -> getClient()
            .getMosaics(mosaicIds);
        return exceptionHandling(networkTypeObservable.flatMap(networkType ->
            call(callback).flatMapIterable(item -> item).map(
                mosaicInfoDTO -> createMosaicInfo(mosaicInfoDTO, networkType)).toList()
                .toObservable()));
    }


    private MosaicInfo createMosaicInfo(MosaicInfoDTO mosaicInfoDTO,
        NetworkType networkType) {

        return MosaicInfo.create(
            MapperUtils.toMosaicId(mosaicInfoDTO.getMosaic().getId()),
            mosaicInfoDTO.getMosaic().getSupply(),
            mosaicInfoDTO.getMosaic().getStartHeight(),
            new PublicAccount(mosaicInfoDTO.getMosaic().getOwnerPublicKey(), networkType),
            mosaicInfoDTO.getMosaic().getRevision(),
            extractMosaicFlags(mosaicInfoDTO.getMosaic()),
            mosaicInfoDTO.getMosaic().getDivisibility(),
            mosaicInfoDTO.getMosaic().getDuration());
    }

    private MosaicFlags extractMosaicFlags(MosaicDTO mosaicDTO) {
        return MosaicFlags.create(mosaicDTO.getFlags());
    }
}
