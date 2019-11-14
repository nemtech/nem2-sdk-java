/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.Listener;
import io.nem.sdk.model.account.Account;
import java.util.concurrent.ExecutionException;

/**
 * Main class that listen to catapult in order to troubleshooting integration tests.
 */
public class ListenerForTests extends BaseIntegrationTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new ListenerForTests().run();
    }

    private void run() throws ExecutionException, InterruptedException {
        setUp();
        Listener listener = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).createListener();
        listener.open().get();
        listenToAccount("Test Account 1", config().getTestAccount(), listener);
        listenToAccount("Test Account 2", config().getTestAccount2(), listener);
        listenToAccount("Cosignatory Account", config().getCosignatoryAccount(), listener);
        listenToAccount("Cosignatory Account 2", config().getCosignatory2Account(), listener);
        listenToAccount("Multisign Account 2", config().getMultisigAccount(), listener);
        listenToAccount("Nemesis Account", config().getNemesisAccount(), listener);
    }

    private void listenToAccount(String accountDescription, Account account, Listener listener) {
        System.out.println("Listening for transaction of account " + account.getAddress().plain()
            + ". " + accountDescription);

        listener.unconfirmedAdded(account.getAddress())
            .subscribe(c -> System.out
                .println(accountDescription + " received unconfirmedAdded transaction " + c));

        listener.confirmed(account.getAddress())
            .subscribe(c -> System.out
                .println(accountDescription + " received confirmed transaction " + c));

        listener.cosignatureAdded(account.getAddress())
            .subscribe(c -> System.out
                .println(accountDescription + " Received cosignatureAdded transaction " + c));

        listener.status(account.getAddress())
            .subscribe(c -> System.out.println(accountDescription + " Error: " + c.getStatus()));
    }

}
