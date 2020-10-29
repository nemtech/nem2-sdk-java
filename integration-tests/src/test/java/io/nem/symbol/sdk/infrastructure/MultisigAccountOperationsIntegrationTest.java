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
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MultisigAccountOperationsIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void cosignatureTransactionOnSign(RepositoryType type) {

    Account multisigAccount = helper().getMultisigAccount(type).getLeft();
    Account cosignatoryAccount = config().getCosignatoryAccount();

    Address recipient = getRecipient();
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipient,
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))))
            .message(new PlainMessage("test-message"))
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createBonded(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    transferTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedTransaction =
        cosignatoryAccount.sign(aggregateTransaction, getGenerationHash());

    TransactionFactory<HashLockTransaction> hashLockTransaction =
        HashLockTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction)
            .maxFee(maxFee);
    SignedTransaction signedHashLockTransaction =
        hashLockTransaction.build().signWith(cosignatoryAccount, getGenerationHash());

    AggregateTransaction finalTransaction =
        getTransactionOrFail(
            getTransactionService(type)
                .announceHashLockAggregateBonded(
                    getListener(type), signedHashLockTransaction, signedTransaction),
            aggregateTransaction);
    Assertions.assertNotNull(finalTransaction);
  }
}
