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

import io.nem.symbol.core.utils.ExceptionUtils;
import java.security.MessageDigest;
import java.security.Security;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/** Static class that exposes hash functions. */
public class Hashes {

  /** The provider. */
  private static final String BC = "BC";

  /** The SHA256 algorithm. */
  private static final String SHA_256 = "SHA256";

  /** The SHA512 algorithm. */
  private static final String SHA_512 = "SHA512";

  /** The RIPEMD_160 algorithm. */
  private static final String RIPEMD_160 = "RIPEMD160";

  /** The SHA3-256 algorithm. */
  private static final String SHA_3_256 = "SHA3-256";

  /** The SHA3-512 algorithm. */
  private static final String SHA_3_512 = "SHA3-512";
  /** The KECCAK-256 algorithm. */
  private static final String KECCAK_256 = "KECCAK-256";
  /** The KECCAK-512 algorithm. */
  private static final String KECCAK_512 = "KECCAK-512";

  /** Private constructor for this utility class. */
  private Hashes() {}

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Performs a SHA_3_256 hash of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  @SuppressWarnings("squid:S00100")
  public static byte[] sha3_256(final byte[]... inputs) {
    return hash(SHA_3_256, inputs);
  }

  /**
   * Performs a SHA_3_512 hash of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  @SuppressWarnings("squid:S00100")
  public static byte[] sha3_512(final byte[]... inputs) {
    return hash(SHA_3_512, inputs);
  }

  /**
   * Performs a RIPEMD_160 hash of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  public static byte[] ripemd160(final byte[]... inputs) {
    return hash(RIPEMD_160, inputs);
  }

  /**
   * Performs a KECCAK_256 hash of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  public static byte[] keccak256(final byte[]... inputs) {
    return hash(KECCAK_256, inputs);
  }

  /**
   * Performs a KECCAK_512 hash of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  public static byte[] keccak512(final byte[]... inputs) {
    return hash(KECCAK_512, inputs);
  }

  /**
   * Performs a SHA_512 hash of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  public static byte[] sha512(final byte[]... inputs) {
    return hash(SHA_512, inputs);
  }

  /**
   * Performs a SHA_256 hash of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  public static byte[] hash256(final byte[]... inputs) {
    byte[] hashedSha256 = hash(SHA_256, inputs);
    return hash(SHA_256, hashedSha256);
  }

  /**
   * Performs a RIPEMD_160 hash of SHA_256 of the concatenated inputs.
   *
   * @param inputs The byte arrays to concatenate and hash.
   * @return The hash of the concatenated inputs.
   * @throws CryptoException if the hash operation failed.
   */
  public static byte[] hash160(final byte[]... inputs) {
    byte[] hashedSha256 = hash(SHA_256, inputs);
    return hash(RIPEMD_160, hashedSha256);
  }

  private static byte[] hash(final String algorithm, final byte[]... inputs) {
    return ExceptionUtils.propagate(
        () -> {
          final MessageDigest digest = MessageDigest.getInstance(algorithm, BC);

          for (final byte[] input : inputs) {
            digest.update(input);
          }

          return digest.digest();
        },
        e -> new CryptoException(e.getMessage(), e));
  }

  /**
   * Hasher used for shared keys
   *
   * @param sharedSecret the shared secret
   * @return the shared key hash.
   */
  public static byte[] sha256ForSharedKey(byte[] sharedSecret) {
    Digest hash = new SHA256Digest();
    byte[] info = "catapult".getBytes();
    int length = 32;
    byte[] sharedKey = new byte[length];
    HKDFParameters params = new HKDFParameters(sharedSecret, null, info);
    HKDFBytesGenerator hkdf = new HKDFBytesGenerator(hash);
    hkdf.init(params);
    hkdf.generateBytes(sharedKey, 0, length);
    return sharedKey;
  }
}
