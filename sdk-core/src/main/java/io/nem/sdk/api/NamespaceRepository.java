/*
 * Copyright 2018 NEM
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

package io.nem.sdk.api;

import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.reactivex.Observable;
import java.util.List;

/**
 * Namespace interface repository.
 *
 * @since 1.0
 */
public interface NamespaceRepository {

    /**
     * Gets the NamespaceInfo for a given namespaceId.
     *
     * @param namespaceId NamespaceId
     * @return {@link Observable} of {@link NamespaceInfo}
     */
    Observable<NamespaceInfo> getNamespace(NamespaceId namespaceId);

    /**
     * Gets list of NamespaceInfo for an account. With pagination.
     *
     * @param address Address
     * @param queryParams QueryParans
     * @return {@link Observable} of {@link NamespaceInfo} List
     */
    Observable<List<NamespaceInfo>> getNamespacesFromAccount(
        Address address, QueryParams queryParams);

    /**
     * Gets list of NamespaceInfo for an account.
     *
     * @param address Address
     * @return {@link Observable} of {@link NamespaceInfo} List
     */
    Observable<List<NamespaceInfo>> getNamespacesFromAccount(Address address);

    /**
     * Gets list of NamespaceInfo for different account. With pagination.
     *
     * @param addresses List of Address
     * @param queryParams QueryParams
     * @return {@link Observable} of {@link NamespaceInfo} List
     */
    Observable<List<NamespaceInfo>> getNamespacesFromAccounts(
        List<Address> addresses, QueryParams queryParams);

    /**
     * Gets list of NamespaceInfo for different account.
     *
     * @param addresses List of Address
     * @return {@link Observable} of {@link NamespaceInfo} List
     */
    Observable<List<NamespaceInfo>> getNamespacesFromAccounts(List<Address> addresses);

    /**
     * Gets list of NamespaceName for different namespaceIds.
     *
     * @param namespaceIds List of NamespaceId
     * @return {@link Observable} of {@link NamespaceName} List
     */
    Observable<List<NamespaceName>> getNamespaceNames(List<NamespaceId> namespaceIds);

    /**
     * Gets the MosaicId from a MosaicAlias
     *
     * @param namespaceId - the namespaceId of the namespace
     * @return {@link Observable} of {@link MosaicId}
     */
    Observable<MosaicId> getLinkedMosaicId(NamespaceId namespaceId);

    /**
     * Gets the Address from a AddressAlias
     *
     * @param namespaceId - the namespaceId of the namespace
     * @return Observable of {@link Address}
     */
    Observable<Address> getLinkedAddress(NamespaceId namespaceId);


    /**
     * Gets AccountNames for different accounts based on their addresses. The names are namespaces
     * linked using address aliases.
     *
     * @param addresses {@link List} of {@link Address}
     * @return Observable {@link List} of {@link AccountNames}
     */
    Observable<List<AccountNames>> getAccountsNames(List<Address> addresses);


    /**
     * Gets MosaicNames for different accounts. The names are namespaces linked using mosaic
     * aliases.
     *
     * @param mosaicIds {@link List} of {@link MosaicId}
     * @return {@link Observable} of {@link MosaicNames} List
     */

    Observable<List<MosaicNames>> getMosaicsNames(List<MosaicId> mosaicIds);
}
