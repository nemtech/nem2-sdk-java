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

package io.nem.sdk.api;

import io.nem.sdk.model.transaction.Transaction;

/**
 * Implementation of this class knows how to serialize different model object from/to DTOs/JSONs.
 */
public interface JsonSerialization {

    /**
     * It converts a {@link Transaction} to a json string.
     *
     * @param transaction the transaction
     * @return the json string.
     */
    String transactionToJson(Transaction transaction);

    /**
     * It parses and converts a json string into a {@link Transaction}.
     *
     * @param json the json string.
     * @return the transaction
     */
    Transaction jsonToTransaction(String json);


}
