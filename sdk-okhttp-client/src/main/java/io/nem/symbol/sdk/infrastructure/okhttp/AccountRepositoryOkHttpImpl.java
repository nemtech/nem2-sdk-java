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
import io.nem.symbol.sdk.model.account.AccountLinkVotingKey;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.ActivityBucket;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.SupplementalAccountKeys;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.AccountRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SupplementalPublicKeysDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by fernando on 29/07/19.
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements AccountRepository {

    private final AccountRoutesApi client;

    public AccountRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new AccountRoutesApi(apiClient);
    }


    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {

        Callable<AccountInfoDTO> callback = () -> getClient().getAccountInfo(address.plain());
        return exceptionHandling(call(callback).map(AccountInfoDTO::getAccount).map(this::toAccountInfo));
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        Callable<List<AccountInfoDTO>> callback = () -> getClient().getAccountsInfo(accountIds);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(AccountInfoDTO::getAccount).map(this::toAccountInfo)
                .toList().toObservable());
    }


    private AccountInfo toAccountInfo(AccountDTO accountDTO) {
        return new AccountInfo(toAddress(accountDTO.getAddress()), accountDTO.getAddressHeight(),
            accountDTO.getPublicKey(), accountDTO.getPublicKeyHeight(), accountDTO.getImportance(),
            accountDTO.getImportanceHeight(), accountDTO.getMosaics().stream()
            .map(mosaicDTO -> new Mosaic(toMosaicId(mosaicDTO.getId()), mosaicDTO.getAmount()))
            .collect(Collectors.toList()), AccountType.rawValueOf(accountDTO.getAccountType().getValue()),
            toDto(accountDTO.getSupplementalPublicKeys()), accountDTO.getActivityBuckets().stream().map(
            dto -> new ActivityBucket(dto.getStartHeight(), dto.getTotalFeesPaid(), dto.getBeneficiaryCount(),
                dto.getRawScore())).collect(Collectors.toList()));
    }

    private SupplementalAccountKeys toDto(SupplementalPublicKeysDTO dto) {
        if (dto == null) {
            return new SupplementalAccountKeys(Optional.empty(), Optional.empty(), Optional.empty(),
                Collections.emptyList());
        }
        Optional<String> linked = Optional.ofNullable(dto.getLinked() == null ? null : dto.getLinked().getPublicKey());
        Optional<String> node = Optional.ofNullable(dto.getNode() == null ? null : dto.getNode().getPublicKey());
        Optional<String> vrf = Optional.ofNullable(dto.getVrf() == null ? null : dto.getVrf().getPublicKey());

        List<AccountLinkVotingKey> voting =
            dto.getVoting() == null || dto.getVoting().getPublicKeys() == null ? Collections.emptyList()
                : dto.getVoting().getPublicKeys().stream().map(
                    p -> new AccountLinkVotingKey(p.getPublicKey(), new BigInteger(p.getStartPoint()),
                        new BigInteger(p.getEndPoint()))).collect(Collectors.toList());
        return new SupplementalAccountKeys(linked, node, vrf, voting);
    }


    private AccountRoutesApi getClient() {
        return client;
    }

}
