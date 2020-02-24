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

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AggregateTransactionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.CosignatureDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.EmbeddedTransactionInfoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregate transaction mapper.
 */
class AggregateTransactionMapper extends
    AbstractTransactionMapper<AggregateTransactionDTO, AggregateTransaction> {

    private TransactionMapper transactionMapper;

    public AggregateTransactionMapper(JsonHelper jsonHelper,
        TransactionType transactionType,
        TransactionMapper transactionMapper) {
        super(jsonHelper, transactionType, AggregateTransactionDTO.class);
        this.transactionMapper = transactionMapper;
    }

    @Override
    protected AggregateTransactionFactory createFactory(NetworkType networkType,
        AggregateTransactionDTO transaction) {

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
                    .map(aggregateCosignature -> toCosignature(networkType, aggregateCosignature))
                    .collect(Collectors.toList());
        }

        return AggregateTransactionFactory
            .create(getTransactionType(), networkType, transaction.getTransactionsHash(),
                transactions,
                cosignatures);
    }

    private AggregateTransactionCosignature toCosignature(NetworkType networkType,
        CosignatureDTO aggregateCosignature) {
        return new AggregateTransactionCosignature(
            aggregateCosignature.getSignature(),
            PublicAccount
                .createFromPublicKey(aggregateCosignature.getSignerPublicKey(),
                    networkType));
    }

    @Override
    protected void copyToDto(AggregateTransaction transaction, AggregateTransactionDTO dto) {
        List<EmbeddedTransactionInfoDTO> transactions = transaction.getInnerTransactions().stream()
            .map(embeddedTransactionInfoDTO -> transactionMapper
                .mapToEmbedded(embeddedTransactionInfoDTO)).collect(Collectors.toList());
        List<CosignatureDTO> cosignatures = new ArrayList<>();
        if (transaction.getCosignatures() != null) {
            cosignatures =
                transaction.getCosignatures().stream().map(this::toCosignature)
                    .collect(Collectors.toList());
        }
        dto.setTransactionsHash(transaction.getTransactionsHash());
        dto.setCosignatures(cosignatures);
        dto.setTransactions(transactions);
    }

    private CosignatureDTO toCosignature(
        AggregateTransactionCosignature aggregateCosignature) {
        CosignatureDTO cosignatureDTO = new CosignatureDTO();
        cosignatureDTO
            .setSignerPublicKey(aggregateCosignature.getSigner().getPublicKey().toHex());
        cosignatureDTO.setSignature(aggregateCosignature.getSignature());
        return cosignatureDTO;
    }

}
