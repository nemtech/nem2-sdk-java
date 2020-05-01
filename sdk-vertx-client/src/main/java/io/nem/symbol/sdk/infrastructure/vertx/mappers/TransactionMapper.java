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

package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.EmbeddedTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoExtendedDTO;

/**
 * A transaction mapper knows how to map DTO for standard and embedded transactions into a {@link
 * Transaction} object.
 */
public interface TransactionMapper {

    /**
     * It maps an embedded transaction included in an aggregate transaction.
     *
     * @param transactionInfoDTO the embedded transaction
     * @return the {@link Transaction}
     */
    Transaction map(EmbeddedTransactionInfoDTO transactionInfoDTO);

    /**
     * It maps an embedded transaction included in an aggregate transaction.
     *
     * @param transactionInfoExtendedDTO the embedded transaction
     * @return the {@link Transaction}
     */
    Transaction map(TransactionInfoExtendedDTO transactionInfoExtendedDTO);

    /**
     * It maps a general transaction included in a top level json response.
     *
     * @param transactionInfoDTO the the general transaction
     * @return the {@link Transaction}
     */
    Transaction map(TransactionInfoDTO transactionInfoDTO);

    /**
     * It maps an inner transaction to an embedded DTO transaction.
     *
     * @param transaction the transaction.
     * @return the {@link EmbeddedTransactionInfoDTO}
     */
    EmbeddedTransactionInfoDTO mapToEmbedded(Transaction transaction);

    /**
     *  It maps an transaction to an DTO transaction.
     *
     * @param transaction the the general transaction
     * @return the {@link TransactionInfoDTO}
     */
    TransactionInfoDTO map(Transaction transaction);

    /**
     * The type of transactions this mapper supports.
     *
     * @return the supported transaction type or null if supports all.
     */
    TransactionType getTransactionType();

}
