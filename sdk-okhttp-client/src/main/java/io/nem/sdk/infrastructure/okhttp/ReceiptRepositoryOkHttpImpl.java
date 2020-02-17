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

import io.nem.sdk.api.ReceiptRepository;
import io.nem.sdk.model.blockchain.MerklePathItem;
import io.nem.sdk.model.blockchain.MerkleProofInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.openapi.okhttp_gson.api.ReceiptRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.MerkleProofInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.StatementsDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


/**
 * OkHttp implementation of {@link ReceiptRepository}.
 */
public class ReceiptRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    ReceiptRepository {

    private final ReceiptRoutesApi client;

    private final Observable<NetworkType> networkTypeObservable;

    public ReceiptRepositoryOkHttpImpl(ApiClient apiClient,
        Observable<NetworkType> networkTypeObservable) {
        super(apiClient);
        this.client = new ReceiptRoutesApi(apiClient);
        this.networkTypeObservable = networkTypeObservable;
    }

    @Override
    public Observable<Statement> getBlockReceipts(BigInteger height) {
        Callable<StatementsDTO> callback = () ->
            getClient().getBlockReceipts(height);
        return exceptionHandling(
            networkTypeObservable.flatMap(networkType -> call(callback).map(statementsDTO ->
                new ReceiptMappingOkHttp(getJsonHelper())
                    .createStatementFromDto(statementsDTO, networkType))));
    }


    public Observable<MerkleProofInfo> getMerkleReceipts(BigInteger height, String hash) {

        Callable<MerkleProofInfoDTO> callback = () ->
            getClient().getMerkleReceipts(height, hash);
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
