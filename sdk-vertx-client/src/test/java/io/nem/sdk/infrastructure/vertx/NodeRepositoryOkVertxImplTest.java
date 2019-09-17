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
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeTime;
import io.nem.sdk.model.node.RoleType;
import io.nem.sdk.openapi.vertx.model.CommunicationTimestampsDTO;
import io.nem.sdk.openapi.vertx.model.NodeInfoDTO;
import io.nem.sdk.openapi.vertx.model.NodeTimeDTO;
import io.nem.sdk.openapi.vertx.model.RolesTypeEnum;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NodeRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class NodeRepositoryOkVertxImplTest extends AbstractVertxRespositoryTest {

    private NodeRepositoryVertxImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new NodeRepositoryVertxImpl(apiClientMock, networkType, signSchema);
    }

    @Test
    public void shouldGetNode() throws Exception {

        NodeInfoDTO dto = new NodeInfoDTO();
        dto.setPort(3000);
        dto.setHost("http://hostname");
        dto.setFriendlyName("friendlyName");
        dto.setNetworkIdentifier(104);
        dto.setRoles(RolesTypeEnum.NUMBER_2);
        dto.setVersion(1234);
        dto.setPublicKey("somePublicKey");

        mockRemoteCall(dto);

        NodeInfo info = repository.getNodeInfo().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(dto.getPort().intValue(), info.getPort());
        Assertions.assertEquals(dto.getHost(), info.getHost());
        Assertions.assertEquals(dto.getPublicKey(), info.getPublicKey());
        Assertions.assertEquals(dto.getFriendlyName(), info.getFriendlyName());
        Assertions.assertEquals(NetworkType.MAIN_NET, info.getNetworkIdentifier());
        Assertions.assertEquals(RoleType.API_NODE, info.getRoles());
        Assertions.assertEquals(dto.getVersion().intValue(), info.getVersion());

    }

    @Test
    public void shouldGetNodeTime() throws Exception {

        NodeTimeDTO dto = new NodeTimeDTO();
        CommunicationTimestampsDTO comm = new CommunicationTimestampsDTO();
        comm.setReceiveTimestamp(BigInteger.ONE);
        comm.setSendTimestamp(BigInteger.valueOf(2));

        dto.setCommunicationTimestamps(comm);
        mockRemoteCall(dto);

        NodeTime info = repository.getNodeTime().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(BigInteger.valueOf(1L), info.getReceiveTimeStamp());
        Assertions.assertEquals(BigInteger.valueOf(2L), info.getSendTimeStamp());

    }

}
