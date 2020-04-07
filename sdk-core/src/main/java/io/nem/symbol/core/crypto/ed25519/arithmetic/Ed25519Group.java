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

package io.nem.symbol.core.crypto.ed25519.arithmetic;

import io.nem.symbol.core.utils.ConvertUtils;
import java.math.BigInteger;

/**
 * Represents the underlying group for Ed25519.
 */
public class Ed25519Group {

    /**
     * Private constructor for this utility class.
     */
    private Ed25519Group() {
    }

    /**
     * 2^252 - 27742317777372353535851937790883648493
     */
    public static final BigInteger GROUP_ORDER =
        BigInteger.ONE.shiftLeft(252).add(new BigInteger("27742317777372353535851937790883648493"));

    /**
     * <pre>{@code
     * (x, 4/5); x > 0
     * }</pre>
     */
    public static final Ed25519GroupElement BASE_POINT = getBasePoint();

    // different representations of zero
    public static final Ed25519GroupElement ZERO_P3 =
        Ed25519GroupElement.p3(
            Ed25519Field.ZERO, Ed25519Field.ONE, Ed25519Field.ONE, Ed25519Field.ZERO);
    public static final Ed25519GroupElement ZERO_P2 =
        Ed25519GroupElement.p2(Ed25519Field.ZERO, Ed25519Field.ONE, Ed25519Field.ONE);
    public static final Ed25519GroupElement ZERO_PRECOMPUTED =
        Ed25519GroupElement.precomputed(Ed25519Field.ONE, Ed25519Field.ONE, Ed25519Field.ZERO);

    private static Ed25519GroupElement getBasePoint() {
        final byte[] rawEncodedGroupElement =
            ConvertUtils.getBytes("5866666666666666666666666666666666666666666666666666666666666666");
        final Ed25519GroupElement basePoint =
            new Ed25519EncodedGroupElement(rawEncodedGroupElement).decode();
        basePoint.precomputeForScalarMultiplication();
        basePoint.precomputeForDoubleScalarMultiplication();
        return basePoint;
    }
}