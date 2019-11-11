/*
 * Copyright 2019 NEM
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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.RestrictionAccountRepository;
import io.nem.sdk.model.account.AccountRestriction;
import io.nem.sdk.model.account.AccountRestrictions;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.openapi.okhttp_gson.api.RestrictionAccountRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionsDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionsInfoDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class RestrictionAccountRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    RestrictionAccountRepository {

    private final RestrictionAccountRoutesApi client;

    public RestrictionAccountRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new RestrictionAccountRoutesApi(apiClient);
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
        return new AccountRestrictions(MapperUtils.toAddressFromEncoded(dto.getAddress()),
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


    private BigInteger toBigInteger(String value) {
        return new BigInteger(value);
    }


    public RestrictionAccountRoutesApi getClient() {
        return client;
    }
}
