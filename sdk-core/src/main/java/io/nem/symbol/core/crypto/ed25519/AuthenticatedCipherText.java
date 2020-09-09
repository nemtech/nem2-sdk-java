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

import org.apache.commons.lang3.Validate;

/** Authenticated cipher text. This class is immutable. */
final class AuthenticatedCipherText {

  /** The cipher text. */
  private final byte[] cipherText;

  /** The authentication tag. */
  private final byte[] authenticationTag;

  /** The Initialisation Vector (IV) . */
  private final byte[] iv;

  /**
   * Creates a new authenticated cipher text.
   *
   * @param cipherText The cipher text. Must not be {@code null}.
   * @param authenticationTag The authentication tag. Must not be {@code null}.
   * @param iv The Initialisation Vector (IV). Must not be {@code null}.
   */
  AuthenticatedCipherText(
      final byte[] cipherText, final byte[] authenticationTag, final byte[] iv) {
    Validate.notNull(cipherText, "cipherText is required");
    Validate.notNull(authenticationTag, "authenticationTag is required");
    Validate.notNull(iv, "iv is required");
    this.cipherText = cipherText;
    this.authenticationTag = authenticationTag;
    this.iv = iv;
  }

  /**
   * Gets the cipher text.
   *
   * @return The cipher text.
   */
  public byte[] getCipherText() {
    return cipherText;
  }

  /**
   * Gets the authentication tag.
   *
   * @return The authentication tag.
   */
  public byte[] getAuthenticationTag() {
    return authenticationTag;
  }

  /**
   * Gets the Initialisation Vector (IV)
   *
   * @return The Initialisation Vector (IV) .
   */
  public byte[] getIv() {
    return iv;
  }
}
