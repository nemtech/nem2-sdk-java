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

package io.nem.sdk.api;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrency;

/**
 * This bean helps the user to create {@link RepositoryFactory}.
 *
 * The only required attribute is the network base url. If the user knows the other values, they can
 * be provided to allow a better offline capability. If the values are not provided, the {@link
 * RepositoryFactory} will load and cache them using the repositories
 *
 * @see RepositoryFactory
 * @see io.nem.sdk.infrastructure.RepositoryFactoryBase
 */
public class RepositoryFactoryConfiguration {

    /**
     * The required base url of the network.
     */
    private final String baseUrl;

    /**
     * The known network type. If not provided, the value will be retrieved from rest.
     */
    private NetworkType networkType;

    /**
     * The known generation hash. If not provided, the value will be retrieved from rest.
     */
    private String generationHash;

    /**
     * The known network currency. If not provided, the value will be retrieved from rest.
     */
    private NetworkCurrency networkCurrency;

    /**
     * The known harvest currency. If not provided, the value will be retrieved from rest.
     */
    private NetworkCurrency harvestCurrency;

    /**
     * It creates a basic configuration with the required base url.
     *
     * @param baseUrl the base url.
     */
    public RepositoryFactoryConfiguration(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Helper method to setup the networkType when don't want to load it using rest.
     *
     * @param networkType the networkType
     * @return this configuration.
     */
    public RepositoryFactoryConfiguration withNetworkType(NetworkType networkType) {
        this.networkType = networkType;
        return this;
    }

    /**
     * Helper method to setup the main {@link NetworkCurrency} when don't want to load it using
     * rest.
     *
     * @param networkCurrency the main {@link NetworkCurrency}
     * @return this configuration.
     */
    public RepositoryFactoryConfiguration withNetworkCurrency(
        NetworkCurrency networkCurrency) {
        this.networkCurrency = networkCurrency;
        return this;
    }

    /**
     * Helper method to setup the generationHash when don't want to load it using rest.
     *
     * @param generationHash the generationHash
     * @return this configuration.
     */
    public RepositoryFactoryConfiguration withGenerationHash(String generationHash) {
        this.generationHash = generationHash;
        return this;
    }

    /**
     * Helper method to setup the harvest {@link NetworkCurrency} when don't want to load it using
     * rest.
     *
     * @param harvestCurrency the harvest {@link NetworkCurrency}
     * @return this configuration.
     */
    public RepositoryFactoryConfiguration withHarvestCurrency(
        NetworkCurrency harvestCurrency) {
        this.harvestCurrency = harvestCurrency;
        return this;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public String getGenerationHash() {
        return generationHash;
    }

    public NetworkCurrency getNetworkCurrency() {
        return networkCurrency;
    }

    public NetworkCurrency getHarvestCurrency() {
        return harvestCurrency;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public void setNetworkCurrency(
        NetworkCurrency networkCurrency) {
        this.networkCurrency = networkCurrency;
    }

    public void setGenerationHash(String generationHash) {
        this.generationHash = generationHash;
    }

    public void setHarvestCurrency(
        NetworkCurrency harvestCurrency) {
        this.harvestCurrency = harvestCurrency;
    }
}
