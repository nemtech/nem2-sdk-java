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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link AddressAliasTransaction}
 */
public class AddressAliasTransactionFactory extends TransactionFactory<AddressAliasTransaction> {

    private final AliasAction aliasAction;
    private final NamespaceId namespaceId;
    private final Address address;

    private AddressAliasTransactionFactory(
        final NetworkType networkType,
        final AliasAction aliasAction,
        final NamespaceId namespaceId,
        final Address address) {
        super(TransactionType.ADDRESS_ALIAS, networkType);
        Validate.notNull(aliasAction, "aliasAction must not be null");
        Validate.notNull(namespaceId, "namespaceId must not be null");
        Validate.notNull(address, "address must not be null");
        this.aliasAction = aliasAction;
        this.namespaceId = namespaceId;
        this.address = address;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param aliasAction Alias action.
     * @param namespaceId Namespace id.
     * @param address Address.
     * @return Address alias transaction.
     */
    public static AddressAliasTransactionFactory create(NetworkType networkType,
        AliasAction aliasAction, NamespaceId namespaceId, Address address) {
        return new AddressAliasTransactionFactory(networkType, aliasAction, namespaceId, address);
    }

    /**
     * Gets the alias action.
     *
     * @return Alias Action.
     */
    public AliasAction getAliasAction() {
        return this.aliasAction;
    }

    /**
     * Gets the namespace id.
     *
     * @return Namespace id.
     */
    public NamespaceId getNamespaceId() {
        return this.namespaceId;
    }

    /**
     * Gets the address.
     *
     * @return Address of the account.
     */
    public Address getAddress() {
        return this.address;
    }


    @Override
    public AddressAliasTransaction build() {
        return new AddressAliasTransaction(this);
    }
}
