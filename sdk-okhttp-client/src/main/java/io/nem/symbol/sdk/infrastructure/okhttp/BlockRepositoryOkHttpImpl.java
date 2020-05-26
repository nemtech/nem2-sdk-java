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

package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.sdk.api.BlockOrderBy;
import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.BlockSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerklePathItem;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.BlockRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockOrderByEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleProofInfoDTO;
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


    public BlockRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new BlockRoutesApi(apiClient);
    }

    public static BlockInfo toBlockInfo(BlockInfoDTO blockInfoDTO) {
        return BlockInfo.create(
            blockInfoDTO.getMeta().getHash(),
            blockInfoDTO.getMeta().getGenerationHash(),
            blockInfoDTO.getMeta().getTotalFee(),
            blockInfoDTO.getMeta().getNumTransactions(),
            Optional.ofNullable(blockInfoDTO.getMeta().getNumStatements()),
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
            blockInfoDTO.getBlock().getProofGamma(),
            blockInfoDTO.getBlock().getProofScalar(),
            blockInfoDTO.getBlock().getProofVerificationHash(),
            blockInfoDTO.getBlock().getBeneficiaryPublicKey());
    }

    @Override
    public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
        Callable<BlockInfoDTO> callback = () -> getClient().getBlockByHeight(height);
        return exceptionHandling(call(callback).map(BlockRepositoryOkHttpImpl::toBlockInfo));
    }

    @Override
    public Observable<Page<BlockInfo>> searchBlocks(BlockSearchCriteria criteria) {
        Callable<BlockPage> callback = () -> getClient()
            .searchBlocks(toDto(criteria.getSignerPublicKey()),
                toDto(criteria.getSignerPublicKey()),
                criteria.getPageSize(),
                criteria.getPageNumber(), criteria.getOffset(),
                toDto(criteria.getOrder()), toDto(criteria.getOrderBy()));

        return exceptionHandling(
            call(callback).map(mosaicPage -> this.toPage(mosaicPage.getPagination(),
                mosaicPage.getData().stream().map(BlockRepositoryOkHttpImpl::toBlockInfo).collect(
                    Collectors.toList()))));
    }

    private BlockOrderByEnum toDto(BlockOrderBy orderBy) {
        return orderBy == null ? null : BlockOrderByEnum.fromValue(orderBy.getValue());
    }

    @Override
    public Observable<MerkleProofInfo> getMerkleTransaction(BigInteger height, String hash) {
        Callable<MerkleProofInfoDTO> callback = () ->
            getClient().getMerkleTransaction(height, hash);
        return exceptionHandling(call(callback).map(this::toMerkleProofInfo));

    }

    private MerkleProofInfo toMerkleProofInfo(MerkleProofInfoDTO dto) {
        List<MerklePathItem> pathItems =
            dto.getMerklePath().stream()
                .map(pathItem -> new MerklePathItem(pathItem.getPosition() == null ? null
                    : Position.rawValueOf(pathItem.getPosition().getValue()), pathItem.getHash()))
                .collect(Collectors.toList());
        return new MerkleProofInfo(pathItems);
    }

    public BlockRoutesApi getClient() {
        return client;
    }
}
