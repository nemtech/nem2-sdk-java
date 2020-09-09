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

import io.nem.symbol.sdk.api.AccountOrderBy;
import io.nem.symbol.sdk.api.AccountPaginationStreamer;
import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountSearchRepositoryIntegrationTest extends BaseIntegrationTest {

  public AccountRepository getAccountRepository(RepositoryType type) {
    return getRepositoryFactory(type).createAccountRepository();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchNoMosaicIdProvided(RepositoryType type) {
    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class,
            () ->
                get(
                    this.getAccountRepository(type)
                        .search(new AccountSearchCriteria().orderBy(AccountOrderBy.BALANCE))));

    Assertions.assertEquals(
        "ApiException: Conflict - 409 - InvalidArgument - mosaicId must be provided when sorting by balance",
        exception.getMessage());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void searchMosaicIdProvided(
      RepositoryType type, Function<MosaicId, AccountSearchCriteria> function) {
    AccountPaginationStreamer streamer =
        new AccountPaginationStreamer(this.getAccountRepository(type));
    MosaicId mosaicId =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getLinkedMosaicId(NetworkCurrency.CAT_CURRENCY.getNamespaceId().get()));

    AccountSearchCriteria criteria = function.apply(mosaicId);
    List<AccountInfo> accounts = get(streamer.search(criteria).toList().toObservable());

    Assertions.assertFalse(accounts.isEmpty());
    System.out.println(toJson(accounts));
    accounts.forEach(
        a -> {
          Assertions.assertTrue(
              a.getMosaics().stream()
                      .filter(m -> m.getId().equals(mosaicId))
                      .findFirst()
                      .get()
                      .getAmount()
                      .longValue()
                  > 0);
        });
  }

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

  private PaginationTester<AccountInfo, AccountSearchCriteria> getPaginationTester(
      RepositoryType type) {
    return new PaginationTester<>(
        () -> new AccountSearchCriteria(), getAccountRepository(type)::search);
  }
}
