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
import io.nem.sdk.api.RestrictionAccountRepository;
import io.nem.sdk.model.account.AccountRestriction;
import io.nem.sdk.model.account.AccountRestrictions;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.sdk.openapi.vertx.api.RestrictionAccountRoutesApi;
import io.nem.sdk.openapi.vertx.api.RestrictionAccountRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.AccountIds;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionsDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionsInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RestrictionAccountRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    RestrictionAccountRepository {

    private final RestrictionAccountRoutesApi client;

    public RestrictionAccountRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new RestrictionAccountRoutesApiImpl(apiClient);
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
        return new AccountRestrictions(MapperUtils.toAddressFromEncoded(dto.getAddress()),
            dto.getRestrictions().stream().map(this::toAccountRestriction).collect(
                Collectors.toList()));
    }

    private AccountRestriction toAccountRestriction(AccountRestrictionDTO dto) {
        AccountRestrictionFlags restrictionFlags = AccountRestrictionFlags
            .rawValueOf(dto.getRestrictionFlags().getValue());
        return new AccountRestriction(
            restrictionFlags,
            dto.getValues().stream().filter(Objects::nonNull).map(Object::toString)
                .map(restrictionFlags.getTargetType()::fromString).collect(
                Collectors.toList()));
    }

    public RestrictionAccountRoutesApi getClient() {
        return client;
    }
}
