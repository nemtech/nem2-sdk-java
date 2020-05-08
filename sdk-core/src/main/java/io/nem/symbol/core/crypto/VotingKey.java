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
 * Represents a public key.
 */
public class VotingKey extends Key {

    /**
     * The size of Symbol's voting keys.
     */
    public static final int SIZE = 48;

    /**
     * Creates a new voting key.
     *
     * @param bytes The raw voting key value.
     */
    public VotingKey(byte[] bytes) {
        super(bytes, SIZE);
    }

    /**
     * Creates a new voting key from a big int value
     *
     * @param value the value
     */
    public VotingKey(BigInteger value) {
        super(value, SIZE);
    }

    /**
     * Creates a new voting key from an hex.
     *
     * @param hex the hex.
     */
    public VotingKey(String hex) {
        super(hex, SIZE);
    }

    /**
     * Creates a voting key from a hex string.
     *
     * @param hex The hex string.
     * @return The new voting key.
     */
    public static VotingKey fromHexString(final String hex) {
        return new VotingKey(hex);
    }

}
