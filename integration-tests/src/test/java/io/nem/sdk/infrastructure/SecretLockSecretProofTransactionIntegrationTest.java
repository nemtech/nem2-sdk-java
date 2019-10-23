/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure;

import io.nem.core.crypto.Hashes;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.SecretProofTransactionFactory;
import java.math.BigInteger;
import java.util.Random;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("squid:S2699")
public class SecretLockSecretProofTransactionIntegrationTest extends BaseIntegrationTest {

    Account account = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSecretLockTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        Address recipient = config().getTestAccount2().getAddress();
        SecretLockTransaction secretLockTransaction =
            SecretLockTransactionFactory.create(getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                recipient
            ).build();

        announceAndValidate(type, account, secretLockTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSecretLockTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        Address recipient = config().getTestAccount2().getAddress();
        SecretLockTransaction transaction =
            SecretLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                recipient
            ).build();

        announceAggregateAndValidate(type, account, transaction);

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSecretProofTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        String proof = Hex.encodeHexString(secretBytes);
        Address recipient = config().getTestAccount2().getAddress();
        SecretLockTransaction secretLockTransaction =
            SecretLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                recipient
            ).build();

        announceAndValidate(type, account, secretLockTransaction);

        SecretProofTransaction secretProofTransaction =
            SecretProofTransactionFactory.create(
                getNetworkType(),
                LockHashAlgorithmType.SHA3_256,
                recipient,
                secret,
                proof).build();

        announceAndValidate(type, account, secretProofTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSecretProofTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        String proof = Hex.encodeHexString(secretBytes);
        Address recipient = config().getTestAccount2().getAddress();
        SecretLockTransaction secretLockTransaction =
            SecretLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                recipient).build();

        announceAndValidate(type, account, secretLockTransaction);

        SecretProofTransaction secretProofTransaction =
            SecretProofTransactionFactory.create(getNetworkType(),
                LockHashAlgorithmType.SHA3_256,
                recipient,
                secret,
                proof
            ).build();

        announceAggregateAndValidate(type, account, secretProofTransaction);
    }
}
