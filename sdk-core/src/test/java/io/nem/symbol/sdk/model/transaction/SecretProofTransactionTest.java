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
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SecretProofTransactionTest extends AbstractTransactionTester {

  private static String generationHash =
      "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  private static Account account =
      new Account(
          "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
          NetworkType.MIJIN_TEST);

  private static Address recipient =
      Address.createFromPublicKey(
          "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
          NetworkType.MIJIN_TEST);

  @Test
  @DisplayName("Serialization")
  void serialization() {

    String expected =
        "BF000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905242000000000000000001000000000000009022D04812D05000F96C283657B0C17990932BC84926CDE63FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE0400009A493664";

    String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
    String secretSeed = "9a493664";
    SecretProofTransaction transaction =
        SecretProofTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                LockHashAlgorithm.SHA3_256,
                recipient,
                secret,
                secretSeed)
            .build();

    assertSerialization(expected, transaction);
  }

  @Test
  @DisplayName("To aggregate")
  void toAggregate() {
    String expected =
        "6F000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B2400000000019052429022D04812D05000F96C283657B0C17990932BC84926CDE63FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE0400009A493664";

    String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
    String proof = "9a493664";
    SecretProofTransaction transaction =
        SecretProofTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                LockHashAlgorithm.SHA3_256,
                recipient,
                secret,
                proof)
            .build();

    transaction.toAggregate(
        new PublicAccount(
            "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
            NetworkType.MIJIN_TEST));

    assertEmbeddedSerialization(expected, transaction);
  }

  @Test
  void serializeAndSignTransaction() {
    String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
    String secretSeed = "9a493664";
    SecretProofTransaction transaction =
        SecretProofTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                LockHashAlgorithm.SHA3_256,
                recipient,
                secret,
                secretSeed)
            .build();
    SignedTransaction signedTransaction = transaction.signWith(account, generationHash);
    String payload = signedTransaction.getPayload();
    assertEquals(
        "BF0000000000000020BD216F6F00B887F8653A9EA0C6D7BFB718A0468B84BA2FB2CC4568A73A9AEF8EFDD3D0451A82705740E3DA81E6C3F43C79600F1F01B66C0A5B094B07ACB80C2134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F20000000001905242000000000000000001000000000000009022D04812D05000F96C283657B0C17990932BC84926CDE63FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE0400009A493664",
        payload);
    assertEquals(
        "5BDB8B04C5F3F6BCA6EEEBC07DE8F5F342634074F1CF06BD630540AE60F4FD25",
        signedTransaction.getHash());
  }

  @Test
  void shouldThrowErrorWhenSecretIsNotValid() {
    String proof =
        "B778A39A3663719DFC5E48C9D78431B1E45C2AF9DF538782BF199C189DABEAC7680ADA57DCEC8EEE91"
            + "C4E3BF3BFA9AF6FFDE90CD1D249D1C6121D7B759A001B1";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          SecretProofTransaction transaction =
              SecretProofTransactionFactory.create(
                      NetworkType.MIJIN_TEST,
                      new Deadline(BigInteger.ONE),
                      LockHashAlgorithm.SHA3_256,
                      recipient,
                      "non valid hash",
                      proof)
                  .build();
        },
        "not a valid secret");
  }
}
