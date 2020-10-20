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
import io.nem.symbol.sdk.api.CurrencyService;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.CurrencyBuilder;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencies;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.reactivex.Observable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;

/** Implementation of {@link CurrencyService} */
public class CurrencyServiceImpl implements CurrencyService {

  /** the @{@link NetworkRepository} */
  private final NetworkRepository networkRepository;

  /** The {@link MosaicRepository}. */
  private final MosaicRepository mosaicRepository;

  /** The {@link NamespaceRepository}. */
  private final NamespaceRepository namespaceRepository;

  /**
   * Constructor.
   *
   * @param repositoryFactory the repository factory.
   */
  public CurrencyServiceImpl(RepositoryFactory repositoryFactory) {
    this.networkRepository = repositoryFactory.createNetworkRepository();
    this.mosaicRepository = repositoryFactory.createMosaicRepository();
    this.namespaceRepository = repositoryFactory.createNamespaceRepository();
  }

  @Override
  public Observable<NetworkCurrencies> getNetworkCurrencies() {
    return this.networkRepository
        .getNetworkProperties()
        .flatMap(
            properties -> {
              if (properties.getChain() == null
                  || properties.getChain().getCurrencyMosaicId() == null) {
                return Observable.error(
                    new IllegalArgumentException(
                        "CurrencyMosaicId could not be loaded from network properties!!!"));
              }
              if (properties.getChain() == null
                  || properties.getChain().getHarvestingMosaicId() == null) {
                return Observable.error(
                    new IllegalArgumentException(
                        "HarvestingMosaicId could not be loaded from network properties!!"));
              }
              MosaicId currencyMosaicId =
                  new MosaicId(
                      FormatUtils.toSimpleHex(properties.getChain().getCurrencyMosaicId()));
              MosaicId harvestingMosaicId =
                  new MosaicId(
                      FormatUtils.toSimpleHex(properties.getChain().getHarvestingMosaicId()));
              List<MosaicId> mosaicIds =
                  currencyMosaicId.equals(harvestingMosaicId)
                      ? Collections.singletonList(currencyMosaicId)
                      : Arrays.asList(currencyMosaicId, harvestingMosaicId);
              return this.getCurrencies(mosaicIds)
                  .map(
                      currencies -> {
                        Currency currency =
                            currencies.stream()
                                .filter(
                                    c ->
                                        c.getMosaicId()
                                            .filter(mosaicId -> mosaicId.equals(currencyMosaicId))
                                            .isPresent())
                                .findFirst()
                                .orElseThrow(
                                    () ->
                                        new IllegalArgumentException(
                                            "There is no Main Currency with id "
                                                + currencyMosaicId));

                        Currency harvest =
                            currencies.stream()
                                .filter(
                                    c ->
                                        c.getMosaicId()
                                            .filter(mosaicId -> mosaicId.equals(harvestingMosaicId))
                                            .isPresent())
                                .findFirst()
                                .orElseThrow(
                                    () ->
                                        new IllegalArgumentException(
                                            "There is no Harvest Currency with id "
                                                + harvestingMosaicId));
                        return new NetworkCurrencies(currency, harvest);
                      });
            });
  }

  @Override
  public Observable<Currency> getCurrency(MosaicId mosaicId) {
    return getCurrencies(Collections.singletonList(mosaicId))
        .map(
            list ->
                list.stream()
                    .findFirst()
                    .orElseThrow(
                        () ->
                            new IllegalArgumentException(
                                "There is no currency with id " + mosaicId)));
  }

  @Override
  public Observable<List<Currency>> getCurrencies(List<MosaicId> mosaicIds) {
    Validate.notNull(mosaicIds, "mosaicIds is required");
    return this.mosaicRepository
        .getMosaics(mosaicIds)
        .flatMap(
            mosaicInfos ->
                this.namespaceRepository
                    .getMosaicsNames(mosaicIds)
                    .onErrorReturnItem(Collections.emptyList())
                    .map(
                        mosaicNames ->
                            mosaicInfos.stream()
                                .map(mosaicInfo -> getCurrency(mosaicInfo, mosaicNames))
                                .collect(Collectors.toList())));
  }

  @Override
  public Observable<Currency> getCurrencyFromNamespaceId(NamespaceId namespaceId) {
    Validate.notNull(namespaceId, "namespaceId is required");
    return this.namespaceRepository
        .getLinkedMosaicId(namespaceId)
        .flatMap(
            mosaicId ->
                this.mosaicRepository
                    .getMosaic(mosaicId)
                    .flatMap(
                        info ->
                            namespaceRepository
                                .getMosaicsNames(Collections.singletonList(mosaicId))
                                .onErrorReturnItem(Collections.emptyList())
                                .map(mosaicNames -> getCurrency(info, mosaicNames))));
  }

  private CurrencyBuilder createCurrency(MosaicInfo mosaicInfo, Optional<NamespaceId> namespaceId) {
    UnresolvedMosaicId unresolvedMosaicId = mosaicInfo.getMosaicId();
    CurrencyBuilder builder =
        new CurrencyBuilder(unresolvedMosaicId, mosaicInfo.getDivisibility())
            .withMosaicId(mosaicInfo.getMosaicId())
            .withSupplyMutable(mosaicInfo.isSupplyMutable())
            .withTransferable(mosaicInfo.isTransferable())
            .withRestrictable(mosaicInfo.isRestrictable());
    namespaceId.ifPresent(builder::withNamespaceId);
    return builder;
  }

  /** Creates a network currency model given mosaic info and mosaic names */
  private Currency getCurrency(MosaicInfo mosaicInfo, List<MosaicNames> mosaicNamesList) {
    MosaicId mosaicId = mosaicInfo.getMosaicId();
    Optional<String> namespaceName = getName(mosaicNamesList, mosaicId);
    Optional<NamespaceId> namespaceId = namespaceName.map(NamespaceId::createFromName);
    return createCurrency(mosaicInfo, namespaceId).build();
  }

  private Optional<String> getName(List<MosaicNames> mosaicNames, MosaicId accountMosaicDto) {
    return mosaicNames.stream()
        .filter((n) -> n.getMosaicId().equals(accountMosaicDto))
        .flatMap((n) -> n.getNames().stream().map(NamespaceName::getName))
        .findFirst();
  }
}
