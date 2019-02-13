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
 * Static class that contains utility functions for converting Base32 strings to and from bytes.
 */
public class Base32Encoder {

    private static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * Converts a string to a byte array.
     *
     * @param base32String The input Base32 string.
     * @return The output byte array.
     */
    public static byte[] getBytes(final String base32String) {
        String replacedString = base32String.trim().replaceAll("=", "");
        final byte[] convertedBytes = new byte[replacedString.length() * 5 / 8];

        int index = 0;
        int bitCount = 0;
        int current = 0;

        for(char c : replacedString.toCharArray()) {
            final int symbolValue = CHARS.indexOf(c);
            if (symbolValue < 0) {
                throw new IllegalArgumentException(base32String + " is not a valid Base32 string.");
            }
            for (int i = 4; i >= 0; i--) {
                current = (current << 1) + (symbolValue >> i & 0x1);
                bitCount++;

                if (bitCount == 8) {
                    convertedBytes[index++] = (byte)(current);

                    bitCount = 0;
                    current = 0;
                }
            }
        }
        return convertedBytes;
    }

    /**
     * Converts a byte array to a Base32 string.
     *
     * @param bytes The input byte array.
     * @return The output Base32 string.
     */
    public static String getString(final byte[] bytes) {
        StringBuilder convertedString = new StringBuilder();

        int bitCount = 0;
        int current = 0;

        for(Byte b : bytes) {
            final int intValue = b.intValue();
            for (int i = 7; i >= 0; i--) {
                current = (current << 1) + (intValue >> i & 0x1);
                bitCount++;

                if (bitCount == 5) {
                    convertedString.append(CHARS.charAt(current));

                    bitCount = 0;
                    current = 0;
                }
            }
        }
        if (bitCount > 0) {
            current = current << (5 - bitCount);
            convertedString.append(CHARS.charAt(current));
        }

        // padding
        int outputLength = ((bytes.length * 8 + 39) / 40) * 8;
        while(convertedString.length() < outputLength) {
            convertedString.append("=");
        }

        return convertedString.toString();
    }
}
