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

import io.nem.sdk.api.ReceiptRepository;
import io.nem.sdk.model.blockchain.MerklePathItem;
import io.nem.sdk.model.blockchain.MerkleProofInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.openapi.vertx.api.ReceiptRoutesApi;
import io.nem.sdk.openapi.vertx.api.ReceiptRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.MerkleProofInfoDTO;
import io.nem.sdk.openapi.vertx.model.StatementsDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * OkHttp implementation of {@link ReceiptRepository}.
 */
public class ReceiptRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    ReceiptRepository {

    private final ReceiptRoutesApi client;

    private final Observable<NetworkType> networkTypeObservable;

    public ReceiptRepositoryVertxImpl(ApiClient apiClient,
        Observable<NetworkType> networkTypeObservable) {
        super(apiClient);
        this.client = new ReceiptRoutesApiImpl(apiClient);
        this.networkTypeObservable = networkTypeObservable;
    }

    @Override
    public Observable<Statement> getBlockReceipts(BigInteger height) {
        Consumer<Handler<AsyncResult<StatementsDTO>>> callback = handler ->
            getClient().getBlockReceipts(height, handler);
        return exceptionHandling(
            networkTypeObservable.flatMap(networkType -> call(callback).map(statementsDTO ->
                new ReceiptMappingVertx(getJsonHelper())
                    .createStatementFromDto(statementsDTO, networkType))));
    }


    @Override
    public Observable<MerkleProofInfo> getMerkleReceipts(BigInteger height, String hash) {

        Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback = handler ->
            getClient().getMerkleReceipts(height, hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkleProofInfo));
    }


    private MerkleProofInfo toMerkleProofInfo(MerkleProofInfoDTO dto) {
        List<MerklePathItem> pathItems =
            dto.getMerklePath().stream()
                .map(
                    pathItem ->
                        new MerklePathItem(pathItem.getPosition(), pathItem.getHash()))
                .collect(Collectors.toList());
        return new MerkleProofInfo(pathItems);
    }


    public ReceiptRoutesApi getClient() {
        return client;
    }
}
