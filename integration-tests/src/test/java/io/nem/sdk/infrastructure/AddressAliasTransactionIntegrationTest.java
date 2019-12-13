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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddressAliasTransactionIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void sendAddressAliasTransaction(RepositoryType type) throws InterruptedException {
        Account account = config().getDefaultAccount();
        String namespaceName =
            "test-root-namespace-for-address-alias-" + new Double(Math.floor(Math.random() * 10000))
                .intValue();

        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(),
                namespaceName,
                BigInteger.valueOf(100)).maxFee(this.maxFee).build();

        NamespaceId rootNamespaceId = announceAggregateAndValidate(type,
            namespaceRegistrationTransaction, account
        ).getLeft().getNamespaceId();

        AddressAliasTransaction addressAliasTransaction =
            AddressAliasTransactionFactory.create(getNetworkType(),
                AliasAction.LINK,
                rootNamespaceId,
                account.getAddress()
            ).maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction2 =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    addressAliasTransaction.toAggregate(account.getPublicAccount()))
            ).maxFee(this.maxFee).build();

        announceAndValidate(type, account, aggregateTransaction2);

        List<AccountNames> accountNames = get(getRepositoryFactory(type).createNamespaceRepository()
            .getAccountsNames(Collections.singletonList(account.getAddress())));

        Assertions.assertEquals(1, accountNames.size());

        assertEquals(1, accountNames.size());
        assertEquals(account.getAddress(), accountNames.get(0).getAddress());
        assertTrue(accountNames.get(0).getNames().stream().map(NamespaceName::getName).collect(
            Collectors.toList()).contains(namespaceName));
    }
}
