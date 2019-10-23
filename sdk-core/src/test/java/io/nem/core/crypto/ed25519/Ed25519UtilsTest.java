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
import io.nem.core.crypto.SignSchema;
import io.nem.core.test.Utils;
import java.math.BigInteger;
import java.util.stream.Stream;
import org.bouncycastle.util.encoders.Hex;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

public class Ed25519UtilsTest {

    private static Stream<Arguments> params() {
        return Stream.of(
            Arguments.of("227F", SignSchema.SHA3,
                "d8229a6d2bb1ee8ce10e5b283254c68b4ee8ab8a28fa078f6c47ddd3d2bb256e"),
            Arguments.of("227F", SignSchema.KECCAK,
                "704011e5b78404847b0a0a55f3a19c3db5401889ff438fc950537797baf42d77"),
            Arguments.of("AAAA", SignSchema.SHA3,
                "e092911d630f8fcad20b896b9d42f7a79c9fe2146bc8543ab4dcf7263e119255"),
            Arguments.of("AAAA", SignSchema.KECCAK,
                "e8cc94aeeab674586e0d62ad5f7dab2678dbdf43b73f13debdd014ed5a0c684a"),
            Arguments.of("BBADABA123", SignSchema.SHA3,
                "800df91a0217b997945f94dbd62c2b278925a56f040ebbc677671e396e7e3859"),
            Arguments.of("BBADABA123", SignSchema.KECCAK,
                "70f35d7c791981554bae85677606e61e1f29e70e0d8d7f288af795933f03a642")
        );
    }

    // region prepareForScalarMultiply

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void prepareForScalarMultiplyReturnsClampedValue(SignSchema signSchema) {
        // Arrange:
        final PrivateKey privateKey = new PrivateKey(new BigInteger(Utils.generateRandomBytes(32)));

        // Act:
        final byte[] a = Ed25519Utils.prepareForScalarMultiply(privateKey, signSchema).getRaw();

        // Assert:
        MatcherAssert.assertThat(a[31] & 0x40, IsEqual.equalTo(0x40));
        MatcherAssert.assertThat(a[31] & 0x80, IsEqual.equalTo(0x0));
        MatcherAssert.assertThat(a[0] & 0x7, IsEqual.equalTo(0x0));
    }

    @ParameterizedTest
    @MethodSource("params")
    public void shouldPrepareForScalarMultiply(String input,
        SignSchema signSchema, String expected) {
        // Arrange:
        final PrivateKey privateKey = PrivateKey.fromHexString(input);
        Assertions.assertEquals(expected,
            Hex.toHexString(
                Ed25519Utils.prepareForScalarMultiply(privateKey, signSchema).getRaw()));
    }

    // endregion
}
