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

import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.HashLockTransactionDTO;

/**
 * Hash lock transaction mapper.
 */
class HashLockTransactionMapper extends
    AbstractTransactionMapper<HashLockTransactionDTO, HashLockTransaction> {

    public HashLockTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.LOCK, HashLockTransactionDTO.class);
    }

    private Mosaic getMosaic(io.nem.sdk.openapi.vertx.model.Mosaic mosaic) {
        return new Mosaic(toMosaicId(mosaic.getId()),
            mosaic.getAmount());
    }

    @Override
    protected TransactionFactory<HashLockTransaction> createFactory(NetworkType networkType,
        HashLockTransactionDTO transaction) {
        Mosaic mosaic = getMosaic(transaction.getMosaic());
        SignedTransaction signedTransaction = new SignedTransaction("", transaction.getHash(),
            TransactionType.AGGREGATE_BONDED);
        return HashLockTransactionFactory.create(networkType, mosaic, transaction.getDuration(),
            signedTransaction);
    }
}
