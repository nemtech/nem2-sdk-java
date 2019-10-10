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

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashLockTransactionTest {

    static Account account;
    static String generationHash;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST);
        generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        String expected =
            "b0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019048410000000000000000010000000000000044b262c46ceabb85809698000000000064000000000000008498b38d89c1dc8a448ea5824938ff828926cd9f7747b1844b59b4b6807e878b";
        SignedTransaction signedTransaction =
            new SignedTransaction(
                "payload",
                "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
                TransactionType.AGGREGATE_BONDED);
        HashLockTransaction transaction =
            HashLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).deadline(new FakeDeadline()).build();
        byte[] actual = transaction.generateBytes();
        assertEquals(expected, ConvertUtils.toHex(actual));

    }

    @Test
    @DisplayName("To aggregate")
    void toAggregate() {
        String expected =
            "600000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240190484144b262c46ceabb85809698000000000064000000000000008498b38d89c1dc8a448ea5824938ff828926cd9f7747b1844b59b4b6807e878b";

        SignedTransaction signedTransaction =
            new SignedTransaction(
                "payload",
                "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
                TransactionType.AGGREGATE_BONDED);
        HashLockTransaction transaction =
            HashLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).deadline(new FakeDeadline()).build();
        byte[] actual =
            transaction
                .toAggregate(
                    new PublicAccount(
                        "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                        NetworkType.MIJIN_TEST))
                .serialize();
        assertEquals(expected, ConvertUtils.toHex(actual));

    }

    @Test
    void serializeAndSignTransaction() {
        SignedTransaction signedTransaction =
            new SignedTransaction(
                "payload",
                "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
                TransactionType.AGGREGATE_BONDED);
        HashLockTransaction transaction =
            HashLockTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).deadline(new FakeDeadline()).build();
        SignedTransaction lockFundsTransactionSigned = transaction
            .signWith(account, generationHash);

        String payload = lockFundsTransactionSigned.getPayload();
        assertEquals(
            "44B262C46CEABB85809698000000000064000000000000008498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
            payload.substring(240)
        );
        assertEquals(
            "11533C71C8A6F9A86E041AD6EE3B1CBA81FA9E7DDF93AEFB9EB7ACA153BB3E2C",
            lockFundsTransactionSigned.getHash());

    }

    @Test
    void shouldThrowExceptionWhenSignedTransactionIsNotTypeAggregateBonded() {

        SignedTransaction signedTransaction =
            new SignedTransaction(
                "payload",
                "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
                TransactionType.TRANSFER);
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                HashLockTransactionFactory.create(
                    NetworkType.MIJIN_TEST,
                    NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                    BigInteger.valueOf(100),
                    signedTransaction).deadline(
                    new FakeDeadline()).build();
            },
            "Signed transaction must be Aggregate Bonded Transaction");
    }
}
