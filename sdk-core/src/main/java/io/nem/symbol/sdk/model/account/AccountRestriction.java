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

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.List;

/** It defines one account restriction. */
public class AccountRestriction {

  /** The restriction type. */
  private final AccountRestrictionFlags restrictionFlags;

  /**
   * The list of model objects referencing the restricted value. It can be a {@link MosaicId}, an
   * {@link Address} or a {@link TransactionType} depending on the target of the restrictionFlags
   */
  private final List<Object> values;

  public AccountRestriction(AccountRestrictionFlags restrictionFlags, List<Object> values) {
    this.restrictionFlags = restrictionFlags;
    this.values = values;
  }

  public AccountRestrictionFlags getRestrictionFlags() {
    return restrictionFlags;
  }

  public List<Object> getValues() {
    return values;
  }
}
