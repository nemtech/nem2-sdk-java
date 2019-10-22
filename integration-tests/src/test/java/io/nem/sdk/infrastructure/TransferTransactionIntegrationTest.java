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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.core.crypto.KeyPair;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.MessageType;
import io.nem.sdk.model.message.PersistentHarvestingDelegationMessage;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.message.EncryptedMessage;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
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
        String message =
            "E2ETest:aggregateTransferTransaction:messagelooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "oooooooong";
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipient,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                /*new PlainMessage(
                    "E2ETest:aggregateTransferTransaction:message"), */
// short message for debugging
                new PlainMessage(
                    message)
                // Use long message to test if size of inner transaction is calculated correctly
            ).build();

        TransferTransaction processed = announceAggregateAndValidate(type, account,
            transferTransaction);
        Assertions.assertEquals(message, processed.getMessage().getPayload());
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void standaloneTransferTransactionEncryptedMessage(RepositoryType type)
        throws InterruptedException {
        String namespaceName = "testaccount2";

        NamespaceId recipient = setAddressAlias(type, getRecipient(), namespaceName);
        Assertions.assertEquals("9188dd7d72227ecae700000000000000000000000000000000",
            recipient.encoded(getNetworkType()));
        String message = "E2ETest:standaloneTransferTransaction:message 漢字";

        NetworkType networkType = getNetworkType();
        KeyPair senderKeyPair = KeyPair.random(networkType.resolveSignSchema());
        KeyPair recipientKeyPair = KeyPair.random(networkType.resolveSignSchema());

        Message encryptedMessage = EncryptedMessage
            .create(message, senderKeyPair.getPrivateKey(), recipientKeyPair.getPublicKey(),
                networkType);

        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipient,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                encryptedMessage
            ).build();

        TransferTransaction processed = announceAndValidate(type, account, transferTransaction);

        assertTransferTransactions(transferTransaction, processed);

        assertEncryptedMessageTransaction(message, senderKeyPair, recipientKeyPair, processed);
        sleep(1000);

        TransferTransaction restTransaction = (TransferTransaction) get(
            getRepositoryFactory(type).createTransactionRepository()
                .getTransaction(processed.getTransactionInfo().get().getHash().get()));

        assertTransferTransactions(transferTransaction, restTransaction);

        assertEncryptedMessageTransaction(message, senderKeyPair, recipientKeyPair,
            restTransaction);
    }

    private void assertTransferTransactions(TransferTransaction expected,
        TransferTransaction processed) {
        Assertions
            .assertEquals(expected.getRecipient().encoded(getNetworkType()),
                processed.getRecipient().encoded(
                    getNetworkType()));
        Assertions.assertEquals(expected.getRecipient(), processed.getRecipient());
        Assertions.assertEquals(expected.getMessage().getType(), processed.getMessage().getType());
        Assertions
            .assertEquals(expected.getMessage().getPayload(), processed.getMessage().getPayload());
    }

    private void assertEncryptedMessageTransaction(String message,
        KeyPair senderKeyPair, KeyPair recipientKeyPair, TransferTransaction transaction) {
        Assertions.assertTrue(transaction.getMessage() instanceof EncryptedMessage);
        Assertions.assertNotEquals(message, transaction.getMessage().getPayload());
        String decryptedMessage = ((EncryptedMessage) transaction.getMessage())
            .decryptPayload(senderKeyPair.getPublicKey(), recipientKeyPair.getPrivateKey(),
                getNetworkType());
        Assertions.assertNotNull(message, decryptedMessage);
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void standaloneCreatePersistentDelegationRequestTransaction(RepositoryType type)
        throws InterruptedException {

        NetworkType networkType = getNetworkType();
        KeyPair senderKeyPair = KeyPair.random(networkType.resolveSignSchema());
        KeyPair recipientKeyPair = KeyPair.random(networkType.resolveSignSchema());

        TransferTransaction transferTransaction =
            TransferTransactionFactory.createPersistentDelegationRequestTransaction(
                getNetworkType(),
                senderKeyPair.getPrivateKey(), senderKeyPair.getPrivateKey(),
                recipientKeyPair.getPublicKey()
            ).build();

        TransferTransaction processed = announceAndValidate(type, account, transferTransaction);

        assertPersistentDelegationTransaction(senderKeyPair, recipientKeyPair, processed);
        sleep(1000);

        TransferTransaction restTransaction = (TransferTransaction) get(
            getRepositoryFactory(type).createTransactionRepository()
                .getTransaction(processed.getTransactionInfo().get().getHash().get()));

        assertPersistentDelegationTransaction(senderKeyPair, recipientKeyPair, restTransaction);
    }

    private void assertPersistentDelegationTransaction(
        KeyPair senderKeyPair, KeyPair recipientKeyPair, TransferTransaction transaction) {

        String message = recipientKeyPair.getPublicKey().toHex();
        Assertions
            .assertTrue(transaction.getMessage() instanceof PersistentHarvestingDelegationMessage);
        Assertions.assertNotEquals(message, transaction.getMessage().getPayload());
        Assertions.assertEquals(MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
            transaction.getMessage().getType());
        String decryptedMessage = ((PersistentHarvestingDelegationMessage) transaction.getMessage())
            .decryptPayload(senderKeyPair.getPublicKey(), recipientKeyPair.getPrivateKey(),
                getNetworkType());
        Assertions.assertNotNull(message, decryptedMessage);
    }

}
