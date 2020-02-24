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

package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;

/**
 * Blockchain interface repository
 *
 * @since 1.0
 */
public interface BlockRepository {

    /**
     * Gets a BlockInfo for a given block height.
     *
     * @param height BigInteger
     * @return Observable of {@link BlockInfo}
     */
    Observable<BlockInfo> getBlockByHeight(BigInteger height);

    /**
     * Gets list of transactions included in a block for a block height
     *
     * @param height BigInteger
     * @return {@link Observable} of {@link Transaction} List
     */
    Observable<List<Transaction>> getBlockTransactions(BigInteger height);

    /**
     * Gets list of transactions included in a block for a block height With pagination.
     *
     * @param height BigInteger
     * @param queryParams QueryParams
     * @return {@link Observable} of {@link Transaction} List
     */
    Observable<List<Transaction>> getBlockTransactions(BigInteger height, QueryParams queryParams);

    /**
     * Get the merkle path for a given a receipt statement hash and block Returns the merkle path
     * for a [receipt statement or resolution](https://nemtech.github.io/concepts/receipt.html)
     * linked to a block. The path is the complementary data needed to calculate the merkle root. A
     * client can compare if the calculated root equals the one recorded in the block header,
     * verifying that the receipt was linked with the block.
     *
     * @param height the height
     * @param limit the limit.
     * @return {@link Observable} of BlockInfo
     */
    Observable<List<BlockInfo>> getBlocksByHeightWithLimit(
        BigInteger height, int limit);



    /**
     * Get the merkle path for a given a transaction and block Returns the merkle path for a
     * [transaction](https://nemtech.github.io/concepts/transaction.html) included in a block. The
     * path is the complementary data needed to calculate the merkle root. A client can compare if
     * the calculated root equals the one recorded in the block header, verifying that the
     * transaction was included in the block.
     *
     * @param height the height.
     * @param hash the expected hash.
     * @return {@link Observable} of MerkleProofInfo
     */
    Observable<MerkleProofInfo> getMerkleTransaction(BigInteger height, String hash);


}
