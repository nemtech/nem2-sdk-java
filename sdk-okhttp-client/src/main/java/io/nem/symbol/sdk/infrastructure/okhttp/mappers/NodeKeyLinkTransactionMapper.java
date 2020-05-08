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
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.NodeKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.NodeKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.LinkActionEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NodeKeyLinkTransactionDTO;

/**
 * {@link NodeKeyLinkTransaction} mapper.
 */
public class NodeKeyLinkTransactionMapper extends
    AbstractTransactionMapper<NodeKeyLinkTransactionDTO, NodeKeyLinkTransaction> {

    public NodeKeyLinkTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.NODE_KEY_LINK, NodeKeyLinkTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<NodeKeyLinkTransaction> createFactory(NetworkType networkType,
        NodeKeyLinkTransactionDTO transaction) {
        PublicKey linkedPublicKey = PublicKey.fromHexString(transaction.getLinkedPublicKey());
        LinkAction linkAction = LinkAction.rawValueOf(transaction.getLinkAction().getValue());
        return NodeKeyLinkTransactionFactory.create(networkType, linkedPublicKey, linkAction);
    }

    @Override
    protected void copyToDto(NodeKeyLinkTransaction transaction, NodeKeyLinkTransactionDTO dto) {
        dto.setLinkAction(LinkActionEnum.fromValue((int) transaction.getLinkAction().getValue()));
        dto.setLinkedPublicKey(transaction.getLinkedPublicKey().toHex());
    }

}
