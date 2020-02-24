/*
 * Copyright 2019 NEM
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
package io.nem.sdk.api;

import io.nem.sdk.model.blockchain.ServerInfo;
import io.nem.sdk.model.blockchain.StorageInfo;
import io.nem.sdk.model.node.NodeHealth;
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeTime;
import io.reactivex.Observable;
import java.util.List;

public interface NodeRepository {

    /**
     * Supplies additional information about the application running on a node.
     *
     * @return Get the node information
     */
    Observable<NodeInfo> getNodeInfo();

    /**
     * Get node info of the pears visible by the node.
     *
     * @return {@link Observable} of a list of {@link NodeInfo}
     */
    Observable<List<NodeInfo>> getNodePeers();

    /**
     * Gets the node time at the moment the reply was sent and received.
     *
     * @return Get the node time
     */
    Observable<NodeTime> getNodeTime();

    /**
     * Gets node storage info.
     *
     * @return Observable of {@link StorageInfo}
     */
    Observable<StorageInfo> getNodeStorage();

    /**
     * Get node health information
     *
     * @return {@link NodeHealth} of NodeHealth
     */
    Observable<NodeHealth> getNodeHealth();

    /**
     * Gets node server info.
     *
     * @return {@link Observable} of ServerInfo
     */
    Observable<ServerInfo> getServerInfo();


}
