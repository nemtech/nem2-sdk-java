/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import static io.nem.core.utils.MapperUtils.toNamespaceId;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.namespace.AddressAlias;
import io.nem.sdk.model.namespace.Alias;
import io.nem.sdk.model.namespace.AliasType;
import io.nem.sdk.model.namespace.EmptyAlias;
import io.nem.sdk.model.namespace.MosaicAlias;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.NamespaceRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountsNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicsNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceNameDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespacesInfoDTO;
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
public class NamespaceRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    NamespaceRepository {

    private final NamespaceRoutesApi client;

    private final Observable<NetworkType> networkTypeObservable;

    public NamespaceRepositoryOkHttpImpl(ApiClient apiClient,
        Observable<NetworkType> networkTypeObservable) {
        super(apiClient);
        this.client = new NamespaceRoutesApi(apiClient);
        this.networkTypeObservable = networkTypeObservable;
    }

    public NamespaceRoutesApi getClient() {
        return client;
    }

    @Override
    public Observable<NamespaceInfo> getNamespace(NamespaceId namespaceId) {
        Callable<NamespaceInfoDTO> callback = () -> getClient()
            .getNamespace(namespaceId.getIdAsHex());
        return exceptionHandling(networkTypeObservable.flatMap(networkType -> call(callback).map(
            namespaceInfoDTO -> toNamespaceInfo(namespaceInfoDTO, networkType))));
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccount(
        Address address, QueryParams queryParams) {
        return this.getNamespacesFromAccount(address, Optional.of(queryParams));
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccount(Address address) {
        return this.getNamespacesFromAccount(address, Optional.empty());
    }

    private Observable<List<NamespaceInfo>> getNamespacesFromAccount(
        Address address, Optional<QueryParams> queryParams) {

        Callable<NamespacesInfoDTO> callback = () ->
            getClient().getNamespacesFromAccount(address.plain(),
                getPageSize(queryParams),
                getId(queryParams)
            );

        return exceptionHandling(networkTypeObservable.flatMap(networkType ->
            call(callback).flatMapIterable(NamespacesInfoDTO::getNamespaces)
                .map(namespaceInfoDTO -> toNamespaceInfo(namespaceInfoDTO, networkType)).toList()
                .toObservable()));
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccounts(List<Address> addresses) {

        AccountIds accounts = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(
                Collectors.toList()));

        Callable<NamespacesInfoDTO> callback = () ->
            getClient()
                .getNamespacesFromAccounts(accounts);

        return exceptionHandling(networkTypeObservable.flatMap(networkType ->
            call(callback).flatMapIterable(NamespacesInfoDTO::getNamespaces)
                .map(namespaceInfoDTO -> toNamespaceInfo(namespaceInfoDTO, networkType)).toList()
                .toObservable()));
    }


    @Override
    public Observable<List<NamespaceName>> getNamespaceNames(List<NamespaceId> namespaceIds) {

        NamespaceIds ids = new NamespaceIds()
            .namespaceIds(namespaceIds.stream().map(NamespaceId::getIdAsHex)
                .collect(Collectors.toList()));

        Callable<List<NamespaceNameDTO>> callback = () ->
            getClient().getNamespacesNames(ids);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toNamespaceName).toList()
                .toObservable());
    }

    private NamespaceName toNamespaceName(NamespaceNameDTO dto) {
        return new NamespaceName(
            toNamespaceId(dto.getId()),
            dto.getName(), Optional.ofNullable(toNamespaceId(dto.getParentId())));
    }


    /**
     * Gets the MosaicId from a MosaicAlias
     *
     * @param namespaceId - the namespaceId of the namespace
     * @return Observable of {@link MosaicId}
     */
    @Override
    public Observable<MosaicId> getLinkedMosaicId(NamespaceId namespaceId) {
        Callable<NamespaceInfoDTO> callback = () -> getClient()
            .getNamespace(namespaceId.getIdAsHex());
        return exceptionHandling(call(callback).map(namespaceInfoDTO -> this
            .toMosaicId(namespaceInfoDTO.getNamespace())));
    }

    /**
     * Gets the Address from a AddressAlias
     *
     * @param namespaceId - the namespaceId of the namespace
     * @return Observable of {@link MosaicId}
     */
    @Override
    public Observable<Address> getLinkedAddress(NamespaceId namespaceId) {
        Callable<NamespaceInfoDTO> callback = () -> getClient()
            .getNamespace(namespaceId.getIdAsHex());
        return exceptionHandling(call(callback).map(namespaceInfoDTO -> this
            .toAddress(namespaceInfoDTO.getNamespace())));
    }


    @Override
    public Observable<List<AccountNames>> getAccountsNames(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        return getAccountNames(accountIds);
    }

    private Observable<List<AccountNames>> getAccountNames(AccountIds accountIds) {
        Callable<AccountsNamesDTO> callback = () -> getClient()
            .getAccountsNames(accountIds);
        return exceptionHandling(
            call(callback).map(AccountsNamesDTO::getAccountNames).flatMapIterable(item -> item)
                .map(this::toAccountNames).toList().toObservable());
    }

    /**
     * Converts a {@link AccountNamesDTO} into a {@link AccountNames}
     *
     * @param dto {@link AccountNamesDTO}
     * @return {@link AccountNames}
     */
    private AccountNames toAccountNames(AccountNamesDTO dto) {
        return new AccountNames(MapperUtils.toAddressFromEncoded(dto.getAddress()),
            dto.getNames().stream().map(NamespaceName::new).collect(Collectors.toList()));
    }


    @Override
    public Observable<List<MosaicNames>> getMosaicsNames(List<MosaicId> ids) {
        MosaicIds mosaicIds = new MosaicIds();
        mosaicIds.mosaicIds(ids.stream()
            .map(MosaicId::getIdAsHex)
            .collect(Collectors.toList()));
        Callable<MosaicsNamesDTO> callback = () -> getClient()
            .getMosaicsNames(mosaicIds);
        return exceptionHandling(
            call(callback).map(MosaicsNamesDTO::getMosaicNames).flatMapIterable(item -> item)
                .map(this::toMosaicNames).toList()
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
     * @param networkType the network type
     */
    private NamespaceInfo toNamespaceInfo(
        NamespaceInfoDTO namespaceInfoDTO, NetworkType networkType) {
        return new NamespaceInfo(
            namespaceInfoDTO.getMeta().getActive(),
            namespaceInfoDTO.getMeta().getIndex(),
            namespaceInfoDTO.getMeta().getId(),
            NamespaceRegistrationType
                .rawValueOf(namespaceInfoDTO.getNamespace().getRegistrationType().getValue()),
            namespaceInfoDTO.getNamespace().getDepth(),
            this.extractLevels(namespaceInfoDTO),
            toNamespaceId(namespaceInfoDTO.getNamespace().getParentId()),
            new PublicAccount(namespaceInfoDTO.getNamespace().getOwnerPublicKey(), networkType),
            namespaceInfoDTO.getNamespace().getStartHeight(),
            namespaceInfoDTO.getNamespace().getEndHeight(),
            this.extractAlias(namespaceInfoDTO.getNamespace()));
    }


    /**
     * Extract a list of NamespaceId levels from a NamespaceInfoDTO
     */
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

    /**
     * Extract the alias from a NamespaceDTO
     */
    private Alias extractAlias(NamespaceDTO namespaceDTO) {

        Alias alias = new EmptyAlias();
        if (namespaceDTO.getAlias() != null) {
            if (namespaceDTO.getAlias().getType().getValue().equals(AliasType.MOSAIC.getValue())) {
                return new MosaicAlias(toMosaicId(namespaceDTO));
            } else if (namespaceDTO.getAlias().getType().getValue().equals(AliasType.ADDRESS
                .getValue())) {
                return new AddressAlias(toAddress(namespaceDTO));
            }
        }
        return alias;
    }

    /**
     * Create a MosaicId from a NamespaceDTO
     */
    private MosaicId toMosaicId(NamespaceDTO namespaceDTO) {
        if (namespaceDTO.getAlias() != null && AliasType.MOSAIC.getValue()
            .equals(namespaceDTO.getAlias().getType().getValue())) {
            return MapperUtils.toMosaicId(namespaceDTO.getAlias().getMosaicId());
        } else {
            return null;
        }
    }

    /**
     * Create a Address from a NamespaceDTO
     */
    private Address toAddress(NamespaceDTO namespaceDTO) {
        if (namespaceDTO.getAlias() != null && AliasType.ADDRESS.getValue()
            .equals(namespaceDTO.getAlias().getType().getValue())) {
            return MapperUtils.toAddressFromEncoded(namespaceDTO.getAlias().getAddress());
        } else {
            return null;
        }
    }
}
