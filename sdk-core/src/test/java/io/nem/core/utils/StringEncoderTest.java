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

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

public class StringEncoderTest {

    private static final byte[] ENCODED_SIGMA_BYTES = new byte[]{0x53, 0x69, 0x67, 0x6D, 0x61};

    private static final byte[] ENCODED_CURRENCY_SYMBOLS_BYTES =
            new byte[]{0x24, (byte) 0xC2, (byte) 0xA2, (byte) 0xE2, (byte) 0x82, (byte) 0xAC};

    @Test
    public void stringCanBeConvertedToByteArray() {
        // Assert:
        MatcherAssert.assertThat(StringEncoder.getBytes("Sigma"), IsEqual.equalTo(ENCODED_SIGMA_BYTES));
        MatcherAssert.assertThat(
                StringEncoder.getBytes("\u0024\u00a2\u20ac"), IsEqual.equalTo(ENCODED_CURRENCY_SYMBOLS_BYTES));
    }

    @Test
    public void byteArrayCanBeConvertedToString() {
        // Assert:
        MatcherAssert.assertThat(StringEncoder.getString(ENCODED_SIGMA_BYTES), IsEqual.equalTo("Sigma"));
        MatcherAssert.assertThat(
                StringEncoder.getString(ENCODED_CURRENCY_SYMBOLS_BYTES), IsEqual.equalTo("\u0024\u00a2\u20ac"));
    }
}
