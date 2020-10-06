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
package io.nem.symbol.sdk.infrastructure.okhttp.mappers;

import static io.nem.symbol.core.utils.MapperUtils.getIdAsHex;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransaction;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AddressAliasTransactionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AliasActionEnum;

/** Account alias transaction mapper. */
class AddressAliasTransactionMapper
    extends AbstractTransactionMapper<AddressAliasTransactionDTO, AddressAliasTransaction> {

  public AddressAliasTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.ADDRESS_ALIAS, AddressAliasTransactionDTO.class);
  }

  @Override
  protected AddressAliasTransactionFactory createFactory(
      NetworkType networkType, Deadline deadline, AddressAliasTransactionDTO transaction) {
    NamespaceId namespaceId = MapperUtils.toNamespaceId(transaction.getNamespaceId());
    AliasAction aliasAction =
        AliasAction.rawValueOf(transaction.getAliasAction().getValue().byteValue());
    return AddressAliasTransactionFactory.create(
        networkType,
        deadline,
        aliasAction,
        namespaceId,
        MapperUtils.toAddress(transaction.getAddress()));
  }

  @Override
  protected void copyToDto(AddressAliasTransaction transaction, AddressAliasTransactionDTO dto) {
    dto.setAddress(transaction.getAddress().encoded(transaction.getNetworkType()));
    dto.setNamespaceId(getIdAsHex(transaction.getNamespaceId()));
    dto.setAliasAction(AliasActionEnum.fromValue((int) transaction.getAliasAction().getValue()));
  }
}
