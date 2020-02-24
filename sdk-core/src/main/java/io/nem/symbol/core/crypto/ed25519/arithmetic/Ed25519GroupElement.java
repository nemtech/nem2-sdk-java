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

import io.nem.symbol.core.utils.ByteUtils;
import java.io.Serializable;
import java.util.Objects;

/**
 * A point on the ED25519 curve which represents a group element. This implementation is based on
 * the ref10 implementation of SUPERCOP. <br> Literature: [1] Daniel J. Bernstein, Niels Duif, Tanja
 * Lange, Peter Schwabe and Bo-Yin Yang : High-speed high-security signatures [2] Huseyin Hisil,
 * Kenneth Koon-Ho Wong, Gary Carter, Ed Dawson: Twisted Edwards Curves Revisited [3] Daniel J.
 * Bernsteina, Tanja Lange: A complete set of addition laws for incomplete Edwards curves [4] Daniel
 * J. Bernstein, Peter Birkner, Marc Joye, Tanja Lange and Christiane Peters: Twisted Edwards Curves
 * [5] Christiane Pascale Peters: Curves, Codes, and Cryptography (PhD thesis) [6] Daniel J.
 * Bernstein, Peter Birkner, Tanja Lange and Christiane Peters: Optimizing double-base
 * elliptic-curve single-scalar multiplication
 */
public class Ed25519GroupElement implements Serializable {

    private final CoordinateSystem coordinateSystem;

    @SuppressWarnings("NonConstantFieldWithUpperCaseName")
    private final Ed25519FieldElement x;

    @SuppressWarnings("NonConstantFieldWithUpperCaseName")
    private final Ed25519FieldElement y;

    @SuppressWarnings("NonConstantFieldWithUpperCaseName")
    private final Ed25519FieldElement z;

    @SuppressWarnings("NonConstantFieldWithUpperCaseName")
    private final Ed25519FieldElement t;

    /**
     * Precomputed table for a single scalar multiplication.
     */
    private Ed25519GroupElement[][] precomputedForSingle;

    /**
     * Precomputed table for a double scalar multiplication
     */
    private Ed25519GroupElement[] precomputedForDouble;

    // region constructors

    /**
     * Creates a group element for a curve.
     *
     * @param coordinateSystem The coordinate system used for the group element.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @param t The t coordinate.
     */
    public Ed25519GroupElement(
        final CoordinateSystem coordinateSystem,
        final Ed25519FieldElement x,
        final Ed25519FieldElement y,
        final Ed25519FieldElement z,
        final Ed25519FieldElement t) {
        this.coordinateSystem = coordinateSystem;
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
    }

    /**
     * Creates a new group element using the AFFINE coordinate system.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The Z coordinate.
     * @return The group element using the P2 coordinate system.
     */
    public static Ed25519GroupElement affine(
        final Ed25519FieldElement x, final Ed25519FieldElement y, final Ed25519FieldElement z) {
        return new Ed25519GroupElement(CoordinateSystem.AFFINE, x, y, z, null);
    }

    /**
     * Creates a new group element using the P2 coordinate system.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The group element using the P2 coordinate system.
     */
    public static Ed25519GroupElement p2(
        final Ed25519FieldElement x, final Ed25519FieldElement y, final Ed25519FieldElement z) {
        return new Ed25519GroupElement(CoordinateSystem.P2, x, y, z, null);
    }

    /**
     * Creates a new group element using the P3 coordinate system.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @param t The T coordinate.
     * @return The group element using the P3 coordinate system.
     */
    public static Ed25519GroupElement p3(
        final Ed25519FieldElement x,
        final Ed25519FieldElement y,
        final Ed25519FieldElement z,
        final Ed25519FieldElement t) {
        return new Ed25519GroupElement(CoordinateSystem.P3, x, y, z, t);
    }

    /**
     * Creates a new group element using the P1xP1 coordinate system.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @param t The T coordinate.
     * @return The group element using the P1xP1 coordinate system.
     */
    public static Ed25519GroupElement p1xp1(
        final Ed25519FieldElement x,
        final Ed25519FieldElement y,
        final Ed25519FieldElement z,
        final Ed25519FieldElement t) {
        return new Ed25519GroupElement(CoordinateSystem.P1xP1, x, y, z, t);
    }

    /**
     * Creates a new group element using the PRECOMPUTED coordinate system.
     *
     * @param yPlusx The y + x value.
     * @param yMinusx The y - x value.
     * @param xy2d The 2 * d * x * y value.
     * @return The group element using the PRECOMPUTED coordinate system.
     */
    public static Ed25519GroupElement precomputed(
        final Ed25519FieldElement yPlusx,
        final Ed25519FieldElement yMinusx,
        final Ed25519FieldElement xy2d) {
        //noinspection SuspiciousNameCombination
        return new Ed25519GroupElement(CoordinateSystem.PRECOMPUTED, yPlusx, yMinusx, xy2d, null);
    }

    /**
     * Creates a new group element using the CACHED coordinate system.
     *
     * @param yPlusX The Y + X value.
     * @param yMinusX The Y - X value.
     * @param z The Z coordinate.
     * @param t2D The 2 * d * T value.
     * @return The group element using the CACHED coordinate system.
     */
    public static Ed25519GroupElement cached(
        final Ed25519FieldElement yPlusX,
        final Ed25519FieldElement yMinusX,
        final Ed25519FieldElement z,
        final Ed25519FieldElement t2D) {
        return new Ed25519GroupElement(CoordinateSystem.CACHED, yPlusX, yMinusX, z, t2D);
    }

    // endregion

    // region accessors

    /**
     * Convert a to 2^16 bit representation.
     *
     * @param encoded The encode field element.
     * @return 64 bytes, each between -8 and 7
     */
    private static byte[] toRadix16(final Ed25519EncodedFieldElement encoded) {
        final byte[] a = encoded.getRaw();
        final byte[] e = new byte[64];
        int i;
        for (i = 0; i < 32; i++) {
            e[2 * i] = (byte) (a[i] & 15);
            e[2 * i + 1] = (byte) ((a[i] >> 4) & 15);
        }
        /* each e[i] is between 0 and 15 */
        /* e[63] is between 0 and 7 */
        int carry = 0;
        for (i = 0; i < 63; i++) {
            e[i] += carry;
            carry = e[i] + 8;
            carry >>= 4;
            e[i] -= carry << 4;
        }
        e[63] += carry;

        return e;
    }

    /**
     * Calculates a sliding-windows base 2 representation for a given encoded field element a. To
     * learn more about it see [6] page 8. <br> Output: r which satisfies a = r0 * 2^0 + r1 * 2^1 +
     * ... + r255 * 2^255 with ri in {-15, -13, -11, -9, -7, -5, -3, -1, 0, 1, 3, 5, 7, 9, 11, 13,
     * 15} <br> Method is package private only so that tests run.
     *
     * @param encoded The encoded field element.
     * @return The byte array r in the above described form.
     */
    @SuppressWarnings({"squid:S3034","squid:S3776"})
    private static byte[] slide(final Ed25519EncodedFieldElement encoded) {
        final byte[] a = encoded.getRaw();
        final byte[] r = new byte[256];

        // Put each bit of 'a' into a separate byte, 0 or 1
        for (int i = 0; i < 256; ++i) {
            r[i] = (byte) (1 & (a[i >> 3] >> (i & 7)));
        }

        // Note: r[i] will always be odd.
        for (int i = 0; i < 256; ++i) {
            if (r[i] != 0) {
                for (int b = 1; b <= 6 && i + b < 256; ++b) {
                    // Accumulate bits if possible
                    if (r[i + b] != 0) {
                        if (r[i] + (r[i + b] << b) <= 15) {
                            r[i] += r[i + b] << b;
                            r[i + b] = 0;
                        } else if (r[i] - (r[i + b] << b) >= -15) {
                            r[i] -= r[i + b] << b;
                            for (int k = i + b; k < 256; ++k) {
                                if (r[k] == 0) {
                                    r[k] = 1;
                                    break;
                                }
                                r[k] = 0;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        return r;
    }

    /**
     * Gets the coordinate system for the group element.
     *
     * @return The coordinate system.
     */
    public CoordinateSystem getCoordinateSystem() {
        return this.coordinateSystem;
    }

    /**
     * Gets the X value of the group element. This is for most coordinate systems the projective X
     * coordinate.
     *
     * @return The X value.
     */
    public Ed25519FieldElement getX() {
        return this.x;
    }

    /**
     * Gets the Y value of the group element. This is for most coordinate systems the projective Y
     * coordinate.
     *
     * @return The Y value.
     */
    public Ed25519FieldElement getY() {
        return this.y;
    }

    /**
     * Gets the Z value of the group element. This is for most coordinate systems the projective Z
     * coordinate.
     *
     * @return The Z value.
     */
    public Ed25519FieldElement getZ() {
        return this.z;
    }

    /**
     * Gets the T value of the group element. This is for most coordinate systems the projective T
     * coordinate.
     *
     * @return The T value.
     */
    public Ed25519FieldElement getT() {
        return this.t;
    }

    /**
     * Gets a value indicating whether or not the group element has a precomputed table for double
     * scalar multiplication.
     *
     * @return true if it has the table, false otherwise.
     */
    public boolean isPrecomputedForDoubleScalarMultiplication() {
        return null != this.precomputedForDouble;
    }

    // endregion

    /**
     * Gets the table with the precomputed group elements for single scalar multiplication.
     *
     * @return The precomputed table.
     */
    public Ed25519GroupElement[][] getPrecomputedForSingle() {
        return this.precomputedForSingle;
    }

    /**
     * Gets the table with the precomputed group elements for double scalar multiplication.
     *
     * @return The precomputed table.
     */
    public Ed25519GroupElement[] getPrecomputedForDouble() {
        return this.precomputedForDouble;
    }

    /**
     * Converts the group element to an encoded point on the curve.
     *
     * @return The encoded point as byte array.
     */
    public Ed25519EncodedGroupElement encode() {
        switch (this.coordinateSystem) {
            case P2:
            case P3:
                final Ed25519FieldElement inverse = this.z.invert();
                final Ed25519FieldElement xElement = this.x.multiply(inverse);
                final Ed25519FieldElement yElement = this.y.multiply(inverse);
                final byte[] s = yElement.encode().getRaw();
                s[s.length - 1] |= (xElement.isNegative() ? (byte) 0x80 : 0);

                return new Ed25519EncodedGroupElement(s);
            default:
                return this.toP2().encode();
        }
    }

    /**
     * Converts the group element to the P2 coordinate system.
     *
     * @return The group element in the P2 coordinate system.
     */
    public Ed25519GroupElement toP2() {
        return this.toCoordinateSystem(CoordinateSystem.P2);
    }

    /**
     * Converts the group element to the P3 coordinate system.
     *
     * @return The group element in the P3 coordinate system.
     */
    public Ed25519GroupElement toP3() {
        return this.toCoordinateSystem(CoordinateSystem.P3);
    }

    /**
     * Converts the group element to the CACHED coordinate system.
     *
     * @return The group element in the CACHED coordinate system.
     */
    public Ed25519GroupElement toCached() {
        return this.toCoordinateSystem(CoordinateSystem.CACHED);
    }

    /**
     * Convert a Ed25519GroupElement from one coordinate system to another. <br> Supported
     * conversions: - P3 -> P2 - P3 -> CACHED (1 multiply, 1 add, 1 subtract) - P1xP1 -> P2 (3
     * multiply) - P1xP1 -> P3 (4 multiply)
     *
     * @param newCoordinateSystem The coordinate system to convert to.
     * @return A new group element in the new coordinate system.
     */
    private Ed25519GroupElement toCoordinateSystem(final CoordinateSystem newCoordinateSystem) {
        switch (this.coordinateSystem) {
            case P2:
                if (newCoordinateSystem == CoordinateSystem.P2) {
                    return p2(this.x, this.y, this.z);
                }
                throw new IllegalArgumentException();
            case P3:
                switch (newCoordinateSystem) {
                    case P2:
                        return p2(this.x, this.y, this.z);
                    case P3:
                        return p3(this.x, this.y, this.z, this.t);
                    case CACHED:
                        return cached(
                            this.y.add(this.x),
                            this.y.subtract(this.x),
                            this.z,
                            this.t.multiply(Ed25519Field.D_Times_TWO));
                    default:
                        throw new IllegalArgumentException();
                }
            case P1xP1:
                switch (newCoordinateSystem) {
                    case P2:
                        return p2(this.x.multiply(this.t), this.y.multiply(this.z),
                            this.z.multiply(this.t));
                    case P3:
                        return p3(
                            this.x.multiply(this.t),
                            this.y.multiply(this.z),
                            this.z.multiply(this.t),
                            this.x.multiply(this.y));
                    case P1xP1:
                        return p1xp1(this.x, this.y, this.z, this.t);
                    default:
                        throw new IllegalArgumentException();
                }
            case PRECOMPUTED:
                if (newCoordinateSystem
                    == CoordinateSystem.PRECOMPUTED) {//noinspection SuspiciousNameCombination
                    return precomputed(this.x, this.y, this.z);
                }
                throw new IllegalArgumentException();
            case CACHED:
                if (newCoordinateSystem == CoordinateSystem.CACHED) {
                    return cached(this.x, this.y, this.z, this.t);
                }
                throw new IllegalArgumentException();
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Precomputes the group elements needed to speed up a scalar multiplication.
     */
    public void precomputeForScalarMultiplication() {
        if (null != this.precomputedForSingle) {
            return;
        }

        Ed25519GroupElement bi = this;
        this.precomputedForSingle = new Ed25519GroupElement[32][8];

        for (int i = 0; i < 32; i++) {
            Ed25519GroupElement bij = bi;
            for (int j = 0; j < 8; j++)  {
                final Ed25519FieldElement inverse = bij.z.invert();
                final Ed25519FieldElement xElement = bij.x.multiply(inverse);
                final Ed25519FieldElement yElement = bij.y.multiply(inverse);
                this.precomputedForSingle[i][j] =
                    precomputed(yElement.add(xElement), yElement.subtract(xElement),
                        xElement.multiply(yElement).multiply(Ed25519Field.D_Times_TWO));
                bij = bij.add(bi.toCached()).toP3();
            }
            // Only every second summand is precomputed (16^2 = 256).
            for (int k = 0; k < 8; k++) {
                bi = bi.add(bi.toCached()).toP3();
            }
        }
    }

    /**
     * Precomputes the group elements used to speed up a double scalar multiplication.
     */
    public void precomputeForDoubleScalarMultiplication() {
        if (null != this.precomputedForDouble) {
            return;
        }
        Ed25519GroupElement bi = this;
        this.precomputedForDouble = new Ed25519GroupElement[8];
        for (int i = 0; i < 8; i++) {
            final Ed25519FieldElement inverse = bi.z.invert();
            final Ed25519FieldElement xElement = bi.x.multiply(inverse);
            final Ed25519FieldElement yElement = bi.y.multiply(inverse);
            this.precomputedForDouble[i] =
                precomputed(yElement.add(xElement), yElement.subtract(xElement),
                    xElement.multiply(yElement).multiply(Ed25519Field.D_Times_TWO));
            bi = this.add(this.add(bi.toCached()).toP3().toCached()).toP3();
        }
    }

    /**
     * Doubles a given group element p in P^2 or P^3 coordinate system and returns the result in P x
     * P coordinate system. r = 2 * p where p = (X : Y : Z) or p = (X : Y : Z : T) <br> r in P x P
     * coordinate system: <br> r = ((X' : Z'), (Y' : T')) where X' = (X + Y)^2 - (Y^2 + X^2) Y' =
     * Y^2 + X^2 Z' = y^2 - X^2 T' = 2 * Z^2 - (y^2 - X^2) <br> r converted from P x P to P^2
     * coordinate system: <br> r = (X'' : Y'' : Z'') where X'' = X' * T' = ((X + Y)^2 - Y^2 - X^2) *
     * (2 * Z^2 - (y^2 - X^2)) Y'' = Y' * Z' = (Y^2 + X^2) * (y^2 - X^2) Z'' = Z' * T' = (y^2 - X^2)
     * * (2 * Z^2 - (y^2 - X^2))
     * <br>
     * Formula for the P^2 coordinate system is in agreement with the formula given in [4] page 12
     * (with a = -1) (up to a common factor -1 which does not matter): <br> B = (X + Y)^2; C = X^2;
     * D = Y^2; E = -C = -X^2; F := E + D = Y^2 - X^2; H = Z^2; J = F − 2 * H; X3 = (B − C − D) · J
     * = X' * (-T'); Y3 = F · (E − D) = Z' * (-Y'); Z3 = F · J = Z' * (-T').
     *
     * @return The doubled group element in the P x P coordinate system.
     */
    public Ed25519GroupElement dbl() {
        switch (this.coordinateSystem) {
            case P2:
            case P3:
                final Ed25519FieldElement xSquare;
                final Ed25519FieldElement ySquare;
                final Ed25519FieldElement b;
                final Ed25519FieldElement a;
                final Ed25519FieldElement aSquare;
                final Ed25519FieldElement ySquarePlusXSquare;
                final Ed25519FieldElement ySquareMinusXSquare;
                xSquare = this.x.square();
                ySquare = this.y.square();
                b = this.z.squareAndDouble();
                a = this.x.add(this.y);
                aSquare = a.square();
                ySquarePlusXSquare = ySquare.add(xSquare);
                ySquareMinusXSquare = ySquare.subtract(xSquare);
                return p1xp1(
                    aSquare.subtract(ySquarePlusXSquare),
                    ySquarePlusXSquare,
                    ySquareMinusXSquare,
                    b.subtract(ySquareMinusXSquare));
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Ed25519GroupElement addition using the twisted Edwards addition law for extended coordinates.
     * this must be given in P^3 coordinate system and g in PRECOMPUTED coordinate system. r = this
     * + g where this = (X1 : Y1 : Z1 : T1), g = (g.X, g.Y, g.Z) = (Y2/Z2 + X2/Z2, Y2/Z2 - X2/Z2, 2
     * * d * X2/Z2 * Y2/Z2) <br> r in P x P coordinate system: <br> r = ((X' : Z'), (Y' : T')) where
     * X' = (Y1 + X1) * g.X - (Y1 - X1) * q.Y = ((Y1 + X1) * (Y2 + X2) - (Y1 - X1) * (Y2 - X2)) *
     * 1/Z2 Y' = (Y1 + X1) * g.X + (Y1 - X1) * q.Y = ((Y1 + X1) * (Y2 + X2) + (Y1 - X1) * (Y2 - X2))
     * * 1/Z2 Z' = 2 * Z1 + T1 * g.Z = 2 * Z1 + T1 * 2 * d * X2 * Y2 * 1/Z2^2 = (2 * Z1 * Z2 + 2 * d
     * * T1 * T2) * 1/Z2 T' = 2 * Z1 - T1 * g.Z = 2 * Z1 - T1 * 2 * d * X2 * Y2 * 1/Z2^2 = (2 * Z1 *
     * Z2 - 2 * d * T1 * T2) * 1/Z2 <br> Formula for the P x P coordinate system is in agreement
     * with the formula given in file ge25519.c method add_p1p1() in ref implementation. Setting A =
     * (Y1 - X1) * (Y2 - X2), B = (Y1 + X1) * (Y2 + X2), C = 2 * d * T1 * T2, D = 2 * Z1 * Z2 we get
     * X' = (B - A) * 1/Z2 Y' = (B + A) * 1/Z2 Z' = (D + C) * 1/Z2 T' = (D - C) * 1/Z2 <br> r
     * converted from P x P to P^2 coordinate system: <br> r = (X'' : Y'' : Z'' : T'') where X'' =
     * X' * T' = (B - A) * (D - C) * 1/Z2^2 Y'' = Y' * Z' = (B + A) * (D + C) * 1/Z2^2 Z'' = Z' * T'
     * = (D + C) * (D - C) * 1/Z2^2 T'' = X' * Y' = (B - A) * (B + A) * 1/Z2^2 <br> Formula above
     * for the P^2 coordinate system is in agreement with the formula given in [2] page 6 (the
     * common factor 1/Z2^2 does not matter) E = B - A, F = D - C, G = D + C, H = B + A X3 = E * F =
     * (B - A) * (D - C); Y3 = G * H = (D + C) * (B + A); Z3 = F * G = (D - C) * (D + C); T3 = E * H
     * = (B - A) * (B + A);
     *
     * @param g The group element to add.
     * @return The resulting group element in the P x P coordinate system.
     */
    private Ed25519GroupElement precomputedAdd(final Ed25519GroupElement g) {
        if (this.coordinateSystem != CoordinateSystem.P3) {
            throw new UnsupportedOperationException();
        }
        if (g.coordinateSystem != CoordinateSystem.PRECOMPUTED) {
            throw new IllegalArgumentException();
        }

        final Ed25519FieldElement yPlusX;
        final Ed25519FieldElement yMinusX;
        final Ed25519FieldElement a;
        final Ed25519FieldElement b;
        final Ed25519FieldElement c;
        final Ed25519FieldElement d;
        yPlusX = this.y.add(this.x);
        yMinusX = this.y.subtract(this.x);
        a = yPlusX.multiply(g.x);
        b = yMinusX.multiply(g.y);
        c = g.z.multiply(this.t);
        d = this.z.add(this.z);

        return p1xp1(a.subtract(b), a.add(b), d.add(c), d.subtract(c));
    }

    /**
     * Ed25519GroupElement subtraction using the twisted Edwards addition law for extended
     * coordinates. this must be given in P^3 coordinate system and g in PRECOMPUTED coordinate
     * system. r = this - g where this = (X1 : Y1 : Z1 : T1), g = (g.X, g.Y, g.Z) = (Y2/Z2 + X2/Z2,
     * Y2/Z2 - X2/Z2, 2 * d * X2/Z2 * Y2/Z2) <br> Negating g means negating the value of X2 and T2
     * (the latter is irrelevant here). The formula is in accordance to the above addition.
     *
     * @param g he group element to subtract.
     * @return The result in the P x P coordinate system.
     */
    private Ed25519GroupElement precomputedSubtract(final Ed25519GroupElement g) {
        if (this.coordinateSystem != CoordinateSystem.P3) {
            throw new UnsupportedOperationException();
        }
        if (g.coordinateSystem != CoordinateSystem.PRECOMPUTED) {
            throw new IllegalArgumentException();
        }

        final Ed25519FieldElement yPlusX = this.y.add(this.x);
        final Ed25519FieldElement yMinusX = this.y.subtract(this.x);
        final Ed25519FieldElement a = yPlusX.multiply(g.y);
        final Ed25519FieldElement b = yMinusX.multiply(g.x);
        final Ed25519FieldElement c = g.z.multiply(this.t);
        final Ed25519FieldElement d = this.z.add(this.z);

        return p1xp1(a.subtract(b), a.add(b), d.subtract(c), d.add(c));
    }

    /**
     * Ed25519GroupElement addition using the twisted Edwards addition law for extended coordinates.
     * this must be given in P^3 coordinate system and g in CACHED coordinate system. r = this + g
     * where this = (X1 : Y1 : Z1 : T1), g = (g.X, g.Y, g.Z, g.T) = (Y2 + X2, Y2 - X2, Z2, 2 * d *
     * T2)
     * <br>
     * r in P x P coordinate system.: X' = (Y1 + X1) * (Y2 + X2) - (Y1 - X1) * (Y2 - X2) Y' = (Y1 +
     * X1) * (Y2 + X2) + (Y1 - X1) * (Y2 - X2) Z' = 2 * Z1 * Z2 + 2 * d * T1 * T2 T' = 2 * Z1 * T2 -
     * 2 * d * T1 * T2 <br> Setting A = (Y1 - X1) * (Y2 - X2), B = (Y1 + X1) * (Y2 + X2), C = 2 * d
     * * T1 * T2, D = 2 * Z1 * Z2 we get X' = (B - A) Y' = (B + A) Z' = (D + C) T' = (D - C) <br>
     * Same result as in precomputedAdd() (up to a common factor which does not matter).
     *
     * @param g The group element to add.
     * @return The result in the P x P coordinate system.
     */
    public Ed25519GroupElement add(final Ed25519GroupElement g) {
        if (this.coordinateSystem != CoordinateSystem.P3) {
            throw new UnsupportedOperationException();
        }
        if (g.coordinateSystem != CoordinateSystem.CACHED) {
            throw new IllegalArgumentException();
        }

        final Ed25519FieldElement yPlusX = this.y.add(this.x);
        final Ed25519FieldElement yMinusX = this.y.subtract(this.x);
        final Ed25519FieldElement a = yPlusX.multiply(g.x);
        final Ed25519FieldElement b = yMinusX.multiply(g.y);
        final Ed25519FieldElement c = g.t.multiply(this.t);
        final Ed25519FieldElement zSquare = this.z.multiply(g.z);
        final Ed25519FieldElement d = zSquare.add(zSquare);

        return p1xp1(a.subtract(b), a.add(b), d.add(c), d.subtract(c));
    }

    /**
     * Ed25519GroupElement subtraction using the twisted Edwards addition law for extended
     * coordinates. <br> Negating g means negating the value of the coordinate X2 and T2. The
     * formula is in accordance to the above addition.
     *
     * @param g The group element to subtract.
     * @return The result in the P x P coordinate system.
     */
    public Ed25519GroupElement subtract(final Ed25519GroupElement g) {
        if (this.coordinateSystem != CoordinateSystem.P3) {
            throw new UnsupportedOperationException();
        }
        if (g.coordinateSystem != CoordinateSystem.CACHED) {
            throw new IllegalArgumentException();
        }

        final Ed25519FieldElement yPlusX;
        final Ed25519FieldElement yMinusX;
        final Ed25519FieldElement zSquare;
        final Ed25519FieldElement a;
        final Ed25519FieldElement b;
        final Ed25519FieldElement c;
        final Ed25519FieldElement d;
        yPlusX = this.y.add(this.x);
        yMinusX = this.y.subtract(this.x);
        a = yPlusX.multiply(g.y);
        b = yMinusX.multiply(g.x);
        c = g.t.multiply(this.t);
        zSquare = this.z.multiply(g.z);
        d = zSquare.add(zSquare);

        return p1xp1(a.subtract(b), a.add(b), d.subtract(c), d.add(c));
    }

    /**
     * Negates this group element by subtracting it from the neutral group element. (only used in
     * MathUtils so it doesn't have to be fast)
     *
     * @return The negative of this group element.
     */
    public Ed25519GroupElement negate() {
        if (this.coordinateSystem != CoordinateSystem.P3) {
            throw new UnsupportedOperationException();
        }

        return Ed25519Group.ZERO_P3.subtract(this.toCached()).toP3();
    }

    @Override
    public int hashCode() {
        return this.encode().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Ed25519GroupElement)) {
            return false;
        }
        Ed25519GroupElement ge = (Ed25519GroupElement) obj;
        if (!this.coordinateSystem.equals(ge.coordinateSystem)) {
            try {
                ge = ge.toCoordinateSystem(this.coordinateSystem);
            } catch (final Exception e) {
                return false;
            }
        }
        switch (this.coordinateSystem) {
            case P2:
            case P3:
                if (this.z.equals(ge.z)) {
                    return this.x.equals(ge.x) && this.y.equals(ge.y);
                }

                final Ed25519FieldElement x1 = this.x.multiply(ge.z);
                final Ed25519FieldElement y1 = this.y.multiply(ge.z);
                final Ed25519FieldElement x2 = ge.x.multiply(this.z);
                final Ed25519FieldElement y2 = ge.y.multiply(this.z);

                return x1.equals(x2) && y1.equals(y2);
            case P1xP1:
                return this.toP2().equals(ge);
            case PRECOMPUTED:
                return this.x.equals(ge.x) && this.y.equals(ge.y) && this.z.equals(ge.z);
            case CACHED:
                if (this.z.equals(ge.z)) {
                    return this.x.equals(ge.x) && this.y.equals(ge.y) && this.t.equals(ge.t);
                }

                final Ed25519FieldElement x3 = this.x.multiply(ge.z);
                final Ed25519FieldElement y3 = this.y.multiply(ge.z);
                final Ed25519FieldElement t3 = this.t.multiply(ge.z);
                final Ed25519FieldElement x4 = ge.x.multiply(this.z);
                final Ed25519FieldElement y4 = ge.y.multiply(this.z);
                final Ed25519FieldElement t4 = ge.t.multiply(this.z);

                return x3.equals(x4) && y3.equals(y4) && t3.equals(t4);
            default:
                return false;
        }
    }

    /**
     * Constant-time conditional move. Replaces this with u if b == 1. Replaces this with this if b
     * == 0.
     *
     * @param u The group element to return if b == 1.
     * @param b in {0, 1}
     * @return u if b == 1; this if b == 0; null otherwise.
     */
    private Ed25519GroupElement cmov(final Ed25519GroupElement u, final int b) {
        Ed25519GroupElement ret = null;
        for (int i = 0; i < b; i++) {
            // Only for b == 1
            ret = u;
        }
        for (int i = 0; i < 1 - b; i++) {
            // Only for b == 0
            ret = this;
        }
        return ret;
    }

    /**
     * Constant-time conditional move. Replaces this with u if b == 1. Replaces this with this if b
     * == 0.
     *
     * @param u The group element to return if b == 1.
     * @param b in {0, 1}
     * @return u if b == 1; this if b == 0; @{@link IllegalStateException} otherwise.
     */
    private Ed25519GroupElement nullSafeCmov(final Ed25519GroupElement u, final int b) {
        Ed25519GroupElement ret = cmov(u, b);
        if (ret == null) {
            throw new IllegalStateException(
                "Ed25519GroupElement " + Objects.toString(u, "NULL") + " and argument " + b
                    + " resolved a null cmov");
        }
        return ret;
    }

    /**
     * Look up 16^i r_i B in the precomputed table. No secret array indices, no secret branching.
     * Constant time. <br> Must have previously precomputed.
     *
     * @param pos = i/2 for i in {0, 2, 4,..., 62}
     * @param b = r_i
     * @return The Ed25519GroupElement
     */
    private Ed25519GroupElement select(final int pos, final int b) {
        // Is r_i negative?
        final int bNegative = ByteUtils.isNegativeConstantTime(b);
        // |r_i|
        final int bAbs = b - (((-bNegative) & b) << 1);

        // 16^i |r_i| B
        final Ed25519GroupElement tElement =
            Ed25519Group.ZERO_PRECOMPUTED
                .nullSafeCmov(this.precomputedForSingle[pos][0],
                    ByteUtils.isEqualConstantTime(bAbs, 1))
                .nullSafeCmov(this.precomputedForSingle[pos][1],
                    ByteUtils.isEqualConstantTime(bAbs, 2))
                .nullSafeCmov(this.precomputedForSingle[pos][2],
                    ByteUtils.isEqualConstantTime(bAbs, 3))
                .nullSafeCmov(this.precomputedForSingle[pos][3],
                    ByteUtils.isEqualConstantTime(bAbs, 4))
                .nullSafeCmov(this.precomputedForSingle[pos][4],
                    ByteUtils.isEqualConstantTime(bAbs, 5))
                .nullSafeCmov(this.precomputedForSingle[pos][5],
                    ByteUtils.isEqualConstantTime(bAbs, 6))
                .nullSafeCmov(this.precomputedForSingle[pos][6],
                    ByteUtils.isEqualConstantTime(bAbs, 7))
                .nullSafeCmov(this.precomputedForSingle[pos][7],
                    ByteUtils.isEqualConstantTime(bAbs, 8));
        // -16^i |r_i| B
        //noinspection SuspiciousNameCombination
        final Ed25519GroupElement tMinus = precomputed(tElement.y, tElement.x, tElement.z.negate());
        // 16^i r_i B
        return tElement.nullSafeCmov(tMinus, bNegative);
    }

    /**
     * h = a * B where a = a[0]+256*a[1]+...+256^31 a[31] and B is this point. If its lookup table
     * has not been precomputed, it will be at the start of the method (and cached for later calls).
     * Constant time.
     *
     * @param a The encoded field element.
     * @return The resulting group element.
     */
    public Ed25519GroupElement scalarMultiply(final Ed25519EncodedFieldElement a) {
        Ed25519GroupElement g;
        int i;
        final byte[] e = toRadix16(a);
        Ed25519GroupElement h = Ed25519Group.ZERO_P3;
        for (i = 1; i < 64; i += 2) {
            g = this.select(i / 2, e[i]);
            h = h.precomputedAdd(g).toP3();
        }

        h = h.dbl().toP2().dbl().toP2().dbl().toP2().dbl().toP3();

        for (i = 0; i < 64; i += 2) {
            g = this.select(i / 2, e[i]);
            h = h.precomputedAdd(g).toP3();
        }

        return h;
    }

    /**
     * r = b * B - a * A where a and b are encoded field elements and B is this point. A must have
     * been previously precomputed for double scalar multiplication.
     *
     * @param aGroupElement in P3 coordinate system.
     * @param a = The first encoded field element.
     * @param b = The second encoded field element.
     * @return The resulting group element.
     */
    public Ed25519GroupElement doubleScalarMultiplyVariableTime(
        final Ed25519GroupElement aGroupElement,
        final Ed25519EncodedFieldElement a,
        final Ed25519EncodedFieldElement b) {
        final byte[] aSlide = slide(a);
        final byte[] bSlide = slide(b);
        Ed25519GroupElement r = Ed25519Group.ZERO_P2;

        int i;
        for (i = 255; i >= 0; --i) {
            if (aSlide[i] != 0 || bSlide[i] != 0) {
                break;
            }
        }

        for (; i >= 0; --i) {
            Ed25519GroupElement tElement = r.dbl();

            if (aSlide[i] > 0) {
                tElement = tElement.toP3()
                    .precomputedSubtract(aGroupElement.precomputedForDouble[aSlide[i] / 2]);
            } else if (aSlide[i] < 0) {
                tElement = tElement.toP3().precomputedAdd(aGroupElement.precomputedForDouble[(-aSlide[i]) / 2]);
            }

            if (bSlide[i] > 0) {
                tElement = tElement.toP3().precomputedAdd(this.precomputedForDouble[bSlide[i] / 2]);
            } else if (bSlide[i] < 0) {
                tElement = tElement.toP3()
                    .precomputedSubtract(this.precomputedForDouble[(-bSlide[i]) / 2]);
            }

            r = tElement.toP2();
        }

        return r;
    }

    /**
     * Verify that the group element satisfies the curve equation.
     *
     * @return true if the group element satisfies the curve equation, false otherwise.
     */
    public boolean satisfiesCurveEquation() {
        switch (this.coordinateSystem) {
            case P2:
            case P3:
                final Ed25519FieldElement inverse = this.z.invert();
                final Ed25519FieldElement xElement = this.x.multiply(inverse);
                final Ed25519FieldElement yElement = this.y.multiply(inverse);
                final Ed25519FieldElement xSquare = xElement.square();
                final Ed25519FieldElement ySquare = yElement.square();
                final Ed25519FieldElement dXSquareYSquare =
                    Ed25519Field.D.multiply(xSquare).multiply(ySquare);
                return Ed25519Field.ONE.add(dXSquareYSquare).add(xSquare).equals(ySquare);

            default:
                return this.toP2().satisfiesCurveEquation();
        }
    }

    @Override
    @SuppressWarnings("squid:S3457")
    public String toString() {
        return String.format(
            "X=%s\nY=%s\nZ=%s\nT=%s\n",
            this.x.toString(), this.y.toString(), this.z.toString(), this.t.toString());
    }
}
