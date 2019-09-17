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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.EmbeddedTransactionInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.EmbeddedTransactionMetaDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionMetaDTO;
import java.util.Collections;
import java.util.Map;

/**
 * Abstract transaction mapper for the transaction mappers that support a specific type of
 * transaction (Account Link, Mosaic Alias, etc.).
 *
 * @param <T> the dto type of the transaction object.
 */
public abstract class AbstractTransactionMapper<T> implements TransactionMapper {

    private final TransactionType transactionType;

    private final JsonHelper jsonHelper;

    private final Class<T> transactionDtoClass;

    private final SignSchema signSchema;

    public AbstractTransactionMapper(JsonHelper jsonHelper, TransactionType transactionType,
        Class<T> transactionDtoClass, SignSchema signSchema) {
        this.jsonHelper = jsonHelper;
        this.transactionType = transactionType;
        this.transactionDtoClass = transactionDtoClass;
        this.signSchema = signSchema;
    }

    /**
     * Performs some patches so catapult 5 and 6 are a bit more compatible.
     *
     * It quite likely that this method is going to be removed.
     *
     * @param transaction the transaction to be patched.
     */
    private void patchTransaction(Object transaction) {
        //Version 5 vs 6 workarounds
        if (transaction instanceof Map) {
            Map<String, Object> transactionMap = (Map<String, Object>) transaction;
            if (transactionMap.containsKey("mosaicId")) {
                transactionMap.put("mosaic",
                    Collections.singletonMap("id", transactionMap.get("mosaicId")));
            }
            if (transactionMap.containsKey("action")) {
                transactionMap.put("aliasAction", transactionMap.get("action"));
            }
            if (transactionMap.containsKey("mosaicNonce")) {
                transactionMap.put("nonce", transactionMap.get("mosaicNonce"));
            }
        }
    }

    @Override
    public Transaction map(EmbeddedTransactionInfoDTO transactionInfoDTO) {
        patchTransaction(transactionInfoDTO.getTransaction());
        TransactionInfo transactionInfo = createTransactionInfo(transactionInfoDTO.getMeta());
        T transaction = getJsonHelper()
            .convert(transactionInfoDTO.getTransaction(), transactionDtoClass);
        return basicMap(transactionInfo, transaction);
    }

    @Override
    public Transaction map(TransactionInfoDTO transactionInfoDTO) {
        patchTransaction(transactionInfoDTO.getTransaction());
        TransactionInfo transactionInfo = createTransactionInfo(transactionInfoDTO.getMeta());
        T transaction = getJsonHelper()
            .convert(transactionInfoDTO.getTransaction(), transactionDtoClass);
        return basicMap(transactionInfo, transaction);
    }

    protected abstract Transaction basicMap(TransactionInfo transactionInfo, T transaction);

    protected TransactionInfo createTransactionInfo(TransactionMetaDTO meta) {
        return TransactionInfo.create(meta.getHeight(),
            meta.getIndex(),
            meta.getId(),
            meta.getHash(),
            meta.getMerkleComponentHash());
    }

    protected TransactionInfo createTransactionInfo(EmbeddedTransactionMetaDTO meta) {
        return TransactionInfo.createAggregate(
            meta.getHeight(),
            meta.getIndex(),
            meta.getId(),
            meta.getAggregateHash(),
            meta.getAggregateId());
    }

    protected Integer extractTransactionVersion(int version) {
        return (int) Long.parseLong(Integer.toHexString(version).substring(2, 4), 16);
    }

    protected NetworkType extractNetworkType(int version) {
        int networkType = (int) Long.parseLong(Integer.toHexString(version).substring(0, 2), 16);
        return NetworkType.rawValueOf(networkType);
    }

    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }

    @Override
    public TransactionType getTransactionType() {
        return transactionType;
    }

    public SignSchema getSignSchema() {
        return signSchema;
    }
}
