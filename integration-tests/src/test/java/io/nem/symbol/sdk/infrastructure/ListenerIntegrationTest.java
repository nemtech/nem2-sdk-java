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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ListenerIntegrationTest extends BaseIntegrationTest {

  private Account account;
  private Account multisigAccount;
  private Account cosignatoryAccount;
  private Account cosignatoryAccount2;

  @BeforeEach
  void setup() {
    account = config().getDefaultAccount();
    multisigAccount = config().getMultisigAccount();
    cosignatoryAccount = config().getCosignatoryAccount();
    cosignatoryAccount2 = config().getCosignatory2Account();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldConnectToWebSocket(RepositoryType type)
      throws ExecutionException, InterruptedException {
    Listener listener = getRepositoryFactory(type).createListener();
    CompletableFuture<Void> connected = listener.open();
    connected.get();
    assertTrue(connected.isDone());
    assertNotNull(listener.getUid());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnNewBlockViaListener(RepositoryType type) {
    Listener listener = getListener(type);
    BlockInfo blockInfo = get(listener.newBlock().take(1));
    assertTrue(blockInfo.getHeight().intValue() > 0);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  @Disabled
  void shouldReturnFinalizedBlock(RepositoryType type) {
    Listener listener = getListener(type);
    FinalizedBlock finalizedBlock1 = get(listener.finalizedBlock().take(1));
    FinalizedBlock finalizedBlock2 =
        get(getRepositoryFactory(type).createChainRepository().getChainInfo())
            .getLatestFinalizedBlock();
    assertTrue(finalizedBlock1.getHeight().intValue() > 0);
    assertEquals(finalizedBlock1, finalizedBlock2);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnConfirmedTransactionAddressSignerViaListener(RepositoryType type) {
    Listener listener = getListener(type);

    SignedTransaction signedTransaction =
        this.announceStandaloneTransferTransaction(type, this.getRecipient());

    Observable<Transaction> confirmed =
        listener.confirmed(this.account.getAddress(), signedTransaction.getHash());

    Transaction transaction = get(confirmed.take(1));

    assertEquals(
        signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnConfirmedTransactionAddressRecipientViaListener(RepositoryType type) {
    Listener listener = getListener(type);

    SignedTransaction signedTransaction =
        this.announceStandaloneTransferTransaction(type, this.getRecipient());

    Observable<Transaction> confirmed =
        listener.confirmed(this.getRecipient(), signedTransaction.getHash());
    Transaction transaction = get(confirmed.take(1));

    assertEquals(
        signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnUnconfirmedAddedTransactionViaListener(RepositoryType type) {
    Listener listener = getListener(type);

    SignedTransaction signedTransaction =
        this.announceStandaloneTransferTransaction(type, this.getRecipient());
    Transaction transaction =
        get(
            listener
                .unconfirmedAdded(this.account.getAddress(), signedTransaction.getHash())
                .take(1));
    assertEquals(
        signedTransaction.getHash(), transaction.getTransactionInfo().get().getHash().get());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnUnconfirmedRemovedTransactionViaListener(RepositoryType type) {
    Listener listener = getListener(type);

    SignedTransaction signedTransaction =
        this.announceStandaloneTransferTransaction(type, this.getRecipient());

    String transactionHash = get(listener.unconfirmedRemoved(this.account.getAddress()).take(1));
    assertEquals(signedTransaction.getHash(), transactionHash);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnAggregateBondedAddedTransactionViaListener(RepositoryType type) {
    Listener listener = getListener(type);
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    TransactionService transactionService = new TransactionServiceImpl(repositoryFactory);

    Pair<SignedTransaction, SignedTransaction> pair = this.createAggregateBondedTransaction(type);

    System.out.println("Announcing HashLock transaction " + pair.getRight().getHash());
    get(transactionService.announce(listener, pair.getRight()));

    SignedTransaction signedTransaction = pair.getLeft();

    System.out.println("Announcing Aggregate transaction " + signedTransaction.getHash());
    AggregateTransaction aggregateTransaction =
        get(transactionService.announceAggregateBonded(listener, signedTransaction).take(1));

    assertEquals(
        signedTransaction.getHash(),
        aggregateTransaction.getTransactionInfo().get().getHash().get());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  @Disabled
  void shouldReturnAggregateBondedRemovedTransactionViaListener(RepositoryType type) {
    Listener listener = getListener(type);

    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    TransactionService transactionService = new TransactionServiceImpl(repositoryFactory);

    Pair<SignedTransaction, SignedTransaction> pair = this.createAggregateBondedTransaction(type);

    System.out.println("Announcing HashLock transaction " + pair.getRight().getHash());
    get(transactionService.announce(listener, pair.getRight()));

    SignedTransaction signedTransaction = pair.getLeft();

    System.out.println("Announcing Aggregate transaction " + signedTransaction.getHash());
    get(transactionService.announceAggregateBonded(listener, signedTransaction).take(1));

    String transactionHash =
        get(listener.aggregateBondedRemoved(this.account.getAddress()).take(1));
    assertEquals(signedTransaction.getHash(), transactionHash);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  @Disabled
  void shouldReturnCosignatureAddedViaListener(RepositoryType type) {
    Listener listener = getListener(type);

    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    TransactionService transactionService = new TransactionServiceImpl(repositoryFactory);

    Pair<SignedTransaction, SignedTransaction> pair = this.createAggregateBondedTransaction(type);

    System.out.println("Announcing HashLock transaction " + pair.getRight().getHash());
    get(transactionService.announce(listener, pair.getRight()));

    SignedTransaction signedTransaction = pair.getLeft();

    AggregateTransaction announcedTransaction =
        get(transactionService.announceAggregateBonded(listener, signedTransaction));

    assertEquals(
        signedTransaction.getHash(),
        announcedTransaction.getTransactionInfo().get().getHash().get());

    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();

    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .transactionTypes(Collections.singletonList(TransactionType.AGGREGATE_BONDED))
                    .signerPublicKey(this.cosignatoryAccount.getPublicAccount().getPublicKey())))
            .getData();

    AggregateTransaction transactionToCosign = (AggregateTransaction) transactions.get(0);

    this.announceCosignatureTransaction(transactionToCosign, type);

    CosignatureSignedTransaction cosignatureSignedTransaction =
        get(listener.cosignatureAdded(this.cosignatoryAccount.getAddress()).take(1));

    assertEquals(
        cosignatureSignedTransaction.getSigner(), this.cosignatoryAccount2.getPublicAccount());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void shouldReturnTransactionStatusGivenAddedViaListener(RepositoryType type) {
    Listener listener = getListener(type);

    SignedTransaction signedTransaction =
        this.announceStandaloneTransferTransactionWithInsufficientBalance(type);

    TransactionStatusError transactionHash =
        get(listener.status(this.account.getAddress(), signedTransaction.getHash()).take(1));
    assertEquals(signedTransaction.getHash(), transactionHash.getHash());
  }

  private SignedTransaction announceStandaloneTransferTransaction(
      RepositoryType type, Address recipient) {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipient,
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(10000L))))
            .message(new PlainMessage("test-message"))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedTransaction =
        this.account.sign(transferTransaction, getGenerationHash());
    System.out.println("Announcing transaction " + signedTransaction.getHash());
    get(getTransactionRepository(type).announce(signedTransaction));
    System.out.println("Transaction " + signedTransaction.getHash() + " Announced");
    return signedTransaction;
  }

  private TransactionRepository getTransactionRepository(RepositoryType type) {
    return getRepositoryFactory(type).createTransactionRepository();
  }

  private SignedTransaction announceStandaloneTransferTransactionWithInsufficientBalance(
      RepositoryType type) {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                Address.generateRandom(getNetworkType()),
                Collections.singletonList(
                    getNetworkCurrency().createRelative(new BigInteger("100000000000"))))
            .message(new PlainMessage("test-message"))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedTransaction =
        this.account.sign(transferTransaction, getGenerationHash());
    get(getTransactionRepository(type).announce(signedTransaction));
    return signedTransaction;
  }

  private Pair<SignedTransaction, SignedTransaction> createAggregateBondedTransaction(
      RepositoryType type) {

    helper().sendMosaicFromNemesis(type, this.cosignatoryAccount.getAddress(), false);
    helper().sendMosaicFromNemesis(type, this.cosignatoryAccount2.getAddress(), false);
    helper().sendMosaicFromNemesis(type, this.multisigAccount.getAddress(), false);
    helper()
        .createMultisigAccountBonded(
            type, this.multisigAccount, this.cosignatoryAccount, this.cosignatoryAccount2);

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                Account.generateNewAccount(getNetworkType()).getAddress(),
                Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createBonded(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    transferTransaction.toAggregate(this.multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction aggregateSignedTransaction =
        this.cosignatoryAccount.sign(aggregateTransaction, getGenerationHash());

    HashLockTransaction hashLockTransaction =
        HashLockTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                aggregateSignedTransaction)
            .maxFee(maxFee)
            .build();

    SignedTransaction signedHashLockTransaction =
        hashLockTransaction.signWith(this.cosignatoryAccount, getGenerationHash());

    return Pair.of(aggregateSignedTransaction, signedHashLockTransaction);
  }

  private CosignatureSignedTransaction announceCosignatureTransaction(
      AggregateTransaction transactionToCosign, RepositoryType type) {
    CosignatureTransaction cosignatureTransaction = new CosignatureTransaction(transactionToCosign);

    CosignatureSignedTransaction cosignatureSignedTransaction =
        this.cosignatoryAccount2.signCosignatureTransaction(cosignatureTransaction);

    get(
        getRepositoryFactory(type)
            .createTransactionRepository()
            .announceAggregateBondedCosignature(cosignatureSignedTransaction));

    return cosignatureSignedTransaction;
  }
}
