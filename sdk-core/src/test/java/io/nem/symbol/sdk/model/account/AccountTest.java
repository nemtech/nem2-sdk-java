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

package io.nem.symbol.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.ed25519.Ed25519CryptoEngine;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.FakeDeadline;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class AccountTest {

    private final String generationHash =
        "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @Test
    void shouldCreateAccountViaConstructor() {
        Account account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST);
        assertEquals(
            "787225AAFF3D2C71F4FFA32D4F19EC4922F3CD869747F267378F81F8E3FCB12D",
            account.getPrivateKey());
        assertEquals(
            "2134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2",
            account.getPublicKey());
        assertEquals("SAEJCCEGA5SMEL65GTVYS6P6V2F5TOPDAOVAC5Q", account.getAddress().plain());
    }

    @Test
    void shouldCreateAccountViaStaticConstructor() {
        Account account =
            Account.createFromPrivateKey(
                "787225AAFF3D2C71F4FFA32D4F19EC4922F3CD869747F267378F81F8E3FCB12D",
                NetworkType.MIJIN_TEST);
        assertEquals(
            "787225AAFF3D2C71F4FFA32D4F19EC4922F3CD869747F267378F81F8E3FCB12D",
            account.getPrivateKey());
        assertEquals(
            "2134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2",
            account.getPublicKey());
        assertEquals("SAEJCCEGA5SMEL65GTVYS6P6V2F5TOPDAOVAC5Q", account.getAddress().plain());
    }

    @Test
    void shouldCreateAccountViaStaticConstructor2() {
        Account account =
            Account.createFromPrivateKey(
                "5098D500390934F81EA416D9A2F50F276DE446E28488E1801212931E3470DA31",
                NetworkType.MIJIN_TEST);
        assertEquals(
            "5098D500390934F81EA416D9A2F50F276DE446E28488E1801212931E3470DA31",
            account.getPrivateKey());
        assertEquals(
            "8A6ADEDF033FFDA38E2E762F5A729AAF2AAE4EEACF9297CC886FFFE2765333AB",
            account.getPublicKey());
        assertEquals("SD3LI4-KBRLPF-4VEHE5-VGEXT2-UWS4GE-EBRMBB-JRA",
            account.getAddress().pretty());
    }

    @Test
    void shouldCreateAccountViaStaticConstructor3() {
        Account account =
            Account.createFromPrivateKey(
                "B8AFAE6F4AD13A1B8AAD047B488E0738A437C7389D4FF30C359AC068910C1D59",
                NetworkType.MIJIN_TEST);
        assertEquals(
            "B8AFAE6F4AD13A1B8AAD047B488E0738A437C7389D4FF30C359AC068910C1D59",
            account.getPrivateKey());
        assertEquals(
            "F9D6329A1A927F5D8918D3D313524CF179DE126AF8F0E83F0FBF2782B5D8F68C",
            account.getPublicKey());
        assertEquals("SDICGGG5273NEYOPJPRN5RXFLENIVYTEBC3F3AY", account.getAddress().plain());
    }

    @Test
    void generateNewAccountTest() {
        Account account = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        assertNotEquals(null, account.getPrivateKey());
        assertNotEquals(null, account.getPublicKey());
        assertEquals(64, account.getPrivateKey().length());
        assertEquals(64, account.getPublicKey().length());
    }

    @Test
    void shouldSignTransaction() {
        Account account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST);
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBF", NetworkType.MIJIN_TEST),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty
            ).deadline(new FakeDeadline()).build();

        SignedTransaction signedTransaction = account.sign(transferTransaction, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "B1000000000000002740ABCB87D42B2BC2ED41E8D77FB07731B2572EF77F8ADB65C5BD0D763201B03F1AB7B08B3D75837D561472303290EFD6CFA21077DC5F7051744A2AC9C9190D2134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F200000000019054410000000000000000010000000000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA840100010000000000672B0000CE560000640000000000000000",
            payload);
        assertEquals(
            "91793BF5A61BC9A926B945BE900287D527D14D225B4B23167FEFE923FABAB7ED",
            signedTransaction.getHash());
    }

    @Test
    void shouldAcceptKeyPairAsConstructor() {
        NetworkType networkType = NetworkType.MIJIN_TEST;
        KeyPair random = KeyPair
            .random(new Ed25519CryptoEngine());
        Account account = new Account(random, networkType);
        assertEquals(random.getPrivateKey().toHex().toUpperCase(), account.getPrivateKey());
        assertEquals(networkType, account.getAddress().getNetworkType());
    }

    @Test
    public void testAddresses2() {
        Address address = Address
            .createFromPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C",
                NetworkType.MIJIN_TEST);
        assertEquals("SCUNEE-EE4FON-6N3DKD-FZUM6U-V7AU26-ZFTWT5-6NQ", address.pretty());
        assertEquals("SCUNEEEE4FON6N3DKDFZUM6UV7AU26ZFTWT56NQ", address.plain());
    }

    @Test
    void shouldConstruct() {
        PublicAccount account1 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST);

        assertEquals("SDWGJE7XOYRX5RQMMLWF4TE7U5Y2HUYBRDVX2OI",
            account1.getAddress().plain());
        assertEquals("SDWGJE-7XOYRX-5RQMML-WF4TE7-U5Y2HU-YBRDVX-2OI",
            account1.getAddress().pretty());
        assertEquals(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            account1.getPublicKey().toHex());
    }

}
