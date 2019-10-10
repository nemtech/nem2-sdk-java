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

package io.nem.sdk.infrastructure.okhttp;

import com.google.gson.Gson;
import io.nem.sdk.api.JsonSerialization;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import org.apache.commons.lang3.Validate;

/**
 * Vertx implementation of {@link JsonSerialization}
 */
public class JsonSerializationOkHttp implements JsonSerialization {

    private final GeneralTransactionMapper generalTransactionMapper;

    private final JsonHelper jsonHelper;

    public JsonSerializationOkHttp(Gson objectMapper) {
        Validate.notNull(objectMapper, "ObjectMapper must not be null");
        this.jsonHelper = new JsonHelperGson(objectMapper);
        this.generalTransactionMapper = new GeneralTransactionMapper(jsonHelper);
    }

    @Override
    public String transactionToJson(Transaction transaction) {
        Validate.notNull(transaction, "Transaction must not be null");
        return jsonHelper.print(generalTransactionMapper.map(transaction));
    }

    @Override
    public Transaction jsonToTransaction(String json) {
        Validate.notNull(json, "Json must not be null");
        return generalTransactionMapper.map(jsonHelper.parse(json, TransactionInfoDTO.class));
    }
}
