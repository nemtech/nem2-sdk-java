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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SecretProofTransactionTest extends AbstractTransactionTester {

    private static String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    private static Account account = new Account(
        "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
        NetworkType.MIJIN_TEST);


    private static Address recipient = Address.createFromPublicKey(
        "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
        NetworkType.MIJIN_TEST);

    @Test
    @DisplayName("Serialization")
    void serialization() {

        String expected =
            "c0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905242000000000000000001000000000000003fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe0400009022d04812d05000f96c283657b0c17990932bc84926cde64f9a493664";

        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        String secretSeed = "9a493664";
        SecretProofTransaction transaction =
            SecretProofTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                LockHashAlgorithmType.SHA3_256,
                recipient,
                secret,
                secretSeed).deadline(new FakeDeadline()).build();

        assertSerialization(expected, transaction);

    }

    @Test
    @DisplayName("To aggregate")
    void toAggregate() {
        String expected =
            "70000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2400000000019052423fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe0400009022d04812d05000f96c283657b0c17990932bc84926cde64f9a493664";

        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        String proof = "9a493664";
        SecretProofTransaction transaction =
            SecretProofTransactionFactory.create(NetworkType.MIJIN_TEST,
                LockHashAlgorithmType.SHA3_256,
                recipient,
                secret,
                proof
            ).deadline(new FakeDeadline()).build();

        transaction
            .toAggregate(
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
                LockHashAlgorithmType.SHA3_256,
                recipient,
                secret,
                secretSeed).deadline(new FakeDeadline()).build();
        SignedTransaction signedTransaction = transaction.signWith(account, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "c00000000000000024849d448743962a21a64e3fe61bff6127c84a7b910c54a4755a942f36138ecfd6f3eceab9ba3b000ac2c742c170381d46352172a157e1eb9839a2e6f1f4be0d2134e47aee6f2392a5b3d1238cd7714eabeb739361b7ccf24bae127f10df17f20000000001905242000000000000000001000000000000003fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe0400009022d04812d05000f96c283657b0c17990932bc84926cde64f9a493664",
            payload);
        assertEquals(
            "b40ae8725dc90668f239e4d65b0fa602b8e4221542381449c5908787ae81aafb",
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
                        LockHashAlgorithmType.SHA3_256,
                        recipient,
                        "non valid hash",
                        proof).deadline(new FakeDeadline()).build();
            },
            "not a valid secret");
    }
}
