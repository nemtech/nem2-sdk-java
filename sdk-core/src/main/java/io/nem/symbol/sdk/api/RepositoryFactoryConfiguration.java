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
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.infrastructure.RepositoryFactoryBase;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.time.Duration;

/**
 * This bean helps the user to create {@link RepositoryFactory}.
 *
 * <p>The only required attribute is the network base url. If the user knows the other values, they
 * can be provided to allow a better offline capability. If the values are not provided, the {@link
 * RepositoryFactory} will load and cache them using the repositories
 *
 * @see RepositoryFactory
 * @see RepositoryFactoryBase
 */
public class RepositoryFactoryConfiguration {

  /** The required base url of the network. */
  private final String baseUrl;

  /** The known network type. If not provided, the value will be retrieved from rest. */
  private NetworkType networkType;

  /** The known generation hash. If not provided, the value will be retrieved from rest. */
  private String generationHash;

  /** The server epochAdjustment. If not provided, the value will be retrieved from rest. /* */
  private Duration epochAdjustment;

  /** The known network currencies. If not provided, the value will be retrieved from rest. */
  private NetworkCurrencies networkCurrencies;

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
   * Helper method to setup the {@link NetworkCurrencies} when don't want to load it using rest.
   *
   * @param networkCurrencies the {@link NetworkCurrencies}
   * @return this configuration.
   */
  public RepositoryFactoryConfiguration withNetworkCurrencies(NetworkCurrencies networkCurrencies) {
    this.networkCurrencies = networkCurrencies;
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
   * Helper method to setup the epochAdjustment when don't want to load it using rest.
   *
   * @param epochAdjustment the configured epochAdjustment
   * @return this configuration.
   */
  public RepositoryFactoryConfiguration withEpochAdjustment(Duration epochAdjustment) {
    this.epochAdjustment = epochAdjustment;
    return this;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public NetworkType getNetworkType() {
    return networkType;
  }

  public void setNetworkType(NetworkType networkType) {
    this.networkType = networkType;
  }

  public String getGenerationHash() {
    return generationHash;
  }

  public void setGenerationHash(String generationHash) {
    this.generationHash = generationHash;
  }

  public Duration getEpochAdjustment() {
    return epochAdjustment;
  }

  public void setEpochAdjustment(Duration epochAdjustment) {
    this.epochAdjustment = epochAdjustment;
  }

  public NetworkCurrencies getNetworkCurrencies() {
    return networkCurrencies;
  }

  public void setNetworkCurrencies(NetworkCurrencies networkCurrencies) {
    this.networkCurrencies = networkCurrencies;
  }
}
