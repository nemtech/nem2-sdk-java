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

package io.nem.sdk.infrastructure.vertx;

import static io.nem.core.utils.MapperUtils.toNamespaceId;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AddressAlias;
import io.nem.sdk.model.namespace.Alias;
import io.nem.sdk.model.namespace.AliasType;
import io.nem.sdk.model.namespace.EmptyAlias;
import io.nem.sdk.model.namespace.MosaicAlias;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.sdk.openapi.vertx.api.NamespaceRoutesApi;
import io.nem.sdk.openapi.vertx.api.NamespaceRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.AccountIds;
import io.nem.sdk.openapi.vertx.model.NamespaceDTO;
import io.nem.sdk.openapi.vertx.model.NamespaceIds;
import io.nem.sdk.openapi.vertx.model.NamespaceInfoDTO;
import io.nem.sdk.openapi.vertx.model.NamespaceNameDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Namespace http repository.
 *
 * @since 1.0
 */
public class NamespaceRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    NamespaceRepository {

    private final NamespaceRoutesApi client;

    public NamespaceRepositoryVertxImpl(ApiClient apiClient, Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
        client = new NamespaceRoutesApiImpl(apiClient);
    }

    public NamespaceRoutesApi getClient() {
        return client;
    }

    @Override
    public Observable<NamespaceInfo> getNamespace(NamespaceId namespaceId) {
        Consumer<Handler<AsyncResult<NamespaceInfoDTO>>> callback = handler -> getClient()
            .getNamespace(namespaceId.getIdAsHex(), handler);
        return exceptionHandling(call(callback).map(this::toNamespaceInfo));
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

        Consumer<Handler<AsyncResult<List<NamespaceInfoDTO>>>> callback = handler ->
            client.getNamespacesFromAccount(address.plain(),
                getPageSize(queryParams),
                getId(queryParams),
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toNamespaceInfo).toList()
                .toObservable());
    }


    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccounts(
        List<Address> addresses, QueryParams queryParams) {
        return this.getNamespacesFromAccounts(addresses, Optional.of(queryParams));
    }

    @Override
    public Observable<List<NamespaceInfo>> getNamespacesFromAccounts(List<Address> addresses) {
        return this.getNamespacesFromAccounts(addresses, Optional.empty());
    }

    private Observable<List<NamespaceInfo>> getNamespacesFromAccounts(
        List<Address> addresses, Optional<QueryParams> queryParams) {

        AccountIds accounts = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(
                Collectors.toList()));

        Consumer<Handler<AsyncResult<List<NamespaceInfoDTO>>>> callback = handler -> client
            .getNamespacesFromAccounts(accounts, handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toNamespaceInfo).toList()
                .toObservable());
    }


    @Override
    public Observable<List<NamespaceName>> getNamespaceNames(List<NamespaceId> namespaceIds) {

        NamespaceIds ids = new NamespaceIds()
            .namespaceIds(namespaceIds.stream()
                .map(NamespaceId::getIdAsHex)
                .collect(Collectors.toList()));

        Consumer<Handler<AsyncResult<List<NamespaceNameDTO>>>> callback = handler ->
            client.getNamespacesNames(ids,
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toNamespaceName).toList()
                .toObservable());
    }


    /**
     * Gets the MosaicId from a MosaicAlias
     *
     * @param namespaceId - the namespaceId of the namespace
     * @return Observable of {@link MosaicId}
     */
    @Override
    public Observable<MosaicId> getLinkedMosaicId(NamespaceId namespaceId) {
        Consumer<Handler<AsyncResult<NamespaceInfoDTO>>> callback = handler -> getClient()
            .getNamespace(namespaceId.getIdAsHex(), handler);
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
        Consumer<Handler<AsyncResult<NamespaceInfoDTO>>> callback = handler -> getClient()
            .getNamespace(namespaceId.getIdAsHex(), handler);
        return exceptionHandling(call(callback).map(namespaceInfoDTO -> this
            .toAddress(namespaceInfoDTO.getNamespace())));
    }

    private NamespaceName toNamespaceName(NamespaceNameDTO dto) {
        return new NamespaceName(
            toNamespaceId(dto.getId()),
            dto.getName(), Optional.ofNullable(toNamespaceId(dto.getParentId())));
    }


    /**
     * Create a NamespaceInfo from a NamespaceInfoDTO and a NetworkType
     *
     * @param namespaceInfoDTO, networkType
     * @internal
     * @access private
     */
    private NamespaceInfo toNamespaceInfo(
        NamespaceInfoDTO namespaceInfoDTO) {
        return new NamespaceInfo(
            namespaceInfoDTO.getMeta().getActive(),
            namespaceInfoDTO.getMeta().getIndex(),
            namespaceInfoDTO.getMeta().getId(),
            NamespaceRegistrationType
                .rawValueOf(namespaceInfoDTO.getNamespace().getRegistrationType().getValue()),
            namespaceInfoDTO.getNamespace().getDepth(),
            this.extractLevels(namespaceInfoDTO),
            toNamespaceId(namespaceInfoDTO.getNamespace().getParentId()),
            new PublicAccount(namespaceInfoDTO.getNamespace().getOwnerPublicKey(),
                getNetworkTypeBlocking()),
            namespaceInfoDTO.getNamespace().getStartHeight(),
            namespaceInfoDTO.getNamespace().getEndHeight(),
            this.extractAlias(namespaceInfoDTO.getNamespace()));
    }

    /**
     * Create a MosaicId from a NamespaceDTO
     *
     * @internal
     * @access private
     */
    private MosaicId toMosaicId(NamespaceDTO namespaceDTO) {
        MosaicId mosaicId = null;
        if (namespaceDTO.getAlias() != null && AliasType.MOSAIC.getValue()
            .equals(namespaceDTO.getAlias().getType().getValue())) {
            mosaicId = MapperUtils.toMosaicId(namespaceDTO.getAlias().getMosaicId());
        }
        return mosaicId;
    }

    /**
     * Create a Address from a NamespaceDTO
     *
     * @internal
     * @access private
     */
    private Address toAddress(NamespaceDTO namespaceDTO) {
        Address address = null;
        if (namespaceDTO.getAlias() != null && AliasType.ADDRESS.getValue()
            .equals(namespaceDTO.getAlias().getType().getValue())) {
            String encodedAddress = namespaceDTO.getAlias().getAddress();
            if (encodedAddress != null) {
                address = MapperUtils.toAddressFromEncoded(encodedAddress);
            }
        }
        return address;
    }

    /**
     * Extract a list of NamespaceId levels from a NamespaceInfoDTO
     *
     * @internal
     * @access private
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
     *
     * @param namespaceDTO the dto
     * @return the address, mosaic or empty alias.
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

}
