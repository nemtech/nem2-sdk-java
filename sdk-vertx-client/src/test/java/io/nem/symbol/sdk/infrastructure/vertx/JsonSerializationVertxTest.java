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


import io.nem.symbol.sdk.api.JsonSerialization;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.vertx.core.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link JsonSerialization}
 */
public class JsonSerializationVertxTest {

    private final JsonSerialization jsonSerialization = new JsonSerializationVertx(
        JsonHelperJackson2.configureMapper(Json.mapper));

    @Test
    public void jsonToTransaction() {
        String json = TestHelperVertx.loadResource(
            "TransactionMapping-shouldCreateAggregateTransferTransaction.json"
        );
        Transaction transaction = jsonSerialization.jsonToTransaction(json);
        Assertions.assertNotNull(transaction);

        String mappedJson = jsonSerialization.transactionToJson(transaction);
        Assertions.assertNotNull(mappedJson);
    }

}
