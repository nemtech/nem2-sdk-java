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
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;

/**
 * This interface allow users to serialize and deserialize transaction using the symbol binary
 * format.
 */
public interface BinarySerialization {

  /**
   * It serializes a transaction into a byte array using the symbol buffer format.
   *
   * @param <T> the type of the transaction
   * @param transaction the transaction
   * @return the byte array.
   */
  <T extends Transaction> byte[] serialize(T transaction);

  /**
   * It deserializes the symbol buffer payload into a transaction factory.
   *
   * @param payload the byte array payload
   * @return the {@link TransactionFactory}.
   */
  TransactionFactory<?> deserializeToFactory(byte[] payload);

  /**
   * It deserializes the symbol buffer payload into a transaction.
   *
   * @param payload the byte array payload
   * @return the transaction.
   */
  Transaction deserialize(byte[] payload);

  /**
   * It returns the transaction's byte array size useful to calculate its fee.
   *
   * @param <T> the type of the transaction
   * @param transaction the transaction
   * @return the size of the transaction.
   */
  <T extends Transaction> long getSize(T transaction);
}
