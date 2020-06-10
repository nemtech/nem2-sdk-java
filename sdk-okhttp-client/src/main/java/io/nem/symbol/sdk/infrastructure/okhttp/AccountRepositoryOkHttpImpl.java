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

import static io.nem.symbol.core.utils.MapperUtils.toAddress;
import static io.nem.symbol.core.utils.MapperUtils.toMosaicId;

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountKey;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.ActivityBucket;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.KeyType;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.AccountRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionTypeEnum;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by fernando on 29/07/19.
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    AccountRepository {

    private final AccountRoutesApi client;

    public AccountRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new AccountRoutesApi(apiClient);
    }


    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {

        Callable<AccountInfoDTO> callback = () -> getClient()
            .getAccountInfo(address.plain());
        return exceptionHandling(
            call(callback).map(AccountInfoDTO::getAccount).map(this::toAccountInfo));
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        Callable<List<AccountInfoDTO>> callback = () -> getClient()
            .getAccountsInfo(accountIds);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item)
                .map(AccountInfoDTO::getAccount)
                .map(this::toAccountInfo).toList().toObservable());
    }


    private AccountInfo toAccountInfo(AccountDTO accountDTO) {
        return new AccountInfo(
            toAddress(accountDTO.getAddress()),
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
            AccountType.rawValueOf(accountDTO.getAccountType().getValue()),
            accountDTO.getSupplementalAccountKeys().stream().map(dto -> new AccountKey(
                KeyType.rawValueOf(dto.getKeyType().getValue()), dto.getKey()))
                .collect(Collectors.toList()),
            accountDTO.getActivityBuckets().stream()
                .map(dto -> new ActivityBucket(dto.getStartHeight(),
                    dto.getTotalFeesPaid(), dto.getBeneficiaryCount(), dto.getRawScore()))
                .collect(Collectors.toList()));
    }


    private AccountRoutesApi getClient() {
        return client;
    }

    private List<TransactionTypeEnum> toTransactionTypes(List<TransactionType> transactionTypes) {
        return transactionTypes == null || transactionTypes.isEmpty() ? null
            : transactionTypes.stream()
                .map(transactionType -> TransactionTypeEnum.fromValue(transactionType.getValue()))
                .collect(Collectors.toList());
    }
}
