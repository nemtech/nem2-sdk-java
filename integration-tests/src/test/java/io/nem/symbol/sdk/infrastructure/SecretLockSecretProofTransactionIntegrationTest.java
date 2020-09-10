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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("squid:S2699")
public class SecretLockSecretProofTransactionIntegrationTest extends BaseIntegrationTest {

  Account account;

  @BeforeEach
  void setup() {
    account = config().getDefaultAccount();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void standaloneSecretLockTransaction(RepositoryType type) {
    byte[] secretBytes = RandomUtils.generateRandomBytes(20);
    byte[] result = Hashes.sha3_256(secretBytes);
    String secret = ConvertUtils.toHex(result);
    Address recipient = config().getTestAccount2().getAddress();
    SecretLockTransaction secretLockTransaction =
        SecretLockTransactionFactory.create(
                getNetworkType(),
                getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithm.SHA3_256,
                secret,
                recipient)
            .maxFee(this.maxFee)
            .build();

    announceAndValidate(type, account, secretLockTransaction);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void aggregateSecretLockTransaction(RepositoryType type) {
    byte[] secretBytes = RandomUtils.generateRandomBytes(20);
    byte[] result = Hashes.sha3_256(secretBytes);
    String secret = ConvertUtils.toHex(result);
    Address recipient = config().getTestAccount2().getAddress();
    SecretLockTransaction transaction =
        SecretLockTransactionFactory.create(
                getNetworkType(),
                getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithm.SHA3_256,
                secret,
                recipient)
            .maxFee(this.maxFee)
            .build();

    announceAggregateAndValidate(type, transaction, account);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void standaloneSecretProofTransaction(RepositoryType type) {
    byte[] secretBytes = RandomUtils.generateRandomBytes(20);
    byte[] result = Hashes.sha3_256(secretBytes);
    String secret = ConvertUtils.toHex(result);
    String proof = ConvertUtils.toHex(secretBytes);
    Address recipient = config().getTestAccount2().getAddress();
    SecretLockTransaction secretLockTransaction =
        SecretLockTransactionFactory.create(
                getNetworkType(),
                getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithm.SHA3_256,
                secret,
                recipient)
            .maxFee(this.maxFee)
            .build();

    announceAndValidate(type, account, secretLockTransaction);

    SecretProofTransaction secretProofTransaction =
        SecretProofTransactionFactory.create(
                getNetworkType(), LockHashAlgorithm.SHA3_256, recipient, secret, proof)
            .maxFee(this.maxFee)
            .build();

    announceAndValidate(type, account, secretProofTransaction);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void aggregateSecretProofTransaction(RepositoryType type) {
    byte[] secretBytes = RandomUtils.generateRandomBytes(20);
    byte[] result = Hashes.sha3_256(secretBytes);
    String secret = ConvertUtils.toHex(result);
    String proof = ConvertUtils.toHex(secretBytes);
    Address recipient = config().getTestAccount2().getAddress();
    SecretLockTransaction secretLockTransaction =
        SecretLockTransactionFactory.create(
                getNetworkType(),
                getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithm.SHA3_256,
                secret,
                recipient)
            .maxFee(this.maxFee)
            .build();

    announceAndValidate(type, account, secretLockTransaction);

    SecretProofTransaction secretProofTransaction =
        SecretProofTransactionFactory.create(
                getNetworkType(), LockHashAlgorithm.SHA3_256, recipient, secret, proof)
            .maxFee(this.maxFee)
            .build();

    announceAggregateAndValidate(type, secretProofTransaction, account);
  }
}
