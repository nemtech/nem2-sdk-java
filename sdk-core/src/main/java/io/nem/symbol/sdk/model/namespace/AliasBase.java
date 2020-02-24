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
