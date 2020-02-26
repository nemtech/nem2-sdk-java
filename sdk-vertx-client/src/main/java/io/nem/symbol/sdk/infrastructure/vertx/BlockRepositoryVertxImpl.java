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

package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.QueryParams;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.TransactionMapper;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerklePathItem;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.openapi.vertx.api.BlockRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.BlockRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.BlockInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MerkleProofInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Blockchain http repository.
 *
 * @since 1.0
 */
public class BlockRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    BlockRepository {

    private final BlockRoutesApi client;

    private final TransactionMapper transactionMapper;

    public BlockRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        client = new BlockRoutesApiImpl(apiClient);
        transactionMapper = new GeneralTransactionMapper(getJsonHelper());
    }

    @Override
    public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
        Consumer<Handler<AsyncResult<BlockInfoDTO>>> callback = handler -> getClient()
            .getBlockByHeight(height, handler);
        return exceptionHandling(call(callback).map(BlockRepositoryVertxImpl::toBlockInfo));
    }

    @Override
    public Observable<List<Transaction>> getBlockTransactions(
        BigInteger height, QueryParams queryParams) {
        return this.getBlockTransactions(height, Optional.of(queryParams));
    }

    @Override
    public Observable<List<Transaction>> getBlockTransactions(BigInteger height) {
        return this.getBlockTransactions(height, Optional.empty());
    }

    @Override
    public Observable<List<BlockInfo>> getBlocksByHeightWithLimit(BigInteger height, int limit) {
        Consumer<Handler<AsyncResult<List<BlockInfoDTO>>>> callback = handler ->
            client.getBlocksByHeightWithLimit(height, limit, handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(BlockRepositoryVertxImpl::toBlockInfo)
                .toList().toObservable());
    }

    @Override
    public Observable<MerkleProofInfo> getMerkleTransaction(BigInteger height, String hash) {
        Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback = handler ->
            client.getMerkleTransaction(height, hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkleProofInfo));

    }

    private MerkleProofInfo toMerkleProofInfo(MerkleProofInfoDTO dto) {
        List<MerklePathItem> pathItems =
            dto.getMerklePath().stream()
                .map(
                    pathItem ->
                        new MerklePathItem(pathItem.getPosition() == null ? null
                            : Position.rawValueOf(pathItem.getPosition().getValue()),
                            pathItem.getHash()))
                .collect(Collectors.toList());
        return new MerkleProofInfo(pathItems);
    }

    private Observable<List<Transaction>> getBlockTransactions(
        BigInteger height, Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getBlockTransactions(height,
                getPageSize(queryParams),
                getId(queryParams),
                null,
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    private Transaction toTransaction(TransactionInfoDTO input) {
        return transactionMapper.map(input);
    }

    public static BlockInfo toBlockInfo(BlockInfoDTO blockInfoDTO) {
        return BlockInfo.create(
            blockInfoDTO.getMeta().getHash(),
            blockInfoDTO.getMeta().getGenerationHash(),
            blockInfoDTO.getMeta().getTotalFee(),
            blockInfoDTO.getMeta().getNumTransactions(),
            blockInfoDTO.getMeta().getNumStatements(),
            blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(),
            blockInfoDTO.getBlock().getSignature(),
            blockInfoDTO.getBlock().getSignerPublicKey(),
            NetworkType.rawValueOf(blockInfoDTO.getBlock().getNetwork().getValue()),
            blockInfoDTO.getBlock().getVersion(),
            blockInfoDTO.getBlock().getType(),
            blockInfoDTO.getBlock().getHeight(),
            blockInfoDTO.getBlock().getTimestamp(),
            blockInfoDTO.getBlock().getDifficulty(),
            blockInfoDTO.getBlock().getFeeMultiplier(),
            blockInfoDTO.getBlock().getPreviousBlockHash(),
            blockInfoDTO.getBlock().getTransactionsHash(),
            blockInfoDTO.getBlock().getReceiptsHash(),
            blockInfoDTO.getBlock().getStateHash(),
            blockInfoDTO.getBlock().getBeneficiaryPublicKey());
    }

    public BlockRoutesApi getClient() {
        return client;
    }
}
