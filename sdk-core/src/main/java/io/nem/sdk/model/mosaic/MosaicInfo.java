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
package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.account.PublicAccount;
import java.math.BigInteger;

/**
 * The mosaic info structure contains its properties, the owner and the namespace to which it
 * belongs to.
 */
public class MosaicInfo {

    private final MosaicId mosaicId;
    private final BigInteger supply;
    private final BigInteger startHeight;
    private final PublicAccount owner;
    private final Integer revision;
    private final MosaicFlags mosaicFlags;
    private final int divisibility;
    private final BigInteger duration;

    @SuppressWarnings("squid:S00107")
    private MosaicInfo(final MosaicId mosaicId, final BigInteger supply,
        final BigInteger startHeight, final PublicAccount owner, final Integer revision,
        final MosaicFlags mosaicFlags, final int divisibility, final BigInteger duration) {
        this.mosaicId = mosaicId;
        this.supply = supply;
        this.startHeight = startHeight;
        this.owner = owner;
        this.revision = revision;
        this.mosaicFlags = mosaicFlags;
        this.divisibility = divisibility;
        this.duration = duration;
    }

    @SuppressWarnings("squid:S00107")
    public static MosaicInfo create(final MosaicId mosaicId,
        final BigInteger supply, final BigInteger startHeight,
        final PublicAccount owner, final Integer revision, final MosaicFlags mosaicFlags,
        final int divisibility, final BigInteger duration) {
        return new MosaicInfo(mosaicId, supply, startHeight, owner, revision, mosaicFlags,
            divisibility, duration);
    }

    /**
     * Returns the mosaic id
     *
     * @return mosaic id
     */
    public MosaicId getMosaicId() {
        return mosaicId;
    }

    /**
     * Returns the total mosaic supply
     *
     * @return total mosaic supply
     */
    public BigInteger getSupply() {
        return supply;
    }

    /**
     * Returns the block height it was created
     *
     * @return height it was created
     */
    public BigInteger getStartHeight() {
        return startHeight;
    }

    /**
     * Returns the mosaic account owner
     *
     * @return mosaic account owner
     */
    public PublicAccount getOwner() {
        return owner;
    }

    /**
     * Returns the revision number
     *
     * @return revision
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * Returns true if the supply is mutable
     *
     * @return if supply is mutable
     */
    public boolean isSupplyMutable() {
        return mosaicFlags.isSupplyMutable();
    }

    /**
     * Returns tue if the mosaic is transferable between non-owner accounts
     *
     * @return if the mosaic is transferable between non-owner accounts
     */
    public boolean isTransferable() {
        return mosaicFlags.isTransferable();
    }

    /**
     * Returns tue if the mosaic is restrictable between non-owner accounts
     *
     * @return if the mosaic is restrictable between non-owner accounts
     */
    public boolean isRestrictable() {
        return mosaicFlags.isRestrictable();
    }

    /**
     * Returns the mosaic divisibility
     *
     * @return mosaic divisibility
     */
    public int getDivisibility() {
        return divisibility;
    }

    /**
     * Return the number of blocks from height it will be active
     *
     * @return the number of blocks from height it will be active
     */
    public BigInteger getDuration() {
        return duration;
    }
}
