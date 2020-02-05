/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.catapult.builders.EmbeddedTransferTransactionBuilder;
import io.nem.catapult.builders.TransactionBuilderFactory;
import io.nem.catapult.builders.TransferTransactionBuilder;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PrivateKey;
import io.nem.sdk.api.BinarySerialization;
import io.nem.sdk.infrastructure.BinarySerializationImpl;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.MessageType;
import io.nem.sdk.model.message.PersistentHarvestingDelegationMessage;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransferTransactionTest extends AbstractTransactionTester {

    static Account account;
    static String generationHash;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                networkType);
        generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
    }

    @Test
    void createATransferTransactionViaStaticConstructor() {

        TransferTransactionFactory factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST,
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
                    networkType),
                Collections.emptyList(),
                PlainMessage.Empty
            );
        TransferTransaction transaction =
            factory.build();

        assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
        assertEquals(1, (int) transaction.getVersion());
        assertTrue(LocalDateTime.now().isBefore(transaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), transaction.getMaxFee());
        assertEquals(
            new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", networkType),
            transaction.getRecipient());
        assertEquals(0, transaction.getMosaics().size());
        assertNotNull(transaction.getMessage());

        assertEquals(161, factory.getSize());
        assertEquals(transaction.getSize(), factory.getSize());

    }

    @Test
    @DisplayName("Serialization")
    void shouldGenerateBytes() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        String expected =
            "b10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019054410000000000000000010000000000000090e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac01010000000000672b0000ce560000640000000000000000";
        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", networkType),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty).deadline(new FakeDeadline()).build();
        assertSerialization(expected, transaction);

    }

    @Test
    @DisplayName("Serialization-public")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        String expected =
            "d400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001026d70e1954775749c6811084d6450a3184d977383f0e4282cd47118af3775500000000019054410000000000000000010000000000000090e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac02140000000000671305c6390b00002c01000000000000672b0000ce560000640000000000000000536f6d65204d65737361676520e6bca2e5ad97";
        Mosaic mosaicId1 = new Mosaic(
            new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100));
        Mosaic mosaicId2 = new Mosaic(
            new MosaicId(new BigInteger("12342763262823")), BigInteger.valueOf(300));
        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", networkType),
                Arrays.asList(
                    mosaicId1, mosaicId2),
                new PlainMessage("Some Message 漢字")).signer(account.getPublicAccount())
                .deadline(new FakeDeadline()).build();

        assertSerialization(expected, transaction);

        String embeddedExpected = "84000000000000001026d70e1954775749c6811084d6450a3184d977383f0e4282cd47118af37755000000000190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac02140000000000671305c6390b00002c01000000000000672b0000ce560000640000000000000000536f6d65204d65737361676520e6bca2e5ad97";

        assertEmbeddedSerialization(embeddedExpected, transaction);

    }

    @Test
    @DisplayName("Serialization-public-namespace-recipient")
    void serializationNamespaceRecipient() {
        String expected =
            "c4000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905441000000000000000001000000000000009151776168d24257d80000000000000000000000000000000001140000000000672b0000ce560000640000000000000000536f6d65204d65737361676520e6bca2e5ad97";
        NamespaceId recipient = NamespaceId.createFromName("nem.owner");

        Assertions.assertEquals("d85742d268617751",
            recipient.getIdAsHex());

        Assertions.assertEquals("9151776168d24257d800000000000000000000000000000000",
            recipient.encoded(networkType));

        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                recipient,
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                new PlainMessage("Some Message 漢字")).deadline(new FakeDeadline()).build();

        Assertions.assertEquals(recipient.encoded(networkType), transaction.getRecipient().encoded(
            networkType));

        assertSerialization(expected, transaction);

    }

    @Test
    void basicCatbufferSimpleSerialization() {
        String expected =
            "c4000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905441000000000000000001000000000000009151776168d24257d80000000000000000000000000000000001140000000000672b0000ce560000640000000000000000536f6d65204d65737361676520e6bca2e5ad97";

        TransferTransactionBuilder transactionBuilder = (TransferTransactionBuilder) TransactionBuilderFactory
            .createTransactionBuilder(SerializationUtils.toDataInput(Hex.decode(expected)));

        Assertions.assertEquals(expected, Hex.toHexString(
            transactionBuilder.serialize()));

        Assertions
            .assertEquals(100L, transactionBuilder.getMosaics().get(0).getAmount().getAmount());

    }

    @Test
    void basicCatbufferAggregateSerialization() {
        String expected =
            "61000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac01010000000000672b0000ce560000640000000000000000";

        EmbeddedTransferTransactionBuilder transactionBuilder = (EmbeddedTransferTransactionBuilder) TransactionBuilderFactory
            .createEmbeddedTransactionBuilder(SerializationUtils.toDataInput(Hex.decode(expected)));

        Assertions.assertEquals(expected, Hex.toHexString(
            transactionBuilder.serialize()));

        Assertions
            .assertEquals(100L, transactionBuilder.getMosaics().get(0).getAmount().getAmount());

    }

    @Test
    @DisplayName("To aggregate")
    void toAggregate() {
        String expected =
            "61000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac01010000000000672b0000ce560000640000000000000000";

        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", networkType),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty).deadline(new FakeDeadline()).build();

        Transaction aggregateTransaction =
            transaction.toAggregate(
                new PublicAccount(
                    "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                    networkType));

        assertEmbeddedSerialization(expected, aggregateTransaction);

    }

    @Test
    void serializeAndSignTransaction() {
        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", networkType),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty).deadline(new FakeDeadline()).build();

        SignedTransaction signedTransaction = transaction.signWith(account, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "0000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC01010000000000672B0000CE560000640000000000000000",
            payload.substring(248));
        assertEquals(
            "C86F3B0A954677761671C2D1A273C59B5F45EE7FB19385DB2D094DE5A0270262",
            signedTransaction.getHash());


    }

    @Test
    void serializeNamespaceTransaction() {
        NamespaceId namespaceId = NamespaceId.createFromName("testaccount2");
        Assertions.assertEquals("e7ca7e22727ddd88", namespaceId.getIdAsHex());
        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                namespaceId,
                Collections.singletonList(NetworkCurrency.CAT_CURRENCY.createAbsolute(BigInteger.ONE)),
                PlainMessage.create("test-message")).deadline(new Deadline(BigInteger.ONE)).build();

        byte[] payload = transaction.serialize();
        String payloadHex = Hex.toHexString(payload);

        TransferTransaction newTransaction = (TransferTransaction) new BinarySerializationImpl()
            .deserialize(payload);
        String newPayloadHex = Hex.toHexString(newTransaction.serialize());

        Assertions.assertEquals(transaction.getRecipient().encoded(networkType),
            newTransaction.getRecipient().encoded(networkType));

        Assertions.assertEquals(payloadHex, newPayloadHex);
    }

    @Test
    void createPersistentDelegationRequestTransaction() {

        KeyPair remoteProxy = KeyPair.fromPrivate(PrivateKey
                .fromHexString("2602F4236B199B3DF762B2AAB46FC3B77D8DDB214F0B62538D3827576C46C111"),
            networkType.resolveSignSchema());

        KeyPair recipient = KeyPair.fromPrivate(PrivateKey
                .fromHexString("B72F2950498111BADF276D6D9D5E345F04E0D5C9B8342DA983C3395B4CF18F08"),
            networkType.resolveSignSchema());

        TransferTransaction transferTransaction =
            TransferTransactionFactory
                .createPersistentDelegationRequestTransaction(networkType,
                    remoteProxy.getPrivateKey(),
                    recipient.getPublicKey()
                ).deadline(new FakeDeadline()).build();

        assertEquals(NetworkType.MIJIN_TEST, transferTransaction.getNetworkType());
        assertEquals(1, (int) transferTransaction.getVersion());
        assertTrue(
            LocalDateTime.now().isBefore(transferTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), transferTransaction.getMaxFee());
        assertEquals(
            Address.createFromPublicKey(recipient.getPublicKey().toHex(), networkType),
            transferTransaction.getRecipient());
        assertEquals(0, transferTransaction.getMosaics().size());
        assertNotNull(transferTransaction.getMessage());
        assertEquals(MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
            transferTransaction.getMessage().getType());
        assertNotNull(transferTransaction.getMessage().getPayload());

        PersistentHarvestingDelegationMessage message = (PersistentHarvestingDelegationMessage) transferTransaction
            .getMessage();
        Assertions.assertEquals(remoteProxy.getPrivateKey().toHex().toUpperCase(),
            message.decryptPayload(recipient.getPrivateKey(), networkType));

        byte[] actual = transferTransaction.serialize();

        BinarySerialization serialization = new BinarySerializationImpl();

        TransferTransaction deserialized = (TransferTransaction) serialization.deserialize(actual);

        assertEquals(MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
            deserialized.getMessage().getType());
        PersistentHarvestingDelegationMessage deserializedMessage = (PersistentHarvestingDelegationMessage) deserialized
            .getMessage();
        Assertions.assertEquals(remoteProxy.getPrivateKey().toHex().toUpperCase(),
            deserializedMessage
                .decryptPayload(recipient.getPrivateKey(), networkType));

    }

    @Test
    void mosaicArrayToBeSorted() {
        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(
            new MosaicId(new BigInteger("200")), BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(
            new MosaicId(new BigInteger("100")), BigInteger.valueOf(2)));

        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", networkType),
                mosaics,
                PlainMessage.Empty).deadline(new FakeDeadline()).build();

        assertEquals(mosaics.get(0).getId().getIdAsLong(), new BigInteger("200").longValue());
        assertEquals(mosaics.get(1).getId().getIdAsLong(), new BigInteger("100").longValue());
        TransferTransaction deserialized = (TransferTransaction) new BinarySerializationImpl()
            .deserialize(transaction.serialize());

        assertEquals(mosaics.get(1).getId(), deserialized.getMosaics().get(0).getId());
        assertEquals(mosaics.get(0).getId(), deserialized.getMosaics().get(1).getId());
    }

    @Test
    void mosaicArrayToBeSortedHex() {
        ArrayList<Mosaic> mosaics = new ArrayList<>();
        mosaics.add(new Mosaic(
            new MosaicId("D525AD41D95FCF29"), BigInteger.valueOf(1)));
        mosaics.add(new Mosaic(
            new MosaicId("77A1969932D987D7"), BigInteger.valueOf(2)));
        mosaics.add(new Mosaic(
            new MosaicId("67F2B76F28BD36BA"), BigInteger.valueOf(3)));

        TransferTransaction transaction =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", networkType),
                mosaics,
                PlainMessage.Empty).deadline(new FakeDeadline()).build();

        assertEquals("D525AD41D95FCF29", mosaics.get(0).getId().getIdAsHex().toUpperCase());
        assertEquals("77A1969932D987D7", mosaics.get(1).getId().getIdAsHex().toUpperCase());
        assertEquals("67F2B76F28BD36BA", mosaics.get(2).getId().getIdAsHex().toUpperCase());

        TransferTransaction deserialized = (TransferTransaction) new BinarySerializationImpl()
            .deserialize(transaction.serialize());

        assertEquals(mosaics.get(2).getId(), deserialized.getMosaics().get(0).getId());
        assertEquals(mosaics.get(1).getId(), deserialized.getMosaics().get(1).getId());
        assertEquals(mosaics.get(0).getId(), deserialized.getMosaics().get(2).getId());

    }
}
