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

import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import java.math.BigInteger;
import java.util.Optional;

/**
 * The mosaic info structure contains its properties, the owner and the namespace to which it
 * belongs to.
 */
public class MosaicInfo implements Stored {

    /**
     * The database id.
     */
    private final String recordId;
    private final MosaicId mosaicId;
    private final BigInteger supply;
    private final BigInteger startHeight;
    private final Address ownerAddress;
    private final Long revision;
    private final MosaicFlags mosaicFlags;
    private final int divisibility;
    private final BigInteger duration;

    @SuppressWarnings("squid:S00107")
    public MosaicInfo(final String recordId, final MosaicId mosaicId, final BigInteger supply,
        final BigInteger startHeight, final Address ownerAddress, final Long revision,
        final MosaicFlags mosaicFlags, final int divisibility, final BigInteger duration) {
        this.recordId = recordId;
        this.mosaicId = mosaicId;
        this.supply = supply;
        this.startHeight = startHeight;
        this.ownerAddress = ownerAddress;
        this.revision = revision;
        this.mosaicFlags = mosaicFlags;
        this.divisibility = divisibility;
        this.duration = duration;
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
     * Returns the mosaic account address
     *
     * @return mosaic account owner
     */
    public UnresolvedAddress getOwnerAddress() {
        return ownerAddress;
    }

    /**
     * Returns the revision number
     *
     * @return revision
     */
    public Long getRevision() {
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

    /**
     * @return the internal database id.
     */
    public Optional<String> getRecordId() {
        return Optional.ofNullable(recordId);
    }
}
