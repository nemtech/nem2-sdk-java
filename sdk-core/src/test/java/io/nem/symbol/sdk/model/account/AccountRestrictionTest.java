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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionFlags;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AccountRestrictionTest {

  @Test
  void shouldCreateAccountRestrictionViaConstructor() {
    AccountRestriction accountRestriction =
        new AccountRestriction(
            AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            Arrays.asList("SDZWZJUAYNOWGBTCUDBY3SE5JF4NCC2RDM6SIGQM"));
    assertEquals(
        AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
        accountRestriction.getRestrictionFlags());
    assertEquals(1, accountRestriction.getValues().size());
  }
}
