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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.MosaicRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestrictionItem;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.openapi.vertx.api.RestrictionMosaicRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.RestrictionMosaicRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicAddressRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicGlobalRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicRestrictionEntryTypeEnum;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicRestrictionsPage;
import io.nem.symbol.sdk.openapi.vertx.model.Order;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

public class RestrictionMosaicRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements RestrictionMosaicRepository {

  private final RestrictionMosaicRoutesApi client;

  public RestrictionMosaicRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new RestrictionMosaicRoutesApiImpl(apiClient);
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
                    e -> toMosaicGlobalRestrictionItem(e.getRestriction()),
                    (x, y) -> y,
                    LinkedHashMap::new));

    return new MosaicGlobalRestriction(
        mosaicGlobalRestrictionDTO.getId(),
        ObjectUtils.defaultIfNull(
            mosaicGlobalRestrictionDTO.getMosaicRestrictionEntry().getVersion(), 1),
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
        mosaicAddressRestrictionDTO.getId(),
        ObjectUtils.defaultIfNull(dto.getVersion(), 1),
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

  @Override
  public Observable<Page<MosaicRestriction<?>>> search(MosaicRestrictionSearchCriteria criteria) {

    String mosaicId = criteria.getMosaicId() == null ? null : criteria.getMosaicId().getIdAsHex();
    MosaicRestrictionEntryTypeEnum entryType =
        criteria.getEntryType() == null
            ? null
            : MosaicRestrictionEntryTypeEnum.fromValue(criteria.getEntryType().getValue());
    String targetAddress = toDto(criteria.getTargetAddress());
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());

    Consumer<Handler<AsyncResult<MosaicRestrictionsPage>>> callback =
        (h) ->
            getClient()
                .searchMosaicRestrictions(
                    mosaicId, entryType, targetAddress, pageSize, pageNumber, offset, order, h);

    return call(
        callback,
        page ->
            toPage(
                page.getPagination(),
                page.getData().stream()
                    .map(this::toMosaicRestriction)
                    .collect(Collectors.toList())));
  }

  private MosaicRestriction<?> toMosaicRestriction(Object restrictionObject) {
    MosaicRestrictionEntryType thisEntryType =
        MosaicRestrictionEntryType.rawValueOf(
            getJsonHelper().getInteger(restrictionObject, "mosaicRestrictionEntry", "entryType"));
    switch (thisEntryType) {
      case ADDRESS:
        return toMosaicAddressRestriction(
            getJsonHelper().convert(restrictionObject, MosaicAddressRestrictionDTO.class));
      case GLOBAL:
        return toMosaicGlobalRestriction(
            getJsonHelper().convert(restrictionObject, MosaicGlobalRestrictionDTO.class));
    }
    throw new IllegalStateException("Invalid entry type " + thisEntryType);
  }

  @Override
  public Observable<MosaicRestriction<?>> getMosaicRestrictions(String compositeHash) {
    return this.call(
        (h) -> getClient().getMosaicRestrictions(compositeHash, h), this::toMosaicRestriction);
  }

  @Override
  public Observable<MerkleStateInfo> getMosaicRestrictionsMerkle(String compositeHash) {
    return this.call(
        (h) -> getClient().getMosaicRestrictionsMerkle(compositeHash, h), this::toMerkleStateInfo);
  }
}
