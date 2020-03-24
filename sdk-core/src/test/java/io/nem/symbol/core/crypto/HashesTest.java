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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.util.Arrays;
import java.util.function.Function;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;

public class HashesTest {


    private static final HashTester SHA3_256_TESTER = new HashTester(Hashes::sha3_256, 32);
    private static final HashTester SHA3_512_TESTER = new HashTester(Hashes::sha3_512, 64);
    private static final HashTester RIPEMD160_TESTER = new HashTester(Hashes::ripemd160, 20);
    private static final HashTester HASH_256_TESTER = new HashTester(Hashes::hash256, 32);
    private static final HashTester SHA_512_TESTER = new HashTester(Hashes::sha512, 64);
    private static final HashTester HASH_160_TESTER = new HashTester(Hashes::hash160, 20);
    private static final HashTester KECCAK_256_TESTER = new HashTester(Hashes::keccak256, 32);

    @Test
    public void testLeading00Hashes() {
        final String hex = "00137c7c32881d1fff2e905f5b7034bcbcdb806d232f351db48a7816285c548f";
        final PrivateKey privateKey = PrivateKey.fromHexString(hex);
        MatcherAssert.assertThat(ConvertUtils.toHex(privateKey.getBytes()),
            IsEqual.equalTo("00137C7C32881D1FFF2E905F5B7034BCBCDB806D232F351DB48A7816285C548F"));
        final byte[] hash = Hashes.sha512(privateKey.getBytes());

        MatcherAssert.assertThat(ConvertUtils.toHex(hash),
            IsEqual.equalTo("47C755C29EC3494D59A4A7ECC497302271D46F2F69E1697AC15FA8C2B480CBC2FF6517F780C51B59E7E79AAF74C9CE04E65FCFDC62A157956AE0DC23C105E6B7"));

        final byte[] d = Arrays.copyOfRange(hash, 0, 32);
        String actual = ConvertUtils.toHex(d);
        String expected = "47C755C29EC3494D59A4A7ECC497302271D46F2F69E1697AC15FA8C2B480CBC2";
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    @Test
    public void testNoLoadingZeros() {
        final String hex = "e8857f8e488d4e6d4b71bcd44bb4cff49208c32651e1f6500c3b58cafeb8def6";
        final PrivateKey privateKey = PrivateKey.fromHexString(hex);

        MatcherAssert.assertThat(ConvertUtils.toHex(privateKey.getBytes()),
            IsEqual.equalTo("E8857F8E488D4E6D4B71BCD44BB4CFF49208C32651E1F6500C3B58CAFEB8DEF6"));

        final byte[] hash = Hashes.sha512(privateKey.getBytes());

        MatcherAssert.assertThat(ConvertUtils.toHex(hash),
            IsEqual.equalTo("CE46355C204ECF9CD7FF922B59934C40926759C4651D4BC34CA7B89BF5DF6A22EEE0B5ED6759085057D8AA6E032C76944370E77A81B318B0215EEEFD45DD9C46"));

        final byte[] d = Arrays.copyOfRange(hash, 0, 32);
        String actual = ConvertUtils.toHex(d);
        String expected = "CE46355C204ECF9CD7FF922B59934C40926759C4651D4BC34CA7B89BF5DF6A22";
        MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
    }

    // region sha3_256

    private static void assertHashesAreDifferent(
        final Function<byte[], byte[]> hashFunction1,
        final Function<byte[], byte[]> hashFunction2) {

        // Arrange:
        final byte[] input = RandomUtils.generateRandomBytes();

        // Act:
        final byte[] hash1 = hashFunction1.apply(input);
        final byte[] hash2 = hashFunction2.apply(input);

        // Assert:
        MatcherAssert.assertThat(hash2, IsNot.not(IsEqual.equalTo(hash1)));
    }

    @Test
    public void sha3_256HashHasExpectedByteLength() {
        // Assert:
        SHA3_256_TESTER.assertHashHasExpectedLength();
    }

    @Test
    public void sha3_256GeneratesSameHashForSameInputs() {
        // Assert:
        SHA3_256_TESTER.assertHashIsSameForSameInputs();
    }

    @Test
    public void sha3_256GeneratesSameHashForSameMergedInputs() {
        // Assert:
        SHA3_256_TESTER.assertHashIsSameForSplitInputs();
    }

    @Test
    public void sha3_256GeneratesDifferentHashForDifferentInputs() {
        // Assert:
        SHA3_256_TESTER.assertHashIsDifferentForDifferentInputs();
    }

    //SHA_512_TESTER

    @Test
    public void sha512_HashHasExpectedByteLength() {
        // Assert:
        SHA_512_TESTER.assertHashHasExpectedLength();
    }

    @Test
    public void sha512_GeneratesSameHashForSameInputs() {
        // Assert:
        SHA_512_TESTER.assertHashIsSameForSameInputs();
    }

    @Test
    public void sha512_GeneratesSameHashForSameMergedInputs() {
        // Assert:
        SHA_512_TESTER.assertHashIsSameForSplitInputs();
    }

    @Test
    public void sha512_GeneratesDifferentHashForDifferentInputs() {
        // Assert:
        SHA_512_TESTER.assertHashIsDifferentForDifferentInputs();
    }

    // endregion

    // region sha3_512

    @Test
    public void sha3_512HashHasExpectedByteLength() {
        // Assert:
        SHA3_512_TESTER.assertHashHasExpectedLength();
    }

    @Test
    public void sha3_512GeneratesSameHashForSameInputs() {
        // Assert:
        SHA3_512_TESTER.assertHashIsSameForSameInputs();
    }

    @Test
    public void sha3_512GeneratesSameHashForSameMergedInputs() {
        // Assert:
        SHA3_512_TESTER.assertHashIsSameForSplitInputs();
    }

    @Test
    public void sha3_512GeneratesDifferentHashForDifferentInputs() {
        // Assert:
        SHA3_512_TESTER.assertHashIsDifferentForDifferentInputs();
    }

    // endregion

    // region ripemd160

    @Test
    public void ripemd160HashHasExpectedByteLength() {
        // Assert:
        RIPEMD160_TESTER.assertHashHasExpectedLength();
    }

    @Test
    public void ripemd160GeneratesSameHashForSameInputs() {
        // Assert:
        RIPEMD160_TESTER.assertHashIsSameForSameInputs();
    }

    @Test
    public void ripemd160GeneratesSameHashForSameMergedInputs() {
        // Assert:
        RIPEMD160_TESTER.assertHashIsSameForSplitInputs();
    }

    @Test
    public void ripemd160GeneratesDifferentHashForDifferentInputs() {
        // Assert:
        RIPEMD160_TESTER.assertHashIsDifferentForDifferentInputs();
    }

    // endregion

    // region keccak_256

    @Test
    public void keccak_256HashHasExpectedByteLength() {
        // Assert:
        KECCAK_256_TESTER.assertHashHasExpectedLength();
    }

    @Test
    public void keccak_256GeneratesSameHashForSameInputs() {
        // Assert:
        KECCAK_256_TESTER.assertHashIsSameForSameInputs();
    }

    @Test
    public void keccak_256GeneratesSameHashForSameMergedInputs() {
        // Assert:
        KECCAK_256_TESTER.assertHashIsSameForSplitInputs();
    }

    @Test
    public void keccak_256GeneratesDifferentHashForDifferentInputs() {
        // Assert:
        KECCAK_256_TESTER.assertHashIsDifferentForDifferentInputs();
    }

    // endregion

    // region HASH_160

    @Test
    public void hash_160HashHasExpectedByteLength() {
        // Assert:
        HASH_160_TESTER.assertHashHasExpectedLength();
    }

    @Test
    public void hash_160GeneratesSameHashForSameInputs() {
        // Assert:
        HASH_160_TESTER.assertHashIsSameForSameInputs();
    }

    @Test
    public void hash_160GeneratesSameHashForSameMergedInputs() {
        // Assert:
        HASH_160_TESTER.assertHashIsSameForSplitInputs();
    }

    @Test
    public void hash_160GeneratesDifferentHashForDifferentInputs() {
        // Assert:
        HASH_160_TESTER.assertHashIsDifferentForDifferentInputs();
    }

    // endregion

    // region HASH_256

    @Test
    public void hash_256HashHasExpectedByteLength() {
        // Assert:
        HASH_256_TESTER.assertHashHasExpectedLength();
    }

    @Test
    public void hash_256GeneratesSameHashForSameInputs() {
        // Assert:
        HASH_256_TESTER.assertHashIsSameForSameInputs();
    }

    @Test
    public void hash_256GeneratesSameHashForSameMergedInputs() {
        // Assert:
        HASH_256_TESTER.assertHashIsSameForSplitInputs();
    }

    @Test
    public void hash_256GeneratesDifferentHashForDifferentInputs() {
        // Assert:
        HASH_256_TESTER.assertHashIsDifferentForDifferentInputs();
    }

    // endregion

    // region different hash algorithm

    @Test
    public void sha3_256AndRipemd160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_256, Hashes::ripemd160);
    }

    @Test
    public void sha3_256AndSha3_512GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_256, Hashes::sha3_512);
    }

    @Test
    public void sha3_256AndKeccak_256GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_256, Hashes::keccak256);
    }

    @Test
    public void sha3_256AndHash_160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_256, Hashes::hash160);
    }

    @Test
    public void sha3_256AndHash_256GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_256, Hashes::hash256);
    }

    @Test
    public void sha3_512AndRipemd160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_512, Hashes::ripemd160);
    }

    @Test
    public void sha3_512AndKeccak_256GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_512, Hashes::keccak256);
    }

    @Test
    public void sha3_512AndHash_160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_512, Hashes::hash160);
    }

    @Test
    public void sha3_512AndHash_256GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::sha3_512, Hashes::hash256);
    }

    @Test
    public void keccak_256AndRipemd160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::keccak256, Hashes::ripemd160);
    }

    @Test
    public void keccak_256AndHash_160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::keccak256, Hashes::hash160);
    }

    @Test
    public void keccak_256AndHash_256GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::keccak256, Hashes::hash256);
    }

    @Test
    public void hash_160AndRipemd160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::hash160, Hashes::ripemd160);
    }

    @Test
    public void hash_160AndHash_256GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::hash160, Hashes::hash256);
    }

    @Test
    public void hash_256AndRipemd160GenerateDifferentHashForSameInputs() {
        // Assert:
        assertHashesAreDifferent(Hashes::hash256, Hashes::ripemd160);
    }

    // endregion

    private static class HashTester {

        private final Function<byte[], byte[]> hashFunction;
        private final Function<byte[][], byte[]> hashMultipleFunction;
        private final int expectedHashLength;

        public HashTester(
            final Function<byte[][], byte[]> hashMultipleFunction, final int expectedHashLength) {
            this.hashMultipleFunction = hashMultipleFunction;
            this.hashFunction = input -> hashMultipleFunction.apply(new byte[][]{input});
            this.expectedHashLength = expectedHashLength;
        }

        // this is only for Keccak function, accepting only byte array
        public HashTester(final int expectedHashLength,
            final Function<byte[], byte[]> hashFunction) {
            this.hashMultipleFunction = null;
            this.hashFunction = input -> hashFunction.apply(input);
            this.expectedHashLength = expectedHashLength;
        }

        private static byte[][] split(final byte[] input) {
            return new byte[][]{
                Arrays.copyOfRange(input, 0, 17),
                Arrays.copyOfRange(input, 17, 100),
                Arrays.copyOfRange(input, 100, input.length)
            };
        }

        public void assertHashHasExpectedLength() {
            // Arrange:
            final byte[] input = RandomUtils.generateRandomBytes();

            // Act:
            final byte[] hash = this.hashFunction.apply(input);

            // Assert:
            MatcherAssert.assertThat(hash.length, IsEqual.equalTo(this.expectedHashLength));
        }

        public void assertHashIsSameForSameInputs() {
            // Arrange:
            final byte[] input = RandomUtils.generateRandomBytes();

            // Act:
            final byte[] hash1 = this.hashFunction.apply(input);
            final byte[] hash2 = this.hashFunction.apply(input);

            // Assert:
            MatcherAssert.assertThat(hash2, IsEqual.equalTo(hash1));
        }

        public void assertHashIsSameForSplitInputs() {
            // Arrange:
            final byte[] input = RandomUtils.generateRandomBytes();

            // Act:
            final byte[] hash1 = this.hashFunction.apply(input);
            final byte[] hash2 = this.hashMultipleFunction.apply(split(input));

            // Assert:
            MatcherAssert.assertThat(hash2, IsEqual.equalTo(hash1));
        }

        public void assertHashIsDifferentForDifferentInputs() {
            // Arrange:
            final byte[] input1 = RandomUtils.generateRandomBytes();
            final byte[] input2 = RandomUtils.generateRandomBytes();

            // Act:
            final byte[] hash1 = this.hashFunction.apply(input1);
            final byte[] hash2 = this.hashFunction.apply(input2);

            // Assert:
            MatcherAssert.assertThat(hash2, IsNot.not(IsEqual.equalTo(hash1)));
        }
    }
}
