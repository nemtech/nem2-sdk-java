/*
 * Copyright 2019 NEM
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

package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.api.AggregateRepository;
import io.nem.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.sdk.infrastructure.vertx.mappers.TransactionMapper;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.openapi.vertx.api.AggregateRoutesApi;
import io.nem.sdk.openapi.vertx.api.AggregateRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.AnnounceTransactionInfoDTO;
import io.nem.sdk.openapi.vertx.model.Cosignature;
import io.nem.sdk.openapi.vertx.model.TransactionPayload;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AggregateRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    AggregateRepository {

    private final AggregateRoutesApi client;

    private final TransactionMapper transactionMapper;

    public AggregateRepositoryVertxImpl(ApiClient apiClient,
        Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
        client = new AggregateRoutesApiImpl(apiClient);
        transactionMapper = new GeneralTransactionMapper(getJsonHelper());
    }


    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBonded(
        SignedTransaction signedTransaction) {
        Consumer<Handler<AsyncResult<AnnounceTransactionInfoDTO>>> callback = handler -> getClient()
            .announcePartialTransaction(
                new TransactionPayload().payload(signedTransaction.getPayload()),
                handler);
        return exceptionHandling(
            call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(
        CosignatureSignedTransaction cosignatureSignedTransaction) {

        Consumer<Handler<AsyncResult<AnnounceTransactionInfoDTO>>> callback = handler -> getClient()
            .announceCosignatureTransaction(
                new Cosignature().parentHash(cosignatureSignedTransaction.getParentHash())
                    .signature(cosignatureSignedTransaction.getSignature())
                    .signature(cosignatureSignedTransaction.getSigner()),
                handler);
        return exceptionHandling(
            call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));


    }

    public AggregateRoutesApi getClient() {
        return client;
    }
}
