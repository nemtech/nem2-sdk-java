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

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.BlockService;
import io.nem.sdk.api.RepositoryFactory;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockServiceIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void testTransferCatCurrencyFromNemesis(RepositoryType type) {
        BigInteger height = BigInteger.ONE;
        RepositoryFactory repositoryFactory = getRepositoryFactory(type);

        String hash = get(repositoryFactory.createBlockRepository().getBlockTransactions(
            height)).get(0).getTransactionInfo().get().getHash().get();

        BlockService service = new BlockServiceImpl(repositoryFactory);

        Boolean valid = get(service.isValidTransactionInBlock(height, hash));
        Assertions.assertTrue(valid);
    }

}
