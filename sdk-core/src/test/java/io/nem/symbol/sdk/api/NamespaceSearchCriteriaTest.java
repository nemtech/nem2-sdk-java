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

package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.namespace.AliasType;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link NamespaceSearchCriteria}
 */
class NamespaceSearchCriteriaTest {

    @Test
    void shouldCreate() {
        NamespaceSearchCriteria criteria = new NamespaceSearchCriteria();
        Assertions.assertNull(criteria.getId());
        Assertions.assertNull(criteria.getOrder());
        Assertions.assertNull(criteria.getPageSize());
        Assertions.assertNull(criteria.getPageNumber());
        Assertions.assertNull(criteria.getOffset());
        Assertions.assertNull(criteria.getLevel0());
        Assertions.assertNull(criteria.getAliasType());
        Assertions.assertNull(criteria.getRegistrationType());
    }

    @Test
    void shouldSetValues() {

        NamespaceSearchCriteria criteria = new NamespaceSearchCriteria();
        criteria.setOrder(OrderBy.DESC);
        criteria.setPageSize(10);
        criteria.setPageNumber(5);
        criteria.setId("a");
        criteria.setOffset("abc");
        criteria.setAliasType(AliasType.ADDRESS);
        criteria.setLevel0("someLevel");
        criteria.setRegistrationType(NamespaceRegistrationType.ROOT_NAMESPACE);

        Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
        Assertions.assertEquals("a", criteria.getId());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions.assertEquals(5, criteria.getPageNumber());
        Assertions.assertEquals("abc", criteria.getOffset());
        Assertions.assertEquals("someLevel", criteria.getLevel0());
        Assertions.assertEquals(AliasType.ADDRESS, criteria.getAliasType());
        Assertions.assertEquals(NamespaceRegistrationType.ROOT_NAMESPACE, criteria.getRegistrationType());
    }

    @Test
    void shouldUseBuilderMethods() {

        NamespaceSearchCriteria criteria = new NamespaceSearchCriteria();
        criteria.order(OrderBy.DESC);
        criteria.pageSize(10);
        criteria.setId("a");
        criteria.pageNumber(5);
        criteria.offset("abc");
        criteria.aliasType(AliasType.ADDRESS);
        criteria.level0("someLevel");
        criteria.registrationType(NamespaceRegistrationType.ROOT_NAMESPACE);

        Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
        Assertions.assertEquals("a", criteria.getId());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions.assertEquals(5, criteria.getPageNumber());
        Assertions.assertEquals("abc", criteria.getOffset());
        Assertions.assertEquals("someLevel", criteria.getLevel0());
        Assertions.assertEquals(AliasType.ADDRESS, criteria.getAliasType());
        Assertions.assertEquals(NamespaceRegistrationType.ROOT_NAMESPACE, criteria.getRegistrationType());
    }

    @Test
    void shouldBeEquals() {

        NamespaceSearchCriteria criteria1 = new NamespaceSearchCriteria();
        criteria1.order(OrderBy.DESC);
        criteria1.pageSize(10);
        criteria1.pageNumber(5);
        criteria1.offset("abc");
        criteria1.setId("a");
        criteria1.aliasType(AliasType.ADDRESS);
        criteria1.level0("someLevel");
        criteria1.registrationType(NamespaceRegistrationType.ROOT_NAMESPACE);

        NamespaceSearchCriteria criteria2 = new NamespaceSearchCriteria();
        criteria2.order(OrderBy.DESC);
        criteria2.pageSize(10);
        criteria2.pageNumber(5);
        criteria2.offset("abc");
        criteria2.setId("a");
        criteria2.aliasType(AliasType.ADDRESS);
        criteria2.level0("someLevel");
        criteria2.registrationType(NamespaceRegistrationType.ROOT_NAMESPACE);

        Assertions.assertEquals(new NamespaceSearchCriteria(), new NamespaceSearchCriteria());
        Assertions.assertEquals(criteria1, criteria2);
        Assertions.assertEquals(criteria1, criteria1);
        Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

        criteria1.pageNumber(30);
        Assertions.assertNotEquals(criteria1, criteria2);
        Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

        criteria2.pageNumber(100);
        Assertions.assertNotEquals(criteria1, criteria2);
        Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

        Assertions.assertNotEquals("ABC", criteria2);
    }


}
