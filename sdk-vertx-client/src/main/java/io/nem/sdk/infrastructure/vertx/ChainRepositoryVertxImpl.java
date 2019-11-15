/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.model.blockchain.BlockchainScore;
import io.nem.sdk.openapi.vertx.api.ChainRoutesApi;
import io.nem.sdk.openapi.vertx.api.ChainRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.ChainScoreDTO;
import io.nem.sdk.openapi.vertx.model.HeightInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.function.Consumer;

/**
 * Chain http repository.
 */
public class ChainRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    ChainRepository {

    private final ChainRoutesApi client;

    public ChainRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        client = new ChainRoutesApiImpl(apiClient);
    }

    public ChainRoutesApi getClient() {
        return client;
    }


    /**
     * Get Block chain height
     *
     * @return {@link Observable} of {@link BigInteger}
     */
    public Observable<BigInteger> getBlockchainHeight() {
        Consumer<Handler<AsyncResult<HeightInfoDTO>>> callback = client::getChainHeight;
        return exceptionHandling(call(callback).map(HeightInfoDTO::getHeight));

    }

    /**
     * Get Block chain score
     *
     * @return {@link Observable} of {@link BigInteger}
     */
    public Observable<BlockchainScore> getChainScore() {
        Consumer<Handler<AsyncResult<ChainScoreDTO>>> callback = client::getChainScore;
        return exceptionHandling(call(callback).map(
            blockchainScoreDTO ->
                new BlockchainScore(
                    blockchainScoreDTO.getScoreLow(),
                    blockchainScoreDTO.getScoreHigh())));
    }
}
