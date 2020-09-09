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

import io.nem.symbol.sdk.api.BlockService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockServiceIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void isValidTransactionInBlock(RepositoryType type) {
    BigInteger height = BigInteger.ONE;
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);

    TransactionRepository transactionRepository =
        getRepositoryFactory(type).createTransactionRepository();

    List<Transaction> transactions =
        get(transactionRepository.search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .height(height)
                    .pageNumber(1)))
            .getData();

    BlockService service = new BlockServiceImpl(repositoryFactory);

    transactions.forEach(
        t -> {
          String hash = t.getTransactionInfo().get().getHash().get();
          Assertions.assertNotNull(hash);

          Boolean valid = get(service.isValidTransactionInBlock(height, hash));
          Assertions.assertTrue(valid);
        });
  }
}
