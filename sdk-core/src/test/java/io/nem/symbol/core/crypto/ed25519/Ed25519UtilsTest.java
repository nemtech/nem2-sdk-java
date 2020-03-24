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

package io.nem.symbol.core.crypto.ed25519;

import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class Ed25519UtilsTest {

    private static Stream<Arguments> params() {
        return Stream.of(
            Arguments
                .of("000000000000000000000000000000000000000000000000000000000000227F",
                    "C0294A89A92F21B520F75BAFDD73500711F5FFCC08A4B307A5A7AD9C84AF5049"),
            Arguments
                .of("000000000000000000000000000000000000000000000000000000000000AAAA",
                    "B0B326832F810025B52EE4A5AB2B897A0590A13BD9B4E705E25AA089F5061852"),

            Arguments.of("000000000000000000000000000000000000000000000000000000BBADABA123",
                "D0983B05E5DED10A63DC1EEABD82164411625A104995398582F75F1674E47061")
        );
    }

    // region prepareForScalarMultiply

    @Test
    public void prepareForScalarMultiplyReturnsClampedValue() {
        // Arrange:
        final PrivateKey privateKey = new PrivateKey(
            new BigInteger(RandomUtils.generateRandomBytes(32)));

        // Act:
        final byte[] a = Ed25519Utils.prepareForScalarMultiply(privateKey).getRaw();

        // Assert:
        MatcherAssert.assertThat(a[31] & 0x40, IsEqual.equalTo(0x40));
        MatcherAssert.assertThat(a[31] & 0x80, IsEqual.equalTo(0x0));
        MatcherAssert.assertThat(a[0] & 0x7, IsEqual.equalTo(0x0));
    }

    @ParameterizedTest
    @MethodSource("params")
    public void shouldPrepareForScalarMultiply(String input,
        String expected) {
        // Arrange:
        final PrivateKey privateKey = PrivateKey.fromHexString(input);
        Assertions.assertEquals(input.toUpperCase(), privateKey.toHex().toUpperCase());
        Assertions.assertEquals(expected.toUpperCase(),
            ConvertUtils.toHex(
                Ed25519Utils.prepareForScalarMultiply(privateKey).getRaw()));
    }

    // endregion
}
