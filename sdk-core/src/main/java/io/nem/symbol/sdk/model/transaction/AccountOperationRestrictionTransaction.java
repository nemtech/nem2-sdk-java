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
package io.nem.symbol.sdk.model.transaction;

import java.util.List;

public class AccountOperationRestrictionTransaction extends Transaction {

  private final AccountOperationRestrictionFlags restrictionFlags;
  private final List<TransactionType> restrictionAdditions;
  private final List<TransactionType> restrictionDeletions;

  AccountOperationRestrictionTransaction(AccountOperationRestrictionTransactionFactory factory) {
    super(factory);
    this.restrictionFlags = factory.getRestrictionFlags();
    this.restrictionAdditions = factory.getRestrictionAdditions();
    this.restrictionDeletions = factory.getRestrictionDeletions();
  }

  /**
   * Get account restriction flags
   *
   * @return {@link AccountOperationRestrictionFlags}
   */
  public AccountOperationRestrictionFlags getRestrictionFlags() {
    return this.restrictionFlags;
  }

  /** @return List of transaction types that are going to be added to the restriction. */
  public List<TransactionType> getRestrictionAdditions() {
    return restrictionAdditions;
  }

  /** @return List of transaction types that are going to be removed from the restriction. */
  public List<TransactionType> getRestrictionDeletions() {
    return restrictionDeletions;
  }
}
