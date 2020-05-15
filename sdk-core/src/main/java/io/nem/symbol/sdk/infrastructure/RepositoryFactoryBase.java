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

import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RepositoryFactoryConfiguration;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencyBuilder;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.node.NodeInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;

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
    private final Observable<String> generationHashSeed;

    /**
     * The cached remote network configuration.
     */
    private final Observable<NetworkConfiguration> remoteNetworkConfiguration;

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

        this.generationHashSeed = createLazyObservable(configuration.getGenerationHash(),
            () -> createNodeRepository().getNodeInfo()
                .map(NodeInfo::getNetworkGenerationHashSeed));

        this.remoteNetworkConfiguration = Observable
            .defer(() -> createNetworkRepository().getNetworkProperties()).cache();

        this.networkCurrency = createLazyObservable(configuration.getNetworkCurrency(),
            this::loadNetworkCurrency);

        this.harvestCurrency = createLazyObservable(configuration.getHarvestCurrency(),
            this::loadHarvestCurrency);
    }

    protected Observable<NetworkCurrency> loadHarvestCurrency() {
        return this.remoteNetworkConfiguration.flatMap(cs -> {
            if (cs == null || cs.getChain() == null
                || cs.getChain().getHarvestingMosaicId() == null) {
                return this.networkCurrency;
            }
            if (cs.getChain().getHarvestingMosaicId()
                .equals(cs.getChain().getCurrencyMosaicId())) {
                return this.networkCurrency;
            }
            return getNetworkCurrency(cs.getChain().getHarvestingMosaicId());
        });
    }

    protected Observable<NetworkCurrency> loadNetworkCurrency() {
        return this.remoteNetworkConfiguration.flatMap(cs -> {
            if (cs == null || cs.getChain() == null
                || cs.getChain().getCurrencyMosaicId() == null) {
                return Observable.error(
                    new IllegalStateException(
                        "No currency could be found in the network configuration."));
            }
            return getNetworkCurrency(cs.getChain().getCurrencyMosaicId());
        });
    }

    protected ObservableSource<NetworkCurrency> getNetworkCurrency(String mosaicIdHex) {
        MosaicId mosaicId = new MosaicId(
            StringUtils.removeStart(mosaicIdHex.replace("'", "").substring(2), "0x"));
        return createMosaicRepository().getMosaic(mosaicId)
            .map(mosaicInfo -> new NetworkCurrencyBuilder(mosaicInfo.getMosaicId(),
                mosaicInfo.getDivisibility()).withTransferable(mosaicInfo.isTransferable())
                .withSupplyMutable(mosaicInfo.isSupplyMutable()))
            .flatMap(builder -> createNamespaceRepository()
                .getMosaicsNames(Collections.singletonList(mosaicId)).map(
                    names -> names.stream().filter(n -> n.getMosaicId().equals(mosaicId))
                        .findFirst()
                        .map(name -> builder.withNamespaceId(getNamespaceId(names)).build())
                        .orElse(builder.build())));
    }

    private NamespaceId getNamespaceId(List<MosaicNames> names) {
        NamespaceName namespaceName = names.get(0).getNames().get(0);
        NamespaceId namespaceId = namespaceName.getNamespaceId();
        return NamespaceId
            .createFromIdAndFullName(namespaceId.getId(), namespaceName.getName());
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
        return generationHashSeed;
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
