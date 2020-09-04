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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("squid:S2699")
public class HashLockTransactionIntegrationTest extends BaseIntegrationTest {

    private Account account;

    @BeforeEach
    void setup() {
        account = config().getDefaultAccount();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneLockFundsTransaction(RepositoryType type) {

        TransferTransactionFactory factory = TransferTransactionFactory.create(getNetworkType(), account.getAddress(),
            Collections.singletonList(getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))),
            new PlainMessage("E2ETest:standaloneLockFundsTransaction"));

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createBonded(getNetworkType(),
            Collections.singletonList(factory.build().toAggregate(account.getPublicAccount()))).maxFee(this.maxFee)
            .build();
        SignedTransaction signedTransaction = this.account.sign(aggregateTransaction, getGenerationHash());

        HashLockTransaction hashLockTransaction = HashLockTransactionFactory
            .create(getNetworkType(), getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100), signedTransaction).maxFee(this.maxFee).build();

        announceAndValidate(type, this.account, hashLockTransaction);

        announceAndValidate(type, this.account, aggregateTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateLockFundsTransaction(RepositoryType type) {

        TransferTransactionFactory factory = TransferTransactionFactory
            .create(getNetworkType(), this.account.getAddress(),
                Collections.singletonList(getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))),
                new PlainMessage("E2ETest:standaloneLockFundsTransaction"));

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createBonded(getNetworkType(),
            Collections.singletonList(factory.build().toAggregate(account.getPublicAccount()))).maxFee(this.maxFee)
            .build();

        SignedTransaction signedTransaction = this.account.sign(aggregateTransaction, getGenerationHash());
        HashLockTransaction hashLockTransaction = HashLockTransactionFactory
            .create(getNetworkType(), getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100), signedTransaction).maxFee(this.maxFee).build();

        announceAggregateAndValidate(type, hashLockTransaction, this.account);

        announceAndValidate(type, this.account, aggregateTransaction);
    }
}
