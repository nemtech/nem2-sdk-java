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
import io.nem.symbol.core.crypto.CryptoException;
import io.nem.symbol.core.crypto.DsaSigner;
import io.nem.symbol.core.crypto.DsaSignerTest;
import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.Signature;
import io.nem.symbol.core.crypto.ed25519.arithmetic.MathUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class Ed25519DsaSignerTest extends DsaSignerTest {

  @Test
  public void isCanonicalReturnsFalseForNonCanonicalSignature() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final Signature signature = dsaSigner.sign(input);
    final BigInteger nonCanonicalS = engine.getCurve().getGroupOrder().add(signature.getS());
    final Signature nonCanonicalSignature = new Signature(signature.getR(), nonCanonicalS);

    // Assert:
    Assertions.assertFalse(dsaSigner.isCanonicalSignature(nonCanonicalSignature));
  }

  @Test
  public void makeCanonicalMakesNonCanonicalSignatureCanonical() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final Signature signature = dsaSigner.sign(input);
    final BigInteger nonCanonicalS = engine.getCurve().getGroupOrder().add(signature.getS());
    final Signature nonCanonicalSignature = new Signature(signature.getR(), nonCanonicalS);
    Assertions.assertFalse(dsaSigner.isCanonicalSignature(nonCanonicalSignature));
    final Signature canonicalSignature = dsaSigner.makeSignatureCanonical(nonCanonicalSignature);

    // Assert:
    Assertions.assertTrue(dsaSigner.isCanonicalSignature(canonicalSignature));
  }

  @Test
  public void replacingRWithGroupOrderPlusRInSignatureRuinsSignature() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final BigInteger groupOrder = engine.getCurve().getGroupOrder();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    Signature signature;
    byte[] input;
    while (true) {
      input = RandomUtils.generateRandomBytes();
      signature = dsaSigner.sign(input);
      if (signature.getR().add(groupOrder).compareTo(BigInteger.ONE.shiftLeft(256)) < 0) {
        break;
      }
    }

    // Act:
    final Signature signature2 = new Signature(groupOrder.add(signature.getR()), signature.getS());

    // Assert:
    Assertions.assertFalse(dsaSigner.verify(input, signature2));
  }

  @Test
  public void signReturnsExpectedSignature() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair keyPair = KeyPair.random(engine);
    for (int i = 0; i < 20; i++) {
      final DsaSigner dsaSigner = this.getDsaSigner(keyPair);
      final byte[] input = RandomUtils.generateRandomBytes();

      // Act:
      final Signature signature1 = dsaSigner.sign(input);
      final Signature signature2 = MathUtils.sign(keyPair, input);

      // Assert:
      Assertions.assertEquals(signature1, signature2);
    }
  }

  @Test
  public void signReturnsVerifiableSignature() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair keyPair = KeyPair.random(engine);
    for (int i = 0; i < 20; i++) {
      final DsaSigner dsaSigner = this.getDsaSigner(keyPair);
      final byte[] input = RandomUtils.generateRandomBytes();

      // Act:
      final Signature signature1 = dsaSigner.sign(input);

      // Assert:
      Assertions.assertTrue(dsaSigner.verify(input, signature1));
    }
  }

  @Test
  public void signThrowsIfGeneratedSignatureIsNotCanonical() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair keyPair = KeyPair.random(engine);
    final Ed25519DsaSigner dsaSigner =
        new Ed25519DsaSigner(keyPair) {
          @Override
          public boolean isCanonicalSignature(Signature signature) {
            return false;
          }
        };
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    Assertions.assertEquals(
        "Generated signature is not canonical",
        Assertions.assertThrows(CryptoException.class, () -> dsaSigner.sign(input)).getMessage());
  }

  @Test
  public void verifyReturnsFalseIfPublicKeyIsZeroArray() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    final byte[] input = RandomUtils.generateRandomBytes();
    final Signature signature = dsaSigner.sign(input);
    final Ed25519DsaSigner dsaSignerWithZeroArrayPublicKey = Mockito.mock(Ed25519DsaSigner.class);
    final KeyPair keyPairWithZeroArrayPublicKey = Mockito.mock(KeyPair.class);
    Mockito.when(dsaSignerWithZeroArrayPublicKey.getKeyPair())
        .thenReturn(keyPairWithZeroArrayPublicKey);
    Mockito.when(keyPairWithZeroArrayPublicKey.getPublicKey())
        .thenReturn(new PublicKey(new byte[32]));
    Mockito.when(dsaSignerWithZeroArrayPublicKey.verify(input, signature)).thenCallRealMethod();
    Mockito.when(dsaSignerWithZeroArrayPublicKey.isCanonicalSignature(signature)).thenReturn(true);

    // Act:
    final boolean result = dsaSignerWithZeroArrayPublicKey.verify(input, signature);

    // Assert (getKeyPair() would be called more than once if it got beyond the
    // second check):
    Assertions.assertFalse(result);
    Mockito.verify(dsaSignerWithZeroArrayPublicKey, Mockito.times(1))
        .isCanonicalSignature(signature);
    Mockito.verify(dsaSignerWithZeroArrayPublicKey, Mockito.times(1)).getKeyPair();
  }

  @Override
  protected CryptoEngine getCryptoEngine() {
    return CryptoEngines.ed25519Engine();
  }
}
