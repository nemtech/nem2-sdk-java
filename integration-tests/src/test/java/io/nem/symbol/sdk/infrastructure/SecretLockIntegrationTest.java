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
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.transaction.SecretHashAlgorithm;
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
    void secretLockAndProofTransaction(RepositoryType type, SecretHashAlgorithm lockHashType) {

        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        byte[] secretSeed = RandomUtils.generateRandomBytes(20);
        String secret = ConvertUtils.toHex(lockHashType.hash(secretSeed));
        String proof = ConvertUtils.toHex(secretSeed);

        Account account = config().getNemesisAccount1();
        Account account2 = config().getNemesisAccount2();
        NetworkCurrency networkCurrency = get(repositoryFactory.getNetworkCurrency());
        SecretLockTransaction secretLockTransaction = SecretLockTransactionFactory.create(
            getNetworkType(),
            networkCurrency.createAbsolute(BigInteger.valueOf(1)),
            BigInteger.valueOf(100),
            lockHashType,
            secret,
            account2.getAddress()
        ).maxFee(maxFee).build();

        announceAndValidate(type, account, secretLockTransaction);

        SecretProofTransaction secretProofTransaction = SecretProofTransactionFactory.create(
            getNetworkType(),
            lockHashType,
            account2.getAddress(),
            secret,
            proof).maxFee(maxFee).build();

        SecretProofTransaction secretProofTransactionAnnounced = announceAndValidate(type, account,
            secretProofTransaction);

        Assertions.assertEquals(lockHashType, secretProofTransactionAnnounced.getHashType());
        Assertions
            .assertEquals(account2.getAddress(), secretProofTransactionAnnounced.getRecipient());
        Assertions.assertEquals(StringUtils.rightPad(secret, 64, "0"),
            secretProofTransactionAnnounced.getSecret());
        Assertions.assertEquals(proof,
            secretProofTransactionAnnounced.getProof());


    }

}
