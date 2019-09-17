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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    static SignSchema signSchema = SignSchema.DEFAULT;

    private final PublicAccount signer =
        PublicAccount.createFromPublicKey(
            "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
            NetworkType.MIJIN_TEST, signSchema);
    private final String generationHash =
        "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @Test
    void generateHashFromTransferTransactionPayload() {
        String hash =
            Transaction.createTransactionHash(
                "C7000000D0B190DFEEAB0378F943F79CDB7BC44453491890FAA70F5AA95B909E67487408407956BDE32AC977D035FBBA575C11AA034B23402066C16FD6126893F3661B099A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24039054410000000000000000A76541BE0C00000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC03000300303064000000000000006400000000000000002F00FA0DEDD9086400000000000000443F6D806C05543A6400000000000000",
                generationHash.getBytes());
        assertEquals("BADC739882F2EE3D7D54A0DC3B62C2ADA50259CB32E99E012452C516C7BE94C6", hash);
    }

    @Test
    void generateHashFromAggregateTransactionPayload() {
        String hash =
            Transaction.createTransactionHash(
                "E9000000A37C8B0456474FB5E3E910E84B5929293C114E0AF97FEF0D940D3A2A2C337BAFA0C59538E5988229B65A3065B4E9BD57B1AFAEC64DFBE2211B8AF6E742801E08C2F93346E27CE6AD1A9F8F5E3066F8326593A406BDF357ACB041E2F9AB402EFE0390414100000000000000008EEAC2C80C0000006D0000006D000000C2F93346E27CE6AD1A9F8F5E3066F8326593A406BDF357ACB041E2F9AB402EFE0390554101020200B0F93CBEE49EEB9953C6F3985B15A4F238E205584D8F924C621CBE4D7AC6EC2400B1B5581FC81A6970DEE418D2C2978F2724228B7B36C5C6DF71B0162BB04778B4",
                generationHash.getBytes());
        assertEquals("EA67D29F3E3EE1F5107AB68E0E8BD5F1CF85901F8778AAF6FAF74316D973B30D", hash);
    }

    @Test
    void shouldReturnTransactionIsUnannouncedWhenThereIsNoTransactionInfo() {
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransaction(
                NetworkType.MIJIN_TEST, 1, new FakeDeadline(), BigInteger.valueOf(0));

        assertTrue(fakeTransaction.isUnannounced());
    }

    @Test
    void shouldReturnTransactionIsUnconfirmedWhenHeightIs0() {
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransaction(
                NetworkType.MIJIN_TEST,
                1,
                new FakeDeadline(),
                BigInteger.valueOf(0),
                "signature",
                signer,
                TransactionInfo.create(BigInteger.valueOf(0), 1, "id_hash", "hash", "hash"));

        assertTrue(fakeTransaction.isUnconfirmed());
    }

    @Test
    void shouldReturnTransactionIsNotUnconfirmedWhenHeightIsNot0() {
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransaction(
                NetworkType.MIJIN_TEST,
                1,
                new FakeDeadline(),
                BigInteger.valueOf(0),
                "signature",
                signer,
                TransactionInfo.create(BigInteger.valueOf(100), 1, "id_hash", "hash", "hash"));

        assertFalse(fakeTransaction.isUnconfirmed());
    }

    @Test
    void shouldReturnTransactionIsConfirmedWhenHeightIsNot0() {
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransaction(
                NetworkType.MIJIN_TEST,
                1,
                new FakeDeadline(),
                BigInteger.valueOf(0),
                "signature",
                signer,
                TransactionInfo.create(BigInteger.valueOf(100), 1, "id_hash", "hash", "hash"));

        assertTrue(fakeTransaction.isConfirmed());
    }

    @Test
    void shouldReturnTransactionIsAggregateBondedWhenHeightIs0AndHashAndMerkHashAreDifferent() {
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransaction(
                NetworkType.MIJIN_TEST,
                1,
                new FakeDeadline(),
                BigInteger.valueOf(0),
                "signature",
                signer,
                TransactionInfo.create(BigInteger.valueOf(0), 1, "id_hash", "hash", "hash_2"));

        assertTrue(fakeTransaction.hasMissingSignatures());
    }
}
