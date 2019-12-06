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

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.account.PublicAccount;

/**
 * The signed transaction object is used to transfer the transaction data and the signature to NIS
 * in order to initiate and broadcast a transaction.
 *
 * @since 1.0
 */
public class SignedTransaction {

    private final PublicAccount signer;
    private final String payload;
    private final String hash;
    private final TransactionType type;

    /**
     * The SignedTransaction constructor.
     *
     * @param signer the signer of the transaction.
     * @param payload the payload.
     * @param hash the hash of the transaction.
     * @param type the transaction type.
     */
    public SignedTransaction(PublicAccount signer, String payload, String hash,
        TransactionType type) {
        this.signer = signer;
        this.payload = payload;
        this.hash = hash;
        this.type = type;
    }

    /**
     * Returns the signer of this transaction.
     *
     * @return the signer of this transaction.
     */
    public PublicAccount getSigner() {
        return signer;
    }

    /**
     * Returns transaction serialized data.
     *
     * @return transaction serialized data
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Returns transaction hash.
     *
     * @return transaction hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * Returns transaction type.
     *
     * @return transaction type
     */
    public TransactionType getType() {
        return type;
    }
}
