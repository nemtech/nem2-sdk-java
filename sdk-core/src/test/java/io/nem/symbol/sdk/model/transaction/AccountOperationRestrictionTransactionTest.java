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
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountOperationRestrictionTransactionTest extends AbstractTransactionTester {

    static Account account = new Account(
        "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
        NetworkType.MIJIN_TEST);

    @Test
    void create() {
        List<TransactionType> additions = Collections.singletonList(TransactionType.SECRET_PROOF);
        List<TransactionType> deletions = Collections.singletonList(TransactionType.TRANSFER);

        AccountOperationRestrictionTransaction transaction =
            AccountOperationRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
                additions, deletions).deadline(new FakeDeadline()).build();
        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
            transaction.getRestrictionFlags());
        Assertions.assertEquals(additions, transaction.getRestrictionAdditions());
        Assertions.assertEquals(deletions, transaction.getRestrictionDeletions());
    }

    @Test
    void shouldGenerateBytes() {

        List<TransactionType> additions = Collections.singletonList(TransactionType.SECRET_PROOF);
        List<TransactionType> deletions = Collections.singletonList(TransactionType.TRANSFER);
        AccountOperationRestrictionTransaction transaction =
            AccountOperationRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
                additions, deletions).signer(account.getPublicAccount())
                .deadline(new FakeDeadline())
                .build();

        String expected = "8c0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e6000000000190504300000000000000000100000000000000020001010000000052425441";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "3c00000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001905043020001010000000052425441";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
