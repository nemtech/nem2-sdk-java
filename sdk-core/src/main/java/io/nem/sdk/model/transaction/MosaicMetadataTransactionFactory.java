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

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
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
    private final MosaicId targetMosaicId;

    private MosaicMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetAccount,
        MosaicId targetMosaicId,
        BigInteger scopedMetadataKey,
        int valueSizeDelta,
        int valueSize,
        String value) {
        super(TransactionType.MOSAIC_METADATA_TRANSACTION, networkType, targetAccount,
            scopedMetadataKey, valueSizeDelta, valueSize, value);
        Validate.notNull(targetMosaicId, "TargetMosaicId must not be null");
        this.targetMosaicId = targetMosaicId;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param targetAccount Target account.
     * @param targetMosaicId Target mosaic id.
     * @param scopedMetadataKey Scoped metadata key.
     * @param valueSizeDelta Value size delta.
     * @param valueSize Value size.
     * @param value Value.
     * @return Mosaic metadata transaction.
     */
    public static MosaicMetadataTransactionFactory create(NetworkType networkType,
        PublicAccount targetAccount, MosaicId targetMosaicId, BigInteger scopedMetadataKey,
        int valueSizeDelta, int valueSize, String value) {
        return new MosaicMetadataTransactionFactory(networkType, targetAccount, targetMosaicId, scopedMetadataKey, valueSizeDelta, valueSize, value);
    }

    public MosaicId getTargetMosaicId() {
        return targetMosaicId;
    }

    @Override
    public MosaicMetadataTransaction build() {
        return new MosaicMetadataTransaction(this);
    }
}
