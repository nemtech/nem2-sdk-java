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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.AggregateBondedTransactionDTO;
import io.nem.sdk.openapi.vertx.model.EmbeddedTransactionInfoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class AggregateTransactionMapper extends
    AbstractTransactionMapper<AggregateBondedTransactionDTO> {

    private TransactionMapper transactionMapper;

    public AggregateTransactionMapper(JsonHelper jsonHelper, TransactionType transactionType,
        TransactionMapper transactionMapper, SignSchema signSchema) {
        super(jsonHelper, transactionType, AggregateBondedTransactionDTO.class, signSchema);
        this.transactionMapper = transactionMapper;
    }

    @Override
    protected Transaction basicMap(TransactionInfo transactionInfo,
        AggregateBondedTransactionDTO transaction) {

        Deadline deadline = new Deadline(transaction.getDeadline());
        NetworkType networkType = extractNetworkType(transaction.getVersion());

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
                                new PublicAccount(aggregateCosignature.getSignerPublicKey(),
                                    networkType, getSignSchema())))
                    .collect(Collectors.toList());
        }

        return new AggregateTransaction(
            networkType,
            TransactionType.rawValueOf(transaction.getType()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            transaction.getMaxFee(),
            transactions,
            cosignatures,
            transaction.getSignature(),
            new PublicAccount(transaction.getSignerPublicKey(), networkType, getSignSchema()),
            transactionInfo);
    }


}
