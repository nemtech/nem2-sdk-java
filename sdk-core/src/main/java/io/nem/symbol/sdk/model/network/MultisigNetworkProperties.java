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
package io.nem.symbol.sdk.model.network;
/** MultisigNetworkProperties */
public class MultisigNetworkProperties {

  /** Maximum number of multisig levels. */
  private final String maxMultisigDepth;

  /** Maximum number of cosignatories per account. */
  private final String maxCosignatoriesPerAccount;
  /** Maximum number of accounts a single account can cosign. */
  private final String maxCosignedAccountsPerAccount;

  public MultisigNetworkProperties(
      String maxMultisigDepth,
      String maxCosignatoriesPerAccount,
      String maxCosignedAccountsPerAccount) {
    this.maxMultisigDepth = maxMultisigDepth;
    this.maxCosignatoriesPerAccount = maxCosignatoriesPerAccount;
    this.maxCosignedAccountsPerAccount = maxCosignedAccountsPerAccount;
  }

  public String getMaxMultisigDepth() {
    return maxMultisigDepth;
  }

  public String getMaxCosignatoriesPerAccount() {
    return maxCosignatoriesPerAccount;
  }

  public String getMaxCosignedAccountsPerAccount() {
    return maxCosignedAccountsPerAccount;
  }
}
