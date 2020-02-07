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

import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.blockchain.ServerInfo;
import io.nem.sdk.model.blockchain.StorageInfo;
import io.nem.sdk.model.node.NodeHealth;
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeStatus;
import io.nem.sdk.model.node.NodeTime;
import io.nem.sdk.model.node.RoleType;
import io.nem.sdk.openapi.okhttp_gson.api.NodeRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.NodeHealthInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NodeInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NodeTimeDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ServerDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ServerInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.StorageInfoDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.concurrent.Callable;

/**
 * Node http repository.
 */
public class NodeRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    NodeRepository {

    private final NodeRoutesApi client;

    public NodeRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new NodeRoutesApi(apiClient);
    }


    public NodeRoutesApi getClient() {
        return client;
    }

    /**
     * Get node info
     *
     * @return {@link Observable} of NodeInfo
     */
    public Observable<NodeInfo> getNodeInfo() {
        return exceptionHandling(call(getClient()::getNodeInfo).map(this::toNodeInfo));
    }

    private NodeInfo toNodeInfo(NodeInfoDTO nodeInfoDTO) {
        return new NodeInfo(
            nodeInfoDTO.getPublicKey(),
            nodeInfoDTO.getPort(),
            NetworkType.rawValueOf(nodeInfoDTO.getNetworkIdentifier()),
            nodeInfoDTO.getVersion(),
            RoleType.rawValueOf(nodeInfoDTO.getRoles().getValue()),
            nodeInfoDTO.getHost(),
            nodeInfoDTO.getFriendlyName());
    }

    /**
     * Get node time
     *
     * @return {@link Observable} of NodeTime
     */
    public Observable<NodeTime> getNodeTime() {
        Callable<NodeTimeDTO> callback = () -> getClient()
            .getNodeTime();
        return exceptionHandling(
            call(callback).map(this::toNodeTime));
    }

    /**
     * Get storage info
     *
     * @return {@link Observable} of StorageInfo
     */
    @Override
    public Observable<StorageInfo> getNodeStorage() {
        Callable<StorageInfoDTO> callback = getClient()::getNodeStorage;
        return exceptionHandling(call(callback).map(this::toStorageInfo));
    }

    /**
     * Get node health information
     *
     * @return {@link NodeHealth} of NodeHealth
     */
    @Override
    public Observable<NodeHealth> getNodeHealth() {
        Callable<NodeHealthInfoDTO> callback = getClient()::getNodeHealth;
        return exceptionHandling(call(callback)
            .map(dto -> new NodeHealth(
                NodeStatus.rawValueOf(dto.getStatus().getApiNode().getValue()),
                NodeStatus.rawValueOf(dto.getStatus().getDb().getValue()))));
    }


    private StorageInfo toStorageInfo(StorageInfoDTO storageInfoDTO) {
        return new StorageInfo(
            storageInfoDTO.getNumAccounts(),
            storageInfoDTO.getNumBlocks(),
            storageInfoDTO.getNumTransactions());
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


    private NodeTime toNodeTime(NodeTimeDTO nodeTimeDTO) {
        BigInteger sendTimestamp = (
            nodeTimeDTO.getCommunicationTimestamps().getSendTimestamp());
        BigInteger receiveTimestamp = (
            nodeTimeDTO.getCommunicationTimestamps().getReceiveTimestamp());
        return new NodeTime(sendTimestamp, receiveTimestamp);
    }

}
