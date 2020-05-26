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

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PageTest {

    @Test
    void testConstructor() {
        List<String> data = Arrays.asList("a", "b", "c");
        Page<String> page = new Page<>(data, 1, 2, 3, 4);
        Assertions.assertEquals(1, page.getPageNumber());
        Assertions.assertEquals(2, page.getPageSize());
        Assertions.assertEquals(3, page.getTotalEntries());
        Assertions.assertEquals(4, page.getTotalPages());
        Assertions.assertEquals(data, page.getData());
    }

}
