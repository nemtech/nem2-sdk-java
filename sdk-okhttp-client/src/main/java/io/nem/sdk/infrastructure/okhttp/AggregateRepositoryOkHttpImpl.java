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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.api.AggregateRepository;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.openapi.okhttp_gson.api.AggregateRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.AnnounceTransactionInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.Cosignature;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionPayload;
import io.reactivex.Observable;
import java.util.concurrent.Callable;

public class AggregateRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    AggregateRepository {

    private final AggregateRoutesApi client;
    private final GeneralTransactionMapper transactionMapper;

    public AggregateRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new AggregateRoutesApi(apiClient);
        this.transactionMapper = new GeneralTransactionMapper(getJsonHelper());
    }


    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBonded(
        SignedTransaction signedTransaction) {
        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient()
            .announcePartialTransaction(
                new TransactionPayload().payload(signedTransaction.getPayload()));
        return exceptionHandling(
            call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(
        CosignatureSignedTransaction cosignatureSignedTransaction) {

        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient()
            .announceCosignatureTransaction(
                new Cosignature().parentHash(cosignatureSignedTransaction.getParentHash())
                    .signature(cosignatureSignedTransaction.getSignature())
                    .signature(cosignatureSignedTransaction.getSigner()));
        return exceptionHandling(
            call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));


    }

    public AggregateRoutesApi getClient() {
        return client;
    }
}
