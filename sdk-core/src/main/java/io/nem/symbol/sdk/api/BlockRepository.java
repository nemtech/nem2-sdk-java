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
import io.reactivex.Observable;
import java.math.BigInteger;

/**
 * Blockchain interface repository
 *
 * @since 1.0
 */
public interface BlockRepository extends Searcher<BlockInfo, BlockSearchCriteria> {

  /**
   * Gets a BlockInfo for a given block height.
   *
   * @param height BigInteger
   * @return Observable of {@link BlockInfo}
   */
  Observable<BlockInfo> getBlockByHeight(BigInteger height);

  /**
   * Get the merkle path for a given a transaction and block Returns the merkle path for a
   * [transaction](https://nemtech.github.io/concepts/transaction.html) included in a block. The
   * path is the complementary data needed to calculate the merkle root. A client can compare if the
   * calculated root equals the one recorded in the block header, verifying that the transaction was
   * included in the block.
   *
   * @param height the height.
   * @param hash the expected hash.
   * @return {@link Observable} of MerkleProofInfo
   */
  Observable<MerkleProofInfo> getMerkleTransaction(BigInteger height, String hash);

  /**
   * Get the merkle path for a given a receipt statement hash and block Returns the merkle path for
   * a receipt statement or resolution linked to a block. The merkle path is the minimum number of
   * nodes needed to calculate the merkle root. Steps to calculate the merkle root: 1. proofHash
   * &#x3D; hash (leaf). 2. Concatenate proofHash with the first unprocessed item from the
   * merklePath list as follows: * a) If item.position &#x3D;&#x3D; left -&gt; proofHash &#x3D;
   * sha_256(item.hash + proofHash). * b) If item.position &#x3D;&#x3D; right -&gt; proofHash &#x3D;
   * sha_256(proofHash+ item.hash). 3. Repeat 2. for every item in the merklePath list. 4. Compare
   * if the calculated proofHash equals the one recorded in the block header (block.receiptsHash) to
   * verify if the statement was linked with the block.
   *
   * @param height Block height. (required)
   * @param hash Receipt hash. (required)
   * @return {@link Observable} of MerkleProofInfo
   */
  Observable<MerkleProofInfo> getMerkleReceipts(BigInteger height, String hash);
}
