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

import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Mosaic nonce class
 */
public class MosaicNonce {

    /**
     * The number of bytes of the nonce.
     */
    private static final int NO_OF_RANDOM_BYTES = 4;
    /**
     * Mosaic nonce
     */
    private final byte[] nonce;

    /**
     * Create MosaicNonce from byte array
     *
     * @param nonce the nonce as byte array.
     */
    public MosaicNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    /**
     * Create a random MosaicNonce
     *
     * @return MosaicNonce nonce
     */
    public static MosaicNonce createRandom() {
        return new MosaicNonce(RandomUtils.generateRandomBytes(NO_OF_RANDOM_BYTES));
    }

    /**
     * Create a MosaicNonce from hexadecimal notation.
     *
     * @param hex the hex value.
     * @return MosaicNonce
     */
    public static MosaicNonce createFromHex(String hex) {
        final byte[] bytes = ConvertUtils.fromHexToBytes(hex);
        if (bytes.length != 4) {
            throw new IllegalIdentifierException(
                "Expected 4 bytes for Nonce but got " + bytes.length + " instead.");
        }
        return new MosaicNonce(bytes);
    }

    /**
     * Create a MosaicNonce from a BigInteger.
     *
     * @param number the nonce as number.
     * @return MosaicNonce
     */
    public static MosaicNonce createFromBigInteger(BigInteger number) {
        return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(number, 4));
    }

    /**
     * Create a MosaicNonce from a Integer.
     *
     * @param number the nonce as number.
     * @return MosaicNonce
     */
    public static MosaicNonce createFromInteger(Integer number) {
        return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(BigInteger.valueOf(number), 4));
    }

    /**
     * @return nonce
     */
    public byte[] getNonce() {
        return nonce;
    }

    /**
     * @return nonce int
     */
    public int getNonceAsInt() {
        return ByteUtils.bytesToInt(this.nonce);
    }

    /**
     * @return nonce long
     */
    public long getNonceAsLong() {
        return Integer.toUnsignedLong(getNonceAsInt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MosaicNonce that = (MosaicNonce) o;
        return Arrays.equals(nonce, that.nonce);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nonce);
    }
}
