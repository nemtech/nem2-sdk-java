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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.utils.FormatUtils;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RepositoryFactoryConfiguration;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.node.NodeInfo;
import io.reactivex.Observable;
import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * Base class of all the {@link RepositoryFactory}. It handles common functions like resolving
 * configuration.
 */
public abstract class RepositoryFactoryBase implements RepositoryFactory {

  /** The base url of the network. This observable is lazy (cold) and cached. */
  private final String baseUrl;

  /** The resolved network type. This observable is lazy (cold) and cached. */
  private final Observable<NetworkType> networkType;

  /** The resolved generation hash. This observable is lazy (cold) and cached. */
  private final Observable<String> generationHashSeed;

  /** The cached remote network configuration. */
  private final Observable<NetworkConfiguration> remoteNetworkConfiguration;

  /** The resolved network currency . This observable is lazy (cold) and cached. */
  private final Observable<NetworkCurrencies> networkCurrencies;

  /** The resolved epochAdjustment. This observable is lazy (cold) and cached. */
  private final Observable<Duration> epochAdjustment;

  /** @param configuration the user provided configuration. */
  public RepositoryFactoryBase(RepositoryFactoryConfiguration configuration) {
    this.baseUrl = configuration.getBaseUrl();

    this.networkType =
        createLazyObservable(
            configuration.getNetworkType(), () -> createNetworkRepository().getNetworkType());

    this.generationHashSeed =
        createLazyObservable(
            configuration.getGenerationHash(),
            () -> createNodeRepository().getNodeInfo().map(NodeInfo::getNetworkGenerationHashSeed));

    this.remoteNetworkConfiguration =
        Observable.defer(() -> createNetworkRepository().getNetworkProperties()).cache();

    this.networkCurrencies =
        createLazyObservable(configuration.getNetworkCurrencies(), this::loadNetworkCurrencies);

    this.epochAdjustment =
        createLazyObservable(configuration.getEpochAdjustment(), this::loadEpochAdjustment);
  }

  private static <T> Observable<T> createLazyObservable(
      T providedValue, Callable<Observable<T>> remoteObservable) {
    if (providedValue != null) {
      return Observable.just(providedValue);
    } else {
      return Observable.defer(remoteObservable).cache();
    }
  }

  protected Observable<NetworkCurrencies> loadNetworkCurrencies() {
    return new CurrencyServiceImpl(this).getNetworkCurrencies();
  }

  protected Observable<Duration> loadEpochAdjustment() {
    return this.remoteNetworkConfiguration.map(
        cs -> {
          if (cs.getNetwork() == null || cs.getNetwork().getEpochAdjustment() == null) {
            throw new IllegalStateException(
                "EpochAdjustment could not be loaded from Rest's network Properties.");
          } else {
            return FormatUtils.parserServerDuration(cs.getNetwork().getEpochAdjustment());
          }
        });
  }

  @Override
  public Observable<NetworkType> getNetworkType() {
    return networkType;
  }

  @Override
  public Observable<String> getGenerationHash() {
    return generationHashSeed;
  }

  protected String getBaseUrl() {
    return baseUrl;
  }

  @Override
  public Observable<Currency> getNetworkCurrency() {
    return this.getNetworkCurrencies().map(NetworkCurrencies::getCurrency);
  }

  @Override
  public Observable<Currency> getHarvestCurrency() {
    return this.getNetworkCurrencies().map(NetworkCurrencies::getHarvest);
  }

  @Override
  public Observable<NetworkCurrencies> getNetworkCurrencies() {
    return this.networkCurrencies;
  }

  @Override
  public Observable<Duration> getEpochAdjustment() {
    return epochAdjustment;
  }
}
