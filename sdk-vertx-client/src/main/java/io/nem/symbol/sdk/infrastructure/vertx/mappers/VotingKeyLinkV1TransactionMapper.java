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

import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkV1Transaction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkV1TransactionFactory;
import io.nem.symbol.sdk.openapi.vertx.model.LinkActionEnum;
import io.nem.symbol.sdk.openapi.vertx.model.VotingKeyLinkTransactionDTO;

/** {@link VotingKeyLinkV1Transaction} mapper. */
public class VotingKeyLinkV1TransactionMapper
    extends AbstractTransactionMapper<VotingKeyLinkTransactionDTO, VotingKeyLinkV1Transaction> {

  public VotingKeyLinkV1TransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.VOTING_KEY_LINK, VotingKeyLinkTransactionDTO.class);
  }

  @Override
  protected TransactionFactory<VotingKeyLinkV1Transaction> createFactory(
      NetworkType networkType, Deadline deadline, VotingKeyLinkTransactionDTO transaction) {
    VotingKey linkedPublicKey = VotingKey.fromHexString(transaction.getLinkedPublicKey());
    LinkAction linkAction = LinkAction.rawValueOf(transaction.getLinkAction().getValue());
    return VotingKeyLinkV1TransactionFactory.create(
        networkType,
        deadline,
        linkedPublicKey,
        transaction.getStartEpoch(),
        transaction.getEndEpoch(),
        linkAction);
  }

  public int getVersion() {
    return 1;
  }

  @Override
  protected void copyToDto(
      VotingKeyLinkV1Transaction transaction, VotingKeyLinkTransactionDTO dto) {
    dto.setLinkAction(LinkActionEnum.fromValue((int) transaction.getLinkAction().getValue()));
    dto.setLinkedPublicKey(transaction.getLinkedPublicKey().toHex());
    dto.setStartEpoch(transaction.getStartEpoch());
    dto.setEndEpoch(transaction.getEndEpoch());
  }
}
