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

package io.nem.symbol.sdk.infrastructure;


import io.nem.symbol.sdk.model.mosaic.IllegalIdentifierException;
import java.security.SecureRandom;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Utility class to generate random values.
 */
public class RandomUtils {

    /**
     * private constructor.
     */
    private RandomUtils() {

    }

    /**
     * Generates a byte array containing random data.
     *
     * @param numBytes The number of bytes to generate.
     * @return A byte array containing random data.
     */
    public static byte[] generateRandomBytes(int numBytes) {

        try {
            byte[] bytes = new byte[numBytes]; // the array to be filled in with random bytes
            new SecureRandom().nextBytes(bytes);
            return bytes;
        } catch (Exception e) {
            throw new IllegalIdentifierException(ExceptionUtils.getMessage(e), e);
        }
    }

    /**
     * Generates a byte array containing random data.
     *
     * @return A byte array containing random data.
     */
    public static byte[] generateRandomBytes() {
        return generateRandomBytes(214);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code int} value between 0 (inclusive) and the
     * specified value (exclusive)/
     * @param bound the upper bound (exclusive).  Must be positive.
     * @return a random int between 0 (inclusive) and bound (exclusive)
     */
    public static int generateRandomInt(int bound) {
        return new SecureRandom().nextInt(bound);
    }
}
