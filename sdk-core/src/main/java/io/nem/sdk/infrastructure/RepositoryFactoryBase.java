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

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.NetworkCurrencyService;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.RepositoryFactoryConfiguration;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Base class of all the {@link RepositoryFactory}. It handles common functions like resolving
 * configuration.
 */
public abstract class RepositoryFactoryBase implements RepositoryFactory {

    /**
     * The base url of the network. This observable is lazy (cold) and cached.
     */
    private final String baseUrl;

    /**
     * The resolved network type. This observable is lazy (cold) and cached.
     */
    private final Observable<NetworkType> networkType;

    /**
     * The resolved generation hash. This observable is lazy (cold) and cached.
     */
    private final Observable<String> generationHash;

    /**
     * The cached remote network currencies resolved using {@link NetworkCurrencyService}. This
     * observable is lazy (cold) and cached.
     */
    private final Observable<List<NetworkCurrency>> remoteNetworkCurrencies;

    /**
     * The resolved network currency . This observable is lazy (cold) and cached.
     */
    private final Observable<NetworkCurrency> networkCurrency;

    /**
     * The resolved harvest currency. This observable is lazy (cold) and cached.
     */
    private final Observable<NetworkCurrency> harvestCurrency;

    /**
     * @param configuration the user provided configuration.
     */
    public RepositoryFactoryBase(RepositoryFactoryConfiguration configuration) {
        this.baseUrl = configuration.getBaseUrl();

        this.networkType = createLazyObservable(configuration.getNetworkType(),
            () -> createNetworkRepository().getNetworkType());

        this.generationHash = createLazyObservable(configuration.getGenerationHash(),
            () -> createBlockRepository().getBlockByHeight(BigInteger.ONE)
                .map(BlockInfo::getGenerationHash));

        this.remoteNetworkCurrencies = Observable
            .defer(() -> createNetworkCurrencyService()
                .getNetworkCurrencies()).cache();

        this.networkCurrency = createLazyObservable(
            configuration.getNetworkCurrency(),
            () -> this.remoteNetworkCurrencies.map(cs -> {
                if (cs.isEmpty()) {
                    throw new IllegalStateException("No currency could be found in the network.");
                }
                return cs.stream().filter(c -> !c.isSupplyMutable()).findFirst()
                    .orElse(cs.iterator().next());
            }));

        this.harvestCurrency = createLazyObservable(
            configuration.getHarvestCurrency(),
            () -> this.remoteNetworkCurrencies.map(cs -> {
                if (cs.isEmpty()) {
                    throw new IllegalStateException("No currency could be found in the network.");
                }
                return cs.stream().filter(NetworkCurrency::isSupplyMutable).findFirst()
                    .orElse(cs.iterator().next());
            }));
    }

    protected NetworkCurrencyService createNetworkCurrencyService() {
        return new NetworkCurrencyServiceImpl(this);
    }

    private static <T> Observable<T> createLazyObservable(T providedValue,
        Callable<Observable<T>> remoteObservable) {
        if (providedValue != null) {
            return Observable.just(providedValue);
        } else {
            return Observable.defer(remoteObservable).cache();
        }
    }

    @Override
    public Observable<NetworkType> getNetworkType() {
        return networkType;
    }

    @Override
    public Observable<String> getGenerationHash() {
        return generationHash;
    }

    protected String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public Observable<NetworkCurrency> getNetworkCurrency() {
        return networkCurrency;
    }

    @Override
    public Observable<NetworkCurrency> getHarvestCurrency() {
        return harvestCurrency;
    }
}
