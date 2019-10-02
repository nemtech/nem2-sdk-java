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

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MosaicSupplyChangeTransaction}
 */
public class MosaicSupplyChangeTransactionFactory extends
    TransactionFactory<MosaicSupplyChangeTransaction> {

    private final MosaicId mosaicId;
    private final MosaicSupplyChangeActionType action;
    private final BigInteger delta;

    public MosaicSupplyChangeTransactionFactory(
        NetworkType networkType, MosaicId mosaicId,
        MosaicSupplyChangeActionType action,
        BigInteger delta) {
        super(TransactionType.MOSAIC_SUPPLY_CHANGE, networkType);
        Validate.notNull(mosaicId, "MosaicId must not be null");
        Validate.notNull(action, "Action must not be null");
        Validate.notNull(delta, "Delta must not be null");
        this.mosaicId = mosaicId;
        this.action = action;
        this.delta = delta;
    }

    public static MosaicSupplyChangeTransactionFactory create(NetworkType networkType, MosaicId mosaicId,
        MosaicSupplyChangeActionType action, BigInteger delta) {
        return new MosaicSupplyChangeTransactionFactory(networkType, mosaicId, action, delta);
    }

    /**
     * Returns mosaic id.
     *
     * @return BigInteger
     */
    public MosaicId getMosaicId() {
        return mosaicId;
    }

    /**
     * Returns mosaic supply type.
     *
     * @return {@link MosaicSupplyChangeActionType}
     */
    public MosaicSupplyChangeActionType getAction() {
        return action;
    }

    /**
     * Returns amount of mosaics added or removed.
     *
     * @return BigInteger
     */
    public BigInteger getDelta() {
        return delta;
    }

    @Override
    public MosaicSupplyChangeTransaction build() {
        return new MosaicSupplyChangeTransaction(this);
    }
}
