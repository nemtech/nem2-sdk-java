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

package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicMetadataTransactionDTO;
import java.math.BigInteger;

/**
 * Mosaic metadata transaction mapper.
 */
class MosaicMetadataTransactionMapper extends
    AbstractTransactionMapper<MosaicMetadataTransactionDTO, MosaicMetadataTransaction> {

    public MosaicMetadataTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MOSAIC_METADATA,
            MosaicMetadataTransactionDTO.class);
    }

    @Override
    protected MosaicMetadataTransactionFactory createFactory(NetworkType networkType,
        MosaicMetadataTransactionDTO transaction) {
        PublicAccount targetAccount = PublicAccount
            .createFromPublicKey(transaction.getTargetPublicKey(), networkType);
        Integer valueSizeDelta = transaction.getValueSizeDelta();
        BigInteger scopedMetaDataKey = new BigInteger(transaction.getScopedMetadataKey(), 16);
        String value = ConvertUtils.fromHexToString(transaction.getValue());
        UnresolvedMosaicId targetMosaic = MapperUtils
            .toUnresolvedMosaicId(transaction.getTargetMosaicId());
        MosaicMetadataTransactionFactory factory = MosaicMetadataTransactionFactory.create(
            networkType,
            targetAccount,
            targetMosaic,
            scopedMetaDataKey,
            value);
        factory.valueSizeDelta(valueSizeDelta);
        Integer valueSize = transaction.getValueSize();
        if (valueSize != null) {
            factory.valueSize(valueSize);
        }
        return factory;
    }

    @Override
    protected void copyToDto(MosaicMetadataTransaction transaction,
        MosaicMetadataTransactionDTO dto) {
        dto.setTargetPublicKey(transaction.getTargetAccount().getPublicKey().toHex());
        dto.setTargetMosaicId(MapperUtils.getIdAsHex(transaction.getTargetMosaicId()));
        dto.setScopedMetadataKey(transaction.getScopedMetadataKey().toString());
        dto.setValue(ConvertUtils.fromStringToHex(transaction.getValue()));
        dto.setValueSizeDelta(transaction.getValueSizeDelta());
        dto.setValueSize(transaction.getValueSize());
    }
}
