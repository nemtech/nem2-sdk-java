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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MosaicMetadataTransaction}
 */
public class MosaicMetadataTransactionFactory extends
    MetadataTransactionFactory<MosaicMetadataTransaction> {

    /**
     * Metadata target mosaic id.
     */
    private final UnresolvedMosaicId targetMosaicId;

    private MosaicMetadataTransactionFactory(
        NetworkType networkType,
        UnresolvedAddress targetAddress,
        UnresolvedMosaicId targetMosaicId,
        BigInteger scopedMetadataKey,
        String value) {
        super(TransactionType.MOSAIC_METADATA, networkType, targetAddress,
            scopedMetadataKey, value);
        Validate.notNull(targetMosaicId, "TargetMosaicId must not be null");
        this.targetMosaicId = targetMosaicId;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param targetAddress Target address.
     * @param targetMosaicId Target mosaic id.
     * @param scopedMetadataKey Scoped metadata key.
     * @param value Value.
     * @return Mosaic metadata transaction.
     */
    public static MosaicMetadataTransactionFactory create(NetworkType networkType,
        UnresolvedAddress targetAddress, UnresolvedMosaicId targetMosaicId, BigInteger scopedMetadataKey, String value) {
        return new MosaicMetadataTransactionFactory(networkType, targetAddress, targetMosaicId, scopedMetadataKey, value);
    }

    public UnresolvedMosaicId getTargetMosaicId() {
        return targetMosaicId;
    }

    @Override
    public MosaicMetadataTransaction build() {
        return new MosaicMetadataTransaction(this);
    }
}
