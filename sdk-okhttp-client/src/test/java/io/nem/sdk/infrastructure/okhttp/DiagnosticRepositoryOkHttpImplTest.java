/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.ServerInfo;
import io.nem.sdk.openapi.okhttp_gson.model.ServerDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ServerInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.StorageInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link ChainRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class DiagnosticRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private DiagnosticRepositoryOkHttpImpl repository;


    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new DiagnosticRepositoryOkHttpImpl(apiClientMock);
    }

    @Test
    public void shouldGetBlockchainStorage() throws Exception {
        StorageInfoDTO dto = new StorageInfoDTO();
        dto.setNumAccounts(1L);
        dto.setNumBlocks(2L);
        dto.setNumTransactions(3L);

        mockRemoteCall(dto);

        BlockchainStorageInfo blockchainStorageInfo = repository.getBlockchainStorage().toFuture()
            .get();
        Assertions.assertEquals(dto.getNumAccounts(), blockchainStorageInfo.getNumAccounts());
        Assertions.assertEquals(dto.getNumBlocks(), blockchainStorageInfo.getNumBlocks());
        Assertions
            .assertEquals(dto.getNumTransactions(), blockchainStorageInfo.getNumTransactions());

    }

    @Test
    public void shouldGetServerInfo() throws Exception {
        ServerInfoDTO dto = new ServerInfoDTO();
        ServerDTO serverInfoDto = new ServerDTO();
        serverInfoDto.setRestVersion("RestVersion1");
        serverInfoDto.setSdkVersion("SdkVersion1");
        dto.serverInfo(serverInfoDto);

        mockRemoteCall(dto);

        ServerInfo serverInfo = repository.getServerInfo().toFuture()
            .get();

        Assertions.assertEquals(dto.getServerInfo().getRestVersion(), serverInfo.getRestVersion());
        Assertions.assertEquals(dto.getServerInfo().getSdkVersion(), serverInfo.getSdkVersion());

    }


    @Override
    public DiagnosticRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
