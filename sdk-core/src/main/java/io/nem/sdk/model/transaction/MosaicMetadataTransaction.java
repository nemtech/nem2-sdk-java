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

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedMosaicMetadataTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicMetadataTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import java.nio.ByteBuffer;

/**
 * Announce an MosaicMetadataTransaction to associate a key-value state to an mosaic.
 */
public class MosaicMetadataTransaction extends MetadataTransaction {

    /**
     * Metadata target mosaic id.
     */
    private final UnresolvedMosaicId targetMosaicId;

    /**
     * Constructor
     *
     * @param factory the factory with the configured data.
     */
    MosaicMetadataTransaction(MosaicMetadataTransactionFactory factory) {
        super(factory);
        this.targetMosaicId = factory.getTargetMosaicId();
    }


    public UnresolvedMosaicId getTargetMosaicId() {
        return targetMosaicId;
    }


    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        MosaicMetadataTransactionBuilder txBuilder =
            MosaicMetadataTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new KeyDto(this.getTargetAccount().getPublicKey().getByteBuffer()),
                this.getScopedMetadataKey().longValue(),
                new UnresolvedMosaicIdDto(getTargetMosaicId().getId().longValue()),
                (short) getValueSizeDelta(),
                getValueBuffer()
            );
        return txBuilder.serialize();
    }

    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedMosaicMetadataTransactionBuilder txBuilder =
            EmbeddedMosaicMetadataTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new KeyDto(this.getTargetAccount().getPublicKey().getByteBuffer()),
                this.getScopedMetadataKey().longValue(),
                new UnresolvedMosaicIdDto(getTargetMosaicId().getId().longValue()),
                (short) getValueSizeDelta(),
                getValueBuffer()
            );
        return txBuilder.serialize();
    }

}
