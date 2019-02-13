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


/**
 * Static class that contains utility functions for converting hex strings to and from bytes.
 */
public class HexEncoder {

    /**
     * Converts a hex string to a byte array.
     *
     * @param hexString The input hex string.
     * @return The output byte array.
     */
    public static byte[] getBytes(final String hexString) {
        return getBytesInternal(hexString);
    }

    /**
     * Tries to convert a hex string to a byte array.
     *
     * @param hexString The input hex string.
     * @return The output byte array or null if the input string is malformed.
     */
    public static byte[] tryGetBytes(final String hexString) {
        try {
            return getBytesInternal(hexString);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    private static byte[] getBytesInternal(final String hexString) throws IllegalArgumentException {
        final String paddedHexString = 0 == hexString.length() % 2 ? hexString : "0" + hexString;

        final int length = paddedHexString.length();
        final byte[] encodedBytes = new byte[length / 2];
        int i = 0;
        while (i < length) {
            final int upper = Character.digit(paddedHexString.charAt(i), 16) << 4;
            final int lower = Character.digit(paddedHexString.charAt(i + 1), 16);
            if ((upper < 0) || (lower < 0)){
                throw new IllegalArgumentException(hexString + " is not a valid hex string.");
            }

            encodedBytes[i / 2] = (byte)(lower + upper);
            i += 2;
        }

        return encodedBytes;
    }

    /**
     * Converts a byte array to a hex string.
     *
     * @param bytes The input byte array.
     * @return The output hex string.
     */
    public static String getString(final byte[] bytes) {
        final StringBuilder encodedString = new StringBuilder();
        for(byte b : bytes) {
            encodedString.append(String.format("%02x", b));
        }
        return encodedString.toString();
    }
}
