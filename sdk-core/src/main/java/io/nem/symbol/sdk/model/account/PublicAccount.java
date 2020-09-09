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
package io.nem.symbol.sdk.model.account;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Objects;

/**
 * The public account structure contains account's address and public key.
 *
 * @since 1.0
 */
public class PublicAccount {

  private final Address address;
  private final PublicKey publicKey;

  public PublicAccount(String publicKey, NetworkType networkType) {
    this.address = Address.createFromPublicKey(publicKey, networkType);
    this.publicKey = PublicKey.fromHexString(publicKey);
  }

  /**
   * Create a PublicAccount from a public key and network type.
   *
   * @param publicKey Public key
   * @param networkType NetworkType
   * @return {@link PublicAccount}
   */
  public static PublicAccount createFromPublicKey(String publicKey, NetworkType networkType) {
    return new PublicAccount(publicKey, networkType);
  }

  /**
   * Compares public accounts for equality.
   *
   * @param o PublicAccount
   * @return boolean
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PublicAccount)) {
      return false;
    }
    PublicAccount that = (PublicAccount) o;
    return Objects.equals(address, that.address) && Objects.equals(publicKey, that.publicKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, publicKey);
  }

  /**
   * Returns account address.
   *
   * @return {@link Address}
   */
  public Address getAddress() {
    return address;
  }

  /**
   * Return account public key.
   *
   * @return PublicKey
   */
  public PublicKey getPublicKey() {
    return publicKey;
  }
}
