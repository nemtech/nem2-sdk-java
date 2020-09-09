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

import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings("squid:S1607")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MultisignIntegrationTest extends BaseIntegrationTest {

  private Account account;
  private Account account2;
  private Account multisigAccount;
  private Account cosignAccount1;
  private Account cosignAccount2;
  private Account cosignAccount3;

  @BeforeEach
  void setup() {
    account = config().getNemesisAccount1();
    account2 = config().getNemesisAccount2();
    multisigAccount = config().getNemesisAccount(7);
    cosignAccount1 = config().getNemesisAccount5();
    cosignAccount2 = config().getCosignatory2Account();
    cosignAccount3 = config().getCosignatory3Account();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void createMultisigAccount(RepositoryType type) {

    boolean multisigAccountExist =
        get(
            getRepositoryFactory(type)
                .createMultisigRepository()
                .getMultisigAccountInfo(multisigAccount.getAddress())
                .map(s -> true)
                .onErrorReturnItem(false));
    if (multisigAccountExist) {
      System.out.println(
          "Multisign account " + multisigAccount.getAddress().plain() + " already exist");
      return;
    }
    MultisigAccountModificationTransaction modifyMultisigAccountTransaction =
        MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                (byte) 2,
                (byte) 1,
                Arrays.asList(
                    cosignAccount1.getAddress(),
                    cosignAccount2.getAddress(),
                    cosignAccount3.getAddress()),
                Collections.emptyList())
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    modifyMultisigAccountTransaction.toAggregate(
                        multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount,
            Arrays.asList(cosignAccount1, cosignAccount2, cosignAccount3),
            getGenerationHash());

    Transaction transaction =
        get(getTransactionService(type).announce(getListener(type), signedTransaction));

    Assertions.assertNotNull(transaction);
  }

  @Disabled
  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnCosignatureAddedViaListener2(RepositoryType type) {
    Listener listener = getListener(type);
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    TransactionRepository transactionRepository = repositoryFactory.createTransactionRepository();

    SignedTransaction signedAggregatedTx =
        createSignedAggregatedBondTransaction(
            multisigAccount, cosignAccount1, account2.getAddress());

    Object finalObject =
        get(
            createHashLockTransactionAndAnnounce(type, signedAggregatedTx, cosignAccount1)
                .flatMap(
                    t -> {
                      System.out.println("hash lock finished");

                      TransactionServiceImpl transactionService =
                          new TransactionServiceImpl(getRepositoryFactory(type));

                      return transactionService
                          .announceAggregateBonded(listener, signedAggregatedTx)
                          .flatMap(
                              a -> {
                                System.out.println("Aggregate bonded finished");
                                return repositoryFactory
                                    .createTransactionRepository()
                                    .search(
                                        new TransactionSearchCriteria(TransactionGroup.PARTIAL)
                                            .signerPublicKey(
                                                cosignAccount1.getPublicAccount().getPublicKey()))
                                    .flatMap(
                                        (page) -> {
                                          List<Transaction> transactions = page.getData();
                                          System.out.println(
                                              "partialTransactions " + transactions.size());
                                          AggregateTransaction transactionToCosign =
                                              (AggregateTransaction) transactions.get(0);

                                          CosignatureTransaction cosignatureTransaction =
                                              CosignatureTransaction.create(transactionToCosign);
                                          CosignatureSignedTransaction
                                              cosignatureSignedTransaction =
                                                  cosignAccount2.signCosignatureTransaction(
                                                      cosignatureTransaction);
                                          return transactionRepository
                                              .announceAggregateBondedCosignature(
                                                  cosignatureSignedTransaction)
                                              .flatMap(
                                                  r -> {
                                                    System.out.println(
                                                        "announceAggregateBondedCosignature " + r);
                                                    return listener.cosignatureAdded(
                                                        cosignAccount1.getAddress());
                                                  });
                                        });
                              });
                    }));
    System.out.println(finalObject.getClass());
    Assertions.assertNull(finalObject);
  }

  private SignedTransaction createSignedAggregatedBondTransaction(
      Account multisigAccount, Account cosignAccount, Address recipient) {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                recipient,
                Collections.emptyList(),
                PlainMessage.create("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Collections.singletonList(
                    transferTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();
    return cosignAccount.sign(aggregateTransaction, getGenerationHash());
  }

  private Observable<Transaction> createHashLockTransactionAndAnnounce(
      RepositoryType type, SignedTransaction signedAggregatedTransaction, Account signer) {

    HashLockTransaction hasLockTransaction =
        HashLockTransactionFactory.create(
                getNetworkType(),
                getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(10000),
                signedAggregatedTransaction)
            .maxFee(maxFee)
            .build();

    SignedTransaction signedLockFundsTransaction =
        signer.sign(hasLockTransaction, getGenerationHash());
    TransactionServiceImpl transactionService =
        new TransactionServiceImpl(getRepositoryFactory(type));
    return transactionService.announce(getListener(type), signedLockFundsTransaction);
  }
}
