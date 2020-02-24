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
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link SecretProofTransaction}
 */
public class SecretProofTransactionFactory extends TransactionFactory<SecretProofTransaction> {

    private final LockHashAlgorithmType hashType;
    private final String secret;
    private final String proof;
    private final UnresolvedAddress recipient;

    private SecretProofTransactionFactory(
        final NetworkType networkType,
        final LockHashAlgorithmType hashType,
        final UnresolvedAddress recipient,
        final String secret,
        final String proof) {
        super(TransactionType.SECRET_PROOF, networkType);
        Validate.notNull(secret, "Secret must not be null.");
        Validate.notNull(proof, "Proof must not be null.");
        Validate.notNull(recipient, "Recipient must not be null.");
        if (!LockHashAlgorithmType.validator(hashType, secret)) {
            throw new IllegalArgumentException(
                "HashType and Secret have incompatible length or not hexadecimal string");
        }
        this.hashType = hashType;
        this.secret = secret;
        this.proof = proof;
        this.recipient = recipient;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param hashType Hash algorithm secret is generated with.
     * @param recipient Recipient address.
     * @param secret Seed proof hashed.
     * @param proof Seed proof
     * @return Secret proof transaction.
     */
    public static SecretProofTransactionFactory create(NetworkType networkType,
        LockHashAlgorithmType hashType, UnresolvedAddress recipient, String secret, String proof) {
        return new SecretProofTransactionFactory(networkType, hashType, recipient, secret, proof);
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
    public UnresolvedAddress getRecipient() {
        return recipient;
    }

    @Override
    public SecretProofTransaction build() {
        return new SecretProofTransaction(this);
    }
}
