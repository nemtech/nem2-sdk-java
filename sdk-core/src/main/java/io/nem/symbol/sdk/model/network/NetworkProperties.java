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




package io.nem.symbol.sdk.model.network;

public class NetworkProperties {

    /**
     * Network identifier.
     */
    private final String identifier;

    /**
     * Get nodeEqualityStrategy
     */
    private final NodeIdentityEqualityStrategy nodeEqualityStrategy;

    /**
     * Public key.
     */
    private final String nemesisSignerPublicKey;

    /**
     * Get generationHash
     */
    private final String generationHashSeed;

    /**
     * Nemesis epoch time adjustment.
     */
    private final String epochAdjustment;

    public NetworkProperties(String identifier,
        NodeIdentityEqualityStrategy nodeEqualityStrategy, String nemesisSignerPublicKey,
        String generationHashSeed, String epochAdjustment) {
        this.identifier = identifier;
        this.nodeEqualityStrategy = nodeEqualityStrategy;
        this.nemesisSignerPublicKey = nemesisSignerPublicKey;
        this.generationHashSeed = generationHashSeed;
        this.epochAdjustment = epochAdjustment;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getNemesisSignerPublicKey() {
        return nemesisSignerPublicKey;
    }

    public NodeIdentityEqualityStrategy getNodeEqualityStrategy() {
        return nodeEqualityStrategy;
    }

    public String getGenerationHashSeed() {
        return generationHashSeed;
    }

    public String getEpochAdjustment() {
        return epochAdjustment;
    }
}

