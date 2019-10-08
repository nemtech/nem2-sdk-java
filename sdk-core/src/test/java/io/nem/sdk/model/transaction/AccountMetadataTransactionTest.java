/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Tests for the {@link AccountMetadataTransaction} and the factory.
 **/
public class AccountMetadataTransactionTest {

    static Account account;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
                NetworkType.MIJIN_TEST);
    }

    @Test
    void shouldBuild() {
        AccountMetadataTransaction transaction =
            AccountMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                BigInteger.TEN, "123BAC").valueSize(20).valueSizeDelta(10)
                .deadline(new FakeDeadline()).build();

        assertEquals("123BAC", transaction.getValue());
        assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
        assertEquals(10, transaction.getValueSizeDelta());
        assertEquals(20, transaction.getValueSize());
        assertEquals(BigInteger.TEN, transaction.getScopedMetadataKey());

        assertEquals(account.getPublicKey(),
            transaction.getTargetAccount().getPublicKey().toString());

    }

    @Test
    void shouldGenerateBytes() {
        AccountMetadataTransaction transaction =
            AccountMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                BigInteger.TEN, "123BAC").valueSize(20).valueSizeDelta(10)
                .signer(account.getPublicAccount())
                .deadline(new FakeDeadline()).build();

        String expected = "aa00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904441000000000000000001000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240a000000000000000a000600313233424143";
        Assertions.assertEquals(expected, ConvertUtils.toHex(transaction.generateBytes()));

        String expectedEmbeddedHash = "5a0000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24019044419a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240a000000000000000a000600313233424143";
        Assertions.assertEquals(expectedEmbeddedHash,
            ConvertUtils.toHex(transaction.generateEmbeddedBytes()));
    }
}
