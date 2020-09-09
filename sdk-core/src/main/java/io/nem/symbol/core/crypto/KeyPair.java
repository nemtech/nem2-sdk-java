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

import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * It represents an asymmetric private/public encryption key. Public key is always present. Private
 * key may not be present. If the private key is not preset, the key cannot be used to decrypt
 * values.
 */
public class KeyPair {

  private final Optional<PrivateKey> privateKey;

  private final PublicKey publicKey;

  private KeyPair(
      final Optional<PrivateKey> privateKey, final PublicKey publicKey, CryptoEngine engine) {
    Validate.notNull(privateKey, "Optional PrivateKey must not be null");
    Validate.notNull(publicKey, "PublicKey must not be null");

    if (!engine.createKeyAnalyzer().isKeyCompressed(publicKey)) {
      throw new IllegalArgumentException("PublicKey must be in compressed form");
    }
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  /**
   * Creates a random key pair using the default engine.
   *
   * @return a {@link KeyPair} with both public and private keys.
   */
  public static KeyPair random() {
    return CryptoEngines.defaultEngine().createKeyGenerator().generateKeyPair();
  }

  /**
   * Creates a random key pair using the provided engine.
   *
   * @param engine the engine.
   * @return a {@link KeyPair} with both public and private keys.
   */
  public static KeyPair random(CryptoEngine engine) {
    return engine.createKeyGenerator().generateKeyPair();
  }

  /**
   * Creates a key pair around a private key. The public key is calculated from the private key.
   *
   * @param privateKey The private key.
   * @param engine the engine.
   * @return a {@link KeyPair} with both public and private keys.
   */
  public static KeyPair fromPrivate(final PrivateKey privateKey, final CryptoEngine engine) {
    PublicKey publicKey = engine.createKeyGenerator().derivePublicKey(privateKey);
    return new KeyPair(Optional.of(privateKey), publicKey, engine);
  }

  /**
   * Creates a pair that only holds a public key using the default engine. It is good to encrypt but
   * cannot decrypt values.
   *
   * @param publicKey The public key.
   * @return a {@link KeyPair} with just the public key.
   */
  public static KeyPair onlyPublic(final PublicKey publicKey) {
    return new KeyPair(Optional.empty(), publicKey, CryptoEngines.defaultEngine());
  }

  /**
   * Creates a pair that only holds a public key. It is good to encrypt but cannot decrypt values.
   *
   * @param publicKey The public key.
   * @param engine the engine.
   * @return a {@link KeyPair} with just the public key.
   */
  public static KeyPair onlyPublic(final PublicKey publicKey, final CryptoEngine engine) {
    return new KeyPair(Optional.empty(), publicKey, engine);
  }

  /**
   * Creates a key pair around a private key using the default engine. The public key is calculated
   * from the private key.
   *
   * @param privateKey The private key.
   * @return a {@link KeyPair} with both public and private keys.
   */
  public static KeyPair fromPrivate(final PrivateKey privateKey) {
    return fromPrivate(privateKey, CryptoEngines.defaultEngine());
  }

  /**
   * Gets the private key.
   *
   * @return The private key raising an {@link IllegalStateException} if the private key hasn't been
   *     set.
   */
  public PrivateKey getPrivateKey() {
    return this.privateKey.orElseThrow(
        () -> new IllegalStateException("Private Key hasn't been provided."));
  }

  /**
   * Gets the public key.
   *
   * @return the public key. Never null.
   */
  public PublicKey getPublicKey() {
    return this.publicKey;
  }

  /**
   * Determines if the current key pair has a private key.
   *
   * @return true if the current key pair has a private key.
   */
  public boolean hasPrivateKey() {
    return privateKey.isPresent();
  }
}
