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

import io.nem.symbol.sdk.api.BlockOrderBy;
import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.BlockSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerklePathItem;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.openapi.vertx.api.BlockRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.BlockRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.BlockInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.BlockOrderByEnum;
import io.nem.symbol.sdk.openapi.vertx.model.BlockPage;
import io.nem.symbol.sdk.openapi.vertx.model.MerkleProofInfoDTO;
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
public class BlockRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements BlockRepository {

    private final BlockRoutesApi client;

    public BlockRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        client = new BlockRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
        Consumer<Handler<AsyncResult<BlockInfoDTO>>> callback = handler -> getClient()
            .getBlockByHeight(height, handler);
        return exceptionHandling(call(callback).map(BlockRepositoryVertxImpl::toBlockInfo));
    }

    @Override
    public Observable<Page<BlockInfo>> search(BlockSearchCriteria criteria) {
        Consumer<Handler<AsyncResult<BlockPage>>> callback = handler -> getClient()
            .searchBlocks(toDto(criteria.getSignerPublicKey()), toDto(criteria.getBeneficiaryAddress()),
                criteria.getPageSize(), criteria.getPageNumber(), criteria.getOffset(), toDto(criteria.getOrder()),
                toDto(criteria.getOrderBy()), handler);

        return exceptionHandling(call(callback).map(mosaicPage -> this.toPage(mosaicPage.getPagination(),
            mosaicPage.getData().stream().map(BlockRepositoryVertxImpl::toBlockInfo).collect(Collectors.toList()))));
    }

    private BlockOrderByEnum toDto(BlockOrderBy orderBy) {
        return orderBy == null ? null : BlockOrderByEnum.fromValue(orderBy.getValue());
    }

    @Override
    public Observable<MerkleProofInfo> getMerkleTransaction(BigInteger height, String hash) {
        Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback = handler -> client
            .getMerkleTransaction(height, hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkleProofInfo));

    }

    private MerkleProofInfo toMerkleProofInfo(MerkleProofInfoDTO dto) {
        List<MerklePathItem> pathItems = dto.getMerklePath().stream().map(pathItem -> new MerklePathItem(
            pathItem.getPosition() == null ? null : Position.rawValueOf(pathItem.getPosition().getValue()),
            pathItem.getHash())).collect(Collectors.toList());
        return new MerkleProofInfo(pathItems);
    }


    public Observable<MerkleProofInfo> getMerkleReceipts(BigInteger height, String hash) {
        Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback = (handler) -> getClient()
            .getMerkleReceipts(height, hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkleProofInfo));
    }


    public static BlockInfo toBlockInfo(BlockInfoDTO blockInfoDTO) {
        NetworkType networkType = NetworkType.rawValueOf(blockInfoDTO.getBlock().getNetwork().getValue());
        return new BlockInfo(blockInfoDTO.getId(), blockInfoDTO.getBlock().getSize(), blockInfoDTO.getMeta().getHash(),
            blockInfoDTO.getMeta().getGenerationHash(), blockInfoDTO.getMeta().getTotalFee(),
            blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(),
            blockInfoDTO.getMeta().getNumTransactions(), Optional.ofNullable(blockInfoDTO.getMeta().getNumStatements()),
            blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(), blockInfoDTO.getBlock().getSignature(),
            PublicAccount.createFromPublicKey(blockInfoDTO.getBlock().getSignerPublicKey(), networkType), networkType,
            blockInfoDTO.getBlock().getVersion(), blockInfoDTO.getBlock().getType(),
            blockInfoDTO.getBlock().getHeight(), blockInfoDTO.getBlock().getTimestamp(),
            blockInfoDTO.getBlock().getDifficulty(), blockInfoDTO.getBlock().getFeeMultiplier(),
            blockInfoDTO.getBlock().getPreviousBlockHash(), blockInfoDTO.getBlock().getTransactionsHash(),
            blockInfoDTO.getBlock().getReceiptsHash(), blockInfoDTO.getBlock().getStateHash(),
            blockInfoDTO.getBlock().getProofGamma(), blockInfoDTO.getBlock().getProofScalar(),
            blockInfoDTO.getBlock().getProofVerificationHash(),
            Address.createFromEncoded(blockInfoDTO.getBlock().getBeneficiaryAddress()));
    }

    public BlockRoutesApi getClient() {
        return client;
    }
}
