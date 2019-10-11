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
import java.util.Arrays;
import java.util.Collections;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultisigAccountModificationTransactionTest extends AbstractTransactionTester {

    private static Account account = new Account(
        "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
        NetworkType.MIJIN_TEST);

    @Test
    void createAMultisigModificationTransactionViaConstructor() {
        MultisigAccountModificationTransaction multisigAccountModificationTransaction =
            MultisigAccountModificationTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                (byte) 2,
                (byte) 1,
                Collections.singletonList(
                    new MultisigCosignatoryModification(
                        CosignatoryModificationActionType.ADD,
                        PublicAccount.createFromPublicKey(
                            "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763",
                            NetworkType.MIJIN_TEST)))
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
        assertEquals(
            "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763".toUpperCase(),
            multisigAccountModificationTransaction
                .getModifications()
                .get(0)
                .getCosignatoryPublicAccount()
                .getPublicKey()
                .toHex()
                .toUpperCase());
        assertEquals(
            CosignatoryModificationActionType.ADD,
            multisigAccountModificationTransaction.getModifications().get(0)
                .getModificationAction());
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/ModifyMultisigAccountTransaction.spec.js
        MultisigAccountModificationTransaction transaction =
            MultisigAccountModificationTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                (byte) 2,
                (byte) 1,
                Arrays.asList(
                    new MultisigCosignatoryModification(
                        CosignatoryModificationActionType.ADD,
                        PublicAccount.createFromPublicKey(
                            "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763",
                            NetworkType.MIJIN_TEST)),
                    new MultisigCosignatoryModification(
                        CosignatoryModificationActionType.ADD,
                        PublicAccount.createFromPublicKey(
                            "cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb",
                            NetworkType.MIJIN_TEST)))
            ).signer(account.getPublicAccount()).deadline(new FakeDeadline()).build();

        String expected =
            "bd00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905541000000000000000001000000000000000102020168b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b76301cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb";

        assertSerialization(expected, transaction);

        String expectedEmbedded =
            "6d0000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24019055410102020168b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b76301cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb";

        assertEmbeddedSerialization(expectedEmbedded, transaction);
    }
}
