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

package io.nem.core.crypto.ed25519;

import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.PublicKey;
import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.AbstractVectorTester;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Ed25519BlockDeriveVectorTester extends AbstractVectorTester {

    private static Stream<Arguments> testResolveSharedKey() {
        //NOTE!! first example of each file is broken?????
        Stream<Arguments> catapultArguments = createArguments("3.test-derive-catapult.json",
            entry -> extractArguments(SignSchema.SHA3, entry), 1, 20
        );
        Stream<Arguments> nis1Arguments = createArguments("3.test-derive-nis1.json",
            entry -> extractArguments(SignSchema.KECCAK, entry), 1, 20
        );
        return Stream.concat(catapultArguments, nis1Arguments);
    }

    private static List<Arguments> extractArguments(SignSchema signSchema,
        Map<String, String> entry) {
        return Collections
            .singletonList(Arguments.of(
                signSchema,
                entry.get("privateKey"),
                entry.get("otherPublicKey"),
                entry.get("scalarMulResult"),
                entry.get("sharedKey")));
    }

    @ParameterizedTest
    @MethodSource("testResolveSharedKey")
    void testResolveSharedKey(SignSchema signSchema, String privateKey,
        String otherPublicKey, String scalarMulResult, String sharedKey) {
        PrivateKey privateKeyObject = PrivateKey.fromHexString(privateKey);
        PublicKey otherPublicKeyObject = PublicKey.fromHexString(otherPublicKey);
        byte[] resolvedSharedKey = Ed25519BlockCipher.getSharedKey(
            privateKeyObject,
            otherPublicKeyObject,
            signSchema);

        byte[] resolvedSharedSecret = Ed25519BlockCipher.getSharedSecret(
            privateKeyObject,
            otherPublicKeyObject,
            signSchema);

        Assertions.assertEquals(sharedKey.toUpperCase(),
            Hex.toHexString(resolvedSharedKey).toUpperCase());
        Assertions.assertEquals(scalarMulResult.toUpperCase(),
            Hex.toHexString(resolvedSharedSecret).toUpperCase());
    }
}
