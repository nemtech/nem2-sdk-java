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

import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Accounts can rent a namespace for an amount of blocks and after a this renew the contract. This
 * is done via a NamespaceRegistrationTransaction.
 *
 * @since 1.0
 */
public class NamespaceRegistrationTransaction extends Transaction {

    private final String namespaceName;
    private final NamespaceId namespaceId;
    private final Optional<BigInteger> duration;
    private final Optional<NamespaceId> parentId;
    private final NamespaceRegistrationType namespaceRegistrationType;

    /**
     * @param factory the factory with the configured fields.
     */
    NamespaceRegistrationTransaction(NamespaceRegistrationTransactionFactory factory) {
        super(factory);
        this.namespaceName = factory.getNamespaceName();
        this.namespaceRegistrationType = factory.getNamespaceRegistrationType();
        this.namespaceId = factory.getNamespaceId();
        this.duration = factory.getDuration();
        this.parentId = factory.getParentId();
    }

    /**
     * Returns namespace name.
     *
     * @return namespace name
     */
    public String getNamespaceName() {
        return namespaceName;
    }

    /**
     * Returns id of the namespace derived from namespaceName. When creating a sub namespace the
     * namespaceId is derived from namespaceName and parentId.
     *
     * @return namespace id
     */
    public NamespaceId getNamespaceId() {
        return namespaceId;
    }

    /**
     * Returns number of blocks a namespace is active.
     *
     * @return namespace renting duration
     */
    public Optional<BigInteger> getDuration() {
        return duration;
    }

    /**
     * The id of the parent sub namespace.
     *
     * @return sub namespace
     */
    public Optional<NamespaceId> getParentId() {
        return parentId;
    }

    /**
     * Returns namespace type either RootNamespace or SubNamespace.
     *
     * @return namespace type
     */
    public NamespaceRegistrationType getNamespaceRegistrationType() {
        return namespaceRegistrationType;
    }

}
