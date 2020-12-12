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

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.infrastructure.okhttp.RepositoryFactoryOkHttpImpl;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkV1Transaction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkV1TransactionFactory;
import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestnetTester {

  public static final String PRIVATE_KEY =
      "EB189780486B5E653B33A64A20DBD33E0ED0BF7E994E397A112F1618239A50FD";

  public static final BigInteger MAX_FEE = BigInteger.valueOf(1000000);

  public static void main(String[] args) throws Exception {
    String baseUrl = "http://api-01.us-west-2.0.10.0.x.symboldev.network:3000";
    //        String baseUrl = "http://api-01.us-west-1.0.10.0.x.symboldev.network:3000";
    //    String baseUrl = "http://api-01.ap-northeast-1.0.10.0.x.symboldev.network:3000";
    RepositoryFactory repositoryFactory = new RepositoryFactoryOkHttpImpl(baseUrl);
    sendVotingKey2(repositoryFactory);
  }

  private static void sendMosaics(RepositoryFactory repositoryFactory) throws Exception {

    NetworkType networkType = repositoryFactory.getNetworkType().blockingFirst();
    Account account = Account.createFromPrivateKey(PRIVATE_KEY, networkType);
    Account recipient = Account.generateNewAccount(networkType);

    Mosaic mosaic = repositoryFactory.getNetworkCurrency().blockingFirst().createAbsolute(1);

    System.out.println(account.getAddress().plain());
    Duration duration = repositoryFactory.getEpochAdjustment().blockingFirst();
    Deadline deadline = Deadline.create(duration);

    TransferTransaction transaction =
        TransferTransactionFactory.create(
                networkType, deadline, recipient.getAddress(), Collections.singletonList(mosaic))
            .maxFee(MAX_FEE)
            .build();
    announceTransaction(repositoryFactory, account, transaction);
  }

  private static void sendVotingKey1(RepositoryFactory repositoryFactory) throws Exception {

    NetworkType networkType = repositoryFactory.getNetworkType().toFuture().get();
    Account account = Account.createFromPrivateKey(PRIVATE_KEY, networkType);

    System.out.println(account.getAddress().plain());
    Duration duration = repositoryFactory.getEpochAdjustment().toFuture().get();
    Deadline deadline = Deadline.create(duration);
    VotingKey votingKey =
        VotingKey.fromHexString("463CCC639B5306DD06E56A273E13EF08CAB8D46A8ACA1D3919F19AF89DE116C5");

    VotingKeyLinkV1Transaction transaction =
        VotingKeyLinkV1TransactionFactory.create(
                networkType, deadline, votingKey, (1), (26280), LinkAction.LINK)
            .maxFee(MAX_FEE)
            .build();

    announceTransaction(repositoryFactory, account, transaction);
  }

  private static void announceTransaction(
      RepositoryFactory repositoryFactory, Account account, Transaction transaction)
      throws Exception {

    TransactionService transactionService = new TransactionServiceImpl(repositoryFactory);
    String generationHash = repositoryFactory.getGenerationHash().toFuture().get();
    try (Listener listener = repositoryFactory.createListener()) {
      listener.open().get();
      SignedTransaction signedTransaction = account.sign(transaction, generationHash);
      System.out.println(signedTransaction.getHash());
      Transaction completedTransaction =
          transactionService.announce(listener, signedTransaction).toFuture().get();

      System.out.println(completedTransaction.getTransactionInfo().get().getHash().get());
    }
  }

  private static void sendVotingKey2(RepositoryFactory repositoryFactory) throws Exception {

    NetworkType networkType = repositoryFactory.getNetworkType().toFuture().get();
    Account account = Account.createFromPrivateKey(PRIVATE_KEY, networkType);

    System.out.println(account.getAddress().plain());
    Duration duration = repositoryFactory.getEpochAdjustment().toFuture().get();
    Deadline deadline = Deadline.create(duration);
    PublicKey votingKey =
        PublicKey.fromHexString("463CCC639B5306DD06E56A273E13EF08CAB8D46A8ACA1D3919F19AF89DE116C5");

    VotingKeyLinkTransaction transaction =
        VotingKeyLinkTransactionFactory.create(
                networkType, deadline, votingKey, (1), (26280), LinkAction.LINK)
            .maxFee(MAX_FEE)
            .build();

    announceTransaction(repositoryFactory, account, transaction);
  }

  private static void readTransactions(RepositoryFactory repositoryFactory) throws Exception {
    String generationHash = repositoryFactory.getGenerationHash().toFuture().get();

    NetworkType networkType = repositoryFactory.getNetworkType().toFuture().get();

    List<Transaction> transactions =
        repositoryFactory
            .createTransactionRepository()
            .streamer()
            .search(
                new TransactionSearchCriteria(TransactionGroup.CONFIRMED)
                    .order(OrderBy.DESC)
                    .transactionTypes(Arrays.asList(TransactionType.VOTING_KEY_LINK)))
            .take(50)
            .toList()
            .blockingGet();

    transactions.forEach(
        t -> {
          System.out.println(t.getVersion());
          System.out.println(t.getTransactionInfo().get().getHash().get());
          System.out.println(
              t.createTransactionHash(
                  ConvertUtils.toHex(t.serialize()), ConvertUtils.fromHexToBytes(generationHash)));
        });
  }
}
