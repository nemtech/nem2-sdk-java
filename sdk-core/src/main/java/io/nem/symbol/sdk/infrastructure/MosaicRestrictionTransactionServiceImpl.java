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

import io.nem.symbol.sdk.api.AliasService;
import io.nem.symbol.sdk.api.MosaicRestrictionTransactionService;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestrictionItem;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Optional;

/** Implementation of {@link MosaicRestrictionTransactionService}. */
public class MosaicRestrictionTransactionServiceImpl
    implements MosaicRestrictionTransactionService {

  /** The network type so user is not required to provide it each time. */
  private final Observable<NetworkType> networkTypeObservable;

  /** The repository used to retrieve the old mosaic restriction values. */
  private final RestrictionMosaicRepository repository;
  /** The repository used to resolve aliases. */
  private final AliasService aliasService;

  /**
   * The constructor.
   *
   * @param repositoryFactory the repository factory.
   */
  public MosaicRestrictionTransactionServiceImpl(RepositoryFactory repositoryFactory) {
    this.repository = repositoryFactory.createRestrictionMosaicRepository();
    this.networkTypeObservable = repositoryFactory.getNetworkType();
    this.aliasService = new AliasServiceImpl(repositoryFactory);
  }

  @Override
  public Observable<MosaicGlobalRestrictionTransactionFactory>
      createMosaicGlobalRestrictionTransactionFactory(
          UnresolvedMosaicId unresolvedMosaicId,
          BigInteger restrictionKey,
          BigInteger restrictionValue,
          MosaicRestrictionType restrictionType) {
    return aliasService
        .resolveMosaicId(unresolvedMosaicId)
        .flatMap(
            mosaicId ->
                networkTypeObservable.flatMap(
                    networkType ->
                        this.getGlobalRestrictionEntry(mosaicId, restrictionKey)
                            .map(
                                optional -> {
                                  MosaicGlobalRestrictionTransactionFactory factory =
                                      MosaicGlobalRestrictionTransactionFactory.create(
                                          networkType,
                                          unresolvedMosaicId,
                                          restrictionKey,
                                          restrictionValue,
                                          restrictionType);

                                  optional.ifPresent(
                                      mosaicGlobalRestrictionItem -> {
                                        factory.previousRestrictionValue(
                                            mosaicGlobalRestrictionItem.getRestrictionValue());
                                        factory.previousRestrictionType(
                                            mosaicGlobalRestrictionItem.getRestrictionType());
                                      });
                                  return factory;
                                })));
  }

  @Override
  public Observable<MosaicAddressRestrictionTransactionFactory>
      createMosaicAddressRestrictionTransactionFactory(
          UnresolvedMosaicId unresolvedMosaicId,
          BigInteger restrictionKey,
          UnresolvedAddress unresolvedTargetAddress,
          BigInteger restrictionValue) {

    return Observable.combineLatest(
            networkTypeObservable,
            aliasService.resolveMosaicId(unresolvedMosaicId),
            aliasService.resolveAddress(unresolvedTargetAddress),
            (networkType, mosaicId, targetAddress) ->
                getGlobalRestrictionEntry(mosaicId, restrictionKey)
                    .flatMap(
                        optional -> {
                          if (!optional.isPresent()) {
                            throw new IllegalArgumentException(
                                "Global restriction is not valid for RestrictionKey: "
                                    + restrictionKey);
                          }

                          return getCurrentMosaicAddressRestrictionValue(
                                  mosaicId, targetAddress, restrictionKey)
                              .map(
                                  optionalValue -> {
                                    MosaicAddressRestrictionTransactionFactory factory =
                                        MosaicAddressRestrictionTransactionFactory.create(
                                            networkType,
                                            unresolvedMosaicId,
                                            restrictionKey,
                                            unresolvedTargetAddress,
                                            restrictionValue);
                                    optionalValue.ifPresent(factory::previousRestrictionValue);
                                    return factory;
                                  });
                        }))
        .flatMap(f -> f);
  }

  /**
   * Get the mosaic address restriction current value.
   *
   * @param mosaicId Mosaic identifier
   * @param targetAddress the target address.
   * @param restrictionKey Mosaic global restriction key
   * @return Observable of BigInteger optional.
   */
  private Observable<Optional<BigInteger>> getCurrentMosaicAddressRestrictionValue(
      MosaicId mosaicId, Address targetAddress, BigInteger restrictionKey) {
    return emptyOnNotFound(
        this.repository
            .getMosaicAddressRestriction(mosaicId, targetAddress)
            .map(r -> Optional.ofNullable(r.getRestrictions().get(restrictionKey))));
  }

  /**
   * Get mosaic global restriction previous value and type
   *
   * @param mosaicId Mosaic identifier
   * @param restrictionKey Mosaic global restriction key
   * @return Observable of MosaicGlobalRestrictionItem optional.
   */
  private Observable<Optional<MosaicGlobalRestrictionItem>> getGlobalRestrictionEntry(
      MosaicId mosaicId, BigInteger restrictionKey) {
    return emptyOnNotFound(
        this.repository
            .getMosaicGlobalRestriction(mosaicId)
            .map(r -> Optional.ofNullable(r.getRestrictions().get(restrictionKey))));
  }

  /**
   * Utility wrapper that returns an empty object when the rest response returns 404.
   *
   * @param call the rest call
   * @param <T> the type of the response
   * @return the Observable that returns empty on 404. If there are other rest errors, the exception
   *     is propagated.
   */
  private <T> Observable<Optional<T>> emptyOnNotFound(Observable<Optional<T>> call) {
    return call.onErrorResumeNext(
        exception -> {
          if (exception instanceof RepositoryCallException
              && ((RepositoryCallException) exception).getStatusCode() == 404) {
            return Observable.just(Optional.empty());
          } else {
            return Observable.error(exception);
          }
        });
  }
}
