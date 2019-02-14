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

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.spongycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SecretProofTransactionTest {
    static Account account;

    @BeforeAll
    public static void setup() {
        account = new Account("787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d", NetworkType.MIJIN_TEST);
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/SecretProofTransaction.spec.js
        byte[] expected = new byte[]{(byte)191,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,3,(byte)144,76,67,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,(byte)183,120,(byte)163,(byte)154,54,
                99,113,(byte)157,(byte)252,94,72,(byte)201,(byte)215,(byte)132,49,(byte)177,(byte)228,92,42,(byte)249,
                (byte)223,83,(byte)135,(byte)130,(byte)191,25,(byte)156,24,(byte)157,(byte)171,(byte)234,(byte)199,104,
                10,(byte)218,87,(byte)220,(byte)236,(byte)142,(byte)238,(byte)145,(byte)196,(byte)227,(byte)191,59,(byte)250,
                (byte)154,(byte)246,(byte)255,(byte)222,(byte)144,(byte)205,29,36,(byte)157,28,97,33,(byte)215,(byte)183,89,
                (byte)160,1,(byte)177,4,0,(byte)154,73,54,100

        };

        String secret = "b778a39a3663719dfc5e48c9d78431b1e45c2af9df538782bf199c189dabeac7680ada57dcec8eee91c4e3bf3bfa9af6ffde90cd1d249d1c6121d7b759a001b1";
        String secretSeed = "9a493664";
        SecretProofTransaction secretProoftx = SecretProofTransaction.create(
                new FakeDeadline(),
                HashType.SHA3_512,
                secret,
                secretSeed,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = secretProoftx.generateBytes();
        assertEquals(Hex.toHexString(expected), Hex.toHexString(actual));
    }

    @Test
    @DisplayName("To aggregate")
    void toAggregate() {
        byte[] expected = new byte[]{(byte)111,0,0,0,-102,73,54,100,6,-84,-87,82,-72,-117,-83,-11,-15,-23,-66,108,-28,-106,-127,
                65,3,90,96,-66,80,50,115,-22,101,69,107,36,3,(byte)144,76,67,0,(byte)183,120,(byte)163,(byte)154,54,
                99,113,(byte)157,(byte)252,94,72,(byte)201,(byte)215,(byte)132,49,(byte)177,(byte)228,92,42,(byte)249,
                (byte)223,83,(byte)135,(byte)130,(byte)191,25,(byte)156,24,(byte)157,(byte)171,(byte)234,(byte)199,104,
                10,(byte)218,87,(byte)220,(byte)236,(byte)142,(byte)238,(byte)145,(byte)196,(byte)227,(byte)191,59,(byte)250,
                (byte)154,(byte)246,(byte)255,(byte)222,(byte)144,(byte)205,29,36,(byte)157,28,97,33,(byte)215,(byte)183,89,
                (byte)160,1,(byte)177,4,0,(byte)154,73,54,100
        };

        String secret = "b778a39a3663719dfc5e48c9d78431b1e45c2af9df538782bf199c189dabeac7680ada57dcec8eee91c4e3bf3bfa9af6ffde90cd1d249d1c6121d7b759a001b1";
        String secretSeed = "9a493664";
        SecretProofTransaction secretProoftx = SecretProofTransaction.create(
                new FakeDeadline(),
                HashType.SHA3_512,
                secret,
                secretSeed,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = secretProoftx.toAggregate(new PublicAccount("9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24", NetworkType.MIJIN_TEST)).toAggregateTransactionBytes();
        assertArrayEquals(expected, actual);
    }

    @Test
    void serializeAndSignTransaction() {
        String secret = "b778a39a3663719dfc5e48c9d78431b1e45c2af9df538782bf199c189dabeac7680ada57dcec8eee91c4e3bf3bfa9af6ffde90cd1d249d1c6121d7b759a001b1";
        String secretSeed = "9a493664";
        SecretProofTransaction secretProoftx = SecretProofTransaction.create(
                new FakeDeadline(),
                HashType.SHA3_512,
                secret,
                secretSeed,
                NetworkType.MIJIN_TEST
        );
        SignedTransaction signedTransaction = secretProoftx.signWith(account);
        assertEquals("BF000000147827E5FDAB2201ABD3663964B0493166DA7DD18497718F53DF09AAFC55271B57A9E81B4E2F627FD19E9E9B77283D1620FB8E9E32BAC5AC265EB0B43C75B4071026D70E1954775749C6811084D6450A3184D977383F0E4282CD47118AF3775503904C430000000000000000010000000000000000B778A39A3663719DFC5E48C9D78431B1E45C2AF9DF538782BF199C189DABEAC7680ADA57DCEC8EEE91C4E3BF3BFA9AF6FFDE90CD1D249D1C6121D7B759A001B104009A493664", signedTransaction.getPayload());
        assertEquals("1169864A7290940854D87C8818625A7A498E6550D19F9BFAF5BA7BEFEB9206D0", signedTransaction.getHash());
    }

    @Test
    void shouldThrowErrorWhenSecretIsNotValid() {
        String proof = "B778A39A3663719DFC5E48C9D78431B1E45C2AF9DF538782BF199C189DABEAC7680ADA57DCEC8EEE91" +
                "C4E3BF3BFA9AF6FFDE90CD1D249D1C6121D7B759A001B1";
        assertThrows(IllegalArgumentException.class, ()-> {
            SecretProofTransaction secretProoftx = SecretProofTransaction.create(
                    new FakeDeadline(),
                    HashType.SHA3_512,
                    "non valid hash",
                    proof,
                    NetworkType.MIJIN_TEST
            );
        }, "not a valid secret");
    }
}

