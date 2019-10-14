/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedMosaicSupplyChangeTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicSupplyChangeActionDto;
import io.nem.catapult.builders.MosaicSupplyChangeTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * In case a mosaic has the flag 'supplyMutable' set to true, the creator of the mosaic can change
 * the supply, i.e. increase or decrease the supply.
 *
 * @since 1.0
 */
public class MosaicSupplyChangeTransaction extends Transaction {

    private final UnresolvedMosaicId mosaicId;
    private final MosaicSupplyChangeActionType action;
    private final BigInteger delta;

    MosaicSupplyChangeTransaction(MosaicSupplyChangeTransactionFactory factory) {
        super(factory);
        this.mosaicId = factory.getMosaicId();
        this.action = factory.getAction();
        this.delta = factory.getDelta();
    }

    /**
     * Returns mosaic id.
     *
     * @return BigInteger
     */
    public UnresolvedMosaicId getMosaicId() {
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

    /**
     * Gets the serialized bytes.
     *
     * @return Serialized bytes
     */
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        MosaicSupplyChangeTransactionBuilder txBuilder =
            MosaicSupplyChangeTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new UnresolvedMosaicIdDto(getMosaicId().getId().longValue()),
                MosaicSupplyChangeActionDto.rawValueOf((byte) getAction().getValue()),
                new AmountDto(getDelta().longValue()));
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    byte[] generateEmbeddedBytes() {
        EmbeddedMosaicSupplyChangeTransactionBuilder txBuilder =
            EmbeddedMosaicSupplyChangeTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new UnresolvedMosaicIdDto(getMosaicId().getId().longValue()),
                MosaicSupplyChangeActionDto.rawValueOf((byte) getAction().getValue()),
                new AmountDto(getDelta().longValue()));
        return txBuilder.serialize();
    }
}
