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
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link NamespaceMetadataTransaction} and the factory.
 **/
public class NamespaceMetadataTransactionTest extends AbstractTransactionTester {

    private static Account account = new Account(
        "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
        NetworkType.MIJIN_TEST);

    @Test
    void shouldBuild() {
        NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(1000));
        NamespaceMetadataTransaction transaction =
            NamespaceMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                namespaceId, BigInteger.TEN, "ABC123").valueSizeDelta(10)
                .deadline(new FakeDeadline()).build();
        assertEquals("ABC123", transaction.getValue());
        assertEquals(namespaceId, transaction.getTargetNamespaceId());
        assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
        assertEquals(10, transaction.getValueSizeDelta());
        assertEquals(BigInteger.TEN, transaction.getScopedMetadataKey());

        assertEquals(account.getPublicKey(),
            transaction.getTargetAccount().getPublicKey().toHex());
    }

    @Test
    void shouldGenerateBytes() {
        NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(1000));
        NamespaceMetadataTransaction transaction =
            NamespaceMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                namespaceId, BigInteger.TEN, "ABC123").valueSizeDelta(10)
                .signer(account.getPublicAccount())
                .deadline(new FakeDeadline()).build();

        String expectedHash = "ba0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e6000000000190444300000000000000000100000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60a00000000000000e8030000000000000a000600414243313233";
        assertSerialization(expectedHash, transaction);

        String expectedEmbeddedHash = "6a00000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001904443f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60a00000000000000e8030000000000000a000600414243313233";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
