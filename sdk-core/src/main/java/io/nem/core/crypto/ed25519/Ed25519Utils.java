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

package io.nem.core.crypto.ed25519;

import io.nem.core.crypto.Hashes;
import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.ed25519.arithmetic.Ed25519EncodedFieldElement;
import java.util.Arrays;

/**
 * Utility methods for Ed25519.
 */
public class Ed25519Utils {

    /**
     * Private constructor for this utility class.
     */
    private Ed25519Utils() {
    }

    /**
     * Prepares a private key's raw value for scalar multiplication. The hashing is for achieving
     * better randomness and the clamping prevents small subgroup attacks.
     *
     * @param key The private key.
     * @return The prepared encoded field element.
     */
    public static Ed25519EncodedFieldElement prepareForScalarMultiply(final PrivateKey key) {
        final byte[] hash = Hashes.sha512(key.getBytes());
        final byte[] a = Arrays.copyOfRange(hash, 0, 32);
        a[31] &= 0x7F;
        a[31] |= 0x40;
        a[0] &= 0xF8;
        return new Ed25519EncodedFieldElement(a);
    }
}
