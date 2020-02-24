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

import io.nem.symbol.core.utils.ConvertUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Represents a public key.
 */
public class PublicKey {

    private final byte[] value;

    /**
     * Creates a new public key.
     *
     * @param bytes The raw public key value.
     */
    public PublicKey(final byte[] bytes) {
        this.value = bytes;
    }

    /**
     * Creates a public key from a hex string.
     *
     * @param hex The hex string.
     * @return The new public key.
     */
    public static PublicKey fromHexString(final String hex) {
        try {
            return new PublicKey(ConvertUtils.getBytes(hex));
        } catch (final IllegalArgumentException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Gets the raw public key value.
     *
     * @return The raw public key value.
     */
    public byte[] getBytes() {
        return this.value;
    }

    /**
     * Gets raw public key value.
     *
     * @return The raw public key value.
     */
    public ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublicKey publicKey = (PublicKey) o;
        return Arrays.equals(value, publicKey.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    /**
     * @return the hex representation of the public key.
     */
    public String toHex() {
        return ConvertUtils.toHex(this.value).toUpperCase();
    }


    @Override
    public String toString() {
        return "PublicKey{" + toHex() + '}';
    }
}
