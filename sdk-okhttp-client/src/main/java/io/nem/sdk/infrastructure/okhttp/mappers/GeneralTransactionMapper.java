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

package io.nem.sdk.infrastructure.okhttp.mappers;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.EmbeddedTransactionInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import java.util.EnumMap;
import java.util.Map;

/**
 * Entry point for the transaction mapping. This mapper should support all the known transactions.
 *
 * It's basically a delegator to the specific {@link TransactionMapper} registered in this object.
 */
public class GeneralTransactionMapper implements TransactionMapper {

    private final JsonHelper jsonHelper;

    private Map<TransactionType, TransactionMapper> transactionMappers = new EnumMap<>(TransactionType.class);

    public GeneralTransactionMapper(JsonHelper jsonHelper, SignSchema signSchema) {
        this.jsonHelper = jsonHelper;
        register(new AccountLinkTransactionMapper(jsonHelper, signSchema));
        register(new AddressAliasTransactionMapper(jsonHelper, signSchema));
        register(new HashLockTransactionMapper(jsonHelper, signSchema));
        register(new MosaicAliasTransactionMapper(jsonHelper, signSchema));
        register(new MosaicDefinitionTransactionMapper(jsonHelper, signSchema));
        register(new MosaicSupplyChangeTransactionMapper(jsonHelper, signSchema));
        register(new MultisigAccountModificationTransactionMapper(jsonHelper, signSchema));
        register(new NamespaceRegistrationTransactionMapper(jsonHelper, signSchema));
        register(new SecretLockTransactionMapper(jsonHelper, signSchema));
        register(new SecretProofTransactionMapper(jsonHelper, signSchema));
        register(new TransferTransactionMapper(jsonHelper, signSchema));

        register(
            new AggregateTransactionMapper(jsonHelper, TransactionType.AGGREGATE_BONDED, this,
                signSchema));
        register(
            new AggregateTransactionMapper(jsonHelper, TransactionType.AGGREGATE_COMPLETE, this,
                signSchema));
    }

    private void register(TransactionMapper mapper) {
        if (transactionMappers.put(mapper.getTransactionType(), mapper) != null) {
            throw new IllegalArgumentException(
                "TransactionMapper for type " + mapper.getTransactionType()
                    + " was already registered!");
        }
    }

    @Override
    public Transaction map(EmbeddedTransactionInfoDTO transactionInfoDTO) {
        return resolveMapper(transactionInfoDTO).map(transactionInfoDTO);
    }

    @Override
    public Transaction map(TransactionInfoDTO transactionInfoDTO) {
        return resolveMapper(transactionInfoDTO).map(transactionInfoDTO);
    }

    @Override
    public TransactionType getTransactionType() {
        //All transaction types supported.
        return null;
    }

    private TransactionMapper resolveMapper(Object transactionInfoJson) {
        Integer type = getJsonHelper().getInteger(transactionInfoJson, "transaction", "type");
        if (type == null) {
            throw new IllegalArgumentException(
                "Transaction cannot be mapped, object does not not have transaction type.");
        }
        TransactionType transactionType = TransactionType.rawValueOf(type);
        TransactionMapper mapper = transactionMappers.get(transactionType);
        if (mapper == null) {
            throw new UnsupportedOperationException(
                "Unimplemented Transaction type " + transactionType);
        }
        return mapper;
    }

    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }
}
