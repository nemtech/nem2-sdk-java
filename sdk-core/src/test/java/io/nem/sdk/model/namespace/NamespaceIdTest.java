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

package io.nem.sdk.model.namespace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NamespaceIdTest {

    static Stream<Arguments> provider() {
        return Stream.of(
            Arguments.of("84b3552d375ffa4b", -8884663987180930485L),
            Arguments.of("f8495aee892fa108", -555813098453229304L),
            Arguments.of("abaef4e86505811f", -6075649568311770849L),
            Arguments.of("aeb8c92b0a1c2d55", -5856710128704934571L),
            Arguments.of("90e09ad44014cabf", -8007229901065893185L),
            Arguments.of("ab114281960bf1cc", -6120037294284213812L)
        );
    }

    @ParameterizedTest
    @MethodSource("provider")
    void createNamespaceIdsFromUInt64LowerAndHigher(
        String expectedIdAsHex, long idAsLong) {
        NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(idAsLong));
        assertEquals(expectedIdAsHex, namespaceId.getIdAsHex());
    }

    @Test
    void createANamespaceIdFromRootNamespaceNameViaConstructor() {
        NamespaceId namespaceId = NamespaceId.createFromName("nem");
        assertEquals(namespaceId.getId(), new BigInteger("9562080086528621131"));
        assertEquals("nem", namespaceId.getFullName().get());
    }


    @Test
    void createANamespaceIdFromSubNamespacePathViaConstructor() {
        NamespaceId test = NamespaceId.createFromName("subnem");
        NamespaceId namespaceId = NamespaceId.createFromName("nem.subnem");
        assertEquals(new BigInteger("16440672666685223858"), namespaceId.getId());
        assertEquals("nem.subnem", namespaceId.getFullName().get());
    }

    @Test
    void createANamespaceIdFromSubNamespaceNameAndParentNamespaceNameViaConstructor() {
        NamespaceId namespaceId = NamespaceId.createFromNameAndParentName("subnem", "nem");
        assertEquals(new BigInteger("16440672666685223858"), namespaceId.getId());
        assertEquals("nem.subnem", namespaceId.getFullName().get());
    }

    @Test
    void createANamespaceIdFromSubNamespaceNameAndParentNamespaceName2ViaConstructor() {
        NamespaceId namespaceId = NamespaceId
            .createFromNameAndParentName("subsubnem", "nem.subnem");
        NamespaceId parentId = NamespaceId.createFromNameAndParentName("subnem", "nem");
        NamespaceId namespaceId2 = NamespaceId
            .createFromNameAndParentId("subsubnem", parentId.getId());

        assertEquals(new BigInteger("10592058992486201054"), namespaceId.getId());
        assertEquals("nem.subnem.subsubnem", namespaceId.getFullName().get());
        assertEquals(namespaceId2.getId(), namespaceId.getId());
    }

    @Test
    void createASubNamespaceIdFromSubNamespaceNameAndParentIdViaConstructor() {
        NamespaceId namespaceId = NamespaceId
            .createFromNameAndParentId("subnem", new BigInteger("-8884663987180930485"));
        assertEquals(new BigInteger("16440672666685223858"), namespaceId.getId());
        assertEquals("subnem", namespaceId.getFullName().get());
    }

    @Test
    void createNamespacePathArray() {
        List<BigInteger> path = NamespaceId.getNamespacePath("nem.subnem");
        assertEquals(new BigInteger("9562080086528621131"), path.get(0));
        assertEquals(new BigInteger("16440672666685223858"), path.get(1));
    }

    @Test
    void createANamespaceIdFromIdViaConstructor() {
        NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
        assertEquals(namespaceId.getId(), new BigInteger("-8884663987180930485"));
        assertFalse(namespaceId.getFullName().isPresent());
    }

    @Test
    void shouldCompareNamespaceIdsForEquality() {
        NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
        NamespaceId namespaceId2 = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
        assertTrue(namespaceId.equals(namespaceId2));
    }

    @Test
    public void shouldCompareNamespaceIdsForEqualityUsingNames() {

        NamespaceId test = NamespaceId.createFromName("subnem");
        NamespaceId test2 = NamespaceId.createFromName("subnem");
        NamespaceId test3 = NamespaceId.createFromName("another");
        Assertions.assertEquals(test, test2);
        Assertions.assertNotEquals(test, test3);
        Assertions.assertNotEquals("NotANamespaceId", test3);
        Assertions.assertNotEquals(test3, "NotANamespaceId");

    }

    @Test
    public void shouldCompareNamespaceIdsHashCode() {

        NamespaceId test = NamespaceId.createFromName("subnem");
        NamespaceId test2 = NamespaceId.createFromName("subnem");
        NamespaceId test3 = NamespaceId.createFromName("another");
        Assertions.assertEquals(test.hashCode(), test2.hashCode());
        Assertions.assertNotEquals(test.hashCode(), test3.hashCode());

    }
}
