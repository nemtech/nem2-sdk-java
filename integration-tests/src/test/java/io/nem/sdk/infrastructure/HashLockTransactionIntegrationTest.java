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

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("squid:S2699")
public class HashLockTransactionIntegrationTest extends BaseIntegrationTest {

    private Account account = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneLockFundsTransaction(RepositoryType type) {
        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Collections.emptyList()).maxFee(this.maxFee).build();
        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, getGenerationHash());

        HashLockTransaction hashLockTransaction =
            HashLockTransactionFactory.create(getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction
            ).maxFee(this.maxFee).build();

        announceAndValidate(type, this.account, hashLockTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateLockFundsTransaction(RepositoryType type) {
        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Collections.emptyList()).maxFee(this.maxFee).build();
        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, getGenerationHash());
        HashLockTransaction hashLockTransaction =
            HashLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).maxFee(this.maxFee).build();

        announceAggregateAndValidate(type, hashLockTransaction, this.account);
    }
}
