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

package io.nem.symbol.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigInteger;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MosaicNonceTest {

    @Test
    void createRandomNonce() {
        MosaicNonce nonce = MosaicNonce.createRandom();
        assertNotNull(nonce.getNonce());
    }

    @Test
    void createRandomNonceTwiceNotTheSame() {
        MosaicNonce nonce1 = MosaicNonce.createRandom();
        MosaicNonce nonce2 = MosaicNonce.createRandom();
        assertNotNull(nonce1.getNonce());
        assertNotNull(nonce2.getNonce());
        assertFalse(Arrays.equals(nonce1.getNonce(), nonce2.getNonce()));
    }

    @Test
    void createNonceFromHexadecimalString() {
        MosaicNonce nonce = MosaicNonce.createFromHex("00000000");
        assertNotNull(nonce.getNonce());
        assertArrayEquals(new byte[]{0x0, 0x0, 0x0, 0x0}, nonce.getNonce());
    }

    @Test
    void shouldFailWhenCreatingFromAndInvalidHex() {
        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class,
                () -> MosaicNonce.createFromHex("Z0000000"));
        Assertions.assertEquals("Z0000000 could not be decoded. DecoderException: Illegal hexadecimal character Z at index 0",
            exception.getMessage());
    }

    @Test
    void createNonceFromHexadecimalStringTwiceNotTheSame() {
        MosaicNonce nonce1 = MosaicNonce.createFromHex("00000000");
        MosaicNonce nonce2 = MosaicNonce.createFromHex("FFFFFFFF");
        assertNotNull(nonce1.getNonce());
        assertNotNull(nonce2.getNonce());
        assertArrayEquals(new byte[]{0x0, 0x0, 0x0, 0x0}, nonce1.getNonce());
        assertArrayEquals(new byte[]{-0x1, -0x1, -0x1, -0x1}, nonce2.getNonce());
        assertFalse(Arrays.equals(nonce1.getNonce(), nonce2.getNonce()));
    }

    @Test
    void createNonceFromBigInteger() {
        MosaicNonce nonce1 = MosaicNonce.createFromBigInteger(new BigInteger("0"));
        MosaicNonce nonce2 = MosaicNonce.createFromBigInteger(new BigInteger("4294967295"));
        assertNotNull(nonce1.getNonce());
        assertNotNull(nonce2.getNonce());
        assertEquals(0, nonce1.getNonceAsInt());
        assertEquals(nonce2.getNonceAsInt(), new BigInteger("4294967295").intValue());
        assertArrayEquals(nonce1.getNonce(), MosaicNonce.createFromHex("00000000").getNonce());
        assertArrayEquals(nonce2.getNonce(), MosaicNonce.createFromHex("FFFFFFFF").getNonce());
    }
}
