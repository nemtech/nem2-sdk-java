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

import io.nem.symbol.sdk.model.namespace.NamespaceName;
import java.util.List;

/**
 * The friendly names of one account. The names are namespaces linked using address aliases.
 *
 * @author Fernando Boucquez
 */
public class AccountNames {

  /** The address of the account. */
  private final Address address;

  /** The names. */
  private final List<NamespaceName> names;

  /**
   * @param address the account address
   * @param names the names.
   */
  public AccountNames(Address address, List<NamespaceName> names) {
    this.address = address;
    this.names = names;
  }

  public Address getAddress() {
    return address;
  }

  public List<NamespaceName> getNames() {
    return names;
  }
}
