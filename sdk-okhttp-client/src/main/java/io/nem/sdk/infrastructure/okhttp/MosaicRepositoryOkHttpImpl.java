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
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.transaction.UInt64Id;
import io.nem.sdk.openapi.okhttp_gson.api.MosaicRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicNamesDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicsNamesDTO;
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

    public MosaicRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new MosaicRoutesApi(apiClient);
    }

    public MosaicRoutesApi getClient() {
        return client;
    }


    @Override
    public Observable<MosaicInfo> getMosaic(UInt64Id mosaicId) {
        Callable<MosaicInfoDTO> callback = () -> getClient().getMosaic(mosaicId.getIdAsHex());
        return exceptionHandling(call(callback).map(this::createMosaicInfo));
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaics(List<UInt64Id> ids) {
        MosaicIds mosaicIds = new MosaicIds();
        mosaicIds.mosaicIds(ids.stream()
            .map(UInt64Id::getIdAsHex)
            .collect(Collectors.toList()));
        Callable<List<MosaicInfoDTO>> callback = () -> getClient()
            .getMosaics(mosaicIds);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::createMosaicInfo).toList()
                .toObservable());
    }

    @Override
    public Observable<List<MosaicNames>> getMosaicsNames(List<UInt64Id> ids) {
        MosaicIds mosaicIds = new MosaicIds();
        mosaicIds.mosaicIds(ids.stream()
            .map(UInt64Id::getIdAsHex)
            .collect(Collectors.toList()));
        Callable<MosaicsNamesDTO> callback = () -> getClient()
            .getMosaicsNames(mosaicIds);
        return exceptionHandling(
            call(callback).map(MosaicsNamesDTO::getMosaicNames).flatMapIterable(item -> item)
                .map(this::toMosaicNames).toList()
                .toObservable());
    }

    /**
     * Converts a {@link MosaicNamesDTO} into a {@link MosaicNames}
     *
     * @param dto {@link MosaicNamesDTO}
     * @return {@link MosaicNames}
     */
    private MosaicNames toMosaicNames(MosaicNamesDTO dto) {
        return new MosaicNames(
            MapperUtils.toMosaicId(dto.getMosaicId()),
            dto.getNames().stream().map(NamespaceName::new).collect(Collectors.toList()));
    }

    private MosaicInfo createMosaicInfo(MosaicInfoDTO mosaicInfoDTO) {
        NetworkType networkType = getNetworkTypeBlocking();
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
