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
            "215b158f0bd416b596271bce527cd9dc8e4a639cc271d896f9156af6f441eeb9",
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
            "1c704e3ac99b124f92d2648649ec72c7a19ea4e2bb24f669b976180a295876fa",
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
            "5dc17b2409d50bcc7c1faa720d0ec8b79a1705d0c517bcc0bdbd316540974d5e",
            ConvertUtils.toHex(builder.getRootHash()));
    }

}
