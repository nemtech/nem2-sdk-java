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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.reactivex.schedulers.Schedulers;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NamespaceRepositoryIntegrationTest extends BaseIntegrationTest {

    private PublicAccount publicAccount;
    private NamespaceId namespaceId;

    @BeforeAll
    void setup() {
        // String publicKey = "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF";
        String publicKey = "F227B3268481DF7F9825CFB7C2051F441A9BC0C65FA0AA2CF3A438C4B3177B81";
        publicAccount = PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST,
            SignSchema.DEFAULT);
        namespaceId = NetworkCurrencyMosaic.NAMESPACEID;
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespace(RepositoryType type) throws ExecutionException, InterruptedException {
        NamespaceInfo namespaceInfo = getNamespaceRepository(type).getNamespace(namespaceId)
            .toFuture()
            .get();

        assertEquals(new BigInteger("1"), namespaceInfo.getStartHeight());
        assertEquals(new BigInteger("-1"), namespaceInfo.getEndHeight());
        assertEquals(BigInteger.valueOf(3294802500L), namespaceId.getId());
        assertEquals(namespaceId.getIdAsLong(), namespaceInfo.getLevels().get(1).getIdAsLong());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespacesFromAccount(RepositoryType type)
        throws ExecutionException, InterruptedException {
        List<NamespaceInfo> namespacesInfo =
            getNamespaceRepository(type).getNamespacesFromAccount(publicAccount.getAddress())
                .toFuture()
                .get();

        assertEquals(1, namespacesInfo.size());
        assertEquals(new BigInteger("1"), namespacesInfo.get(0).getStartHeight());
        assertEquals(new BigInteger("-1"), namespacesInfo.get(0).getEndHeight());
        assertEquals(namespaceId.getIdAsLong(),
            namespacesInfo.get(0).getLevels().get(0).getIdAsLong());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespacesFromAccounts(RepositoryType type)
        throws ExecutionException, InterruptedException {
        List<NamespaceInfo> namespacesInfo =
            getNamespaceRepository(type)
                .getNamespacesFromAccounts(
                    Collections.singletonList(
                        Address.createFromRawAddress("SARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJETM3ZSP")))
                .toFuture()
                .get();

        assertEquals(1, namespacesInfo.size());
        assertEquals(new BigInteger("1"), namespacesInfo.get(0).getStartHeight());
        assertEquals(new BigInteger("-1"), namespacesInfo.get(0).getEndHeight());
        assertEquals(namespaceId, namespacesInfo.get(0).getLevels().get(0).getIdAsLong());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNamespaceNames(RepositoryType type) throws ExecutionException, InterruptedException {
        List<NamespaceName> namespaceNames =
            getNamespaceRepository(type).getNamespaceNames(Collections.singletonList(namespaceId))
                .toFuture()
                .get();

        assertEquals(1, namespaceNames.size());
        assertEquals("nem", namespaceNames.get(0).getName());
        assertEquals(namespaceId, namespaceNames.get(0).getNamespaceId());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenNamespaceDoesNotExists(
        RepositoryType type) {
        // TestObserver<NamespaceInfo> testObserver = new TestObserver<>();
        getNamespaceRepository(type)
            .getNamespace(NamespaceId.createFromName("nonregisterednamespace"))
            .subscribeOn(Schedulers.single())
            .test()
            .awaitDone(2, TimeUnit.SECONDS)
            .assertFailure(RuntimeException.class);
    }

    private NamespaceRepository getNamespaceRepository(RepositoryType type) {
        return getRepositoryFactory(type).createNamespaceRepository();
    }
}
