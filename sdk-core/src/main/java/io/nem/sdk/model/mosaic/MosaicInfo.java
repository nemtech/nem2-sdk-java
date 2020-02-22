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
