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

package io.nem.symbol.core.crypto;

import java.math.BigInteger;

/**
 * Represents a private key.
 */
public class PrivateKey extends Key {

    /**
     * The size of Symbol's private keys .
     */
    public static final int SIZE = 32;

    /**
     * Creates a new private key from a big int value.
     *
     * @param value the big int.
     */
    public PrivateKey(final BigInteger value) {
        super(value, SIZE);
    }

    public PrivateKey(byte[] bytes) {
        super(bytes, SIZE);
    }

    public PrivateKey(String hex) {
        super(hex, SIZE);
    }

    /**
     * Creates a private key from a hex string.
     *
     * @param hex The hex string.
     * @return The new private key.
     */
    public static PrivateKey fromHexString(final String hex) {
        return new PrivateKey(hex);
    }

    /**
     * Creates a private key from a decimal string.
     *
     * @param decimal The decimal string.
     * @return The new private key.
     */
    public static PrivateKey fromDecimalString(final String decimal) {
        return new PrivateKey(new BigInteger(decimal, 10));
    }

}
