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
import static io.nem.core.utils.MapperUtils.toUnresolvedMosaicId;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicAddressRestrictionTransactionDTO;

/**
 * Mosaic address restriction transaction mapper.
 */
class MosaicAddressRestrictionTransactionMapper extends
    AbstractTransactionMapper<MosaicAddressRestrictionTransactionDTO, MosaicAddressRestrictionTransaction> {

    public MosaicAddressRestrictionTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MOSAIC_ADDRESS_RESTRICTION,
            MosaicAddressRestrictionTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<MosaicAddressRestrictionTransaction> createFactory(
        NetworkType networkType,
        MosaicAddressRestrictionTransactionDTO transaction) {
        return MosaicAddressRestrictionTransactionFactory.create(networkType,
            toUnresolvedMosaicId(transaction.getMosaicId()),
            MapperUtils.fromHexToBigInteger(transaction.getRestrictionKey()),
            MapperUtils.toUnresolvedAddress(transaction.getTargetAddress()),
            transaction.getNewRestrictionValue()).previousRestrictionValue(
            transaction.getPreviousRestrictionValue());
    }

    @Override
    protected void copyToDto(MosaicAddressRestrictionTransaction transaction,
        MosaicAddressRestrictionTransactionDTO dto) {
        dto.setMosaicId(getIdAsHex(transaction.getMosaicId()));
        dto.setRestrictionKey(transaction.getRestrictionKey().toString(16));
        dto.setTargetAddress(transaction.getTargetAddress().encoded(transaction.getNetworkType()));
        dto.setPreviousRestrictionValue(transaction.getPreviousRestrictionValue());
        dto.setNewRestrictionValue(transaction.getNewRestrictionValue());

    }
}
