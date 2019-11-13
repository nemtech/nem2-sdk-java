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

import io.nem.sdk.model.blockchain.NetworkInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.openapi.okhttp_gson.model.NetworkTypeDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NetworkRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class NetworkRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private NetworkRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new NetworkRepositoryOkHttpImpl(apiClientMock);
    }

    @Test
    public void shouldGetNetworkType() throws Exception {

        resolveNetworkType();

        NetworkType info = repository.getNetworkType().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(NetworkType.MIJIN_TEST, info);

    }

    @Test
    public void shouldGetNetworkInfo() throws Exception {

        NetworkTypeDTO networkTypeDTO = new NetworkTypeDTO();
        networkTypeDTO.setName("mijinTest");
        networkTypeDTO.setDescription("some description");

        mockRemoteCall(networkTypeDTO);

        NetworkInfo info = repository.getNetworkInfo().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals("mijinTest", info.getName());
        Assertions.assertEquals("some description", info.getDescription());

    }

    @Override
    public NetworkRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
