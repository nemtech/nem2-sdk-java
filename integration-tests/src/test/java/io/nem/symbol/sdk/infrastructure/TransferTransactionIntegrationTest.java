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
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferTransactionIntegrationTest extends BaseIntegrationTest {

    private Account account = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateTransferTransaction(RepositoryType type) {
        UnresolvedAddress recipient = getRecipient();
        String message = "E2ETest:aggregateTransferTransaction:messagelooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
            + "oooooooong";
        TransferTransaction transferTransaction = TransferTransactionFactory.create(getNetworkType(), recipient,
            Collections.singletonList(getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))),
            new PlainMessage(message)).maxFee(this.maxFee).build();

        TransferTransaction processed = announceAggregateAndValidate(type, transferTransaction, account).getKey();
        Assertions.assertEquals(message, processed.getMessage().getPayload());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void standaloneTransferTransactionEncryptedMessage(RepositoryType type) {
        String namespaceName = "testaccount2";

        NamespaceId recipient = setAddressAlias(type, getRecipient(), namespaceName);
        Assertions
            .assertEquals("9988DD7D72227ECAE7000000000000000000000000000000", recipient.encoded(getNetworkType()));
        String message = "E2ETest:standaloneTransferTransaction:message 漢字";

        KeyPair senderKeyPair = KeyPair.random();
        KeyPair recipientKeyPair = KeyPair.random();

        Message encryptedMessage = EncryptedMessage
            .create(message, senderKeyPair.getPrivateKey(), recipientKeyPair.getPublicKey());

        TransferTransaction transferTransaction = TransferTransactionFactory.create(getNetworkType(), recipient,
            Collections.singletonList(getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))), encryptedMessage)
            .maxFee(this.maxFee).build();

        TransferTransaction processed = announceAndValidate(type, account, transferTransaction);

        assertTransferTransactions(transferTransaction, processed);

        assertEncryptedMessageTransaction(message, senderKeyPair, recipientKeyPair, processed);

        TransferTransaction restTransaction = (TransferTransaction) get(
            getRepositoryFactory(type).createTransactionRepository()
                .getTransaction(TransactionGroup.CONFIRMED, processed.getTransactionInfo().get().getHash().get()));

        assertTransferTransactions(transferTransaction, restTransaction);

        assertEncryptedMessageTransaction(message, senderKeyPair, recipientKeyPair, restTransaction);
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void transferTransactionNotEnoughFundAccount(RepositoryType type) {

        Address recipient = config().getTestAccount2().getAddress();

        NetworkType networkType = getNetworkType();

        Account account = Account.generateNewAccount(networkType);

        TransferTransaction transferTransaction = TransferTransactionFactory.create(getNetworkType(), recipient,
            Collections.singletonList(getNetworkCurrency().createAbsolute(BigInteger.valueOf(1000000000))),
            PlainMessage.Empty).maxFee(this.maxFee).build();

        IllegalArgumentException exceptions = Assertions.assertThrows(IllegalArgumentException.class,
            () -> announceAndValidate(type, account, transferTransaction));

        Assertions.assertTrue(exceptions.getMessage().contains("Failure_Core_Insufficient_Balance"));


    }

    private void assertTransferTransactions(TransferTransaction expected, TransferTransaction processed) {
        Assertions.assertEquals(expected.getRecipient().encoded(getNetworkType()),
            processed.getRecipient().encoded(getNetworkType()));
        Assertions.assertEquals(expected.getRecipient(), processed.getRecipient());
        Assertions.assertEquals(expected.getMessage().getType(), processed.getMessage().getType());
        Assertions.assertEquals(expected.getMessage().getPayload(), processed.getMessage().getPayload());
    }

    private void assertEncryptedMessageTransaction(String message, KeyPair senderKeyPair, KeyPair recipientKeyPair,
        TransferTransaction transaction) {
        Assertions.assertTrue(transaction.getMessage() instanceof EncryptedMessage);
        Assertions.assertNotEquals(message, transaction.getMessage().getPayload());
        String decryptedMessage = ((EncryptedMessage) transaction.getMessage())
            .decryptPayload(senderKeyPair.getPublicKey(), recipientKeyPair.getPrivateKey());
        Assertions.assertNotNull(message, decryptedMessage);
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void standaloneCreatePersistentDelegationRequestTransaction(RepositoryType type) {

        KeyPair senderKeyPair = KeyPair.random();
        KeyPair vrfPrivateKey = KeyPair.random();
        KeyPair recipientKeyPair = KeyPair.random();

        TransferTransaction transferTransaction = TransferTransactionFactory
            .createPersistentDelegationRequestTransaction(getNetworkType(), senderKeyPair.getPrivateKey(),
                vrfPrivateKey.getPrivateKey(), recipientKeyPair.getPublicKey()).maxFee(this.maxFee).build();

        TransferTransaction processed = announceAndValidate(type, account, transferTransaction);

        assertPersistentDelegationTransaction(recipientKeyPair, vrfPrivateKey, processed);

        TransferTransaction restTransaction = (TransferTransaction) get(
            getRepositoryFactory(type).createTransactionRepository()
                .getTransaction(TransactionGroup.CONFIRMED, processed.getTransactionInfo().get().getHash().get()));

        assertPersistentDelegationTransaction(recipientKeyPair, vrfPrivateKey, restTransaction);
    }

    private void assertPersistentDelegationTransaction(KeyPair recipientKeyPair, KeyPair vrfPrivateKey,
        TransferTransaction transaction) {
        String message = recipientKeyPair.getPublicKey().toHex();
        Assertions.assertTrue(transaction.getMessage() instanceof PersistentHarvestingDelegationMessage);
        Assertions.assertNotEquals(message, transaction.getMessage().getPayload());
        Assertions
            .assertEquals(MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE, transaction.getMessage().getType());
        HarvestingKeys decryptedMessage = ((PersistentHarvestingDelegationMessage) transaction.getMessage())
            .decryptPayload(recipientKeyPair.getPrivateKey());
        Assertions.assertEquals(recipientKeyPair.getPrivateKey(), decryptedMessage.getSigningPrivateKey());
        Assertions.assertEquals(vrfPrivateKey.getPrivateKey(), decryptedMessage.getVrfPrivateKey());
    }

}
