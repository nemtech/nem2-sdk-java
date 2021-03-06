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

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicGlobalRestrictionTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicRestrictionTypeEnum;

/** Mosaic global restriction transaction mapper. */
class MosaicGlobalRestrictionTransactionMapper
    extends AbstractTransactionMapper<
        MosaicGlobalRestrictionTransactionDTO, MosaicGlobalRestrictionTransaction> {

  public MosaicGlobalRestrictionTransactionMapper(JsonHelper jsonHelper) {
    super(
        jsonHelper,
        TransactionType.MOSAIC_GLOBAL_RESTRICTION,
        MosaicGlobalRestrictionTransactionDTO.class);
  }

  @Override
  protected TransactionFactory<MosaicGlobalRestrictionTransaction> createFactory(
      NetworkType networkType,
      Deadline deadline,
      MosaicGlobalRestrictionTransactionDTO transaction) {

    byte prevRestrictionType = transaction.getPreviousRestrictionType().getValue().byteValue();
    byte newRestrictionType = transaction.getNewRestrictionType().getValue().byteValue();

    MosaicGlobalRestrictionTransactionFactory factory =
        MosaicGlobalRestrictionTransactionFactory.create(
            networkType,
            deadline,
            toUnresolvedMosaicId(transaction.getMosaicId()),
            MapperUtils.fromHexToBigInteger(transaction.getRestrictionKey()),
            transaction.getNewRestrictionValue(),
            MosaicRestrictionType.rawValueOf(newRestrictionType));
    if (transaction.getReferenceMosaicId() != null) {
      factory.referenceMosaicId(
          MapperUtils.toUnresolvedMosaicId(transaction.getReferenceMosaicId()));
    }
    return factory
        .previousRestrictionValue(transaction.getPreviousRestrictionValue())
        .previousRestrictionType(MosaicRestrictionType.rawValueOf(prevRestrictionType));
  }

  @Override
  protected void copyToDto(
      MosaicGlobalRestrictionTransaction transaction, MosaicGlobalRestrictionTransactionDTO dto) {

    dto.setMosaicId(getIdAsHex(transaction.getMosaicId()));
    dto.setRestrictionKey(MapperUtils.fromBigIntegerToHex(transaction.getRestrictionKey()));
    dto.setNewRestrictionValue(transaction.getNewRestrictionValue());
    dto.setPreviousRestrictionValue(transaction.getPreviousRestrictionValue());
    dto.setPreviousRestrictionType(
        MosaicRestrictionTypeEnum.fromValue(
            (int) transaction.getPreviousRestrictionType().getValue()));
    dto.setNewRestrictionType(
        MosaicRestrictionTypeEnum.fromValue((int) transaction.getNewRestrictionType().getValue()));
    dto.setReferenceMosaicId(getIdAsHex(transaction.getReferenceMosaicId()));
  }
}
