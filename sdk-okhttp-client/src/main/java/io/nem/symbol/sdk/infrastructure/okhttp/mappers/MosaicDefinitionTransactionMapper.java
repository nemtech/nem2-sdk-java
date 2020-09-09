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
package io.nem.symbol.sdk.infrastructure.okhttp.mappers;

import static io.nem.symbol.core.utils.MapperUtils.getIdAsHex;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicDefinitionTransactionDTO;
import java.math.BigInteger;

/** Mosaic definition transaction mapper. */
class MosaicDefinitionTransactionMapper
    extends AbstractTransactionMapper<MosaicDefinitionTransactionDTO, MosaicDefinitionTransaction> {

  public MosaicDefinitionTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.MOSAIC_DEFINITION, MosaicDefinitionTransactionDTO.class);
  }

  @Override
  protected TransactionFactory<MosaicDefinitionTransaction> createFactory(
      NetworkType networkType, MosaicDefinitionTransactionDTO transaction) {
    MosaicFlags mosaicFlags = MosaicFlags.create(transaction.getFlags());
    return MosaicDefinitionTransactionFactory.create(
        networkType,
        MosaicNonce.createFromInteger(transaction.getNonce().intValue()),
        MapperUtils.toMosaicId(transaction.getId()),
        mosaicFlags,
        transaction.getDivisibility(),
        new BlockDuration(transaction.getDuration()));
  }

  @Override
  protected void copyToDto(
      MosaicDefinitionTransaction transaction, MosaicDefinitionTransactionDTO dto) {
    dto.setFlags(transaction.getMosaicFlags().getValue());
    dto.setId(getIdAsHex(transaction.getMosaicId()));
    dto.setDivisibility(transaction.getDivisibility());
    dto.setNonce(transaction.getMosaicNonce().getNonceAsLong());
    dto.setDuration(BigInteger.valueOf(transaction.getBlockDuration().getDuration()));
  }
}
