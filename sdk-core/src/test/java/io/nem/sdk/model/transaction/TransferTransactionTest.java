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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransferTransactionTest {

    static Account account;
    static String generationHash;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST);
        generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
    }

    @Test
    void createATransferTransactionViaStaticConstructor() {

        TransferTransaction transferTx =
            TransferTransactionFactory.create(NetworkType.MIJIN_TEST,
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
                    NetworkType.MIJIN_TEST),
                Arrays.asList(),
                PlainMessage.Empty
            ).build();

        assertEquals(NetworkType.MIJIN_TEST, transferTx.getNetworkType());
        assertTrue(1 == transferTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(transferTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), transferTx.getMaxFee());
        assertEquals(
            new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST),
            transferTx.getRecipient().get());
        assertEquals(0, transferTx.getMosaics().size());
        assertNotNull(transferTx.getMessage());
    }

    @Test
    @DisplayName("Serialization")
    void shouldGenerateBytes() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        String expected =
            "a5000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019054410000000000000000010000000000000090e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac01000100672b0000ce5600006400000000000000";
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty).deadline(new FakeDeadline()).build();
        byte[] actual = transferTransaction.generateBytes();
        assertEquals(expected, Hex.toHexString(actual));
    }

    @Test
    @DisplayName("Serialization-public")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        String expected =
            "a5000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019054410000000000000000010000000000000090e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac01000100672b0000ce5600006400000000000000";
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty).deadline(new FakeDeadline()).build();
        byte[] actual = transferTransaction.serialize();
        assertEquals(expected, Hex.toHexString(actual));
    }

    @Test
    @DisplayName("To aggregate")
    void toAggregate() {
        String expected =
            "550000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac01000100672b0000ce5600006400000000000000";

        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty).deadline(new FakeDeadline()).build();
        byte[] actual =
            transferTransaction
                .toAggregate(
                    new PublicAccount(
                        "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                        NetworkType.MIJIN_TEST))
                .toAggregateTransactionBytes();
        assertEquals(expected, Hex.toHexString(actual));
    }

    @Test
    void serializeAndSignTransaction() {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty).deadline(new FakeDeadline()).build();

        SignedTransaction signedTransaction = transferTransaction.signWith(account, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "90E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC01000100672B0000CE5600006400000000000000",
            payload.substring(240));
        assertEquals(
            "B54321C382FA3CC53EB6559FDDE03832898E7E89C8F90C10DF8567AD41A926A2",
            signedTransaction.getHash());
    }
}
