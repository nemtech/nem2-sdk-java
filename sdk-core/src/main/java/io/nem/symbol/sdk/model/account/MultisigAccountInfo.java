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

import java.util.List;

/**
 * The multisig account info structure describes information of a multisig account.
 *
 * @since 1.0
 */
public class MultisigAccountInfo {

  private final Address accountAddress;
  private final long minApproval;
  private final long minRemoval;
  private final List<Address> cosignatoryAddresses;
  private final List<Address> multisigAddresses;

  public MultisigAccountInfo(
      Address accountAddress,
      long minApproval,
      long minRemoval,
      List<Address> cosignatories,
      List<Address> multisigAddresses) {
    this.accountAddress = accountAddress;
    this.minApproval = minApproval;
    this.minRemoval = minRemoval;
    this.cosignatoryAddresses = cosignatories;
    this.multisigAddresses = multisigAddresses;
  }

  /**
   * Returns account multisig public account.
   *
   * @return PublicAccount
   */
  public Address getAccountAddress() {
    return accountAddress;
  }

  /**
   * Returns number of signatures needed to approve a transaction.
   *
   * @return int
   */
  public long getMinApproval() {
    return minApproval;
  }

  /**
   * Returns number of signatures needed to remove a cosignatory.
   *
   * @return int
   */
  public long getMinRemoval() {
    return minRemoval;
  }

  /**
   * Returns multisig account cosignatories.
   *
   * @return List of {@link PublicAccount}
   */
  public List<Address> getCosignatoryAddresses() {
    return cosignatoryAddresses;
  }

  /**
   * Returns multisig accounts this account is cosigner of.
   *
   * @return List of {@link PublicAccount}
   */
  public List<Address> getMultisigAddresses() {
    return multisigAddresses;
  }

  /**
   * Checks if an account is cosignatory of the multisig account.
   *
   * @param account PublicAccount
   * @return boolean
   */
  public boolean hasCosigner(Address account) {
    return this.cosignatoryAddresses.contains(account);
  }

  /**
   * Checks if the multisig account is cosignatory of an account.
   *
   * @param account PublicAccount
   * @return boolean
   */
  public boolean isCosignerOfMultisigAccount(Address account) {
    return this.multisigAddresses.contains(account);
  }

  /**
   * Checks if the account is a multisig account.
   *
   * @return boolean
   */
  public boolean isMultisig() {
    return minApproval != 0 && minRemoval != 0;
  }
}
