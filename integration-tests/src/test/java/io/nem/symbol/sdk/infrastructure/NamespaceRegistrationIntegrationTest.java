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
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("squid:S2699")
public class NamespaceRegistrationIntegrationTest extends BaseIntegrationTest {

  private Account account;

  @BeforeEach
  void setup() {
    account = config().getDefaultAccount();
  }

  NamespaceId rootNamespaceId;

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void standaloneRootRegisterNamespaceTransaction(RepositoryType type) {
    String namespaceName =
        "test-root-namespace-" + Double.valueOf(Math.floor(Math.random() * 10000)).intValue();

    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(), namespaceName, BigInteger.valueOf(100))
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, this.account, namespaceRegistrationTransaction);
    rootNamespaceId = namespaceRegistrationTransaction.getNamespaceId();

    sleep(1000);
    NamespaceInfo namespaceInfo =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getNamespace(namespaceRegistrationTransaction.getNamespaceId()));
    Assertions.assertEquals(this.account.getAddress(), namespaceInfo.getOwnerAddress());
    Assertions.assertEquals(
        namespaceRegistrationTransaction.getNamespaceId(), namespaceInfo.getId());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void aggregateRootRegisterNamespaceTransaction(RepositoryType type) {
    String namespaceName =
        "test-root-namespace-" + Double.valueOf(Math.floor(Math.random() * 10000)).intValue();

    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(), namespaceName, BigInteger.valueOf(100))
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, namespaceRegistrationTransaction, this.account);
    rootNamespaceId = namespaceRegistrationTransaction.getNamespaceId();

    sleep(1000);
    NamespaceInfo namespaceInfo =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getNamespace(namespaceRegistrationTransaction.getNamespaceId()));
    Assertions.assertEquals(this.account.getAddress(), namespaceInfo.getOwnerAddress());
    Assertions.assertEquals(
        namespaceRegistrationTransaction.getNamespaceId(), namespaceInfo.getId());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void standaloneSubNamespaceRegisterNamespaceTransaction(RepositoryType type) {

    this.standaloneRootRegisterNamespaceTransaction(type);

    String namespaceName =
        "test-sub-namespace-" + Double.valueOf(Math.floor(Math.random() * 10000)).intValue();

    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createSubNamespace(
                getNetworkType(), namespaceName, this.rootNamespaceId)
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, this.account, namespaceRegistrationTransaction);

    sleep(1000);
    NamespaceInfo namespaceInfo =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getNamespace(namespaceRegistrationTransaction.getNamespaceId()));
    Assertions.assertEquals(this.account.getAddress(), namespaceInfo.getOwnerAddress());
    Assertions.assertEquals(
        namespaceRegistrationTransaction.getNamespaceId(), namespaceInfo.getId());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void aggregateSubNamespaceRegisterNamespaceTransaction(RepositoryType type) {

    this.aggregateRootRegisterNamespaceTransaction(type);

    String namespaceName =
        "test-sub-namespace-" + Double.valueOf(Math.floor(Math.random() * 10000)).intValue();

    NamespaceRegistrationTransaction namespaceRegistrationTransaction =
        NamespaceRegistrationTransactionFactory.createSubNamespace(
                getNetworkType(), namespaceName, this.rootNamespaceId)
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, namespaceRegistrationTransaction, this.account);

    sleep(1000);
    NamespaceInfo namespaceInfo =
        get(
            getRepositoryFactory(type)
                .createNamespaceRepository()
                .getNamespace(namespaceRegistrationTransaction.getNamespaceId()));
    Assertions.assertEquals(this.account.getAddress(), namespaceInfo.getOwnerAddress());
    Assertions.assertEquals(
        namespaceRegistrationTransaction.getNamespaceId(), namespaceInfo.getId());
  }
}
