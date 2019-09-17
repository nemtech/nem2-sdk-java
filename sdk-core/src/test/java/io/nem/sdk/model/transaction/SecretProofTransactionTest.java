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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SecretProofTransactionTest {

    static SignSchema signSchema = SignSchema.DEFAULT;

    static Account account;
    static String generationHash;
    static Address recipient;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST, signSchema);
        generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
        recipient =
            Address.createFromPublicKey(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.MIJIN_TEST, signSchema);
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {

        String expected =
            "b80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190524200000000000000000100000000000000003fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe9022d04812d05000f96c283657b0c17990932bc84926cde64f04009a493664";

        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        String secretSeed = "9a493664";
        SecretProofTransaction secretProoftx =
            SecretProofTransaction.create(
                new FakeDeadline(),
                BigInteger.ZERO,
                HashType.SHA3_256,
                recipient,
                secret,
                secretSeed,
                NetworkType.MIJIN_TEST);
        byte[] actual = secretProoftx.generateBytes();
        assertEquals(expected, Hex.toHexString(actual));
    }

    @Test
    @DisplayName("To aggregate")
    void toAggregate() {
        String expected =
            "680000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401905242003fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe9022d04812d05000f96c283657b0c17990932bc84926cde64f04009a493664";

        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        String secretSeed = "9a493664";
        SecretProofTransaction secretProoftx =
            SecretProofTransaction.create(
                new FakeDeadline(),
                BigInteger.ZERO,
                HashType.SHA3_256,
                recipient,
                secret,
                secretSeed,
                NetworkType.MIJIN_TEST);
        byte[] actual =
            secretProoftx
                .toAggregate(
                    new PublicAccount(
                        "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                        NetworkType.MIJIN_TEST, signSchema))
                .toAggregateTransactionBytes();
        assertEquals(expected, Hex.toHexString(actual));
    }

    @Test
    void serializeAndSignTransaction() {
        String secret = "3fc8ba10229ab5778d05d9c4b7f56676a88bf9295c185acfc0f961db5408cafe";
        String secretSeed = "9a493664";
        SecretProofTransaction secretProoftx =
            SecretProofTransaction.create(
                new FakeDeadline(),
                BigInteger.ZERO,
                HashType.SHA3_256,
                recipient,
                secret,
                secretSeed,
                NetworkType.MIJIN_TEST);
        SignedTransaction signedTransaction = secretProoftx.signWith(account, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "003FC8BA10229AB5778D05D9C4B7F56676A88BF9295C185ACFC0F961DB5408CAFE9022D04812D05000F96C283657B0C17990932BC84926CDE64F04009A493664",
            payload.substring(240));
        assertEquals(
            "E0FB9BF47C70A411EB77AD4683FA33E823A403BC04ECD0D50F85143BBE2C3229",
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
                SecretProofTransaction secretProoftx =
                    SecretProofTransaction.create(
                        new FakeDeadline(),
                        BigInteger.ZERO,
                        HashType.SHA3_256,
                        recipient,
                        "non valid hash",
                        proof,
                        NetworkType.MIJIN_TEST);
            },
            "not a valid secret");
    }
}
