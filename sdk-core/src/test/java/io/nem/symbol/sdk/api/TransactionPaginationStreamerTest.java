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

import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Test of the TransactionPaginationStreamer */
public class TransactionPaginationStreamerTest {

  @Test
  void testMultiplePageStreamer() {
    tester().basicMultiPageTest();
  }

  @Test
  void singlePageTest() {
    tester().basicSinglePageTest();
  }

  @Test
  void multipageWithLimit() {
    tester().multipageWithLimit();
  }

  @Test
  void limitToTwoPages() {
    tester().limitToTwoPages();
  }

  private PaginationStreamerTester<Transaction, TransactionSearchCriteria> tester() {
    TransactionRepository repository = Mockito.mock(TransactionRepository.class);
    TransactionPaginationStreamer streamer = new TransactionPaginationStreamer(repository);
    return new PaginationStreamerTester<>(
        streamer,
        Transaction.class,
        repository,
        new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED));
  }
}
