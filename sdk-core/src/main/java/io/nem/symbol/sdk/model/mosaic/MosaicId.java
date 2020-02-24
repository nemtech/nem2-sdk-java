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

package io.nem.symbol.sdk.model.mosaic;

import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.transaction.IdGenerator;
import java.math.BigInteger;
import java.util.Objects;

/**
 * The mosaic id structure describes mosaic id
 *
 * @since 1.0
 */
public class MosaicId implements UnresolvedMosaicId {

    private final BigInteger id;


    /**
     * Create MosaicId from mosaic Hex string
     *
     * @param hex the hex value.
     * @throws IllegalIdentifierException MosaicId identifier
     */
    public MosaicId(String hex) {
        ConvertUtils.validateIsHexString(hex, 16);
        this.id = new BigInteger(hex, 16);
    }

    /**
     * Create MosaicId from BigInteger id
     *
     * @param id the mosaic id as {@link BigInteger}.
     */
    public MosaicId(BigInteger id) {
        this.id = id;
    }

    /**
     * Create MosaicId from a MosaicNonce and a PublicAccount
     *
     * @param mosaicNonce the mosaic nonce.
     * @param owner the public account.
     */
    public MosaicId(MosaicNonce mosaicNonce, PublicAccount owner) {
        this.id = IdGenerator
            .generateMosaicId(mosaicNonce.getNonce(), owner.getPublicKey().getBytes());
    }

    /**
     * Create MosaicId from a MosaicNonce and a PublicAccount
     *
     * @param mosaicNonce the mosaic nonce
     * @param owner thw account owner.
     * @return the created {@link MosaicId}.
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
        return false;
    }
}


