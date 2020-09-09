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

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class KeyPairTest {

  // region basic construction

  @Test
  void ctorCanCreateNewKeyPair() {
    // Act:
    final KeyPair kp = KeyPair.random();

    // Assert:
    MatcherAssert.assertThat(kp.hasPrivateKey(), IsEqual.equalTo(true));
    MatcherAssert.assertThat(kp.getPrivateKey(), IsNull.notNullValue());
    MatcherAssert.assertThat(kp.getPublicKey(), IsNull.notNullValue());
  }

  @Test
  void ctorCanCreateKeyPairAroundPrivateKey() {
    // Arrange:
    final KeyPair kp1 = KeyPair.random();

    // Act:
    final KeyPair kp2 = KeyPair.fromPrivate(kp1.getPrivateKey());

    // Assert:
    MatcherAssert.assertThat(kp2.hasPrivateKey(), IsEqual.equalTo(true));
    MatcherAssert.assertThat(kp2.getPrivateKey(), IsEqual.equalTo(kp1.getPrivateKey()));
    MatcherAssert.assertThat(kp2.getPublicKey(), IsEqual.equalTo(kp1.getPublicKey()));
  }

  @Test
  void ctorCanCreateKeyPairAroundPublicKey() {
    // Arrange:
    final KeyPair kp1 = KeyPair.random();

    // Act:
    final KeyPair kp2 = KeyPair.onlyPublic(kp1.getPublicKey());

    // Assert:
    MatcherAssert.assertThat(kp2.hasPrivateKey(), IsEqual.equalTo(false));
    MatcherAssert.assertThat(kp2.getPublicKey(), IsEqual.equalTo(kp1.getPublicKey()));

    IllegalStateException exception =
        Assertions.assertThrows(IllegalStateException.class, kp2::getPrivateKey);
    Assertions.assertEquals("Private Key hasn't been provided.", exception.getMessage());
  }

  // endregion

  @Test
  void ctorCreatesDifferentInstancesWithDifferentKeys() {
    // Act:
    final KeyPair kp1 = KeyPair.random();
    final KeyPair kp2 = KeyPair.random();

    // Assert:
    MatcherAssert.assertThat(kp2.getPrivateKey(), IsNot.not(IsEqual.equalTo(kp1.getPrivateKey())));
    MatcherAssert.assertThat(kp2.getPublicKey(), IsNot.not(IsEqual.equalTo(kp1.getPublicKey())));
  }

  @Test
  void ctorFailsIfPublicKeyIsNotCompressed() {
    // Arrange:
    final KeyPairContext context = new KeyPairContext();
    final PublicKey publicKey = Mockito.mock(PublicKey.class);
    Mockito.when(context.analyzer.isKeyCompressed(publicKey)).thenReturn(false);

    // Act:
    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> KeyPair.onlyPublic(publicKey, context.engine));
    Assertions.assertEquals("PublicKey must be in compressed form", exception.getMessage());
  }

  // region delegation

  @Test
  void ctorCreatesKeyGenerator() {
    // Arrange:
    final KeyPairContext context = new KeyPairContext();

    // Act:
    KeyPair.random(context.engine);

    // Assert:
    Mockito.verify(context.engine, Mockito.times(1)).createKeyGenerator();
  }

  @Test
  void ctorDelegatesKeyGenerationToKeyGenerator() {
    // Arrange:
    final KeyPairContext context = new KeyPairContext();

    // Act:
    KeyPair.random(context.engine);

    // Assert:
    Mockito.verify(context.generator, Mockito.times(1)).generateKeyPair();
  }

  @Test
  void ctorWithPrivateKeyDelegatesToDerivePublicKey() {
    // Arrange:
    final KeyPairContext context = new KeyPairContext();

    // Act:
    KeyPair.fromPrivate(context.privateKey, context.engine);

    // Assert:
    Mockito.verify(context.generator, Mockito.times(1)).derivePublicKey(context.privateKey);
  }

  @Test
  void createFromKnownValue() {
    KeyPair keyPair =
        KeyPair.fromPrivate(
            PrivateKey.fromHexString(
                "575dbb3062267eff57c970a336ebbc8fbcfe12c5bd3ed7bc11eb0481d7704ced"));

    Assertions.assertEquals(
        "2E834140FD66CF87B254A693A2C7862C819217B676D3943267156625E816EC6F",
        keyPair.getPublicKey().toHex());
  }

  private class KeyPairContext {

    private final CryptoEngine engine = Mockito.mock(CryptoEngine.class);
    private final KeyAnalyzer analyzer = Mockito.mock(KeyAnalyzer.class);
    private final KeyGenerator generator = Mockito.mock(KeyGenerator.class);
    private final PrivateKey privateKey = Mockito.mock(PrivateKey.class);
    private final PublicKey publicKey = Mockito.mock(PublicKey.class);
    private final KeyPair keyPair1 = Mockito.mock(KeyPair.class);

    private KeyPairContext() {
      Mockito.when(this.analyzer.isKeyCompressed(Mockito.any())).thenReturn(true);
      Mockito.when(this.engine.createKeyAnalyzer()).thenReturn(this.analyzer);
      Mockito.when(this.engine.createKeyGenerator()).thenReturn(this.generator);
      Mockito.when(this.generator.generateKeyPair()).thenReturn(this.keyPair1);
      Mockito.when(this.generator.derivePublicKey(this.privateKey)).thenReturn(this.publicKey);
    }
  }

  // endregion
}
