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
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SecretLockTransactionTest extends AbstractTransactionTester {

  static Account account =
      new Account(
          "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
          NetworkType.MIJIN_TEST);
  static String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  @Test
  @DisplayName("Serialization")
  void serialization() {
    String expected =
        "D10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019052410000000000000000010000000000000090F36CA680C35D630662A0C38DC89D4978D10B511B3D241A3FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB858096980000000000640000000000000000";

    String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
    SecretLockTransaction transaction =
        SecretLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithm.SHA3_256,
                secret,
                Address.createFromRawAddress("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ"))
            .build();
    assertSerialization(expected, transaction);
  }

  @Test
  @DisplayName("Serialize")
  void serialize() {

    PublicAccount publicAccount =
        new PublicAccount(
            "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
            NetworkType.MIJIN_TEST);

    String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
    SecretLockTransaction transaction =
        SecretLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithm.SHA3_256,
                secret,
                Address.createFromRawAddress("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ"))
            .signer(publicAccount)
            .build();

    String expected =
        "D100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B2400000000019052410000000000000000010000000000000090F36CA680C35D630662A0C38DC89D4978D10B511B3D241A3FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB858096980000000000640000000000000000";

    assertSerialization(expected, transaction);

    String expectedEmbbeded =
        "81000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24000000000190524190F36CA680C35D630662A0C38DC89D4978D10B511B3D241A3FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB858096980000000000640000000000000000";

    assertEmbeddedSerialization(expectedEmbbeded, transaction);
  }

  @Test
  void serializeAndSignTransaction() {
    String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
    SecretLockTransaction transaction =
        SecretLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithm.SHA3_256,
                secret,
                Address.createFromRawAddress("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQ"))
            .build();
    SignedTransaction signedTransaction = transaction.signWith(account, generationHash);
    String payload = signedTransaction.getPayload();
    assertEquals(
        "D1000000000000001024B3B964A1DF3DAAE98D2B5F3AEC913D126399026CA812EE5F6BC814EF614C079143B3AE995CC48A03474D5ECEFF87C6480197E75FFD2298EADFF1CA431B062134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F200000000019052410000000000000000010000000000000090F36CA680C35D630662A0C38DC89D4978D10B511B3D241A3FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB858096980000000000640000000000000000",
        payload);
    assertEquals(
        "ACB9A421CBD17AEF4C18561BB5F269BCBD0FAB8C65AA628E8F57D60CB22A447B",
        signedTransaction.getHash());
  }

  @Test
  void shouldThrowErrorWhenSecretIsNotValid() {
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                SecretLockTransactionFactory.create(
                        NetworkType.MIJIN_TEST,
                        new Deadline(BigInteger.ONE),
                        Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                        BigInteger.valueOf(100),
                        LockHashAlgorithm.SHA3_256,
                        "non valid hash",
                        Address.generateRandom(NetworkType.MIJIN_TEST))
                    .build(),
            "not a valid secret");
    Assertions.assertEquals(
        "HashType and Secret have incompatible length or not hexadecimal string", e.getMessage());
  }
}
