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

package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.api.AliasService;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.AliasType;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.reactivex.Observable;
import org.apache.commons.lang3.Validate;

/**
 * Implementation of the alias service.
 */
public class AliasServiceImpl implements AliasService {

    private final NamespaceRepository namespaceRepository;

    /**
     * Constructor
     *
     * @param repositoryFactory repository factory.
     */
    public AliasServiceImpl(RepositoryFactory repositoryFactory) {
        this.namespaceRepository = repositoryFactory.createNamespaceRepository();
    }

    @Override
    public Observable<MosaicId> resolveMosaicId(UnresolvedMosaicId unresolvedMosaicId) {
        if (unresolvedMosaicId.isAlias()) {
            NamespaceId alias = (NamespaceId) unresolvedMosaicId;
            return namespaceRepository.getNamespace(alias)
                .map(namespaceInfo -> {
                    Validate.isTrue(namespaceInfo.getAlias().getType() == AliasType.MOSAIC,
                        "Alias is not Mosaic");
                    return (MosaicId) namespaceInfo.getAlias().getAliasValue();
                })
                .onErrorResumeNext(e -> {
                    return Observable.error(new IllegalArgumentException(
                        "MosaicId could not be resolved from alias " + alias.getIdAsHex(), e));
                });
        } else {
            return Observable.just((MosaicId) unresolvedMosaicId);
        }

    }

    @Override
    public Observable<Address> resolveAddress(UnresolvedAddress unresolvedAddress) {
        if (unresolvedAddress.isAlias()) {
            NamespaceId alias = (NamespaceId) unresolvedAddress;
            return namespaceRepository.getNamespace(alias)
                .map(namespaceInfo -> {
                    Validate.isTrue(namespaceInfo.getAlias().getType() == AliasType.ADDRESS,
                        "Alias is not address");
                    return (Address) namespaceInfo.getAlias().getAliasValue();
                })
                .onErrorResumeNext(e -> {
                    return Observable.error(new IllegalArgumentException(
                        "Address could not be resolved from alias " + alias.getIdAsHex(), e));
                });
        } else {
            return Observable.just((Address) unresolvedAddress);
        }

    }

}
