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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class QueryParamsTest {


    @Test
    void shouldCreateQueryParamsViaConstructor() {
        QueryParams queryParams = new QueryParams(15, "5A2139FC71C1B9000147D624", OrderBy.ASC);
        assertEquals(15, (int) queryParams.getPageSize());
        assertEquals("5A2139FC71C1B9000147D624", queryParams.getId());
        assertEquals(OrderBy.ASC, queryParams.getOrderBy());
    }

    @Test
    void shouldChangePageSizeTo10WhenSettingNegativeValue() {
        QueryParams queryParams = new QueryParams(-1, "5A2139FC71C1B9000147D624");
        assertEquals(10, (int) queryParams.getPageSize());
        assertEquals("5A2139FC71C1B9000147D624", queryParams.getId());
        assertNull(queryParams.getOrderBy());
    }

    @Test
    void shouldChangePageSizeTo10WhenSettingValue1000() {
        QueryParams queryParams = new QueryParams(1000, "5A2139FC71C1B9000147D624", OrderBy.DESC);
        assertEquals(10, (int) queryParams.getPageSize());
        assertEquals("5A2139FC71C1B9000147D624", queryParams.getId());
        assertEquals(OrderBy.DESC, queryParams.getOrderBy());
    }

}
