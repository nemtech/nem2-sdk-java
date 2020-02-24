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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Tests for the {@link AccountMetadataTransaction} and the factory.
 **/
public class AccountMetadataTransactionTest extends AbstractTransactionTester {

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
                BigInteger.TEN, "123BAC").valueSizeDelta(10)
                .deadline(new FakeDeadline()).build();

        assertEquals("123BAC", transaction.getValue());
        assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
        assertEquals(10, transaction.getValueSizeDelta());
        assertEquals(BigInteger.TEN, transaction.getScopedMetadataKey());

        assertEquals(account.getPublicKey(),
            transaction.getTargetAccount().getPublicKey().toHex());

    }

    @Test
    void shouldGenerateBytes() {
        AccountMetadataTransaction transaction =
            AccountMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                BigInteger.TEN, "123BAC").valueSizeDelta(10)
                .signer(account.getPublicAccount())
                .deadline(new FakeDeadline()).build();

        String expected = "b20000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e6000000000190444100000000000000000100000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60a000000000000000a000600313233424143";

        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "6200000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001904441f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60a000000000000000a000600313233424143";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
