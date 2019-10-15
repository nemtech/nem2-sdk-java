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
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import java.util.Arrays;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapperUtilsTest {

    @Test
    void shouldMapToNamespaceId() {
        Assertions.assertNull(MapperUtils.toNamespaceId(null));
        Assertions
            .assertEquals(BigInteger.valueOf(1194684), MapperUtils.toNamespaceId("123ABC").getId());
    }

    @Test
    void shouldMapToAddress() {
        Assertions.assertNull(MapperUtils.toAddress(null));
        Address address = MapperUtils
            .toAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");
        Assertions.assertNotNull(address);
        Assert.assertEquals("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX", address.plain());
        Assert.assertEquals("SBCPGZ-3S2SCC-3YHBBT-YDCUZV-4ZZEPH-M2KGCP-4QXX", address.pretty());
    }

    @Test
    void shouldMapToAddressFromUnresolved() {

        Assertions.assertNull(MapperUtils.toAddressFromUnresolved(null));
        Address address = MapperUtils
            .toAddressFromUnresolved("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");
        Assertions.assertNotNull(
            address);

        Assert.assertEquals("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", address.plain());
        Assert.assertEquals("SBILTA-367K2L-X2FEXG-5TFWAS-7GEFYA-GY7QLF-BYKC", address.pretty());
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

}
