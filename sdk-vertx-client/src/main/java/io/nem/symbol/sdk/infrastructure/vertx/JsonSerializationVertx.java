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

package io.nem.symbol.sdk.infrastructure.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.symbol.sdk.api.JsonSerialization;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import org.apache.commons.lang3.Validate;

/**
 * Vertx implementation of {@link JsonSerialization}
 */
public class JsonSerializationVertx implements JsonSerialization {

    private final GeneralTransactionMapper generalTransactionMapper;

    private final JsonHelper jsonHelper;

    public JsonSerializationVertx(ObjectMapper objectMapper) {
        Validate.notNull(objectMapper, "ObjectMapper must not be null");
        this.jsonHelper = new JsonHelperJackson2(objectMapper);
        this.generalTransactionMapper = new GeneralTransactionMapper(jsonHelper);
    }

    @Override
    public String transactionToJson(Transaction transaction) {
        Validate.notNull(transaction, "Transaction must not be null");
        return jsonHelper.print(generalTransactionMapper.mapToDto(transaction));
    }

    @Override
    public Transaction jsonToTransaction(String json) {
        Validate.notNull(json, "Json must not be null");
        return generalTransactionMapper.mapFromDto(jsonHelper.parse(json, TransactionInfoDTO.class));
    }
}
