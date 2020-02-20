/*
 * Copyright 2019 NEM
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

package io.nem.core.crypto;


import io.nem.core.utils.ConvertUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testing of {@link MerkleHashBuilder}
 */
public class MerkleHashBuilderTest {


    @Test
    public void testZero() {
        MerkleHashBuilder builder = new MerkleHashBuilder();
        Assertions.assertEquals("0000000000000000000000000000000000000000000000000000000000000000",
            ConvertUtils.toHex(builder.getRootHash()));
    }

    @Test
    public void testOne() {
        MerkleHashBuilder builder = new MerkleHashBuilder();
        builder
            .update(ConvertUtils.fromHexToBytes(
                "215b158f0bd416b596271bce527cd9dc8e4a639cc271d896f9156af6f441eeb9"));
        Assertions.assertEquals(
            "215B158F0BD416B596271BCE527CD9DC8E4A639CC271D896F9156AF6F441EEB9",
            ConvertUtils.toHex(builder.getRootHash()));
    }


    @Test
    public void testTwo() {
        MerkleHashBuilder builder = new MerkleHashBuilder();

        builder
            .update(ConvertUtils.fromHexToBytes(
                "215b158f0bd416b596271bce527cd9dc8e4a639cc271d896f9156af6f441eeb9"));
        builder
            .update(ConvertUtils.fromHexToBytes(
                "976c5ce6bf3f797113e5a3a094c7801c885daf783c50563ffd3ca6a5ef580e25"));

        Assertions.assertEquals(
            "1C704E3AC99B124F92D2648649EC72C7A19EA4E2BB24F669B976180A295876FA",
            ConvertUtils.toHex(builder.getRootHash()));
    }

    @Test
    public void testThree() {
        MerkleHashBuilder builder = new MerkleHashBuilder();

        builder
            .update(ConvertUtils.fromHexToBytes(
                "215b158f0bd416b596271bce527cd9dc8e4a639cc271d896f9156af6f441eeb9"));
        builder
            .update(ConvertUtils.fromHexToBytes(
                "976c5ce6bf3f797113e5a3a094c7801c885daf783c50563ffd3ca6a5ef580e25"));

        builder
            .update(ConvertUtils.fromHexToBytes(
                "e926cc323886d47234bb0b49219c81e280e8a65748b437c2ae83b09b37a5aaf2"));

        Assertions.assertEquals(
            "5DC17B2409D50BCC7C1FAA720D0EC8B79A1705D0C517BCC0BDBD316540974D5E",
            ConvertUtils.toHex(builder.getRootHash()));
    }

}
