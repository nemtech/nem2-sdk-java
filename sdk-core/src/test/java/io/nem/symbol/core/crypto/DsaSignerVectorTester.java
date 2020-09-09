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

/** Test the signer using the vector test data. */
class DsaSignerVectorTester extends AbstractVectorTester {

  private static Stream<Arguments> testSignAll() {
    return createArguments("2.test-sign.json", DsaSignerVectorTester::extractArguments, 10);
  }

  private static List<Arguments> extractArguments(Map<String, String> entry) {
    return Collections.singletonList(
        Arguments.of(
            entry.get("privateKey"),
            entry.get("publicKey"),
            entry.get("data"),
            entry.get("length"),
            entry.get("signature")));
  }

  @ParameterizedTest
  @MethodSource("testSignAll")
  void testSignAll(String privateKey, String publicKey, String data, int length, String signature) {
    final CryptoEngine engine = CryptoEngines.defaultEngine();

    final KeyPair keyPair = KeyPair.fromPrivate(PrivateKey.fromHexString(privateKey));
    final DsaSigner signer = engine.createDsaSigner(keyPair);

    // Act:
    byte[] input = ConvertUtils.fromHexToBytes(data);
    final Signature signatureObject = signer.sign(input);

    // Assert:
    Assertions.assertTrue(signer.verify(input, signatureObject));
    Assertions.assertEquals(signature.toUpperCase(), signatureObject.toString().toUpperCase());
    Assertions.assertEquals(publicKey.toUpperCase(), keyPair.getPublicKey().toHex());
    Assertions.assertEquals(length, input.length);
  }
}
