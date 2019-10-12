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

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AccountLinkTransactionTest extends AbstractTransactionTester {

    static Account account;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
                NetworkType.MIJIN_TEST);
    }

    @Test
    void create() {
        AccountLinkTransaction transaction =
            AccountLinkTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                AccountLinkAction.LINK).deadline(new FakeDeadline()).build();
        assertEquals(AccountLinkAction.LINK, transaction.getLinkAction());
        assertEquals(
            "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
            transaction.getRemoteAccount().getPublicKey().toHex());
    }

    @Test
    void shouldGenerateBytes() {

        AccountLinkTransaction transaction =
            AccountLinkTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                AccountLinkAction.LINK).signer(account.getPublicAccount())
                .deadline(new FakeDeadline()).build();

        String expected = "9900000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904c41000000000000000001000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "490000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401904c419a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
