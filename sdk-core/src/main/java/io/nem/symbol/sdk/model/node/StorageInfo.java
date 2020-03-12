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

package io.nem.symbol.sdk.model.node;

/**
 * The blockchain storage info structure describes stored data.
 *
 * @since 1.0
 */
public class StorageInfo {

    private final Integer numAccounts;
    private final Integer numBlocks;
    private final Integer numTransactions;

    public StorageInfo(Integer numAccounts, Integer numBlocks, Integer numTransactions) {
        this.numAccounts = numAccounts;
        this.numBlocks = numBlocks;
        this.numTransactions = numTransactions;
    }

    /**
     * Returns number of accounts published in the blockchain.
     *
     * @return Long
     */
    public Integer getNumAccounts() {
        return numAccounts;
    }

    /**
     * Returns number of confirmed blocks.
     *
     * @return Long
     */
    public Integer getNumBlocks() {
        return numBlocks;
    }

    /**
     * Returns number of confirmed transactions.
     *
     * @return Long
     */
    public Integer getNumTransactions() {
        return numTransactions;
    }
}
