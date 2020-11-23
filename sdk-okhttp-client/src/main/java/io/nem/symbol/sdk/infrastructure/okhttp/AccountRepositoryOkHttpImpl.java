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

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountLinkVotingKey;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.ActivityBucket;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.SupplementalAccountKeys;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.AccountRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountLinkPublicKeyDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountOrderByEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SupplementalPublicKeysDTO;
import io.reactivex.Observable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by fernando on 29/07/19.
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements AccountRepository {

  private final AccountRoutesApi client;

  public AccountRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new AccountRoutesApi(apiClient);
  }

  @Override
  public Observable<AccountInfo> getAccountInfo(Address address) {
    return call(() -> getClient().getAccountInfo(address.plain()), this::toAccountInfo);
  }

  @Override
  public Observable<MerkleStateInfo> getAccountInfoMerkle(Address address) {
    return call(() -> getClient().getAccountInfoMerkle(address.plain()), this::toMerkleStateInfo);
  }

  @Override
  public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
    AccountIds accountIds =
        new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
    Callable<List<AccountInfoDTO>> callback = () -> getClient().getAccountsInfo(accountIds);
    return exceptionHandling(
        call(callback)
            .flatMapIterable(item -> item)
            .map(this::toAccountInfo)
            .toList()
            .toObservable());
  }

  @Override
  public Observable<Page<AccountInfo>> search(AccountSearchCriteria criteria) {

    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());
    AccountOrderByEnum orderBy =
        criteria.getOrderBy() == null
            ? null
            : AccountOrderByEnum.fromValue(criteria.getOrderBy().getValue());
    String mosaicId = criteria.getMosaicId() == null ? null : criteria.getMosaicId().getIdAsHex();

    Callable<AccountPage> callback =
        () -> getClient().searchAccounts(pageSize, pageNumber, offset, order, orderBy, mosaicId);

    return exceptionHandling(
        call(callback)
            .map(
                page ->
                    this.toPage(
                        page.getPagination(),
                        page.getData().stream()
                            .map(this::toAccountInfo)
                            .collect(Collectors.toList()))));
  }

  private AccountInfo toAccountInfo(AccountInfoDTO accountInfoDTO) {
    AccountDTO accountDTO = accountInfoDTO.getAccount();
    return new AccountInfo(
        accountInfoDTO.getId(),
        toAddress(accountDTO.getAddress()),
        accountDTO.getAddressHeight(),
        PublicKey.fromHexString(accountDTO.getPublicKey()),
        accountDTO.getPublicKeyHeight(),
        accountDTO.getImportance(),
        accountDTO.getImportanceHeight(),
        accountDTO.getMosaics().stream()
            .map(
                mosaicDTO ->
                    new ResolvedMosaic(toMosaicId(mosaicDTO.getId()), mosaicDTO.getAmount()))
            .collect(Collectors.toList()),
        AccountType.rawValueOf(accountDTO.getAccountType().getValue()),
        toDto(accountDTO.getSupplementalPublicKeys()),
        accountDTO.getActivityBuckets().stream()
            .map(
                dto ->
                    new ActivityBucket(
                        dto.getStartHeight(),
                        dto.getTotalFeesPaid(),
                        dto.getBeneficiaryCount(),
                        dto.getRawScore()))
            .collect(Collectors.toList()));
  }

  private SupplementalAccountKeys toDto(SupplementalPublicKeysDTO dto) {
    if (dto == null) {
      return new SupplementalAccountKeys(null, null, null, Collections.emptyList());
    }
    PublicKey linked = toPublicKey(dto.getLinked());
    PublicKey node = toPublicKey(dto.getNode());
    PublicKey vrf = toPublicKey(dto.getVrf());

    List<AccountLinkVotingKey> voting =
        dto.getVoting() == null || dto.getVoting().getPublicKeys() == null
            ? Collections.emptyList()
            : dto.getVoting().getPublicKeys().stream()
                .map(
                    p ->
                        new AccountLinkVotingKey(
                            PublicKey.fromHexString(p.getPublicKey()),
                            (p.getStartEpoch()),
                            (p.getEndEpoch())))
                .collect(Collectors.toList());
    return new SupplementalAccountKeys(linked, node, vrf, voting);
  }

  private PublicKey toPublicKey(AccountLinkPublicKeyDTO dto) {
    if (dto == null || dto.getPublicKey() == null) {
      return null;
    }
    return PublicKey.fromHexString(dto.getPublicKey());
  }

  private AccountRoutesApi getClient() {
    return client;
  }
}
