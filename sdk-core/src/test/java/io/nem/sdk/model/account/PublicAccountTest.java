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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.blockchain.NetworkType;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PublicAccountTest {

    private Map<SignSchema, String> plain;
    private Map<SignSchema, String> pretty;
    private Map<SignSchema, String> encoded;
    private String publicKey = "089E931203F63EECF695DB94957B03E1A6B7941532069B687386D6D4A7B6BE4A";
    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @BeforeEach
    private void setUp() {
        plain = new EnumMap<>(SignSchema.class);
        plain.put(SignSchema.SHA3, "SAKQRU2RTNWMBE3KAQRTA46T3EB6DX567FO4EBFL");
        plain.put(SignSchema.KECCAK_REVERSED_KEY, "SBCUJZNUSWU4FWRYDUPJO5IBOEJ5CI2AEMLBPT6V");

        pretty = new EnumMap<>(SignSchema.class);
        pretty.put(SignSchema.SHA3, "SAKQRU-2RTNWM-BE3KAQ-RTA46T-3EB6DX-567FO4-EBFL");
        pretty
            .put(SignSchema.KECCAK_REVERSED_KEY, "SBCUJZ-NUSWU4-FWRYDU-PJO5IB-OEJ5CI-2AEMLB-PT6V");

        encoded = new EnumMap<>(SignSchema.class);
        encoded.put(SignSchema.SHA3, "901508D3519B6CC0936A04233073D3D903E1DFBEF95DC204AB");
        encoded
            .put(SignSchema.KECCAK_REVERSED_KEY,
                "904544e5b495a9c2da381d1e9775017113d12340231617cfd5");
    }


    @ParameterizedTest
    @EnumSource(SignSchema.class)
    void shouldCreatePublicAccountViaConstructor(SignSchema signSchema) {
        PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.MIJIN_TEST,
            signSchema);
        assertEquals(publicKey.toUpperCase(), publicAccount.getPublicKey().toString());
        assertEquals(plain.get(signSchema),
            publicAccount.getAddress().plain());
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    void shouldCreatePublicAccountViaStaticConstructor(SignSchema signSchema) {
        PublicAccount publicAccount =
            PublicAccount
                .createFromPublicKey(publicKey, NetworkType.MIJIN_TEST, signSchema);
        assertEquals(publicKey.toUpperCase(), publicAccount.getPublicKey().toString());
        assertEquals(plain.get(signSchema), publicAccount.getAddress().plain());
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    void equalityIsBasedOnPublicKeyAndNetwork(SignSchema signSchema) {
        PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.MIJIN_TEST,
            signSchema);
        PublicAccount publicAccount2 = new PublicAccount(publicKey, NetworkType.MIJIN_TEST,
            signSchema);
        assertEquals(publicAccount, publicAccount2);
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    void equalityReturnsFalseIfNetworkIsDifferent(SignSchema signSchema) {
        PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.MIJIN_TEST,
            signSchema);
        PublicAccount publicAccount2 = new PublicAccount(publicKey, NetworkType.MAIN_NET,
            signSchema);
        assertNotEquals(publicAccount, publicAccount2);
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void testAddresses(SignSchema signSchema) {
        assertAddress(Address.createFromRawAddress(plain.get(signSchema)), signSchema);
        assertAddress(Address.createFromRawAddress(pretty.get(signSchema)), signSchema);
        assertAddress(Address.createFromEncoded(encoded.get(signSchema)), signSchema);
        assertAddress(Address.createFromPublicKey(publicKey, networkType, signSchema), signSchema);
        assertAddress(Address
                .createFromEncoded(Address.createFromRawAddress(plain.get(signSchema)).encoded()),
            signSchema);
    }

    private void assertAddress(Address address, SignSchema signSchema) {
        Assert.assertEquals(plain.get(signSchema), address.plain());
        Assert.assertEquals(pretty.get(signSchema), address.pretty());
        Assert.assertEquals(networkType, address.getNetworkType());
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    void shouldBeEquals(SignSchema signSchema) {
        PublicAccount account1 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST, signSchema);

        PublicAccount account2 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST, signSchema);

        PublicAccount account3 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MAIN_NET, signSchema);

        Assertions.assertEquals(account1, account2);
        Assertions.assertEquals(account1.hashCode(), account2.hashCode());
        Assertions.assertNotEquals(account1, account3);

        Assertions.assertNotEquals(account1, new HashSet<>());
    }
}
