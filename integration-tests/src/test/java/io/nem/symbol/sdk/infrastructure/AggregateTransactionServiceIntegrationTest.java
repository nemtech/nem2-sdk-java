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

import io.nem.symbol.sdk.api.AggregateTransactionService;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggregateTransactionServiceIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void isMultisigAccountModificationTransactionAdditionComplete(RepositoryType type) {

        Account multisigAccount = config().getMultisigAccount();
        List<Account> accounts = Arrays.asList(config().getCosignatoryAccount(),
            config().getCosignatory2Account());

        List<PublicAccount> additions = accounts.stream()
            .map(Account::getPublicAccount).collect(Collectors.toList());
        MultisigAccountModificationTransaction multisigAccountModificationTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 1, (byte) 1, additions, Collections.emptyList())
            .maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            getNetworkType(),
            Collections.singletonList(
                multisigAccountModificationTransaction
                    .toAggregate(multisigAccount.getPublicAccount()))
        ).maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount, accounts,
                getGenerationHash());

        AggregateTransactionService aggregateTransactionService = new AggregateTransactionServiceImpl(
            getRepositoryFactory(type));

        Assertions
            .assertTrue(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));

    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void isTransferFromMultisigComplete(RepositoryType type) {

        Account multisigAccount = config().getMultisigAccount();

        TransferTransaction transferTransaction = TransferTransactionFactory
            .create(getNetworkType(), getRecipient(), Collections.emptyList(), PlainMessage.Empty)
            .maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            getNetworkType(),
            Collections.singletonList(
                transferTransaction
                    .toAggregate(multisigAccount.getPublicAccount()))
        ).maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount,
                Arrays.asList(config().getCosignatoryAccount(), config().getTestAccount()),
                getGenerationHash());

        AggregateTransactionService aggregateTransactionService = new AggregateTransactionServiceImpl(
            getRepositoryFactory(type));

        Assertions
            .assertTrue(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void isTransferFromMultisigNotComplete(RepositoryType type) {

        Account multisigAccount = config().getMultisigAccount();

        TransferTransaction transferTransaction = TransferTransactionFactory
            .create(getNetworkType(), getRecipient(), Collections.emptyList(), PlainMessage.Empty)
            .maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            getNetworkType(),
            Collections.singletonList(
                transferTransaction
                    .toAggregate(multisigAccount.getPublicAccount()))
        ).maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount,
                Collections.singletonList(config().getTestAccount()),
                getGenerationHash());

        AggregateTransactionService aggregateTransactionService = new AggregateTransactionServiceImpl(
            getRepositoryFactory(type));

        Assertions
            .assertFalse(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void isMultisigAccountModificationTransactionAdditionNotComplete(RepositoryType type) {

        Account multisigAccount = config().getMultisigAccount();
        List<Account> accounts = Arrays.asList(config().getCosignatoryAccount(),
            config().getCosignatory2Account());

        List<PublicAccount> additions = accounts.stream()
            .map(Account::getPublicAccount).collect(Collectors.toList());
        MultisigAccountModificationTransaction multisigAccountModificationTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 1, (byte) 1, additions, Collections.emptyList())
            .maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            getNetworkType(),
            Collections.singletonList(
                multisigAccountModificationTransaction
                    .toAggregate(multisigAccount.getPublicAccount()))
        ).maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount,
                Collections.singletonList(getTestAccount()),
                getGenerationHash());

        AggregateTransactionService aggregateTransactionService = new AggregateTransactionServiceImpl(
            getRepositoryFactory(type));

        Assertions
            .assertFalse(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void isMultisigAccountModificationTransactionDeletionComplete(RepositoryType type) {

        Account multisigAccount = config().getMultisigAccount();
        List<Account> accounts = Arrays.asList(config().getCosignatoryAccount(),
            config().getCosignatory2Account());

        MultisigAccountModificationTransaction multisigAccountModificationTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 1, (byte) 1, Collections.emptyList(),
                Collections.singletonList(accounts.get(0).getPublicAccount()))
            .maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            getNetworkType(),
            Collections.singletonList(
                multisigAccountModificationTransaction
                    .toAggregate(multisigAccount.getPublicAccount()))
        ).maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount,
                Collections.singletonList(accounts.get(1)),
                getGenerationHash());

        AggregateTransactionService aggregateTransactionService = new AggregateTransactionServiceImpl(
            getRepositoryFactory(type));

        Assertions
            .assertTrue(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void isMultisigAccountModificationTransactionDeletionNotComplete(RepositoryType type) {

        Account multisigAccount = config().getMultisigAccount();
        List<Account> accounts = Arrays.asList(config().getCosignatoryAccount(),
            config().getCosignatory2Account());

        List<PublicAccount> additions = accounts.stream()
            .map(Account::getPublicAccount).collect(Collectors.toList());
        MultisigAccountModificationTransaction multisigAccountModificationTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 1, (byte) 1, Collections.emptyList(),
                Collections.singletonList(accounts.get(0).getPublicAccount()))
            .maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            getNetworkType(),
            Collections.singletonList(
                multisigAccountModificationTransaction
                    .toAggregate(multisigAccount.getPublicAccount()))
        ).maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount,
                Collections.singletonList(config().getTestAccount()), //Random account
                getGenerationHash());

        AggregateTransactionService aggregateTransactionService = new AggregateTransactionServiceImpl(
            getRepositoryFactory(type));

        Assertions
            .assertFalse(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));

    }


}
