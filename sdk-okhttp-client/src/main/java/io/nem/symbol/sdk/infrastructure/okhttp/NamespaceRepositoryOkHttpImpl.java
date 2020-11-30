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

import static io.nem.symbol.core.utils.MapperUtils.toNamespaceId;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NamespaceSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.account.AccountNames;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.namespace.AddressAlias;
import io.nem.symbol.sdk.model.namespace.Alias;
import io.nem.symbol.sdk.model.namespace.AliasType;
import io.nem.symbol.sdk.model.namespace.EmptyAlias;
import io.nem.symbol.sdk.model.namespace.MosaicAlias;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.NamespaceRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountsNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Addresses;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AliasTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicsNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceNameDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespacePage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceRegistrationTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Namespace http repository.
 *
 * @since 1.0
 */
public class NamespaceRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements NamespaceRepository {

  private final NamespaceRoutesApi client;

  public NamespaceRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new NamespaceRoutesApi(apiClient);
  }

  public NamespaceRoutesApi getClient() {
    return client;
  }

  @Override
  public Observable<NamespaceInfo> getNamespace(NamespaceId namespaceId) {
    return call(() -> getClient().getNamespace(namespaceId.getIdAsHex()), this::toNamespaceInfo);
  }

  @Override
  public Observable<MerkleStateInfo> getNamespaceMerkle(NamespaceId namespaceId) {
    return call(
        () -> getClient().getNamespaceMerkle(namespaceId.getIdAsHex()), this::toMerkleStateInfo);
  }

  @Override
  public Observable<Page<NamespaceInfo>> search(NamespaceSearchCriteria criteria) {

    String ownerAddress = toDto(criteria.getOwnerAddress());
    NamespaceRegistrationTypeEnum registrationType =
        criteria.getRegistrationType() == null
            ? null
            : NamespaceRegistrationTypeEnum.fromValue(criteria.getRegistrationType().getValue());
    String level0 = criteria.getLevel0();
    AliasTypeEnum aliasType =
        criteria.getAliasType() == null
            ? null
            : AliasTypeEnum.fromValue(criteria.getAliasType().getValue());
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());
    Callable<NamespacePage> callback =
        () ->
            getClient()
                .searchNamespaces(
                    ownerAddress,
                    registrationType,
                    level0,
                    aliasType,
                    pageSize,
                    pageNumber,
                    offset,
                    order);

    return exceptionHandling(
        call(callback)
            .map(
                page ->
                    this.toPage(
                        page.getPagination(),
                        page.getData().stream()
                            .map(this::toNamespaceInfo)
                            .collect(Collectors.toList()))));
  }

  @Override
  public Observable<List<NamespaceName>> getNamespaceNames(List<NamespaceId> namespaceIds) {

    NamespaceIds ids =
        new NamespaceIds()
            .namespaceIds(
                namespaceIds.stream().map(NamespaceId::getIdAsHex).collect(Collectors.toList()));

    Callable<List<NamespaceNameDTO>> callback = () -> getClient().getNamespacesNames(ids);

    return exceptionHandling(
        call(callback)
            .flatMapIterable(item -> item)
            .map(this::toNamespaceName)
            .toList()
            .toObservable());
  }

  private NamespaceName toNamespaceName(NamespaceNameDTO dto) {
    return new NamespaceName(
        toNamespaceId(dto.getId()),
        dto.getName(),
        Optional.ofNullable(toNamespaceId(dto.getParentId())));
  }

  /**
   * Gets the MosaicId from a MosaicAlias
   *
   * @param namespaceId - the namespaceId of the namespace
   * @return Observable of {@link MosaicId}
   */
  @Override
  public Observable<MosaicId> getLinkedMosaicId(NamespaceId namespaceId) {
    Callable<NamespaceInfoDTO> callback = () -> getClient().getNamespace(namespaceId.getIdAsHex());
    return exceptionHandling(
        call(callback).map(namespaceInfoDTO -> this.toMosaicId(namespaceInfoDTO.getNamespace())));
  }

  /**
   * Gets the Address from a AddressAlias
   *
   * @param namespaceId - the namespaceId of the namespace
   * @return Observable of {@link MosaicId}
   */
  @Override
  public Observable<Address> getLinkedAddress(NamespaceId namespaceId) {
    Callable<NamespaceInfoDTO> callback = () -> getClient().getNamespace(namespaceId.getIdAsHex());
    return exceptionHandling(
        call(callback).map(namespaceInfoDTO -> this.toAddress(namespaceInfoDTO.getNamespace())));
  }

  @Override
  public Observable<List<AccountNames>> getAccountsNames(List<Address> addresses) {
    Addresses addressesDto =
        new Addresses()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
    return getAccountNames(addressesDto);
  }

  private Observable<List<AccountNames>> getAccountNames(Addresses accountIds) {
    Callable<AccountsNamesDTO> callback = () -> getClient().getAccountsNames(accountIds);
    return exceptionHandling(
        call(callback)
            .map(AccountsNamesDTO::getAccountNames)
            .flatMapIterable(item -> item)
            .map(this::toAccountNames)
            .toList()
            .toObservable());
  }

  /**
   * Converts a {@link AccountNamesDTO} into a {@link AccountNames}
   *
   * @param dto {@link AccountNamesDTO}
   * @return {@link AccountNames}
   */
  private AccountNames toAccountNames(AccountNamesDTO dto) {
    return new AccountNames(
        MapperUtils.toAddress(dto.getAddress()),
        dto.getNames().stream().map(NamespaceName::new).collect(Collectors.toList()));
  }

  @Override
  public Observable<List<MosaicNames>> getMosaicsNames(List<MosaicId> ids) {
    MosaicIds mosaicIds = new MosaicIds();
    mosaicIds.mosaicIds(ids.stream().map(MosaicId::getIdAsHex).collect(Collectors.toList()));
    Callable<MosaicsNamesDTO> callback = () -> getClient().getMosaicsNames(mosaicIds);
    return exceptionHandling(
        call(callback)
            .map(MosaicsNamesDTO::getMosaicNames)
            .flatMapIterable(item -> item)
            .map(this::toMosaicNames)
            .toList()
            .toObservable());
  }

  /**
   * Converts a {@link MosaicNamesDTO} into a {@link MosaicNames}
   *
   * @param dto {@link MosaicNamesDTO}
   * @return {@link MosaicNames}
   */
  private MosaicNames toMosaicNames(MosaicNamesDTO dto) {
    return new MosaicNames(
        MapperUtils.toMosaicId(dto.getMosaicId()),
        dto.getNames().stream().map(NamespaceName::new).collect(Collectors.toList()));
  }

  /**
   * Create a NamespaceInfo from a NamespaceInfoDTO and a NetworkType
   *
   * @param namespaceInfoDTO, networkType
   */
  private NamespaceInfo toNamespaceInfo(NamespaceInfoDTO namespaceInfoDTO) {
    return new NamespaceInfo(
        namespaceInfoDTO.getId(),
        namespaceInfoDTO.getNamespace().getVersion(),
        namespaceInfoDTO.getMeta().getActive(),
        namespaceInfoDTO.getMeta().getIndex(),
        NamespaceRegistrationType.rawValueOf(
            namespaceInfoDTO.getNamespace().getRegistrationType().getValue()),
        namespaceInfoDTO.getNamespace().getDepth(),
        this.extractLevels(namespaceInfoDTO),
        toNamespaceId(namespaceInfoDTO.getNamespace().getParentId()),
        MapperUtils.toAddress(namespaceInfoDTO.getNamespace().getOwnerAddress()),
        namespaceInfoDTO.getNamespace().getStartHeight(),
        namespaceInfoDTO.getNamespace().getEndHeight(),
        this.extractAlias(namespaceInfoDTO.getNamespace()));
  }

  /** Extract a list of NamespaceId levels from a NamespaceInfoDTO */
  private List<NamespaceId> extractLevels(NamespaceInfoDTO namespaceInfoDTO) {
    List<NamespaceId> levels = new ArrayList<>();
    if (namespaceInfoDTO.getNamespace().getLevel0() != null) {
      levels.add(toNamespaceId(namespaceInfoDTO.getNamespace().getLevel0()));
    }

    if (namespaceInfoDTO.getNamespace().getLevel1() != null) {
      levels.add(toNamespaceId(namespaceInfoDTO.getNamespace().getLevel1()));
    }

    if (namespaceInfoDTO.getNamespace().getLevel2() != null) {
      levels.add(toNamespaceId(namespaceInfoDTO.getNamespace().getLevel2()));
    }

    return levels;
  }

  /** Extract the alias from a NamespaceDTO */
  private Alias<?> extractAlias(NamespaceDTO namespaceDTO) {
    if (namespaceDTO.getAlias() != null) {
      if (namespaceDTO.getAlias().getType().getValue().equals(AliasType.MOSAIC.getValue())) {
        return new MosaicAlias(toMosaicId(namespaceDTO));
      } else if (namespaceDTO
          .getAlias()
          .getType()
          .getValue()
          .equals(AliasType.ADDRESS.getValue())) {
        return new AddressAlias(toAddress(namespaceDTO));
      }
    }
    return new EmptyAlias();
  }

  /** Create a MosaicId from a NamespaceDTO */
  private MosaicId toMosaicId(NamespaceDTO namespaceDTO) {
    if (namespaceDTO.getAlias() != null
        && AliasType.MOSAIC.getValue() == (namespaceDTO.getAlias().getType().getValue())) {
      return MapperUtils.toMosaicId(namespaceDTO.getAlias().getMosaicId());
    } else {
      return null;
    }
  }

  /** Create a Address from a NamespaceDTO */
  private Address toAddress(NamespaceDTO namespaceDTO) {
    if (namespaceDTO.getAlias() != null
        && AliasType.ADDRESS.getValue() == (namespaceDTO.getAlias().getType().getValue())) {
      return MapperUtils.toAddress(namespaceDTO.getAlias().getAddress());
    } else {
      return null;
    }
  }
}
