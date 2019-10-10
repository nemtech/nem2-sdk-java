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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.RestrictionRepository;
import io.nem.sdk.model.account.AccountRestriction;
import io.nem.sdk.model.account.AccountRestrictions;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.sdk.model.restriction.MosaicGlobalRestrictionItem;
import io.nem.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.openapi.okhttp_gson.api.RestrictionRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionsDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionsInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicAddressRestrictionEntryWrapperDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryRestrictionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicGlobalRestrictionEntryWrapperDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class RestrictionRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    RestrictionRepository {

    private final RestrictionRoutesApi client;

    public RestrictionRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new RestrictionRoutesApi(apiClient);
    }


    @Override
    public Observable<AccountRestrictions> getAccountRestrictions(Address address) {

        Callable<AccountRestrictionsInfoDTO> callback = () -> getClient()
            .getAccountRestrictions(address.plain());
        return exceptionHandling(
            call(callback).map(AccountRestrictionsInfoDTO::getAccountRestrictions)
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
        Callable<List<AccountRestrictionsInfoDTO>> callback = () -> getClient()
            .getAccountRestrictionsFromAccounts(accountIds);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item)
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
    public Observable<List<MosaicAddressRestriction>> getMosaicAddressRestrictions(
        MosaicId mosaicId, List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        return exceptionHandling(call(() -> getClient()
            .getMosaicAddressRestrictions(mosaicId.getIdAsHex(), accountIds))
            .flatMapIterable(item -> item).map(this::toMosaicAddressRestriction)).toList()
            .toObservable();
    }

    @Override
    public Observable<MosaicAddressRestriction> getMosaicAddressRestriction(MosaicId mosaicId,
        Address address) {
        return exceptionHandling(call(
            () -> getClient().getMosaicAddressRestriction(mosaicId.getIdAsHex(), address.plain()))
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
        MosaicIds mosaicIdsParmas = new MosaicIds()
            .mosaicIds(mosaicIds.stream().map(MosaicId::getIdAsHex).collect(Collectors.toList()));
        return exceptionHandling(
            call(() -> getClient().getMosaicGlobalRestrictions(mosaicIdsParmas))
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
