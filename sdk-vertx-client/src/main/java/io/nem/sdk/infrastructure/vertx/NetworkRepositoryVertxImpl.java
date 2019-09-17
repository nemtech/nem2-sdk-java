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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.openapi.vertx.api.NetworkRoutesApi;
import io.nem.sdk.openapi.vertx.api.NetworkRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.NetworkTypeDTO;
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

    private final NetworkRoutesApi client;

    public NetworkRepositoryVertxImpl(ApiClient apiClient, SignSchema signSchema) {
        super(apiClient, null, signSchema);
        client = new NetworkRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<NetworkType> getNetworkType() {
        Consumer<Handler<AsyncResult<NetworkTypeDTO>>> callback = client::getNetworkType;
        return exceptionHandling(
            call(callback).map(NetworkTypeDTO::getName).map(this::getNetworkType));
    }

    private NetworkType getNetworkType(String name) {
        if ("mijinTest".equalsIgnoreCase(name)) {
            return NetworkType.MIJIN_TEST;
        } else {
            throw new IllegalArgumentException(
                "network " + name + " is not supported yet by the sdk");
        }
    }
}
