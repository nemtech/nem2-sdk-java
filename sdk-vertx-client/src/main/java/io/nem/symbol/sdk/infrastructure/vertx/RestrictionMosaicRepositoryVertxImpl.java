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
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestrictionItem;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.openapi.vertx.api.RestrictionMosaicRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.RestrictionMosaicRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.AccountIds;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicAddressRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicGlobalRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicIds;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestrictionMosaicRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    RestrictionMosaicRepository {

    private final RestrictionMosaicRoutesApi client;

    public RestrictionMosaicRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new RestrictionMosaicRoutesApiImpl(apiClient);
    }


    @Override
    public Observable<MosaicAddressRestriction> getMosaicAddressRestriction(MosaicId mosaicId,
        Address address) {
        return exceptionHandling(call(
            (Handler<AsyncResult<MosaicAddressRestrictionDTO>> handler) -> getClient()
                .getMosaicAddressRestriction(mosaicId.getIdAsHex(), address.plain(), handler))
            .map(this::toMosaicAddressRestriction));
    }

    @Override
    public Observable<List<MosaicAddressRestriction>> getMosaicAddressRestrictions(
        MosaicId mosaicId, List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        return exceptionHandling(call(
            (Handler<AsyncResult<List<MosaicAddressRestrictionDTO>>> handler) -> getClient()
                .getMosaicAddressRestrictions(mosaicId.getIdAsHex(), accountIds, handler))
            .flatMapIterable(item -> item).map(this::toMosaicAddressRestriction)).toList()
            .toObservable();
    }

    @Override
    public Observable<MosaicGlobalRestriction> getMosaicGlobalRestriction(MosaicId mosaicId) {
        return exceptionHandling(call(
            (Handler<AsyncResult<MosaicGlobalRestrictionDTO>> handler) -> getClient()
                .getMosaicGlobalRestriction(mosaicId.getIdAsHex(), handler))
            .map(this::toMosaicGlobalRestriction));
    }

    @Override
    public Observable<List<MosaicGlobalRestriction>> getMosaicGlobalRestrictions(
        List<MosaicId> mosaicIds) {
        MosaicIds mosaicIdsParmas = new MosaicIds()
            .mosaicIds(mosaicIds.stream().map(MosaicId::getIdAsHex).collect(Collectors.toList()));
        return exceptionHandling(call(
            (Handler<AsyncResult<List<MosaicGlobalRestrictionDTO>>> handler) -> getClient()
                .getMosaicGlobalRestrictions(mosaicIdsParmas, handler))
            .flatMapIterable(item -> item).map(this::toMosaicGlobalRestriction)).toList()
            .toObservable();
    }


    private MosaicGlobalRestriction toMosaicGlobalRestriction(
        MosaicGlobalRestrictionDTO mosaicGlobalRestrictionDTO) {
        MosaicGlobalRestrictionEntryWrapperDTO dto = mosaicGlobalRestrictionDTO
            .getMosaicRestrictionEntry();
        Map<BigInteger, MosaicGlobalRestrictionItem> restrictions = dto.getRestrictions().stream()
            .collect(Collectors.toMap(e -> new BigInteger(e.getKey()),
                e -> toMosaicGlobalRestrictionItem(e.getRestriction())));

        return new MosaicGlobalRestriction(dto.getCompositeHash(),
            MosaicRestrictionEntryType.rawValueOf(dto.getEntryType().getValue()),
            MapperUtils.toMosaicId(dto.getMosaicId()),
            restrictions);
    }

    private MosaicGlobalRestrictionItem toMosaicGlobalRestrictionItem(
        MosaicGlobalRestrictionEntryRestrictionDTO dto) {
        return new MosaicGlobalRestrictionItem(MapperUtils.toMosaicId(dto.getReferenceMosaicId()),
            dto.getRestrictionValue(),
            MosaicRestrictionType.rawValueOf(dto.getRestrictionType().getValue().byteValue()));
    }

    private MosaicAddressRestriction toMosaicAddressRestriction(
        MosaicAddressRestrictionDTO mosaicAddressRestrictionDTO) {
        MosaicAddressRestrictionEntryWrapperDTO dto = mosaicAddressRestrictionDTO
            .getMosaicRestrictionEntry();
        Map<BigInteger, BigInteger> restrictions = dto.getRestrictions().stream()
            .collect(Collectors.toMap(e -> new BigInteger(e.getKey()),
                e -> toBigInteger(e.getValue())));

        return new MosaicAddressRestriction(dto.getCompositeHash(),
            MosaicRestrictionEntryType.rawValueOf(dto.getEntryType().getValue()),
            MapperUtils.toMosaicId(dto.getMosaicId()), MapperUtils
            .toAddress(dto.getTargetAddress()),
            restrictions);
    }


    private BigInteger toBigInteger(String value) {
        return new BigInteger(value);
    }

    public RestrictionMosaicRoutesApi getClient() {
        return client;
    }
}
