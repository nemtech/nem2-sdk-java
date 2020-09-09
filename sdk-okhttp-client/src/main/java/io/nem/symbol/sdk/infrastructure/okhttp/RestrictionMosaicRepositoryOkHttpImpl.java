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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestrictionItem;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.RestrictionMosaicRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestrictionMosaicRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements RestrictionMosaicRepository {

  private final RestrictionMosaicRoutesApi client;

  public RestrictionMosaicRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new RestrictionMosaicRoutesApi(apiClient);
  }

  @Override
  public Observable<List<MosaicAddressRestriction>> getMosaicAddressRestrictions(
      MosaicId mosaicId, List<Address> addresses) {
    AccountIds accountIds =
        new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
    return exceptionHandling(
            call(() -> getClient().getMosaicAddressRestrictions(mosaicId.getIdAsHex(), accountIds))
                .flatMapIterable(item -> item)
                .map(this::toMosaicAddressRestriction))
        .toList()
        .toObservable();
  }

  @Override
  public Observable<MosaicAddressRestriction> getMosaicAddressRestriction(
      MosaicId mosaicId, Address address) {
    return exceptionHandling(
        call(() -> getClient().getMosaicAddressRestriction(mosaicId.getIdAsHex(), address.plain()))
            .map(this::toMosaicAddressRestriction));
  }

  @Override
  public Observable<MosaicGlobalRestriction> getMosaicGlobalRestriction(MosaicId mosaicId) {
    return exceptionHandling(
        call(() -> getClient().getMosaicGlobalRestriction(mosaicId.getIdAsHex()))
            .map(this::toMosaicGlobalRestriction));
  }

  @Override
  public Observable<List<MosaicGlobalRestriction>> getMosaicGlobalRestrictions(
      List<MosaicId> mosaicIds) {
    MosaicIds mosaicIdsParmas =
        new MosaicIds()
            .mosaicIds(mosaicIds.stream().map(MosaicId::getIdAsHex).collect(Collectors.toList()));
    return exceptionHandling(
            call(() -> getClient().getMosaicGlobalRestrictions(mosaicIdsParmas))
                .flatMapIterable(item -> item)
                .map(this::toMosaicGlobalRestriction))
        .toList()
        .toObservable();
  }

  private MosaicGlobalRestriction toMosaicGlobalRestriction(
      MosaicGlobalRestrictionDTO mosaicGlobalRestrictionDTO) {
    MosaicGlobalRestrictionEntryWrapperDTO dto =
        mosaicGlobalRestrictionDTO.getMosaicRestrictionEntry();
    Map<BigInteger, MosaicGlobalRestrictionItem> restrictions =
        dto.getRestrictions().stream()
            .collect(
                Collectors.toMap(
                    e -> new BigInteger(e.getKey()),
                    e -> toMosaicGlobalRestrictionItem(e.getRestriction())));

    return new MosaicGlobalRestriction(
        dto.getCompositeHash(),
        MosaicRestrictionEntryType.rawValueOf(dto.getEntryType().getValue()),
        MapperUtils.toMosaicId(dto.getMosaicId()),
        restrictions);
  }

  private MosaicGlobalRestrictionItem toMosaicGlobalRestrictionItem(
      MosaicGlobalRestrictionEntryRestrictionDTO dto) {
    return new MosaicGlobalRestrictionItem(
        MapperUtils.toMosaicId(dto.getReferenceMosaicId()),
        dto.getRestrictionValue(),
        MosaicRestrictionType.rawValueOf(dto.getRestrictionType().getValue().byteValue()));
  }

  private MosaicAddressRestriction toMosaicAddressRestriction(
      MosaicAddressRestrictionDTO mosaicAddressRestrictionDTO) {
    MosaicAddressRestrictionEntryWrapperDTO dto =
        mosaicAddressRestrictionDTO.getMosaicRestrictionEntry();
    Map<BigInteger, BigInteger> restrictions =
        dto.getRestrictions().stream()
            .collect(
                Collectors.toMap(e -> new BigInteger(e.getKey()), e -> toBigInteger(e.getValue())));

    return new MosaicAddressRestriction(
        dto.getCompositeHash(),
        MosaicRestrictionEntryType.rawValueOf(dto.getEntryType().getValue()),
        MapperUtils.toMosaicId(dto.getMosaicId()),
        MapperUtils.toAddress(dto.getTargetAddress()),
        restrictions);
  }

  private BigInteger toBigInteger(String value) {
    return new BigInteger(value);
  }

  public RestrictionMosaicRoutesApi getClient() {
    return client;
  }
}
