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
import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.AbstractVectorTester;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Ed25519BlockCipherVectorTester extends AbstractVectorTester {

    @Test
    public void hkdfTest() throws NoSuchProviderException, NoSuchAlgorithmException {
        Digest hash = new SHA256Digest();
        byte[] sharedSecret = "string-or-buffer".getBytes();
        byte[] info = "catapult".getBytes();
        int l = 32;
        byte[] result = new byte[l];

        HKDFParameters params = new HKDFParameters(sharedSecret, null, info);
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(hash);
        hkdf.init(params);
        hkdf.generateBytes(result, 0, l);

        Assertions.assertEquals("E618ACB2558E1721492E4AE3BED3F4D86F26C2B0CE6AD939943A6A540855D23F",
            Hex.toHexString(result).toUpperCase());
        ;

    }

    private static Stream<Arguments> testResolveSharedKey() {
        //NOTE!! first example of each file is broken?????
        Stream<Arguments> catapultArguments = createArguments("4.test-cipher-catapult.json",
            entry -> extractArguments(SignSchema.SHA3, entry), 1, 20
        );
        Stream<Arguments> nis1Arguments = createArguments("4.test-cipher-nis1.json",
            entry -> extractArguments(SignSchema.KECCAK, entry), 1, 20
        );
        return Stream.concat(catapultArguments, nis1Arguments);
    }

//    {
//        "privateKey": "3140f94c79f249787d1ec75a97a885980eb8f0a7d9b7aa03e7200296e422b2b6",
//        "otherPublicKey": "06CA6C64EC727E020022B780FA5677915DD33B14955463548221687771937088",
//        "iv": "a73ff5c32f8fd055b09775817a6a3f95",
//        "cipherText": "1C5ECCCDE6F563886919DC1C4AFFD1334381C601B1BBB5CBA437AF3E4C2221F8",
//        "clearText": "86ddb9e713a8ebf67a51830eff03b837e147c20d75e67b2a54aa29e98c"
//    },

    private static List<Arguments> extractArguments(SignSchema signSchema,
        Map<String, String> entry) {
        return Collections
            .singletonList(Arguments.of(
                signSchema,
                entry.get("privateKey"),
                entry.get("otherPublicKey"),
                entry.get("iv"),
                entry.get("cipherText"),
                entry.get("clearText")));
    }

    @ParameterizedTest
    @MethodSource("testResolveSharedKey")
    void testResolveSharedKey(SignSchema signSchema, String privateKey, String otherPublicKey,
        String iv, String cipherText, String clearText) {
        PrivateKey privateKeyObject = PrivateKey.fromHexString(privateKey);
        PublicKey otherPublicKeyObject = PublicKey.fromHexString(otherPublicKey);
        byte[] sharedKey = Ed25519BlockCipher.getSharedKey(
            privateKeyObject,
            otherPublicKeyObject,
            signSchema);

        final BufferedBlockCipher cipher = Ed25519BlockCipher
            .setupBlockCipher(sharedKey, Hex.decode(iv), false);
        byte[] decrypted = Ed25519BlockCipher.transform(cipher, Hex.decode(cipherText));
        Assertions.assertNotNull(decrypted);
        Assertions.assertEquals(clearText.toUpperCase(), Hex.toHexString(decrypted).toUpperCase());
    }
}
