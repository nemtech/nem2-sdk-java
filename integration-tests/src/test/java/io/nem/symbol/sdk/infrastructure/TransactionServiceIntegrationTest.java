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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void testTransferCatCurrencyFromNemesis(RepositoryType type) {
    String mosaicAlias = getNetworkCurrency().getNamespaceId().get().getFullName().get();

    Pair<Account, NamespaceId> receipt = helper().createTestAccountWithAlias(type);
    NamespaceId recipientAlias = receipt.getRight();

    String hash =
        transferUsingAliases(
                config().getNemesisAccount(), type, mosaicAlias, recipientAlias, BigInteger.TEN)
            .getTransactionInfo()
            .get()
            .getHash()
            .get();

    List<Transaction> transactions =
        get(getTransactionService(type).resolveAliases(Collections.singletonList(hash)));

    Assertions.assertEquals(1, transactions.size());

    TransferTransaction resolvedTransaction = (TransferTransaction) transactions.get(0);
    System.out.println(toJson(resolvedTransaction));

    Assertions.assertEquals(resolvedTransaction.getRecipient(), receipt.getKey().getAddress());

    System.out.println(resolvedTransaction.getMosaics().get(0).getId());

    Assertions.assertTrue(resolvedTransaction.getMosaics().get(0).getId() instanceof MosaicId);
    Assertions.assertTrue(resolvedTransaction.getRecipient() instanceof Address);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void testTransferCustomCurrencyFromAccount1(RepositoryType type) {

    String mosaicAlias =
        ("testTransferCustomCurrencyFromAccount1" + RandomUtils.nextInt(0, 10000)).toLowerCase();
    Pair<Account, NamespaceId> receipt = helper().createTestAccountWithAlias(type);
    NamespaceId recipientAlias = receipt.getRight();
    Account testAccount = helper.getTestAccount(type).getLeft();
    MosaicId mosaicId =
        helper().createMosaic(testAccount, type, BigInteger.valueOf(10000), mosaicAlias);
    String transferTransactionHash =
        transferUsingAliases(testAccount, type, mosaicAlias, recipientAlias, BigInteger.ONE)
            .getTransactionInfo()
            .get()
            .getHash()
            .get();

    List<Transaction> transactions =
        get(getTransactionService(type).resolveAliases(Arrays.asList(transferTransactionHash)));

    Assertions.assertEquals(1, transactions.size());
    TransferTransaction resolvedTransaction = (TransferTransaction) transactions.get(0);
    assertTransaction(mosaicId, resolvedTransaction, testAccount, receipt.getKey());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void testTransferCustomCurrencyFromAccount1UsingAggregate(RepositoryType type) {

    String mosaicAlias =
        ("testTransferCustomCurrencyFromAccount1UsingAggregate" + RandomUtils.nextInt(0, 10000))
            .toLowerCase();
    Account testAccount = helper().getTestAccount(type).getLeft();
    Pair<Account, NamespaceId> receipt = helper().createTestAccountWithAlias(type);
    NamespaceId recipientAlias = receipt.getRight();

    MosaicId mosaicId =
        helper().createMosaic(testAccount, type, BigInteger.valueOf(10000), mosaicAlias);

    String aggregateTransactionHash =
        transferUsingAliasesAggregate(
                testAccount, type, mosaicAlias, recipientAlias, BigInteger.ONE)
            .getTransactionInfo()
            .get()
            .getHash()
            .get();

    List<Transaction> transactions =
        get(
            getTransactionService(type)
                .resolveAliases(Collections.singletonList(aggregateTransactionHash)));

    Assertions.assertEquals(1, transactions.size());
    TransferTransaction resolvedTransaction =
        (TransferTransaction)
            ((AggregateTransaction) transactions.get(0)).getInnerTransactions().get(0);
    assertTransaction(mosaicId, resolvedTransaction, testAccount, receipt.getKey());
  }

  private void assertTransaction(
      MosaicId mosaicId,
      TransferTransaction resolvedTransaction,
      Account testAccount,
      Account receiptAccount) {

    System.out.println(toJson(resolvedTransaction));
    Assertions.assertEquals(testAccount.getPublicAccount(), resolvedTransaction.getSigner().get());
    Assertions.assertEquals(receiptAccount.getAddress(), resolvedTransaction.getRecipient());
    System.out.println(resolvedTransaction.getMosaics().get(0).getId());
    Assertions.assertTrue(resolvedTransaction.getMosaics().get(0).getId() instanceof MosaicId);
    Assertions.assertTrue(resolvedTransaction.getRecipient() instanceof Address);
    Assertions.assertEquals(mosaicId, resolvedTransaction.getMosaics().get(0).getId());
  }

  private TransferTransaction transferUsingAliases(
      Account sender,
      RepositoryType type,
      String mosaicAlias,
      UnresolvedAddress recipientNamespace,
      BigInteger amount) {

    NamespaceId mosaicNamespace = NamespaceId.createFromName(mosaicAlias);

    System.out.println("Sending " + amount + " Mosaic to: " + mosaicAlias);

    TransferTransactionFactory factory =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipientNamespace,
                Collections.singletonList(new Mosaic(mosaicNamespace, amount)))
            .message(new PlainMessage("E2ETest:TransactionServiceIntegrationTest"));

    factory.maxFee(maxFee);
    TransferTransaction transferTransaction = factory.build();

    Assertions.assertTrue(transferTransaction.getMosaics().get(0).getId() instanceof NamespaceId);
    Assertions.assertTrue(transferTransaction.getRecipient() instanceof NamespaceId);

    TransferTransaction processedTransferTransaction =
        announceAndValidate(type, sender, transferTransaction);

    Assertions.assertEquals(amount, processedTransferTransaction.getMosaics().get(0).getAmount());

    System.out.println(toJson(processedTransferTransaction));

    Assertions.assertTrue(
        processedTransferTransaction.getMosaics().get(0).getId() instanceof NamespaceId);
    Assertions.assertTrue(processedTransferTransaction.getRecipient() instanceof NamespaceId);

    return processedTransferTransaction;
  }

  private AggregateTransaction transferUsingAliasesAggregate(
      Account sender,
      RepositoryType type,
      String mosaicAlias,
      UnresolvedAddress recipientNamespace,
      BigInteger amount) {

    NamespaceId mosaicNamespace = NamespaceId.createFromName(mosaicAlias);

    System.out.println("Sending " + amount + " Mosaic to: " + mosaicAlias);

    TransferTransactionFactory factory =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipientNamespace,
                Collections.singletonList(new Mosaic(mosaicNamespace, amount)))
            .message(new PlainMessage("E2ETest:TransactionServiceIntegrationTest"));

    factory.maxFee(maxFee);
    TransferTransaction transferTransaction = factory.build();

    Assertions.assertTrue(transferTransaction.getMosaics().get(0).getId() instanceof NamespaceId);
    Assertions.assertTrue(transferTransaction.getRecipient() instanceof NamespaceId);

    Pair<TransferTransaction, AggregateTransaction> pair =
        announceAggregateAndValidate(type, transferTransaction, sender);

    TransferTransaction processedTransferTransaction = pair.getLeft();
    Assertions.assertEquals(amount, processedTransferTransaction.getMosaics().get(0).getAmount());

    System.out.println(toJson(processedTransferTransaction));

    Assertions.assertTrue(
        processedTransferTransaction.getMosaics().get(0).getId() instanceof NamespaceId);
    Assertions.assertTrue(processedTransferTransaction.getRecipient() instanceof NamespaceId);

    return pair.getRight();
  }
}
