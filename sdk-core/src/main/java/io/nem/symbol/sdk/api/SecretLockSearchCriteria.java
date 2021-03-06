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
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import java.util.Objects;

/** Criteria used to search secret lock entities. */
public class SecretLockSearchCriteria extends SearchCriteria<SecretLockSearchCriteria> {

  /** Account address. */
  private Address address;
  /** filter by secret */
  private String secret;

  public void setAddress(Address address) {
    this.address = address;
  }

  public SecretLockSearchCriteria address(Address address) {
    this.address = address;
    return this;
  }

  public Address getAddress() {
    return address;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public SecretLockSearchCriteria secret(String secret) {
    this.secret = secret;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    SecretLockSearchCriteria that = (SecretLockSearchCriteria) o;
    return Objects.equals(address, that.address) && Objects.equals(secret, that.secret);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), address, secret);
  }
}
