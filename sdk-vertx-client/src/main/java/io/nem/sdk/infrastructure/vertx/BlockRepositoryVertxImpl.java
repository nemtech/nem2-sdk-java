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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.sdk.infrastructure.vertx.mappers.TransactionMapper;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.MerkelPathItem;
import io.nem.sdk.model.blockchain.MerkelProofInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.vertx.api.BlockRoutesApi;
import io.nem.sdk.openapi.vertx.api.BlockRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.BlockInfoDTO;
import io.nem.sdk.openapi.vertx.model.MerkleProofInfoDTO;
import io.nem.sdk.openapi.vertx.model.StatementsDTO;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
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

    public BlockRepositoryVertxImpl(ApiClient apiClient, Supplier<NetworkType> networkType,
        SignSchema signSchema) {
        super(apiClient, networkType, signSchema);
        client = new BlockRoutesApiImpl(apiClient);
        transactionMapper = new GeneralTransactionMapper(getJsonHelper(), signSchema);
    }

    public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
        Consumer<Handler<AsyncResult<BlockInfoDTO>>> callback = handler -> getClient()
            .getBlockByHeight(height.longValue(), handler);
        return exceptionHandling(call(callback).map(
            blockInfoDTO -> toBlockInfo(getSignSchema(), blockInfoDTO)));
    }

    public Observable<List<Transaction>> getBlockTransactions(
        BigInteger height, QueryParams queryParams) {
        return this.getBlockTransactions(height, Optional.of(queryParams));
    }

    public Observable<List<Transaction>> getBlockTransactions(BigInteger height) {
        return this.getBlockTransactions(height, Optional.empty());
    }

    public Observable<List<BlockInfo>> getBlocksByHeightWithLimit(
        BigInteger height, int limit, Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<List<BlockInfoDTO>>>> callback = handler ->
            client.getBlocksByHeightWithLimit(height.longValue(), limit, handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(
                blockInfoDTO -> toBlockInfo(getSignSchema(), blockInfoDTO))
                .toList()
                .toObservable());
    }

    public Observable<MerkelProofInfo> getMerkleReceipts(BigInteger height, String hash) {

        Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback = handler ->
            client.getMerkleReceipts(height.longValue(), hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkelProofInfo));


    }

    private MerkelProofInfo toMerkelProofInfo(MerkleProofInfoDTO dto) {
        List<MerkelPathItem> pathItems =
            dto.getMerklePath().stream()
                .map(
                    pathItem ->
                        new MerkelPathItem(pathItem.getPosition(), pathItem.getHash()))
                .collect(Collectors.toList());
        return new MerkelProofInfo(pathItems, "TODO MerkelProofInfoDto.getType()");
    }

    public Observable<MerkelProofInfo> getMerkleTransaction(BigInteger height, String hash) {
        Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback = handler ->
            client.getMerkleTransaction(height.longValue(), hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkelProofInfo));

    }

    public Observable<Statement> getBlockReceipts(BigInteger height) {
        Consumer<Handler<AsyncResult<StatementsDTO>>> callback = handler ->
            client.getBlockReceipts(height.longValue(), handler);
        return exceptionHandling(call(callback).map(statementsDTO ->
            new ReceiptMappingVertx(getSignSchema(), getJsonHelper())
                .createStatementFromDto(statementsDTO, getNetworkTypeBlocking())));
    }

    private Observable<List<Transaction>> getBlockTransactions(
        BigInteger height, Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getBlockTransactions(height.longValue(),
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

    public static BlockInfo toBlockInfo(SignSchema signSchema,
        BlockInfoDTO blockInfoDTO) {
        return BlockInfo.create(
            signSchema,
            blockInfoDTO.getMeta().getHash(),
            blockInfoDTO.getMeta().getGenerationHash(),
            blockInfoDTO.getMeta().getTotalFee(),
            blockInfoDTO.getMeta().getNumTransactions(),
            blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(),
            blockInfoDTO.getBlock().getSignature(),
            blockInfoDTO.getBlock().getSignerPublicKey(),
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
