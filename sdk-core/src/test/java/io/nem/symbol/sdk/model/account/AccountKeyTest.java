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

package io.nem.symbol.sdk.model.account;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for account Key.
 */
public class AccountKeyTest {

    @Test
    void constructor(){
        AccountKey accountKey = new AccountKey(KeyType.LINKED,"ABC");
        Assertions.assertEquals(KeyType.LINKED, accountKey.getKeyType());
        Assertions.assertEquals("ABC", accountKey.getKey());
    }
}
