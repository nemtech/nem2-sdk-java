/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.blockchain;

/**
 * The blockchain storage info structure describes stored data.
 *
 * @since 1.0
 */
public class StorageInfo {

    private final Long numAccounts;
    private final Long numBlocks;
    private final Long numTransactions;

    public StorageInfo(Long numAccounts, Long numBlocks, Long numTransactions) {
        this.numAccounts = numAccounts;
        this.numBlocks = numBlocks;
        this.numTransactions = numTransactions;
    }

    /**
     * Returns number of accounts published in the blockchain.
     *
     * @return Long
     */
    public Long getNumAccounts() {
        return numAccounts;
    }

    /**
     * Returns number of confirmed blocks.
     *
     * @return Long
     */
    public Long getNumBlocks() {
        return numBlocks;
    }

    /**
     * Returns number of confirmed transactions.
     *
     * @return Long
     */
    public Long getNumTransactions() {
        return numTransactions;
    }
}
