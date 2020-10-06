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

import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.MosaicRestrictionTransactionService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.reactivex.Observable;
import java.math.BigInteger;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Abstract class for all the repository integration tests.
 *
 * <p>In general, the test ares parametrized so multiple implementations of a repository can be
 * tested at the same time.
 */
public abstract class BaseIntegrationTest {

  protected static final RepositoryType DEFAULT_REPOSITORY_TYPE =
      TestHelper.DEFAULT_REPOSITORY_TYPE;
  protected static TestHelper helper;
  protected static BigInteger maxFee;

  @BeforeAll
  public static void beforeAll() throws Exception {
    // runCommand("ls -la");
    // runCommand("symbol-bootstrap start -d -t target/bootstrap -r -c
    // bootstrap-preset.yml");
    BaseIntegrationTest.helper = new TestHelper();
    BaseIntegrationTest.maxFee = helper.maxFee;
    // runCommand("symbol-bootstrap stop -c bootstrap-preset.yml");
  }

  @AfterAll
  static void stopBootstrap() throws Exception {
    // runCommand("symbol-bootstrap stop -c bootstrap-preset.yml");
  }

  @AfterAll
  static void tearDown() throws Exception {
    // listenerMap.values().forEach(Listener::close);
    // repositoryFactoryMap.values().forEach(RepositoryFactory::close);
  }

  /**
   * An utility method that executes a rest call though the Observable. It simplifies and unifies
   * the executions of rest calls.
   *
   * <p>This methods adds the necessary timeouts and exception handling,
   *
   * @param observable the observable, typically the one that performs a rest call.
   * @param <T> the observable type
   * @return the response from the rest call.
   */
  protected <T> T get(Observable<T> observable) {
    return helper().get(observable);
  }

  /**
   * Method that create a {@link RepositoryFactory} based on the {@link RepositoryType} if
   * necessary. The created repository factories are being cached for performance and multithread
   * testing.
   */
  public RepositoryFactory getRepositoryFactory(RepositoryType type) {
    return helper().getRepositoryFactory(type);
  }

  public Config config() {
    return helper().config();
  }

  public TestHelper helper() {
    return helper;
  }

  public JsonHelper jsonHelper() {
    return helper().getJsonHelper();
  }

  public NetworkType getNetworkType() {
    return helper().getNetworkType();
  }



  public Deadline getDeadline() {
    return helper().getDeadline();
  }


  public Address getRecipient() {
    return config().getTestAccount2().getAddress();
  }

  public String getGenerationHash() {
    return helper().getGenerationHash();
  }

  /** Method that creates a {@link RepositoryFactory} based on the {@link RepositoryType}. */
  public Listener getListener(RepositoryType type) {
    return helper().getListener(type);
  }

  <T extends Transaction> Pair<T, AggregateTransaction> announceAggregateAndValidate(
      RepositoryType type, T transaction, Account testAccount, Account... signers) {
    return helper().announceAggregateAndValidate(type, transaction, testAccount, signers);
  }

  <T extends Transaction> T announceAndValidate(
      RepositoryType type, Account testAccount, T transaction) {
    return helper().announceAndValidate(type, testAccount, transaction);
  }

  protected TransactionService getTransactionService(RepositoryType type) {
    return helper().getTransactionService(type);
  }

  protected MosaicRestrictionTransactionService getMosaicRestrictionTransactionService(
      RepositoryType type) {
    return new MosaicRestrictionTransactionServiceImpl(getRepositoryFactory(type));
  }

  /**
   * This method listens for the next object in observable but if a status error happens first it
   * will raise an error. This speeds up the tests, if a transaction is not announced correctly, the
   * method will fail before timing out as it listens errors raised by the server.
   */
  protected <T> T getTransactionOrFail(Observable<T> observable, Transaction originalTransaction) {
    return helper().getTransactionOrFail(observable, originalTransaction);
  }

  protected String toJson(Transaction originalTransaction) {
    return helper().toJson(originalTransaction);
  }

  protected String toJson(Object anyObject) {
    return helper().toJson(anyObject);
  }

  protected void sleep(long time) {
    helper().sleep(time);
  }

  public NetworkCurrency getNetworkCurrency() {
    return helper().getNetworkCurrency();
  }

  protected NamespaceId setAddressAlias(
      RepositoryType type, Address address, String namespaceName) {
    return helper().setAddressAlias(type, address, namespaceName);
  }

  protected NamespaceId setMosaicAlias(
      RepositoryType type, MosaicId mosaicId, String namespaceName) {
    return helper().setMosaicAlias(type, mosaicId, namespaceName);
  }

  protected MosaicId createMosaic(
      Account account, RepositoryType type, BigInteger initialSupply, String alias) {
    return helper().createMosaic(account, type, initialSupply, alias);
  }

  public NamespaceId createRootNamespace(
      RepositoryType type, Account testAccount, String namespaceName) {
    return helper().createRootNamespace(type, testAccount, namespaceName);
  }
}
