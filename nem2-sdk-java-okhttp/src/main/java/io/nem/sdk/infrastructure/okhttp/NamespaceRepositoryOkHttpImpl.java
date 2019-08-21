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

import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AddressAlias;
import io.nem.sdk.model.namespace.Alias;
import io.nem.sdk.model.namespace.AliasType;
import io.nem.sdk.model.namespace.EmptyAlias;
import io.nem.sdk.model.namespace.MosaicAlias;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.openapi.okhttp_gson.api.NamespaceRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceIds;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceNameDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
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

    public NamespaceRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new NamespaceRoutesApi(apiClient);
    }

    public NamespaceRoutesApi getClient() {
        return client;
    }

    @Override
    public Observable<NamespaceInfo> getNamespace(NamespaceId namespaceId) {
        Callable<NamespaceInfoDTO> callback = () -> getClient()
            .getNamespace(UInt64.bigIntegerToHex(namespaceId.getId()));
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

        Callable<List<NamespaceInfoDTO>> callback = () ->
            client.getNamespacesFromAccount(address.plain(),
                getPageSize(queryParams),
                getId(queryParams)
            );

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

        Callable<List<NamespaceInfoDTO>> callback = () ->
            client.getNamespacesFromAccounts(accounts);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toNamespaceInfo).toList()
                .toObservable());
    }


    @Override
    public Observable<List<NamespaceName>> getNamespaceNames(List<NamespaceId> namespaceIds) {

        NamespaceIds ids = new NamespaceIds()
            .namespaceIds(namespaceIds.stream()
                .map(id -> UInt64.bigIntegerToHex(id.getId()))
                .collect(Collectors.toList()));

        Callable<List<NamespaceNameDTO>> callback = () ->
            client.getNamespacesNames(ids);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toNamespaceName).toList()
                .toObservable());
    }

    private NamespaceName toNamespaceName(NamespaceNameDTO namespaceNameDTO) {
        if (namespaceNameDTO.getParentId() != null) {
            return new NamespaceName(
                new NamespaceId(extractIntArray(namespaceNameDTO.getNamespaceId())),
                namespaceNameDTO.getName(),
                new NamespaceId(extractIntArray(namespaceNameDTO.getParentId())));
        } else {
            return new NamespaceName(
                new NamespaceId(extractIntArray(namespaceNameDTO.getNamespaceId())),
                namespaceNameDTO.getName());
        }
    }


    /**
     * Gets the MosaicId from a MosaicAlias
     *
     * @param namespaceId - the namespaceId of the namespace
     * @return Observable of <{@link MosaicId}>
     */
    @Override
    public Observable<MosaicId> getLinkedMosaicId(NamespaceId namespaceId) {
        Callable<NamespaceInfoDTO> callback = () -> getClient()
            .getNamespace(UInt64.bigIntegerToHex(namespaceId.getId()));
        return exceptionHandling(call(callback).map(namespaceInfoDTO -> this
            .toMosaicId(namespaceInfoDTO.getNamespace())));
    }

    /**
     * Gets the Address from a AddressAlias
     *
     * @param namespaceId - the namespaceId of the namespace
     * @return Observable of <{@link MosaicId}>
     */
    @Override
    public Observable<Address> getLinkedAddress(NamespaceId namespaceId) {
        Callable<NamespaceInfoDTO> callback = () -> getClient()
            .getNamespace(UInt64.bigIntegerToHex(namespaceId.getId()));
        return exceptionHandling(call(callback).map(namespaceInfoDTO -> this
            .toAddress(namespaceInfoDTO.getNamespace())));
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
            NamespaceType.rawValueOf(namespaceInfoDTO.getNamespace().getType().getValue()),
            namespaceInfoDTO.getNamespace().getDepth(),
            this.extractLevels(namespaceInfoDTO),
            new NamespaceId(extractIntArray(namespaceInfoDTO.getNamespace().getParentId())),
            new PublicAccount(namespaceInfoDTO.getNamespace().getOwner(),
                getNetworkTypeBlocking()),
            extractIntArray(namespaceInfoDTO.getNamespace().getStartHeight()),
            extractIntArray(namespaceInfoDTO.getNamespace().getEndHeight()),
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
        if (namespaceDTO.getAlias() != null) {
            if (AliasType.Mosaic.getValue().equals(namespaceDTO.getAlias().getType().getValue())) {
                mosaicId = new MosaicId(extractIntArray(namespaceDTO.getAlias().getMosaicId()));
            }
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
        if (namespaceDTO.getAlias() != null) {
            if (AliasType.Address.getValue().equals(namespaceDTO.getAlias().getType().getValue())) {
                String rawAddress = namespaceDTO.getAlias().getAddress();
                address = Address.createFromRawAddress(rawAddress);
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
        List<NamespaceId> levels = new ArrayList<NamespaceId>();
        if (namespaceInfoDTO.getNamespace().getLevel0() != null) {
            levels
                .add(new NamespaceId(
                    extractIntArray(namespaceInfoDTO.getNamespace().getLevel0())));
        }

        if (namespaceInfoDTO.getNamespace().getLevel1() != null) {
            levels
                .add(new NamespaceId(
                    extractIntArray(namespaceInfoDTO.getNamespace().getLevel1())));
        }

        if (namespaceInfoDTO.getNamespace().getLevel2() != null) {
            levels
                .add(new NamespaceId(
                    extractIntArray(namespaceInfoDTO.getNamespace().getLevel2())));
        }

        return levels;
    }

    /**
     * Extract the alias from a NamespaceDTO
     *
     * @internal
     * @access private
     */
    private Alias extractAlias(NamespaceDTO namespaceDTO) {

        Alias alias = new EmptyAlias();
        if (namespaceDTO.getAlias() != null) {
            if (namespaceDTO.getAlias().getType().getValue() == AliasType.Mosaic.getValue()) {
                BigInteger mosaicId = extractIntArray(namespaceDTO.getAlias().getMosaicId());
                return new MosaicAlias(new MosaicId(mosaicId));
            } else if (namespaceDTO.getAlias().getType().getValue() == AliasType.Address
                .getValue()) {
                String rawAddress = namespaceDTO.getAlias().getAddress();
                return new AddressAlias(Address.createFromRawAddress(rawAddress));
            }
        }

        return alias;
    }
}