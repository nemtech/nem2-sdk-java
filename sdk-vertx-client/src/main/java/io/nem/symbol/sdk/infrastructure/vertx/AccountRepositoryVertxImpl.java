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

import static io.nem.symbol.core.utils.MapperUtils.toAddressFromEncoded;
import static io.nem.symbol.core.utils.MapperUtils.toMosaicId;

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.openapi.vertx.api.AccountRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.AccountRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.AccountDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountIds;
import io.nem.symbol.sdk.openapi.vertx.model.AccountInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by fernando on 29/07/19.
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    AccountRepository {


    private final AccountRoutesApi client;


    public AccountRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new AccountRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {

        Consumer<Handler<AsyncResult<AccountInfoDTO>>> callback = handler -> getClient()
            .getAccountInfo(address.plain(), handler);
        return exceptionHandling(
            call(callback).map(AccountInfoDTO::getAccount).map(this::toAccountInfo));
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        Consumer<Handler<AsyncResult<List<AccountInfoDTO>>>> callback = handler -> getClient()
            .getAccountsInfo(accountIds, handler);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item)
                .map(AccountInfoDTO::getAccount)
                .map(this::toAccountInfo).toList().toObservable());
    }


    private AccountInfo toAccountInfo(AccountDTO accountDTO) {
        return new AccountInfo(
            toAddressFromEncoded(accountDTO.getAddress()),
            accountDTO.getAddressHeight(),
            accountDTO.getPublicKey(),
            accountDTO.getPublicKeyHeight(),
            accountDTO.getImportance(),
            accountDTO.getImportanceHeight(),
            accountDTO.getMosaics().stream()
                .map(
                    mosaicDTO ->
                        new Mosaic(
                            toMosaicId(mosaicDTO.getId()),
                            mosaicDTO.getAmount()))
                .collect(Collectors.toList()),
            AccountType.rawValueOf(accountDTO.getAccountType().getValue()));
    }


    private AccountRoutesApi getClient() {
        return client;
    }


}
