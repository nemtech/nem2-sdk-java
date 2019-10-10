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
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConvertUtilsTest {

    // region getBytes

    private static void assertGetBytesConversion(final String input, final byte[] expectedOutput) {
        // Act:
        final byte[] output = ConvertUtils.getBytes(input);

        // Assert:
        Assert.assertThat(output, IsEqual.equalTo(expectedOutput));
    }


    private static void assertGetStringConversion(final byte[] input, final String expectedOutput) {
        // Act:
        final String output = ConvertUtils.toHex(input);

        // Assert:
        Assert.assertThat(output, IsEqual.equalTo(expectedOutput));
    }

    @Test
    public void getBytesCannotConvertMalformedStringToByteArray() {
        // Act:
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> ConvertUtils.getBytes("4e454g465457"));
    }

    @Test
    public void getBytesCanConvertValidStringToByteArray() {
        // Assert:
        assertGetBytesConversion("4e454d465457", new byte[]{0x4e, 0x45, 0x4d, 0x46, 0x54, 0x57});
    }

    // endregion

    // region tryGetBytes

    @Test
    public void getBytesCanConvertValidStringWithOddLengthToByteArray() {
        // Assert:
        assertGetBytesConversion("e454d465457", new byte[]{0x0e, 0x45, 0x4d, 0x46, 0x54, 0x57});
    }

    @Test
    public void getBytesCanConvertValidStringWithLeadingZerosToByteArray() {
        // Assert:
        assertGetBytesConversion("00000d465457", new byte[]{0x00, 0x00, 0x0d, 0x46, 0x54, 0x57});
    }

    @Test
    public void tryGetBytesCanConvertValidStringToByteArray() {
        // Assert:
        assertGetBytesConversion("4e454d465457", new byte[]{0x4e, 0x45, 0x4d, 0x46, 0x54, 0x57});
    }

    @Test
    public void tryGetBytesCanConvertValidStringWithOddLengthToByteArray() {
        // Assert:
        assertGetBytesConversion("e454d465457", new byte[]{0x0e, 0x45, 0x4d, 0x46, 0x54, 0x57});
    }

    @Test
    public void tryGetBytesCanConvertValidStringWithLeadingZerosToByteArray() {
        // Assert:
        assertGetBytesConversion("00000d465457", new byte[]{0x00, 0x00, 0x0d, 0x46, 0x54, 0x57});
    }


    @Test
    public void tryGetBytesCannotConvertMalformedStringToByteArray() {
        // Assert:
        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () ->
                assertGetBytesConversion("4e454g465457", null));
        Assertions.assertEquals(
            "org.apache.commons.codec.DecoderException: Illegal hexadecimal character g at index 5",
            exception.getMessage());
    }

    @Test
    public void getStringCanConvertBytesToHexString() {
        // Assert:
        assertGetStringConversion(new byte[]{0x4e, 0x45, 0x4d, 0x46, 0x54, 0x57}, "4e454d465457");
    }

    @Test
    public void getStringCanConvertBytesWithLeadingZerosToHexString() {
        // Assert:
        assertGetStringConversion(new byte[]{0x00, 0x00, 0x0d, 0x46, 0x54, 0x57}, "00000d465457");
    }

    @Test
    public void getStringCanConvertEmptyBytesToHexString() {
        // Assert:
        assertGetStringConversion(new byte[]{}, "");
    }

    @Test
    public void fromStringToHexToString() {
        // Assert:
        String message = "Some message 汉字";

        Assertions.assertEquals(message,
            ConvertUtils.fromHexToString(ConvertUtils.fromStringToHex(message)));

        Assertions.assertNull(ConvertUtils.fromHexToString(null));
        Assertions.assertNull(ConvertUtils.fromStringToHex(null));
    }

    @Test
    public void fromHex() {
        // Assert:
        String message = "This is the message for this account! 汉字89664";

        Assertions.assertEquals(message,
            ConvertUtils.fromHexToString(
                "5468697320697320746865206D65737361676520666F722074686973206163636F756E742120E6B189E5AD973839363634"));

    }

    @Test
    public void toSize16Hex() {
        Assertions.assertEquals("000000000000000a",
            ConvertUtils.toSize16Hex(BigInteger.TEN));

        Assertions.assertEquals("00000000000186a0",
            ConvertUtils.toSize16Hex(BigInteger.valueOf(100000)));
    }

}
