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

import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.ServerInfo;
import io.nem.sdk.openapi.okhttp_gson.api.DiagnosticRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.ServerDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ServerInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.StorageInfoDTO;
import io.reactivex.Observable;
import java.util.concurrent.Callable;

/**
 * Diagnostic http repository.
 */
public class DiagnosticRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    DiagnosticRepository {

    private final DiagnosticRoutesApi client;

    public DiagnosticRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new DiagnosticRoutesApi(apiClient);
    }

    /**
     * Get storage info
     *
     * @return {@link Observable} of BlockchainStorageInfo
     */
    public Observable<BlockchainStorageInfo> getBlockchainStorage() {
        Callable<StorageInfoDTO> callback = getClient()::getDiagnosticStorage;
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
     * @return {@link Observable} of ServerInfo
     */
    public Observable<ServerInfo> getServerInfo() {
        Callable<ServerInfoDTO> callback = getClient()::getServerInfo;
        return exceptionHandling(
            call(callback).map(ServerInfoDTO::getServerInfo).map(this::toServerInfo));
    }

    private ServerInfo toServerInfo(ServerDTO serverInfoDTO) {
        return new ServerInfo(serverInfoDTO.getRestVersion(), serverInfoDTO.getSdkVersion());
    }

    public DiagnosticRoutesApi getClient() {
        return client;
    }
}
