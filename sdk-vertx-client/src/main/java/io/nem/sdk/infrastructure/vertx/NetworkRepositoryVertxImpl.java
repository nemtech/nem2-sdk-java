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

import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.openapi.vertx.api.NodeRoutesApi;
import io.nem.sdk.openapi.vertx.api.NodeRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.NodeInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.function.Consumer;

/**
 * Created by fernando on 30/07/19.
 *
 * @author Fernando Boucquez
 */
public class NetworkRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    NetworkRepository {

    // Cannot use network route yet.
    // https://github.com/nemtech/nem2-openapi/issues/43
    private final NodeRoutesApi client;

    public NetworkRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient, () -> {
            throw new IllegalStateException(
                "This service is in charge of loading the network type");
        });
        client = new NodeRoutesApiImpl(apiClient);
    }

    /**
     * The current network type.
     *
     * @return Observable of NodeTime
     */
    public Observable<NetworkType> getNetworkType() {
        Consumer<Handler<AsyncResult<NodeInfoDTO>>> callback = handler -> getClient()
            .getNodeInfo(handler);
        return exceptionHandling(
            call(callback).map(info -> NetworkType.rawValueOf(info.getNetworkIdentifier())));
    }


    public NodeRoutesApi getClient() {
        return client;
    }

}
