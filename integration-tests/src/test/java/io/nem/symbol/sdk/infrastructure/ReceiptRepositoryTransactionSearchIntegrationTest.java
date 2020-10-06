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

import io.nem.symbol.sdk.api.PaginationStreamer;
import io.nem.symbol.sdk.api.ReceiptPaginationStreamer;
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.TransactionStatementSearchCriteria;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReceiptRepositoryTransactionSearchIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearch(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(null);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchSize50(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(50);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchTransaction(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(null);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void defaultSearchTransactionPageSize50(RepositoryType type) {
    getPaginationTester(type).basicTestSearch(50);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByIdAsc(RepositoryType type) {
    getPaginationTester(type).searchOrderByIdAsc();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchOrderByIdDesc(RepositoryType type) {
    getPaginationTester(type).searchOrderByIdDesc();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void  searchUsingRecipientTypes(RepositoryType type) {

    assertRecipientType(type, Collections.singletonList(ReceiptType.HARVEST_FEE));
    assertRecipientType(type, Collections.singletonList(ReceiptType.NAMESPACE_RENTAL_FEE));
    assertRecipientType(
        type, Arrays.asList(ReceiptType.HARVEST_FEE, ReceiptType.NAMESPACE_RENTAL_FEE));
    assertRecipientType(
        type, Arrays.asList(ReceiptType.TRANSACTION_GROUP, ReceiptType.NAMESPACE_RENTAL_FEE));
    assertRecipientType(type, Collections.singletonList(ReceiptType.TRANSACTION_GROUP));
  }

  List<TransactionStatement> assertRecipientType(
      RepositoryType type, List<ReceiptType> receiptTypes) {

    ReceiptRepository receiptRepository = getReceiptRepository(type);
    PaginationStreamer<TransactionStatement, TransactionStatementSearchCriteria> streamer =
        ReceiptPaginationStreamer.transactions(receiptRepository);
    List<TransactionStatement> transactionStatements =
        get(
            streamer
                .search(new TransactionStatementSearchCriteria().receiptTypes(receiptTypes))
                .toList()
                .toObservable());

    transactionStatements.forEach(
        s ->
            Assertions.assertTrue(
                s.getReceipts().stream().anyMatch(r -> (receiptTypes.contains(r.getType())))));
    return transactionStatements;
  }

  private ReceiptRepository getReceiptRepository(RepositoryType type) {
    return getRepositoryFactory(type).createReceiptRepository();
  }

  private PaginationTester<TransactionStatement, TransactionStatementSearchCriteria>
      getPaginationTester(RepositoryType type) {
    return new PaginationTester<>(
        TransactionStatementSearchCriteria::new, getReceiptRepository(type)::searchReceipts);
  }
}
