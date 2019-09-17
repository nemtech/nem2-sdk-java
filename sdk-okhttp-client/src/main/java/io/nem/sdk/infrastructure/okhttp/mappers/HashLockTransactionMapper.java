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

package io.nem.sdk.infrastructure.okhttp.mappers;

import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.HashLockTransactionDTO;

class HashLockTransactionMapper extends AbstractTransactionMapper<HashLockTransactionDTO> {

    public HashLockTransactionMapper(JsonHelper jsonHelper,
        SignSchema signSchema) {
        super(jsonHelper, TransactionType.LOCK, HashLockTransactionDTO.class, signSchema);
    }

    @Override
    protected Transaction basicMap(TransactionInfo transactionInfo,
        HashLockTransactionDTO transaction) {

        Deadline deadline = new Deadline(transaction.getDeadline());
        NetworkType networkType = extractNetworkType(transaction.getVersion());
        Mosaic mosaic = getMosaic(transaction.getMosaic());
        return new HashLockTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            transaction.getMaxFee(),
            mosaic,
            transaction.getDuration(),
            new SignedTransaction("", transaction.getHash(),
                TransactionType.AGGREGATE_BONDED),
            transaction.getSignature(),
            new PublicAccount(transaction.getSignerPublicKey(), networkType, getSignSchema()),
            transactionInfo);
    }

    private Mosaic getMosaic(io.nem.sdk.openapi.okhttp_gson.model.Mosaic mosaic) {
        return new Mosaic(toMosaicId(mosaic.getId()),
            mosaic.getAmount());
    }
}
