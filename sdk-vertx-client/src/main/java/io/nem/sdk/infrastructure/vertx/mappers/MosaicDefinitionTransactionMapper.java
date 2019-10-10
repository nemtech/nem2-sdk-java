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

package io.nem.sdk.infrastructure.vertx.mappers;

import static io.nem.core.utils.MapperUtils.getIdAsHex;
import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.MosaicDefinitionTransactionDTO;
import java.math.BigInteger;

/**
 * Mosaic definition transaction mapper.
 */
class MosaicDefinitionTransactionMapper extends
    AbstractTransactionMapper<MosaicDefinitionTransactionDTO, MosaicDefinitionTransaction> {

    public MosaicDefinitionTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MOSAIC_DEFINITION, MosaicDefinitionTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<MosaicDefinitionTransaction> createFactory(NetworkType networkType,
        MosaicDefinitionTransactionDTO transaction) {
        MosaicFlags mosaicFlags =
            MosaicFlags.create(transaction.getFlags().intValue());
        return MosaicDefinitionTransactionFactory.create(networkType,
            MosaicNonce.createFromBigInteger(transaction.getNonce()),
            toMosaicId(transaction.getId()),
            mosaicFlags, transaction.getDivisibility(),
            new BlockDuration(transaction.getDuration()));
    }

    @Override
    protected void copyToDto(MosaicDefinitionTransaction transaction,
        MosaicDefinitionTransactionDTO dto) {
        dto.setFlags(transaction.getMosaicFlags().getValue());
        dto.setId(getIdAsHex(transaction.getMosaicId()));
        dto.setDivisibility(transaction.getDivisibility());
        dto.setNonce((long) transaction.getMosaicNonce().getNonceAsInt());
        dto.setDuration(BigInteger.valueOf(transaction.getBlockDuration().getDuration()));
    }


}
