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

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Utility main class that uses the nemesis address configured to generate new accounts necessary for the integration
 * tests. Use with caution!!
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AAASetupIntegrationTest extends BaseIntegrationTest {

    public static final long AMOUNT_PER_TRANSFER = 100000000;

    private final RepositoryType type = DEFAULT_REPOSITORY_TYPE;

    @Test
    @Order(1)
    void createTestAccount() {
        sendMosaicFromNemesis(config().getTestAccount(), false);
        setAddressAlias(type, config().getTestAccount().getAddress(), "testaccount");
    }

    @Test
    @Order(2)
    void createTestAccount2() {
        sendMosaicFromNemesis(config().getTestAccount2(), false);
        setAddressAlias(type, config().getTestAccount2().getAddress(), "testaccount2");
    }

    @Test
    @Order(3)
    void createCosignatoryAccount() {
        sendMosaicFromNemesis(config().getCosignatoryAccount(), true);
        setAddressAlias(type, config().getCosignatoryAccount().getAddress(), "cosignatory-account");
    }

    @Test
    @Order(4)
    void createCosignatoryAccount2() {
        sendMosaicFromNemesis(config().getCosignatory2Account(), true);
        setAddressAlias(type, config().getCosignatory2Account().getAddress(), "cosignatory-account2");
    }

    @Test
    @Order(5)
    void createMultisigAccount() {
        sendMosaicFromNemesis(config().getMultisigAccount(), true);
        setAddressAlias(type, config().getMultisigAccount().getAddress(), "multisig-account");
        createMultisigAccountBonded(config().getMultisigAccount(), config().getCosignatoryAccount(),
            config().getCosignatory2Account());
    }

    private void createMultisigAccountBonded(Account multisigAccount, Account... accounts) {

        AccountRepository accountRepository = getRepositoryFactory(type).createAccountRepository();

        MultisigRepository multisigRepository = getRepositoryFactory(type).createMultisigRepository();

        AccountInfo accountInfo = get(accountRepository.getAccountInfo(multisigAccount.getAddress()));
        System.out.println(jsonHelper().print(accountInfo));

        try {
            MultisigAccountInfo multisigAccountInfo = get(
                multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));

            System.out
                .println("Multisig account with address " + multisigAccount.getAddress().plain() + " already exist");
            System.out.println(jsonHelper().print(multisigAccountInfo));
            return;
        } catch (RepositoryCallException e) {
            System.out.println(
                "Multisig account with address " + multisigAccount.getAddress().plain() + " does not exist. Creating");
        }

        System.out.println("Creating multisg account " + multisigAccount.getAddress().plain());
        List<UnresolvedAddress> additions = Arrays.stream(accounts).map(Account::getAddress)
            .collect(Collectors.toList());
        MultisigAccountModificationTransaction convertIntoMultisigTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 1, (byte) 1, additions, Collections.emptyList()).maxFee(this.maxFee)
            .build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createBonded(getNetworkType(),
            Collections.singletonList(convertIntoMultisigTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount, Arrays.asList(accounts), getGenerationHash());

        SignedTransaction signedHashLockTransaction = HashLockTransactionFactory
            .create(getNetworkType(), getNetworkCurrency().createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100), signedAggregateTransaction).maxFee(this.maxFee).build()
            .signWith(multisigAccount, getGenerationHash());

        getTransactionOrFail(getTransactionService(type)
                .announceHashLockAggregateBonded(getListener(type), signedHashLockTransaction, signedAggregateTransaction),
            aggregateTransaction);

        sleep(1000);
        Transaction transaction = get(getRepositoryFactory(type).createTransactionRepository()
            .getTransaction(signedAggregateTransaction.getHash()));

        System.out.println(toJson(transaction));
    }

    private void createMultisigAccountComplete(Account multisigAccount, Account... accounts) {

        AccountRepository accountRepository = getRepositoryFactory(type).createAccountRepository();

        MultisigRepository multisigRepository = getRepositoryFactory(type).createMultisigRepository();

        AccountInfo accountInfo = get(accountRepository.getAccountInfo(multisigAccount.getAddress()));
        System.out.println(jsonHelper().print(accountInfo));

        try {
            MultisigAccountInfo multisigAccountInfo = get(
                multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));

            System.out
                .println("Multisig account with address " + multisigAccount.getAddress().plain() + " already exist");
            System.out.println(jsonHelper().print(multisigAccountInfo));
            return;
        } catch (RepositoryCallException e) {
            System.out.println(
                "Multisig account with address " + multisigAccount.getAddress().plain() + " does not exist. Creating");
        }

        System.out.println("Creating multisg account " + multisigAccount.getAddress().plain());
        List<UnresolvedAddress> additions = Arrays.stream(accounts).map(Account::getAddress)
            .collect(Collectors.toList());
        MultisigAccountModificationTransaction convertIntoMultisigTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 1, (byte) 1, additions, Collections.emptyList()).maxFee(this.maxFee)
            .build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(getNetworkType(),
            Collections.singletonList(convertIntoMultisigTransaction.toAggregate(multisigAccount.getPublicAccount())))
            .maxFee(this.maxFee).build();

        SignedTransaction signedAggregateTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount, Arrays.asList(accounts), getGenerationHash());

        Transaction aggregateTransaciton = get(
            getTransactionService(type).announce(getListener(type), signedAggregateTransaction));

        sleep(1000);
        Transaction transaction = get(getRepositoryFactory(type).createTransactionRepository()
            .getTransaction(signedAggregateTransaction.getHash()));

        System.out.println(toJson(transaction));

        Assertions.assertEquals(aggregateTransaciton.getTransactionInfo().get().getHash().get(),
            signedAggregateTransaction.getHash());
    }

    private void sendMosaicFromNemesis(Account recipient, boolean force) {
        if (hasMosaic(recipient) && !force) {
            System.out.println("Ignoring recipient. It has the mosaic already: ");
            printAccount(recipient);
            return;
        }

        Account nemesisAccount = config().getNemesisAccount();
        System.out.println("Sending " + AMOUNT_PER_TRANSFER + " Mosaic to: ");
        printAccount(recipient);

        BigInteger amount = BigInteger.valueOf(AMOUNT_PER_TRANSFER);

        TransferTransactionFactory factory = TransferTransactionFactory.create(getNetworkType(), recipient.getAddress(),
            Collections.singletonList(getNetworkCurrency().createAbsolute(amount)),
            new PlainMessage("E2ETest:SetUpAccountsTool"));

        factory.maxFee(this.maxFee);
        TransferTransaction transferTransaction = factory.build();

        TransferTransaction processedTransaction = announceAndValidate(type, nemesisAccount, transferTransaction);
        Assertions.assertEquals(amount, processedTransaction.getMosaics().get(0).getAmount());

    }

    private boolean hasMosaic(Account recipient) {
        try {
            AccountInfo accountInfo = get(
                getRepositoryFactory(type).createAccountRepository().getAccountInfo(recipient.getAddress()));
            return accountInfo.getMosaics().stream().anyMatch(m -> m.getAmount().longValue() >= AMOUNT_PER_TRANSFER);
        } catch (RepositoryCallException e) {
            return false;
        }
    }

    void printAccount(Account account) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("privateKey", account.getPrivateKey());
        map.put("publicKey", account.getPublicKey());
        map.put("address", account.getAddress().plain());
        System.out.println(jsonHelper().print(map));
    }

}
