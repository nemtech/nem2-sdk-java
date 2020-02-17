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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.MerklePathItem;
import io.nem.sdk.model.blockchain.MerkleProofInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.okhttp_gson.api.BlockRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.BlockInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MerkleProofInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Blockchain http repository.
 *
 * @since 1.0
 */
public class BlockRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    BlockRepository {

    private final BlockRoutesApi client;

    private final GeneralTransactionMapper transactionMapper;

    public BlockRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new BlockRoutesApi(apiClient);
        this.transactionMapper = new GeneralTransactionMapper(getJsonHelper());
    }

    @Override
    public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
        Callable<BlockInfoDTO> callback = () -> getClient().getBlockByHeight(height);
        return exceptionHandling(call(callback).map(BlockRepositoryOkHttpImpl::toBlockInfo));
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
        Callable<List<BlockInfoDTO>> callback = () ->
            getClient().getBlocksByHeightWithLimit(height, limit);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(BlockRepositoryOkHttpImpl::toBlockInfo)
                .toList()
                .toObservable());
    }


    @Override
    public Observable<MerkleProofInfo> getMerkleTransaction(BigInteger height, String hash) {
        Callable<MerkleProofInfoDTO> callback = () ->
            getClient().getMerkleTransaction(height, hash);
        return exceptionHandling(call(callback).map(this::toMerkleProofInfo));

    }

    private Observable<List<Transaction>> getBlockTransactions(
        BigInteger height, Optional<QueryParams> queryParams) {
        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().getBlockTransactions(height,
                getPageSize(queryParams),
                getId(queryParams),
                null
            );

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }


    private MerkleProofInfo toMerkleProofInfo(MerkleProofInfoDTO dto) {
        List<MerklePathItem> pathItems =
            dto.getMerklePath().stream()
                .map(pathItem -> new MerklePathItem(pathItem.getPosition(), pathItem.getHash()))
                .collect(Collectors.toList());
        return new MerkleProofInfo(pathItems);
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
