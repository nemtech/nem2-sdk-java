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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class BlockCipherTest {

  @Test
  void encryptedDataCanBeDecrypted() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair kp = KeyPair.random(engine);
    final BlockCipher blockCipher = this.getBlockCipher(kp, kp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final byte[] encryptedBytes = blockCipher.encrypt(input);
    final byte[] decryptedBytes = blockCipher.decrypt(encryptedBytes);

    // Assert:
    MatcherAssert.assertThat(encryptedBytes, IsNot.not(IsEqual.equalTo(decryptedBytes)));
    MatcherAssert.assertThat(decryptedBytes, IsEqual.equalTo(input));
  }

  @Test
  void dataCanBeEncryptedWithSenderPrivateKeyAndRecipientPublicKey() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair skp = KeyPair.random(engine);
    final KeyPair rkp = KeyPair.random(engine);
    final BlockCipher blockCipher =
        this.getBlockCipher(skp, KeyPair.onlyPublic(rkp.getPublicKey(), engine));
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final byte[] encryptedBytes = blockCipher.encrypt(input);

    // Assert:
    MatcherAssert.assertThat(encryptedBytes, IsNot.not(IsEqual.equalTo(input)));
  }

  @Test
  void dataCanBeDecryptedWithSenderPublicKeyAndRecipientPrivateKey() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair skp = KeyPair.random(engine);
    final KeyPair rkp = KeyPair.random(engine);
    final BlockCipher blockCipher1 =
        this.getBlockCipher(skp, KeyPair.onlyPublic(rkp.getPublicKey(), engine));
    final BlockCipher blockCipher2 =
        this.getBlockCipher(KeyPair.onlyPublic(skp.getPublicKey(), engine), rkp);
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final byte[] encryptedBytes = blockCipher1.encrypt(input);
    final byte[] decryptedBytes = blockCipher2.decrypt(encryptedBytes);

    // Assert:
    Assertions.assertEquals(ConvertUtils.toHex(decryptedBytes), ConvertUtils.toHex(input));
  }

  @Test
  void dataCanBeDecryptedWithSenderPrivateKeyAndRecipientPublicKey() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair skp = KeyPair.random(engine);
    final KeyPair rkp = KeyPair.random(engine);
    final BlockCipher blockCipher1 =
        this.getBlockCipher(skp, KeyPair.onlyPublic(rkp.getPublicKey(), engine));
    final BlockCipher blockCipher2 =
        this.getBlockCipher(KeyPair.onlyPublic(rkp.getPublicKey(), engine), skp);
    final byte[] input = "Some text".getBytes();

    // Act:
    final byte[] encryptedBytes = blockCipher1.encrypt(input);
    final byte[] decryptedBytes = blockCipher2.decrypt(encryptedBytes);

    // Assert:
    MatcherAssert.assertThat(
        ConvertUtils.toHex(decryptedBytes), IsEqual.equalTo(ConvertUtils.toHex(input)));
  }

  @Test
  void dataCanBeDecryptedWhenSmallest() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final KeyPair skp = KeyPair.random(engine);
    final KeyPair rkp = KeyPair.random(engine);
    final BlockCipher blockCipher1 =
        this.getBlockCipher(skp, KeyPair.onlyPublic(rkp.getPublicKey(), engine));
    final BlockCipher blockCipher2 =
        this.getBlockCipher(KeyPair.onlyPublic(rkp.getPublicKey(), engine), skp);
    final byte[] input = new byte[0];

    // Act:
    final byte[] encryptedBytes = blockCipher1.encrypt(input);
    final byte[] decryptedBytes = blockCipher2.decrypt(encryptedBytes);

    // Assert:
    MatcherAssert.assertThat(
        ConvertUtils.toHex(decryptedBytes), IsEqual.equalTo(ConvertUtils.toHex(input)));
  }

  @Test
  void dataEncryptedWithPrivateKeyCanOnlyBeDecryptedByMatchingPublicKey() {
    // Arrange:
    final CryptoEngine engine = this.getCryptoEngine();
    final BlockCipher blockCipher1 =
        this.getBlockCipher(KeyPair.random(engine), KeyPair.random(engine));
    final BlockCipher blockCipher2 =
        this.getBlockCipher(KeyPair.random(engine), KeyPair.random(engine));
    final byte[] input = RandomUtils.generateRandomBytes();

    // Act:
    final byte[] encryptedBytes1 = blockCipher1.encrypt(input);
    final byte[] encryptedBytes2 = blockCipher2.encrypt(input);

    // Assert:
    MatcherAssert.assertThat(blockCipher1.decrypt(encryptedBytes1), IsEqual.equalTo(input));
    MatcherAssert.assertThat(blockCipher2.decrypt(encryptedBytes2), IsEqual.equalTo(input));

    CryptoException e1 =
        Assertions.assertThrows(CryptoException.class, () -> blockCipher2.decrypt(encryptedBytes1));
    Assertions.assertEquals(
        "Could decrypt value: InvalidCipherTextException: mac check in GCM failed",
        e1.getMessage());

    CryptoException e2 =
        Assertions.assertThrows(CryptoException.class, () -> blockCipher1.decrypt(encryptedBytes2));
    Assertions.assertEquals(
        "Could decrypt value: InvalidCipherTextException: mac check in GCM failed",
        e2.getMessage());
  }

  protected BlockCipher getBlockCipher(
      final KeyPair senderKeyPair, final KeyPair recipientKeyPair) {
    return this.getCryptoEngine().createBlockCipher(senderKeyPair, recipientKeyPair);
  }

  protected abstract CryptoEngine getCryptoEngine();
}
