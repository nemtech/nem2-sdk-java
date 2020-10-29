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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecretLockIntegrationTest extends BaseIntegrationTest {

  static List<Arguments> provider() {
    List<Arguments> arguments = new ArrayList<>();
    for (RepositoryType repositoryType : RepositoryType.values()) {
      for (LockHashAlgorithm lockHashAlgorithm : LockHashAlgorithm.values()) {
        arguments.add(Arguments.of(repositoryType, lockHashAlgorithm));
      }
    }
    return arguments;
  }

  @ParameterizedTest
  @MethodSource("provider")
  void secretLockAndProofTransaction(RepositoryType type, LockHashAlgorithm lockHashAlgorithm) {

    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    byte[] secretSeed = RandomUtils.generateRandomBytes(20);
    String secret = ConvertUtils.toHex(lockHashAlgorithm.hash(secretSeed));
    String storedSecret = ConvertUtils.padHex(secret, LockHashAlgorithm.DEFAULT_SECRET_HEX_SIZE);
    if (lockHashAlgorithm == LockHashAlgorithm.HASH_160) {
      Assertions.assertEquals(LockHashAlgorithm.DEFAULT_SECRET_HEX_SIZE, storedSecret.length());
      Assertions.assertEquals(40, secret.length());
    } else {
      Assertions.assertEquals(LockHashAlgorithm.DEFAULT_SECRET_HEX_SIZE, storedSecret.length());
      Assertions.assertEquals(LockHashAlgorithm.DEFAULT_SECRET_HEX_SIZE, secret.length());
    }
    String proof = ConvertUtils.toHex(secretSeed);

    Account account = config().getNemesisAccount1();
    Account account2 = config().getNemesisAccount2();
    Currency currency = get(repositoryFactory.getNetworkCurrency());
    Mosaic mosaic = currency.createAbsolute(BigInteger.valueOf(1));
    BigInteger amount = mosaic.getAmount();
    SecretLockTransaction secretLockTransaction =
        SecretLockTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaic,
                BigInteger.valueOf(100),
                lockHashAlgorithm,
                secret,
                account2.getAddress())
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, account, secretLockTransaction);

    SecretProofTransaction secretProofTransaction =
        SecretProofTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                lockHashAlgorithm,
                account2.getAddress(),
                secret,
                proof)
            .maxFee(maxFee)
            .build();

    SecretProofTransaction secretProofTransactionAnnounced =
        announceAndValidate(type, account, secretProofTransaction);

    sleep(500);
    Assertions.assertEquals(lockHashAlgorithm, secretProofTransactionAnnounced.getHashType());
    Assertions.assertEquals(account2.getAddress(), secretProofTransactionAnnounced.getRecipient());
    Assertions.assertEquals(storedSecret, secretProofTransactionAnnounced.getSecret());
    Assertions.assertEquals(proof, secretProofTransactionAnnounced.getProof());

    SecretLockRepository hashLockRepository =
        getRepositoryFactory(type).createSecretLockRepository();

    SecretLockInfo info =
        get(hashLockRepository.search(
                new SecretLockSearchCriteria(account.getAddress()).secret(storedSecret)))
            .getData()
            .get(0);
    Assertions.assertNotNull(info);
    Assertions.assertEquals(account.getAddress(), info.getOwnerAddress());
    Assertions.assertEquals(account2.getAddress(), info.getRecipientAddress());
    Assertions.assertEquals(amount, info.getAmount());
    Assertions.assertEquals(storedSecret, info.getSecret());
    Assertions.assertEquals(lockHashAlgorithm, info.getHashAlgorithm());
    Assertions.assertEquals(1, info.getStatus());

    Page<SecretLockInfo> page =
        get(
            hashLockRepository.search(
                new SecretLockSearchCriteria(account.getAddress()).order(OrderBy.DESC)));

    Assertions.assertTrue(
        page.getData().stream().anyMatch(m -> m.getSecret().equals(storedSecret)));
    Assertions.assertEquals(20, page.getPageSize());

    SecretLockInfo infoSearch =
        page.getData().stream().filter(m -> m.getSecret().equals(storedSecret)).findFirst().get();
    Assertions.assertNotNull(infoSearch);
    Assertions.assertEquals(account.getAddress(), infoSearch.getOwnerAddress());
    Assertions.assertEquals(account2.getAddress(), infoSearch.getRecipientAddress());
    Assertions.assertEquals(amount, infoSearch.getAmount());
    Assertions.assertEquals(lockHashAlgorithm, infoSearch.getHashAlgorithm());
    Assertions.assertEquals(1, infoSearch.getStatus());
    Assertions.assertEquals(storedSecret, infoSearch.getSecret());
  }
}
