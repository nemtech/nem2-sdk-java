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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.message.PlainMessage;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    private final NetworkType networkType = NetworkType.MIJIN_TEST;

    private final PublicAccount signer =
        PublicAccount.createFromPublicKey(
            "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
            networkType);
    private final String generationHash =
        "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @Test
    void generateHashFromTransferTransactionPayload() {
        TransferTransactionFactory factory = TransferTransactionFactory
            .create(networkType,
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
                    networkType),
                Collections.emptyList(),
                PlainMessage.Empty
            );
        TransferTransaction transaction =
            factory.build();

        String hash =
            transaction.createTransactionHash(
                "C7000000D0B190DFEEAB0378F943F79CDB7BC44453491890FAA70F5AA95B909E67487408407956BDE32AC977D035FBBA575C11AA034B23402066C16FD6126893F3661B099A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24039054410000000000000000A76541BE0C00000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC03000300303064000000000000006400000000000000002F00FA0DEDD9086400000000000000443F6D806C05543A6400000000000000",
                generationHash.getBytes());
        assertEquals("820C535E7998AEE4255677A9C53566190225C02D99558AC48192171EC8144B43", hash);
    }

    @Test
    void generateHashFromAggregateTransactionPayload() {
        TransferTransactionFactory factory = TransferTransactionFactory
            .create(networkType,
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
                    networkType),
                Collections.emptyList(),
                PlainMessage.Empty
            );
        TransferTransaction transaction =
            factory.build();

        String hash =
            transaction.createTransactionHash(
                "E9000000A37C8B0456474FB5E3E910E84B5929293C114E0AF97FEF0D940D3A2A2C337BAFA0C59538E5988229B65A3065B4E9BD57B1AFAEC64DFBE2211B8AF6E742801E08C2F93346E27CE6AD1A9F8F5E3066F8326593A406BDF357ACB041E2F9AB402EFE0390414100000000000000008EEAC2C80C0000006D0000006D000000C2F93346E27CE6AD1A9F8F5E3066F8326593A406BDF357ACB041E2F9AB402EFE0390554101020200B0F93CBEE49EEB9953C6F3985B15A4F238E205584D8F924C621CBE4D7AC6EC2400B1B5581FC81A6970DEE418D2C2978F2724228B7B36C5C6DF71B0162BB04778B4",
                generationHash.getBytes());
        assertEquals("3BB1A0539B49194BFDCA34BFDB0CFE1748C7FE9062DF92EE38A90769E0957B75", hash);
    }

    @Test
    void shouldReturnTransactionIsUnannouncedWhenThereIsNoTransactionInfo() {
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransactionFactory(networkType).deadline(new FakeDeadline())
                .build();
        assertTrue(fakeTransaction.isUnannounced());
    }

    @Test
    void shouldReturnTransactionIsUnconfirmedWhenHeightIs0() {
        TransactionInfo transactionInfo = TransactionInfo
            .create(BigInteger.valueOf(0), 1, "id_hash", "hash", "hash");
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransactionFactory(networkType)
                .deadline(new FakeDeadline())
                .signature("signature")
                .signer(signer)
                .transactionInfo(transactionInfo)
                .build();

        assertTrue(fakeTransaction.isUnconfirmed());
    }

    @Test
    void shouldReturnTransactionIsNotUnconfirmedWhenHeightIsNot0() {
        TransactionInfo transactionInfo = TransactionInfo
            .create(BigInteger.valueOf(100), 1, "id_hash", "hash", "hash");
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransactionFactory(networkType)
                .deadline(new FakeDeadline())
                .signature("signature")
                .signer(signer)
                .transactionInfo(transactionInfo)
                .build();

        assertFalse(fakeTransaction.isUnconfirmed());
    }

    @Test
    void shouldReturnTransactionIsConfirmedWhenHeightIsNot0() {
        TransactionInfo transactionInfo = TransactionInfo
            .create(BigInteger.valueOf(100), 1, "id_hash", "hash", "hash");
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransactionFactory(networkType)
                .deadline(new FakeDeadline())
                .signature("signature")
                .signer(signer)
                .transactionInfo(transactionInfo)
                .build();

        assertTrue(fakeTransaction.isConfirmed());
    }

    @Test
    void shouldReturnTransactionIsAggregateBondedWhenHeightIs0AndHashAndMerkHashAreDifferent() {
        TransactionInfo transactionInfo = TransactionInfo
            .create(BigInteger.valueOf(0), 1, "id_hash", "hash", "hash_2");
        FakeTransferTransaction fakeTransaction =
            new FakeTransferTransactionFactory(networkType)
                .deadline(new FakeDeadline())
                .signature("signature")
                .signer(signer)
                .transactionInfo(transactionInfo)
                .build();

        assertTrue(fakeTransaction.hasMissingSignatures());
    }
}
