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

package io.nem.symbol.core.utils;

import java.math.BigInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteUtilsTest {

    // region BigIntToBytes

    private static void assertBigIntToBytesConversion(final BigInteger input,
        final byte[] expected) {
        // Act:
        final byte[] result = ByteUtils.bigIntToBytes(input);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(expected));
    }

    private static void assertBigIntToBytesOfSizeConversion(
        final BigInteger input, final int size, final byte[] expected) {
        // Act:
        final byte[] result = ByteUtils.bigIntToBytesOfSize(input, size);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(expected));
    }

    private static void assertBytesToLongConversion(final byte[] input, final long expected) {
        // Act:
        final long result = ByteUtils.bytesToLong(input);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(expected));
    }

    private static void assertLongToBytesConversion(final long input, final byte[] expected) {
        // Act:
        final byte[] result = ByteUtils.longToBytes(input);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(expected));
    }

    private static void assertBytesToIntConversion(final byte[] input, final int expected) {
        // Act:
        final int result = ByteUtils.bytesToInt(input);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(expected));
    }

    // endregion

    // region bytesToLong / longToBytes

    // region bytesToLong

    private static void assertIntToBytesConversion(final int input, final byte[] expected) {
        // Act:
        final byte[] result = ByteUtils.intToBytes(input);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(expected));
    }

    private static void assertIntArrayToBytesConversion(final int[] input, final byte[] expected) {
        // Act:
        final byte[] result = ByteUtils.intArrayToByteArray(input);

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(expected));
    }

    @Test
    public void canConvertBigIntToBytes() {
        // Assert:
        assertBigIntToBytesConversion(new BigInteger("0"), new byte[]{0, 0, 0, 0, 0, 0, 0, 0});
        assertBigIntToBytesConversion(
            new BigInteger("2139062143"), new byte[]{0x0, 0x0, 0x0, 0x0, 0x7F, 0x7F, 0x7F, 0x7F});
        assertBigIntToBytesConversion(
            new BigInteger("8034280445828890495"),
            new byte[]{0x6F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F});
    }

    @Test
    public void canConvertBigIntToBytesOfGivenSize() {
        // Assert:
        assertBigIntToBytesOfSizeConversion(new BigInteger("0"), 4, new byte[]{0, 0, 0, 0});
        assertBigIntToBytesOfSizeConversion(
            new BigInteger("2139062143"), 4, new byte[]{0x7F, 0x7F, 0x7F, 0x7F});
    }

    @Test
    public void canConvertBigIntToBytesIgnoresExcessiveData() {
        // Assert:
        // Should truncates higher than 8 bytes
        assertBigIntToBytesConversion(
            new BigInteger("1760474967448236294015"),
            new byte[]{0x6F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F});
        // Should truncates higher than 4 bytes
        assertBigIntToBytesOfSizeConversion(
            new BigInteger("8034280445828890495"), 4, new byte[]{0x7F, 0x7F, 0x7F, 0x7F});
    }

    // endregion

    // region longToBytes

    @Test
    public void canConvertBytesToLong() {
        // Assert:
        assertBytesToLongConversion(new byte[]{1, 2, 3, 4, 5, 6, 7, 8}, 0x0102030405060708L);
    }

    @Test
    public void canConvertBytesToLongNegative() {
        // Assert:
        assertBytesToLongConversion(new byte[]{(byte) 0x80, 2, 3, 4, 5, 6, 7, 8},
            0x8002030405060708L);
    }

    @Test
    public void conversionToLongIgnoresExcessiveData() {
        // Assert:
        assertBytesToLongConversion(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 9},
            0x0102030405060708L);
    }

    // endregion

    // region roundtrip

    @Test
    public void conversionToLongFailsOnDataUnderflow() {
        // Arrange:
        final byte[] data = {1, 2, 3, 4, 5, 6};

        // Act:
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ByteUtils.bytesToLong(data));
    }

    @Test
    public void canConvertLongToBytes() {
        // Assert:
        assertLongToBytesConversion(0x0807060504030201L, new byte[]{8, 7, 6, 5, 4, 3, 2, 1});
    }

    // endregion

    // endregion

    // region bytesToInt / intToBytes

    // region bytesToInt

    @Test
    public void canConvertLongNegativeToBytes() {
        // Arrange:
        assertLongToBytesConversion(
            0x8070605040302010L, new byte[]{(byte) 0x80, 0x70, 0x60, 0x50, 0x40, 0x30, 0x20, 0x10});
    }

    @Test
    public void canRoundtripLongViaBytes() {
        // Arrange:
        final long input = 0x8070605040302010L;

        // Act:
        final long result = ByteUtils.bytesToLong(ByteUtils.longToBytes(input));

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(input));
    }

    @Test
    public void canRoundtripBytesViaLong() {
        // Arrange:
        final byte[] input = {(byte) 0x80, 2, 3, 4, 5, 6, 7, 8};

        // Act:
        final byte[] result = ByteUtils.longToBytes(ByteUtils.bytesToLong(input));

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(input));
    }

    @Test
    public void canConvertBytesToInt() {
        // Assert:
        assertBytesToIntConversion(new byte[]{1, 2, 3, 4}, 0x01020304);
    }

    @Test
    public void canConvertBytesToIntNegative() {
        // Assert:
        assertBytesToIntConversion(new byte[]{(byte) 0x80, 2, 3, 4}, 0x80020304);
    }

    // endregion

    // region intToBytes

    @Test
    public void conversionToIntIgnoresExcessiveData() {
        // Assert:
        assertBytesToIntConversion(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 9}, 0x01020304);
    }

    @Test
    public void conversionToIntFailsOnDataUnderflow() {
        // Arrange:
        final byte[] data = {1, 2, 3};

        // Act:
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ByteUtils.bytesToInt(data));
    }

    @Test
    public void canConvertIntToBytes() {
        // Assert:
        assertIntToBytesConversion(0x08070605, new byte[]{8, 7, 6, 5});
    }

    // endregion

    // region roundtrip

    @Test
    public void canConvertIntNegativeToBytes() {
        // Arrange:
        assertIntToBytesConversion(0x80706050, new byte[]{(byte) 0x80, 0x70, 0x60, 0x50});
    }

    @Test
    public void canRoundtripIntViaBytes() {
        // Arrange:
        final int input = 0x80706050;

        // Act:
        final int result = ByteUtils.bytesToInt(ByteUtils.intToBytes(input));

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(input));
    }

    // endregion

    // region intArrayToBytes

    @Test
    public void canRoundtripBytesViaInt() {
        // Arrange:
        final byte[] input = {(byte) 0x80, 2, 3, 4};

        // Act:
        final byte[] result = ByteUtils.intToBytes(ByteUtils.bytesToInt(input));

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo(input));
    }

    @Test
    public void canConvertIntArrayToBytes() {
        // Assert:
        assertIntArrayToBytesConversion(new int[]{1, 2, 3, 4}, new byte[]{1, 2, 3, 4});
        assertIntArrayToBytesConversion(
            new int[]{1, 2, 3, 4, 5, 6, 7, 8}, new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        assertIntArrayToBytesConversion(new int[]{0x7F, 0x7F}, new byte[]{0x7F, 0x7F});
        assertIntArrayToBytesConversion(new int[]{0xFF, 0xFF}, new byte[]{-0x1, -0x1});
    }

    // endregion

    // region isEqual

    @Test
    public void isEqualReturnsOneIfBytesAreEqual() {
        // Assert:
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(0, 0), IsEqual.equalTo(1));
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(7, 7), IsEqual.equalTo(1));
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(64, 64), IsEqual.equalTo(1));
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(255, 255), IsEqual.equalTo(1));
    }

    @Test
    public void isEqualReturnsOneIfLoBytesAreEqualButHiBytesAreNot() {
        // Assert:
        MatcherAssert
            .assertThat(ByteUtils.isEqualConstantTime(75 + 256, 75 + 256 * 2), IsEqual.equalTo(1));
    }

    @Test
    public void isEqualReturnsZeroIfBytesAreNotEqual() {
        // Assert:
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(0, 1), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(7, -7), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(64, 63), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ByteUtils.isEqualConstantTime(254, 255), IsEqual.equalTo(0));
    }

    // endregion

    // region isNegative

    @Test
    public void isNegativeReturnsOneIfByteIsNegative() {
        // Assert:
        MatcherAssert.assertThat(ByteUtils.isNegativeConstantTime(-1), IsEqual.equalTo(1));
        MatcherAssert.assertThat(ByteUtils.isNegativeConstantTime(-100), IsEqual.equalTo(1));
        MatcherAssert.assertThat(ByteUtils.isNegativeConstantTime(-255), IsEqual.equalTo(1));
    }

    @Test
    public void isNegativeReturnsZeroIfByteIsZeroOrPositive() {
        // Assert:
        MatcherAssert.assertThat(ByteUtils.isNegativeConstantTime(0), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ByteUtils.isNegativeConstantTime(1), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ByteUtils.isNegativeConstantTime(32), IsEqual.equalTo(0));
        MatcherAssert.assertThat(ByteUtils.isNegativeConstantTime(127), IsEqual.equalTo(0));
    }

    // endregion

    // region toString

    @Test
    public void toStringCreatesCorrectRepresentationForEmptyBytes() {
        // Act:
        final String result = ByteUtils.toString(new byte[]{});

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo("{ }"));
    }

    @Test
    public void toStringCreatesCorrectRepresentationForNonEmptyBytes() {
        // Act:
        final String result = ByteUtils.toString(new byte[]{0x12, (byte) 0x8A, 0x00, 0x07});

        // Assert:
        MatcherAssert.assertThat(result, IsEqual.equalTo("{ 12 8A 00 07 }"));
    }

    // endregion
}
