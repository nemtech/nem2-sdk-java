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

package io.nem.core.utils;

import io.nem.sdk.infrastructure.SerializationUtils;
import java.math.BigInteger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Static class that contains utility functions for converting hex strings to and from bytes.
 */
public class ConvertUtils {

    /**
     * Mask used to fix overflowed longs when they are converted to BigInteger.
     */
    private static final BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE)
        .subtract(BigInteger.ONE);


    /**
     * Private constructor of this utility class.
     */
    private ConvertUtils() {
    }

    /**
     * Converts a hex string to a byte array.
     *
     * @param hexString The input hex string.
     * @return The output byte array.
     */
    public static byte[] getBytes(final String hexString) {
        try {
            return getBytesInternal(hexString);
        } catch (final DecoderException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private static byte[] getBytesInternal(final String hexString) throws DecoderException {
        final Hex codec = new Hex();
        final String paddedHexString = 0 == hexString.length() % 2 ? hexString : "0" + hexString;
        final byte[] encodedBytes = StringEncoder.getBytes(paddedHexString);
        return codec.decode(encodedBytes);
    }

    /**
     * Converts a byte array to a hex string.
     *
     * @param bytes The input byte array.
     * @return The output hex string.
     */
    public static String toHex(final byte[] bytes) {
        final Hex codec = new Hex();
        final byte[] decodedBytes = codec.encode(bytes);
        return StringEncoder.getString(decodedBytes);
    }

    /**
     * Converts an hex back to an byte array.
     *
     * @param hexString the hex string input
     * @return the byte array.
     */
    public static byte[] fromHexToBytes(String hexString) {
        final Hex codec = new Hex();
        try {
            return codec.decode(StringEncoder.getBytes(hexString));
        } catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Converts hex string to a plain string
     *
     * @param hexString The input string.
     * @return The output plain string.
     */
    public static String fromHexToString(final String hexString) {
        if (hexString == null) {
            return null;
        }
        return StringEncoder.getString(fromHexToBytes(hexString));
    }

    /**
     * Converts plain string to an hex string
     *
     * @param plainText The plain input string.
     * @return The output hex string.
     */
    public static String fromStringToHex(final String plainText) {
        if (plainText == null) {
            return null;
        }
        return toHex(StringEncoder.getBytes(plainText));
    }


    /**
     * Converts a number to hex padding zeros up to size 16.
     *
     * @param number The input string.
     * @return the hex 16 characters
     */
    public static String toSize16Hex(final BigInteger number) {
        return String.format("%016x", number);
    }


    /**
     * It converts a signed long into an unsigned BigInteger. It fixes overflow problems that could
     * happen when working with unsigned int 64.
     *
     * @param value the value, positive or negative.
     * @return the positive {@link BigInteger}.
     */
    public static BigInteger toUnsignedBigInteger(long value) {
        return BigInteger.valueOf(value).and(UNSIGNED_LONG_MASK);
    }


}
