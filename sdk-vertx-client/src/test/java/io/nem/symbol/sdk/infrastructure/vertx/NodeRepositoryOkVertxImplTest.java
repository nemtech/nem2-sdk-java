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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.node.NodeHealth;
import io.nem.symbol.sdk.model.node.NodeInfo;
import io.nem.symbol.sdk.model.node.NodeStatus;
import io.nem.symbol.sdk.model.node.NodeTime;
import io.nem.symbol.sdk.model.node.RoleType;
import io.nem.symbol.sdk.model.node.ServerInfo;
import io.nem.symbol.sdk.model.node.StorageInfo;
import io.nem.symbol.sdk.openapi.vertx.model.CommunicationTimestampsDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NodeHealthDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NodeHealthInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NodeInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NodeStatusEnum;
import io.nem.symbol.sdk.openapi.vertx.model.NodeTimeDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ServerDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ServerInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.StorageInfoDTO;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
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
    repository = new NodeRepositoryVertxImpl(apiClientMock);
  }

  @Test
  public void shouldGetNode() throws Exception {

    NodeInfoDTO dto = new NodeInfoDTO();
    dto.setPort(3000);
    dto.setHost("http://hostname");
    dto.setFriendlyName("friendlyName");
    dto.setNetworkIdentifier(104);
    dto.setRoles(2);
    dto.setVersion(1234);
    dto.setPublicKey("somePublicKey");
    dto.setNetworkGenerationHashSeed("abc");

    mockRemoteCall(dto);

    NodeInfo info = repository.getNodeInfo().toFuture().get();

    Assertions.assertNotNull(info);

    Assertions.assertEquals(dto.getPort().intValue(), info.getPort());
    Assertions.assertEquals(dto.getHost(), info.getHost());
    Assertions.assertEquals(dto.getPublicKey(), info.getPublicKey());
    Assertions.assertEquals(dto.getFriendlyName(), info.getFriendlyName());
    Assertions.assertEquals(NetworkType.MAIN_NET, info.getNetworkIdentifier());
    Assertions.assertEquals(Collections.singletonList(RoleType.API_NODE), info.getRoles());
    Assertions.assertEquals(dto.getVersion().intValue(), info.getVersion());
    Assertions.assertEquals(
        dto.getNetworkGenerationHashSeed(), info.getNetworkGenerationHashSeed());
  }

  @Test
  public void getNodePeers() throws Exception {

    NodeInfoDTO dto = new NodeInfoDTO();
    dto.setPort(3000);
    dto.setHost("http://hostname");
    dto.setFriendlyName("friendlyName");
    dto.setNetworkIdentifier(104);
    dto.setRoles(2);
    dto.setVersion(1234);
    dto.setPublicKey("somePublicKey");
    dto.setNetworkGenerationHashSeed("abc");

    mockRemoteCall(Arrays.asList(dto));

    NodeInfo info = repository.getNodePeers().toFuture().get().get(0);

    Assertions.assertNotNull(info);

    Assertions.assertEquals(dto.getPort().intValue(), info.getPort());
    Assertions.assertEquals(dto.getHost(), info.getHost());
    Assertions.assertEquals(dto.getPublicKey(), info.getPublicKey());
    Assertions.assertEquals(dto.getFriendlyName(), info.getFriendlyName());
    Assertions.assertEquals(NetworkType.MAIN_NET, info.getNetworkIdentifier());
    Assertions.assertEquals(Collections.singletonList(RoleType.API_NODE), info.getRoles());
    Assertions.assertEquals(dto.getVersion().intValue(), info.getVersion());
    Assertions.assertEquals(
        dto.getNetworkGenerationHashSeed(), info.getNetworkGenerationHashSeed());
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

    Assertions.assertEquals(BigInteger.valueOf(1L), info.getReceiveTimestamp());
    Assertions.assertEquals(BigInteger.valueOf(2L), info.getSendTimestamp());
  }

  @Test
  public void shouldGetStorage() throws Exception {
    StorageInfoDTO dto = new StorageInfoDTO();
    dto.setNumAccounts(1);
    dto.setNumBlocks(2);
    dto.setNumTransactions(3);

    mockRemoteCall(dto);

    StorageInfo storageInfo = repository.getNodeStorage().toFuture().get();
    Assertions.assertEquals(dto.getNumAccounts(), storageInfo.getNumAccounts());
    Assertions.assertEquals(dto.getNumBlocks(), storageInfo.getNumBlocks());
    Assertions.assertEquals(dto.getNumTransactions(), storageInfo.getNumTransactions());
  }

  @Test
  public void shouldGetServerInfo() throws Exception {
    ServerInfoDTO dto = new ServerInfoDTO();
    ServerDTO serverInfoDto = new ServerDTO();
    serverInfoDto.setRestVersion("RestVersion1");
    serverInfoDto.setSdkVersion("SdkVersion1");
    dto.serverInfo(serverInfoDto);

    mockRemoteCall(dto);

    ServerInfo serverInfo = repository.getServerInfo().toFuture().get();

    Assertions.assertEquals(dto.getServerInfo().getRestVersion(), serverInfo.getRestVersion());
    Assertions.assertEquals(dto.getServerInfo().getSdkVersion(), serverInfo.getSdkVersion());
  }

  @Test
  public void getNodeHealth() throws Exception {
    NodeHealthInfoDTO dto =
        new NodeHealthInfoDTO()
            .status(new NodeHealthDTO().apiNode(NodeStatusEnum.DOWN).db(NodeStatusEnum.UP));

    mockRemoteCall(dto);

    NodeHealth nodeHealth = repository.getNodeHealth().toFuture().get();
    Assertions.assertEquals(NodeStatus.DOWN, nodeHealth.getApiNode());
    Assertions.assertEquals(NodeStatus.UP, nodeHealth.getDb());
  }
}
