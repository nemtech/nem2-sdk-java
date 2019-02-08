/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.MosaicName;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.UInt64;
import io.reactivex.Observable;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mosaic http repository.
 *
 * @since 1.0
 */
public class MosaicHttp extends Http implements MosaicRepository {

    public MosaicHttp(String host) throws MalformedURLException {
        this(host, new NetworkHttp(host));
    }

    public MosaicHttp(String host, NetworkHttp networkHttp) throws MalformedURLException {
        super(host, networkHttp);
    }

    @Override
    public Observable<MosaicInfo> getMosaic(MosaicId mosaicId) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .getAbs(this.url + "/mosaic/" + UInt64.bigIntegerToHex(mosaicId.getId()))
                        .map(Http::mapStringOrError)
                        .map(str -> objectMapper.readValue(str, MosaicInfoDTO.class))
                        .map(mosaicInfoDTO -> new MosaicInfo(mosaicInfoDTO.getMeta().isActive(),
                                mosaicInfoDTO.getMeta().getIndex(),
                                mosaicInfoDTO.getMeta().getId(),
                                new NamespaceId(mosaicInfoDTO.getMosaic().getNamespaceId().extractIntArray()),
                                new MosaicId(mosaicInfoDTO.getMosaic().getMosaicId().extractIntArray()),
                                mosaicInfoDTO.getMosaic().getSupply().extractIntArray(),
                                mosaicInfoDTO.getMosaic().getHeight().extractIntArray(),
                                new PublicAccount(mosaicInfoDTO.getMosaic().getOwner(), networkType),
                                extractMosaicProperties(mosaicInfoDTO.getMosaic().getProperties())
                        )));
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaics(List<MosaicId> mosaicIds) {
        JsonObject requestBody = new JsonObject();
        JsonArray mosaicIdJsonArray = new JsonArray();
        for(MosaicId mosaicId: mosaicIds) {
            mosaicIdJsonArray.add(UInt64.bigIntegerToHex(mosaicId.getId()));
        }
        requestBody.add("mosaicIds", mosaicIdJsonArray);
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .postAbs(this.url + "/mosaic", requestBody)
                        .map(Http::mapStringOrError)
                        .map(str -> objectMapper.<List<MosaicInfoDTO>>readValue(str, new TypeReference<List<MosaicInfoDTO>>() {
                        }))
                        .flatMapIterable(item -> item)
                        .map(mosaicInfoDTO -> new MosaicInfo(mosaicInfoDTO.getMeta().isActive(),
                                mosaicInfoDTO.getMeta().getIndex(),
                                mosaicInfoDTO.getMeta().getId(),
                                new NamespaceId(mosaicInfoDTO.getMosaic().getNamespaceId().extractIntArray()),
                                new MosaicId(mosaicInfoDTO.getMosaic().getMosaicId().extractIntArray()),
                                mosaicInfoDTO.getMosaic().getSupply().extractIntArray(),
                                mosaicInfoDTO.getMosaic().getHeight().extractIntArray(),
                                new PublicAccount(mosaicInfoDTO.getMosaic().getOwner(), networkType),
                                extractMosaicProperties(mosaicInfoDTO.getMosaic().getProperties())
                        ))
                        .toList()
                        .toObservable());
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaicsFromNamespace(NamespaceId namespaceId, QueryParams queryParams) {
        return this.getMosaicsFromNamespace(namespaceId, Optional.of(queryParams));
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaicsFromNamespace(NamespaceId namespaceId) {
        return this.getMosaicsFromNamespace(namespaceId, Optional.empty());
    }

    private Observable<List<MosaicInfo>> getMosaicsFromNamespace(NamespaceId namespaceId, Optional<QueryParams> queryParams) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .getAbs(this.url + "/namespace/" + UInt64.bigIntegerToHex(namespaceId.getId()) + "/mosaics" + (queryParams.isPresent() ? queryParams.get().toUrl() : ""))
                        .map(Http::mapStringOrError)
                        .map(str -> objectMapper.<List<MosaicInfoDTO>>readValue(str, new TypeReference<List<MosaicInfoDTO>>() {
                        }))
                        .flatMapIterable(item -> item)
                        .map(mosaicInfoDTO -> new MosaicInfo(mosaicInfoDTO.getMeta().isActive(),
                                mosaicInfoDTO.getMeta().getIndex(),
                                mosaicInfoDTO.getMeta().getId(),
                                new NamespaceId(mosaicInfoDTO.getMosaic().getNamespaceId().extractIntArray()),
                                new MosaicId(mosaicInfoDTO.getMosaic().getMosaicId().extractIntArray()),
                                mosaicInfoDTO.getMosaic().getSupply().extractIntArray(),
                                mosaicInfoDTO.getMosaic().getHeight().extractIntArray(),
                                new PublicAccount(mosaicInfoDTO.getMosaic().getOwner(), networkType),
                                extractMosaicProperties(mosaicInfoDTO.getMosaic().getProperties())
                        ))
                        .toList()
                        .toObservable());
    }

    @Override
    public Observable<List<MosaicName>> getMosaicNames(List<MosaicId> mosaicIds) {
        JsonObject requestBody = new JsonObject();
        JsonArray mosaicIdJsonArray = new JsonArray();
        for(MosaicId mosaicId: mosaicIds) {
            mosaicIdJsonArray.add(UInt64.bigIntegerToHex(mosaicId.getId()));
        }
        requestBody.add("mosaicIds", mosaicIdJsonArray);
        return this.client
                .postAbs(this.url + "/mosaic/names", requestBody)
                .map(Http::mapStringOrError)
                .map(str -> objectMapper.<List<MosaicNameDTO>>readValue(str, new TypeReference<List<MosaicNameDTO>>() {
                }))
                .flatMapIterable(item -> item)
                .map(mosaicNameDTO -> new MosaicName(new MosaicId(mosaicNameDTO.getMosaicId().extractIntArray()),
                        mosaicNameDTO.getName(),
                        new NamespaceId(mosaicNameDTO.getParentId().extractIntArray())))
                .toList()
                .toObservable();
    }

    private MosaicProperties extractMosaicProperties(MosaicPropertiesDTO mosaicPropertiesDTO) {
        String flags = "00" + Integer.toBinaryString(mosaicPropertiesDTO.get(0).extractIntArray().intValue());
        String bitMapFlags = flags.substring(flags.length() - 3, flags.length());
        return new MosaicProperties(bitMapFlags.charAt(2) == '1',
                bitMapFlags.charAt(1) == '1',
                bitMapFlags.charAt(0) == '1',
                mosaicPropertiesDTO.get(1).extractIntArray().intValue(),
                mosaicPropertiesDTO.get(2).extractIntArray());
    }
}