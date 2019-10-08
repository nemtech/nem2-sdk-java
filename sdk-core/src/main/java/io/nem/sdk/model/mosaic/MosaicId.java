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

package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.transaction.IdGenerator;
import io.nem.sdk.model.transaction.UInt64Id;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

/**
 * The mosaic id structure describes mosaic id
 *
 * @since 1.0
 */
public class MosaicId implements UInt64Id {

    private final BigInteger id;

    private final Optional<String> fullName;

    /**
     * Create MosaicId from mosaic Hex string
     *
     * @throws IllegalIdentifierException MosaicId identifier
     */
    public MosaicId(String hex) {
        if (!hex.matches("^[0-9A-Fa-f]{16}$")) {
            throw new IllegalIdentifierException(hex + " is an invalid hex string");
        }
        this.id = new BigInteger(hex, 16);
        this.fullName = Optional.empty();
    }

    /**
     * Create MosaicId from BigInteger id
     */
    public MosaicId(BigInteger id) {
        this.id = id;
        this.fullName = Optional.empty();
    }

    /**
     * Create MosaicId from a MosaicNonce and a PublicAccount
     */
    public MosaicId(MosaicNonce mosaicNonce, PublicAccount owner) {
        this.id = IdGenerator
            .generateMosaicId(mosaicNonce.getNonce(), owner.getPublicKey().getBytes());
        this.fullName = Optional.empty();
    }

    /**
     * Create MosaicId from a MosaicNonce and a PublicAccount
     */
    public static MosaicId createFromNonce(MosaicNonce mosaicNonce, PublicAccount owner) {

        return new MosaicId(mosaicNonce, owner);
    }

    /**
     * Returns mosaic BigInteger id
     *
     * @return mosaic BigInteger id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Returns mosaic id as a long
     *
     * @return id long
     */
    public long getIdAsLong() {
        return this.id.longValue();
    }


    /**
     * Returns optional mosaic alias full name (ex: nem.xem)
     *
     * @return namespace full name
     */
    public Optional<String> getFullName() {
        return fullName;
    }

    /**
     * Compares mosaicIds for equality.
     *
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MosaicId mosaicId = (MosaicId) o;
        return Objects.equals(id, mosaicId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}


