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

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ModifyMultisigAccountTransactionTest {

    @Test
    void createAMultisigModificationTransactionViaConstructor() {
        ModifyMultisigAccountTransaction modifyMultisigAccountTransaction = ModifyMultisigAccountTransaction.create(
                new Deadline(2, ChronoUnit.HOURS),
                2,
                1,
                Collections.singletonList(
                        new MultisigCosignatoryModification(MultisigCosignatoryModificationType.ADD,
                                PublicAccount.createFromPublicKey("68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763", NetworkType.MIJIN_TEST))
                ),
                NetworkType.MIJIN_TEST
        );

        assertEquals(NetworkType.MIJIN_TEST, modifyMultisigAccountTransaction.getNetworkType());
        assertTrue(3 == modifyMultisigAccountTransaction.getVersion());
        assertTrue(LocalDateTime.now().isBefore(modifyMultisigAccountTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), modifyMultisigAccountTransaction.getFee());
        assertEquals(2, modifyMultisigAccountTransaction.getMinApprovalDelta());
        assertEquals(1, modifyMultisigAccountTransaction.getMinRemovalDelta());
        assertEquals("68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763", modifyMultisigAccountTransaction.getModifications().get(0).getCosignatoryPublicAccount().getPublicKey());
        assertEquals(MultisigCosignatoryModificationType.ADD, modifyMultisigAccountTransaction.getModifications().get(0).getType());
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/ModifyMultisigAccountTransaction.spec.js
        byte[] expected = new byte[]{(byte) 189, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, (byte) 144, 85, 65, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 0, 104, (byte) 179, (byte) 251, (byte) 177, (byte) 135, 41, (byte) 193, (byte) 253, (byte) 226, 37, (byte) 197, 127, (byte) 140, (byte) 224, (byte) 128, (byte) 250, (byte) 130, (byte) 143, 0, 103, (byte) 228, 81, (byte) 163, (byte) 253, (byte) 129, (byte) 250, 98, (byte) 136, 66, (byte) 176, (byte) 183, 99, 0, (byte) 207, (byte) 137, 63, (byte) 252, (byte) 196, 124, 51, (byte) 231, (byte) 246, (byte) 138, (byte) 177, (byte) 219, 86, 54, 92, 21, 107, 7, 54, (byte) 130, 74, 12, 30, 39, 63, (byte) 158, 0, (byte) 184, (byte) 223, (byte) 143, 1, (byte) 235};

        ModifyMultisigAccountTransaction modifyMultisigAccountTransaction = ModifyMultisigAccountTransaction.create(
                new FakeDeadline(),
                2,
                1,
                Arrays.asList(
                        new MultisigCosignatoryModification(MultisigCosignatoryModificationType.ADD, PublicAccount.createFromPublicKey("68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763", NetworkType.MIJIN_TEST)),
                        new MultisigCosignatoryModification(MultisigCosignatoryModificationType.ADD, PublicAccount.createFromPublicKey("cf893ffcc47c33e7f68ab1db56365c156b0736824a0c1e273f9e00b8df8f01eb", NetworkType.MIJIN_TEST))

                ),
                NetworkType.MIJIN_TEST
        );

        byte[] actual = modifyMultisigAccountTransaction.generateBytes();
        assertArrayEquals(expected, actual);
    }
}
