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
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
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
            "D1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905241000000000000000001000000000000003FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB85809698000000000064000000000000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84";

        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        SecretLockTransaction transaction =
            SecretLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                NetworkCurrency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBF"))
                .deadline(new FakeDeadline()).build();
        assertSerialization(expected, transaction);

    }

    @Test
    @DisplayName("Serialize")
    void serialize() {

        PublicAccount publicAccount = new PublicAccount(
            "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
            NetworkType.MIJIN_TEST);

        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        SecretLockTransaction transaction =
            SecretLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                NetworkCurrency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBF"))
                .deadline(new FakeDeadline()).signer(publicAccount).build();

        String expected =
            "D100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B240000000001905241000000000000000001000000000000003FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB85809698000000000064000000000000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84";

        assertSerialization(expected, transaction);

        String expectedEmbbeded =
            "81000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B2400000000019052413FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB85809698000000000064000000000000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84";

        assertEmbeddedSerialization(expectedEmbbeded, transaction);

    }

    @Test
    void serializeAndSignTransaction() {
        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        SecretLockTransaction transaction =
            SecretLockTransactionFactory.create(NetworkType.MIJIN_TEST,
                NetworkCurrency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBF")
            ).deadline(new FakeDeadline()).build();
        SignedTransaction signedTransaction = transaction.signWith(account, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "D100000000000000FD08B722F43EBC6BF0DB0EFA9573870A4166EFC5AEF638A8401B775AF9DBA9E154E019158D9CDFA6B228F9801FEABB3A8F7D87092CB59827B4BF404EF1B452012134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F20000000001905241000000000000000001000000000000003FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE44B262C46CEABB85809698000000000064000000000000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84",
            payload);
        assertEquals(
            "CA21D5E18FE171395246000D015D1EF35EC80DA90A2553BC76208A1F3FAD8952",
            signedTransaction.getHash());

    }

    @Test
    void shouldThrowErrorWhenSecretIsNotValid() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                SecretLockTransaction transaction =
                    SecretLockTransactionFactory.create(
                        NetworkType.MIJIN_TEST,
                        NetworkCurrency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                        BigInteger.valueOf(100),
                        LockHashAlgorithmType.SHA3_256,
                        "non valid hash",
                        Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"))
                        .deadline(new FakeDeadline()).build();
            },
            "not a valid secret");
    }
}
