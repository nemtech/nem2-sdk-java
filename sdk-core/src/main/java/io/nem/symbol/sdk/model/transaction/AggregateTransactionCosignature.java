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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.PublicAccount;

/**
 * The model representing cosignature of an aggregate transaction.
 *
 * @since 1.0
 */
public class AggregateTransactionCosignature {

    public static final long VERSION = 1;
    private final String signature;
    private final PublicAccount signer;

    public AggregateTransactionCosignature(String signature, PublicAccount signer) {
        this.signature = signature;
        this.signer = signer;
    }

    /**
     * Returns the signature of aggregate transaction done by the cosigner.
     *
     * @return String
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Returns cosigner public account.
     *
     * @return {@link PublicAccount}
     */
    public PublicAccount getSigner() {
        return signer;
    }
}
