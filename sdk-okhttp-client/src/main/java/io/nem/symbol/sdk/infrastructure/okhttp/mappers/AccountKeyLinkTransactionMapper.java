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

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountKeyLinkTransactionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.LinkActionEnum;

/** Account link transaction mapper. */
class AccountKeyLinkTransactionMapper
    extends AbstractTransactionMapper<AccountKeyLinkTransactionDTO, AccountKeyLinkTransaction> {

  public AccountKeyLinkTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.ACCOUNT_KEY_LINK, AccountKeyLinkTransactionDTO.class);
  }

  @Override
  protected AccountKeyLinkTransactionFactory createFactory(
      NetworkType networkType, Deadline deadline, AccountKeyLinkTransactionDTO dto) {
    PublicKey linkedPublicKey = PublicKey.fromHexString(dto.getLinkedPublicKey());
    return AccountKeyLinkTransactionFactory.create(
        networkType,
        deadline,
        linkedPublicKey,
        LinkAction.rawValueOf(dto.getLinkAction().getValue()));
  }

  @Override
  protected void copyToDto(
      AccountKeyLinkTransaction transaction, AccountKeyLinkTransactionDTO dto) {
    dto.setLinkedPublicKey(transaction.getLinkedPublicKey().toHex());
    dto.setLinkAction(LinkActionEnum.fromValue((int) transaction.getLinkAction().getValue()));
  }
}
