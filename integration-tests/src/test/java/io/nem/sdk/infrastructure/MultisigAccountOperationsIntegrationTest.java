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

import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MultisigAccountOperationsIntegrationTest extends BaseIntegrationTest {

    private Account multisigAccount = config().getMultisigAccount();

    private Account cosignatoryAccount = config().getCosignatoryAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void cosignatureTransactionOnSign(RepositoryType type) {
        Address recipient = getRecipient();
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipient,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                PlainMessage.create("test-message")
            ).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Collections.singletonList(
                    transferTransaction.toAggregate(this.multisigAccount.getPublicAccount())))
                .build();

        SignedTransaction signedTransaction =
            this.cosignatoryAccount.sign(aggregateTransaction, getGenerationHash());

        hashLock(type, this.cosignatoryAccount, signedTransaction);

        get(getTransactionRepository(type).announce(signedTransaction));

        validateTransactionAnnounceCorrectly(cosignatoryAccount.getAddress(),
            signedTransaction.getHash(), type);
    }

    private TransactionRepository getTransactionRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createTransactionRepository();
    }

    protected void hashLock(RepositoryType type, Account account,
        SignedTransaction signedTransaction) {
        HashLockTransaction hashLockTransaction =
            HashLockTransactionFactory.create(getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).build();
        announceAndValidate(type, account, hashLockTransaction);
    }
}
