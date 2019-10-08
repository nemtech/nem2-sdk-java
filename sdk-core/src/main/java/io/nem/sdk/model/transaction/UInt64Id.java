/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.nem.sdk.model.transaction;

import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.ConvertUtils;
import java.math.BigInteger;
import java.util.Optional;

/**
 * UInt64Id Interface
 */
public interface UInt64Id {

    /**
     * Gets the UInt64Id id.
     *
     * @return BigInteger id.
     */
    BigInteger getId();

    /**
     * Gets the UInt64Id id as a long number.
     *
     * @return Long id.
     */
    long getIdAsLong();

    /**
     * Gets the UInt64Id id as a hexadecimal string.
     *
     * @return Hex id.
     */
    default String getIdAsHex() {
        byte[] bytes = ByteUtils.bigIntToBytes(getId());
        return ConvertUtils.toHex(bytes);
    }

    /**
     * Get the optional UInt64Id full name.
     *
     * @return Optional of String with full name.
     */
    Optional<String> getFullName();

    /**
     * Compares UInt64Ids for equality.
     *
     * @return True if equal.
     */
    @Override
    boolean equals(Object o);
}
