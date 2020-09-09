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

import io.nem.symbol.core.crypto.CryptoEngine;
import io.nem.symbol.core.crypto.CryptoEngines;
import io.nem.symbol.core.crypto.KeyGenerator;
import io.nem.symbol.core.crypto.KeyGeneratorTest;
import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.ed25519.arithmetic.Ed25519EncodedGroupElement;
import io.nem.symbol.core.crypto.ed25519.arithmetic.Ed25519GroupElement;
import io.nem.symbol.core.crypto.ed25519.arithmetic.MathUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ed25519KeyGeneratorTest extends KeyGeneratorTest {

  @Test
  public void derivedPublicKeyIsValidPointOnCurve() {
    // Arrange:
    final KeyGenerator generator = this.getKeyGenerator();
    for (int i = 0; i < 100; i++) {
      final KeyPair kp = generator.generateKeyPair();

      // Act:
      final PublicKey publicKey = generator.derivePublicKey(kp.getPrivateKey());

      // Assert (throws if not on the curve):
      Ed25519GroupElement decode = new Ed25519EncodedGroupElement(publicKey.getBytes()).decode();

      Assertions.assertNotNull(decode);
    }
  }

  @Test
  public void derivePublicKeyReturnsExpectedPublicKey() {
    // Arrange:
    final KeyGenerator generator = this.getKeyGenerator();
    for (int i = 0; i < 100; i++) {
      final KeyPair kp = generator.generateKeyPair();

      // Act:
      final PublicKey publicKey1 = generator.derivePublicKey(kp.getPrivateKey());
      final PublicKey publicKey2 = MathUtils.derivePublicKey(kp.getPrivateKey());

      // Assert:
      MatcherAssert.assertThat(publicKey1, IsEqual.equalTo(publicKey2));
    }
  }

  @Test
  public void derivePublicKey() {
    final KeyGenerator generator = this.getKeyGenerator();
    final KeyPair keyPair =
        KeyPair.fromPrivate(
            PrivateKey.fromHexString(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d"));

    final PublicKey publicKey = generator.derivePublicKey(keyPair.getPrivateKey());

    final PublicKey expected =
        PublicKey.fromHexString("2134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2");
    MatcherAssert.assertThat(publicKey, IsEqual.equalTo(expected));
  }

  @Override
  protected CryptoEngine getCryptoEngine() {
    return CryptoEngines.ed25519Engine();
  }
}
