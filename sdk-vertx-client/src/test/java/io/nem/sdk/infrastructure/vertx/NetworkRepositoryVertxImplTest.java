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

package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.openapi.vertx.model.NetworkTypeDTO;
import io.nem.sdk.openapi.vertx.model.NetworkTypeNameEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NetworkRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class NetworkRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private NetworkRepositoryVertxImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new NetworkRepositoryVertxImpl(apiClientMock);
    }

    @Test
    public void shouldGetNetworkType() throws Exception {

        NetworkTypeDTO networkTypeDTO = new NetworkTypeDTO();
        networkTypeDTO.setName(NetworkTypeNameEnum.MIJINTEST);

        mockRemoteCall(networkTypeDTO);

        NetworkType info = repository.getNetworkType().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(NetworkType.MIJIN_TEST, info);

    }

}
