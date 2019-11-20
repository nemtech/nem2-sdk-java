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

package io.nem.sdk.model.namespace;

import io.nem.sdk.model.blockchain.NetworkType;
import java.util.Optional;

/**
 * The namespace name info structure describes basic information of a namespace and name.
 */
public class NamespaceName {

    private final NamespaceId namespaceId;
    private final String name;
    private final Optional<NamespaceId> parentId;

    public NamespaceName(String name, NetworkType networkType) {
        this(NamespaceId.createFromName(name, networkType), name, Optional.empty());
    }

    public NamespaceName(NamespaceId namespaceId, String name) {
        this(namespaceId, name, Optional.empty());
    }

    public NamespaceName(NamespaceId namespaceId, String name, NamespaceId parentId) {
        this(namespaceId, name, Optional.of(parentId));
    }

    public NamespaceName(NamespaceId namespaceId, String name, Optional<NamespaceId> parentId) {
        this.namespaceId = namespaceId;
        this.name = name;
        this.parentId = parentId;
    }

    /**
     * Returns the namespace id
     *
     * @return the namespace id
     */
    public NamespaceId getNamespaceId() {
        return namespaceId;
    }

    /**
     * Returns the namespace name
     *
     * @return the namespace name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an optional that could contain the parent namespace id
     *
     * @return the parent id if available
     */
    public Optional<NamespaceId> getParentId() {
        return parentId;
    }


}
