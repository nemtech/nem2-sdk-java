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

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.RepositoryFactoryConfiguration;
import io.nem.sdk.infrastructure.vertx.RepositoryFactoryVertxImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import io.nem.sdk.model.mosaic.NetworkCurrencyBuilder;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryFactoryConfigurationExamplesIntegrationTest extends BaseIntegrationTest {

    @Test
    void bootAppFullyOffline() throws ExecutionException, InterruptedException {

        //Option 1) Client app boot time. The clients defines the configuration to work offline.
        RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(
            "http://localhost:3000/");
        configuration.withNetworkType(NetworkType.MAIN_NET);
        configuration.withGenerationHash("abc");
        configuration.withNetworkCurrency(
            new NetworkCurrencyBuilder(NamespaceId.createFromName("my.custom.currency"), 6)
                .build());

        configuration.withNetworkCurrency(
            new NetworkCurrencyBuilder(NamespaceId.createFromName("my.custom.harvest"), 3)
                .build());

        RepositoryFactory repositoryFactory = new RepositoryFactoryVertxImpl(configuration);

        appDoSomeStuff(repositoryFactory);
    }

    @Test
    void bootAppUsingRestConfiguration() throws ExecutionException, InterruptedException {

        //Option 2) Client app boot time relaying on the rest configuration:
        RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(
            "http://localhost:3000/");
        RepositoryFactory repositoryFactory = new RepositoryFactoryVertxImpl(configuration);

        appDoSomeStuff(repositoryFactory);
    }


    @Test
    void bootAppUsingLegacyHardcodedCurrencies() throws ExecutionException, InterruptedException {

        //Option 3) Client app boot time relaying on some of the rest configuration. User uses the legacy hardcoded sdk currencies
        RepositoryFactoryConfiguration configuration = new RepositoryFactoryConfiguration(
            "http://localhost:3000/");

        configuration.withNetworkCurrency(NetworkCurrency.CAT_CURRENCY);
        configuration.withHarvestCurrency(NetworkCurrency.CAT_HARVEST);

        RepositoryFactory repositoryFactory = new RepositoryFactoryVertxImpl(configuration);

        appDoSomeStuff(repositoryFactory);
    }

    public void appDoSomeStuff(RepositoryFactory repositoryFactory)
        throws ExecutionException, InterruptedException {
        //The application logic is exactly  the same regardless of how the repository factory was set

        //Note: if rest is used, these values are cached form rest
        NetworkCurrency currency = repositoryFactory.getNetworkCurrency().toFuture().get();
        String generationHash = repositoryFactory.getGenerationHash().toFuture().get();
        NetworkType networkType = repositoryFactory.getNetworkType().toFuture().get();

        Account sender = Account.generateNewAccount(networkType);
        Account recipient = Account.generateNewAccount(networkType);

        TransferTransaction transferTransaction = TransferTransactionFactory
            .create(networkType, recipient.getAddress(),
                Collections.singletonList(currency.createRelative(
                    BigInteger.TEN)), PlainMessage.Empty).build();

        SignedTransaction signedTransaction = transferTransaction.signWith(sender, generationHash);
        //Announce or store somewhere....

    }

}
