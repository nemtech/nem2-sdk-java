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

import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.HexEncoder;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceType;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NamespaceRegistrationTransactionTest {

    static SignSchema signSchema = SignSchema.DEFAULT;

    private final String publicKey =
        "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf";
    private final Account testAccount =
        Account.createFromPrivateKey(publicKey, NetworkType.MIJIN_TEST, signSchema);
    private final String generationHash =
        "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @Test
    void createANamespaceCreationRootNamespaceTransactionViaStaticConstructor() {
        NamespaceId namespaceId = NamespaceId.createFromName("root-test-namespace");
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransaction.createRootNamespace(
                new Deadline(2, ChronoUnit.HOURS),
                BigInteger.ZERO,
                "root-test-namespace",
                BigInteger.valueOf(1000),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            namespaceRegistrationTransaction.signWith(testAccount, generationHash);

        assertEquals("00E803000000000000CFCBE72D994BE69B13726F6F742D746573742D6E616D657370616365",
            signedTransaction.getPayload().substring(240)
        );
        assertEquals(NetworkType.MIJIN_TEST, namespaceRegistrationTransaction.getNetworkType());
        assertTrue(1 == namespaceRegistrationTransaction.getVersion());
        assertTrue(
            LocalDateTime.now()
                .isBefore(namespaceRegistrationTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), namespaceRegistrationTransaction.getFee());
        assertEquals("root-test-namespace", namespaceRegistrationTransaction.getNamespaceName());
        assertEquals(NamespaceType.ROOT_NAMESPACE, namespaceRegistrationTransaction.getNamespaceType());
        assertEquals(BigInteger.valueOf(1000), namespaceRegistrationTransaction.getDuration().get());
        assertEquals(
            namespaceId.getIdAsHex(), namespaceRegistrationTransaction.getNamespaceId().getIdAsHex());
    }

    @Test
    void createANamespaceCreationSubNamespaceTransactionViaStaticConstructor() {
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransaction.createSubNamespace(
                new Deadline(2, ChronoUnit.HOURS),
                BigInteger.ZERO,
                "root-test-namespace",
                "parent-test-namespace",
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            namespaceRegistrationTransaction.signWith(testAccount, generationHash);

        assertEquals(
            "014DF55E7F6D8FB7FF924207DF2CA1BBF313726F6F742D746573742D6E616D657370616365",
            signedTransaction.getPayload().substring(240));
        assertEquals(NetworkType.MIJIN_TEST, namespaceRegistrationTransaction.getNetworkType());
        assertTrue(1 == namespaceRegistrationTransaction.getVersion());
        assertTrue(
            LocalDateTime.now()
                .isBefore(namespaceRegistrationTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), namespaceRegistrationTransaction.getFee());
        assertEquals("root-test-namespace", namespaceRegistrationTransaction.getNamespaceName());
        assertEquals(NamespaceType.SUB_NAMESPACE, namespaceRegistrationTransaction.getNamespaceType());
        assertEquals(Optional.empty(), namespaceRegistrationTransaction.getDuration());
        assertEquals(
            new BigInteger("-883935687755742574"),
            namespaceRegistrationTransaction.getNamespaceId().getId());
    }

    @Test
    void createANamespaceCreationSubNamespaceWithParentIdTransactionViaStaticConstructor() {
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransaction.createSubNamespace(
                new Deadline(2, ChronoUnit.HOURS),
                BigInteger.ZERO,
                "root-test-namespace",
                NamespaceId.createFromId(new BigInteger("18426354100860810573")),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            namespaceRegistrationTransaction.signWith(testAccount, generationHash);

        assertEquals("014DF55E7F6D8FB7FF924207DF2CA1BBF313726F6F742D746573742D6E616D657370616365",
            signedTransaction.getPayload().substring(240));
        assertEquals(NetworkType.MIJIN_TEST, namespaceRegistrationTransaction.getNetworkType());
        assertTrue(1 == namespaceRegistrationTransaction.getVersion());
        assertTrue(
            LocalDateTime.now()
                .isBefore(namespaceRegistrationTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), namespaceRegistrationTransaction.getFee());
        assertEquals("root-test-namespace", namespaceRegistrationTransaction.getNamespaceName());
        assertEquals(NamespaceType.SUB_NAMESPACE, namespaceRegistrationTransaction.getNamespaceType());
        assertEquals(Optional.empty(), namespaceRegistrationTransaction.getDuration());
        assertEquals(
            new BigInteger("-883935687755742574"),
            namespaceRegistrationTransaction.getNamespaceId().getId());
        assertEquals(
            new BigInteger("18426354100860810573"),
            namespaceRegistrationTransaction.getParentId().get().getId());
    }

    @Test
    @DisplayName("Serialization root namespace")
    void serializationRootNamespace() {
        // Generated at nem2-library-js/test/transactions/RegisterNamespaceTransaction.spec.js
        String expected =
            "9600000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904e41000000000000000001000000000000000010270000000000007ee9b3b8afdf53c00c6e65776e616d657370616365";
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransaction.createRootNamespace(
                new FakeDeadline(),
                BigInteger.ZERO,
                "newnamespace",
                BigInteger.valueOf(10000),
                NetworkType.MIJIN_TEST);

        byte[] actual = namespaceRegistrationTransaction.generateBytes();
        assertEquals(expected, HexEncoder.getString(actual));
    }

    @Test
    @DisplayName("Serialization sub namespace")
    void serializationSubNamespace() {
        // Generated at nem2-library-js/test/transactions/RegisterNamespaceTransaction.spec.js
        String expected =
            "9600000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904e4100000000000000000100000000000000017ee9b3b8afdf53400312981b7879a3f10c7375626e616d657370616365";
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransaction.createSubNamespace(
                new FakeDeadline(),
                BigInteger.ZERO,
                "subnamespace",
                NamespaceId.createFromId(new BigInteger("4635294387305441662")),
                NetworkType.MIJIN_TEST);

        byte[] actual = namespaceRegistrationTransaction.generateBytes();
        assertEquals(expected, HexEncoder.getString(actual));
    }
}
