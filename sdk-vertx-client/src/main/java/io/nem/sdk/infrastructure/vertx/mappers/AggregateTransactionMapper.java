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

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.AggregateBondedTransactionDTO;
import io.nem.sdk.openapi.vertx.model.EmbeddedTransactionInfoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregate transaction mapper.
 */
class AggregateTransactionMapper extends
    AbstractTransactionMapper<AggregateBondedTransactionDTO, AggregateTransaction> {

    private TransactionMapper transactionMapper;

    public AggregateTransactionMapper(JsonHelper jsonHelper,
        TransactionType transactionType,
        TransactionMapper transactionMapper) {
        super(jsonHelper, transactionType, AggregateBondedTransactionDTO.class);
        this.transactionMapper = transactionMapper;
    }

    @Override
    protected AggregateTransactionFactory createFactory(NetworkType networkType,
        AggregateBondedTransactionDTO transaction) {

        List<Transaction> transactions = transaction.getTransactions().stream()
            .map(embeddedTransactionInfoDTO -> {

                EmbeddedTransactionInfoDTO transactionInfoDTO = new EmbeddedTransactionInfoDTO();
                transactionInfoDTO.setMeta(embeddedTransactionInfoDTO.getMeta());
                transactionInfoDTO.setTransaction(embeddedTransactionInfoDTO.getTransaction());
                Map<String, Object> innerTransaction = (Map<String, Object>) embeddedTransactionInfoDTO
                    .getTransaction();

                innerTransaction.put("deadline", transaction.getDeadline());
                innerTransaction.put("maxFee", transaction.getMaxFee());
                innerTransaction.put("signature", transaction.getSignature());
                return transactionMapper.map(transactionInfoDTO);

            }).collect(Collectors.toList());

        List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
        if (transaction.getCosignatures() != null) {
            cosignatures =
                transaction.getCosignatures().stream()
                    .map(
                        aggregateCosignature ->
                            new AggregateTransactionCosignature(
                                aggregateCosignature.getSignature(),
                                PublicAccount
                                    .createFromPublicKey(aggregateCosignature.getSignerPublicKey(),
                                        networkType)))
                    .collect(Collectors.toList());
        }

        return AggregateTransactionFactory.create(getTransactionType(), networkType, transactions,
            cosignatures);
    }
}
