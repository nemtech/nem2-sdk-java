/*
 * Copyright 2019 NEM
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

package io.nem.sdk.api;

import io.nem.sdk.model.transaction.Transaction;


/**
 * This interface allow users to serialize and deserialize transaction using the catapult binary
 * format.
 */
public interface BinarySerialization {

    /**
     * It serializes a transaction into a byte array using the catapult buffer format.
     *
     * @param transaction the transaction
     * @return the byte array.
     */
    byte[] serialize(Transaction transaction);


    /**
     * It deserializes the catapult buffer payload into a transaction.
     *
     * @param payload the byte array payload
     * @return the transaction.
     */
    Transaction deserialize(byte[] payload);

}
