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
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.AbstractVectorTester;
import io.nem.symbol.core.utils.ConvertUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Ed25519BlockDeriveVectorTester extends AbstractVectorTester {

    private static Stream<Arguments> testResolveSharedKey() {
        //NOTE!! first example of each file is broken?????
        return createArguments("3.test-derive.json",
            Ed25519BlockDeriveVectorTester::extractArguments, 1, 20
        );
    }

    private static List<Arguments> extractArguments(
        Map<String, String> entry) {
        return Collections
            .singletonList(Arguments.of(
                entry.get("privateKey"),
                entry.get("otherPublicKey"),
                entry.get("scalarMulResult"),
                entry.get("sharedKey")));
    }

    @ParameterizedTest
    @MethodSource("testResolveSharedKey")
    void testResolveSharedKey(String privateKey,
        String otherPublicKey, String scalarMulResult, String sharedKey) {
        PrivateKey privateKeyObject = PrivateKey.fromHexString(privateKey);
        PublicKey otherPublicKeyObject = PublicKey.fromHexString(otherPublicKey);
        byte[] resolvedSharedKey = Ed25519BlockCipher.getSharedKey(
            privateKeyObject,
            otherPublicKeyObject
        );

        byte[] resolvedSharedSecret = Ed25519BlockCipher.getSharedSecret(
            privateKeyObject,
            otherPublicKeyObject
        );

        Assertions.assertEquals(sharedKey.toUpperCase(),
            ConvertUtils.toHex(resolvedSharedKey).toUpperCase());
        Assertions.assertEquals(scalarMulResult.toUpperCase(),
            ConvertUtils.toHex(resolvedSharedSecret).toUpperCase());
    }
}
