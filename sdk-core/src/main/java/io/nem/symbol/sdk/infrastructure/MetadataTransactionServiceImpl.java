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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.AliasService;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.MetadataTransactionService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.MetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Implementation of {@link MetadataTransactionService}
 *
 * @author Ravi Shanker
 */
public class MetadataTransactionServiceImpl implements MetadataTransactionService {

  private final MetadataRepository metadataRepository;

  private final Observable<NetworkType> networkTypeObservable;

  private final AliasService aliasService;

  private final Observable<Duration> epochAdjustmentObservable;

  public MetadataTransactionServiceImpl(RepositoryFactory factory) {
    this.metadataRepository = factory.createMetadataRepository();
    this.networkTypeObservable = factory.getNetworkType();
    this.epochAdjustmentObservable = factory.getEpochAdjustment();
    this.aliasService = new AliasServiceImpl(factory);
  }

  @Override
  public Observable<AccountMetadataTransactionFactory> createAccountMetadataTransactionFactory(
      Address targetAddress, BigInteger key, String value, Address sourceAddress) {
    TriFunction<String, NetworkType, Deadline, AccountMetadataTransactionFactory> factory =
        (newValue, networkType, deadline) ->
            AccountMetadataTransactionFactory.create(
                networkType, deadline, targetAddress, key, newValue);

    return processMetadata(
        new MetadataSearchCriteria()
            .targetAddress(targetAddress)
            .scopedMetadataKey(key)
            .sourceAddress(sourceAddress)
            .metadataType(MetadataType.ACCOUNT),
        factory,
        value);
  }

  @Override
  public Observable<MosaicMetadataTransactionFactory> createMosaicMetadataTransactionFactory(
      Address targetAddress,
      BigInteger key,
      String value,
      Address sourceAddress,
      UnresolvedMosaicId unresolvedTargetId) {

    return aliasService
        .resolveMosaicId(unresolvedTargetId)
        .flatMap(
            targetId -> {
              TriFunction<String, NetworkType, Deadline, MosaicMetadataTransactionFactory> factory =
                  (newValue, networkType, deadline) ->
                      MosaicMetadataTransactionFactory.create(
                          networkType, deadline, targetAddress, unresolvedTargetId, key, newValue);

              return processMetadata(
                  new MetadataSearchCriteria()
                      .targetId(targetId)
                      .scopedMetadataKey(key)
                      .sourceAddress(sourceAddress)
                      .metadataType(MetadataType.MOSAIC),
                  factory,
                  value);
            });
  }

  @Override
  public Observable<NamespaceMetadataTransactionFactory> createNamespaceMetadataTransactionFactory(
      Address targetAddress,
      BigInteger key,
      String value,
      Address sourceAddress,
      NamespaceId targetId) {
    TriFunction<String, NetworkType, Deadline, NamespaceMetadataTransactionFactory> factory =
        (newValue, networkType, deadline) ->
            NamespaceMetadataTransactionFactory.create(
                networkType, deadline, targetAddress, targetId, key, newValue);
    return processMetadata(
        new MetadataSearchCriteria()
            .targetId(targetId)
            .scopedMetadataKey(key)
            .sourceAddress(sourceAddress)
            .metadataType(MetadataType.NAMESPACE),
        factory,
        value);
  }

  /**
   * Generic way of processing a metadata entity and creating a new metadata transaction factory
   * depending on the existing metadata value. This works for Account, Mosaic and Namespace
   * metadata.
   *
   * @param criteria the criteria
   * @param transactionFactory the function that creates a transaction factory
   * @param newValue the new value you want to set.
   * @param <T> the type of the transaction factory.
   * @return an Observable of a transaction factory.
   */
  private <T extends MetadataTransactionFactory> Observable<T> processMetadata(
      MetadataSearchCriteria criteria,
      TriFunction<String, NetworkType, Deadline, T> transactionFactory,
      String newValue) {
    return Observable.combineLatest(
            networkTypeObservable,
            epochAdjustmentObservable,
            (networkType, epochAdjustment) ->
                metadataRepository
                    .search(criteria)
                    .map(
                        page -> {
                          Deadline deadline = Deadline.create(epochAdjustment);
                          if (page.getData().isEmpty()) {
                            return transactionFactory.apply(newValue, networkType, deadline);
                          } else {
                            String originalValue = page.getData().get(0).getValue();
                            Pair<String, Integer> xorAndDelta =
                                ConvertUtils.xorValues(originalValue, newValue);
                            T factory =
                                transactionFactory.apply(
                                    xorAndDelta.getLeft(), networkType, deadline);
                            factory.valueSizeDelta(xorAndDelta.getRight());
                            return factory;
                          }
                        }))
        .flatMap(f -> f);
  }

  @FunctionalInterface
  interface TriFunction<A, B, C, R> {

    R apply(A a, B b, C c);

    default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
      Objects.requireNonNull(after);
      return (A a, B b, C c) -> after.apply(apply(a, b, c));
    }
  }
}
