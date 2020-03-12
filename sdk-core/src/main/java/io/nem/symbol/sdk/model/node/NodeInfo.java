/*
 * Copyright 2020 NEM
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
package io.nem.symbol.sdk.model.node;

import io.nem.symbol.sdk.model.network.NetworkType;

public class NodeInfo {

    private final String publicKey;
    private final int port;
    private final NetworkType networkIdentifier;
    private final int version;
    private final RoleType roles;
    private final String host;
    private final String friendlyName;
    private final String networkGenerationHash;

    public NodeInfo(
        String publicKey,
        int port,
        NetworkType networkIdentifier,
        int version,
        RoleType roles,
        String host,
        String friendlyName, String networkGenerationHash) {
        this.friendlyName = friendlyName;
        this.host = host;
        this.networkIdentifier = networkIdentifier;
        this.publicKey = publicKey;
        this.port = port;
        this.version = version;
        this.roles = roles;
        this.networkGenerationHash = networkGenerationHash;
    }

    /**
     * The port used for the communication.
     *
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * The version of the application.
     *
     * @return int
     */
    public int getVersion() {
        return version;
    }

    /**
     * The network identifier.
     *
     * @return {@link NetworkType}
     */
    public NetworkType getNetworkIdentifier() {
        return networkIdentifier;
    }

    /**
     * The roles of the application.
     *
     * @return {@link RoleType}
     */
    public RoleType getRoles() {
        return roles;
    }

    /**
     * The name of the node.
     *
     * @return String
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * The public key used to identify the node.
     *
     * @return String
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * The IP address of the endpoint.
     *
     * @return String
     */
    public String getHost() {
        return host;
    }


    /**
     * The network generation hash (block/1 generation hash)
     *
     * @return The network generation hash (block/1 generation hash)
     */
    public String getNetworkGenerationHash() {
        return networkGenerationHash;
    }
}
