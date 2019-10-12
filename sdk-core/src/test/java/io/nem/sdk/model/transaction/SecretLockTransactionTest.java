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
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
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
            "ca000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019052410000000000000000010000000000000044b262c46ceabb8580969800000000006400000000000000003fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe90e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac";

        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        SecretLockTransaction transaction =
            SecretLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"))
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
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"))
                .deadline(new FakeDeadline()).signer(publicAccount).build();

        String expected =
            "ca000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019052410000000000000000010000000000000044b262c46ceabb8580969800000000006400000000000000003fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe90e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac";

        assertSerialization(expected, transaction);

        String expectedEmbbeded =
            "7a0000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240190524144b262c46ceabb8580969800000000006400000000000000003fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe90e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac";

        assertEmbeddedSerialization(expectedEmbbeded, transaction);

    }

    @Test
    void serializeAndSignTransaction() {
        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        SecretLockTransaction transaction =
            SecretLockTransactionFactory.create(NetworkType.MIJIN_TEST,
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM")
            ).deadline(new FakeDeadline()).build();
        SignedTransaction signedTransaction = transaction.signWith(account, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "44B262C46CEABB8580969800000000006400000000000000003FC8BA10229AB5778D05D9C4B7F56676A88B"
                + "F9295C185ACFC0F961DB5408CAFE90E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC",
            payload.substring(240));
        assertEquals(
            "DAF5D06A8C47C636E4C97B5AD3FCB82F2B9D2140A37FE5572A3065CF6D65642A",
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
                        NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                        BigInteger.valueOf(100),
                        LockHashAlgorithmType.SHA3_256,
                        "non valid hash",
                        Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"))
                        .deadline(new FakeDeadline()).build();
            },
            "not a valid secret");
    }
}
