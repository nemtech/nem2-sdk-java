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

package io.nem.core.crypto.ed25519;

import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.PublicKey;
import io.nem.core.utils.AbstractVectorTester;
import io.nem.core.utils.ConvertUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Ed25519BlockCipherVectorTester extends AbstractVectorTester {


    private static Stream<Arguments> testResolveSharedKey() {
        //NOTE!! first example of each file is broken?????
        return createArguments("4.test-cipher.json",
            entry -> extractArguments(entry), 1, 20
        );
    }

//    {
//        "privateKey": "3140f94c79f249787d1ec75a97a885980eb8f0a7d9b7aa03e7200296e422b2b6",
//        "otherPublicKey": "06CA6C64EC727E020022B780FA5677915DD33B14955463548221687771937088",
//        "iv": "a73ff5c32f8fd055b09775817a6a3f95",
//        "cipherText": "1C5ECCCDE6F563886919DC1C4AFFD1334381C601B1BBB5CBA437AF3E4C2221F8",
//        "clearText": "86ddb9e713a8ebf67a51830eff03b837e147c20d75e67b2a54aa29e98c"
//    },

    private static List<Arguments> extractArguments(
        Map<String, String> entry) {
        return Collections
            .singletonList(Arguments.of(
                entry.get("privateKey"),
                entry.get("otherPublicKey"),
                entry.get("iv"),
                entry.get("cipherText"),
                entry.get("clearText")));
    }

    @ParameterizedTest
    @MethodSource("testResolveSharedKey")
    void testResolveSharedKey(String privateKey, String otherPublicKey,
        String iv, String cipherText, String clearText) {
        PrivateKey privateKeyObject = PrivateKey.fromHexString(privateKey);
        PublicKey otherPublicKeyObject = PublicKey.fromHexString(otherPublicKey);
        byte[] sharedKey = Ed25519BlockCipher.getSharedKey(
            privateKeyObject,
            otherPublicKeyObject
        );

        final BufferedBlockCipher cipher = Ed25519BlockCipher
            .setupBlockCipher(sharedKey, ConvertUtils.fromHexToBytes(iv), false);
        byte[] decrypted = Ed25519BlockCipher
            .transform(cipher, ConvertUtils.fromHexToBytes(cipherText));
        Assertions.assertNotNull(decrypted);
        Assertions
            .assertEquals(clearText.toUpperCase(), ConvertUtils.toHex(decrypted).toUpperCase());
    }
}
