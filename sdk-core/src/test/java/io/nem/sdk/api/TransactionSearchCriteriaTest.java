/*
 * Copyright 2019 NEM
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

package io.nem.sdk.api;

import io.nem.sdk.model.transaction.TransactionType;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link TransactionSearchCriteria}
 */
public class TransactionSearchCriteriaTest {

    @Test
    void shouldCreate() {
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        Assertions.assertNull(criteria.getId());
        Assertions.assertNull(criteria.getOrder());
        Assertions.assertNull(criteria.getPageSize());
        Assertions.assertNotNull(criteria.getTransactionTypes());
        Assertions.assertTrue(criteria.getTransactionTypes().isEmpty());
    }

    @Test
    void shouldSetValues() {
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();

        criteria.setId("theId");
        criteria.setOrder("TheOder");
        criteria.setPageSize(10);
        criteria.setTransactionTypes(
            Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION));

        Assertions.assertEquals("theId", criteria.getId());
        Assertions.assertEquals("TheOder", criteria.getOrder());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions
            .assertEquals(Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION),
                criteria.getTransactionTypes());
    }

    @Test
    void shouldUseBuilderMethods() {
        TransactionSearchCriteria criteria = new TransactionSearchCriteria();

        criteria.id("theId").order("TheOder").pageSize(10)
            .transactionTypes(Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION));

        Assertions.assertEquals("theId", criteria.getId());
        Assertions.assertEquals("TheOder", criteria.getOrder());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions
            .assertEquals(Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION),
                criteria.getTransactionTypes());
    }

}
