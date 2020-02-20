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

import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.IllegalIdentifierException;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.IdGenerator;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The namespace id structure describes namespace id
 *
 * @since 1.0
 */
public class NamespaceId implements UnresolvedMosaicId, UnresolvedAddress {

    private final BigInteger id;

    private final Optional<String> fullName;

    /**
     * Create NamespaceId from namespace Hex string
     *
     * @param hex the hex value.
     * @throws IllegalIdentifierException NamespaceId identifier
     */
    public NamespaceId(String hex) {
        ConvertUtils.validateIsHexString(hex, 16);
        this.id = new BigInteger(hex, 16);
        this.fullName = Optional.empty();
    }

    private NamespaceId(BigInteger id, Optional<String> fullName) {
        this.id = ConvertUtils.toUnsignedBigInteger(id);
        this.fullName = fullName;
    }

    /**
     * Create NamespaceId from namespace string name (ex: nem or domain.subdom.subdome)
     *
     * @param namespaceName the namespace name.
     * @return the new {@link NamespaceId}
     */
    public static NamespaceId createFromName(String namespaceName) {
        return new NamespaceId(IdGenerator.generateNamespaceId(namespaceName),
            Optional.of(namespaceName));
    }

    /**
     * Creates a NamespaceId when the id and the full name is known.
     *
     * @param id the id.
     * @param fullName the full name that includes the parent namespaces (ex: nem or
     * domain.subdom.subdome)
     * @return the new {@link NamespaceId}
     */
    public static NamespaceId createFromIdAndFullName(BigInteger id, String fullName) {
        return new NamespaceId(id, Optional.of(fullName));
    }

    /**
     * Create NamespaceId from namespace string name (ex: nem or domain.subdom.subdome) and parent
     * id
     *
     * @param namespaceName the namespace name.
     * @param parentId the parent id.
     * @return the new {@link NamespaceId}
     */
    public static NamespaceId createFromNameAndParentId(String namespaceName, BigInteger parentId) {
        return new NamespaceId(IdGenerator.generateNamespaceId(namespaceName, parentId),
            Optional.of(namespaceName));
    }

    /**
     * Create NamespaceId from namespace string name (ex: nem or domain.subdom.subdome) and parent
     * namespace name
     *
     * @param namespaceName the namespace name.
     * @param parentNamespaceName the parent's namespace name.
     * @return the new {@link NamespaceId}
     */
    public static NamespaceId createFromNameAndParentName(String namespaceName,
        String parentNamespaceName) {
        return new NamespaceId(IdGenerator.generateNamespaceId(namespaceName, parentNamespaceName),
            Optional.of(parentNamespaceName + "." + namespaceName));
    }

    /**
     * Create NamespaceId from BigInteger id
     *
     * @param id the namespace id as {@link BigInteger}.
     * @return the new {@link NamespaceId}
     */
    public static NamespaceId createFromId(BigInteger id) {
        return new NamespaceId(id, Optional.empty());
    }

    /**
     * Returns a list of BigInteger ids for a namespace path (ex: nem or domain.subdom.subdome)
     *
     * @param namespaceName the namespace name.
     * @return the paths
     */
    public static List<BigInteger> getNamespacePath(String namespaceName) {
        return IdGenerator.generateNamespacePath(namespaceName);
    }

    /**
     * Returns namespace BigInteger id
     *
     * @return namespace BigInteger id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Returns namespace id as a long
     *
     * @return id long
     */
    public long getIdAsLong() {
        return this.id.longValue();
    }


    /**
     * Returns optional namespace full name, with subnamespaces if it's the case.
     *
     * @return namespace full name
     */
    public Optional<String> getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NamespaceId that = (NamespaceId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String encoded(NetworkType networkType) {
        return ConvertUtils
            .toHex(SerializationUtils.fromUnresolvedAddressToByteBuffer(this, networkType).array());
    }

    /**
     * Gets the id as a hexadecimal string.
     *
     * @return Hex id.
     */
    @Override
    public String getIdAsHex() {
        byte[] bytes = ByteUtils.bigIntToBytes(getId());
        return ConvertUtils.toHex(bytes);
    }

    @Override
    public boolean isAlias() {
        return true;
    }
}
