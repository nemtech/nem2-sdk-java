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

package io.nem.core.crypto;

import io.nem.core.utils.ExceptionUtils;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.Security;

/**
 * Static class that exposes hash functions.
 */
public class Hashes {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Performs a SHA3-256 hash of the concatenated inputs.
     *
     * @param inputs The byte arrays to concatenate and hash.
     * @return The hash of the concatenated inputs.
     * @throws CryptoException if the hash operation failed.
     */
    public static byte[] sha3_256(final byte[]... inputs) {
        return hash("SHA3-256", inputs);
    }

    /**
     * Performs a SHA3-512 hash of the concatenated inputs.
     *
     * @param inputs The byte arrays to concatenate and hash.
     * @return The hash of the concatenated inputs.
     * @throws CryptoException if the hash operation failed.
     */
    public static byte[] sha3_512(final byte[]... inputs) {
        return hash("SHA3-512", inputs);
    }

    /**
     * Performs a RIPEMD160 hash of the concatenated inputs.
     *
     * @param inputs The byte arrays to concatenate and hash.
     * @return The hash of the concatenated inputs.
     * @throws CryptoException if the hash operation failed.
     */
    public static byte[] ripemd160(final byte[]... inputs) {
        return hash("RIPEMD160", inputs);
    }

    private static byte[] hash(final String algorithm, final byte[]... inputs) {
        return ExceptionUtils.propagate(
                () -> {
                    final MessageDigest digest = MessageDigest.getInstance(algorithm, "SC");

                    for (final byte[] input : inputs) {
                        digest.update(input);
                    }

                    return digest.digest();
                },
                CryptoException::new);
    }
}
