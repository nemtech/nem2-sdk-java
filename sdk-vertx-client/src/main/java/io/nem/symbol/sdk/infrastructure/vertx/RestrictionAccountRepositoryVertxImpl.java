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
import io.nem.symbol.sdk.api.AccountRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RestrictionAccountRepository;
import io.nem.symbol.sdk.model.account.AccountRestriction;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.openapi.vertx.api.RestrictionAccountRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.RestrictionAccountRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionsInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionsPage;
import io.nem.symbol.sdk.openapi.vertx.model.Order;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RestrictionAccountRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements RestrictionAccountRepository {

  private final RestrictionAccountRoutesApi client;

  public RestrictionAccountRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new RestrictionAccountRoutesApiImpl(apiClient);
  }

  @Override
  public Observable<AccountRestrictions> getAccountRestrictions(Address address) {

    return call(
        (Handler<AsyncResult<AccountRestrictionsInfoDTO>> handler) ->
            getClient().getAccountRestrictions(address.plain(), handler),
        this::toAccountRestrictions);
  }

  private AccountRestrictions toAccountRestrictions(AccountRestrictionsInfoDTO dto) {
    return new AccountRestrictions(
        dto.getAccountRestrictions().getVersion(),
        MapperUtils.toAddress(dto.getAccountRestrictions().getAddress()),
        dto.getAccountRestrictions().getRestrictions().stream()
            .map(this::toAccountRestriction)
            .collect(Collectors.toList()));
  }

  private AccountRestriction toAccountRestriction(AccountRestrictionDTO dto) {
    AccountRestrictionFlags restrictionFlags =
        AccountRestrictionFlags.rawValueOf(dto.getRestrictionFlags().getValue());
    return new AccountRestriction(
        restrictionFlags,
        dto.getValues().stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .map(restrictionFlags.getTargetType()::fromString)
            .collect(Collectors.toList()));
  }

  @Override
  public Observable<MerkleStateInfo> getAccountRestrictionsMerkle(Address address) {
    return call(
        (h) -> getClient().getAccountRestrictionsMerkle(address.plain(), h),
        this::toMerkleStateInfo);
  }

  public RestrictionAccountRoutesApi getClient() {
    return client;
  }

  @Override
  public Observable<Page<AccountRestrictions>> search(AccountRestrictionSearchCriteria criteria) {
    String address = toDto(criteria.getAddress());
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());
    Consumer<Handler<AsyncResult<AccountRestrictionsPage>>> handlerConsumer =
        (h) ->
            getClient().searchAccountRestrictions(address, pageSize, pageNumber, offset, order, h);
    return this.call(handlerConsumer, this::toPage);
  }

  private Page<AccountRestrictions> toPage(AccountRestrictionsPage page) {
    return toPage(
        page.getPagination(),
        page.getData().stream().map(this::toAccountRestrictions).collect(Collectors.toList()));
  }
}
