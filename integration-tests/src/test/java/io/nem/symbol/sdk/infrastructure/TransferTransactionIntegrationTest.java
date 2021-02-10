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

import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.message.EncryptedMessage;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.message.MessageType;
import io.nem.symbol.sdk.model.message.PersistentHarvestingDelegationMessage;
import io.nem.symbol.sdk.model.message.PersistentHarvestingDelegationMessage.HarvestingKeys;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferTransactionIntegrationTest extends BaseIntegrationTest {

  private Account signerAccount;
  private Pair<Account, NamespaceId> recipient;
  private Address recipientAddress;
  private NamespaceId recipientAlias;

  @BeforeEach
  void setup() {
    signerAccount = config().getDefaultAccount();
    recipient = helper().getTestAccount(RepositoryType.VERTX);
    recipientAddress = recipient.getLeft().getAddress();
    recipientAlias = recipient.getRight();
    Assertions.assertNotEquals(recipient.getKey().getAddress(), signerAccount.getAddress());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAliasSwapped(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAlias.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAlias, "", 100, expected, true, recipientAddress);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAddressSwapped(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAddress.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAddress, "", 100, expected, true, recipientAlias);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAliasSame(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAlias.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAlias, "", 100, expected, true, recipientAlias);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAddressSame(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAddress.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAddress, "", 100, expected, true, recipientAddress);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAliasAllSubscriptions(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAlias.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAlias.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAlias, "", 100, expected, true, recipientAlias, recipientAddress);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAddressAllSubscription(RepositoryType type) throws Exception {

    final List<String> expected =
        Arrays.asList(
            recipientAddress.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);

    basicTransfer(
        type, recipientAddress, "", 100, expected, true, recipientAlias, recipientAddress);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAliasSwappedNoAlias(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAlias, "", 100, expected, false, recipientAddress);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAddressSwappedNoAlias(RepositoryType type) throws Exception {
    final List<String> expected = Arrays.asList();
    basicTransfer(type, recipientAddress, "", 100, expected, false, recipientAlias);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAliasSameNoAlias(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAlias.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAlias.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAlias, "", 100, expected, false, recipientAlias);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAddressSameNoAlias(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAddress.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAddress, "", 100, expected, false, recipientAddress);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAliasAllSubscriptionsNoAlias(RepositoryType type) throws Exception {
    final List<String> expected =
        Arrays.asList(
            recipientAlias.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);
    basicTransfer(type, recipientAlias, "", 100, expected, false, recipientAlias, recipientAddress);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void transferTransactionToAddressAllSubscriptionNoAlias(RepositoryType type) throws Exception {

    final List<String> expected =
        Arrays.asList(
            recipientAddress.plain() + " " + ListenerChannel.UNCONFIRMED_ADDED,
            recipientAddress.plain() + " " + ListenerChannel.CONFIRMED_ADDED);

    basicTransfer(
        type, recipientAddress, "", 100, expected, false, recipientAlias, recipientAddress);
  }

  private List<String> basicTransfer(
      RepositoryType type,
      UnresolvedAddress recipient,
      String s,
      int i,
      List<String> expected,
      boolean includeAliases,
      UnresolvedAddress... listenTo)
      throws InterruptedException, ExecutionException {
    List<String> messages = new ArrayList<>();
    Listener listener = listen(type, messages, includeAliases, listenTo);
    sleep(1000);
    try {
      String message = s;
      Currency networkCurrency = getNetworkCurrency();
      Mosaic mosaic = new Mosaic(networkCurrency.getNamespaceId().get(), BigInteger.valueOf(i));
      TransferTransaction transferTransaction =
          TransferTransactionFactory.create(
                  getNetworkType(), getDeadline(), recipient, Collections.singletonList(mosaic))
              .message(new PlainMessage(message))
              .maxFee(maxFee)
              .build();

      TransferTransaction processed =
          announceAggregateAndValidate(type, transferTransaction, signerAccount).getKey();
      Assertions.assertEquals(message, processed.getMessage().get().getText());

      sleep(1000);
      Assertions.assertEquals(expected, messages);
      return messages;
    } finally {
      listener.close();
    }
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void standaloneTransferTransactionEncryptedMessage(RepositoryType type) throws Exception {
    this.helper().sendMosaicFromNemesis(type, getRecipient(), false);
    String namespaceName = "standaloneTransferTransactionEncryptedMessagealias".toLowerCase();

    NamespaceId recipient = setAddressAlias(type, getRecipient(), namespaceName);

    System.out.println(recipient.getIdAsHex());
    Assertions.assertEquals(
        "9960629109A48AFBC0000000000000000000000000000000", recipient.encoded(getNetworkType()));
    String message = "E2ETest:standaloneTransferTransaction:message 漢字";

    KeyPair senderKeyPair = KeyPair.random();
    KeyPair recipientKeyPair = KeyPair.random();

    Message encryptedMessage =
        EncryptedMessage.create(
            message, senderKeyPair.getPrivateKey(), recipientKeyPair.getPublicKey());
    Currency networkCurrency = getNetworkCurrency();
    Mosaic mosaic =
        new Mosaic(networkCurrency.getNamespaceId().get(), BigInteger.valueOf(10202020));
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(), getDeadline(), recipient, Collections.singletonList(mosaic))
            .message(encryptedMessage)
            .maxFee(maxFee)
            .build();

    TransferTransaction processed = announceAndValidate(type, signerAccount, transferTransaction);

    assertTransferTransactions(transferTransaction, processed);

    assertEncryptedMessageTransaction(message, senderKeyPair, recipientKeyPair, processed);

    TransferTransaction restTransaction =
        (TransferTransaction)
            get(
                getRepositoryFactory(type)
                    .createTransactionRepository()
                    .getTransaction(
                        TransactionGroup.CONFIRMED,
                        processed.getTransactionInfo().get().getHash().get()));

    assertTransferTransactions(transferTransaction, restTransaction);

    assertEncryptedMessageTransaction(message, senderKeyPair, recipientKeyPair, restTransaction);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void transferTransactionNotEnoughFundAccount(RepositoryType type) {

    Address recipient = config().getTestAccount2().getAddress();

    NetworkType networkType = getNetworkType();

    Account account = Account.generateNewAccount(networkType);

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipient,
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(1000000000))))
            .message(new PlainMessage(""))
            .maxFee(maxFee)
            .build();

    IllegalArgumentException exceptions =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> announceAndValidate(type, account, transferTransaction));

    Assertions.assertTrue(exceptions.getMessage().contains("Failure_Core_Insufficient_Balance"));
  }

  private void assertTransferTransactions(
      TransferTransaction expected, TransferTransaction processed) {
    Assertions.assertEquals(
        expected.getRecipient().encoded(getNetworkType()),
        processed.getRecipient().encoded(getNetworkType()));
    Assertions.assertEquals(expected.getRecipient(), processed.getRecipient());
    Assertions.assertEquals(expected.getMessage(), processed.getMessage());
  }

  private void assertEncryptedMessageTransaction(
      String message,
      KeyPair senderKeyPair,
      KeyPair recipientKeyPair,
      TransferTransaction transaction) {
    Assertions.assertTrue(transaction.getMessage().get() instanceof EncryptedMessage);
    Assertions.assertNotEquals(message, transaction.getMessage().get().getText());
    String decryptedMessage =
        ((EncryptedMessage) transaction.getMessage().get())
            .decryptPayload(senderKeyPair.getPublicKey(), recipientKeyPair.getPrivateKey());
    Assertions.assertNotNull(message, decryptedMessage);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void standaloneCreatePersistentDelegationRequestTransaction(RepositoryType type) {

    KeyPair signingKeyPair = KeyPair.random();
    KeyPair vrfPrivateKey = KeyPair.random();
    KeyPair recipientKeyPair = KeyPair.random();

    TransferTransaction transferTransaction =
        TransferTransactionFactory.createPersistentDelegationRequestTransaction(
                getNetworkType(),
                getDeadline(),
                signingKeyPair.getPrivateKey(),
                vrfPrivateKey.getPrivateKey(),
                recipientKeyPair.getPublicKey())
            .maxFee(maxFee)
            .build();

    TransferTransaction processed = announceAndValidate(type, signerAccount, transferTransaction);

    assertPersistentDelegationTransaction(
        recipientKeyPair, signingKeyPair, vrfPrivateKey, processed);

    TransferTransaction restTransaction =
        (TransferTransaction)
            get(
                getRepositoryFactory(type)
                    .createTransactionRepository()
                    .getTransaction(
                        TransactionGroup.CONFIRMED,
                        processed.getTransactionInfo().get().getHash().get()));

    assertPersistentDelegationTransaction(
        recipientKeyPair, signingKeyPair, vrfPrivateKey, restTransaction);
  }

  private void assertPersistentDelegationTransaction(
      KeyPair recipientKeyPair,
      KeyPair signingKeyPair,
      KeyPair vrfPrivateKey,
      TransferTransaction transaction) {
    Assertions.assertTrue(
        transaction.getMessage().get() instanceof PersistentHarvestingDelegationMessage);
    Assertions.assertEquals(
        MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
        transaction.getMessage().get().getType());
    HarvestingKeys decryptedMessage =
        ((PersistentHarvestingDelegationMessage) transaction.getMessage().get())
            .decryptPayload(recipientKeyPair.getPrivateKey());
    Assertions.assertEquals(vrfPrivateKey.getPrivateKey(), decryptedMessage.getVrfPrivateKey());
    Assertions.assertEquals(
        signingKeyPair.getPrivateKey(), decryptedMessage.getSigningPrivateKey());
  }

  private Listener listen(
      RepositoryType type,
      List<String> messages,
      boolean includeAliases,
      UnresolvedAddress... recipients)
      throws InterruptedException, ExecutionException {
    Listener listener = getRepositoryFactory(type).createListener();
    listener.open().get();
    final HashSet<UnresolvedAddress> expected = new HashSet<>();
    expected.add(recipientAlias);
    expected.add(recipientAddress);
    if (includeAliases) {
      for (UnresolvedAddress recipient : recipients) {
        listener
            .getAllAddressesAndAliases(recipient)
            .subscribe(
                multiple -> {
                  Assertions.assertEquals(expected, multiple);
                  subscribeMultiple(messages, listener, multiple);
                });
      }
    } else {
      final Set<UnresolvedAddress> multiple = Arrays.stream(recipients).collect(Collectors.toSet());
      subscribeMultiple(messages, listener, multiple);
    }
    return listener;
  }

  private void subscribeMultiple(
      List<String> messages, Listener listener, Set<UnresolvedAddress> multiple) {
    Arrays.asList(ListenerChannel.CONFIRMED_ADDED, ListenerChannel.UNCONFIRMED_ADDED)
        .forEach(
            channel -> {
              listener
                  .subscribeMultipleAddresses(channel, multiple, null, false)
                  .subscribe(
                      c -> {
                        final String message = c.getChannelParams() + " " + channel;
                        messages.add(message);
                        System.out.println(message);
                      });
            });
  }
}
