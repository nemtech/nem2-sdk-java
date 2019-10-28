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

import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.openapi.okhttp_gson.api.NetworkRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.reactivex.Observable;

/**
 * Created by fernando on 30/07/19.
 *
 * @author Fernando Boucquez
 */
public class NetworkRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    NetworkRepository {

    // Cannot use network route yet.
    // https://github.com/nemtech/nem2-openapi/issues/43
    private final NetworkRoutesApi client;

    public NetworkRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new NetworkRoutesApi(apiClient);
    }

    @Override
    public Observable<NetworkType> getNetworkType() {
        return exceptionHandling(
            call(getClient()::getNetworkType)
                .map(info -> NetworkType.rawValueOf(info.getName().getValue())));
    }

    public NetworkRoutesApi getClient() {
        return client;
    }
}
