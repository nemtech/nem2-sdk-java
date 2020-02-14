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

import org.apache.commons.codec.binary.Base32;

/**
 * Static class that contains utility functions for converting Base32 strings to and from bytes.
 */
public class Base32Encoder {

    /**
     * Private constructor for this utility class.
     */
    private Base32Encoder() {
    }

    /**
     * Converts a string to a byte array.
     *
     * @param base32String The input Base32 string.
     * @return The output byte array.
     */
    public static byte[] getBytes(final String base32String) {
        final Base32 codec = new Base32();
        final byte[] encodedBytes = StringEncoder.getBytes(base32String);
        if (!codec.isInAlphabet(encodedBytes, true)) {
            throw new IllegalArgumentException("malformed base32 string passed to getBytes");
        }
        return codec.decode(encodedBytes);
    }

    /**
     * Converts a byte array to a Base32 string.
     *
     * @param bytes The input byte array.
     * @return The output Base32 string.
     */
    public static String getString(final byte[] bytes) {
        final Base32 codec = new Base32();
        final byte[] decodedBytes = codec.encode(bytes);
        return StringEncoder.getString(decodedBytes);
    }
}
