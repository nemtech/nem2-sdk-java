/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.core.crypto;

import io.nem.core.utils.AbstractVectorTester;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test of SignSchema.
 */
public class KeyPairVectorTester extends AbstractVectorTester {

    private static Stream<Arguments> testKeysCatapult() {
        return createArguments("1.test-keys-catapult.json", KeyPairVectorTester::extractArguments,
            0,
            10
        );
    }

    private static Stream<Arguments> testKeysNis1() {
        return createArguments("1.test-keys-nis1.json", KeyPairVectorTester::extractArguments, 0, 10
        );
    }

    private static List<Arguments> extractArguments(Map<String, String> entry) {
        return Collections
            .singletonList(Arguments.of(entry.get("privateKey"), entry.get("publicKey")));
    }

    @ParameterizedTest
    @MethodSource("testKeysNis1")
    void testKeysNis1(String privateKey, String publicKey) {
        //Reversing to reuse nis1 tests.
        KeyPair keyPair = KeyPair
            .fromPrivate(PrivateKey.fromHexString(privateKey), SignSchema.KECCAK);
        Assertions
            .assertEquals(publicKey.toUpperCase(), keyPair.getPublicKey().toHex().toUpperCase());
    }

    @ParameterizedTest
    @MethodSource("testKeysCatapult")
    void testKeccakCatapult(String privateKey, String publicKey) {
        KeyPair keyPair = KeyPair
            .fromPrivate(PrivateKey.fromHexString(privateKey), SignSchema.SHA3);
        Assertions
            .assertEquals(publicKey.toUpperCase(), keyPair.getPublicKey().toHex().toUpperCase());
    }


}
