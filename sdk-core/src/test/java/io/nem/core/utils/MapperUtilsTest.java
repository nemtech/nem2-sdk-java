/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.core.utils;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapperUtilsTest {

    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @Test
    void shouldMapToNamespaceId() {
        Assertions.assertNull(MapperUtils.toNamespaceId(null));
        Assertions
            .assertEquals(BigInteger.valueOf(1194684), MapperUtils.toNamespaceId("123ABC").getId());
    }

    @Test
    void shouldMapToAddress() {
        Assertions.assertNull(MapperUtils.toAddressFromRawAddress(null));
        Address address = MapperUtils
            .toAddressFromRawAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");
        Assertions.assertNotNull(address);
        Assertions.assertEquals("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX", address.plain());
        Assertions.assertEquals("SBCPGZ-3S2SCC-3YHBBT-YDCUZV-4ZZEPH-M2KGCP-4QXX", address.pretty());
    }

    @Test
    void toAddressFromEncoded() {

        Assertions.assertNull(MapperUtils.toAddressFromEncoded(null));
        Address address = MapperUtils
            .toAddressFromEncoded("9050b9837efab4bbe8a4b9bb32d812f9885c00d8fc1650e142");
        Assertions.assertNotNull(
            address);

        Assertions.assertEquals("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", address.plain());
        Assertions.assertEquals("SBILTA-367K2L-X2FEXG-5TFWAS-7GEFYA-GY7QLF-BYKC", address.pretty());
    }

    @Test
    void shouldMapToMosaicId() {
        Assertions.assertNull(MapperUtils.toMosaicId(null));
        Assertions
            .assertEquals(BigInteger.valueOf(1194684), MapperUtils.toMosaicId("123ABC").getId());
    }


    @Test
    void extractTransactionVersion() {
        Assertions
            .assertEquals(1, MapperUtils.extractTransactionVersion(36865));
        Assertions
            .assertEquals(11, MapperUtils.extractTransactionVersion(36875));
    }

    @Test
    void extractNetworkType() {
        Assertions
            .assertEquals(NetworkType.MIJIN_TEST, MapperUtils.extractNetworkType(36865));
    }

    @Test
    void toNetworkVersion() {
        Assertions
            .assertEquals(36865, MapperUtils.toNetworkVersion(NetworkType.MIJIN_TEST, 1));

        Arrays.stream(NetworkType.values()).forEach(networkType -> {
            int version = RandomUtils.nextInt(1, 100);

            Assertions.assertEquals(networkType,
                MapperUtils.extractNetworkType(MapperUtils.toNetworkVersion(networkType,
                    version)));

            Assertions.assertEquals(version,
                MapperUtils.extractTransactionVersion(MapperUtils.toNetworkVersion(networkType,
                    version)));
        });


    }


    @Test
    void toUnresolvedAddress() {

        NamespaceId namespaceId = NamespaceId.createFromName("some.name");

        Assertions.assertEquals("91d9e338f78767ed9500000000000000000000000000000000",
            namespaceId.encoded(networkType));
        Assertions
            .assertEquals(namespaceId.encoded(networkType),
                MapperUtils.toUnresolvedAddress(namespaceId.encoded(networkType)).encoded(
                    networkType));

        Address address = Address.createFromRawAddress("MCTVW23D2MN5VE4AQ4TZIDZENGNOZXPRPR72DYSX");
        Assertions.assertNull(MapperUtils.toUnresolvedAddress(null));

        Assertions.assertEquals(address, MapperUtils.toUnresolvedAddress(address.encoded(
            networkType)));

        address = Address
            .createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC");

        Assertions
            .assertEquals(address,
                MapperUtils.toUnresolvedAddress(address.encoded(networkType)));
    }


    @Test
    void toUnresolvedAddressZeroPadded() {

        UnresolvedAddress actual = MapperUtils
            .toUnresolvedAddress("01E7CA7E22727DDD8800000000000000000000000000000000");
        Assertions.assertTrue(actual instanceof NamespaceId);
    }

    @Test
    void toUnresolvedMosaicId() {
        MosaicId mosaicId = new MosaicId("11F4B1B3AC033DB5");
        NamespaceId namespaceId = NamespaceId.createFromName("some.name123");

        Assertions.assertNull(MapperUtils.toUnresolvedMosaicId(null));
        Assertions.assertEquals(mosaicId, MapperUtils.toUnresolvedMosaicId(mosaicId.getIdAsHex()));
        Assertions
            .assertEquals(namespaceId, MapperUtils.toUnresolvedMosaicId(namespaceId.getIdAsHex()));

        Assertions
            .assertEquals(new NamespaceId("9a52fde35777cd4f"),
                MapperUtils.toUnresolvedMosaicId("9a52fde35777cd4f"));

    }

    @Test
    public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        Assertions.assertNotNull(generatedString);
    }

}