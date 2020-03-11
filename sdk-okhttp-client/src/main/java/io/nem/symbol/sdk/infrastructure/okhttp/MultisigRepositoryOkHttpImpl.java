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

import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.MultisigRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MultisigAccountGraphInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MultisigAccountInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MultisigDTO;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultisigRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    MultisigRepository {

    private final MultisigRoutesApi client;

    private final Observable<NetworkType> networkTypeObservable;

    public MultisigRepositoryOkHttpImpl(ApiClient apiClient,
        Observable<NetworkType> networkTypeObservable) {
        super(apiClient);
        this.client = new MultisigRoutesApi(apiClient);
        this.networkTypeObservable = networkTypeObservable;
    }

    @Override
    public Observable<MultisigAccountInfo> getMultisigAccountInfo(Address address) {
        return exceptionHandling(networkTypeObservable.flatMap(networkType -> call(
            () -> getClient().getAccountMultisig(address.plain()))
            .map(MultisigAccountInfoDTO::getMultisig)
            .map(dto -> toMultisigAccountInfo(dto, networkType))));

    }

    @Override
    public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {
        return exceptionHandling(networkTypeObservable.flatMap(networkType -> call(
            () -> getClient().getAccountMultisigGraph(address.plain()))
            .map(multisigAccountGraphInfoDTOList -> {
                Map<Integer, List<MultisigAccountInfo>> multisigAccountInfoMap = new HashMap<>();
                multisigAccountGraphInfoDTOList.forEach(
                    item ->
                        multisigAccountInfoMap.put(
                            item.getLevel(),
                            toMultisigAccountInfo(item, networkType)));
                return new MultisigAccountGraphInfo(multisigAccountInfoMap);
            })));
    }

    private List<MultisigAccountInfo> toMultisigAccountInfo(MultisigAccountGraphInfoDTO item,
        NetworkType networkType) {
        return item.getMultisigEntries().stream()
            .map(MultisigAccountInfoDTO::getMultisig)
            .map(dto -> toMultisigAccountInfo(dto, networkType))
            .collect(Collectors.toList());
    }


    private MultisigAccountInfo toMultisigAccountInfo(MultisigDTO dto,
        NetworkType networkType) {
        return new MultisigAccountInfo(
            new PublicAccount(
                dto.getAccountPublicKey(), networkType),
            dto.getMinApproval(),
            dto.getMinRemoval(),
            dto.getCosignatoryPublicKeys().stream()
                .map(cosigner -> new PublicAccount(cosigner, networkType))
                .collect(Collectors.toList()),
            dto.getMultisigPublicKeys().stream()
                .map(multisigAccount -> new PublicAccount(multisigAccount, networkType))
                .collect(Collectors.toList()));
    }


    public MultisigRoutesApi getClient() {
        return client;
    }
}
