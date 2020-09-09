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

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link AccountType}
 *
 * @author Fernando Boucquez
 */
public class AccountTypeTest {

  @Test
  public void rawValueOf() {
    Assertions.assertEquals(AccountType.MAIN, AccountType.rawValueOf(AccountType.MAIN.getValue()));

    Arrays.stream(AccountType.values())
        .forEach(t -> Assertions.assertEquals(t, AccountType.rawValueOf(t.getValue())));
  }

  @Test
  public void rawValueOfNotExist() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> AccountType.rawValueOf(-1));
  }
}
