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

import io.nem.symbol.core.crypto.CryptoException;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/** AES/GSM/NoPadding encryption and decryption methods. Uses the BouncyCastle.org provider. */
public class AESGCM {

  /** The standard Initialisation Vector (IV) length (96 bits). */
  public static final int IV_BIT_LENGTH = 96;

  /** The standard authentication tag length (128 bits). */
  public static final int AUTH_TAG_BIT_LENGTH = 128;

  /** The standard Initialisation Vector (IV) length (12 bytes). */
  public static final int IV_LENGTH = IV_BIT_LENGTH / 8;

  /** The standard authentication tag length (16 bytes). */
  public static final int TAG_LENGTH = AUTH_TAG_BIT_LENGTH / 8;

  /**
   * Generates a random 96 bit (12 byte) Initialisation Vector(IV) for use in AES-GCM encryption.
   *
   * <p>See draft-ietf-jose-json-web-algorithms-08, section-4.9.
   *
   * @return The random 96 bit IV, as 12 byte array.
   */
  public static byte[] generateIV() {
    return RandomUtils.generateRandomBytes(IV_LENGTH);
  }

  /**
   * Creates a new AES/GCM/NoPadding cipher.
   *
   * @param secretKey The AES key. Must not be {@code null}.
   * @param forEncryption If {@code true} creates an encryption cipher, else creates a decryption
   *     cipher.
   * @param iv The initialisation vector (IV). Must not be {@code null}.
   * @return The AES/GCM/NoPadding cipher.
   */
  private static GCMBlockCipher createAESGCMCipher(
      final byte[] secretKey, final boolean forEncryption, final byte[] iv) {

    // Initialise AES cipher
    BlockCipher cipher = createCipher(secretKey, forEncryption);

    // Create GCM cipher with AES
    GCMBlockCipher gcm = new GCMBlockCipher(cipher);

    final KeyParameter keyParam = new KeyParameter(secretKey);
    final CipherParameters params = new ParametersWithIV(keyParam, iv);
    gcm.init(forEncryption, params);
    return gcm;
  }

  /**
   * Creates a new AES cipher.
   *
   * @param secretKey The AES key. Must not be {@code null}.
   * @param forEncryption If {@code true} creates an AES encryption cipher, else creates an AES
   *     decryption cipher.
   * @return The AES cipher.
   */
  private static AESEngine createCipher(final byte[] secretKey, final boolean forEncryption) {
    AESEngine cipher = new AESEngine();
    CipherParameters cipherParams = new KeyParameter(secretKey);
    cipher.init(forEncryption, cipherParams);
    return cipher;
  }

  /**
   * Encrypts the specified plain text using AES/GCM/NoPadding.
   *
   * @param secretKey The AES key. Must not be {@code null}.
   * @param plainText The plain text. Must not be {@code null}.
   * @param iv The initialisation vector (IV). Must not be {@code null}.
   * @return The authenticated cipher text.
   * @throws CryptoException If encryption failed.
   */
  public static AuthenticatedCipherText encrypt(
      final byte[] secretKey, final byte[] iv, final byte[] plainText) throws RuntimeException {

    // Initialise AES/GCM cipher for encryption
    GCMBlockCipher cipher = createAESGCMCipher(secretKey, true, iv);

    // Prepare output buffer
    int outputLength = cipher.getOutputSize(plainText.length);
    byte[] output = new byte[outputLength];

    // Produce cipher text
    int outputOffset = cipher.processBytes(plainText, 0, plainText.length, output, 0);

    // Produce authentication tag
    try {
      outputOffset += cipher.doFinal(output, outputOffset);
    } catch (InvalidCipherTextException e) {
      throw new CryptoException(
          "Could Not Generate GCM Authentication: " + ExceptionUtils.getMessage(e), e);
    }

    // Split output into cipher text and authentication tag
    int authTagLength = AUTH_TAG_BIT_LENGTH / 8;

    byte[] cipherText = new byte[outputOffset - authTagLength];
    byte[] authTag = new byte[authTagLength];

    System.arraycopy(output, 0, cipherText, 0, cipherText.length);
    System.arraycopy(output, outputOffset - authTagLength, authTag, 0, authTag.length);

    return new AuthenticatedCipherText(cipherText, authTag, iv);
  }

  /**
   * Decrypts the specified cipher text using AES/GCM/NoPadding.
   *
   * @param secretKey The AES key. Must not be {@code null}.
   * @param iv The initialisation vector (IV). Must not be {@code null}.
   * @param cipherText The cipher text. Must not be {@code null}.
   * @param authTag The authentication tag. Must not be {@code null}.
   * @return The decrypted plain text.
   * @throws CryptoException If decryption failed.
   */
  public static byte[] decrypt(
      final byte[] secretKey, final byte[] iv, final byte[] cipherText, final byte[] authTag)
      throws RuntimeException {

    // Initialise AES/GCM cipher for decryption
    GCMBlockCipher cipher = createAESGCMCipher(secretKey, false, iv);

    // Join cipher text and authentication tag to produce cipher input
    byte[] input = new byte[cipherText.length + authTag.length];

    System.arraycopy(cipherText, 0, input, 0, cipherText.length);
    System.arraycopy(authTag, 0, input, cipherText.length, authTag.length);

    int outputLength = cipher.getOutputSize(input.length);

    byte[] output = new byte[outputLength];

    // Decrypt
    int outputOffset = cipher.processBytes(input, 0, input.length, output, 0);

    // Validate authentication tag
    try {
      outputOffset += cipher.doFinal(output, outputOffset);
    } catch (InvalidCipherTextException e) {
      throw new CryptoException("Could decrypt value: " + ExceptionUtils.getMessage(e), e);
    }

    return output;
  }

  /** Prevents public instantiation. */
  private AESGCM() {}
}
