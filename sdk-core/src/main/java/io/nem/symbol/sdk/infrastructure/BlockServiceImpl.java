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

package io.nem.symbol.sdk.infrastructure;


import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.BlockService;
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerklePathItem;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Implementation of {@link BlockService}
 */
public class BlockServiceImpl implements BlockService {

    /**
     * The block repository.
     */
    private final BlockRepository blockRepository;

    /**
     * The recipient repository.
     */
    private final ReceiptRepository receiptRepository;

    /**
     * @param repositoryFactory the repository factory.
     */
    public BlockServiceImpl(RepositoryFactory repositoryFactory) {
        this.blockRepository = repositoryFactory.createBlockRepository();
        this.receiptRepository = repositoryFactory.createReceiptRepository();
    }

    @Override
    public Observable<Boolean> isValidTransactionInBlock(BigInteger height,
        String transactionHash) {
        Validate.notNull(height, "height is required");
        Validate.notNull(transactionHash, "transactionHash is required");
        Observable<MerkleProofInfo> merkleTransactionObservable = blockRepository
            .getMerkleTransaction(height, transactionHash);

        return getBooleanObservable(height, transactionHash, merkleTransactionObservable);
    }

    @Override
    public Observable<Boolean> isValidStatementInBlock(BigInteger height, String statementHash) {
        Validate.notNull(height, "height is required");
        Validate.notNull(statementHash, "statementHash is required");
        Observable<MerkleProofInfo> merkleTransactionObservable = receiptRepository
            .getMerkleReceipts(height, statementHash);
        return getBooleanObservable(height, statementHash, merkleTransactionObservable);
    }

    private Observable<Boolean> getBooleanObservable(BigInteger height, String leaf,
        Observable<MerkleProofInfo> merkleTransactionObservable) {

        Observable<BlockInfo> blockByHeightObservable = blockRepository.getBlockByHeight(height);
        BiFunction<BlockInfo, MerkleProofInfo, Boolean> zipper = (blockInfo, merkleProofInfo) -> {
            String root = blockInfo.getBlockTransactionsHash();
            List<MerklePathItem> merklePath = merkleProofInfo.getMerklePath();
            if (merklePath.isEmpty()) {
                // Single item tree, so leaf = HRoot0
                return leaf.equalsIgnoreCase(root);
            }

            // 1 is left
            java.util.function.BiFunction<String, MerklePathItem, String> accumulator = (proofHash, pathItem) -> ConvertUtils
                .toHex(Hashes
                    .sha3_256(ConvertUtils
                        .fromHexToBytes(
                            pathItem.getPosition() == Position.LEFT ? pathItem.getHash() + proofHash
                                : proofHash + pathItem.getHash())));

            String hroot0 = merklePath.stream().reduce(leaf, accumulator, (s1, s2) -> s1);
            return root.equalsIgnoreCase(hroot0);
        };
        return Observable.zip(blockByHeightObservable, merkleTransactionObservable, zipper);
    }
}
