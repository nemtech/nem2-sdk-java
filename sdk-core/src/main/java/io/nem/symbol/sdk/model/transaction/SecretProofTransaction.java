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

import io.nem.symbol.sdk.model.account.UnresolvedAddress;

/**
 * Secret proof transaction.
 */
public class SecretProofTransaction extends Transaction implements RecipientTransaction {

    private final LockHashAlgorithmType hashType;
    private final String secret;
    private final String proof;
    private final UnresolvedAddress recipient;


    /**
     * The transaction contructor using the factory.
     *
     * @param factory the factory with the configured data.
     */
    public SecretProofTransaction(SecretProofTransactionFactory factory) {
        super(factory);
        this.hashType = factory.getHashType();
        this.secret = factory.getSecret();
        this.proof = factory.getProof();
        this.recipient = factory.getRecipient();
    }


    /**
     * Returns the hash algorithm secret is generated with.
     *
     * @return the hash algorithm secret is generated with.
     */
    public LockHashAlgorithmType getHashType() {
        return hashType;
    }

    /**
     * Returns the proof hashed.
     *
     * @return the proof hashed.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Returns proof.
     *
     * @return proof.
     */
    public String getProof() {
        return proof;
    }


    /**
     * @return the recipient
     */
    @Override
    public UnresolvedAddress getRecipient() {
        return recipient;
    }

}
