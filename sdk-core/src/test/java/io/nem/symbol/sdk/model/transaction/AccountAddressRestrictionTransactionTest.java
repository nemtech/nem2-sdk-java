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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountAddressRestrictionTransactionTest extends AbstractTransactionTester {

    static Account account =
        new Account(
            "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
            NetworkType.MIJIN_TEST);

    static Account account2 =
        new Account(
            "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e022222",
            NetworkType.MIJIN_TEST);

    @Test
    void create() {

        List<UnresolvedAddress> additions = Collections.singletonList(account.getAddress());
        List<UnresolvedAddress> deletions = Collections.singletonList(account2.getAddress());

        AccountAddressRestrictionTransaction transaction =
            AccountAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
                additions, deletions).deadline(new FakeDeadline()).build();
        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            transaction.getRestrictionFlags());
        Assertions.assertEquals(additions, transaction.getRestrictionAdditions());
        Assertions.assertEquals(deletions, transaction.getRestrictionDeletions());
    }

    @Test
    void shouldGenerateBytes() {

        List<UnresolvedAddress> additions = Collections.singletonList(account.getAddress());
        List<UnresolvedAddress> deletions = Collections.singletonList(account2.getAddress());

        AccountAddressRestrictionTransaction transaction =
            AccountAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
                additions, deletions).deadline(new FakeDeadline())
                .signer(account.getPublicAccount())
                .build();
        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            transaction.getRestrictionFlags());

        System.out.println(transaction.getSize());
        String expected = "B80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E600000000019050410000000000000000010000000000000001000101000000009083025FF3A8AB5AD104631FB370F290004952CD1FDDC4C990B387A39C0E4607DB7056EEAAF0A0EF43B45C667EB790FF";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "6800000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E6000000000190504101000101000000009083025FF3A8AB5AD104631FB370F290004952CD1FDDC4C990B387A39C0E4607DB7056EEAAF0A0EF43B45C667EB790FF";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
