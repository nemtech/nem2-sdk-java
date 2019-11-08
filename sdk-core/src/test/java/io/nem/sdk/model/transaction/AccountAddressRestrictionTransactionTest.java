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
                AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
                additions, deletions).deadline(new FakeDeadline()).build();
        Assertions.assertEquals(AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
            transaction.getRestrictionType());
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
                AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
                additions, deletions).deadline(new FakeDeadline())
                .signer(account.getPublicAccount())
                .build();
        Assertions.assertEquals(AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
            transaction.getRestrictionType());

        String expected = "ba00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190504100000000000000000100000000000000010001010000000090018141b12dedd54d4e74b80f5c45266983131e03b5d7d54f90afb3793570d11c4dd7957eae7040e887572cebecee3e25e3";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "6a000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001905041010001010000000090018141b12dedd54d4e74b80f5c45266983131e03b5d7d54f90afb3793570d11c4dd7957eae7040e887572cebecee3e25e3";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
