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

import static io.nem.symbol.core.utils.MapperUtils.getIdAsHex;
import static io.nem.symbol.core.utils.MapperUtils.toUnresolvedMosaicId;

import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicSupplyChangeActionEnum;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicSupplyChangeTransactionDTO;

/**
 * Mosaic supply change transaction mapper.
 */
class MosaicSupplyChangeTransactionMapper extends
    AbstractTransactionMapper<MosaicSupplyChangeTransactionDTO, MosaicSupplyChangeTransaction> {

    public MosaicSupplyChangeTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MOSAIC_SUPPLY_CHANGE,
            MosaicSupplyChangeTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<MosaicSupplyChangeTransaction> createFactory(
        NetworkType networkType, MosaicSupplyChangeTransactionDTO transaction) {
        return MosaicSupplyChangeTransactionFactory.create(networkType,
            toUnresolvedMosaicId(transaction.getMosaicId()),
            MosaicSupplyChangeActionType.rawValueOf(transaction.getAction().getValue()),
            transaction.getDelta());
    }

    @Override
    protected void copyToDto(MosaicSupplyChangeTransaction transaction,
        MosaicSupplyChangeTransactionDTO dto) {
        dto.setDelta(transaction.getDelta());
        dto.setMosaicId(getIdAsHex(transaction.getMosaicId()));
        dto.setAction(MosaicSupplyChangeActionEnum.fromValue(transaction.getAction().getValue()));
    }
}
