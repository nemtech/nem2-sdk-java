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
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.openapi.vertx.api.MultisigRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.MultisigRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigAccountGraphInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigAccountInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

public class MultisigRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements MultisigRepository {

  private final MultisigRoutesApi client;

  public MultisigRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new MultisigRoutesApiImpl(apiClient);
  }

  @Override
  public Observable<MultisigAccountInfo> getMultisigAccountInfo(Address address) {
    return call(
        (Handler<AsyncResult<MultisigAccountInfoDTO>> handler) ->
            getClient().getAccountMultisig(address.plain(), handler),
        this::toMultisigAccountInfo);
  }

  @Override
  public Observable<MerkleStateInfo> getMultisigAccountInfoMerkle(Address address) {
    return call(
        h -> getClient().getAccountMultisigMerkle(address.plain(), h), this::toMerkleStateInfo);
  }

  @Override
  public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {

    return call(
        (Handler<AsyncResult<List<MultisigAccountGraphInfoDTO>>> handler) ->
            getClient().getAccountMultisigGraph(address.plain(), handler),
        multisigAccountGraphInfoDTOList -> {
          Map<Integer, List<MultisigAccountInfo>> multisigAccountInfoMap = new HashMap<>();
          multisigAccountGraphInfoDTOList.forEach(
              item -> multisigAccountInfoMap.put(item.getLevel(), toMultisigAccountInfo(item)));
          return new MultisigAccountGraphInfo(multisigAccountInfoMap);
        });
  }

  private List<MultisigAccountInfo> toMultisigAccountInfo(MultisigAccountGraphInfoDTO item) {
    return item.getMultisigEntries().stream()
        .map(this::toMultisigAccountInfo)
        .collect(Collectors.toList());
  }

  private MultisigAccountInfo toMultisigAccountInfo(MultisigAccountInfoDTO info) {
    MultisigDTO dto = info.getMultisig();
    return new MultisigAccountInfo(
        null,
        ObjectUtils.defaultIfNull(info.getMultisig().getVersion(), 1),
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
