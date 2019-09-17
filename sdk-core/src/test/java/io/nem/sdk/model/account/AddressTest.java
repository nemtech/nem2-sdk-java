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
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.blockchain.NetworkType;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class AddressTest {

    private static Stream<Arguments> provider() {
        return Stream.of(
            Arguments.of("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST),
            Arguments.of("MDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN),
            Arguments.of("TDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.TEST_NET),
            Arguments.of("NDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MAIN_NET));
    }

    private static Stream<Arguments> assertExceptionProvider() {
        return Stream.of(
            Arguments.of("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN),
            Arguments.of("MDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MAIN_NET),
            Arguments.of("TDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST),
            Arguments.of("NDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.TEST_NET));
    }

    private static Stream<Arguments> publicKeysSha3() {
        return Stream.of(
            Arguments.of(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.MIJIN_TEST,
                "SARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJETM3ZSP"),
            Arguments.of(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.MIJIN,
                "MARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJE5K5RYU"),
            Arguments.of(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.TEST_NET,
                "TARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJE47FYR3"),
            Arguments.of(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.MAIN_NET,
                "NARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJFJKUV32"));
    }

    private static Stream<Arguments> publicKeysKeccak() {
        return Stream.of(
            Arguments.of(
                "c5f54ba980fcbb657dbaaa42700539b207873e134d2375efeab5f1ab52f87844",
                NetworkType.MIJIN,
                "MDD2CT6LQLIYQ56KIXI3ENTM6EK3D44P5LDT7JHT"),
            Arguments.of(
                "c5f54ba980fcbb657dbaaa42700539b207873e134d2375efeab5f1ab52f87844",
                NetworkType.MIJIN,
                "MDD2CT6LQLIYQ56KIXI3ENTM6EK3D44P5LDT7JHT"),
            Arguments.of(
                "c5f54ba980fcbb657dbaaa42700539b207873e134d2375efeab5f1ab52f87844",
                NetworkType.TEST_NET,
                "TDD2CT6LQLIYQ56KIXI3ENTM6EK3D44P5KZPFMK2"),
            Arguments.of(
                "fbb91b16df828e21a9802980a44fc757c588bc1382a4cea429d6fa2ae0333f56",
                NetworkType.MAIN_NET,
                "NBAF3BFLLPWH33MYE6VUPP5T6DQBZBKIDEQKZQOE"),
            Arguments.of(
                "fbb91b16df828e21a9802980a44fc757c588bc1382a4cea429d6fa2ae0333f56",
                NetworkType.TEST_NET,
                "TBAF3BFLLPWH33MYE6VUPP5T6DQBZBKIDGA56VWB"),
            Arguments.of(
                "fbb91b16df828e21a9802980a44fc757c588bc1382a4cea429d6fa2ae0333f56",
                NetworkType.MIJIN,
                "MBAF3BFLLPWH33MYE6VUPP5T6DQBZBKIDEBBSGE2"),
            Arguments.of(
                "6d34c04f3a0e42f0c3c6f50e475ae018cfa2f56df58c481ad4300424a6270cbb",
                NetworkType.MAIN_NET,
                "NA5IG3XFXZHIPJ5QLKX2FBJPEZYPMBPPK2ZRC3EH"));
    }

    @Test
    void testAddressCreation() {
        Address address =
            new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST);
        assertEquals("SDGLFWDSHILTIUHGIBH5UGX2VYF5VNJEKCCDBR26", address.plain());
    }

    @Test
    void testAddressWithSpacesCreation() {
        Address address =
            new Address(" SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26 ", NetworkType.MIJIN_TEST);
        assertEquals("SDGLFWDSHILTIUHGIBH5UGX2VYF5VNJEKCCDBR26", address.plain());
    }

    @Test
    void testLowerCaseAddressCreation() {
        Address address =
            new Address("sdglfw-dshilt-iuhgib-h5ugx2-vyf5vn-jekccd-br26", NetworkType.MIJIN_TEST);
        assertEquals("SDGLFWDSHILTIUHGIBH5UGX2VYF5VNJEKCCDBR26", address.plain());
    }

    @Test
    void addressInPrettyFormat() {
        Address address =
            new Address("SDRDGF-TDLLCB-67D4HP-GIMIHP-NSRYRJ-RT7DOB-GWZY", NetworkType.MIJIN_TEST);
        assertEquals("SDRDGF-TDLLCB-67D4HP-GIMIHP-NSRYRJ-RT7DOB-GWZY", address.pretty());
    }

    @Test
    void equality() {
        Address address1 =
            new Address("SDRDGF-TDLLCB-67D4HP-GIMIHP-NSRYRJ-RT7DOB-GWZY", NetworkType.MIJIN_TEST);
        Address address2 =
            new Address("SDRDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY", NetworkType.MIJIN_TEST);
        assertEquals(address1, address2);
    }

    @Test
    void noEquality() {
        Address address1 =
            new Address("SRRRRR-TTTTTT-555555-GIMIHP-NSRYRJ-RT7DOB-GWZY", NetworkType.MIJIN_TEST);
        Address address2 =
            new Address("SDRDGF-TDLLCB-67D4HP-GIMIHP-NSRYRJ-RT7DOB-GWZY", NetworkType.MIJIN_TEST);
        assertNotEquals(address1, address2);
    }

    @ParameterizedTest
    @MethodSource("assertExceptionProvider")
    @DisplayName("NetworkType")
    void testThrowErrorWhenNetworkTypeIsNotTheSameAsAddress(
        String rawAddress, NetworkType networkType) {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                new Address(rawAddress, networkType);
            });
    }

    @ParameterizedTest
    @MethodSource("assertExceptionProvider")
    @DisplayName("NetworkType")
    void shouldReturnDifferentNetworkType(
        String address, NetworkType networkType) {
        Assertions
            .assertNotEquals(networkType, Address.createFromRawAddress(address).getNetworkType());
    }

    @Test
    void createFromRawAddressShouldFailWhenInvalidSuffix() {
        Assertions.assertEquals("Address is invalid", assertThrows(
            IllegalArgumentException.class,
            () -> {
                Address.createFromRawAddress("X");
            }).getMessage());
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    void createShouldFailWhenInvalidPublicKey(SignSchema signSchema) {
        Assertions.assertEquals("Public key is not valid", assertThrows(
            IllegalArgumentException.class,
            () -> {
                Address.createFromPublicKey("InvalidPublicKey", NetworkType.MIJIN, signSchema);
            }).getMessage());
    }

    @ParameterizedTest
    @MethodSource("provider")
    @DisplayName("NetworkType")
    void testUInt64FromBigInteger(String rawAddress, NetworkType input) {
        Address address = new Address(rawAddress, input);
        assertEquals(input, address.getNetworkType());
    }

    @ParameterizedTest
    @MethodSource("publicKeysSha3")
    @DisplayName("AddressFromPublicKey")
    void testCreateAddressFromPublicKeySha3(String publicKey, NetworkType networkType,
        String input) {
        Address address = Address.createFromPublicKey(publicKey, networkType, SignSchema.SHA3);
        assertEquals(input, address.plain());
    }

    @ParameterizedTest
    @MethodSource("publicKeysKeccak")
    @DisplayName("AddressFromPublicKey")
    void testCreateAddressFromPublicKeyKeccak(String publicKey, NetworkType networkType,
        String input) {
        Address address = Address
            .createFromPublicKey(publicKey, networkType, SignSchema.KECCAK_REVERSED_KEY);
        assertEquals(input, address.plain());
    }
}
