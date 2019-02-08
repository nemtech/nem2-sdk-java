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
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.UInt64;
import io.reactivex.Observable;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Namespace http repository.
 *
 * @since 1.0
 */
public class NamespaceHttp extends Http implements NamespaceRepository {

    public NamespaceHttp(String host) throws MalformedURLException {
        this(host, new NetworkHttp(host));
    }

    public NamespaceHttp(String host, NetworkHttp networkHttp) throws MalformedURLException {
        super(host, networkHttp);
    }

    @Override
    public Observable<NamespaceInfo> getNamespace(NamespaceId namespaceId) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .getAbs(this.url + "/namespace/" + UInt64.bigIntegerToHex(namespaceId.getId()))
                        .map(Http::mapStringOrError)
                        .map(str -> objectMapper.readValue(str, NamespaceInfoDTO.class))
                        .map(namespaceInfoDTO -> new NamespaceInfo(namespaceInfoDTO.getMeta().isActive(),
                                namespaceInfoDTO.getMeta().getIndex(),
                                namespaceInfoDTO.getMeta().getId(),
                                NamespaceType.rawValueOf(namespaceInfoDTO.getNamespace().getType()),
                                namespaceInfoDTO.getNamespace().getDepth(),
                                extractLevels(namespaceInfoDTO),
                                new NamespaceId(namespaceInfoDTO.getNamespace().getParentId().extractIntArray()),
                                new PublicAccount(namespaceInfoDTO.getNamespace().getOwner(), networkType),
                                namespaceInfoDTO.getNamespace().getStartHeight().extractIntArray(),
                                namespaceInfoDTO.getNamespace().getEndHeight().extractIntArray()
                        )));
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccount(Address address, QueryParams queryParams) {
        return this.getNamespacesFromAccount(address, Optional.of(queryParams));
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccount(Address address) {
        return this.getNamespacesFromAccount(address, Optional.empty());
    }

    private Observable<List<NamespaceInfo>> getNamespacesFromAccount(Address address, Optional<QueryParams> queryParams) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .getAbs(this.url + "/account/" + address.plain() + "/namespaces" + (queryParams.isPresent() ? queryParams.get().toUrl() : ""))
                        .map(Http::mapStringOrError)
                        .map(str -> objectMapper.<List<NamespaceInfoDTO>>readValue(str, new TypeReference<List<NamespaceInfoDTO>>() {
                        }))
                        .flatMapIterable(item -> item)
                        .map(namespaceInfoDTO -> new NamespaceInfo(namespaceInfoDTO.getMeta().isActive(),
                                namespaceInfoDTO.getMeta().getIndex(),
                                namespaceInfoDTO.getMeta().getId(),
                                NamespaceType.rawValueOf(namespaceInfoDTO.getNamespace().getType()),
                                namespaceInfoDTO.getNamespace().getDepth(),
                                extractLevels(namespaceInfoDTO),
                                new NamespaceId(namespaceInfoDTO.getNamespace().getParentId().extractIntArray()),
                                new PublicAccount(namespaceInfoDTO.getNamespace().getOwner(), networkType),
                                namespaceInfoDTO.getNamespace().getStartHeight().extractIntArray(),
                                namespaceInfoDTO.getNamespace().getEndHeight().extractIntArray()
                        ))
                        .toList()
                        .toObservable());
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccounts(List<Address> addresses, QueryParams queryParams) {
        return this.getNamespacesFromAccounts(addresses, Optional.of(queryParams));
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccounts(List<Address> addresses) {
        return this.getNamespacesFromAccounts(addresses, Optional.empty());
    }

    private Observable<List<NamespaceInfo>> getNamespacesFromAccounts(List<Address> addresses, Optional<QueryParams> queryParams) {
        JsonObject requestBody = new JsonObject();
        JsonArray addressesJsonArray = new JsonArray();
        for(Address address : addresses) {
            addressesJsonArray.add(address.plain());
        }

        requestBody.add("addresses", addressesJsonArray);
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .postAbs(this.url + "/account/namespaces" + (queryParams.isPresent() ? queryParams.get().toUrl() : ""), requestBody)
                        .map(Http::mapStringOrError)
                        .map(json -> objectMapper.<List<NamespaceInfoDTO>>readValue(json.toString(), new TypeReference<List<NamespaceInfoDTO>>() {
                        }))
                        .flatMapIterable(item -> item)
                        .map(namespaceInfoDTO -> new NamespaceInfo(namespaceInfoDTO.getMeta().isActive(),
                                namespaceInfoDTO.getMeta().getIndex(),
                                namespaceInfoDTO.getMeta().getId(),
                                NamespaceType.rawValueOf(namespaceInfoDTO.getNamespace().getType()),
                                namespaceInfoDTO.getNamespace().getDepth(),
                                extractLevels(namespaceInfoDTO),
                                new NamespaceId(namespaceInfoDTO.getNamespace().getParentId().extractIntArray()),
                                new PublicAccount(namespaceInfoDTO.getNamespace().getOwner(), networkType),
                                namespaceInfoDTO.getNamespace().getStartHeight().extractIntArray(),
                                namespaceInfoDTO.getNamespace().getEndHeight().extractIntArray()
                        ))
                        .toList()
                        .toObservable());
    }

    @Override
    public Observable<List<NamespaceName>> getNamespaceNames(List<NamespaceId> namespaceIds) {
        JsonObject requestBody = new JsonObject();
        JsonArray namespaceIdJsonArray = new JsonArray();
        for(NamespaceId namespaceId: namespaceIds) {
            namespaceIdJsonArray.add(UInt64.bigIntegerToHex(namespaceId.getId()));
        }

        requestBody.add("namespaceIds", namespaceIdJsonArray);
        return this.client
                .postAbs(this.url + "/namespace/names", requestBody)
                .map(Http::mapStringOrError)
                .map(json -> objectMapper.<List<NamespaceNameDTO>>readValue(json, new TypeReference<List<NamespaceNameDTO>>() {
                }))
                .flatMapIterable(item -> item)
                .map(namespaceNameDTO -> {
                    if (namespaceNameDTO.getParentId() != null) {
                        return new NamespaceName(
                                new NamespaceId(namespaceNameDTO.getNamespaceId().extractIntArray()),
                                namespaceNameDTO.getName(),
                                new NamespaceId(namespaceNameDTO.getParentId().extractIntArray()));
                    } else {
                        return new NamespaceName(
                                new NamespaceId(namespaceNameDTO.getNamespaceId().extractIntArray()),
                                namespaceNameDTO.getName());
                    }
                })
                .toList()
                .toObservable();
    }

    private List<NamespaceId> extractLevels(NamespaceInfoDTO namespaceInfoDTO) {
        List<NamespaceId> levels = new ArrayList<NamespaceId>();
        if (namespaceInfoDTO.getNamespace().getLevel0() != null) {
            levels.add(new NamespaceId(namespaceInfoDTO.getNamespace().getLevel0().extractIntArray()));
        }

        if (namespaceInfoDTO.getNamespace().getLevel1() != null) {
            levels.add(new NamespaceId(namespaceInfoDTO.getNamespace().getLevel1().extractIntArray()));
        }

        if (namespaceInfoDTO.getNamespace().getLevel2() != null) {
            levels.add(new NamespaceId(namespaceInfoDTO.getNamespace().getLevel2().extractIntArray()));
        }

        return levels;
    }

}
