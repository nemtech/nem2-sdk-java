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
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.transaction.SecretHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
            for (SecretHashAlgorithm secretHashAlgorithm : SecretHashAlgorithm.values()) {
                arguments.add(Arguments.of(repositoryType, secretHashAlgorithm));
            }
        }
        return arguments;
    }


    @ParameterizedTest
    @MethodSource("provider")
    void secretLockAndProofTransaction(RepositoryType type, SecretHashAlgorithm secretHashAlgorithm) {

        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        byte[] secretSeed = RandomUtils.generateRandomBytes(20);
        String secret = ConvertUtils.toHex(secretHashAlgorithm.hash(secretSeed));
        String proof = ConvertUtils.toHex(secretSeed);

        Account account = config().getNemesisAccount1();
        Account account2 = config().getNemesisAccount2();
        NetworkCurrency networkCurrency = get(repositoryFactory.getNetworkCurrency());
        Mosaic mosaic = networkCurrency.createAbsolute(BigInteger.valueOf(1));
        BigInteger amount = mosaic.getAmount();
        SecretLockTransaction secretLockTransaction = SecretLockTransactionFactory
            .create(getNetworkType(), mosaic, BigInteger.valueOf(100), secretHashAlgorithm, secret,
                account2.getAddress()).maxFee(maxFee).build();

        announceAndValidate(type, account, secretLockTransaction);

        SecretProofTransaction secretProofTransaction = SecretProofTransactionFactory
            .create(getNetworkType(), secretHashAlgorithm, account2.getAddress(), secret, proof).maxFee(maxFee).build();

        SecretProofTransaction secretProofTransactionAnnounced = announceAndValidate(type, account,
            secretProofTransaction);

        Assertions.assertEquals(secretHashAlgorithm, secretProofTransactionAnnounced.getHashType());
        Assertions.assertEquals(account2.getAddress(), secretProofTransactionAnnounced.getRecipient());
        Assertions.assertEquals(StringUtils.rightPad(secret, 64, "0"), secretProofTransactionAnnounced.getSecret());
        Assertions.assertEquals(proof, secretProofTransactionAnnounced.getProof());

        SecretLockRepository hashLockRepository = getRepositoryFactory(type).createSecretLockRepository();

        SecretLockInfo info = get(hashLockRepository.getSecretLock(secret));
        Assertions.assertNotNull(info);
        Assertions.assertEquals(account.getAddress(), info.getOwnerAddress());
        Assertions.assertEquals(account2.getAddress(), info.getRecipientAddress());
        Assertions.assertEquals(amount, info.getAmount());
        Assertions.assertEquals(secretHashAlgorithm, info.getHashAlgorithm());
        Assertions.assertEquals(1, info.getStatus());
        Assertions.assertEquals(secret, info.getSecret());

        Page<SecretLockInfo> page = get(hashLockRepository.search(new SecretLockSearchCriteria(account.getAddress())));
        Assertions.assertTrue(page.getData().stream().anyMatch(m -> m.getSecret().equals(secret)));
        Assertions.assertEquals(20, page.getPageSize());

        SecretLockInfo infoSearch = page.getData().stream().filter(m -> m.getSecret().equals(secret)).findFirst().get();
        Assertions.assertNotNull(infoSearch);
        Assertions.assertEquals(account.getAddress(), infoSearch.getOwnerAddress());
        Assertions.assertEquals(account2.getAddress(), infoSearch.getRecipientAddress());
        Assertions.assertEquals(amount, infoSearch.getAmount());
        Assertions.assertEquals(secretHashAlgorithm, infoSearch.getHashAlgorithm());
        Assertions.assertEquals(1, infoSearch.getStatus());
        Assertions.assertEquals(secret, infoSearch.getSecret());


    }

}
