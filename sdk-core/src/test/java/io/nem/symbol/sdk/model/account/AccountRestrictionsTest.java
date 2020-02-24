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

import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AccountRestrictionsTest {

    @Test
    void shouldCreateAccountRestrictionsViaConstructor() {
        Address address =
            Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");
        AccountRestriction accountRestriction =
            new AccountRestriction(
                AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
                Arrays.asList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        AccountRestrictions accountRestrictions =
            new AccountRestrictions(address, Arrays.asList(accountRestriction));

        assertEquals(address, accountRestrictions.getAddress());
        assertEquals(1, accountRestrictions.getRestrictions().size());
        assertEquals(
            AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            accountRestrictions.getRestrictions().get(0).getRestrictionFlags());
    }
}
