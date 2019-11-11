/*
 * Copyright 2019 NEM
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

package io.nem.sdk.api;

import io.nem.sdk.model.blockchain.MerkelProofInfo;
import io.nem.sdk.model.receipt.Statement;
import io.reactivex.Observable;
import java.math.BigInteger;

public interface ReceiptRepository {

    /**
     * Get receipts from a block
     *
     * @param height the height
     * @return {@link Observable} of Statement
     */
    Observable<Statement> getBlockReceipts(BigInteger height);

    /**
     * @param height the height
     * @param hash the hash.
     * @return {@link Observable} of MerkleProofInfo
     */
    Observable<MerkelProofInfo> getMerkleReceipts(BigInteger height, String hash);
}
