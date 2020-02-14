/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
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

        String expected = "ba0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e600000000019050410000000000000000010000000000000001000101000000009083025ff3a8ab5ad104631fb370f290004952cd1fddc4c95d90b387a39c0e4607db7056eeaaf0a0ef43b45c667eb790ffea";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "6a00000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e6000000000190504101000101000000009083025ff3a8ab5ad104631fb370f290004952cd1fddc4c95d90b387a39c0e4607db7056eeaaf0a0ef43b45c667eb790ffea";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
