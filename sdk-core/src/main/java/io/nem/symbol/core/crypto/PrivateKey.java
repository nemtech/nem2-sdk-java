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

import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import java.math.BigInteger;

/**
 * Represents a private key.
 */
public class PrivateKey {

    private final BigInteger value;

    /**
     * Creates a new private key.
     *
     * @param value The raw private key value.
     */
    public PrivateKey(final BigInteger value) {
        this.value = value;
    }

    /**
     * Creates a private key from a hex string.
     *
     * @param hex The hex string.
     * @return The new private key.
     */
    public static PrivateKey fromHexString(final String hex) {
        try {
            return new PrivateKey(new BigInteger(ConvertUtils.getBytes(hex)));
        } catch (final IllegalArgumentException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Creates a private key from a decimal string.
     *
     * @param decimal The decimal string.
     * @return The new private key.
     */
    public static PrivateKey fromDecimalString(final String decimal) {
        try {
            return new PrivateKey(new BigInteger(decimal, 10));
        } catch (final NumberFormatException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Gets the raw private key value.
     *
     * @return The raw private key value.
     */
    public BigInteger getRaw() {
        return this.value;
    }

    public byte[] getBytes() {
        return ByteUtils.bigIntToByteArrayLeadingZeros(this.value, 32);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof PrivateKey)) {
            return false;
        }

        final PrivateKey rhs = (PrivateKey) obj;
        return this.value.equals(rhs.value);
    }

    /**
     * @return the hex representation of the private key.
     */
    public String toHex() {
        return ConvertUtils.toHex(getBytes());
    }

}
