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
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountAddressRestrictionTransactionTest extends AbstractTransactionTester {

    static Account account =
        new Account(
            "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
            NetworkType.MIJIN_TEST);


    @Test
    void create() {

        final List<AccountRestrictionModification<Address>> modifications = new ArrayList<>();
        AccountRestrictionModification<Address> modification = AccountRestrictionModification
            .createForAddress(AccountRestrictionModificationAction.ADD, account.getAddress());
        modifications.add(modification);
        AccountAddressRestrictionTransaction transaction =
            AccountAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
                modifications).deadline(new FakeDeadline()).build();
        Assertions.assertEquals(AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
            transaction.getRestrictionType());
        Assertions.assertEquals(modifications, transaction.getModifications());
        Assertions.assertEquals(AccountRestrictionModificationAction.ADD,
            modification.getModificationAction());
        Assertions.assertEquals(account.getAddress(),
            modification.getValue());
    }

    @Test
    void shouldGenerateBytes() {

        final List<AccountRestrictionModification<Address>> modifications = new ArrayList<>();
        modifications.add(AccountRestrictionModification
            .createForAddress(AccountRestrictionModificationAction.ADD, account.getAddress()));
        AccountAddressRestrictionTransaction transaction =
            AccountAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
                modifications).deadline(new FakeDeadline()).signer(account.getPublicAccount())
                .build();
        Assertions.assertEquals(AccountRestrictionType.ALLOW_INCOMING_ADDRESS,
            transaction.getRestrictionType());
        Assertions.assertEquals(modifications, transaction.getModifications());

        String expected = "94000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019050410000000000000000010000000000000001010190018141b12dedd54d4e74b80f5c45266983131e03b5d7d54f";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "440000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240190504101010190018141b12dedd54d4e74b80f5c45266983131e03b5d7d54f";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
