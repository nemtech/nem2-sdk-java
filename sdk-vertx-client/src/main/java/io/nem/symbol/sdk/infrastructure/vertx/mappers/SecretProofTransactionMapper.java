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

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.LockHashAlgorithmEnum;
import io.nem.symbol.sdk.openapi.vertx.model.SecretProofTransactionDTO;

/** Secret proof transaction mapper. */
class SecretProofTransactionMapper
    extends AbstractTransactionMapper<SecretProofTransactionDTO, SecretProofTransaction> {

  public SecretProofTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.SECRET_PROOF, SecretProofTransactionDTO.class);
  }

  @Override
  protected TransactionFactory<SecretProofTransaction> createFactory(
      NetworkType networkType, Deadline deadline, SecretProofTransactionDTO transaction) {
    return SecretProofTransactionFactory.create(
        networkType,
        deadline,
        LockHashAlgorithm.rawValueOf(transaction.getHashAlgorithm().getValue()),
        MapperUtils.toUnresolvedAddress(transaction.getRecipientAddress()),
        transaction.getSecret(),
        transaction.getProof());
  }

  @Override
  protected void copyToDto(SecretProofTransaction transaction, SecretProofTransactionDTO dto) {
    dto.setHashAlgorithm(LockHashAlgorithmEnum.fromValue(transaction.getHashType().getValue()));
    dto.setRecipientAddress(transaction.getRecipient().encoded(transaction.getNetworkType()));
    dto.setSecret(transaction.getSecret());
    dto.setProof(transaction.getProof());
  }
}
