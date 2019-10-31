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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.blockchain.NetworkType;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class AddressTest {

    private static Stream<Arguments> provider() {
        return Stream.of(
            Arguments
                .of("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST, true),
            Arguments
                .of("MDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN, false),
            Arguments
                .of("TDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.TEST_NET, false),
            Arguments
                .of("NDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MAIN_NET, false));
    }

    private static Stream<Arguments> assertExceptionProvider() {
        return Stream.of(
            Arguments.of("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN),
            Arguments.of("MDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MAIN_NET),
            Arguments.of("TDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST),
            Arguments.of("NDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.TEST_NET));
    }


    private static Stream<Arguments> publicKeys() {
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
                "TDGMF64NDRRK6RLTDRG3LIGZ2C5LVFMTDXXPJNGI"),
            Arguments.of(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.MAIN_NET,
                "NDGMF64NDRRK6RLTDRG3LIGZ2C5LVFMTDVFEAGXC"),
            Arguments.of(
                "c5f54ba980fcbb657dbaaa42700539b207873e134d2375efeab5f1ab52f87844",
                NetworkType.MIJIN,
                "MAKIIYW7AXR3YGQBH5L5PF7JUFULUKJYQ4FB7MFF"),
            Arguments.of(
                "c5f54ba980fcbb657dbaaa42700539b207873e134d2375efeab5f1ab52f87844",
                NetworkType.MIJIN_TEST,
                "SAKIIYW7AXR3YGQBH5L5PF7JUFULUKJYQ6FYMGNN"),
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
                "MCC3LX5AJZFAU7KC24GK32JSQARCFAHXDEUVY6Y5"),
            Arguments.of(
                "6d34c04f3a0e42f0c3c6f50e475ae018cfa2f56df58c481ad4300424a6270cbb",
                NetworkType.MAIN_NET,
                "NA5IG3XFXZHIPJ5QLKX2FBJPEZYPMBPPK2ZRC3EH"),
            Arguments.of(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.MIJIN_TEST,
                "SARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJETM3ZSP"),
            Arguments.of(
                "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf",
                NetworkType.MIJIN,
                "MARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJE5K5RYU"),
            Arguments.of(
                "c5f54ba980fcbb657dbaaa42700539b207873e134d2375efeab5f1ab52f87844",
                NetworkType.MIJIN,
                "MAKIIYW7AXR3YGQBH5L5PF7JUFULUKJYQ4FB7MFF"),
            Arguments.of(
                "c5f54ba980fcbb657dbaaa42700539b207873e134d2375efeab5f1ab52f87844",
                NetworkType.MIJIN,
                "MAKIIYW7AXR3YGQBH5L5PF7JUFULUKJYQ4FB7MFF"),
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
                "MCC3LX5AJZFAU7KC24GK32JSQARCFAHXDEUVY6Y5"),
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
    void shouldCreateFromEncoded() {
        String encoded = "901508D3519B6CC0936A04233073D3D903E1DFBEF95DC204AB";
        Address address = Address
            .createFromEncoded(encoded);
        assertEquals(encoded, address.encoded().toUpperCase());
        assertEquals(encoded, address.encoded(NetworkType.MIJIN_TEST).toUpperCase());
    }

    @Test
    void shouldCreateFromEncodedFailWhenInvalid() {
        Assertions.assertEquals(
            "invalid! could not be decoded. DecoderException: Illegal hexadecimal character i at index 0",
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> Address.createFromEncoded("invalid!")).getMessage());
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
        assertNotEquals("notAndAddress", address2);
    }

    @ParameterizedTest
    @MethodSource("assertExceptionProvider")
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
    void shouldReturnDifferentNetworkType(
        String address, NetworkType networkType) {
        Assertions
            .assertNotEquals(networkType, Address.createFromRawAddress(address).getNetworkType());
    }

    @Test
    void createFromRawAddressShouldFailWhenInvalidSize() {
        Assertions.assertEquals("Address X has to be 40 characters long.", assertThrows(
            IllegalArgumentException.class,
            () -> {
                Address.createFromRawAddress("X");
            }).getMessage());
    }

    @Test
    void createFromRawAddressShouldFailWhenInvalidSuffix() {
        Assertions.assertEquals("ADRDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY is an invalid address.",
            assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Address.createFromRawAddress("ADRDGF-TDLLCB-67D4HP-GIMIHP-NSRYRJ-RT7DOB-GWZY");
                }).getMessage());
    }

    @ParameterizedTest
    @EnumSource(NetworkType.class)
    void createShouldFailWhenInvalidPublicKey(NetworkType networkType) {
        Assertions.assertEquals("Public key is not valid", assertThrows(
            IllegalArgumentException.class,
            () -> Address.createFromPublicKey("InvalidPublicKey", networkType)).getMessage());
    }

    @ParameterizedTest
    @MethodSource("publicKeys")
    void testCreateAddressFromPublicKeys(String publicKey, NetworkType networkType,
        String input) {
        Address address = Address.createFromPublicKey(publicKey, networkType);
        assertEquals(input, address.plain());
        assertEquals(networkType, address.getNetworkType());
    }


    @ParameterizedTest
    @MethodSource("provider")
    void testUInt64FromBigInteger(String rawAddress, NetworkType input, boolean validChecksum) {
        Address address = new Address(rawAddress, input);
        assertEquals(input, address.getNetworkType());
        Assertions.assertEquals(validChecksum, Address.isValidPlainAddress(address.plain()));
    }

    @ParameterizedTest
    @MethodSource("publicKeys")
    void isValidAddressFromPublicKeys(String publicKey, NetworkType networkType,
        String input) {
        Address address = Address.createFromPublicKey(publicKey, networkType);
        assertEquals(input, address.plain());
        assertTrue(Address.isValidPlainAddress(address.plain()));
        assertTrue(Address.isValidEncodedAddress(address.encoded()));
    }

    @ParameterizedTest
    @MethodSource("assertExceptionProvider")
    void isValidAddressWhenInvalid(String rawAddress, NetworkType networkType) {
        boolean validPlainAddress = Address.isValidPlainAddress(rawAddress);
        if (validPlainAddress) {
            Assertions.assertNotEquals(networkType,
                Address.createFromRawAddress(rawAddress).getNetworkType());
        } else {
            Assertions.assertFalse(validPlainAddress);
        }
    }


    @ParameterizedTest
    @EnumSource(NetworkType.class)
    void isValidAddressFromGeneratedPublicKey(NetworkType networkType) {
        Account account = Account.generateNewAccount(networkType);
        Assertions.assertTrue(Address.isValidPlainAddress(account.getAddress().plain()));
        Assertions.assertTrue(Address.isValidEncodedAddress(account.getAddress().encoded()));
    }

}
