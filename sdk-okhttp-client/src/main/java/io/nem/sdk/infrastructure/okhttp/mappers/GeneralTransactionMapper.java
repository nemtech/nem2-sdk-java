/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp.mappers;

import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.EmbeddedTransactionInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;

/**
 * Entry point for the transaction mapping. This mapper should support all the known transactions.
 *
 * It's basically a delegator to the specific {@link TransactionMapper} registered in this object.
 */
public class GeneralTransactionMapper implements TransactionMapper {

    private final JsonHelper jsonHelper;

    private Map<TransactionType, TransactionMapper> transactionMappers = new EnumMap<>(
        TransactionType.class);

    public GeneralTransactionMapper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
        Validate.notNull(jsonHelper, "jsonHelper must not be null");
        register(new AccountLinkTransactionMapper(jsonHelper));
        register(new AddressAliasTransactionMapper(jsonHelper));
        register(new HashLockTransactionMapper(jsonHelper));
        register(new MosaicAddressRestrictionTransactionMapper(jsonHelper));
        register(new MosaicAliasTransactionMapper(jsonHelper));
        register(new MosaicDefinitionTransactionMapper(jsonHelper));
        register(new MosaicGlobalRestrictionTransactionMapper(jsonHelper));
        register(new MosaicSupplyChangeTransactionMapper(jsonHelper));
        register(new MultisigAccountModificationTransactionMapper(jsonHelper));
        register(new NamespaceRegistrationTransactionMapper(jsonHelper));
        register(new SecretLockTransactionMapper(jsonHelper));
        register(new SecretProofTransactionMapper(jsonHelper));
        register(new TransferTransactionMapper(jsonHelper));
        register(new AccountMetadataTransactionMapper(jsonHelper));
        register(new MosaicMetadataTransactionMapper(jsonHelper));
        register(new NamespaceMetadataTransactionMapper(jsonHelper));
        register(new AccountAddressRestrictionTransactionMapper(jsonHelper));
        register(new AccountMosaicRestrictionTransactionMapper(jsonHelper));
        register(new AccountOperationRestrictionTransactionMapper(jsonHelper));

        register(
            new AggregateTransactionMapper(jsonHelper, TransactionType.AGGREGATE_BONDED, this));
        register(
            new AggregateTransactionMapper(jsonHelper, TransactionType.AGGREGATE_COMPLETE, this));
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
        Validate.notNull(transactionInfoDTO, "transactionInfoDTO must not be null");
        return resolveMapper(transactionInfoDTO).map(transactionInfoDTO);
    }

    @Override
    public Transaction map(TransactionInfoDTO transactionInfoDTO) {
        Validate.notNull(transactionInfoDTO, "transactionInfoDTO must not be null");
        return resolveMapper(transactionInfoDTO).map(transactionInfoDTO);
    }

    @Override
    public EmbeddedTransactionInfoDTO mapToEmbedded(Transaction transaction) {
        Validate.notNull(transaction, "transaction must not be null");
        return resolveMapper(transaction.getType()).mapToEmbedded(transaction);
    }

    @Override
    public TransactionInfoDTO map(Transaction transaction) {
        Validate.notNull(transaction, "transaction must not be null");
        return resolveMapper(transaction.getType()).map(transaction);
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
        return resolveMapper(transactionType);
    }

    private TransactionMapper resolveMapper(TransactionType transactionType) {
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
