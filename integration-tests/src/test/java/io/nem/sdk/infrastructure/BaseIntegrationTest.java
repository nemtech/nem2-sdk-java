/*
 * Copyright 2018 NEM
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.core.utils.ExceptionUtils;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.infrastructure.okhttp.RepositoryFactoryOkHttpImpl;
import io.nem.sdk.infrastructure.vertx.JsonHelperJackson2;
import io.nem.sdk.infrastructure.vertx.RepositoryFactoryVertxImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.nem.sdk.model.transaction.TransactionType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract class for all the repository integration tests.
 *
 * In general, the test ares parametrized so multiple implementations of a repository can be tested
 * at the same time.
 */
public abstract class BaseIntegrationTest {

    /**
     * The default repository type used when you are not testing the different implementations.
     */
    protected static final RepositoryType DEFAULT_REPOSITORY_TYPE = RepositoryType.VERTX;

    /**
     * Known implementations of repositories that the integration tests use.
     */
    public enum RepositoryType {
        VERTX, OKHTTP
    }

    private static final Config CONFIG = Config.getInstance();
    private final NetworkType networkType = this.config().getNetworkType();
    private final Long timeoutSeconds = this.config().getTimeoutSeconds();
    private final Map<RepositoryType, RepositoryFactory> repositoryFactoryMap = new HashMap<>();
    private final Map<RepositoryType, Listener> listenerMap = new HashMap<>();
    private final JsonHelper jsonHelper = new JsonHelperJackson2(
        JsonHelperJackson2.configureMapper(new ObjectMapper()));
    private String generationHash = this.config().getGenerationHash();

    @BeforeAll
    void setUp() {
        System.out.println("Running tests against server: " + config().getApiUrl());
        generationHash = resolveGenerationHash();
        System.out.println("Generation Hash: " + generationHash);
    }

    @BeforeEach
    void coolDown() throws InterruptedException {
        //To avoid rate-limiting errors from server. (5 per seconds)
        sleep(500);
    }

    private String resolveGenerationHash() {
        return Optional.ofNullable(this.config().getGenerationHash()).orElseGet(
            () -> get(getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).createBlockRepository()
                .getBlockByHeight(
                    BigInteger.ONE)).getGenerationHash());

    }

    @AfterAll
    void tearDown() {
        listenerMap.values().forEach(Listener::close);
        repositoryFactoryMap.values().forEach(RepositoryFactory::close);
    }

    private Listener createListener(RepositoryType type) {
        Listener listener = getRepositoryFactory(type).createListener();
        try {
            listener.open().get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Listener could not be created or opened. Error "
                    + org.apache.commons.lang3.exception.ExceptionUtils
                    .getMessage(e),
                e);
        }
        return listener;
    }

    /**
     * Method that create a {@link RepositoryFactory} based on the {@link RepositoryType} if
     * necessary. The created repository factories are being cached for performance and multithread
     * testing.
     */
    public RepositoryFactory getRepositoryFactory(RepositoryType type) {
        return repositoryFactoryMap.computeIfAbsent(type, this::createRepositoryFactory);
    }

    /**
     * Method that creates a {@link RepositoryFactory} based on the {@link RepositoryType}.
     */
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

    public Config config() {
        return BaseIntegrationTest.CONFIG;
    }

    public JsonHelper jsonHelper() {
        return this.jsonHelper;
    }

    public String getApiUrl() {
        return this.config().getApiUrl();
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

    public Address getTestAccountAddress() {
        return this.config().getTestAccount().getAddress();
    }

    public Account getTestMultisigAccount() {
        return this.config().getMultisigAccount();
    }

    public Account getTestCosignatoryAccount() {
        return this.config().getCosignatoryAccount();
    }

    public Account getTestCosignatoryAccount2() {
        return this.config().getCosignatory2Account();
    }

    public Address getRecipient() {
        return this.config().getTestAccount2().getAddress();
    }


    public String getGenerationHash() {
        return generationHash;
    }

    public Long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * An utility method that executes a rest call though the Observable. It simplifies and unifies
     * the executions of rest calls.
     *
     * This methods adds the necessary timeouts and exception handling,
     *
     * @param observable the observable, typically the one that performs a rest call.
     * @param <T> the observable type
     * @return the response from the rest call.
     */
    protected <T> T get(Observable<T> observable) {
        return ExceptionUtils
            .propagate(() -> observable.toFuture().get(getTimeoutSeconds(), TimeUnit.SECONDS));
    }

    /**
     * Method that creates a {@link RepositoryFactory} based on the {@link RepositoryType}.
     */
    public Listener getListener(RepositoryType type) {
        return listenerMap.computeIfAbsent(type, this::createListener);
    }


    <T extends Transaction> T announceAggregateAndValidate(RepositoryType type, Account testAccount,
        T transaction) {

        System.out.println(
            "Announcing Aggregate Transaction address: " + testAccount.getAddress().pretty()
                + " Transaction: " + transaction.getType());
        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    transaction.toAggregate(testAccount.getPublicAccount()))
            ).build();

        T announcedCorrectly = (T) announceAndValidate(
            type, testAccount, aggregateTransaction).getInnerTransactions().get(0);
        System.out.println("Transaction completed");
        return announcedCorrectly;
    }


    <T extends Transaction> T announceAndValidate(RepositoryType type, Account testAccount,
        T transaction) {

        if (transaction.getType() != TransactionType.AGGREGATE_COMPLETE) {
            System.out
                .println("Announcing Transaction address: " + testAccount.getAddress().pretty()
                    + " Transaction: " + transaction.getType());
        }
        SignedTransaction signedTransaction = testAccount
            .sign(transaction, getGenerationHash());

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getRepositoryFactory(type).createTransactionRepository()
                .announce(signedTransaction));
        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        Transaction announceCorrectly = this
            .validateTransactionAnnounceCorrectly(
                testAccount.getAddress(), signedTransaction.getHash(), type);
        Assertions.assertEquals(announceCorrectly.getType(), transaction.getType());
        if (transaction.getType() != TransactionType.AGGREGATE_COMPLETE) {
            System.out.println("Transaction completed");
        }
        return (T) announceCorrectly;
    }


    Transaction validateTransactionAnnounceCorrectly(Address address, String transactionHash,
        RepositoryType type) {
        Listener listener = getListener(type);
        Observable<Transaction> observable = listener.confirmed(address)
            .filter(t -> t.getTransactionInfo().flatMap(TransactionInfo::getHash)
                .filter(s -> s.equals(transactionHash)).isPresent());
        Transaction transaction = getTransactionOrFail(address, listener, observable);
        assertEquals(transactionHash, transaction.getTransactionInfo().get().getHash().get());
        return transaction;
    }


    AggregateTransaction validateAggregateBondedTransactionAnnounceCorrectly(Address address,
        String transactionHash, RepositoryType type) {
        Listener listener = getListener(type);
        Observable<AggregateTransaction> observable = listener.aggregateBondedAdded(address)
            .filter(t -> t.getTransactionInfo().flatMap(TransactionInfo::getHash)
                .filter(s -> s.equals(transactionHash)).isPresent());
        AggregateTransaction aggregateTransaction = getTransactionOrFail(address, listener,
            observable);
        assertEquals(transactionHash,
            aggregateTransaction.getTransactionInfo().get().getHash().get());
        return aggregateTransaction;
    }

    CosignatureSignedTransaction validateAggregateBondedCosignatureTransactionAnnounceCorrectly(
        Address address, String transactionHash,
        RepositoryType type) {
        Listener listener = getListener(type);
        Observable<CosignatureSignedTransaction> observable = listener.cosignatureAdded(address)
            .filter(t -> t.getParentHash().equals(transactionHash));
        CosignatureSignedTransaction transaction = getTransactionOrFail(address, listener,
            observable);
        assertEquals(transactionHash, transaction.getParentHash());
        return transaction;
    }

    /**
     * This method listens for the next object in observable but if a status error happens first it
     * will raise an error. This speeds up the tests, if a transaction is not announced  correctly,
     * the method will fail before timing out as it listens errors raised by the server.
     */
    private <T> T getTransactionOrFail(Address address, Listener listener,
        Observable<T> observable) {
        Observable<Object> errorOrTransactionObservable = Observable
            .merge(observable, listener.status(address));
        Object errorOrTransaction = get(errorOrTransactionObservable.take(1));
        if (errorOrTransaction instanceof TransactionStatusError) {
            throw new IllegalArgumentException(
                "TransactionStatusError " + ((TransactionStatusError) errorOrTransaction)
                    .getStatus());
        }
        return (T) errorOrTransaction;
    }


    @SuppressWarnings("squid:S2925")
    protected void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
    }


    protected NamespaceId setAddressAlias(RepositoryType type, Address address,
        String namespaceName) {

        NamespaceId namespaceId = NamespaceId.createFromName(namespaceName);

        Account nemesisAccount = config().getNemesisAccount();

        List<AccountNames> accountNames = get(
            getRepositoryFactory(type).createAccountRepository().getAccountsNames(
                Collections.singletonList(address)));

        if (accountNames.stream().anyMatch(
            an -> an.getNames().stream().anyMatch(
                ns -> ns.getName().equals(namespaceName) && ns.getNamespaceId()
                    .equals(namespaceId)))) {
            System.out.println(namespaceName + " ADDRESS Alias found, reusing it.");
            return namespaceId;

        } else {
            System.out.println(namespaceName + " ADDRESS Alias not found, CREATING MOSAIC ALIAS");
        }

        System.out.println(
            "Setting up namespace " + namespaceName);
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(),
                namespaceName,
                BigInteger.valueOf(100)).build();

        NamespaceId rootNamespaceId = announceAggregateAndValidate(type, nemesisAccount,
            namespaceRegistrationTransaction).getNamespaceId();

        System.out.println(
            "Setting account alias " + address.plain() + " alias: " + namespaceName);
        AddressAliasTransaction aliasTransaction =
            AddressAliasTransactionFactory.create(getNetworkType(),
                AliasAction.LINK,
                rootNamespaceId,
                address
            ).build();

        announceAggregateAndValidate(type, nemesisAccount, aliasTransaction);
        return rootNamespaceId;
    }

    protected NamespaceId setMosaicAlias(
        RepositoryType type, MosaicId mosaicId,
        String namespaceName) {
        Account nemesisAccount = config().getNemesisAccount();
        NamespaceId namespaceId = NamespaceId.createFromName(namespaceName);
        List<MosaicNames> mosaicNames = get(
            getRepositoryFactory(type).createMosaicRepository().getMosaicsNames(
                Collections.singletonList(mosaicId)));

        if (mosaicNames.stream().anyMatch(
            an -> an.getNames().stream().anyMatch(
                ns -> ns.getName().equals(namespaceName) && ns.getNamespaceId()
                    .equals(namespaceId)))) {
            System.out.println(namespaceName + " MOSAIC Alias found, reusing it.");
            return namespaceId;
        } else {
            System.out.println(namespaceName + " MOSAIC Alias not found, CREATING MOSAIC ALIAS");
        }

        System.out.println(
            "Setting up namespace " + namespaceName);
        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(),
                namespaceName,
                BigInteger.valueOf(100)).build();

        NamespaceId rootNamespaceId = announceAggregateAndValidate(type, nemesisAccount,
            namespaceRegistrationTransaction).getNamespaceId();

        System.out.println(
            "Setting mosaic alias " + mosaicId.getIdAsHex() + " alias: " + namespaceName);

        MosaicAliasTransaction aliasTransaction =
            MosaicAliasTransactionFactory.create(getNetworkType(),
                AliasAction.LINK,
                rootNamespaceId,
                mosaicId
            ).build();

        announceAggregateAndValidate(type, nemesisAccount, aliasTransaction);
        return rootNamespaceId;
    }

}
