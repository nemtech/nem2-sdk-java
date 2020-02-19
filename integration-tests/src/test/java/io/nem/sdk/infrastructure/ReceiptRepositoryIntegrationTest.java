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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.api.ReceiptRepository;
import io.nem.sdk.model.blockchain.MerkleProofInfo;
import io.nem.sdk.model.receipt.Statement;
import java.math.BigInteger;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReceiptRepositoryIntegrationTest extends BaseIntegrationTest {


    private ReceiptRepository getReceiptRepository(RepositoryType type) {
        return getRepositoryFactory(type).createReceiptRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockReceipts(RepositoryType type) {
        Statement statement = get(
            getReceiptRepository(type).getBlockReceipts(BigInteger.valueOf(1)));
        assertTrue(statement.getTransactionStatements().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockReceiptMerkle(RepositoryType type) {
        Statement statement = get(
            getReceiptRepository(type).getBlockReceipts(BigInteger.valueOf(2)));
        String hash = statement.getTransactionStatements().get(0).generateHash();

        MerkleProofInfo merkleInfo = get(
            getReceiptRepository(type).getMerkleReceipts(BigInteger.valueOf(2), hash));
        assertTrue(merkleInfo.getMerklePath().size() >= 0);
    }

}
