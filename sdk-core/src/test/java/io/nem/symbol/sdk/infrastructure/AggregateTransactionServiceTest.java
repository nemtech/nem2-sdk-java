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
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.network.AggregateNetworkProperties;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.network.PluginsProperties;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Test of {@link AggregateTransactionServiceImpl} */
public class AggregateTransactionServiceTest {

  private final NetworkType networkType = NetworkType.MIJIN_TEST;
  private final Account account1 = Account.generateNewAccount(networkType);
  private final Account account2 = Account.generateNewAccount(networkType);
  private final Account account3 = Account.generateNewAccount(networkType);
  private final Account account4 = Account.generateNewAccount(networkType);
  private final Account multisig1 = Account.generateNewAccount(networkType);
  private final Account multisig2 = Account.generateNewAccount(networkType);
  private final Account multisig3 = Account.generateNewAccount(networkType);
  private final String generationHash =
      "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
  private AggregateTransactionServiceImpl service;
  private RepositoryFactory factory;
  private MultisigRepository multisigRepository;
  private NetworkRepository networkRepository;

  @BeforeEach
  void setup() {
    factory = Mockito.mock(RepositoryFactory.class);
    multisigRepository = Mockito.mock(MultisigRepository.class);
    networkRepository = Mockito.mock(NetworkRepository.class);

    Mockito.when(factory.createMultisigRepository()).thenReturn(multisigRepository);
    Mockito.when(factory.createNetworkRepository()).thenReturn(networkRepository);

    service = new AggregateTransactionServiceImpl(factory);

    Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account1.getAddress())))
        .thenReturn(Observable.just(givenAccount1Info()));
    Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account4.getAddress())))
        .thenReturn(Observable.just(givenAccount4Info()));
    Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(multisig2.getAddress())))
        .thenReturn(Observable.just(givenMultisig2AccountInfo()));
    Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(multisig3.getAddress())))
        .thenReturn(Observable.just(givenMultisig3AccountInfo()));
    Mockito.when(multisigRepository.getMultisigAccountGraphInfo(Mockito.eq(multisig2.getAddress())))
        .thenReturn(Observable.just(givenMultisig2AccountGraphInfo()));
    Mockito.when(multisigRepository.getMultisigAccountGraphInfo(Mockito.eq(multisig3.getAddress())))
        .thenReturn(Observable.just(givenMultisig3AccountGraphInfo()));
    Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account2.getAddress())))
        .thenReturn(Observable.just(givenAccount2Info()));
    Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account3.getAddress())))
        .thenReturn(Observable.just(givenAccount3Info()));
  }

  public MultisigAccountInfo givenMultisig2AccountInfo() {
    return new MultisigAccountInfo(
        multisig2.getAddress(),
        2,
        1,
        Arrays.asList(multisig1.getAddress(), account1.getAddress()),
        Collections.emptyList());
  }

  MultisigAccountInfo givenMultisig3AccountInfo() {
    return new MultisigAccountInfo(
        multisig3.getAddress(),
        2,
        2,
        Arrays.asList(account2.getAddress(), account3.getAddress()),
        Collections.emptyList());
  }

  MultisigAccountInfo givenAccount1Info() {
    return new MultisigAccountInfo(
        account1.getAddress(),
        0,
        0,
        Collections.emptyList(),
        Collections.singletonList(multisig2.getAddress()));
  }

  MultisigAccountInfo givenAccount2Info() {
    return new MultisigAccountInfo(
        account2.getAddress(),
        0,
        0,
        Collections.emptyList(),
        Arrays.asList(multisig2.getAddress(), multisig3.getAddress()));
  }

  MultisigAccountInfo givenAccount3Info() {
    return new MultisigAccountInfo(
        account3.getAddress(),
        0,
        0,
        Collections.emptyList(),
        Arrays.asList(multisig2.getAddress(), multisig3.getAddress()));
  }

  MultisigAccountInfo givenAccount4Info() {
    return new MultisigAccountInfo(
        account4.getAddress(), 0, 0, Collections.emptyList(), Collections.emptyList());
  }

  MultisigAccountGraphInfo givenMultisig2AccountGraphInfo() {
    Map<Integer, List<MultisigAccountInfo>> map = new HashMap<>();
    map.put(
        1,
        Collections.singletonList(
            new MultisigAccountInfo(
                multisig2.getAddress(),
                0,
                1,
                Arrays.asList(multisig1.getAddress(), account1.getAddress()),
                Collections.emptyList())));

    map.put(
        2,
        Collections.singletonList(
            new MultisigAccountInfo(
                multisig1.getAddress(),
                1,
                1,
                Arrays.asList(account2.getAddress(), account3.getAddress()),
                Collections.singletonList(multisig2.getAddress()))));

    return new MultisigAccountGraphInfo(map);
  }

  MultisigAccountGraphInfo givenMultisig3AccountGraphInfo() {
    Map<Integer, List<MultisigAccountInfo>> map = new HashMap<>();
    map.put(
        0,
        Collections.singletonList(
            new MultisigAccountInfo(
                multisig3.getAddress(),
                2,
                2,
                Arrays.asList(account2.getAddress(), account3.getAddress()),
                Collections.emptyList())));

    return new MultisigAccountGraphInfo(map);
  }

  @Test
  void getMaxCosignatures() throws ExecutionException, InterruptedException {
    Map<Integer, List<MultisigAccountInfo>> infoMap = new HashMap<>();
    MultisigAccountInfo multisigAccountInfo1 =
        new MultisigAccountInfo(
            multisig1.getAddress(),
            1,
            1,
            Arrays.asList(account1.getAddress(), account2.getAddress(), account3.getAddress()),
            Collections.emptyList());
    infoMap.put(-3, Collections.singletonList(multisigAccountInfo1));

    MultisigAccountInfo multisigAccountInfo2 =
        new MultisigAccountInfo(
            multisig2.getAddress(),
            1,
            1,
            Arrays.asList(account4.getAddress(), account2.getAddress(), account3.getAddress()),
            Collections.emptyList());
    infoMap.put(-2, Collections.singletonList(multisigAccountInfo2));

    MultisigAccountGraphInfo multisigAccountGraphInfo = new MultisigAccountGraphInfo(infoMap);

    Mockito.when(multisigRepository.getMultisigAccountGraphInfo(Mockito.eq(account1.getAddress())))
        .thenReturn(Observable.just(multisigAccountGraphInfo));

    Integer maxConsignatures = service.getMaxCosignatures(account1.getAddress()).toFuture().get();
    Assertions.assertEquals(4, maxConsignatures);
  }

  @Test
  void getNetworkMaxCosignaturesPerAggregateWhenValid()
      throws ExecutionException, InterruptedException {

    NetworkConfiguration configuration = Mockito.mock(NetworkConfiguration.class);
    PluginsProperties pluginsProperties = Mockito.mock(PluginsProperties.class);
    AggregateNetworkProperties aggregateNetworkProperties =
        Mockito.mock(AggregateNetworkProperties.class);

    Mockito.when(aggregateNetworkProperties.getMaxCosignaturesPerAggregate()).thenReturn("25");
    Mockito.when(pluginsProperties.getAggregate()).thenReturn(aggregateNetworkProperties);
    Mockito.when(configuration.getPlugins()).thenReturn(pluginsProperties);

    Mockito.when(networkRepository.getNetworkProperties())
        .thenReturn(Observable.just(configuration));

    Integer maxCosignaturesPerAggregate =
        service.getNetworkMaxCosignaturesPerAggregate().toFuture().get();

    Assertions.assertEquals(25, maxCosignaturesPerAggregate);
  }

  @Test
  void getNetworkMaxCosignaturesPerAggregateWhenValidInvalid() {

    NetworkConfiguration configuration = Mockito.mock(NetworkConfiguration.class);
    PluginsProperties pluginsProperties = Mockito.mock(PluginsProperties.class);
    AggregateNetworkProperties aggregateNetworkProperties =
        Mockito.mock(AggregateNetworkProperties.class);

    Mockito.when(pluginsProperties.getAggregate()).thenReturn(aggregateNetworkProperties);
    Mockito.when(configuration.getPlugins()).thenReturn(pluginsProperties);

    Mockito.when(networkRepository.getNetworkProperties())
        .thenReturn(Observable.just(configuration));

    IllegalStateException exception =
        Assertions.assertThrows(
            IllegalStateException.class,
            () ->
                ExceptionUtils.propagate(
                    () -> service.getNetworkMaxCosignaturesPerAggregate().toFuture().get()));

    Assertions.assertEquals(
        "Cannot get maxCosignaturesPerAggregate from network properties.", exception.getMessage());
  }

  /*
   * MLMA Alice (account1): normal account Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
   * Charles (multisig1) - Multisig 1-1 (account2 || account3) Given signatories: Account1 &&
   * Account4 Expecting complete as Bob needs 2 signatures (account1 && (account2 || account3))
   */

  @Test
  void shouldReturnIsCompleteTrueForAggregatedCompleteTransaction2LevelsMultisig()
      throws ExecutionException, InterruptedException {

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, Address.generateRandom(networkType), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    transferTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Collections.singletonList(account2), generationHash);

    Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
  }

  /*
   * MLMA Alice (account1): normal account Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
   * Charles (multisig1) - Multisig 1-1 (account2 || account3) Given signatories: Account1 &&
   * Account4 Expecting incomplete as Bob needs 2 signatures (account1 && (account2 || account3))
   * but only got account1
   */
  @Test
  void shouldReturnIsCompleteFalseForAggregatedCompleteTransaction2LevelsMultisig()
      throws ExecutionException, InterruptedException {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, Address.generateRandom(networkType), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    transferTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Collections.emptyList(), generationHash);
    Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());

    AggregateTransaction deserialize =
        (AggregateTransaction)
            BinarySerializationImpl.INSTANCE.deserialize(
                ConvertUtils.fromHexToBytes(signedTransaction.getPayload()));

    Assertions.assertEquals(account1.getAddress(), deserialize.getSigner().get().getAddress());
    Assertions.assertEquals(0, deserialize.getCosignatures().size());
    Assertions.assertEquals(
        multisig2.getPublicAccount(), deserialize.getInnerTransactions().get(0).getSigner().get());

    Assertions.assertEquals(
        deserialize.getSize(), ConvertUtils.fromHexToBytes(signedTransaction.getPayload()).length);
  }

  /*
   * MLMA Alice (account1): normal account Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
   * Charles (multisig1) - Multisig 1-1 (account2 || account3) Given signatories: Account1 &&
   * Account4 Expecting incomplete as Bob needs 2 signatures (account1 && (account2 || account3))
   * but got account4
   */
  @Test
  void shouldReturnIsCompleteFalseForAggregatedCompleteTransaction2LevelsMultisig2()
      throws ExecutionException, InterruptedException {

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, Address.generateRandom(networkType), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    transferTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction1 =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Collections.singletonList(account4), generationHash);

    SignedTransaction signedTransaction2 =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Collections.singletonList(account4), generationHash);

    Assertions.assertEquals(signedTransaction1.getHash(), signedTransaction2.getHash());
    Assertions.assertEquals(signedTransaction1.getPayload(), signedTransaction2.getPayload());

    Assertions.assertFalse(service.isComplete(signedTransaction1).toFuture().get());

    AggregateTransaction deserialize =
        (AggregateTransaction)
            BinarySerializationImpl.INSTANCE.deserialize(
                ConvertUtils.fromHexToBytes(signedTransaction2.getPayload()));

    Assertions.assertEquals(account1.getAddress(), deserialize.getSigner().get().getAddress());
    Assertions.assertEquals(1, deserialize.getCosignatures().size());
    Assertions.assertEquals(
        account4.getPublicAccount(), deserialize.getCosignatures().get(0).getSigner());
    Assertions.assertEquals(
        multisig2.getPublicAccount(), deserialize.getInnerTransactions().get(0).getSigner().get());
    Assertions.assertEquals(
        deserialize.getSize(), ConvertUtils.fromHexToBytes(signedTransaction1.getPayload()).length);
  }

  /*
   * MLMA - with multiple transaction Alice (account1): normal account Bob (multisig2) - Multisig
   * 2-1 (account1 && multisig1) Charles (multisig1) - Multisig 1-1 (account2 || account3) An
   * extra inner transaction to account4 (just to increase the complexity) Given signatories:
   * Account1 && Account4 Expecting incomplete as Bob needs 2 signatures (account1 && (account2 ||
   * account3))
   */
  @Test
  void
      shouldReturnCorrectIsCompleteStatusForAggregatedCompleteTransaction2LevelsMultisigMultiinnerTransaction()
          throws ExecutionException, InterruptedException {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, Address.generateRandom(networkType), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    TransferTransaction transferTransaction2 =
        TransferTransactionFactory.create(
                networkType, account2.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Arrays.asList(
                    transferTransaction.toAggregate(multisig2.getPublicAccount()),
                    transferTransaction2.toAggregate(account4.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Collections.singletonList(account4), generationHash);
    Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
  }

  /*
   * MLMA - with multiple transaction Alice (account1): normal account Bob (multisig2) - Multisig
   * 2-1 (account1 && multisig1) Charles (multisig1) - Multisig 1-1 (account2 || account3) An
   * extra inner transaction to account4 (just to increase the complexity) Given signatories:
   * Account1 && Account4 && Account2 Expecting complete
   */
  @Test
  void
      shouldReturnCorrectIsCompleteStatusForAggregatedCompleteTransaction2LevelsMultisigMmultiInnerTransaction()
          throws ExecutionException, InterruptedException {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, account2.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    TransferTransaction transferTransaction2 =
        TransferTransactionFactory.create(
                networkType, account2.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Arrays.asList(
                    transferTransaction.toAggregate(multisig2.getPublicAccount()),
                    transferTransaction2.toAggregate(account4.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Arrays.asList(account4, account2), generationHash);
    Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
  }

  /*
   * If the inner transaction is issued to a multisig account and the inner transaction itself is
   * a ModifyMultiSigAccountTransaction - Removal The validator should use minRemoval value rather
   * than minApproval value to determine if the act is complete or not
   */
  @Test
  void shouldUseMinRemovalForMultisigAccountValidationIfInnerTransactionIsModifyMultisigRemove()
      throws ExecutionException, InterruptedException {
    MultisigAccountModificationTransaction modifyMultisigTransaction =
        MultisigAccountModificationTransactionFactory.create(
                networkType,
                (byte) 1,
                (byte) 1,
                Collections.emptyList(),
                Collections.singletonList(account1.getAddress()))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    modifyMultisigTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction = aggregateTransaction.signWith(account2, generationHash);
    Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
  }

  /*
   * If the inner transaction is issued to a multisig account and the inner transaction itself is
   * a ModifyMultiSigAccountTransaction - Removal The validator should use minRemoval value rather
   * than minApproval value to determine if the act is complete or not
   */
  @Test
  void shouldReturnCorrectIsCompleteStatusFalseForAggregatedCompleteTransactionNoneMultisig()
      throws ExecutionException, InterruptedException {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, account2.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    transferTransaction.toAggregate(account4.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction = aggregateTransaction.signWith(account1, generationHash);
    Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
  }

  /*
   * ACT Alice (account1): normal account Bob (account4) - normal account Alice initiate the
   * transaction Bob sign
   */
  @Test
  void shouldReturnCorrectIsCompleteStatusTrueForAggregatedCompleteTransactionNoneMultisig()
      throws ExecutionException, InterruptedException {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, account2.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    transferTransaction.toAggregate(account4.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction = aggregateTransaction.signWith(account4, generationHash);

    Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
  }

  /*
   * ACT Alice: account1 Bob: account4 An escrow contract is signed by all the participants
   * (normal accounts) Given Alice defined the following escrow contract: | sender | recipient |
   * type | data | | Alice | Bob | send-an-asset | 1 concert.ticket | | Bob | Alice |
   * send-an-asset | 20 euros | And Bob signs the contract And Alice signs the contract Then the
   * contract should appear as complete
   */
  @Test
  void shouldReturnCorrectIsCompleteStatuTrueMultipleNormalAccount()
      throws ExecutionException, InterruptedException {

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, account1.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    TransferTransaction transferTransaction2 =
        TransferTransactionFactory.create(
                networkType, account4.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Arrays.asList(
                    transferTransaction.toAggregate(account4.getPublicAccount()),
                    transferTransaction2.toAggregate(account1.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction1 = aggregateTransaction.signWith(account1, generationHash);

    Assertions.assertFalse(service.isComplete(signedTransaction1).toFuture().get());

    SignedTransaction signedTransaction2 =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Collections.singletonList(account4), generationHash);

    Assertions.assertTrue(service.isComplete(signedTransaction2).toFuture().get());
  }

  /*
   * ACT Alice: account1 Bob: account4 An escrow contract is signed by all the participants
   * (normal accounts) Given Alice defined the following escrow contract: | sender | recipient |
   * type | data | | Alice | Bob | send-an-asset | 1 concert.ticket | | Bob | Alice |
   * send-an-asset | 20 euros | And Alice signs the contract Then the contract should appear as
   * incomplete
   */
  @Test
  void shouldReturnCorrectIsCompleteStatusFalseMultipleNormalAccount()
      throws ExecutionException, InterruptedException {

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, account1.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    TransferTransaction transferTransaction2 =
        TransferTransactionFactory.create(
                networkType, account4.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Arrays.asList(
                    transferTransaction.toAggregate(account4.getPublicAccount()),
                    transferTransaction2.toAggregate(account1.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction =
        account1.signTransactionWithCosignatories(
            aggregateTransaction, Collections.emptyList(), generationHash);

    Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
  }

  /*
   * ACT - Multisig single level Alice (account1): initiate an transfer to Bob Bob (multisig3): is
   * a 2/2 multisig account (account2 && account3)
   */
  @Test
  void shouldReturnCorrectIsCompleteStatusTRUEMultisigSingleLevel()
      throws ExecutionException, InterruptedException {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, account4.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    transferTransaction.toAggregate(multisig3.getPublicAccount())))
            .build();

    SignedTransaction signedTransactionNoCosignatures =
        account2.sign(aggregateTransaction, generationHash);

    SignedTransaction signedTransaction1 =
        account2.signTransactionGivenSignatures(
            aggregateTransaction,
            Collections.singletonList(
                account3.signCosignatureTransaction(signedTransactionNoCosignatures.getHash())),
            generationHash);

    SignedTransaction signedTransaction2 =
        account2.signTransactionWithCosignatories(
            aggregateTransaction, Collections.singletonList(account3), generationHash);

    Assertions.assertEquals(signedTransaction1.getPayload(), signedTransaction2.getPayload());
    Assertions.assertEquals(signedTransaction1.getHash(), signedTransaction2.getHash());
    Assertions.assertEquals(signedTransaction1.getSigner(), signedTransaction2.getSigner());
    Assertions.assertEquals(signedTransaction1.getType(), signedTransaction2.getType());

    Assertions.assertTrue(service.isComplete(signedTransaction1).toFuture().get());
    Assertions.assertTrue(service.isComplete(signedTransaction2).toFuture().get());
  }

  /*
   * ACT - Multisig single level Alice (account1): initiate an transfer to Bob Bob (multisig3): is
   * a 2/2 multisig account (account2 && account3)
   */
  @Test
  void shouldReturnCorrectIsCompleteStatusFALSEMultisigSingleLevel()
      throws ExecutionException, InterruptedException {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                networkType, account4.getAddress(), Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                networkType,
                Collections.singletonList(
                    transferTransaction.toAggregate(multisig3.getPublicAccount())))
            .build();

    SignedTransaction signedTransaction =
        account2.signTransactionWithCosignatories(
            aggregateTransaction, Collections.emptyList(), generationHash);

    Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
  }
}
