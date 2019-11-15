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
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeTime;
import io.nem.sdk.model.node.RoleType;
import io.nem.sdk.openapi.vertx.api.NodeRoutesApi;
import io.nem.sdk.openapi.vertx.api.NodeRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.NodeInfoDTO;
import io.nem.sdk.openapi.vertx.model.NodeTimeDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.function.Consumer;

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

}
