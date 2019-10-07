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

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.blockchain.NetworkType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link AggregateTransaction}
 */
public class AggregateTransactionFactory extends TransactionFactory<AggregateTransaction> {

    private final List<Transaction> innerTransactions;

    private final List<AggregateTransactionCosignature> cosignatures;

    private AggregateTransactionFactory(TransactionType type,
        NetworkType networkType,
        List<Transaction> innerTransactions,
        List<AggregateTransactionCosignature> cosignatures) {
        super(type, networkType);
        Validate.notNull(innerTransactions, "InnerTransactions must not be null");
        Validate.notNull(cosignatures, "Cosignatures must not be null");
        this.innerTransactions = innerTransactions;
        this.cosignatures = cosignatures;
    }

    /**
     * Create an aggregate transaction factory that can be customized.
     *
     * @param type Transaction type.
     * @param networkType Network type.
     * @param innerTransactions List of inner transactions.
     * @param cosignatures List of transaction cosigners signatures.
     * @return The aggregate transaction factory
     */
    public static AggregateTransactionFactory create(TransactionType type,
        NetworkType networkType,
        List<Transaction> innerTransactions,
        List<AggregateTransactionCosignature> cosignatures) {
        return new AggregateTransactionFactory(type, networkType, innerTransactions, cosignatures);
    }

    /**
     * Create an aggregate complete transaction factory that can be customized.
     *
     * @param networkType The network type.
     * @param innerTransactions The list of inner innerTransactions.
     * @return The aggregate transaction factory
     */
    public static AggregateTransactionFactory createComplete(NetworkType networkType,
        List<Transaction> innerTransactions) {
        return create(
            TransactionType.AGGREGATE_COMPLETE,
            networkType,
            innerTransactions,
            new ArrayList<>());
    }

    /**
     * Create an aggregate bonded transaction factory that can be customized.
     *
     * @param networkType The network type.
     * @param innerTransactions The list of inner innerTransactions.
     * @return The aggregate transaction factory
     */
    public static AggregateTransactionFactory createBonded(NetworkType networkType,
        List<Transaction> innerTransactions) {
        return create(
            TransactionType.AGGREGATE_BONDED,
            networkType,
            innerTransactions,
            new ArrayList<>());
    }

    /**
     * Returns list of innerTransactions included in the aggregate transaction.
     *
     * @return List of innerTransactions included in the aggregate transaction.
     */
    public List<Transaction> getInnerTransactions() {
        return innerTransactions;
    }

    /**
     * Returns list of transaction cosigners signatures.
     *
     * @return List of transaction cosigners signatures.
     */
    public List<AggregateTransactionCosignature> getCosignatures() {
        return cosignatures;
    }


    @Override
    public AggregateTransaction build() {
        return new AggregateTransaction(this);
    }
}
