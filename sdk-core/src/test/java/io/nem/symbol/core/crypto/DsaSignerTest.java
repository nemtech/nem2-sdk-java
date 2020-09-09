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

import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public abstract class DsaSignerTest {

  @Test
  public void signedDataCanBeVerified() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final Signature signature = dsaSigner.sign(input);

    // Assert:
    Assertions.assertTrue(dsaSigner.verify(input, signature));
  }

  @Test
  public void dataSignedWithKeyPairCannotBeVerifiedWithDifferentKeyPair() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp1 = KeyPair.random(engine);
    final KeyPair kp2 = KeyPair.random(engine);
    final DsaSigner dsaSigner1 = this.getDsaSigner(kp1);
    final DsaSigner dsaSigner2 = this.getDsaSigner(kp2);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final Signature signature1 = dsaSigner1.sign(input);
    final Signature signature2 = dsaSigner2.sign(input);

    // Assert:
    Assertions.assertTrue(dsaSigner1.verify(input, signature1));
    Assertions.assertFalse(dsaSigner1.verify(input, signature2));
    Assertions.assertFalse(dsaSigner2.verify(input, signature1));
    Assertions.assertTrue(dsaSigner2.verify(input, signature2));
  }

  @Test
  public void signaturesReturnedBySignAreDeterministic() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final Signature signature1 = dsaSigner.sign(input);
    final Signature signature2 = dsaSigner.sign(input);

    // Assert:
    Assertions.assertEquals(signature1, signature2);
  }

  @Test
  public void cannotSignPayloadWithoutPrivateKey() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.onlyPublic(KeyPair.random(engine).getPublicKey(), engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    CryptoException exception =
        Assertions.assertThrows(CryptoException.class, () -> dsaSigner.sign(input));

    Assertions.assertEquals("cannot sign without private key", exception.getMessage());
  }

  @Test
  public void isCanonicalReturnsTrueForCanonicalSignature() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = this.getDsaSigner(kp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final Signature signature = dsaSigner.sign(input);

    // Assert:
    Assertions.assertTrue(dsaSigner.isCanonicalSignature(signature));
  }

  @Test
  public void verifyCallsIsCanonicalSignature() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final DsaSigner dsaSigner = Mockito.spy(this.getDsaSigner(kp));
    final byte[] input = RandomUtils.generateRandomBytes();
    final Signature signature = new Signature(BigInteger.ONE, BigInteger.ONE);

    // Act:
    dsaSigner.verify(input, signature);

    // Assert:
    Mockito.verify(dsaSigner, Mockito.times(1)).isCanonicalSignature(signature);
  }

  protected DsaSigner getDsaSigner(final KeyPair keyPair) {
    return this.getCryptoEngine().createDsaSigner(keyPair);
  }

  protected abstract CryptoEngine getCryptoEngine();
}
