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
package io.nem.sdk.model.namespace;

import java.util.Objects;

/**
 * Alias base.
 */
public abstract class AliasBase<T> implements Alias<T> {

    /* the alias type */
    private final AliasType aliasType;

    /* alias value. */
    private final T aliasValue;


    /**
     * Creates an alias of type T.
     *
     * @param aliasType the alias type.
     * @param value Value of type T.
     */
    public AliasBase(AliasType aliasType, T value) {
        this.aliasType = aliasType;
        this.aliasValue = value;
    }

    /**
     * Gets the alias type.
     *
     * @return Alias type.
     */
    public AliasType getType() {
        return aliasType;
    }

    /**
     * Gets the alias value of type T.
     *
     * @return Alias value.
     */
    @Override
    public T getAliasValue() {
        return this.aliasValue;
    }

    /**
     * Checks if the Alias is empty.
     *
     * @return True if alias type is none.
     */
    @Override
    public boolean isEmpty() {
        return AliasType.NONE == getType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AliasBase<?> aliasBase = (AliasBase<?>) o;
        return aliasType == aliasBase.aliasType &&
            Objects.equals(getAliasValue(), aliasBase.getAliasValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliasType, getAliasValue());
    }
}
