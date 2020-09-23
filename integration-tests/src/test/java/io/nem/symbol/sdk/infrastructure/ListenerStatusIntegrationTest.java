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

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@SuppressWarnings("squid:S1607")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
class ListenerStatusIntegrationTest extends BaseIntegrationTest {

  @Test
  void statusListener() throws ExecutionException, InterruptedException {
    RepositoryType type = DEFAULT_REPOSITORY_TYPE;
    Account account1 = Account.generateNewAccount(getNetworkType());
    Account account2 = Account.generateNewAccount(getNetworkType());
    Account account3 = Account.generateNewAccount(getNetworkType());

    createListener(type)
        .status(account1.getAddress())
        .subscribe(
            a -> {
              System.out.println(
                  ">>>> account 1 "
                      + a.getAddress().plain()
                      + " "
                      + a.getHash()
                      + " "
                      + a.getStatus());
            });

    createListener(type)
        .status(account2.getAddress())
        .subscribe(
            a -> {
              System.out.println(
                  ">>>> account 2 "
                      + a.getAddress().plain()
                      + " "
                      + a.getHash()
                      + " "
                      + a.getStatus());
            });

    createListener(type)
        .status(account3.getAddress())
        .subscribe(
            a -> {
              System.out.println(
                  ">>>> account 3  "
                      + a.getAddress().plain()
                      + " "
                      + a.getHash()
                      + " "
                      + a.getStatus());
            });

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                account2.getAddress(),
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))))
            .message(new PlainMessage("test-message"))
            .maxFee(this.maxFee)
            .build();
    announceAndValidate(type, account1, transferTransaction);
  }

  @Test
  void sendTransactionsReusingListener() throws ExecutionException, InterruptedException {
    RepositoryType type = DEFAULT_REPOSITORY_TYPE;
    Account account1 = config().getNemesisAccount1();
    Account account2 = Account.generateNewAccount(getNetworkType());
    Account account3 = Account.generateNewAccount(getNetworkType());

    Listener listener = createListener(type);
    listener
        .unconfirmedRemoved(account1.getAddress())
        .subscribe(
            a -> {
              System.out.println(">>>> account 1 " + a);
            });

    listener
        .unconfirmedRemoved(account2.getAddress())
        .subscribe(
            a -> {
              System.out.println(">>>> account 2 " + a);
            });

    listener
        .unconfirmedRemoved(account3.getAddress())
        .subscribe(
            a -> {
              System.out.println(">>>> account 3  " + a);
            });
    // IT PRINTS:
    // >>>> account 1
    // 94BE61F8FA091319A3564D843468ABD8E51034F7CDF132A74BBA2A7465E27C7D
    // >>>> account 2
    // 94BE61F8FA091319A3564D843468ABD8E51034F7CDF132A74BBA2A7465E27C7D
    // >>>> account 3
    // 94BE61F8FA091319A3564D843468ABD8E51034F7CDF132A74BBA2A7465E27C7D
    // >>>> account 1
    // 94BE61F8FA091319A3564D843468ABD8E51034F7CDF132A74BBA2A7465E27C7D
    // >>>> account 2
    // 94BE61F8FA091319A3564D843468ABD8E51034F7CDF132A74BBA2A7465E27C7D
    // >>>> account 3
    // 94BE61F8FA091319A3564D843468ABD8E51034F7CDF132A74BBA2A7465E27C7D

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                account2.getAddress(),
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))))
            .message(new PlainMessage("test-message"))
            .maxFee(this.maxFee)
            .build();
    announceAndValidate(type, account1, transferTransaction);
    sleep(1000);
  }

  @Test
  void sendTransactionsNewListener() throws ExecutionException, InterruptedException {
    RepositoryType type = DEFAULT_REPOSITORY_TYPE;
    Account account1 = config().getNemesisAccount1();
    Account account2 = Account.generateNewAccount(getNetworkType());
    Account account3 = Account.generateNewAccount(getNetworkType());

    createListener(type)
        .unconfirmedRemoved(account1.getAddress())
        .subscribe(
            a -> {
              System.out.println(">>>> account 1 " + a);
            });

    createListener(type)
        .unconfirmedRemoved(account2.getAddress())
        .subscribe(
            a -> {
              System.out.println(">>>> account 2 " + a);
            });

    createListener(type)
        .unconfirmedRemoved(account3.getAddress())
        .subscribe(
            a -> {
              System.out.println(">>>> account 3  " + a);
            });

    // IT prints:
    // >>>> account 1
    // B742A00E5F7D8381F78EBE8CE47023C6298FB1802CDB3861CA0C05286DE0EE63
    // >>>> account 2
    // B742A00E5F7D8381F78EBE8CE47023C6298FB1802CDB3861CA0C05286DE0EE63

    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                account2.getAddress(),
                Collections.singletonList(
                    getNetworkCurrency().createAbsolute(BigInteger.valueOf(1))))
            .message(new PlainMessage("test-message"))
            .maxFee(this.maxFee)
            .build();
    announceAndValidate(type, account1, transferTransaction);
    sleep(1000);
  }

  private Listener createListener(RepositoryType type)
      throws InterruptedException, ExecutionException {
    Listener listener = getRepositoryFactory(type).createListener();
    CompletableFuture<Void> connected = listener.open();
    connected.get();
    assertTrue(connected.isDone());
    return listener;
  }
}
