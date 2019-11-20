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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NamespaceRegistrationTransactionTest extends AbstractTransactionTester {

    private static Account account = new Account(
        "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
        NetworkType.MIJIN_TEST);

    private final String publicKey =
        "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf";
    private final Account testAccount =
        Account.createFromPrivateKey(publicKey, NetworkType.MIJIN_TEST);
    private final String generationHash =
        "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @Test
    void createANamespaceCreationRootNamespaceTransactionViaStaticConstructor() {
        NamespaceId namespaceId = NamespaceId.createFromName("root-test-namespace", networkType);
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createRootNamespace(
                NetworkType.MIJIN_TEST,
                "root-test-namespace",
                BigInteger.valueOf(1000)).build();

        SignedTransaction signedTransaction =
            namespaceRegistrationTransaction.signWith(testAccount, generationHash);

        assertEquals("1A000000E803000000000000CFCBE72D994BE69B0013726F6F742D746573742D6E616D657370616365",
            signedTransaction.getPayload().substring(248)
        );
        assertEquals(NetworkType.MIJIN_TEST, namespaceRegistrationTransaction.getNetworkType());
        assertTrue(1 == namespaceRegistrationTransaction.getVersion());
        assertTrue(
            LocalDateTime.now()
                .isBefore(namespaceRegistrationTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), namespaceRegistrationTransaction.getMaxFee());
        assertEquals("root-test-namespace", namespaceRegistrationTransaction.getNamespaceName());
        assertEquals(NamespaceRegistrationType.ROOT_NAMESPACE,
            namespaceRegistrationTransaction.getNamespaceRegistrationType());
        assertEquals(BigInteger.valueOf(1000),
            namespaceRegistrationTransaction.getDuration().get());
        assertEquals(
            namespaceId.getIdAsHex(),
            namespaceRegistrationTransaction.getNamespaceId().getIdAsHex());
    }

    @Test
    void createANamespaceCreationSubNamespaceTransactionViaStaticConstructor() {
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createSubNamespace(
                NetworkType.MIJIN_TEST,
                "root-test-namespace",
                NamespaceId.createFromName("parent-test-namespace", networkType)
            ).build();

        SignedTransaction signedTransaction =
            namespaceRegistrationTransaction.signWith(testAccount, generationHash);

        assertEquals(
            "1A0000004DF55E7F6D8FB7FF924207DF2CA1BBF30113726F6F742D746573742D6E616D657370616365",
            signedTransaction.getPayload().substring(248));
        assertEquals(NetworkType.MIJIN_TEST, namespaceRegistrationTransaction.getNetworkType());
        assertTrue(1 == namespaceRegistrationTransaction.getVersion());
        assertTrue(
            LocalDateTime.now()
                .isBefore(namespaceRegistrationTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), namespaceRegistrationTransaction.getMaxFee());
        assertEquals("root-test-namespace", namespaceRegistrationTransaction.getNamespaceName());
        assertEquals(NamespaceRegistrationType.SUB_NAMESPACE,
            namespaceRegistrationTransaction.getNamespaceRegistrationType());
        assertEquals(Optional.empty(), namespaceRegistrationTransaction.getDuration());
        assertEquals(
            new BigInteger("17562808385953809042"),
            namespaceRegistrationTransaction.getNamespaceId().getId());
    }

    @Test
    void createANamespaceCreationSubNamespaceWithParentIdTransactionViaStaticConstructor() {
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createSubNamespace(
                NetworkType.MIJIN_TEST,
                "root-test-namespace",
                NamespaceId.createFromId(new BigInteger("18426354100860810573"))
            ).build();

        SignedTransaction signedTransaction =
            namespaceRegistrationTransaction.signWith(testAccount, generationHash);

        assertEquals("1A0000004DF55E7F6D8FB7FF924207DF2CA1BBF30113726F6F742D746573742D6E616D657370616365",
            signedTransaction.getPayload().substring(248));
        assertEquals(NetworkType.MIJIN_TEST, namespaceRegistrationTransaction.getNetworkType());
        assertTrue(1 == namespaceRegistrationTransaction.getVersion());
        assertTrue(
            LocalDateTime.now()
                .isBefore(namespaceRegistrationTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), namespaceRegistrationTransaction.getMaxFee());
        assertEquals("root-test-namespace", namespaceRegistrationTransaction.getNamespaceName());
        assertEquals(NamespaceRegistrationType.SUB_NAMESPACE,
            namespaceRegistrationTransaction.getNamespaceRegistrationType());
        assertEquals(Optional.empty(), namespaceRegistrationTransaction.getDuration());
        assertEquals(
            new BigInteger("17562808385953809042"),
            namespaceRegistrationTransaction.getNamespaceId().getId());
        assertEquals(
            new BigInteger("18426354100860810573"),
            namespaceRegistrationTransaction.getParentId().get().getId());
    }

    @Test
    @DisplayName("Serialization root namespace")
    void serializationRootNamespace() {
        // Generated at nem2-library-js/test/transactions/RegisterNamespaceTransaction.spec.js
        NamespaceRegistrationTransaction transaction =
            NamespaceRegistrationTransactionFactory.createRootNamespace(
                NetworkType.MIJIN_TEST, "newnamespace", BigInteger.valueOf(10000))
                .deadline(new FakeDeadline()).signer(account.getPublicAccount())
                .build();

        String expected =
            "9e00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904e410000000000000000010000000000000010270000000000007ee9b3b8afdf53c0000c6e65776e616d657370616365";
        assertSerialization(expected, transaction);

        String expectedEmbedded =
            "4e000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904e4110270000000000007ee9b3b8afdf53c0000c6e65776e616d657370616365";
        assertEmbeddedSerialization(expectedEmbedded, transaction);

    }

    @Test
    @DisplayName("Serialization sub namespace")
    void serializationSubNamespace() {
        // Generated at nem2-library-js/test/transactions/RegisterNamespaceTransaction.spec.js
        NamespaceRegistrationTransaction transaction =
            NamespaceRegistrationTransactionFactory.createSubNamespace(
                NetworkType.MIJIN_TEST,
                "subnamespace",
                NamespaceId.createFromId(new BigInteger("4635294387305441662")))
                .signer(account.getPublicAccount())
                .deadline(new FakeDeadline()).build();

        String expected =
            "9e00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904e41000000000000000001000000000000007ee9b3b8afdf53400312981b7879a3f1010c7375626e616d657370616365";
        assertSerialization(expected, transaction);

        String expectedEmbedded =
            "4e000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904e417ee9b3b8afdf53400312981b7879a3f1010c7375626e616d657370616365";
        assertEmbeddedSerialization(expectedEmbedded, transaction);
    }
}
