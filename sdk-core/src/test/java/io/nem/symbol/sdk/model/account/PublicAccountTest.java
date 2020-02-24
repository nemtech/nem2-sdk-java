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

import io.nem.symbol.sdk.model.blockchain.NetworkType;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PublicAccountTest {

    private String plain = "SAKQRU2RTNWMBE3KAQRTA46T3EB6DX567FO4EBFL";
    private String pretty = "SAKQRU-2RTNWM-BE3KAQ-RTA46T-3EB6DX-567FO4-EBFL";
    private String encoded = "901508D3519B6CC0936A04233073D3D903E1DFBEF95DC204AB";
    private String publicKey = "089E931203F63EECF695DB94957B03E1A6B7941532069B687386D6D4A7B6BE4A";
    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @Test
    void shouldCreatePublicAccountViaConstructor() {
        PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.MIJIN_TEST);
        assertEquals(publicKey.toUpperCase(), publicAccount.getPublicKey().toHex());
        assertEquals(plain,
            publicAccount.getAddress().plain());
    }

    @Test
    void shouldCreatePublicAccountViaStaticConstructor() {
        PublicAccount publicAccount =
            PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST);
        assertEquals(publicKey.toUpperCase(), publicAccount.getPublicKey().toHex());
        assertEquals(plain,
            publicAccount.getAddress().plain());
    }

    @Test
    void equalityIsBasedOnPublicKeyAndNetwork() {
        PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.MIJIN_TEST);
        PublicAccount publicAccount2 = new PublicAccount(publicKey, NetworkType.MIJIN_TEST);
        assertEquals(publicAccount, publicAccount2);
    }

    @Test
    void equalityReturnsFalseIfNetworkIsDifferent() {
        PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.MIJIN_TEST);
        PublicAccount publicAccount2 = new PublicAccount(publicKey, NetworkType.MAIN_NET);
        assertNotEquals(publicAccount, publicAccount2);
    }


    @Test
    public void testAddresses() {
        assertAddress(Address.createFromRawAddress(plain));
        assertAddress(Address.createFromRawAddress(pretty));
        assertAddress(Address.createFromEncoded(encoded));
        assertAddress(Address.createFromPublicKey(publicKey, networkType));
    }

    private void assertAddress(Address address) {
        assertEquals(plain, address.plain());
        assertEquals(pretty, address.pretty());
        Assertions.assertEquals(networkType, address.getNetworkType());
    }


    @Test
    void shouldBeEquals() {
        PublicAccount account1 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST);

        PublicAccount account2 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST);

        PublicAccount account3 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MAIN_NET);

        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
        assertNotEquals(account1, account3);

        assertNotEquals(account1, new HashSet<>());
    }
}
