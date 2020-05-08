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

package io.nem.symbol.core.crypto;

import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VotingKeyTest {

    private static final byte[] TEST_BYTES = RandomUtils.generateRandomBytes(VotingKey.SIZE);
    private static final byte[] MODIFIED_TEST_BYTES = RandomUtils.generateRandomBytes(VotingKey.SIZE);

    // region constructors / factories

    @Test
    public void canCreateFromBytes() {
        // Arrange:
        final VotingKey key = new VotingKey(TEST_BYTES);

        // Assert:
        MatcherAssert.assertThat(key.getBytes(), IsEqual.equalTo(TEST_BYTES));
    }

    @Test
    public void canCreateFromHexString() {
        // Arrange:
        final VotingKey key = VotingKey.fromHexString("227F");
        Assertions.assertEquals(VotingKey.SIZE, key.getSize());

        // Assert:
        MatcherAssert.assertThat(key.getBytes(), IsEqual.equalTo(ByteUtils.byteArrayLeadingZeros(new byte[]{0x22, 0x7F},VotingKey.SIZE)));
    }

    @Test
    public void shouldBeEquals() {
        // Arrange:
        final VotingKey key1 = VotingKey.fromHexString("227F");
        final VotingKey key2 = VotingKey.fromHexString("227F");
        final VotingKey key3 = VotingKey.fromHexString("327F");

        // Assert:
        MatcherAssert.assertThat(key1, IsEqual.equalTo(key2));
        MatcherAssert.assertThat(key1, IsNot.not(IsEqual.equalTo(key3)));
    }

    @Test
    public void cannotCreateAroundMalformedHexString() {
        // Act:
        Assertions.assertThrows(IllegalArgumentException.class, () -> VotingKey.fromHexString("22G75"));
    }

    // endregion

    // region serializer

    // endregion

    // region equals / hashCode

    @Test
    public void equalsOnlyReturnsTrueForEquivalentObjects() {
        // Arrange:
        final VotingKey key = new VotingKey(TEST_BYTES);

        // Assert:
        MatcherAssert.assertThat(new VotingKey(TEST_BYTES), IsEqual.equalTo(key));
        MatcherAssert
            .assertThat(new VotingKey(MODIFIED_TEST_BYTES), IsNot.not(IsEqual.equalTo(key)));
        MatcherAssert.assertThat(null, IsNot.not(IsEqual.equalTo(key)));
        MatcherAssert.assertThat(TEST_BYTES, IsNot.not(IsEqual.equalTo(key)));
        MatcherAssert.assertThat(key, IsNot.not(IsEqual.equalTo("ImNotAVotingKey")));
    }

    @Test
    public void canGetByteBuffers() {
        // Arrange:
        final VotingKey key = new VotingKey(TEST_BYTES);
        Assertions.assertEquals(TEST_BYTES, key.getByteBuffer().array());
    }

    // endregion

    // region toString

    @Test
    public void toStringReturnsHexRepresentation() {
        // Assert:
        MatcherAssert.assertThat(
            new VotingKey(TEST_BYTES).toHex().toUpperCase(),
            IsEqual.equalTo(ConvertUtils.toHex(TEST_BYTES).toUpperCase()));
    }

    // endregion
}
