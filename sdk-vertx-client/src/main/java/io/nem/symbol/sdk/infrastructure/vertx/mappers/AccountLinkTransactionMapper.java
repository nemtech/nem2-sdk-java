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

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountLinkTransaction;
import io.nem.symbol.sdk.model.transaction.AccountLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.AccountLinkTransactionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.LinkActionEnum;

/**
 * Account link transaction mapper.
 */
class AccountLinkTransactionMapper extends
    AbstractTransactionMapper<AccountLinkTransactionDTO, AccountLinkTransaction> {

    public AccountLinkTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.ACCOUNT_LINK, AccountLinkTransactionDTO.class);
    }

    @Override
    protected AccountLinkTransactionFactory createFactory(NetworkType networkType,
        AccountLinkTransactionDTO dto) {
        PublicAccount remoteAccount = PublicAccount
            .createFromPublicKey(dto.getRemotePublicKey(), networkType);
        return AccountLinkTransactionFactory.create(networkType,
            remoteAccount,
            LinkAction.rawValueOf(dto.getLinkAction().getValue()));
    }

    @Override
    protected void copyToDto(AccountLinkTransaction transaction, AccountLinkTransactionDTO dto) {
        dto.setRemotePublicKey(transaction.getRemoteAccount().getPublicKey().toHex());
        dto.setLinkAction(
            LinkActionEnum.fromValue((int) transaction.getLinkAction().getValue()));
    }

}
