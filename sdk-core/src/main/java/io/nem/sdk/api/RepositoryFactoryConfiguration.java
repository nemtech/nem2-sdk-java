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
 * This beans
 */
public class RepositoryFactoryConfiguration {

    private final String baseUrl;

    private NetworkType networkType;

    private String generationHash;

    private NetworkCurrency networkCurrency;

    private NetworkCurrency harvestCurrency;

    public RepositoryFactoryConfiguration(String baseUrl) {
        this.baseUrl = baseUrl;
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


    public RepositoryFactoryConfiguration withNetworkType(NetworkType networkType) {
        this.networkType = networkType;
        return this;
    }

    public RepositoryFactoryConfiguration withNetworkCurrency(
        NetworkCurrency networkCurrency) {
        this.networkCurrency = networkCurrency;
        return this;
    }

    public RepositoryFactoryConfiguration withGenerationHash(String generationHash) {
        this.generationHash = generationHash;
        return this;
    }

    public RepositoryFactoryConfiguration withHarvestCurrency(
        NetworkCurrency harvestCurrency) {
        this.harvestCurrency = harvestCurrency;
        return this;
    }
}
