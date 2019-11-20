/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.MultisigRepository;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;

/**
 * Utility main class that uses the nemesis address configured to generate new accounts necessary
 * for the integration tests. Use with caution!!
 */
public class SetUpAccountsTool extends BaseIntegrationTest {

    public static final long AMOUNT_PER_TRANSFER = 10000000;

    private final RepositoryType type = DEFAULT_REPOSITORY_TYPE;

    public static void main(String[] args) {
        new SetUpAccountsTool().createAccounts();
    }

    private void createAccounts() {
        setUp();

        try {
            sendMosaicFromNemesis(config().getTestAccount(), false);
            setAddressAlias(type, config().getTestAccount().getAddress(), "testaccount");

            sendMosaicFromNemesis(config().getTestAccount2(), false);
            setAddressAlias(type, config().getTestAccount2().getAddress(), "testaccount2");

            sendMosaicFromNemesis(config().getCosignatoryAccount(), false);
            setAddressAlias(type, config().getCosignatoryAccount().getAddress(),
                "cosignatory-account");

            sendMosaicFromNemesis(config().getCosignatory2Account(), false);
            setAddressAlias(type, config().getCosignatory2Account().getAddress(),
                "cosignatory-account2");

            sendMosaicFromNemesis(config().getCosignatory3Account(), false);
            setAddressAlias(type, config().getCosignatory3Account().getAddress(),
                "cosignatory-account3");

            sendMosaicFromNemesis(config().getMultisigAccount(), false);
            setAddressAlias(type, config().getMultisigAccount().getAddress(), "nemesis-account");

            createMultisigAccount(config().getMultisigAccount(),
                config().getCosignatoryAccount(),
                config().getCosignatory2Account()
            );
        } finally {
            tearDown();
        }
    }

    private void createMultisigAccount(Account multisigAccount, Account... accounts) {

        AccountRepository accountRepository = getRepositoryFactory(type)
            .createAccountRepository();

        MultisigRepository multisigRepository = getRepositoryFactory(type)
            .createMultisigRepository();

        AccountInfo accountInfo = get(
            accountRepository.getAccountInfo(multisigAccount.getAddress()));
        System.out.println(jsonHelper().print(accountInfo));

        try {
            MultisigAccountInfo multisigAccountInfo = get(
                multisigRepository.getMultisigAccountInfo(multisigAccount.getAddress()));

            System.out.println(
                "Multisig account with address " + multisigAccount.getAddress() + " already exist");
            System.out.println(jsonHelper().print(multisigAccountInfo));
            return;
        } catch (RepositoryCallException e) {
            System.out.println(
                "Multisig account with address " + multisigAccount.getAddress()
                    + " does not exist. Creating");
        }

        System.out.println("Creating multisg account");
        List<PublicAccount> additions = Arrays.stream(accounts)
            .map(Account::getPublicAccount).collect(Collectors.toList());
        MultisigAccountModificationTransaction convertIntoMultisigTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 1, (byte) 1, additions, Collections.emptyList())
            .maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createBonded(
            getNetworkType(),
            Collections.singletonList(
                convertIntoMultisigTransaction.toAggregate(multisigAccount.getPublicAccount()))
        ).maxFee(this.maxFee).build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(multisigAccount, Arrays.asList(accounts),
                getGenerationHash());

        hashLock(type, multisigAccount, signedTransaction);

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getRepositoryFactory(type).createTransactionRepository()
                .announce(signedTransaction));

        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        validateTransactionAnnounceCorrectly(multisigAccount.getAddress(),
            signedTransaction.getHash(), type);


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


        TransferTransactionFactory factory =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipient.getAddress(),
                Collections.singletonList(NetworkCurrencyMosaic.createAbsolute(amount, getNetworkType())),
                new PlainMessage("E2ETest:SetUpAccountsTool")
            );

        factory.maxFee(this.maxFee);
        TransferTransaction transferTransaction = factory.build();


        TransferTransaction processedTransaction = announceAndValidate(type, nemesisAccount,
            transferTransaction);
        Assertions.assertEquals(amount, processedTransaction.getMosaics().get(0).getAmount());

    }

    private boolean hasMosaic(Account recipient) {
        try {
            AccountInfo accountInfo = get(getRepositoryFactory(type).createAccountRepository()
                .getAccountInfo(recipient.getAddress()));
            return accountInfo.getMosaics().stream().anyMatch(
                m -> m.getAmount().longValue() >= 100);
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

    protected void hashLock(RepositoryType type, Account account,
        SignedTransaction signedTransaction) {
        HashLockTransaction hashLockTransaction = HashLockTransactionFactory.create(
            getNetworkType(),
            NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10), getNetworkType()),
            BigInteger.valueOf(100),
            signedTransaction)
            .maxFee(this.maxFee).build();
        announceAndValidate(type, account, hashLockTransaction);
    }
}
