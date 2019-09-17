/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.infrastructure.vertx.mappers;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.AccountLinkTransactionDTO;

class AccountLinkTransactionMapper extends AbstractTransactionMapper<AccountLinkTransactionDTO> {


    public AccountLinkTransactionMapper(JsonHelper jsonHelper, SignSchema signSchema) {
        super(jsonHelper, TransactionType.ACCOUNT_LINK, AccountLinkTransactionDTO.class,
            signSchema);
    }

    @Override
    protected Transaction basicMap(TransactionInfo transactionInfo,
        AccountLinkTransactionDTO transaction) {
        Deadline deadline = new Deadline(transaction.getDeadline());
        NetworkType networkType = extractNetworkType(transaction.getVersion());
        return new AccountLinkTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            transaction.getMaxFee(),
            PublicAccount
                .createFromPublicKey(transaction.getRemotePublicKey(), networkType,
                    getSignSchema()),
            AccountLinkAction.rawValueOf(transaction.getLinkAction().getValue()),
            transaction.getSignature(),
            new PublicAccount(transaction.getSignerPublicKey(), networkType, getSignSchema()),
            transactionInfo);
    }
}
