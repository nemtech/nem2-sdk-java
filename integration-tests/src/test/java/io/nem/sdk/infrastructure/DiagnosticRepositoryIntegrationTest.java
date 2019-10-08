/*
 * Copyright 2019 NEM
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.ServerInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DiagnosticRepositoryIntegrationTest extends BaseIntegrationTest {

    private DiagnosticRepository getDiagnosticRepository(RepositoryType type) {
        return getRepositoryFactory(type).createDiagnosticRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getBlockchainStorage(RepositoryType type) {
        BlockchainStorageInfo blockchainStorageInfo = get(
            getDiagnosticRepository(type).getBlockchainStorage());

        assertTrue(blockchainStorageInfo.getNumAccounts() > 0);
        assertTrue(blockchainStorageInfo.getNumTransactions() > 0);
        assertTrue(blockchainStorageInfo.getNumBlocks() > 0);
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getServerInfo(RepositoryType type) {
        ServerInfo serverInfo = get(getDiagnosticRepository(type).getServerInfo());

        assertNotEquals("", serverInfo.getRestVersion());
        assertNotEquals("", serverInfo.getSdkVersion());
    }
}
