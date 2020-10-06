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

import io.nem.symbol.sdk.infrastructure.TransactionMapper;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.AggregateTransactionExtendedDTO;
import io.nem.symbol.sdk.openapi.vertx.model.CosignatureDTO;
import io.nem.symbol.sdk.openapi.vertx.model.EmbeddedTransactionInfoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Aggregate transaction mapper. */
class AggregateTransactionMapper
    extends AbstractTransactionMapper<AggregateTransactionExtendedDTO, AggregateTransaction> {

  private final TransactionMapper transactionMapper;

  public AggregateTransactionMapper(
      JsonHelper jsonHelper, TransactionType transactionType, TransactionMapper transactionMapper) {
    super(jsonHelper, transactionType, AggregateTransactionExtendedDTO.class);
    this.transactionMapper = transactionMapper;
  }

  @Override
  protected AggregateTransactionFactory createFactory(
      NetworkType networkType, Deadline deadline, AggregateTransactionExtendedDTO transaction) {

    List<Transaction> transactions =
        transaction.getTransactions().stream()
            .map(
                embeddedTransactionInfoDTO -> {
                  Map<String, Object> innerTransaction =
                      (Map<String, Object>) embeddedTransactionInfoDTO.getTransaction();

                  innerTransaction.put("deadline", transaction.getDeadline());
                  innerTransaction.put("maxFee", transaction.getMaxFee());
                  innerTransaction.put("signature", transaction.getSignature());
                  return transactionMapper.mapFromDto(embeddedTransactionInfoDTO);
                })
            .collect(Collectors.toList());

    List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
    if (transaction.getCosignatures() != null) {
      cosignatures =
          transaction.getCosignatures().stream()
              .map(aggregateCosignature -> toCosignature(networkType, aggregateCosignature))
              .collect(Collectors.toList());
    }

    return AggregateTransactionFactory.create(
        getTransactionType(),
        networkType,
        deadline,
        transaction.getTransactionsHash(),
        transactions,
        cosignatures);
  }

  private AggregateTransactionCosignature toCosignature(
      NetworkType networkType, CosignatureDTO aggregateCosignature) {

    return new AggregateTransactionCosignature(
        aggregateCosignature.getVersion(),
        aggregateCosignature.getSignature(),
        PublicAccount.createFromPublicKey(aggregateCosignature.getSignerPublicKey(), networkType));
  }

  @Override
  protected void copyToDto(AggregateTransaction transaction, AggregateTransactionExtendedDTO dto) {
    List<EmbeddedTransactionInfoDTO> transactions =
        transaction.getInnerTransactions().stream()
            .map(
                embeddedTransactionInfoDTO ->
                    (EmbeddedTransactionInfoDTO)
                        transactionMapper.mapToDto(embeddedTransactionInfoDTO, true))
            .collect(Collectors.toList());
    List<CosignatureDTO> cosignatures = new ArrayList<>();
    if (transaction.getCosignatures() != null) {
      cosignatures =
          transaction.getCosignatures().stream()
              .map(this::toCosignature)
              .collect(Collectors.toList());
    }
    dto.setTransactionsHash(transaction.getTransactionsHash());
    dto.setCosignatures(cosignatures);
    dto.setTransactions(transactions);
  }

  private CosignatureDTO toCosignature(AggregateTransactionCosignature aggregateCosignature) {
    CosignatureDTO cosignatureDTO = new CosignatureDTO();
    cosignatureDTO.setSignerPublicKey(aggregateCosignature.getSigner().getPublicKey().toHex());
    cosignatureDTO.setSignature(aggregateCosignature.getSignature());
    cosignatureDTO.setVersion(aggregateCosignature.getVersion());
    return cosignatureDTO;
  }
}
