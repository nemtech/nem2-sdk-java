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

import io.nem.symbol.core.crypto.KeyPair;
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

class Ed25519BlockCipherVectorTester extends AbstractVectorTester {


    private static Stream<Arguments> cipherVector() {
        return createArguments("4.test-cipher.json", Ed25519BlockCipherVectorTester::extractArguments, 20);
    }


    private static List<Arguments> extractArguments(Map<String, String> entry) {
        return Collections.singletonList(Arguments
            .of(entry.get("privateKey"), entry.get("otherPublicKey"), entry.get("iv"), entry.get("tag"),
                entry.get("cipherText"), entry.get("clearText")));
    }

    @ParameterizedTest
    @MethodSource("cipherVector")
    void testDecrypt(String privateKey, String otherPublicKey, String iv, String tag, String cipherText,
        String clearText) {
        PrivateKey privateKeyObject = PrivateKey.fromHexString(privateKey);
        PublicKey otherPublicKeyObject = PublicKey.fromHexString(otherPublicKey);

        Ed25519BlockCipher cipher = new Ed25519BlockCipher(KeyPair.onlyPublic(otherPublicKeyObject),
            KeyPair.fromPrivate(privateKeyObject));

        byte[] decrypted = cipher.decode(ConvertUtils.fromHexToBytes(tag), ConvertUtils.fromHexToBytes(iv),
            ConvertUtils.fromHexToBytes(cipherText));

        Assertions.assertNotNull(decrypted);
        Assertions.assertEquals(clearText.toUpperCase(), ConvertUtils.toHex(decrypted).toUpperCase());
    }


    @ParameterizedTest
    @MethodSource("cipherVector")
    void testEncrypt(String privateKey, String otherPublicKey, String iv, String tag, String cipherText,
        String clearText) {
        PrivateKey privateKeyObject = PrivateKey.fromHexString(privateKey);
        PublicKey otherPublicKeyObject = PublicKey.fromHexString(otherPublicKey);

        Ed25519BlockCipher cipher = new Ed25519BlockCipher(KeyPair.fromPrivate(privateKeyObject),
            KeyPair.onlyPublic(otherPublicKeyObject));

        AuthenticatedCipherText encodedData = cipher
            .encode(ConvertUtils.fromHexToBytes(clearText), ConvertUtils.fromHexToBytes(iv));

        Assertions.assertEquals(iv, ConvertUtils.toHex(encodedData.getIv()));
        Assertions.assertEquals(cipherText, ConvertUtils.toHex(encodedData.getCipherText()));
        Assertions.assertEquals(tag, ConvertUtils.toHex(encodedData.getAuthenticationTag()));

        byte[] encryptData = cipher.encrypt(ConvertUtils.fromHexToBytes(clearText), ConvertUtils.fromHexToBytes(iv));
        Assertions.assertEquals(tag + iv + cipherText, ConvertUtils.toHex(encryptData));


    }

}
