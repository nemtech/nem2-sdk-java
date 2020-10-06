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
package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.AccountAddressRestrictionTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionFlagsEnum;
import java.util.List;
import java.util.stream.Collectors;

/** DTO mapper of {@link AccountAddressRestrictionTransaction}. */
public class AccountAddressRestrictionTransactionMapper
    extends AbstractTransactionMapper<
        AccountAddressRestrictionTransactionDTO, AccountAddressRestrictionTransaction> {

  public AccountAddressRestrictionTransactionMapper(JsonHelper jsonHelper) {
    super(
        jsonHelper,
        TransactionType.ACCOUNT_ADDRESS_RESTRICTION,
        AccountAddressRestrictionTransactionDTO.class);
  }

  @Override
  protected AccountAddressRestrictionTransactionFactory createFactory(
      NetworkType networkType,
      Deadline deadline,
      AccountAddressRestrictionTransactionDTO transaction) {
    AccountAddressRestrictionFlags restrictionFlags =
        AccountAddressRestrictionFlags.rawValueOf(transaction.getRestrictionFlags().getValue());
    List<UnresolvedAddress> restrictionAdditions =
        transaction.getRestrictionAdditions().stream()
            .map(MapperUtils::toUnresolvedAddress)
            .collect(Collectors.toList());

    List<UnresolvedAddress> restrictionDeletions =
        transaction.getRestrictionDeletions().stream()
            .map(MapperUtils::toUnresolvedAddress)
            .collect(Collectors.toList());

    return AccountAddressRestrictionTransactionFactory.create(
        networkType, deadline, restrictionFlags, restrictionAdditions, restrictionDeletions);
  }

  @Override
  protected void copyToDto(
      AccountAddressRestrictionTransaction transaction,
      AccountAddressRestrictionTransactionDTO dto) {
    dto.setRestrictionFlags(
        AccountRestrictionFlagsEnum.fromValue(transaction.getRestrictionFlags().getValue()));

    dto.setRestrictionAdditions(
        transaction.getRestrictionAdditions().stream()
            .map(r -> r.encoded(transaction.getNetworkType()))
            .collect(Collectors.toList()));

    dto.setRestrictionDeletions(
        transaction.getRestrictionDeletions().stream()
            .map(r -> r.encoded(transaction.getNetworkType()))
            .collect(Collectors.toList()));
  }
}
