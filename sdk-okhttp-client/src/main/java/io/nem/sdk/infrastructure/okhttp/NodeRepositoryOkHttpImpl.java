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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeTime;
import io.nem.sdk.model.node.RoleType;
import io.nem.sdk.openapi.okhttp_gson.api.NodeRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.NodeInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NodeTimeDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.concurrent.Callable;

/**
 * Node http repository.
 */
public class NodeRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    NodeRepository {

    private final NodeRoutesApi client;

    public NodeRepositoryOkHttpImpl(ApiClient apiClient, SignSchema signSchema) {
        super(apiClient, signSchema);
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

    private NodeTime toNodeTime(NodeTimeDTO nodeTimeDTO) {
        BigInteger sendTimeStamp = (
            nodeTimeDTO.getCommunicationTimestamps().getSendTimestamp());
        BigInteger receiveTimeStamp = (
            nodeTimeDTO.getCommunicationTimestamps().getReceiveTimestamp());
        return new NodeTime(
            sendTimeStamp,
            receiveTimeStamp);
    }

}
