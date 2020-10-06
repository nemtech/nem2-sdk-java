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
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
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
  void aggregateWhenEmptyInnerTransactions(RepositoryType type) {

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(), getDeadline(), Collections.emptyList())
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, aggregateTransaction, config().getDefaultAccount());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void isMultisigAccountModificationTransactionAdditionComplete(RepositoryType type) {

    Account multisigAccount = helper().getMultisigAccount(type);
    Account cosignatoryAccount = config().getCosignatoryAccount();
    Account cosignatory2Account = config().getCosignatory2Account();

    List<Account> accounts = Arrays.asList(cosignatoryAccount, cosignatory2Account);
    List<UnresolvedAddress> additions =
        accounts.stream().map(Account::getAddress).collect(Collectors.toList());
    MultisigAccountModificationTransaction multisigAccountModificationTransaction =
        MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                (byte) 1,
                (byte) 1,
                additions,
                Collections.emptyList())
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    multisigAccountModificationTransaction.toAggregate(
                        multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount, accounts, getGenerationHash());

    AggregateTransactionService aggregateTransactionService =
        new AggregateTransactionServiceImpl(getRepositoryFactory(type));

    Assertions.assertTrue(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void isTransferFromMultisigComplete(RepositoryType type) {

    Account multisigAccount = helper().getMultisigAccount(type);
    Account cosignatoryAccount = config().getCosignatoryAccount();

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(), getDeadline(), getRecipient(), Collections.emptyList())
            .message(new PlainMessage(""))
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    transferTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount,
            Arrays.asList(cosignatoryAccount, config().getTestAccount()),
            getGenerationHash());

    AggregateTransactionService aggregateTransactionService =
        new AggregateTransactionServiceImpl(getRepositoryFactory(type));

    Assertions.assertTrue(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void isTransferFromMultisigNotComplete(RepositoryType type) {

    Account multisigAccount = helper().getMultisigAccount(type);

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(), getDeadline(), getRecipient(), Collections.emptyList())
            .message(new PlainMessage(""))
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    transferTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount,
            Collections.singletonList(config().getTestAccount()),
            getGenerationHash());

    AggregateTransactionService aggregateTransactionService =
        new AggregateTransactionServiceImpl(getRepositoryFactory(type));

    Assertions.assertFalse(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void isMultisigAccountModificationTransactionAdditionNotComplete(RepositoryType type) {

    Account testAccount = helper().getTestAccount(type);

    Account multisigAccount = helper().getMultisigAccount(type);
    Account cosignatoryAccount = config().getCosignatoryAccount();
    Account cosignatory2Account = config().getCosignatory2Account();

    List<Account> accounts = Arrays.asList(cosignatoryAccount, cosignatory2Account);

    List<UnresolvedAddress> additions =
        accounts.stream().map(Account::getAddress).collect(Collectors.toList());
    MultisigAccountModificationTransaction multisigAccountModificationTransaction =
        MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                (byte) 1,
                (byte) 1,
                additions,
                Collections.emptyList())
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    multisigAccountModificationTransaction.toAggregate(
                        multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount, Collections.singletonList(testAccount), getGenerationHash());

    AggregateTransactionService aggregateTransactionService =
        new AggregateTransactionServiceImpl(getRepositoryFactory(type));

    Assertions.assertFalse(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void isMultisigAccountModificationTransactionDeletionComplete(RepositoryType type) {

    Account multisigAccount = helper().getMultisigAccount(type);
    Account cosignatoryAccount = config().getCosignatoryAccount();
    Account cosignatory2Account = config().getCosignatory2Account();
    List<Account> accounts = Arrays.asList(cosignatoryAccount, cosignatory2Account);

    MultisigAccountModificationTransaction multisigAccountModificationTransaction =
        MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                (byte) 1,
                (byte) 1,
                Collections.emptyList(),
                Collections.singletonList(accounts.get(0).getAddress()))
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    multisigAccountModificationTransaction.toAggregate(
                        multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount, Collections.singletonList(accounts.get(1)), getGenerationHash());

    AggregateTransactionService aggregateTransactionService =
        new AggregateTransactionServiceImpl(getRepositoryFactory(type));

    Assertions.assertTrue(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void isMultisigAccountModificationTransactionDeletionNotComplete(RepositoryType type) {

    Account multisigAccount = helper().getMultisigAccount(type);
    Account cosignatoryAccount = config().getCosignatoryAccount();
    Account cosignatory2Account = config().getCosignatory2Account();

    List<Account> accounts = Arrays.asList(cosignatoryAccount, cosignatory2Account);

    MultisigAccountModificationTransaction multisigAccountModificationTransaction =
        MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                (byte) 1,
                (byte) 1,
                Collections.emptyList(),
                Collections.singletonList(accounts.get(0).getAddress()))
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    multisigAccountModificationTransaction.toAggregate(
                        multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount,
            Collections.singletonList(config().getTestAccount()), // Random
            // account
            getGenerationHash());

    AggregateTransactionService aggregateTransactionService =
        new AggregateTransactionServiceImpl(getRepositoryFactory(type));

    Assertions.assertFalse(get(aggregateTransactionService.isComplete(signedAggregateTransaction)));
  }
}
