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
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.HashLockRepository;
import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.infrastructure.okhttp.RepositoryFactoryOkHttpImpl;
import io.nem.symbol.sdk.infrastructure.vertx.JsonHelperJackson2;
import io.nem.symbol.sdk.infrastructure.vertx.RepositoryFactoryVertxImpl;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountNames;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransaction;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LockStatus;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.io.File;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;

public class TestHelper {

  public static final long AMOUNT_PER_TRANSFER = 100000000;
  public static final long MIN_AMOUNT_PER_TRANSFER = 1000000;
  protected static final RepositoryType DEFAULT_REPOSITORY_TYPE = RepositoryType.VERTX;
  private final Map<RepositoryType, RepositoryFactory> repositoryFactoryMap = new HashMap<>();
  private final Map<RepositoryType, Listener> listenerMap = new HashMap<>();
  private final String generationHash;
  private final NetworkType networkType;
  private final Currency currency;
  private final JsonHelper jsonHelper =
      new JsonHelperJackson2(JsonHelperJackson2.configureMapper(new ObjectMapper()));
  private final Config config;
  private final Duration epochAdjustment;
  protected BigInteger maxFee = BigInteger.valueOf(10000000);

  public TestHelper() {
    this.config = new Config();
    System.out.println("Running tests against server: " + config().getApiUrl());
    this.generationHash = resolveGenerationHash();
    this.currency = resolveNetworkCurrency();
    this.networkType = resolveNetworkType();
    this.epochAdjustment = resolveEpochAdjustment();
    this.config.init(this.networkType);

    System.out.println("Network Type: " + networkType + " " + networkType.getValue());
    System.out.println("Generation Hash: " + generationHash);
  }

  public Config config() {
    return config;
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
  public <T> T get(Observable<T> observable) {
    return get(observable.toFuture());
  }

  /**
   * An utility method that executes a rest call though the Observable. It simplifies and unifies
   * the executions of rest calls.
   *
   * <p>This methods adds the necessary timeouts and exception handling,
   *
   * @param future the future, typically the one that performs a rest call.
   * @param <T> the future type
   * @return the response from the rest call.
   */
  public <T> T get(Future<T> future) {
    return ExceptionUtils.propagate(() -> future.get(config.getTimeoutSeconds(), TimeUnit.SECONDS));
  }

  <T extends Transaction> Pair<T, AggregateTransaction> announceAggregateAndValidate(
      RepositoryType type, T transaction, Account signer, Account... cosigners) {

    System.out.println(
        "Announcing Aggregate Transaction: "
            + transaction.getType()
            + " signer "
            + signer.getAddress().plain()
            + " cosigners: "
            + Arrays.stream(cosigners)
                .map(s -> s.getAddress().plain())
                .collect(Collectors.joining(", ")));

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(transaction.toAggregate(signer.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedTransaction =
        signer.signTransactionWithCosignatories(
            aggregateTransaction, Arrays.asList(cosigners), getGenerationHash());
    if (transaction.getType() != TransactionType.AGGREGATE_COMPLETE) {
      System.out.println(
          "Announcing Transaction Transaction: "
              + transaction.getType()
              + " Address: "
              + signer.getAddress().plain()
              + " Public Key: "
              + signer.getPublicAccount().getPublicKey().toHex()
              + " hash "
              + signedTransaction.getHash());
    }
    TransactionService transactionService = new TransactionServiceImpl(getRepositoryFactory(type));
    AggregateTransaction announcedAggregateTransaction =
        (AggregateTransaction)
            getTransactionOrFail(
                transactionService.announce(getListener(type), signedTransaction), transaction);

    T announcedCorrectly = (T) announcedAggregateTransaction.getInnerTransactions().get(0);
    System.out.println(
        "Transaction completed, Transaction hash "
            + announcedAggregateTransaction.getTransactionInfo().get().getHash().get());
    return Pair.of(announcedCorrectly, announcedAggregateTransaction);
  }

  <T extends Transaction> T announceAndValidate(
      RepositoryType type, Account testAccount, T transaction) {

    SignedTransaction signedTransaction = testAccount.sign(transaction, getGenerationHash());
    System.out.println(
        "Announcing Transaction Transaction: "
            + transaction.getType()
            + " Address: "
            + testAccount.getAddress().plain()
            + " Public Key: "
            + testAccount.getPublicAccount().getPublicKey().toHex()
            + " hash "
            + signedTransaction.getHash());
    TransactionService transactionService = new TransactionServiceImpl(getRepositoryFactory(type));
    Transaction announceCorrectly =
        getTransactionOrFail(
            transactionService.announce(getListener(type), signedTransaction), transaction);
    Assertions.assertEquals(
        signedTransaction.getHash(), announceCorrectly.getTransactionInfo().get().getHash().get());
    Assertions.assertEquals(announceCorrectly.getType(), transaction.getType());
    System.out.println("Transaction completed, Transaction hash " + signedTransaction.getHash());
    return (T) announceCorrectly;
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

  private void runCommand(String cmd) {
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

  protected boolean hasMosaic(Address recipient) {
    try {
      AccountInfo accountInfo =
          get(
              getRepositoryFactory(DEFAULT_REPOSITORY_TYPE)
                  .createAccountRepository()
                  .getAccountInfo(recipient));
      return accountInfo.getMosaics().stream()
          .anyMatch(
              m ->
                  currency.getMosaicId().get().equals(m.getId())
                      && m.getAmount().longValue() >= MIN_AMOUNT_PER_TRANSFER);
    } catch (RepositoryCallException e) {
      return false;
    }
  }

  public Listener getListener(RepositoryType type) {
    return listenerMap.computeIfAbsent(type, this::createListener);
  }

  private Listener createListener(RepositoryType type) {
    Listener listener = getRepositoryFactory(type).createListener();
    get(listener.open());
    return listener;
  }

  public void assertById(
      TransactionRepository transactionRepository,
      TransactionGroup group,
      List<Transaction> transactions) {
    if (transactions.isEmpty()) {
      return;
    }

    transactions.forEach(
        t -> {
          Assertions.assertNotNull(
              get(transactionRepository.getTransaction(group, t.getRecordId().get())));
        });

    transactions.forEach(
        t -> {
          Assertions.assertNotNull(
              get(
                  transactionRepository.getTransaction(
                      group, t.getTransactionInfo().get().getHash().get())));
        });

    List<Transaction> transactionsByIds =
        get(
            transactionRepository.getTransactions(
                group,
                transactions.stream()
                    .map(t -> t.getRecordId().get())
                    .collect(Collectors.toList())));
    assertSameRecordList(transactionsByIds, transactions);

    List<Transaction> transactionsByHashes =
        get(
            transactionRepository.getTransactions(
                group,
                transactions.stream()
                    .map(t -> t.getTransactionInfo().get().getHash().get())
                    .collect(Collectors.toList())));
    assertSameRecordList(transactionsByHashes, transactions);
  }

  public <T extends Stored> void assertSameRecordList(List<T> list1, List<T> list2) {
    Set<String> records1 =
        list1.stream().map(r -> r.getRecordId().get()).collect(Collectors.toSet());
    Set<String> records2 =
        list2.stream().map(r -> r.getRecordId().get()).collect(Collectors.toSet());
    Assertions.assertEquals(records1, records2);
  }

  private Currency resolveNetworkCurrency() {
    return get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).getNetworkCurrency());
  }

  private String resolveGenerationHash() {
    return get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).getGenerationHash());
  }

  private NetworkType resolveNetworkType() {
    return get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).getNetworkType());
  }

  private Duration resolveEpochAdjustment() {
    return get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).getEpochAdjustment());
  }

  public RepositoryFactory getRepositoryFactory(RepositoryType type) {
    return repositoryFactoryMap.computeIfAbsent(type, this::createRepositoryFactory);
  }

  private RepositoryFactory createRepositoryFactory(RepositoryType type) {

    switch (type) {
      case VERTX:
        return new RepositoryFactoryVertxImpl(getApiUrl());
      case OKHTTP:
        return new RepositoryFactoryOkHttpImpl(getApiUrl());
      default:
        throw new IllegalStateException("Invalid Repository type " + type);
    }
  }

  public String getApiUrl() {
    return config().getApiUrl();
  }

  public String getGenerationHash() {
    return generationHash;
  }

  public NetworkType getNetworkType() {
    return networkType;
  }

  public Currency getCurrency() {
    return currency;
  }

  public JsonHelper getJsonHelper() {
    return jsonHelper;
  }

  protected String toJson(Transaction originalTransaction) {
    return getRepositoryFactory(DEFAULT_REPOSITORY_TYPE)
        .createJsonSerialization()
        .transactionToJson(originalTransaction);
  }

  protected String toJson(Object anyObject) {
    return jsonHelper.prettyPrint(anyObject);
  }

  protected boolean isAlias(RepositoryType type, Address address, NamespaceId namespaceId) {
    List<AccountNames> accountNames =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getAccountsNames(Collections.singletonList(address)));

    return (accountNames.stream()
        .anyMatch(
            an ->
                an.getNames().stream()
                    .anyMatch(
                        ns ->
                            namespaceId.getFullName().map(f -> ns.getName().equals(f)).orElse(false)
                                && ns.getNamespaceId().equals(namespaceId))));
  }

  public NamespaceId setAddressAlias(RepositoryType type, Address address, String namespaceName) {

    NamespaceId namespaceId = NamespaceId.createFromName(namespaceName);

    if (isAlias(type, address, namespaceId)) {
      System.out.println(
          namespaceName + " ADDRESS Alias for address " + address.plain() + " found, reusing it.");
      return namespaceId;
    }
    System.out.println(
        namespaceName + " ADDRESS Alias not found, CREATING ADDRESS " + address.plain() + " ALIAS");

    Account nemesisAccount = config().getNemesisAccount1();

    System.out.println("Setting up namespace " + namespaceName);
    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(), getDeadline(), namespaceName, getDuration())
            .maxFee(maxFee)
            .build();

    NamespaceId rootNamespaceId =
        announceAggregateAndValidate(type, namespaceRegistrationTransaction, nemesisAccount)
            .getLeft()
            .getNamespaceId();

    System.out.println("Setting account alias " + address.plain() + " alias: " + namespaceName);
    AddressAliasTransaction aliasTransaction =
        AddressAliasTransactionFactory.create(
                getNetworkType(), getDeadline(), AliasAction.LINK, rootNamespaceId, address)
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, aliasTransaction, nemesisAccount);

    int retry = 10;
    while (!isAlias(type, address, namespaceId)) {
      sleep(300);
      retry--;
      if (retry == 0) {
        Assertions.fail("Could not create " + address.plain() + " alias: " + namespaceName);
      }
    }

    return rootNamespaceId;
  }

  protected boolean isAlias(RepositoryType type, MosaicId mosaicId, NamespaceId namespaceId) {
    List<MosaicNames> mosaicNames =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getMosaicsNames(Collections.singletonList(mosaicId)));

    return (mosaicNames.stream()
        .anyMatch(
            an ->
                an.getNames().stream()
                    .anyMatch(
                        ns ->
                            namespaceId.getFullName().map(f -> ns.getName().equals(f)).orElse(false)
                                && ns.getNamespaceId().equals(namespaceId))));
  }

  protected NamespaceId setMosaicAlias(
      RepositoryType type, MosaicId mosaicId, String namespaceName) {
    Account nemesisAccount = config().getNemesisAccount1();
    NamespaceId namespaceId = NamespaceId.createFromName(namespaceName);
    if (isAlias(type, mosaicId, namespaceId)) {
      System.out.println(namespaceName + " MOSAIC Alias found, reusing it.");
      return namespaceId;
    } else {
      System.out.println(namespaceName + " MOSAIC Alias not found, CREATING MOSAIC ALIAS");
    }

    System.out.println("Setting up namespace " + namespaceName);
    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(), getDeadline(), namespaceName, getDuration())
            .maxFee(maxFee)
            .build();

    NamespaceId rootNamespaceId =
        announceAggregateAndValidate(type, namespaceRegistrationTransaction, nemesisAccount)
            .getLeft()
            .getNamespaceId();

    System.out.println(
        "Setting mosaic alias " + mosaicId.getIdAsHex() + " alias: " + namespaceName);

    MosaicAliasTransaction aliasTransaction =
        MosaicAliasTransactionFactory.create(
                getNetworkType(), getDeadline(), AliasAction.LINK, rootNamespaceId, mosaicId)
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, aliasTransaction, nemesisAccount);

    int retry = 10;
    while (!isAlias(type, mosaicId, namespaceId)) {
      sleep(300);
      retry--;
      if (retry == 0) {
        Assertions.fail("Could not create " + mosaicId.getIdAsHex() + " alias: " + namespaceName);
      }
    }
    return rootNamespaceId;
  }

  protected MosaicId createMosaic(
      Account account, RepositoryType type, BigInteger initialSupply, String alias) {
    MosaicNonce nonce = MosaicNonce.createRandom();
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, account.getPublicAccount());

    MosaicDefinitionTransaction mosaicDefinitionTransaction =
        MosaicDefinitionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                4,
                new BlockDuration(100))
            .maxFee(maxFee)
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
                  getNetworkType(), getDeadline(), AliasAction.LINK, rootNamespaceId, mosaicId)
              .maxFee(maxFee)
              .build();

      announceAggregateAndValidate(type, addressAliasTransaction, account);
    }

    if (initialSupply != null && initialSupply.longValue() > 0) {
      MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
          MosaicSupplyChangeTransactionFactory.create(
                  getNetworkType(),
                  getDeadline(),
                  unresolvedMosaicId,
                  MosaicSupplyChangeActionType.INCREASE,
                  initialSupply)
              .maxFee(maxFee)
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
                getNetworkType(), getDeadline(), namespaceName, getDuration())
            .maxFee(maxFee)
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

  public BigInteger getDuration() {
    return BigInteger.valueOf(10000);
  }

  public boolean isMultisig(RepositoryType type, Account multisigAccount) {
    try {
      MultisigRepository multisigRepository = getRepositoryFactory(type).createMultisigRepository();
      MultisigAccountInfo multisigAccountInfo =
          get(multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));
      return multisigAccountInfo != null;
    } catch (RepositoryCallException e) {
      Assertions.assertEquals(404, e.getStatusCode());
      return false;
    }
  }

  public Pair<Account, NamespaceId> getMultisigAccount(RepositoryType type) {
    Account multisigAccount = config().getMultisigAccount();
    sendMosaicFromNemesis(type, multisigAccount.getAddress(), false);
    NamespaceId namespaceId =
        setAddressAlias(type, multisigAccount.getAddress(), "multisigaccount");
    this.createMultisigAccountBonded(
        type, multisigAccount, config().getCosignatoryAccount(), config().getCosignatory2Account());
    return Pair.of(multisigAccount, namespaceId);
  }

  public MultisigAccountInfo createMultisigAccountBonded(
      RepositoryType type, Account multisigAccount, Account... accounts) {

    AccountRepository accountRepository = getRepositoryFactory(type).createAccountRepository();
    MultisigRepository multisigRepository = getRepositoryFactory(type).createMultisigRepository();
    AccountInfo accountInfo = get(accountRepository.getAccountInfo(multisigAccount.getAddress()));
    System.out.println(getJsonHelper().print(accountInfo));

    if (isMultisig(type, multisigAccount)) {
      System.out.println(
          "Multisig account with address "
              + multisigAccount.getAddress().plain()
              + " already exist");
      return get(multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));
    }
    System.out.println(
        "Multisig account with address "
            + multisigAccount.getAddress().plain()
            + " does not exist. Creating");

    System.out.println("Creating multisg account " + multisigAccount.getAddress().plain());
    List<UnresolvedAddress> additions =
        Arrays.stream(accounts).map(Account::getAddress).collect(Collectors.toList());
    MultisigAccountModificationTransaction convertIntoMultisigTransaction =
        MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                (byte) 1,
                (byte) 1,
                additions,
                Collections.emptyList())
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createBonded(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    convertIntoMultisigTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount, Arrays.asList(accounts), getGenerationHash());

    Mosaic hashAmount = getCurrency().createRelative(BigInteger.valueOf(10));
    HashLockTransaction hashLockTransaction =
        HashLockTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                hashAmount,
                BigInteger.valueOf(100),
                signedAggregateTransaction)
            .maxFee(maxFee)
            .build();
    SignedTransaction signedHashLockTransaction =
        hashLockTransaction.signWith(multisigAccount, getGenerationHash());

    getTransactionOrFail(
        getTransactionService(type)
            .announceHashLockAggregateBonded(
                getListener(type), signedHashLockTransaction, signedAggregateTransaction),
        aggregateTransaction);

    HashLockRepository hashLockRepository = getRepositoryFactory(type).createHashLockRepository();

    HashLockInfo hashLockInfo = get(hashLockRepository.getHashLock(hashLockTransaction.getHash()));
    Assertions.assertNotNull(hashLockInfo);
    Assertions.assertEquals(multisigAccount.getAddress(), hashLockInfo.getOwnerAddress());
    Assertions.assertEquals(hashAmount.getAmount(), hashLockInfo.getAmount());
    Assertions.assertEquals(LockStatus.UNUSED, hashLockInfo.getStatus());
    Assertions.assertEquals(hashLockTransaction.getHash(), hashLockInfo.getHash());

    Page<HashLockInfo> page =
        get(
            hashLockRepository.search(
                new HashLockSearchCriteria().address(multisigAccount.getAddress())));
    Assertions.assertTrue(
        page.getData().stream().anyMatch(m -> m.getHash().equals(hashLockTransaction.getHash())));
    Assertions.assertEquals(20, page.getPageSize());
    sleep(1000);
    return get(multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));
  }

  public MultisigAccountInfo createMultisigAccountComplete(
      RepositoryType type, Account multisigAccount, Account... accounts) {

    AccountRepository accountRepository = getRepositoryFactory(type).createAccountRepository();

    MultisigRepository multisigRepository = getRepositoryFactory(type).createMultisigRepository();

    AccountInfo accountInfo = get(accountRepository.getAccountInfo(multisigAccount.getAddress()));
    System.out.println(getJsonHelper().print(accountInfo));

    try {
      MultisigAccountInfo multisigAccountInfo =
          get(multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));

      System.out.println(
          "Multisig account with address "
              + multisigAccount.getAddress().plain()
              + " already exist");
      System.out.println(getJsonHelper().print(multisigAccountInfo));
      return multisigAccountInfo;
    } catch (RepositoryCallException e) {
      System.out.println(
          "Multisig account with address "
              + multisigAccount.getAddress().plain()
              + " does not exist. Creating");
    }

    System.out.println("Creating multisg account " + multisigAccount.getAddress().plain());
    List<UnresolvedAddress> additions =
        Arrays.stream(accounts).map(Account::getAddress).collect(Collectors.toList());
    MultisigAccountModificationTransaction convertIntoMultisigTransaction =
        MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                (byte) 1,
                (byte) 1,
                additions,
                Collections.emptyList())
            .maxFee(maxFee)
            .build();

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(
                    convertIntoMultisigTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    SignedTransaction signedAggregateTransaction =
        aggregateTransaction.signTransactionWithCosigners(
            multisigAccount, Arrays.asList(accounts), getGenerationHash());

    Transaction aggregateTransaciton =
        get(getTransactionService(type).announce(getListener(type), signedAggregateTransaction));

    sleep(1000);

    Assertions.assertEquals(
        aggregateTransaciton.getTransactionInfo().get().getHash().get(),
        signedAggregateTransaction.getHash());
    return get(multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));
  }

  public TransactionService getTransactionService(RepositoryType type) {
    return new TransactionServiceImpl(getRepositoryFactory(type));
  }

  protected void sendMosaicFromNemesis(RepositoryType type, Address recipient, boolean force) {
    if (hasMosaic(recipient) && !force) {
      System.out.println("Ignoring recipient. It has the currency token already: ");
      printAccount(recipient);
      return;
    }
    System.out.println("Sending " + AMOUNT_PER_TRANSFER + " currency tokens to: ");
    printAccount(recipient);
    basicSendMosaicFromNemesis(type, recipient);
  }

  public void basicSendMosaicFromNemesis(RepositoryType type, UnresolvedAddress recipient) {
    BigInteger amount = BigInteger.valueOf(AMOUNT_PER_TRANSFER);
    basicTransfer(type, config().getNemesisAccount(), recipient, amount);
  }

  public void basicTransfer(
      RepositoryType type, Account nemesisAccount, UnresolvedAddress recipient, BigInteger amount) {

    TransferTransactionFactory factory =
        TransferTransactionFactory.create(
            getNetworkType(),
            getDeadline(),
            recipient,
            Collections.singletonList(getCurrency().createAbsolute(amount)));

    factory.maxFee(maxFee);
    TransferTransaction transferTransaction = factory.build();

    TransferTransaction processedTransaction =
        announceAndValidate(type, nemesisAccount, transferTransaction);
    Assertions.assertEquals(amount, processedTransaction.getMosaics().get(0).getAmount());
  }

  void printAccount(Address account) {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("address", account.plain());
    System.out.println(getJsonHelper().print(map));
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

  public Account createTestAccount(RepositoryType type) {
    Account testAccount = Account.generateNewAccount(this.networkType);
    sendMosaicFromNemesis(type, testAccount.getAddress(), false);
    return testAccount;
  }

  public Pair<Account, NamespaceId> getTestAccount(RepositoryType type) {
    Account testAccount = config().getTestAccount();
    NamespaceId namespaceId = setAddressAlias(type, testAccount.getAddress(), "testaccount");
    sendMosaicFromNemesis(type, testAccount.getAddress(), false);
    return Pair.of(testAccount, namespaceId);
  }

  public Deadline getDeadline() {
    return Deadline.create(this.epochAdjustment);
  }
}
