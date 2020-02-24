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

import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.blockchain.ServerInfo;
import io.nem.sdk.model.blockchain.StorageInfo;
import io.nem.sdk.model.node.NodeHealth;
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeStatus;
import io.nem.sdk.model.node.NodeTime;
import io.nem.sdk.model.node.RoleType;
import io.nem.symbol.sdk.openapi.vertx.api.NodeRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.NodeRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.NodeHealthInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NodeInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NodeTimeDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ServerDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ServerInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.StorageInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Node http repository.
 */
public class NodeRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    NodeRepository {

    private final NodeRoutesApi client;

    public NodeRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        client = new NodeRoutesApiImpl(apiClient);
    }


    public NodeRoutesApi getClient() {
        return client;
    }

    /**
     * Get node info
     *
     * @return Observable of NodeTime
     */
    public Observable<NodeInfo> getNodeInfo() {
        Consumer<Handler<AsyncResult<NodeInfoDTO>>> callback = handler -> getClient()
            .getNodeInfo(handler);
        return exceptionHandling(
            call(callback).map(this::toNodeInfo));
    }

    /**
     * Get node info of the pears visible by the node.
     *
     * @return {@link Observable} of a list of {@link NodeInfo}
     */
    @Override
    public Observable<List<NodeInfo>> getNodePeers() {
        Consumer<Handler<AsyncResult<List<NodeInfoDTO>>>> callback = handler -> getClient()
            .getNodePeers(handler);
        return exceptionHandling(
            call(callback).map(l -> l.stream().map(this::toNodeInfo).collect(
                Collectors.toList())));
    }

    private NodeInfo toNodeInfo(NodeInfoDTO nodeInfoDTO) {
        return new NodeInfo(
            nodeInfoDTO.getPublicKey(),
            nodeInfoDTO.getPort(),
            NetworkType.rawValueOf(nodeInfoDTO.getNetworkIdentifier()),
            nodeInfoDTO.getVersion(),
            RoleType.rawValueOf(nodeInfoDTO.getRoles().getValue()),
            nodeInfoDTO.getHost(),
            nodeInfoDTO.getFriendlyName(), nodeInfoDTO.getNetworkGenerationHash());
    }

    /**
     * Get node time
     *
     * @return Observable of NodeTime
     */
    public Observable<NodeTime> getNodeTime() {
        Consumer<Handler<AsyncResult<NodeTimeDTO>>> callback = handler -> getClient()
            .getNodeTime(handler);
        return exceptionHandling(
            call(callback).map(this::toNodeTime));
    }

    private NodeTime toNodeTime(NodeTimeDTO nodeTimeDTO) {
        BigInteger sendTimestamp = nodeTimeDTO.getCommunicationTimestamps().getSendTimestamp();
        BigInteger receiveTimestamp = nodeTimeDTO.getCommunicationTimestamps()
            .getReceiveTimestamp();
        return new NodeTime(sendTimestamp, receiveTimestamp);
    }

    /**
     * Get storage info
     *
     * @return io.reactivex.Observable of {@link StorageInfo}
     */
    @Override
    public Observable<StorageInfo> getNodeStorage() {
        Consumer<Handler<AsyncResult<StorageInfoDTO>>> callback = getClient()::getNodeStorage;
        return exceptionHandling(call(callback).map(this::toStorageInfo));
    }

    private StorageInfo toStorageInfo(StorageInfoDTO storageInfoDTO) {
        return new StorageInfo(
            storageInfoDTO.getNumAccounts(),
            storageInfoDTO.getNumBlocks(),
            storageInfoDTO.getNumTransactions());
    }

    /**
     * Get node health information
     *
     * @return {@link NodeHealth} of NodeHealth
     */
    @Override
    public Observable<NodeHealth> getNodeHealth() {
        Consumer<Handler<AsyncResult<NodeHealthInfoDTO>>> callback = getClient()::getNodeHealth;
        return exceptionHandling(call(callback).map(dto -> new NodeHealth(
            NodeStatus.rawValueOf(dto.getStatus().getApiNode().getValue()),
            NodeStatus.rawValueOf(dto.getStatus().getDb().getValue()))));
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
