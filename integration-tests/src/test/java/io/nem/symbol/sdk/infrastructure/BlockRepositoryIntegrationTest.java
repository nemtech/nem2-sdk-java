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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.QueryParams;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.transaction.Transaction;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockRepositoryIntegrationTest extends BaseIntegrationTest {


    private BlockRepository getBlockRepository(RepositoryType type) {
        return getRepositoryFactory(type).createBlockRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockByHeight(RepositoryType type) {
        BlockInfo blockInfo = get(getBlockRepository(type).getBlockByHeight(BigInteger.valueOf(1)));
        assertEquals(1, blockInfo.getHeight().intValue());
        assertEquals(0, blockInfo.getTimestamp().intValue());
        assertEquals(getGenerationHash(), blockInfo.getGenerationHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockTransactions(RepositoryType type) {
        List<Transaction> transactions = get(
            getBlockRepository(type).getBlockTransactions(BigInteger.valueOf(1)));

        assertEquals(10, transactions.size());

        List<Transaction> nextTransactions = get(getBlockRepository(type).getBlockTransactions(
            BigInteger.valueOf(1),
            new QueryParams(10,
                transactions.get(0).getTransactionInfo().get().getId().get())));

        assertEquals(10, nextTransactions.size());
        assertEquals(transactions.get(1).getTransactionInfo().get().getHash(),
            nextTransactions.get(0).getTransactionInfo().get().getHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenBlockDoesNotExists(RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(getBlockRepository(type)
                .getBlockByHeight(BigInteger.valueOf(0))));

        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '0'",
            exception.getMessage());
    }
}
