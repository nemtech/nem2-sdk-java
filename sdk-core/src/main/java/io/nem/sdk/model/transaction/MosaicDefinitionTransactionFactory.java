/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicFlags;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MosaicDefinitionTransaction}
 */
public class MosaicDefinitionTransactionFactory extends
    TransactionFactory<MosaicDefinitionTransaction> {

    private final MosaicNonce mosaicNonce;
    private final MosaicId mosaicId;
    private final MosaicFlags mosaicFlags;
    private final int divisibility;
    private final BlockDuration blockDuration;

    /**
     * Create a mosaic definition transaction object with factory build and modifier methods.
     *
     * @param networkType {@link NetworkType}
     * @param mosaicNonce {@link MosaicNonce}
     * @param mosaicId {@link MosaicId}
     * @param mosaicFlags {@link MosaicFlags}
     * @param divisibility int
     * @param blockDuration {@link BlockDuration}
     */
    public MosaicDefinitionTransactionFactory(NetworkType networkType, MosaicNonce mosaicNonce,
        MosaicId mosaicId, MosaicFlags mosaicFlags, int divisibility,
        BlockDuration blockDuration) {
        super(TransactionType.MOSAIC_DEFINITION, networkType);
        Validate.notNull(mosaicNonce, "MosaicNonce must not be null");
        Validate.notNull(mosaicId, "MosaicId must not be null");
        Validate.notNull(mosaicFlags, "MosaicFlags must not be null");
        Validate.notNull(divisibility, "Divisibility must not be null");
        Validate.notNull(blockDuration, "BlockDuration must not be null");
        this.mosaicNonce = mosaicNonce;
        this.mosaicId = mosaicId;
        this.mosaicFlags = mosaicFlags;
        this.divisibility = divisibility;
        this.blockDuration = blockDuration;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param mosaicNonce Mosaic nonce.
     * @param mosaicId Mosaic id.
     * @param mosaicFlags Mosaic flags.
     * @param divisibility Divisibility.
     * @param blockDuration Block duration.
     * @return Mosaic definition transaction.
     */
    public static MosaicDefinitionTransactionFactory create(NetworkType networkType, MosaicNonce mosaicNonce,
        MosaicId mosaicId, MosaicFlags mosaicFlags, int divisibility,
        BlockDuration blockDuration) {
        return new MosaicDefinitionTransactionFactory(networkType, mosaicNonce, mosaicId, mosaicFlags, divisibility, blockDuration);
    }

    /**
     * Returns the mosaic id.
     *
     * @return {@link MosaicId}
     */
    public MosaicId getMosaicId() {
        return mosaicId;
    }

    /**
     * Returns the mosaic nonce.
     *
     * @return MosaicNonce {@link MosaicNonce}
     */
    public MosaicNonce getMosaicNonce() {
        return mosaicNonce;
    }

    /**
     * Returns the mosaic flags defining mosaic.
     *
     * @return {@link MosaicFlags}
     */
    public MosaicFlags getMosaicFlags() {
        return mosaicFlags;
    }

    /**
     * Returns the block duration.
     *
     * @return {@link BlockDuration}
     */
    public BlockDuration getBlockDuration() {
        return blockDuration;
    }

    /**
     * Returns the mosaic divisibility.
     *
     * @return int divisibility
     */
    public int getDivisibility() {
        return divisibility;
    }

    @Override
    public MosaicDefinitionTransaction build() {
        return new MosaicDefinitionTransaction(this);
    }
}
