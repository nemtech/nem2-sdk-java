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
import io.nem.symbol.sdk.model.namespace.AliasType;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import java.util.Objects;

/**
 * Defines the params used to search namespaces. With this criteria, you can sort and filter
 * namespaces queries using rest.
 */
public class NamespaceSearchCriteria extends SearchCriteria<NamespaceSearchCriteria> {

  /** Namespace identifier up to which transactions are returned. (optional) */
  private String id;

  /** The filter by owner address. (optional) */
  private Address ownerAddress;

  /** Filter by registration type. (optional) */
  private NamespaceRegistrationType registrationType;

  /** Filter by root namespace id. (optional) */
  private String level0;

  /** Filter by alias type. (optional) */
  private AliasType aliasType;

  public String getId() {
    return id;
  }

  public Address getOwnerAddress() {
    return ownerAddress;
  }

  public NamespaceRegistrationType getRegistrationType() {
    return registrationType;
  }

  public String getLevel0() {
    return level0;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AliasType getAliasType() {
    return aliasType;
  }

  public void setAliasType(AliasType aliasType) {
    this.aliasType = aliasType;
  }

  public void setLevel0(String level0) {
    this.level0 = level0;
  }

  public void setRegistrationType(NamespaceRegistrationType registrationType) {
    this.registrationType = registrationType;
  }

  public void setOwnerAddress(Address ownerAddress) {
    this.ownerAddress = ownerAddress;
  }

  public NamespaceSearchCriteria id(String id) {
    this.id = id;
    return this;
  }

  public NamespaceSearchCriteria aliasType(AliasType aliasType) {
    this.aliasType = aliasType;
    return this;
  }

  public NamespaceSearchCriteria level0(String level0) {
    this.level0 = level0;
    return this;
  }

  public NamespaceSearchCriteria registrationType(NamespaceRegistrationType registrationType) {
    this.registrationType = registrationType;
    return this;
  }

  public NamespaceSearchCriteria ownerAddress(Address ownerAddress) {
    this.ownerAddress = ownerAddress;
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
    NamespaceSearchCriteria that = (NamespaceSearchCriteria) o;
    return Objects.equals(id, that.id)
        && Objects.equals(ownerAddress, that.ownerAddress)
        && registrationType == that.registrationType
        && Objects.equals(level0, that.level0)
        && aliasType == that.aliasType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, ownerAddress, registrationType, level0, aliasType);
  }
}
