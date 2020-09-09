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

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
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

public class MultisigRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements MultisigRepository {

  private final MultisigRoutesApi client;

  private final Observable<NetworkType> networkTypeObservable;

  public MultisigRepositoryOkHttpImpl(
      ApiClient apiClient, Observable<NetworkType> networkTypeObservable) {
    super(apiClient);
    this.client = new MultisigRoutesApi(apiClient);
    this.networkTypeObservable = networkTypeObservable;
  }

  @Override
  public Observable<MultisigAccountInfo> getMultisigAccountInfo(Address address) {
    return exceptionHandling(
        networkTypeObservable.flatMap(
            networkType ->
                call(() -> getClient().getAccountMultisig(address.plain()))
                    .map(MultisigAccountInfoDTO::getMultisig)
                    .map(dto -> toMultisigAccountInfo(dto))));
  }

  @Override
  public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {
    return exceptionHandling(
        networkTypeObservable.flatMap(
            networkType ->
                call(() -> getClient().getAccountMultisigGraph(address.plain()))
                    .map(
                        multisigAccountGraphInfoDTOList -> {
                          Map<Integer, List<MultisigAccountInfo>> multisigAccountInfoMap =
                              new HashMap<>();
                          multisigAccountGraphInfoDTOList.forEach(
                              item ->
                                  multisigAccountInfoMap.put(
                                      item.getLevel(), toMultisigAccountInfo(item)));
                          return new MultisigAccountGraphInfo(multisigAccountInfoMap);
                        })));
  }

  private List<MultisigAccountInfo> toMultisigAccountInfo(MultisigAccountGraphInfoDTO item) {
    return item.getMultisigEntries().stream()
        .map(MultisigAccountInfoDTO::getMultisig)
        .map(dto -> toMultisigAccountInfo(dto))
        .collect(Collectors.toList());
  }

  private MultisigAccountInfo toMultisigAccountInfo(MultisigDTO dto) {
    return new MultisigAccountInfo(
        MapperUtils.toAddress(dto.getAccountAddress()),
        dto.getMinApproval(),
        dto.getMinRemoval(),
        dto.getCosignatoryAddresses().stream()
            .map(MapperUtils::toAddress)
            .collect(Collectors.toList()),
        dto.getMultisigAddresses().stream()
            .map(MapperUtils::toAddress)
            .collect(Collectors.toList()));
  }

  public MultisigRoutesApi getClient() {
    return client;
  }
}
