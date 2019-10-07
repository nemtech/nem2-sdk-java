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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link SecretLockTransaction}
 */
public class SecretLockTransactionFactory extends TransactionFactory<SecretLockTransaction> {

    private final Mosaic mosaic;
    private final BigInteger duration;
    private final LockHashAlgorithmType hashAlgorithm;
    private final String secret;
    private final Address recipient;

    private SecretLockTransactionFactory(
        NetworkType networkType,
        Mosaic mosaic,
        BigInteger duration,
        LockHashAlgorithmType hashAlgorithm,
        String secret,
        Address recipient) {
        super(TransactionType.SECRET_LOCK, networkType);
        Validate.notNull(mosaic, "Mosaic must not be null");
        Validate.notNull(duration, "Duration must not be null");
        Validate.notNull(secret, "Secret must not be null");
        Validate.notNull(recipient, "Recipient must not be null");
        if (!LockHashAlgorithmType.validator(hashAlgorithm, secret)) {
            throw new IllegalArgumentException(
                "HashType and Secret have incompatible length or not hexadecimal string");
        }
        this.mosaic = mosaic;
        this.duration = duration;
        this.hashAlgorithm = hashAlgorithm;
        this.secret = secret;
        this.recipient = recipient;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param mosaic Mosaic.
     * @param duration Duration.
     * @param hashAlgorithm Hash algorithm.
     * @param secret Secret.
     * @param recipient Recipient.
     * @return Secret lock transaction.
     */
    public static SecretLockTransactionFactory create(NetworkType networkType, Mosaic mosaic,
        BigInteger duration, LockHashAlgorithmType hashAlgorithm, String secret, Address recipient) {
        return new SecretLockTransactionFactory(networkType, mosaic, duration, hashAlgorithm, secret, recipient);
    }

    /**
     * Returns locked mosaic.
     *
     * @return locked mosaic.
     */
    public Mosaic getMosaic() {
        return mosaic;
    }

    /**
     * Returns duration for the funds to be released or returned.
     *
     * @return duration for the funds to be released or returned.
     */
    public BigInteger getDuration() {
        return duration;
    }

    /**
     * Returns the hash algorithm, secret is generated with.
     *
     * @return the hash algorithm, secret is generated with.
     */
    public LockHashAlgorithmType getHashAlgorithm() {
        return hashAlgorithm;
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
     * Returns the recipient of the funds.
     *
     * @return the recipient of the funds.
     */
    public Address getRecipient() {
        return recipient;
    }

    @Override
    public SecretLockTransaction build() {
        return new SecretLockTransaction(this);
    }
}
