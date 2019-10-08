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

import io.nem.core.utils.ArrayUtils;
import io.nem.core.utils.ConvertUtils;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * A EC signature.
 */
public class Signature {

    private static final BigInteger MAXIMUM_VALUE =
        BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

    private final byte[] r;
    private final byte[] s;

    /**
     * Creates a new signature.
     *
     * @param r The r-part of the signature.
     * @param s The s-part of the signature.
     */
    public Signature(final BigInteger r, final BigInteger s) {
        if (0 < r.compareTo(MAXIMUM_VALUE) || 0 < s.compareTo(MAXIMUM_VALUE)) {
            throw new IllegalArgumentException("r and s must fit into 32 bytes");
        }

        this.r = ArrayUtils.toByteArray(r, 32);
        this.s = ArrayUtils.toByteArray(s, 32);
    }

    /**
     * Creates a new signature.
     *
     * @param bytes The binary representation of the signature.
     */
    public Signature(final byte[] bytes) {
        if (64 != bytes.length) {
            throw new IllegalArgumentException("binary signature representation must be 64 bytes");
        }

        final byte[][] parts = ArrayUtils.split(bytes, 32);
        this.r = parts[0];
        this.s = parts[1];
    }

    /**
     * Creates a new signature.
     *
     * @param r The binary representation of r.
     * @param s The binary representation of s.
     */
    public Signature(final byte[] r, final byte[] s) {
        if (32 != r.length || 32 != s.length) {
            throw new IllegalArgumentException(
                "binary signature representation of r and s must both have 32 bytes length");
        }

        this.r = r;
        this.s = s;
    }

    /**
     * Gets the r-part of the signature.
     *
     * @return The r-part of the signature.
     */
    public BigInteger getR() {
        return ArrayUtils.toBigInteger(this.r);
    }

    /**
     * Gets the r-part of the signature.
     *
     * @return The r-part of the signature.
     */
    public byte[] getBinaryR() {
        return this.r;
    }

    /**
     * Gets the s-part of the signature.
     *
     * @return The s-part of the signature.
     */
    public BigInteger getS() {
        return ArrayUtils.toBigInteger(this.s);
    }

    /**
     * Gets the s-part of the signature.
     *
     * @return The s-part of the signature.
     */
    public byte[] getBinaryS() {
        return this.s;
    }

    /**
     * Gets a little-endian 64-byte representation of the signature.
     *
     * @return a little-endian 64-byte representation of the signature
     */
    public byte[] getBytes() {
        return ArrayUtils.concat(this.r, this.s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Signature signature = (Signature) o;
        return Arrays.equals(r, signature.r) &&
            Arrays.equals(s, signature.s);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(r);
        result = 31 * result + Arrays.hashCode(s);
        return result;
    }

    @Override
    public String toString() {
        return ConvertUtils.toHex(this.getBytes());
    }
}
