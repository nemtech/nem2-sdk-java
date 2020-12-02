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

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.BlockOrderBy;
import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.BlockSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.BlockType;
import io.nem.symbol.sdk.model.blockchain.ImportanceBlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerklePathItem;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.vertx.api.BlockRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.BlockRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.BlockInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.BlockOrderByEnum;
import io.nem.symbol.sdk.openapi.vertx.model.BlockPage;
import io.nem.symbol.sdk.openapi.vertx.model.ImportanceBlockDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MerkleProofInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Blockchain http repository.
 *
 * @since 1.0
 */
public class BlockRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements BlockRepository {

  private final BlockRoutesApi client;

  public BlockRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    client = new BlockRoutesApiImpl(apiClient);
  }

  @Override
  public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
    Consumer<Handler<AsyncResult<BlockInfoDTO>>> callback =
        handler -> getClient().getBlockByHeight(height, handler);
    return exceptionHandling(
        call(callback)
            .map((BlockInfoDTO blockInfoDTO) -> toBlockInfo(blockInfoDTO, getJsonHelper())));
  }

  @Override
  public Observable<Page<BlockInfo>> search(BlockSearchCriteria criteria) {
    Consumer<Handler<AsyncResult<BlockPage>>> callback =
        handler ->
            getClient()
                .searchBlocks(
                    toDto(criteria.getSignerPublicKey()),
                    toDto(criteria.getBeneficiaryAddress()),
                    criteria.getPageSize(),
                    criteria.getPageNumber(),
                    criteria.getOffset(),
                    toDto(criteria.getOrder()),
                    toDto(criteria.getOrderBy()),
                    handler);

    return exceptionHandling(
        call(callback)
            .map(
                mosaicPage ->
                    this.toPage(
                        mosaicPage.getPagination(),
                        mosaicPage.getData().stream()
                            .map(
                                (BlockInfoDTO blockInfoDTO) ->
                                    toBlockInfo(blockInfoDTO, getJsonHelper()))
                            .collect(Collectors.toList()))));
  }

  private BlockOrderByEnum toDto(BlockOrderBy orderBy) {
    return orderBy == null ? null : BlockOrderByEnum.fromValue(orderBy.getValue());
  }

  @Override
  public Observable<MerkleProofInfo> getMerkleTransaction(BigInteger height, String hash) {
    Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback =
        handler -> client.getMerkleTransaction(height, hash, handler);
    return call(callback, this::toMerkleProofInfo);
  }

  private MerkleProofInfo toMerkleProofInfo(MerkleProofInfoDTO dto) {
    List<MerklePathItem> pathItems =
        dto.getMerklePath().stream()
            .map(
                pathItem ->
                    new MerklePathItem(
                        pathItem.getPosition() == null
                            ? null
                            : Position.rawValueOf(pathItem.getPosition().getValue()),
                        pathItem.getHash()))
            .collect(Collectors.toList());
    return new MerkleProofInfo(pathItems);
  }

  public Observable<MerkleProofInfo> getMerkleReceipts(BigInteger height, String hash) {
    Consumer<Handler<AsyncResult<MerkleProofInfoDTO>>> callback =
        (handler) -> getClient().getMerkleReceipts(height, hash, handler);
    return call(callback, this::toMerkleProofInfo);
  }

  public static BlockInfo toBlockInfo(BlockInfoDTO blockInfoDTO, JsonHelper jsonHelper) {
    ImportanceBlockDTO block =
        jsonHelper.convert(blockInfoDTO.getBlock(), ImportanceBlockDTO.class);
    NetworkType networkType = NetworkType.rawValueOf(block.getNetwork().getValue());
    BlockType type = BlockType.rawValueOf(block.getType());
    // block.getVotingEligibleAccountsCount() == null for the testnet block 1 incomplete nemesis block.
    // Remove before public net release
    if (type == BlockType.NORMAL_BLOCK || block.getVotingEligibleAccountsCount() == null)
      return new BlockInfo(
          blockInfoDTO.getId(),
          block.getSize(),
          blockInfoDTO.getMeta().getHash(),
          blockInfoDTO.getMeta().getGenerationHash(),
          blockInfoDTO.getMeta().getTotalFee(),
          blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(),
          blockInfoDTO.getMeta().getTransactionsCount(),
          blockInfoDTO.getMeta().getTotalTransactionsCount(),
          blockInfoDTO.getMeta().getStatementsCount(),
          blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(),
          block.getSignature(),
          PublicAccount.createFromPublicKey(block.getSignerPublicKey(), networkType),
          networkType,
          block.getVersion(),
          type,
          block.getHeight(),
          block.getTimestamp(),
          block.getDifficulty(),
          block.getFeeMultiplier(),
          block.getPreviousBlockHash(),
          block.getTransactionsHash(),
          block.getReceiptsHash(),
          block.getStateHash(),
          block.getProofGamma(),
          block.getProofScalar(),
          block.getProofVerificationHash(),
          MapperUtils.toAddress(block.getBeneficiaryAddress()));
    else {
      return new ImportanceBlockInfo(
          blockInfoDTO.getId(),
          block.getSize(),
          blockInfoDTO.getMeta().getHash(),
          blockInfoDTO.getMeta().getGenerationHash(),
          blockInfoDTO.getMeta().getTotalFee(),
          blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(),
          blockInfoDTO.getMeta().getTransactionsCount(),
          blockInfoDTO.getMeta().getTotalTransactionsCount(),
          blockInfoDTO.getMeta().getStatementsCount(),
          blockInfoDTO.getMeta().getStateHashSubCacheMerkleRoots(),
          block.getSignature(),
          PublicAccount.createFromPublicKey(block.getSignerPublicKey(), networkType),
          networkType,
          block.getVersion(),
          type,
          block.getHeight(),
          block.getTimestamp(),
          block.getDifficulty(),
          block.getFeeMultiplier(),
          block.getPreviousBlockHash(),
          block.getTransactionsHash(),
          block.getReceiptsHash(),
          block.getStateHash(),
          block.getProofGamma(),
          block.getProofScalar(),
          block.getProofVerificationHash(),
          MapperUtils.toAddress(block.getBeneficiaryAddress()),
          block.getVotingEligibleAccountsCount(),
          block.getHarvestingEligibleAccountsCount(),
          block.getTotalVotingBalance(),
          block.getPreviousImportanceBlockHash());
    }
  }

  public BlockRoutesApi getClient() {
    return client;
  }
}
