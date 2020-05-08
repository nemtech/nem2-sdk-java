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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.network.NetworkType;
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
                LinkAction.LINK).deadline(new FakeDeadline()).build();
        assertEquals(LinkAction.LINK, transaction.getLinkAction());
        assertEquals(
            "F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E6",
            transaction.getRemoteAccount().getPublicKey().toHex());
    }

    @Test
    void shouldGenerateBytes() {

        AccountLinkTransaction transaction =
            AccountLinkTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                LinkAction.LINK).signer(account.getPublicAccount())
                .deadline(new FakeDeadline()).build();

        String expected = "A10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001904C4100000000000000000100000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E601";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "5100000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001904c41f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e601";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
