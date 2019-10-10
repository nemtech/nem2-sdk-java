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
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test the signer using the vector test data.
 */
public class DsaSignerVectorTester extends AbstractVectorTester {

    private static Stream<Arguments> testSignAll() throws Exception {
        Stream<Arguments> catapultArguments = createArguments("2.test-sign-catapult.json",
            entry -> extractArguments(SignSchema.SHA3, entry), 0, 10
        );
        Stream<Arguments> nis1Arguments = createArguments("2.test-sign-nis1.json",
            entry -> extractArguments(SignSchema.KECCAK, entry), 0, 10
        );
        return Stream.concat(catapultArguments, nis1Arguments);
    }

    private static List<Arguments> extractArguments(SignSchema signSchema, Map<String, String> entry) {
        return Collections
            .singletonList(Arguments.of(
                signSchema,
                entry.get("privateKey"),
                entry.get("publicKey"),
                entry.get("data"),
                entry.get("length"),
                entry.get("signature")));
    }


    @ParameterizedTest
    @MethodSource("testSignAll")
    void testSignAll(SignSchema signSchema,  String privateKey, String publicKey,
        String data,
        int length, String signature) {
        final CryptoEngine engine = CryptoEngines.defaultEngine();

        //Reusing vector NIS 1 vector tests by reversing the private key when using SignSchema.KECCAK
        final KeyPair keyPair = KeyPair
            .fromPrivate(
                PrivateKey.fromHexString(
                    signSchema == SignSchema.KECCAK ? SignSchema.reverse(privateKey) : privateKey),
                signSchema);
        final DsaSigner signer = engine.createDsaSigner(keyPair, signSchema);

        // Act:
        byte[] input = Hex.decode(data);
        final Signature signatureObject = signer.sign(input);

        // Assert:
        Assertions.assertTrue(signer.verify(input, signatureObject));
        Assertions.assertEquals(signature.toUpperCase(), signatureObject.toString().toUpperCase());
        Assertions.assertEquals(publicKey.toUpperCase(), keyPair.getPublicKey().toHex());
        Assertions.assertEquals(length, input.length);

    }

}
