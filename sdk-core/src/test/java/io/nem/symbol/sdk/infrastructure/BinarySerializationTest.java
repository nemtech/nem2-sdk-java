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
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.message.MessageType;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.FakeDeadline;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link BinarySerializationImpl} */
class BinarySerializationTest {

  private static String generationHash =
      "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  private static Account account =
      new Account(
          "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
          NetworkType.MIJIN_TEST);

  @Test
  void testAllTransactionAreHandled() {
    BinarySerializationImpl binarySerialization = new BinarySerializationImpl();
    List<TransactionType> notHandledTransactionTypes =
        Arrays.stream(TransactionType.values())
            .filter(
                t -> {
                  try {
                    Assertions.assertNotNull(binarySerialization.resolveSerializer(t));
                    return false;
                  } catch (UnsupportedOperationException e) {
                    return true;
                  }
                })
            .collect(Collectors.toList());

    Assertions.assertTrue(
        notHandledTransactionTypes.isEmpty(),
        "The following transaction types are not handled: \n"
            + notHandledTransactionTypes.stream()
                .map(TransactionType::toString)
                .collect(Collectors.joining("\n")));
  }

  @Test
  void testSerializationDeserialization() {
    BinarySerializationImpl binarySerialization = new BinarySerializationImpl();
    TransferTransaction transaction =
        TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                Address.generateRandom(NetworkType.MIJIN_TEST),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))))
            .message(new PlainMessage("Some Message"))
            .deadline(new FakeDeadline())
            .build();

    byte[] serialize = binarySerialization.serialize(transaction);
    Assertions.assertNotNull(serialize);

    TransferTransaction deserializedTransaction =
        (TransferTransaction) binarySerialization.deserialize(serialize);
    Assertions.assertNotNull(deserializedTransaction);

    Assertions.assertEquals(
        "Some Message", deserializedTransaction.getMessage().get().getPayload());
    Assertions.assertEquals(
        MessageType.PLAIN_MESSAGE, deserializedTransaction.getMessage().get().getType());
  }

  @Test
  void testSignature() {
    BinarySerializationImpl binarySerialization = new BinarySerializationImpl();
    TransferTransaction transaction =
        TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                Address.generateRandom(NetworkType.MIJIN_TEST),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))))
            .message(new PlainMessage("Some Message"))
            .deadline(new FakeDeadline())
            .build();

    SignedTransaction signedTransaction = transaction.signWith(account, generationHash);

    String signature = signedTransaction.getPayload().substring(16, 128 + 16);

    // If we deserialize the signed transaction, we get everything back, include the
    // signer and
    // signature

    byte[] payloadWithSignatureAndSigner = ConvertUtils.getBytes(signedTransaction.getPayload());

    TransferTransaction deserialized =
        (TransferTransaction) binarySerialization.deserialize(payloadWithSignatureAndSigner);

    Assertions.assertTrue(deserialized.getSignature().isPresent());
    Assertions.assertTrue(deserialized.getSigner().isPresent());
    Assertions.assertEquals(signature.toUpperCase(), deserialized.getSignature().get());
    Assertions.assertEquals(account.getPublicAccount(), deserialized.getSigner().get());

    // Test that the payload is the same, just without the signature and signer.
    byte[] payloadWithoutSignatureAndSigner = binarySerialization.serialize(transaction);
    Assertions.assertEquals(
        ConvertUtils.toHex(payloadWithoutSignatureAndSigner).substring(208),
        ConvertUtils.toHex(payloadWithSignatureAndSigner).substring(208));

    Transaction deserializeWithoutSignature =
        binarySerialization.deserialize(payloadWithoutSignatureAndSigner);

    Assertions.assertFalse(deserializeWithoutSignature.getSignature().isPresent());
    Assertions.assertFalse(deserializeWithoutSignature.getSigner().isPresent());
  }
}
