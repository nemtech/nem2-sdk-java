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

package io.nem.symbol.sdk.model.namespace;

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import java.math.BigInteger;
import java.util.List;

/**
 * NamespaceInfo contains the state information of a namespace.
 *
 * @since 1.0
 */
public class NamespaceInfo {

    private final boolean active;
    private final Integer index;
    private final String metaId;
    private final NamespaceRegistrationType registrationType;
    private final Integer depth;
    private final List<NamespaceId> levels;
    private final NamespaceId parentId;
    private final UnresolvedAddress ownerAddress;
    private final BigInteger startHeight;
    private final BigInteger endHeight;
    private final Alias alias;

    @SuppressWarnings("squid:S00107")
    public NamespaceInfo(
        boolean active,
        Integer index,
        String metaId,
        NamespaceRegistrationType registrationType,
        Integer depth,
        List<NamespaceId> levels,
        NamespaceId parentId,
        UnresolvedAddress ownerAddress,
        BigInteger startHeight,
        BigInteger endHeight,
        Alias alias) {
        this.active = active;
        this.index = index;
        this.metaId = metaId;
        this.registrationType = registrationType;
        this.depth = depth;
        this.levels = levels;
        this.parentId = parentId;
        this.ownerAddress = ownerAddress;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.alias = alias;
    }

    /**
     * Returns true if namespace is active
     *
     * @return true if the namespace is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the namespace expiration status
     *
     * @return true if namespace is expired
     */
    public boolean isExpired() {
        return !active;
    }

    public Integer getIndex() {
        return index;
    }

    public String getMetaId() {
        return metaId;
    }

    /**
     * Returns the namespace type
     *
     * @return the namespace type
     */
    public NamespaceRegistrationType getRegistrationType() {
        return registrationType;
    }

    /**
     * Returns the namespace level depth
     *
     * @return the namespace level depth
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Returns the different NamespaceIds per level
     *
     * @return the different Namespace IDs per level
     */
    public List<NamespaceId> getLevels() {
        return levels;
    }

    /**
     * Returns the namespace owner address
     *
     * @return mosaic namespace owner
     */
    public UnresolvedAddress getOwnerAddress() {
        return ownerAddress;
    }

    /**
     * Returns the block height the namespace was registered
     *
     * @return the block height the namespace was registered
     */
    public BigInteger getStartHeight() {
        return startHeight;
    }

    /**
     * Returns the block height the namespace expires if not renewed
     *
     * @return the block height the namespace expires
     */
    public BigInteger getEndHeight() {
        return endHeight;
    }

    /**
     * Returns the NamespaceId
     *
     * @return namespace id
     */
    public NamespaceId getId() {
        return this.levels.get(this.levels.size() - 1);
    }

    /**
     * Returns true if namespace is Root
     *
     * @return true if namespace is Root
     */
    public boolean isRoot() {
        return this.registrationType == NamespaceRegistrationType.ROOT_NAMESPACE;
    }

    /**
     * Returns the Alias
     *
     * @return alias
     */
    public Alias getAlias() {
        return alias;
    }

    /**
     * Returns true if namespace has Alias
     *
     * @return true if namespace has Alias
     */
    public boolean hasAlias() {
        return !this.alias.isEmpty();
    }

    /**
     * Returns true if namespace is Subnamespace
     *
     * @return true if namespace is Subnamespace
     */
    public boolean isSubnamespace() {
        return this.registrationType == NamespaceRegistrationType.SUB_NAMESPACE;
    }

    /**
     * Returns the Parent Namespace Id
     *
     * @return the Parent Namespace Idpace
     */
    public NamespaceId parentNamespaceId() {
        if (this.isRoot()) {
            throw new IllegalStateException("Is A Root Namespace");
        }
        return this.parentId;
    }
}
