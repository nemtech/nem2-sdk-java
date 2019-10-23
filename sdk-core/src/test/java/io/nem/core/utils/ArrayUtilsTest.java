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

package io.nem.core.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsSame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayUtilsTest {

    // region duplicate

    private static void assertCanDuplicate(final byte[] bytes) {
        // Act:
        final byte[] result = ArrayUtils.duplicate(bytes);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(bytes));
    }

    private static String createMessage(final byte[] bytes1, final byte[] bytes2) {
        return String.format(
            "bytes1: %s%sbytes2: %s",
            ByteUtils.toString(bytes1), System.lineSeparator(), ByteUtils.toString(bytes2));
    }

    private static void assertCompareZero(final byte[] a, final byte[] b) {
        // Assert:
        MatcherAssert.assertThat(ArrayUtils.compare(a, b), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ArrayUtils.compare(b, a), IsEqual.equalTo(0));
    }

    private static void assertCompareNonZero(final byte[] smaller, final byte[] larger) {
        // Assert:
        MatcherAssert.assertThat(ArrayUtils.compare(smaller, larger), IsEqual.equalTo(-1));
        MatcherAssert.assertThat(ArrayUtils.compare(larger, smaller), IsEqual.equalTo(1));
    }

    @Test
    public void duplicateIsNotReference() {
        // Arrange:
        final byte[] src = new byte[]{1, 2, 3, 4};

        // Act:
        final byte[] result = ArrayUtils.duplicate(src);

        // Assert:
        MatcherAssert.assertThat(result, IsNot.not(IsSame.sameInstance(src)));
    }

    // endregion

    // region concat

    @Test
    public void duplicateCanDuplicateEmptyArray() {
        // Assert:
        assertCanDuplicate(new byte[]{});
    }

    @Test
    public void duplicateCanDuplicateNonEmptyArray() {
        // Assert:
        assertCanDuplicate(new byte[]{1, 2, 3, 4});
    }

    @Test
    public void duplicateThrowsExceptionOnNull() {
        // Act:
        Assertions.assertThrows(NullPointerException.class, () -> ArrayUtils.duplicate(null));
    }

    @Test
    public void concatCanCombineEmptyArrayWithEmptyArray() {
        // Arrange:
        final byte[] lhs = new byte[]{};
        final byte[] rhs = new byte[]{};

        // Act:
        final byte[] result = ArrayUtils.concat(lhs, rhs);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(new byte[]{}));
    }

    @Test
    public void concatCanCombineEmptyArrayWithNonEmptyArray() {
        // Arrange:
        final byte[] lhs = new byte[]{};
        final byte[] rhs = new byte[]{12, 4, 6};

        // Act:
        final byte[] result = ArrayUtils.concat(lhs, rhs);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(new byte[]{12, 4, 6}));
    }

    // endregion

    // region split

    @Test
    public void concatCanCombineNonEmptyArrayWithEmptyArray() {
        // Arrange:
        final byte[] lhs = new byte[]{7, 13};
        final byte[] rhs = new byte[]{};

        // Act:
        final byte[] result = ArrayUtils.concat(lhs, rhs);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(new byte[]{7, 13}));
    }

    @Test
    public void concatCanCombineNonEmptyArrayWithNonEmptyArray() {
        // Arrange:
        final byte[] lhs = new byte[]{7, 13};
        final byte[] rhs = new byte[]{12, 4, 6};

        // Act:
        final byte[] result = ArrayUtils.concat(lhs, rhs);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(new byte[]{7, 13, 12, 4, 6}));
    }

    @Test
    public void concatCanCombineMoreThanTwoArrays() {
        // Act:
        final byte[] result =
            ArrayUtils.concat(new byte[]{7, 13}, new byte[]{12, 4, 6}, new byte[]{11, 9});

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(new byte[]{7, 13, 12, 4, 6, 11, 9}));
    }

    @Test
    public void splitFailsIfSplitIndexIsNegative() {
        // Arrange:
        final byte[] bytes = new byte[]{7, 13, 12, 4, 6};

        // Act:
        Assertions.assertThrows(IllegalArgumentException.class, () -> ArrayUtils.split(bytes, -1));
    }

    @Test
    public void splitFailsIfSplitIndexIsGreaterThanInputLength() {
        // Arrange:
        final byte[] bytes = new byte[]{7, 13, 12, 4, 6};

        // Act:
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> ArrayUtils.split(bytes, bytes.length + 1));
    }

    @Test
    public void canSplitEmptyArray() {
        // Arrange:
        final byte[] bytes = new byte[]{};

        // Act:
        final byte[][] parts = ArrayUtils.split(bytes, 0);

        // Assert:
        MatcherAssert.assertThat(parts.length, IsEqual.equalTo(2));
        MatcherAssert.assertThat(parts[0], IsEqual.equalTo(new byte[]{}));
        MatcherAssert.assertThat(parts[1], IsEqual.equalTo(new byte[]{}));
    }

    // endregion

    // region toByteArray

    @Test
    public void canSplitArrayAtBeginning() {
        // Arrange:
        final byte[] bytes = new byte[]{12, 4, 6};

        // Act:
        final byte[][] parts = ArrayUtils.split(bytes, 0);

        // Assert:
        MatcherAssert.assertThat(parts.length, IsEqual.equalTo(2));
        MatcherAssert.assertThat(parts[0], IsEqual.equalTo(new byte[]{}));
        MatcherAssert.assertThat(parts[1], IsEqual.equalTo(new byte[]{12, 4, 6}));
    }

    @Test
    public void canSplitArrayAtEnd() {
        // Arrange:
        final byte[] bytes = new byte[]{7, 13};

        // Act:
        final byte[][] parts = ArrayUtils.split(bytes, 2);

        // Assert:
        MatcherAssert.assertThat(parts.length, IsEqual.equalTo(2));
        MatcherAssert.assertThat(parts[0], IsEqual.equalTo(new byte[]{7, 13}));
        MatcherAssert.assertThat(parts[1], IsEqual.equalTo(new byte[]{}));
    }

    @Test
    public void canSplitArrayAtMiddle() {
        // Arrange:
        final byte[] bytes = new byte[]{7, 13, 12, 4, 6};

        // Act:
        final byte[][] parts = ArrayUtils.split(bytes, 2);

        // Assert:
        MatcherAssert.assertThat(parts.length, IsEqual.equalTo(2));
        MatcherAssert.assertThat(parts[0], IsEqual.equalTo(new byte[]{7, 13}));
        MatcherAssert.assertThat(parts[1], IsEqual.equalTo(new byte[]{12, 4, 6}));
    }

    @Test
    public void canConvertPositiveBigIntegerToByteArray() {
        // Act:
        final byte[] bytes = ArrayUtils.toByteArray(new BigInteger("321495", 16), 3);

        // Assert:
        MatcherAssert.assertThat(bytes, IsEqual.equalTo(new byte[]{(byte) 0x95, 0x14, 0x32}));
    }

    // endregion

    // region toBigInteger

    @Test
    public void canConvertNegativeBigIntegerToByteArray() {
        // Act:
        final byte[] bytes = ArrayUtils.toByteArray(new BigInteger("F21495", 16), 3);

        // Assert:
        MatcherAssert.assertThat(bytes, IsEqual.equalTo(new byte[]{(byte) 0x95, 0x14, (byte) 0xF2}));
    }

    @Test
    public void canConvertBigIntegerWithLeadingZerosToByteArray() {
        // Act:
        final byte[] bytes = ArrayUtils.toByteArray(new BigInteger("0000A5", 16), 3);

        // Assert:
        MatcherAssert.assertThat(bytes, IsEqual.equalTo(new byte[]{(byte) 0xA5, 0x00, 0x00}));
    }

    @Test
    public void byteArrayConversionTruncatesLargeInteger() {
        // Act:
        final byte[] bytes = ArrayUtils.toByteArray(new BigInteger("F122321495", 16), 3);

        // Assert:
        MatcherAssert.assertThat(bytes, IsEqual.equalTo(new byte[]{(byte) 0x95, 0x14, 0x32}));
    }

    // endregion

    // region isEqual

    @Test
    public void canConvertByteArrayToPositiveBigInteger() {
        // Act:
        final BigInteger result = ArrayUtils.toBigInteger(new byte[]{(byte) 0x95, 0x14, 0x32});

        // Assert:
        MatcherAssert.assertThat(new BigInteger("321495", 16), IsEqual.equalTo(result));
    }

    @Test
    public void canConvertByteArrayToNegativeBigInteger() {
        // Act:
        final BigInteger result = ArrayUtils
            .toBigInteger(new byte[]{(byte) 0x95, 0x14, (byte) 0xF2});

        // Assert:
        MatcherAssert.assertThat(new BigInteger("F21495", 16), IsEqual.equalTo(result));
    }

    @Test
    public void canConvertByteArrayWithLeadingZerosToBigInteger() {
        // Act:
        final BigInteger result = ArrayUtils.toBigInteger(new byte[]{(byte) 0xA5, 0x00, 0x00});

        // Assert:
        MatcherAssert.assertThat(new BigInteger("0000A5", 16), IsEqual.equalTo(result));
    }

    // endregion

    // region compare

    @Test
    public void isEqualsReturnsOneForEqualByteArrays() {
        // Arrange:
        final SecureRandom random = new SecureRandom();
        final byte[] bytes1 = new byte[32];
        final byte[] bytes2 = new byte[32];
        for (int i = 0; i < 100; i++) {
            random.nextBytes(bytes1);
            System.arraycopy(bytes1, 0, bytes2, 0, 32);

            // Act:
            final int result = ArrayUtils.isEqualConstantTime(bytes1, bytes2);

            // Assert:
            MatcherAssert.assertThat(createMessage(bytes1, bytes2), result, IsEqual.equalTo(1));
        }
    }

    @Test
    public void isEqualsReturnsZeroForUnequalByteArrays() {
        // Arrange:
        final SecureRandom random = new SecureRandom();
        final byte[] bytes1 = new byte[32];
        final byte[] bytes2 = new byte[32];
        random.nextBytes(bytes1);
        for (int i = 0; i < 32; i++) {
            System.arraycopy(bytes1, 0, bytes2, 0, 32);
            bytes2[i] = (byte) (bytes2[i] ^ 0xff);

            // Act:
            final int result = ArrayUtils.isEqualConstantTime(bytes1, bytes2);

            // Assert:
            MatcherAssert.assertThat(createMessage(bytes1, bytes2), result, IsEqual.equalTo(0));
        }
    }

    @Test
    public void compareReturnsZeroForEmptyArrays() {
        // Assert:
        assertCompareZero(new byte[]{}, new byte[]{});
    }

    @Test
    public void compareReturnsNonZeroForEmptyAndNonEmptyArrays() {
        // Assert:
        assertCompareNonZero(new byte[]{}, new byte[]{5});
    }

    @Test
    public void compareReturnsNonZeroForDifferentSizedArrays() {
        // Assert:
        assertCompareNonZero(new byte[]{54}, new byte[]{5, 4});
    }

    @Test
    public void compareReturnsNonZeroForArraysOfSameLengthWithDifferentPositiveElementValue() {
        // Assert:
        assertCompareNonZero(new byte[]{1, 2, 3, 4, 5}, new byte[]{1, 2, 4, 4, 5});
    }

    @Test
    public void compareReturnsNonZeroForArraysOfSameLengthWithDifferentNegativeElementValue() {
        // Assert:
        assertCompareNonZero(new byte[]{1, 2, -4, 4, 5}, new byte[]{1, 2, -3, 4, 5});
    }

    @Test
    public void compareReturnsZeroForArraysOfSameLengthWithEqualElementValues() {
        // Assert:
        assertCompareZero(new byte[]{1, 2, 3, 4, 5}, new byte[]{1, 2, 3, 4, 5});
    }

    // endregion

    // region getBit

    @Test
    public void getBitReturnZeroIfBitIsNotSet() {
        MatcherAssert.assertThat(ArrayUtils.getBit(new byte[]{0}, 0), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ArrayUtils.getBit(new byte[]{1, 2, 3}, 15), IsEqual.equalTo(0));
    }

    @Test
    public void getBitReturnOneIfBitIsSet() {
        // Assert:
        MatcherAssert.assertThat(ArrayUtils.getBit(new byte[]{8}, 3), IsEqual.equalTo(1));
        MatcherAssert.assertThat(ArrayUtils.getBit(new byte[]{1, 2, 3}, 9), IsEqual.equalTo(1));
        MatcherAssert.assertThat(ArrayUtils.getBit(new byte[]{1, 2, 3}, 16), IsEqual.equalTo(1));
    }

    // endregion
}
