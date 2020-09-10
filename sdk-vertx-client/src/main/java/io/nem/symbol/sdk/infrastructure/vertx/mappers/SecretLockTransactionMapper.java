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

import static io.nem.symbol.core.utils.MapperUtils.toUnresolvedMosaicId;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.LockHashAlgorithmEnum;
import io.nem.symbol.sdk.openapi.vertx.model.SecretLockTransactionDTO;

/** Secret lock transaction mapper. */
class SecretLockTransactionMapper
    extends AbstractTransactionMapper<SecretLockTransactionDTO, SecretLockTransaction> {

  public SecretLockTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.SECRET_LOCK, SecretLockTransactionDTO.class);
  }

  @Override
  protected TransactionFactory<SecretLockTransaction> createFactory(
      NetworkType networkType, SecretLockTransactionDTO transaction) {
    Mosaic mosaic =
        new Mosaic(toUnresolvedMosaicId(transaction.getMosaicId()), transaction.getAmount());
    return SecretLockTransactionFactory.create(
        networkType,
        mosaic,
        transaction.getDuration(),
        LockHashAlgorithm.rawValueOf(transaction.getHashAlgorithm().getValue()),
        transaction.getSecret(),
        MapperUtils.toUnresolvedAddress(transaction.getRecipientAddress()));
  }

  @Override
  protected void copyToDto(SecretLockTransaction transaction, SecretLockTransactionDTO dto) {
    dto.setAmount(transaction.getMosaic().getAmount());
    dto.setMosaicId(MapperUtils.getIdAsHex(transaction.getMosaic().getId()));
    dto.setDuration(transaction.getDuration());
    dto.setHashAlgorithm(
        LockHashAlgorithmEnum.fromValue(transaction.getHashAlgorithm().getValue()));
    dto.setSecret(transaction.getSecret());
    dto.setRecipientAddress(transaction.getRecipient().encoded(transaction.getNetworkType()));
  }
}
