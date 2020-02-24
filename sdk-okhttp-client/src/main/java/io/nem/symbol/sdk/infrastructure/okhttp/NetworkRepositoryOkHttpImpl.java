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

import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.model.blockchain.NetworkFees;
import io.nem.symbol.sdk.model.blockchain.NetworkInfo;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.NetworkRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.NodeRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.reactivex.Observable;

/**
 * Created by fernando on 30/07/19.
 *
 * @author Fernando Boucquez
 */
public class NetworkRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    NetworkRepository {

    private final NetworkRoutesApi networkRoutesApi;

    private final NodeRoutesApi nodeRoutesApi;

    public NetworkRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        networkRoutesApi = new NetworkRoutesApi(apiClient);
        nodeRoutesApi = new NodeRoutesApi(apiClient);
    }

    @Override
    public Observable<NetworkType> getNetworkType() {
        return exceptionHandling(
            call(getNodeRoutesApi()::getNodeInfo)
                .map(info -> NetworkType.rawValueOf(info.getNetworkIdentifier())));
    }

    @Override
    public Observable<NetworkInfo> getNetworkInfo() {
        return exceptionHandling(
            call(getNetworkRoutesApi()::getNetworkType)
                .map(info -> new NetworkInfo(info.getName(), info.getDescription())));
    }

    @Override
    public Observable<NetworkFees> getNetworkFees() {
        return exceptionHandling(
            call(getNetworkRoutesApi()::getNetworkFees)
                .map(info -> new NetworkFees(info.getAverageFeeMultiplier(),
                    info.getMedianFeeMultiplier(), info.getLowestFeeMultiplier(), info.getHighestFeeMultiplier()
                )));
    }


    public NetworkRoutesApi getNetworkRoutesApi() {
        return networkRoutesApi;
    }

    public NodeRoutesApi getNodeRoutesApi() {
        return nodeRoutesApi;
    }
}
