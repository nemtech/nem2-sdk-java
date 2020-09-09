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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.MosaicRestrictionTransactionService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.infrastructure.okhttp.RepositoryFactoryOkHttpImpl;
import io.nem.symbol.sdk.infrastructure.vertx.JsonHelperJackson2;
import io.nem.symbol.sdk.infrastructure.vertx.RepositoryFactoryVertxImpl;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountNames;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransaction;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.reactivex.Observable;
import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

/**
 * Abstract class for all the repository integration tests.
 *
 * <p>In general, the test ares parametrized so multiple implementations of a repository can be
 * tested at the same time.
 */
public abstract class BaseIntegrationTest {

  /** The default repository type used when you are not testing the different implementations. */
  protected static final RepositoryType DEFAULT_REPOSITORY_TYPE = RepositoryType.VERTX;

  protected static TestHelper helper;
  private static final Map<RepositoryType, RepositoryFactory> repositoryFactoryMap =
      new HashMap<>();
  private static final Map<RepositoryType, Listener> listenerMap = new HashMap<>();
  private final JsonHelper jsonHelper =
      new JsonHelperJackson2(JsonHelperJackson2.configureMapper(new ObjectMapper()));
  private static String generationHash;
  private static NetworkType networkType;
  private static NetworkCurrency networkCurrency;
  protected static BigInteger maxFee = BigInteger.valueOf(1000000);

  @BeforeAll
  static void beforeAll() throws Exception {
    // runCommand("ls -la");
    // runCommand("symbol-bootstrap start -d -t target/bootstrap -r -c
    // bootstrap-preset.yml");
    BaseIntegrationTest.helper = new TestHelper();
    System.out.println("Running tests against server: " + config().getApiUrl());
    BaseIntegrationTest.generationHash = resolveGenerationHash();
    BaseIntegrationTest.networkType = resolveNetworkType();
    BaseIntegrationTest.helper.config().init(networkType);
    BaseIntegrationTest.networkCurrency = resolveNetworkCurrency();

    System.out.println("Network Type: " + networkType);
    System.out.println("Generation Hash: " + generationHash);
    // runCommand("symbol-bootstrap stop -c bootstrap-preset.yml");
  }

  @AfterAll
  static void stopBootstrap() throws Exception {
    // runCommand("symbol-bootstrap stop -c bootstrap-preset.yml");
  }

  private static NetworkCurrency resolveNetworkCurrency() {
    return get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).getNetworkCurrency());
  }

  private static String resolveGenerationHash() {
    return get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).getGenerationHash());
  }

  private static NetworkType resolveNetworkType() {
    return get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).getNetworkType());
  }

  @AfterAll
  static void tearDown() throws Exception {
    // listenerMap.values().forEach(Listener::close);
    // repositoryFactoryMap.values().forEach(RepositoryFactory::close);
  }

  private static void runCommand(String cmd) {
    try {
      ProcessBuilder builder = new ProcessBuilder();
      builder.command(cmd.split(" "));
      builder.directory(new File("."));
      Process process = builder.start();
      StreamGobbler streamGobbler =
          new StreamGobbler(process.getInputStream(), System.out::println);
      Executors.newSingleThreadExecutor().submit(streamGobbler);
      int exitCode = process.waitFor();
      Assertions.assertEquals(0, exitCode);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Listener createListener(RepositoryType type) {
    Listener listener = getRepositoryFactory(type).createListener();
    this.helper.get(listener.open());
    return listener;
  }

  /**
   * Method that create a {@link RepositoryFactory} based on the {@link RepositoryType} if
   * necessary. The created repository factories are being cached for performance and multithread
   * testing.
   */
  public static RepositoryFactory getRepositoryFactory(RepositoryType type) {
    return repositoryFactoryMap.computeIfAbsent(type, BaseIntegrationTest::createRepositoryFactory);
  }

  /** Method that creates a {@link RepositoryFactory} based on the {@link RepositoryType}. */
  private static RepositoryFactory createRepositoryFactory(RepositoryType type) {

    switch (type) {
      case VERTX:
        return new RepositoryFactoryVertxImpl(getApiUrl());
      case OKHTTP:
        return new RepositoryFactoryOkHttpImpl(getApiUrl());
      default:
        throw new IllegalStateException("Invalid Repository type " + type);
    }
  }

  public static Config config() {
    return helper.config();
  }

  public JsonHelper jsonHelper() {
    return this.jsonHelper;
  }

  public static String getApiUrl() {
    return config().getApiUrl();
  }

  public NetworkType getNetworkType() {
    return this.networkType;
  }

  public Account getTestAccount() {
    return this.config().getTestAccount();
  }

  public PublicAccount getTestPublicAccount() {
    return this.config().getTestAccount().getPublicAccount();
  }

  public Address getRecipient() {
    return this.config().getTestAccount2().getAddress();
  }

  public String getGenerationHash() {
    return generationHash;
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
  protected static <T> T get(Observable<T> observable) {
    return BaseIntegrationTest.helper.get(observable);
  }

  /** Method that creates a {@link RepositoryFactory} based on the {@link RepositoryType}. */
  public Listener getListener(RepositoryType type) {
    return listenerMap.computeIfAbsent(type, this::createListener);
  }

  <T extends Transaction> Pair<T, AggregateTransaction> announceAggregateAndValidate(
      RepositoryType type, T transaction, Account... signers) {

    Assertions.assertTrue(signers.length > 0);

    System.out.println(
        "Announcing Aggregate Transaction: "
            + transaction.getType()
            + " address: "
            + Arrays.stream(signers)
                .map(s -> s.getAddress().plain())
                .collect(Collectors.joining(", ")));
    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Arrays.stream(signers)
                    .map(s -> transaction.toAggregate(s.getPublicAccount()))
                    .collect(Collectors.toList()))
            .maxFee(this.maxFee)
            .build();

    AggregateTransaction announcedAggregateTransaction =
        announceAndValidate(type, signers[0], aggregateTransaction);
    T announcedCorrectly = (T) announcedAggregateTransaction.getInnerTransactions().get(0);
    System.out.println(
        "Transaction completed, Transaction hash "
            + announcedAggregateTransaction.getTransactionInfo().get().getHash().get());
    return Pair.of(announcedCorrectly, announcedAggregateTransaction);
  }

  <T extends Transaction> T announceAndValidate(
      RepositoryType type, Account testAccount, T transaction) {

    SignedTransaction signedTransaction = testAccount.sign(transaction, getGenerationHash());
    if (transaction.getType() != TransactionType.AGGREGATE_COMPLETE) {
      System.out.println(
          "Announcing Transaction Transaction: "
              + transaction.getType()
              + " Address: "
              + testAccount.getAddress().plain()
              + " Public Key: "
              + testAccount.getPublicAccount().getPublicKey().toHex()
              + " hash "
              + signedTransaction.getHash());
    }
    TransactionService transactionService = getTransactionService(type);
    Transaction announceCorrectly =
        getTransactionOrFail(
            transactionService.announce(getListener(type), signedTransaction), transaction);
    Assertions.assertEquals(
        signedTransaction.getHash(), announceCorrectly.getTransactionInfo().get().getHash().get());
    Assertions.assertEquals(announceCorrectly.getType(), transaction.getType());
    if (transaction.getType() != TransactionType.AGGREGATE_COMPLETE) {
      System.out.println("Transaction completed, Transaction hash " + signedTransaction.getHash());
    }
    return (T) announceCorrectly;
  }

  protected TransactionService getTransactionService(RepositoryType type) {
    return new TransactionServiceImpl(getRepositoryFactory(type));
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
    try {
      return get(observable.take(1));
    } catch (Exception e) {
      throw new IllegalArgumentException(
          e.getMessage() + ". Failed Transaction json: \n" + toJson(originalTransaction), e);
    }
  }

  protected String toJson(Transaction originalTransaction) {
    return getRepositoryFactory(DEFAULT_REPOSITORY_TYPE)
        .createJsonSerialization()
        .transactionToJson(originalTransaction);
  }

  protected String toJson(Object anyObject) {
    return jsonHelper.prettyPrint(anyObject);
  }

  @SuppressWarnings("squid:S2925")
  protected void sleep(long time) {
    try {
      System.out.println("Sleeping for " + time);
      Thread.sleep(time);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  public NetworkCurrency getNetworkCurrency() {
    return networkCurrency;
  }

  protected NamespaceId setAddressAlias(
      RepositoryType type, Address address, String namespaceName) {

    NamespaceId namespaceId = NamespaceId.createFromName(namespaceName);

    Account nemesisAccount = config().getNemesisAccount1();

    List<AccountNames> accountNames =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getAccountsNames(Collections.singletonList(address)));

    if (accountNames.stream()
        .anyMatch(
            an ->
                an.getNames().stream()
                    .anyMatch(
                        ns ->
                            ns.getName().equals(namespaceName)
                                && ns.getNamespaceId().equals(namespaceId)))) {
      System.out.println(namespaceName + " ADDRESS Alias found, reusing it.");
      return namespaceId;

    } else {
      System.out.println(namespaceName + " ADDRESS Alias not found, CREATING MOSAIC ALIAS");
    }

    System.out.println("Setting up namespace " + namespaceName);
    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(), namespaceName, BigInteger.valueOf(100))
            .maxFee(this.maxFee)
            .build();

    NamespaceId rootNamespaceId =
        announceAggregateAndValidate(type, namespaceRegistrationTransaction, nemesisAccount)
            .getLeft()
            .getNamespaceId();

    System.out.println("Setting account alias " + address.plain() + " alias: " + namespaceName);
    AddressAliasTransaction aliasTransaction =
        AddressAliasTransactionFactory.create(
                getNetworkType(), AliasAction.LINK, rootNamespaceId, address)
            .maxFee(this.maxFee)
            .build();

    announceAggregateAndValidate(type, aliasTransaction, nemesisAccount);
    return rootNamespaceId;
  }

  protected NamespaceId setMosaicAlias(
      RepositoryType type, MosaicId mosaicId, String namespaceName) {
    Account nemesisAccount = config().getNemesisAccount1();
    NamespaceId namespaceId = NamespaceId.createFromName(namespaceName);
    List<MosaicNames> mosaicNames =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getMosaicsNames(Collections.singletonList(mosaicId)));

    if (mosaicNames.stream()
        .anyMatch(
            an ->
                an.getNames().stream()
                    .anyMatch(
                        ns ->
                            ns.getName().equals(namespaceName)
                                && ns.getNamespaceId().equals(namespaceId)))) {
      System.out.println(namespaceName + " MOSAIC Alias found, reusing it.");
      return namespaceId;
    } else {
      System.out.println(namespaceName + " MOSAIC Alias not found, CREATING MOSAIC ALIAS");
    }

    System.out.println("Setting up namespace " + namespaceName);
    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(), namespaceName, BigInteger.valueOf(100))
            .maxFee(this.maxFee)
            .build();

    NamespaceId rootNamespaceId =
        announceAggregateAndValidate(type, namespaceRegistrationTransaction, nemesisAccount)
            .getLeft()
            .getNamespaceId();

    System.out.println(
        "Setting mosaic alias " + mosaicId.getIdAsHex() + " alias: " + namespaceName);

    MosaicAliasTransaction aliasTransaction =
        MosaicAliasTransactionFactory.create(
                getNetworkType(), AliasAction.LINK, rootNamespaceId, mosaicId)
            .maxFee(this.maxFee)
            .build();

    announceAggregateAndValidate(type, aliasTransaction, nemesisAccount);
    return rootNamespaceId;
  }

  protected MosaicId createMosaic(
      Account account, RepositoryType type, BigInteger initialSupply, String alias) {
    MosaicNonce nonce = MosaicNonce.createRandom();
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, account.getPublicAccount());

    MosaicDefinitionTransaction mosaicDefinitionTransaction =
        MosaicDefinitionTransactionFactory.create(
                getNetworkType(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                4,
                new BlockDuration(100))
            .maxFee(this.maxFee)
            .build();

    MosaicDefinitionTransaction validateTransaction =
        announceAndValidate(type, account, mosaicDefinitionTransaction);
    Assertions.assertEquals(mosaicId, validateTransaction.getMosaicId());
    UnresolvedMosaicId unresolvedMosaicId = mosaicId;

    if (alias != null) {

      NamespaceId rootNamespaceId = createRootNamespace(type, account, alias);
      unresolvedMosaicId = rootNamespaceId;

      MosaicAliasTransaction addressAliasTransaction =
          MosaicAliasTransactionFactory.create(
                  getNetworkType(), AliasAction.LINK, rootNamespaceId, mosaicId)
              .maxFee(this.maxFee)
              .build();

      announceAggregateAndValidate(type, addressAliasTransaction, account);
    }

    if (initialSupply != null && initialSupply.longValue() > 0) {
      MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
          MosaicSupplyChangeTransactionFactory.create(
                  getNetworkType(),
                  unresolvedMosaicId,
                  MosaicSupplyChangeActionType.INCREASE,
                  initialSupply)
              .maxFee(this.maxFee)
              .build();
      announceAndValidate(type, account, mosaicSupplyChangeTransaction);
    }

    return mosaicId;
  }

  public NamespaceId createRootNamespace(
      RepositoryType type, Account testAccount, String namespaceName) {

    System.out.println("Creating namespace " + namespaceName);
    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(), namespaceName, BigInteger.valueOf(10))
            .maxFee(this.maxFee)
            .build();

    NamespaceRegistrationTransaction processedTransaction =
        announceAndValidate(type, testAccount, namespaceRegistrationTransaction);

    Assertions.assertEquals(
        namespaceRegistrationTransaction.getNamespaceId().getIdAsHex(),
        processedTransaction.getNamespaceId().getIdAsHex());

    Assertions.assertEquals(
        namespaceRegistrationTransaction.getNamespaceName(),
        processedTransaction.getNamespaceName());
    return namespaceRegistrationTransaction.getNamespaceId();
  }
}
