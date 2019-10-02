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
import io.nem.sdk.model.mosaic.Mosaic;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link HashLockTransaction}
 */
public class HashLockTransactionFactory extends TransactionFactory<HashLockTransaction> {

    private final Mosaic mosaic;
    private final BigInteger duration;
    private final SignedTransaction signedTransaction;

    public HashLockTransactionFactory(
        NetworkType networkType,
        Mosaic mosaic,
        BigInteger duration,
        SignedTransaction signedTransaction) {
        super(TransactionType.LOCK, networkType);
        Validate.notNull(mosaic, "Mosaic must not be null");
        Validate.notNull(duration, "Duration must not be null");
        Validate.notNull(signedTransaction, "Signed transaction must not be null");
        this.mosaic = mosaic;
        this.duration = duration;
        this.signedTransaction = signedTransaction;
    }

    public static HashLockTransactionFactory create(NetworkType networkType,
        Mosaic mosaic, BigInteger duration, SignedTransaction signedTransaction) {
        return new HashLockTransactionFactory(networkType, mosaic, duration, signedTransaction);
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
     * Returns funds lock duration in number of blocks.
     *
     * @return funds lock duration in number of blocks.
     */
    public BigInteger getDuration() {
        return duration;
    }

    /**
     * Returns signed transaction for which funds are locked.
     *
     * @return signed transaction for which funds are locked.
     */
    public SignedTransaction getSignedTransaction() {
        return signedTransaction;
    }

    @Override
    public HashLockTransaction build() {
        return new HashLockTransaction(this);
    }
}
