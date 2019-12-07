/*
 * Copyright 2019 NEM
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

import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceIntegrationTest extends BaseIntegrationTest {

    String mosaicAlias = "transaction-service-mosaic-test-5";

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void testTransferCatCurrencyFromNemesis(RepositoryType type)
        throws InterruptedException {
        String hash = transferUsingAliases(config().getNemesisAccount(), type, "cat.currency",
            "testaccount2", BigInteger.TEN).getTransactionInfo().get().getHash().get();
        sleep(2000);

        List<Transaction> transactions = get(
            getTransactionService(type).resolveAliases(Collections.singletonList(hash)));

        Assertions.assertEquals(1, transactions.size());

        TransferTransaction resolvedTransaction = (TransferTransaction) transactions.get(0);
        System.out.println(toJson(resolvedTransaction));

        Assertions.assertEquals(config().getTestAccount2().getAddress(),
            resolvedTransaction.getRecipient());

        System.out.println(resolvedTransaction.getMosaics().get(0).getId());

        Assertions.assertFalse(resolvedTransaction.getMosaics().get(0).getId().isAlias());
        Assertions.assertFalse(resolvedTransaction.getRecipient().isAlias());

    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void testTransferCustomCurrencyFromAccount1(RepositoryType type)
        throws InterruptedException {

        MosaicId mosaicId = createMosaicUsingAlias(type, mosaicAlias);

        String transferTransactionHash = transferUsingAliases(
            config().getNemesisAccount(), type, mosaicAlias,
            "testaccount2", BigInteger.ONE).getTransactionInfo().get().getHash().get();

        sleep(2000);

        List<Transaction> transactions = get(
            getTransactionService(type)
                .resolveAliases(Arrays.asList(transferTransactionHash)));

        Assertions.assertEquals(1, transactions.size());
        TransferTransaction resolvedTransaction = (TransferTransaction) transactions.get(0);
        assertTransaction(mosaicId, resolvedTransaction);


    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void testTransferCustomCurrencyFromAccount1UsingAggregate(RepositoryType type)
        throws InterruptedException {

        MosaicId mosaicId = createMosaicUsingAlias(type, mosaicAlias);

        String aggregateTransactionHash = transferUsingAliasesAggregate(
            config().getNemesisAccount(), type, mosaicAlias,
            "testaccount2", BigInteger.ONE).getTransactionInfo().get().getHash().get();

        sleep(2000);

        List<Transaction> transactions = get(
            getTransactionService(type)
                .resolveAliases(Collections.singletonList(aggregateTransactionHash)));

        Assertions.assertEquals(1, transactions.size());
        TransferTransaction resolvedTransaction = (TransferTransaction) ((AggregateTransaction) transactions
            .get(0)).getInnerTransactions().get(0);
        assertTransaction(mosaicId, resolvedTransaction);


    }

    private MosaicId createMosaicUsingAlias(RepositoryType type, String mosaicAlias)
        throws InterruptedException {
        NamespaceId mosaicNamespace = NamespaceId.createFromName(mosaicAlias);
        NamespaceInfo namespaceInfo = null;
        MosaicId mosaicId = null;
        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        try {
            namespaceInfo = get(
                repositoryFactory.createNamespaceRepository()
                    .getNamespace(mosaicNamespace));
            System.out.println("Mosaic found!");
            mosaicId = get(
                repositoryFactory.createNamespaceRepository().getLinkedMosaicId(mosaicNamespace));
            System.out.println("Mosaic id: " + mosaicId.getIdAsHex());

            MosaicInfo mosaicInfo = get(
                repositoryFactory.createMosaicRepository().getMosaic(mosaicId));

            System.out.println("Supply: " + mosaicInfo.getSupply());

        } catch (Exception e) {

        }

        System.out.println("Mosaic Alias: " + mosaicAlias);
        if (namespaceInfo == null) {

            System.out.println("Creating mosaic!");

            Account account = this.config().getDefaultAccount();
            AccountInfo accountInfo = get(repositoryFactory.createAccountRepository()
                .getAccountInfo(account.getPublicAccount().getAddress()));

            Assertions.assertFalse(
                accountInfo.getMosaics().isEmpty());

            mosaicId = createMosaic(account, type, BigInteger.valueOf(100000), mosaicAlias);

            sleep(2000);
        }
        return mosaicId;
    }

    private void assertTransaction(MosaicId mosaicId, TransferTransaction resolvedTransaction) {

        System.out.println(toJson(resolvedTransaction));
        Assertions.assertEquals(config().getTestAccount2().getAddress(),
            resolvedTransaction.getRecipient());
        System.out.println(resolvedTransaction.getMosaics().get(0).getId());
        Assertions.assertFalse(resolvedTransaction.getMosaics().get(0).getId().isAlias());
        Assertions.assertFalse(resolvedTransaction.getRecipient().isAlias());
        Assertions.assertEquals(mosaicId, resolvedTransaction.getMosaics().get(0).getId());
        Assertions.assertEquals(config().getTestAccount2().getAddress(),
            resolvedTransaction.getRecipient());
    }


    private TransferTransaction transferUsingAliases(Account sender, RepositoryType type,
        String mosaicAlias, String recipientAlias, BigInteger amount) {

        NamespaceId recipientNamespace = NamespaceId.createFromName(recipientAlias);

        NamespaceId mosaicNamespace = NamespaceId.createFromName(mosaicAlias);

        System.out.println("Sending " + amount + " Mosaic to: " + mosaicAlias);

        TransferTransactionFactory factory =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipientNamespace,
                Collections.singletonList(new Mosaic(mosaicNamespace, amount)),
                new PlainMessage("E2ETest:TransactionServiceIntegrationTest")
            );

        factory.maxFee(this.maxFee);
        TransferTransaction transferTransaction = factory.build();

        Assertions.assertTrue(transferTransaction.getMosaics().get(0).getId().isAlias());
        Assertions.assertTrue(transferTransaction.getRecipient().isAlias());

        TransferTransaction processedTransferTransaction = announceAndValidate(type, sender,
            transferTransaction);

        Assertions
            .assertEquals(amount, processedTransferTransaction.getMosaics().get(0).getAmount());

        System.out.println(toJson(processedTransferTransaction));

        Assertions.assertTrue(processedTransferTransaction.getMosaics().get(0).getId().isAlias());
        Assertions.assertTrue(processedTransferTransaction.getRecipient().isAlias());

        return processedTransferTransaction;

    }

    private AggregateTransaction transferUsingAliasesAggregate(Account sender, RepositoryType type,
        String mosaicAlias, String recipientAlias, BigInteger amount) {

        NamespaceId recipientNamespace = NamespaceId.createFromName(recipientAlias);

        NamespaceId mosaicNamespace = NamespaceId.createFromName(mosaicAlias);

        System.out.println("Sending " + amount + " Mosaic to: " + mosaicAlias);

        TransferTransactionFactory factory =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipientNamespace,
                Collections.singletonList(new Mosaic(mosaicNamespace, amount)),
                new PlainMessage("E2ETest:TransactionServiceIntegrationTest")
            );

        factory.maxFee(this.maxFee);
        TransferTransaction transferTransaction = factory.build();

        Assertions.assertTrue(transferTransaction.getMosaics().get(0).getId().isAlias());
        Assertions.assertTrue(transferTransaction.getRecipient().isAlias());

        Pair<TransferTransaction, AggregateTransaction> pair = announceAggregateAndValidate(type,
            transferTransaction, sender);

        TransferTransaction processedTransferTransaction = pair.getLeft();
        Assertions
            .assertEquals(amount, processedTransferTransaction.getMosaics().get(0).getAmount());

        System.out.println(toJson(processedTransferTransaction));

        Assertions.assertTrue(processedTransferTransaction.getMosaics().get(0).getId().isAlias());
        Assertions.assertTrue(processedTransferTransaction.getRecipient().isAlias());

        return pair.getRight();

    }

}
