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
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.util.concurrent.ExecutionException;

/** Main class that listen to symbol in order to troubleshooting integration tests. */
public class ListenerForTests extends BaseIntegrationTest {

  public static void main(String[] args) throws Exception {
    BaseIntegrationTest.beforeAll();
    new ListenerForTests().run();
  }

  private void run() throws ExecutionException, InterruptedException {
    Listener listener = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).createListener();
    listener.open().get();

    listener
        .newBlock()
        .subscribe(
            b -> {
              System.out.println("New BLOCK!! " + b.getHeight());
            });

    listener
        .finalizedBlock()
        .subscribe(
            b -> {
              System.out.println("New Finalized Block!! " + b.getHeight());
            });
    listenToAccount("Test Account 1", config().getTestAccount().getAddress(), listener);
    listenToAccount("Test Account 2", config().getTestAccount2().getAddress(), listener);
    listenToAccount("Cosignatory Account", config().getCosignatoryAccount().getAddress(), listener);
    listenToAccount(
        "Cosignatory Account 2", config().getCosignatory2Account().getAddress(), listener);
    listenToAccount("Multisign Account 2", config().getMultisigAccount().getAddress(), listener);

    config()
        .getNemesisAccounts()
        .forEach(account -> listenToAccount("Nemesis Account", account.getAddress(), listener));
  }

  private void listenToAccount(
      String accountDescription, UnresolvedAddress account, Listener listener) {
    System.out.println(
        "Listening for transaction of account "
            + (account instanceof Address
                ? ((Address) account).plain()
                : ((NamespaceId) account).getIdAsHex())
            + ". "
            + accountDescription);

    listener
        .confirmed(account)
        .subscribe(
            c ->
                System.out.println(
                    accountDescription + " received confirmed transaction " + toJson(c)));

    listener
        .cosignatureAdded(account)
        .subscribe(
            c ->
                System.out.println(
                    accountDescription + " Received cosignatureAdded transaction " + toJson(c)));

    listener
        .aggregateBondedAdded(account)
        .subscribe(
            c ->
                System.out.println(
                    accountDescription
                        + " Received aggregateBondedAdded transaction "
                        + toJson(c)));

    listener
        .aggregateBondedRemoved(account)
        .subscribe(
            c ->
                System.out.println(
                    accountDescription
                        + " Received aggregateBondedRemoved transaction "
                        + toJson(c)));

    listener
        .unconfirmedRemoved(account)
        .subscribe(
            c ->
                System.out.println(
                    accountDescription + " Received unconfirmedRemoved transaction " + toJson(c)));

    listener
        .status(account)
        .subscribe(c -> System.out.println(accountDescription + " Error: " + toJson(c)));
  }
}
