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

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TransferTransactionTest {
    static Account account;

    @BeforeAll
    public static void setup() {
        account = new Account("787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d", NetworkType.MIJIN_TEST);
    }

    @Test
    void createATransferTransactionViaStaticConstructor() {

        TransferTransaction transferTx = TransferTransaction.create(
                new Deadline(2, ChronoUnit.HOURS),
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST),
                Arrays.asList(),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );

        assertEquals(NetworkType.MIJIN_TEST, transferTx.getNetworkType());
        assertTrue(3 == transferTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(transferTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), transferTx.getFee());
        assertTrue(new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST)
                .equals(transferTx.getRecipient()));
        assertEquals(0, transferTx.getMosaics().size());
        assertNotNull(transferTx.getMessage());
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        byte[] expected = new byte[]{(byte) 165, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, (byte) 144, 84, 65, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, (byte) 144, (byte) 232, (byte) 254, (byte) 189, (byte) 103, (byte) 29, (byte) 212, (byte) 27, (byte) 238, (byte) 148, (byte) 236, (byte) 59, (byte) 165, (byte) 131, (byte) 28, (byte) 182, (byte) 8, (byte) 163, (byte) 18, (byte) 194, (byte) 242, (byte) 3, (byte) 186, (byte) 132, (byte) 172,
                1, 0, 1, 0, 103, 43, 0, 0, (byte) 206, 86, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0};

        TransferTransaction transferTransaction = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = transferTransaction.generateBytes();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("To aggregate")
    void toAggregate() {
        byte[] expected =  new byte[]{85,0,0,0,-102,73,54,100,6,-84,-87,82,-72,-117,-83,-11,-15,-23,-66,108,-28,-106,-127,
                65,3,90,96,-66,80,50,115,-22,101,69,107,36,3,-112,84,65,-112,-24,-2,-67,103,29,-44,27,-18,-108,-20,59,
                -91,-125,28,-74,8,-93,18,-62,-14,3,-70,-124,-84,1,0,1,0,103,43,0,0,-50,86,0,0,100,0,0,0,0,0,0,0};

        TransferTransaction transferTransaction = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = transferTransaction.toAggregate(new PublicAccount("9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24", NetworkType.MIJIN_TEST)).toAggregateTransactionBytes();
        assertArrayEquals(expected, actual);
    }

    @Test
    void serializeAndSignTransaction() {
        TransferTransaction transferTransaction = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );

        SignedTransaction signedTransaction = transferTransaction.signWith(account);
        assertEquals("A5000000773891AD01DD4CDF6E3A55C186C673E256D7DF9D471846F1943CC3529E4E02B38B9AF3F8D13784645FF5FAAFA94A321B94933C673D12DE60E4BC05ABA56F750E1026D70E1954775749C6811084D6450A3184D977383F0E4282CD47118AF37755039054410000000000000000010000000000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC01000100672B0000CE5600006400000000000000", signedTransaction.getPayload());
        assertEquals("350AE56BC97DB805E2098AB2C596FA4C6B37EF974BF24DFD61CD9F77C7687424", signedTransaction.getHash());
    }
}
