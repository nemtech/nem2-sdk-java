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
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link MosaicAliasTransaction} and the factory.
 **/
public class MosaicAliasTransactionTest extends AbstractTransactionTester {

    private static Account account =
        new Account(
            "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
            NetworkType.MIJIN_TEST);

    @Test
    void shouldBuild() {
        MosaicId mosaicId = new MosaicId(BigInteger.TEN);
        NamespaceId namespaceId = NamespaceId.createFromName("anamespaced");
        MosaicAliasTransaction transaction =
            MosaicAliasTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AliasAction.LINK,
                namespaceId,
                mosaicId
            ).build();

        assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
        assertEquals(AliasAction.LINK, transaction.getAliasAction());
        assertEquals(mosaicId, transaction.getMosaicId());
        assertEquals(namespaceId, transaction.getNamespaceId());

    }

    @Test
    void serialize() {
        MosaicId mosaicId = new MosaicId(BigInteger.TEN);
        NamespaceId namespaceId = NamespaceId.createFromName("anamespaced");
        MosaicAliasTransaction transaction =
            MosaicAliasTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AliasAction.LINK,
                namespaceId,
                mosaicId
            ).signer(account.getPublicAccount()).deadline(new FakeDeadline()).build();

        String expectedHash = "8900000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904e430000000000000000010000000000000001a487791451fdf1b60a00000000000000";
        assertSerialization(expectedHash, transaction);

        String expectedEmbeddedHash = "390000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401904e4301a487791451fdf1b60a00000000000000";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);

    }
}
