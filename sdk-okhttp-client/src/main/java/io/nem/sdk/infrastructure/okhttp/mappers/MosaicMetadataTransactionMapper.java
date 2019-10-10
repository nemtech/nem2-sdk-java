/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp.mappers;

import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicMetadataTransactionDTO;
import java.math.BigInteger;

/**
 * Mosaic metadata transaction mapper.
 */
class MosaicMetadataTransactionMapper extends
    AbstractTransactionMapper<MosaicMetadataTransactionDTO, MosaicMetadataTransaction> {

    public MosaicMetadataTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MOSAIC_METADATA_TRANSACTION,
            MosaicMetadataTransactionDTO.class);
    }

    @Override
    protected MosaicMetadataTransactionFactory createFactory(NetworkType networkType,
        MosaicMetadataTransactionDTO transaction) {
        PublicAccount targetAccount = PublicAccount
            .createFromPublicKey(transaction.getTargetPublicKey(), networkType);
        Integer valueSizeDelta = transaction.getValueSizeDelta();
        BigInteger scopedMetaDataKey = new BigInteger(transaction.getScopedMetadataKey(), 16);
        Integer valueSize = transaction.getValueSize();
        String value = ConvertUtils.fromHexToString(transaction.getValue());
        MosaicId targetMosaic = MapperUtils.toMosaicId(transaction.getTargetMosaicId());
        MosaicMetadataTransactionFactory factory = MosaicMetadataTransactionFactory.create(
            networkType,
            targetAccount,
            targetMosaic,
            scopedMetaDataKey,
            value);
        factory.valueSizeDelta(valueSizeDelta);
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
    }
}
