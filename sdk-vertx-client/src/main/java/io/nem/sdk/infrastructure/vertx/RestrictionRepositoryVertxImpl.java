/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.RestrictionRepository;
import io.nem.sdk.model.account.AccountRestriction;
import io.nem.sdk.model.account.AccountRestrictions;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.sdk.model.restriction.MosaicGlobalRestrictionItem;
import io.nem.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.openapi.vertx.api.RestrictionRoutesApi;
import io.nem.sdk.openapi.vertx.api.RestrictionRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.AccountIds;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionsDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionsInfoDTO;
import io.nem.sdk.openapi.vertx.model.MosaicAddressRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.sdk.openapi.vertx.model.MosaicGlobalRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.sdk.openapi.vertx.model.MosaicIds;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RestrictionRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    RestrictionRepository {

    private final RestrictionRoutesApi client;

    public RestrictionRepositoryVertxImpl(ApiClient apiClient,
        Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
        this.client = new RestrictionRoutesApiImpl(apiClient);
    }


    @Override
    public Observable<AccountRestrictions> getAccountRestrictions(Address address) {

        return exceptionHandling(call(
            (Handler<AsyncResult<AccountRestrictionsInfoDTO>> handler) -> getClient()
                .getAccountRestrictions(address.plain(), handler))
            .map(AccountRestrictionsInfoDTO::getAccountRestrictions)
            .map(this::toAccountRestrictions));
    }

    @Override
    public Observable<List<AccountRestrictions>> getAccountsRestrictions(
        List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        return getAccountsRestrictions(accountIds);
    }


    private Observable<List<AccountRestrictions>> getAccountsRestrictions(AccountIds accountIds) {
        return exceptionHandling(call(
            (Handler<AsyncResult<List<AccountRestrictionsInfoDTO>>> handler) -> getClient()
                .getAccountRestrictionsFromAccounts(accountIds, handler))
            .flatMapIterable(item -> item)
            .map(AccountRestrictionsInfoDTO::getAccountRestrictions)
            .map(this::toAccountRestrictions)).toList().toObservable();
    }


    private AccountRestrictions toAccountRestrictions(AccountRestrictionsDTO dto) {
        return new AccountRestrictions(MapperUtils.toAddressFromUnresolved(dto.getAddress()),
            dto.getRestrictions().stream().map(this::toAccountRestriction).collect(
                Collectors.toList()));
    }

    private AccountRestriction toAccountRestriction(AccountRestrictionDTO dto) {
        AccountRestrictionType restrictionType = AccountRestrictionType
            .rawValueOf(dto.getRestrictionType().getValue());
        return new AccountRestriction(
            restrictionType,
            dto.getValues().stream().filter(Objects::nonNull).map(Object::toString)
                .map(restrictionType.getTargetType()::fromString).collect(
                Collectors.toList()));
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
            .collect(Collectors.toMap(e -> MapperUtils.fromHex(e.getKey()),
                e -> toMosaicGlobalRestrictionItem(e.getRestriction())));

        return new MosaicGlobalRestriction(dto.getCompositeHash(),
            MosaicRestrictionEntryType.rawValueOf(dto.getEntryType().getValue()),
            MapperUtils.toMosaicId(dto.getMosaicId()),
            restrictions);
    }

    private MosaicGlobalRestrictionItem toMosaicGlobalRestrictionItem(
        MosaicGlobalRestrictionEntryRestrictionDTO dto) {
        return new MosaicGlobalRestrictionItem(MapperUtils.toMosaicId(dto.getReferenceMosaicId()),
            toBigInteger(dto.getRestrictionValue()),
            MosaicRestrictionType.rawValueOf(dto.getRestrictionType().getValue().byteValue()));
    }

    private MosaicAddressRestriction toMosaicAddressRestriction(
        MosaicAddressRestrictionDTO mosaicAddressRestrictionDTO) {
        MosaicAddressRestrictionEntryWrapperDTO dto = mosaicAddressRestrictionDTO
            .getMosaicRestrictionEntry();
        Map<BigInteger, BigInteger> restrictions = dto.getRestrictions().stream()
            .collect(Collectors.toMap(e -> MapperUtils.fromHex(e.getKey()),
                e -> toBigInteger(e.getValue())));

        return new MosaicAddressRestriction(dto.getCompositeHash(),
            MosaicRestrictionEntryType.rawValueOf(dto.getEntryType().getValue()),
            MapperUtils.toMosaicId(dto.getMosaicId()), MapperUtils
            .toAddressFromUnresolved(dto.getTargetAddress()),
            restrictions);
    }


    private BigInteger toBigInteger(String value) {
        return new BigInteger(value);
    }

    public RestrictionRoutesApi getClient() {
        return client;
    }
}
