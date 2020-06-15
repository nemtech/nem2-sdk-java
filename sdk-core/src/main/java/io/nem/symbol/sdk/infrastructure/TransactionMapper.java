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

package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;

/**
 * A transaction mapper knows how to map DTO for standard and embedded transactions into a {@link Transaction} object.
 */
public interface TransactionMapper {

    /**
     * It maps an embedded or top level transaction
     *
     * @param transactionDto the transaction
     * @return the {@link Transaction}
     */
    Transaction mapFromDto(Object transactionDto);

    /**
     * It maps an embedded or top level transaction to a factory.
     *
     * @param transactionDto the transaction
     * @return the {@link TransactionFactory}
     */
    TransactionFactory<?> mapToFactoryFromDto(Object transactionDto);

    /**
     * It maps an transaction to an DTO transaction.
     *
     * @param transaction the the general transaction
     * @return the {@link Object}
     */
    default Object mapToDto(Transaction transaction) {
        return this.mapToDto(transaction, null);
    }

    /**
     * It maps an transaction to an DTO transaction.
     *
     * @param transaction the the general transaction
     * @param embedded if it's known to be embedded.
     * @return the {@link Object}
     */
    Object mapToDto(Transaction transaction, Boolean embedded);

    /**
     * The type of transactions this mapper supports.
     *
     * @return the supported transaction type or null if supports all.
     */
    TransactionType getTransactionType();

}
