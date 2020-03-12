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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultisigAccountModificationTransactionTest extends AbstractTransactionTester {

    private static Account account = new Account(
        "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
        NetworkType.MIJIN_TEST);

    @Test
    void createAMultisigModificationTransactionViaConstructor() {
        List<PublicAccount> additions = Collections.singletonList(
            PublicAccount.createFromPublicKey(
                "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b111",
                NetworkType.MIJIN_TEST));
        List<PublicAccount> deletions = Collections.singletonList(
            PublicAccount.createFromPublicKey(
                "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222",
                NetworkType.MIJIN_TEST));
        MultisigAccountModificationTransaction multisigAccountModificationTransaction =
            MultisigAccountModificationTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                (byte) 2,
                (byte) 1,
                additions,
                deletions
            ).build();

        assertEquals(NetworkType.MIJIN_TEST,
            multisigAccountModificationTransaction.getNetworkType());
        assertTrue(1 == multisigAccountModificationTransaction.getVersion());
        assertTrue(
            LocalDateTime.now()
                .isBefore(multisigAccountModificationTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), multisigAccountModificationTransaction.getMaxFee());
        assertEquals(2, multisigAccountModificationTransaction.getMinApprovalDelta());
        assertEquals(1, multisigAccountModificationTransaction.getMinRemovalDelta());
        assertEquals(additions, multisigAccountModificationTransaction.getPublicKeyAdditions());
        assertEquals(deletions, multisigAccountModificationTransaction.getPublicKeyDeletions());

    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        // Generated at symbol-library-js/test/transactions/ModifyMultisigAccountTransaction.spec.js
        List<PublicAccount> additions = Collections.singletonList(
            PublicAccount.createFromPublicKey(
                "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b111",
                NetworkType.MIJIN_TEST));
        List<PublicAccount> deletions = Collections.singletonList(
            PublicAccount.createFromPublicKey(
                "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222",
                NetworkType.MIJIN_TEST));
        MultisigAccountModificationTransaction transaction =
            MultisigAccountModificationTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                (byte) 2,
                (byte) 1,
                additions,
                deletions
            ).signer(account.getPublicAccount()).deadline(new FakeDeadline()).build();

        String expected =
            "c80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e6000000000190554100000000000000000100000000000000010201010000000068b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b11168b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222";

        assertSerialization(expected, transaction);

        String expectedEmbedded =
            "7800000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001905541010201010000000068b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b11168b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222";

        assertEmbeddedSerialization(expectedEmbedded, transaction);
    }
}
