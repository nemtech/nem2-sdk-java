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

package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.ed25519.Ed25519CryptoEngine;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.FakeDeadline;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
            "1026D70E1954775749C6811084D6450A3184D977383F0E4282CD47118AF37755",
            account.getPublicKey());
        assertEquals("SDRDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY", account.getAddress().plain());
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
            "1026D70E1954775749C6811084D6450A3184D977383F0E4282CD47118AF37755",
            account.getPublicKey());
        assertEquals("SDRDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY", account.getAddress().plain());
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
            "9B800145F7228CE0014FC6FB44AD899BFCAD7B0CDF48DB63A7CC7299E373D734",
            account.getPublicKey());
        assertEquals("SAQC5A-K6X2K6-YYAI4L-2TQI2T-4ZRWAO-URYDYT-UO77",
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
            "68B3FBB18729C1FDE225C57F8CE080FA828F0067E451A3FD81FA628842B0B763",
            account.getPublicKey());
        assertEquals("SBE6CS7LZKJXLDVTNAC3VZ3AUVZDTF3PACNFIXFN", account.getAddress().plain());
    }

    @Test
    void generateNewAccountTest() {
        Account account = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        assertNotEquals(null, account.getPrivateKey());
        assertNotEquals(null, account.getPublicKey());
        assertEquals(64, account.getPrivateKey().length());
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
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Collections.singletonList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                PlainMessage.Empty
            ).deadline(new FakeDeadline()).build();

        SignedTransaction signedTransaction = account.sign(transferTransaction, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(
            "90E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC01000100672B0000CE5600006400000000000000",
            payload.substring(240));
        assertEquals(
            "B54321C382FA3CC53EB6559FDDE03832898E7E89C8F90C10DF8567AD41A926A2",
            signedTransaction.getHash());
    }

    @Test
    void shouldAcceptKeyPairAsConstructor() {
        NetworkType networkType = NetworkType.MIJIN_TEST;
        KeyPair random = KeyPair
            .random(new Ed25519CryptoEngine(), networkType.resolveSignSchema());
        Account account = new Account(random, networkType);
        assertEquals(random.getPrivateKey().toHex().toUpperCase(), account.getPrivateKey());
        assertEquals(networkType, account.getAddress().getNetworkType());
    }

    @Test
    public void testAddresses2() {
        Address address = Address
            .createFromPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C",
                NetworkType.MIJIN_TEST);
        Assert.assertEquals("SCUNEE-EE4FON-6N3DKD-FZUM6U-V7AU26-ZFTWT5-6NUX", address.pretty());
        Assert.assertEquals("SCUNEEEE4FON6N3DKDFZUM6UV7AU26ZFTWT56NUX", address.plain());
    }

    @Test
    void shouldConstruct() {
        PublicAccount account1 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST);

        Assertions.assertEquals("SDWGJE7XOYRX5RQMMLWF4TE7U5Y2HUYBRDVX2OJE",
            account1.getAddress().plain());
        Assertions.assertEquals("SDWGJE-7XOYRX-5RQMML-WF4TE7-U5Y2HU-YBRDVX-2OJE",
            account1.getAddress().pretty());
        Assertions.assertEquals(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            account1.getPublicKey().toHex());
    }


}
