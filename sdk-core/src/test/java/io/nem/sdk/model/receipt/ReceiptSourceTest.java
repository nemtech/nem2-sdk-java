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

package io.nem.sdk.model.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.core.utils.ConvertUtils;
import org.junit.jupiter.api.Test;

public class ReceiptSourceTest {

    @Test
    void shouldCreateReceiptSource() {

        ReceiptSource source = new ReceiptSource(10, 2);
        assertEquals(10, source.getPrimaryId());
        assertEquals(2, source.getSecondaryId());

        assertEquals("0A00000002000000", ConvertUtils.toHex(source.serialize()));
    }
}
