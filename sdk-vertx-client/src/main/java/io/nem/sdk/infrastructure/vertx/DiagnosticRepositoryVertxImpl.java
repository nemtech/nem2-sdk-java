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

import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.blockchain.ServerInfo;
import io.nem.sdk.openapi.vertx.api.DiagnosticRoutesApi;
import io.nem.sdk.openapi.vertx.api.DiagnosticRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.ServerDTO;
import io.nem.sdk.openapi.vertx.model.ServerInfoDTO;
import io.nem.sdk.openapi.vertx.model.StorageInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Diagnostic http repository.
 */
public class DiagnosticRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    DiagnosticRepository {

    private final DiagnosticRoutesApi client;

    public DiagnosticRepositoryVertxImpl(ApiClient apiClient, Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
        client = new DiagnosticRoutesApiImpl(apiClient);
    }

    public DiagnosticRoutesApi getClient() {
        return client;
    }


    /**
     * Get storage info
     *
     * @return io.reactivex.Observable of {@link BlockchainStorageInfo}
     */
    public Observable<BlockchainStorageInfo> getBlockchainStorage() {
        Consumer<Handler<AsyncResult<StorageInfoDTO>>> callback = getClient()::getDiagnosticStorage;
        return exceptionHandling(call(callback).map(this::toBlockchainStorageInfo));
    }

    private BlockchainStorageInfo toBlockchainStorageInfo(StorageInfoDTO blockchainStorageInfoDTO) {
        return new BlockchainStorageInfo(
            blockchainStorageInfoDTO.getNumAccounts(),
            blockchainStorageInfoDTO.getNumBlocks(),
            blockchainStorageInfoDTO.getNumTransactions());
    }

    /**
     * Get server info
     *
     * @return Observable of {@link ServerInfo}
     */
    public Observable<ServerInfo> getServerInfo() {
        Consumer<Handler<AsyncResult<ServerInfoDTO>>> callback = getClient()::getServerInfo;
        return exceptionHandling(
            call(callback).map(ServerInfoDTO::getServerInfo).map(this::toServerInfo));
    }

    private ServerInfo toServerInfo(ServerDTO serverInfoDTO) {
        return new ServerInfo(serverInfoDTO.getRestVersion(), serverInfoDTO.getSdkVersion());
    }
}
