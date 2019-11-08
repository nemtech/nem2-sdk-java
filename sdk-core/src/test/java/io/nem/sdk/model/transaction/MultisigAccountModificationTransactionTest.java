/*
 * Copyright 2018 NEM
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
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
        // Generated at nem2-library-js/test/transactions/ModifyMultisigAccountTransaction.spec.js
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
            "c800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190554100000000000000000100000000000000010201010000000068b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b11168b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222";

        assertSerialization(expected, transaction);

        String expectedEmbedded =
            "78000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001905541010201010000000068b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b11168b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222";

        assertEmbeddedSerialization(expectedEmbedded, transaction);
    }
}
