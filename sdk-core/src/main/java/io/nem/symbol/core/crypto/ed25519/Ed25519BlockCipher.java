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

import static io.nem.symbol.core.crypto.ed25519.AESGCM.IV_LENGTH;
import static io.nem.symbol.core.crypto.ed25519.AESGCM.TAG_LENGTH;

import io.nem.symbol.core.crypto.BlockCipher;
import io.nem.symbol.core.crypto.CryptoException;
import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.ed25519.arithmetic.Ed25519EncodedGroupElement;
import io.nem.symbol.core.crypto.ed25519.arithmetic.Ed25519GroupElement;
import io.nem.symbol.core.utils.ArrayUtils;
import java.util.Arrays;

/** Implementation of the block cipher for Ed25519. */
public class Ed25519BlockCipher implements BlockCipher {

  private final KeyPair senderKeyPair;

  private final KeyPair recipientKeyPair;

  public Ed25519BlockCipher(final KeyPair senderKeyPair, final KeyPair recipientKeyPair) {
    this.senderKeyPair = senderKeyPair;
    this.recipientKeyPair = recipientKeyPair;
  }

  @Override
  public byte[] encrypt(final byte[] plainText) {
    return encrypt(plainText, AESGCM.generateIV());
  }

  public byte[] encrypt(final byte[] plainText, final byte[] ivData) {
    AuthenticatedCipherText encryptResult = encode(plainText, ivData);
    byte[] authTag = encryptResult.getAuthenticationTag();
    byte[] cipherText = encryptResult.getCipherText();
    return ArrayUtils.concat(authTag, encryptResult.getIv(), cipherText);
  }

  public AuthenticatedCipherText encode(final byte[] plainText, final byte[] ivData) {
    // Derive shared key.
    final byte[] sharedKey =
        getSharedKey(this.senderKeyPair.getPrivateKey(), this.recipientKeyPair.getPublicKey());
    return AESGCM.encrypt(sharedKey, ivData, plainText);
  }

  @Override
  public byte[] decrypt(final byte[] input) {
    if (input == null) {
      throw new CryptoException("Cannot decrypt. Input is required.");
    }
    int minSize = TAG_LENGTH + IV_LENGTH;
    if (input.length < minSize) {
      throw new CryptoException(
          "Cannot decrypt input. Size is "
              + input.length
              + " when at least "
              + minSize
              + " is expected.");
    }
    final byte[] authTag = Arrays.copyOfRange(input, 0, TAG_LENGTH);
    final byte[] ivData = Arrays.copyOfRange(input, TAG_LENGTH, IV_LENGTH + TAG_LENGTH);
    final byte[] cypherText = Arrays.copyOfRange(input, TAG_LENGTH + IV_LENGTH, input.length);
    return decode(authTag, ivData, cypherText);
  }

  public byte[] decode(byte[] authTag, byte[] ivData, byte[] cypherText) {
    final byte[] sharedKey =
        getSharedKey(this.recipientKeyPair.getPrivateKey(), this.senderKeyPair.getPublicKey());
    return AESGCM.decrypt(sharedKey, ivData, cypherText, authTag);
  }

  public static byte[] getSharedKey(final PrivateKey privateKey, final PublicKey publicKey) {
    return Hashes.sha256ForSharedKey(getSharedSecret(privateKey, publicKey));
  }

  public static byte[] getSharedSecret(final PrivateKey privateKey, final PublicKey publicKey) {
    final Ed25519GroupElement senderA =
        new Ed25519EncodedGroupElement(publicKey.getBytes()).decode();
    senderA.precomputeForScalarMultiplication();
    return senderA
        .scalarMultiply(Ed25519Utils.prepareForScalarMultiply(privateKey))
        .encode()
        .getRaw();
  }
}
