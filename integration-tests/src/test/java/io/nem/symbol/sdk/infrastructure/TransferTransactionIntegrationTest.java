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
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferTransactionIntegrationTest extends BaseIntegrationTest {

  private Account account;

  @BeforeEach
  void setup() {
    account = config().getDefaultAccount();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void aggregateTransferTransaction(RepositoryType type) {
    UnresolvedAddress recipient = helper().getTestAccount(type).getRight();
    String message =
        "E2ETest:aggregateTransferTransaction:messagelooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "oooooooong";
    Currency networkCurrency = getNetworkCurrency();
    Mosaic mosaic =
        new Mosaic(networkCurrency.getNamespaceId().get(), BigInteger.valueOf(10202020));
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(), getDeadline(), recipient, Collections.singletonList(mosaic))
            .message(new PlainMessage(message))
            .maxFee(maxFee)
            .build();

    TransferTransaction processed =
        announceAggregateAndValidate(type, transferTransaction, account).getKey();
    Assertions.assertEquals(message, processed.getMessage().get().getText());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void standaloneTransferTransactionEncryptedMessage(RepositoryType type) {
    this.helper().sendMosaicFromNemesis(type, getRecipient(), false);
    String namespaceName = "standaloneTransferTransactionEncryptedMessagealias".toLowerCase();

    NamespaceId recipient = setAddressAlias(type, getRecipient(), namespaceName);
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

    TransferTransaction processed = announceAndValidate(type, account, transferTransaction);

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

    KeyPair senderKeyPair = KeyPair.random();
    KeyPair vrfPrivateKey = KeyPair.random();
    KeyPair recipientKeyPair = KeyPair.random();

    TransferTransaction transferTransaction =
        TransferTransactionFactory.createPersistentDelegationRequestTransaction(
                getNetworkType(),
                getDeadline(),
                senderKeyPair.getPrivateKey(),
                vrfPrivateKey.getPrivateKey(),
                recipientKeyPair.getPublicKey())
            .maxFee(maxFee)
            .build();

    TransferTransaction processed = announceAndValidate(type, account, transferTransaction);

    assertPersistentDelegationTransaction(recipientKeyPair, vrfPrivateKey, processed);

    TransferTransaction restTransaction =
        (TransferTransaction)
            get(
                getRepositoryFactory(type)
                    .createTransactionRepository()
                    .getTransaction(
                        TransactionGroup.CONFIRMED,
                        processed.getTransactionInfo().get().getHash().get()));

    assertPersistentDelegationTransaction(recipientKeyPair, vrfPrivateKey, restTransaction);
  }

  private void assertPersistentDelegationTransaction(
      KeyPair recipientKeyPair, KeyPair vrfPrivateKey, TransferTransaction transaction) {
    String message = recipientKeyPair.getPublicKey().toHex();
    Assertions.assertTrue(
        transaction.getMessage().get() instanceof PersistentHarvestingDelegationMessage);
    Assertions.assertNotEquals(message, transaction.getMessage().get().getText());
    Assertions.assertEquals(
        MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
        transaction.getMessage().get().getType());
    HarvestingKeys decryptedMessage =
        ((PersistentHarvestingDelegationMessage) transaction.getMessage().get())
            .decryptPayload(recipientKeyPair.getPrivateKey());
    Assertions.assertEquals(
        recipientKeyPair.getPrivateKey(), decryptedMessage.getSigningPrivateKey());
    Assertions.assertEquals(vrfPrivateKey.getPrivateKey(), decryptedMessage.getVrfPrivateKey());
  }
}
