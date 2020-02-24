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

package io.nem.symbol.sdk.model.namespace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class NamespaceNameTest {

    @Test
    void createANamespaceName() {
        NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
        NamespaceName namespaceName = new NamespaceName(namespaceId, "nem");

        assertEquals(namespaceId, namespaceName.getNamespaceId());
        assertEquals("nem", namespaceName.getName());
        assertFalse(namespaceName.getParentId().isPresent());
    }

    @Test
    void createANamespaceNameWithParentId() {
        NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
        NamespaceId parentId = NamespaceId.createFromId(new BigInteger("-3087871471161192663"));
        NamespaceName namespaceName = new NamespaceName(namespaceId, "nem", parentId);

        assertEquals(namespaceId, namespaceName.getNamespaceId());
        assertEquals("nem", namespaceName.getName());
        assertEquals(parentId, namespaceName.getParentId().get());
    }
}
