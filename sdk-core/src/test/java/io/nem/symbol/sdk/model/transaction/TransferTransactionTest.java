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
package io.nem.symbol.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.catapult.builders.EmbeddedTransactionBuilderHelper;
import io.nem.symbol.catapult.builders.EmbeddedTransferTransactionBuilder;
import io.nem.symbol.catapult.builders.TransactionBuilderHelper;
import io.nem.symbol.catapult.builders.TransferTransactionBuilder;
import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.BinarySerialization;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.MessageType;
import io.nem.symbol.sdk.model.message.PersistentHarvestingDelegationMessage;
import io.nem.symbol.sdk.model.message.PersistentHarvestingDelegationMessage.HarvestingKeys;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
            "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d", networkType);
    generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
  }

  @Test
  void createATransferTransactionViaStaticConstructor() {
    Duration epochAdjustment = Duration.ofSeconds(100);
    Address address = Address.generateRandom(networkType);
    Deadline originalDeadline = new Deadline(BigInteger.ONE);
    TransferTransactionFactory factory =
        TransferTransactionFactory.create(
                networkType, originalDeadline, address, Collections.emptyList())
            .message(new PlainMessage(""));

    Assertions.assertEquals(originalDeadline, factory.getDeadline());

    Deadline updatedDeadline = Deadline.create(epochAdjustment);
    factory.deadline(updatedDeadline);
    Assertions.assertEquals(updatedDeadline, factory.getDeadline());
    TransferTransaction transaction = factory.build();

    assertEquals(networkType, transaction.getNetworkType());
    assertEquals(1, (int) transaction.getVersion());
    assertTrue(
        LocalDateTime.now().isBefore(transaction.getDeadline().getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(0), transaction.getMaxFee());
    assertEquals(address, transaction.getRecipient());
    assertEquals(0, transaction.getMosaics().size());
    assertNotNull(transaction.getMessage());

    assertEquals(161, factory.getSize());
    assertEquals(transaction.getSize(), factory.getSize());
  }

  @Test
  void createATransferTransactionViaStaticConstructorSetMaxFee() {
    Duration epochAdjustment = Duration.ofSeconds(100);
    long feeMultiplier = 10;
    TransactionFactory<TransferTransaction> factory =
        TransferTransactionFactory.create(
                networkType,
                Deadline.create(epochAdjustment),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                Collections.emptyList())
            .message(new PlainMessage(""))
            .calculateMaxFeeFromMultiplier(feeMultiplier);
    TransferTransaction transaction = factory.build();

    assertEquals(networkType, transaction.getNetworkType());
    assertEquals(1, (int) transaction.getVersion());
    assertTrue(
        LocalDateTime.now().isBefore(transaction.getDeadline().getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(1610), transaction.getMaxFee());
    assertEquals(
        new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
        transaction.getRecipient());
    assertEquals(0, transaction.getMosaics().size());
    assertNotNull(transaction.getMessage());

    assertEquals(161, factory.getSize());
    assertEquals(
        BigInteger.valueOf(transaction.getSize()).multiply(BigInteger.valueOf(feeMultiplier)),
        transaction.getMaxFee());
    assertEquals(transaction.getSize(), factory.getSize());
  }

  @Test
  void shouldGenerateBytes() {
    String expected =
        "B10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019054410000000000000000010000000000000090F36CA680C35D630662A0C38DC89D4978D10B511B3D241A0100010000000000672B0000CE560000640000000000000000";
    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))))
            .message(new PlainMessage(""))
            .build();
    assertSerialization(expected, transaction);
  }

  @Test
  void shouldGenerateBytesNoMessage() {
    String expected =
        "B00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019054410000000000000000010000000000000090F36CA680C35D630662A0C38DC89D4978D10B511B3D241A0000010000000000672B0000CE5600006400000000000000";
    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))))
            .build();
    assertSerialization(expected, transaction);
  }

  @Test
  @DisplayName("Serialization-public")
  void serialization() {
    String expected =
        "D400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F200000000019054410000000000000000010000000000000090F36CA680C35D630662A0C38DC89D4978D10B511B3D241A1400020000000000671305C6390B00002C01000000000000672B0000CE560000640000000000000000536F6D65204D65737361676520E6BCA2E5AD97";
    Mosaic mosaicId1 =
        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100));
    Mosaic mosaicId2 =
        new Mosaic(new MosaicId(new BigInteger("12342763262823")), BigInteger.valueOf(300));
    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                Arrays.asList(mosaicId1, mosaicId2))
            .message(new PlainMessage("Some Message 漢字"))
            .signer(account.getPublicAccount())
            .build();

    assertSerialization(expected, transaction);

    String embeddedExpected =
        "84000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2000000000190544190F36CA680C35D630662A0C38DC89D4978D10B511B3D241A1400020000000000671305C6390B00002C01000000000000672B0000CE560000640000000000000000536F6D65204D65737361676520E6BCA2E5AD97";

    assertEmbeddedSerialization(embeddedExpected, transaction);
  }

  @Test
  @DisplayName("Serialization-public-namespace-recipient")
  void serializationNamespaceRecipient() {
    String expected =
        "C4000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905441000000000000000001000000000000009151776168D24257D80000000000000000000000000000001400010000000000672B0000CE560000640000000000000000536F6D65204D65737361676520E6BCA2E5AD97";
    NamespaceId recipient = NamespaceId.createFromName("nem.owner");

    Assertions.assertEquals("D85742D268617751", recipient.getIdAsHex());

    Assertions.assertEquals(
        "9151776168D24257D8000000000000000000000000000000", recipient.encoded(networkType));

    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                recipient,
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))))
            .message(new PlainMessage("Some Message 漢字"))
            .build();

    Assertions.assertEquals(
        recipient.encoded(networkType), transaction.getRecipient().encoded(networkType));

    assertSerialization(expected, transaction);
  }

  @Test
  void basicCatbufferSimpleSerialization() {
    String expected =
        "C4000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905441000000000000000001000000000000009151776168D24257D80000000000000000000000000000001400010000000000672B0000CE560000640000000000000000536F6D65204D65737361676520E6BCA2E5AD97";

    TransferTransactionBuilder transactionBuilder =
        (TransferTransactionBuilder)
            TransactionBuilderHelper.loadFromBinary(
                SerializationUtils.toDataInput(ConvertUtils.fromHexToBytes(expected)));

    Assertions.assertEquals(expected, ConvertUtils.toHex(transactionBuilder.serialize()));

    Assertions.assertEquals(100L, transactionBuilder.getMosaics().get(0).getAmount().getAmount());
  }

  @Test
  void basicCatbufferAggregateSerialization() {
    String expected =
        "61000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24000000000190544190E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA840100010000000000672B0000CE560000640000000000000000";

    EmbeddedTransferTransactionBuilder transactionBuilder =
        (EmbeddedTransferTransactionBuilder)
            EmbeddedTransactionBuilderHelper.loadFromBinary(
                SerializationUtils.toDataInput(ConvertUtils.fromHexToBytes(expected)));

    Assertions.assertEquals(expected, ConvertUtils.toHex(transactionBuilder.serialize()));

    Assertions.assertEquals(100L, transactionBuilder.getMosaics().get(0).getAmount().getAmount());
  }

  @Test
  @DisplayName("To aggregate")
  void toAggregate() {
    String expected =
        "61000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24000000000190544190F36CA680C35D630662A0C38DC89D4978D10B511B3D241A0100010000000000672B0000CE560000640000000000000000";

    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))))
            .message(new PlainMessage(""))
            .build();

    Transaction aggregateTransaction =
        transaction.toAggregate(
            new PublicAccount(
                "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24", networkType));

    assertEmbeddedSerialization(expected, aggregateTransaction);
  }

  @Test
  void serializeAndSignTransaction() {
    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))))
            .message(new PlainMessage(""))
            .build();

    SignedTransaction signedTransaction = transaction.signWith(account, generationHash);
    String payload = signedTransaction.getPayload();
    assertEquals(
        "B10000000000000013193EF4F0D94DE26249D196A0575944877D5572CE13B80E4E8380FA34F9F54FF936F96162473857546F7B624A6248D8F7B3D0142DC85BBB06EB7BFEE125880B2134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F200000000019054410000000000000000010000000000000090F36CA680C35D630662A0C38DC89D4978D10B511B3D241A0100010000000000672B0000CE560000640000000000000000",
        payload);
    assertEquals(
        "58D649ABF9A26AFB3F070E26F157920DAB6423415B4344108B93EDD80DE330E5",
        signedTransaction.getHash());
  }

  @Test
  void serializeNamespaceTransaction() {
    NamespaceId namespaceId = NamespaceId.createFromName("testaccount2");
    Assertions.assertEquals("E7CA7E22727DDD88", namespaceId.getIdAsHex());
    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                namespaceId,
                Collections.singletonList(Currency.CAT_CURRENCY.createAbsolute(BigInteger.ONE)))
            .message(new PlainMessage("test-message"))
            .build();

    byte[] payload = transaction.serialize();
    String payloadHex = ConvertUtils.toHex(payload);

    TransferTransaction newTransaction =
        (TransferTransaction) new BinarySerializationImpl().deserialize(payload);
    String newPayloadHex = ConvertUtils.toHex(newTransaction.serialize());

    Assertions.assertEquals(
        transaction.getRecipient().encoded(networkType),
        newTransaction.getRecipient().encoded(networkType));

    Assertions.assertEquals(payloadHex, newPayloadHex);
  }

  @Test
  void createPersistentDelegationRequestTransaction() {
    Duration epochAdjustment = Duration.ofSeconds(100);
    KeyPair signingPrivateKey =
        KeyPair.fromPrivate(
            PrivateKey.fromHexString(
                "2602F4236B199B3DF762B2AAB46FC3B77D8DDB214F0B62538D3827576C46C111"));

    KeyPair vrfPrivateKey =
        KeyPair.fromPrivate(
            PrivateKey.fromHexString(
                "2602F4236B199B3DF762B2AAB46FC3B77D8DDB214F0B62538D3827576C46C000"));

    KeyPair recipient =
        KeyPair.fromPrivate(
            PrivateKey.fromHexString(
                "B72F2950498111BADF276D6D9D5E345F04E0D5C9B8342DA983C3395B4CF18F08"));

    TransferTransaction transferTransaction =
        TransferTransactionFactory.createPersistentDelegationRequestTransaction(
                networkType,
                Deadline.create(epochAdjustment),
                signingPrivateKey.getPrivateKey(),
                vrfPrivateKey.getPrivateKey(),
                recipient.getPublicKey())
            .build();

    assertEquals(networkType, transferTransaction.getNetworkType());
    assertEquals(1, (int) transferTransaction.getVersion());
    assertTrue(
        LocalDateTime.now()
            .isBefore(transferTransaction.getDeadline().getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(0), transferTransaction.getMaxFee());
    assertEquals(
        Address.createFromPublicKey(recipient.getPublicKey().toHex(), networkType),
        transferTransaction.getRecipient());
    assertEquals(0, transferTransaction.getMosaics().size());
    assertNotNull(transferTransaction.getMessage());
    assertEquals(
        MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
        transferTransaction.getMessage().get().getType());
    assertNotNull(transferTransaction.getMessage().get().getText());

    PersistentHarvestingDelegationMessage message =
        (PersistentHarvestingDelegationMessage) transferTransaction.getMessage().get();
    HarvestingKeys decodedKeys = message.decryptPayload(recipient.getPrivateKey());
    Assertions.assertEquals(signingPrivateKey.getPrivateKey(), decodedKeys.getSigningPrivateKey());
    Assertions.assertEquals(vrfPrivateKey.getPrivateKey(), decodedKeys.getVrfPrivateKey());

    byte[] actual = transferTransaction.serialize();

    BinarySerialization serialization = new BinarySerializationImpl();

    TransferTransaction deserialized = (TransferTransaction) serialization.deserialize(actual);

    assertEquals(
        MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
        deserialized.getMessage().get().getType());
    PersistentHarvestingDelegationMessage deserializedMessage =
        (PersistentHarvestingDelegationMessage) deserialized.getMessage().get();
    HarvestingKeys decodedKeys2 = deserializedMessage.decryptPayload(recipient.getPrivateKey());
    Assertions.assertEquals(signingPrivateKey.getPrivateKey(), decodedKeys2.getSigningPrivateKey());
    Assertions.assertEquals(vrfPrivateKey.getPrivateKey(), decodedKeys2.getVrfPrivateKey());
  }

  @Test
  void mosaicArrayToBeSorted() {
    ArrayList<Mosaic> mosaics = new ArrayList<>();
    mosaics.add(new Mosaic(new MosaicId(new BigInteger("200")), BigInteger.valueOf(1)));
    mosaics.add(new Mosaic(new MosaicId(new BigInteger("100")), BigInteger.valueOf(2)));

    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                mosaics)
            .message(new PlainMessage(""))
            .build();

    assertEquals(mosaics.get(0).getId().getIdAsLong(), new BigInteger("200").longValue());
    assertEquals(mosaics.get(1).getId().getIdAsLong(), new BigInteger("100").longValue());
    TransferTransaction deserialized =
        (TransferTransaction) new BinarySerializationImpl().deserialize(transaction.serialize());

    assertEquals(mosaics.get(1).getId(), deserialized.getMosaics().get(0).getId());
    assertEquals(mosaics.get(0).getId(), deserialized.getMosaics().get(1).getId());
  }

  @Test
  void mosaicArrayToBeSortedHex() {
    ArrayList<Mosaic> mosaics = new ArrayList<>();
    mosaics.add(new Mosaic(new MosaicId("D525AD41D95FCF29"), BigInteger.valueOf(1)));
    mosaics.add(new Mosaic(new MosaicId("77A1969932D987D7"), BigInteger.valueOf(2)));
    mosaics.add(new Mosaic(new MosaicId("67F2B76F28BD36BA"), BigInteger.valueOf(3)));

    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new Address("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ", networkType),
                mosaics)
            .message(new PlainMessage(""))
            .build();

    assertEquals("D525AD41D95FCF29", mosaics.get(0).getId().getIdAsHex().toUpperCase());
    assertEquals("77A1969932D987D7", mosaics.get(1).getId().getIdAsHex().toUpperCase());
    assertEquals("67F2B76F28BD36BA", mosaics.get(2).getId().getIdAsHex().toUpperCase());

    TransferTransaction deserialized =
        (TransferTransaction) new BinarySerializationImpl().deserialize(transaction.serialize());

    assertEquals(mosaics.get(2).getId(), deserialized.getMosaics().get(0).getId());
    assertEquals(mosaics.get(1).getId(), deserialized.getMosaics().get(1).getId());
    assertEquals(mosaics.get(0).getId(), deserialized.getMosaics().get(2).getId());
  }
}
