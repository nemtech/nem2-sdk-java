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

import static io.nem.symbol.core.utils.MapperUtils.toMosaicId;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.HashLockTransactionDTO;

/** Hash lock transaction mapper. */
class HashLockTransactionMapper
    extends AbstractTransactionMapper<HashLockTransactionDTO, HashLockTransaction> {

  public HashLockTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.HASH_LOCK, HashLockTransactionDTO.class);
  }

  private Mosaic getMosaic(HashLockTransactionDTO mosaic) {
    return new Mosaic(toMosaicId(mosaic.getMosaicId()), mosaic.getAmount());
  }

  @Override
  protected TransactionFactory<HashLockTransaction> createFactory(
      NetworkType networkType, Deadline deadline, HashLockTransactionDTO transaction) {
    return HashLockTransactionFactory.create(
        networkType,
        deadline,
        getMosaic(transaction),
        transaction.getDuration(),
        transaction.getHash());
  }

  @Override
  protected void copyToDto(HashLockTransaction transaction, HashLockTransactionDTO dto) {
    dto.setMosaicId(MapperUtils.getIdAsHex(transaction.getMosaic().getId()));
    dto.setAmount(transaction.getMosaic().getAmount());
    dto.setDuration(transaction.getDuration());
    dto.setHash(transaction.getHash());
  }
}
