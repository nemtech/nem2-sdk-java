/*
 * Copyright 2018 NEM
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//TODO Broken!!
class NamespaceRepositoryIntegrationTest extends BaseIntegrationTest {

    private NamespaceId namespaceId;

    @BeforeAll
    void setup() {
        namespaceId = NetworkCurrencyMosaic.NAMESPACEID;
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespace(RepositoryType type) {
        NamespaceInfo namespaceInfo = get(getNamespaceRepository(type).getNamespace(namespaceId));
        assertEquals(new BigInteger("1"), namespaceInfo.getStartHeight());
//        assertEquals(namespaceId, namespaceInfo.getId());
        assertEquals(namespaceId.getIdAsHex(), namespaceInfo.getId().getIdAsHex());
        assertEquals(namespaceId.getIdAsLong(), namespaceInfo.getLevels().get(1).getIdAsLong());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespacesFromAccount(RepositoryType type) {
        Account account = config().getNemesisAccount();
        List<NamespaceInfo> namespacesInfo =
            get(getNamespaceRepository(type).getNamespacesFromAccount(account.getAddress()));

        namespacesInfo.forEach(n -> {
            Assertions.assertEquals(account.getPublicAccount(), n.getOwner());
        });
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespacesFromAccounts(RepositoryType type) {
        Account account = config().getNemesisAccount();
        List<NamespaceInfo> namespacesInfo = get(getNamespaceRepository(type)
            .getNamespacesFromAccounts(
                Collections.singletonList(
                    account.getAddress())));

        namespacesInfo.forEach(n -> {
            Assertions.assertEquals(account.getPublicAccount(), n.getOwner());
        });

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespaceNames(RepositoryType type) {
        List<NamespaceName> namespaceNames =
            get(getNamespaceRepository(type)
                .getNamespaceNames(Collections.singletonList(namespaceId)));

        Assertions.assertEquals(2, namespaceNames.size());
        Assertions.assertEquals("currency", namespaceNames.get(0).getName());
        Assertions.assertTrue(namespaceNames.get(0).getParentId().isPresent());

        Assertions.assertEquals("cat", namespaceNames.get(1).getName());
        Assertions.assertFalse(namespaceNames.get(1).getParentId().isPresent());


    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenNamespaceDoesNotExists(RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(getNamespaceRepository(type)
                .getNamespace(NamespaceId.createFromName("nonregisterednamespace"))));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'f75cf605c224a9e7'",
            exception.getMessage());
    }

    private NamespaceRepository getNamespaceRepository(RepositoryType type) {
        return getRepositoryFactory(type).createNamespaceRepository();
    }
}
