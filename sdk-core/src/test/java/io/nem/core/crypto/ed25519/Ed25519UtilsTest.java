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

package io.nem.core.crypto.ed25519;

import io.nem.core.crypto.PrivateKey;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.infrastructure.RandomUtils;
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
                .of("227F",
                    "f0579208310ff67d48adf7b23a10ff8d18197423d786281c637ffe88d1ba5d5a"),
            Arguments
                .of("AAAA",
                    "207d1de947c444ad18c08a4158b4e56b4a5c8efabac9278ca7591e9b2ade7969"),
            Arguments.of("BBADABA123",
                "5046fc7086d31b6110b15e892902c7ef8abbe00b1407626fa5a73961d4e70f58")
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
        Assertions.assertEquals(expected,
            ConvertUtils.toHex(
                Ed25519Utils.prepareForScalarMultiply(privateKey).getRaw()));
    }

    // endregion
}
