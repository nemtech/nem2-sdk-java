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

import io.nem.symbol.sdk.model.blockchain.ChainInfo;
import io.nem.symbol.sdk.openapi.vertx.model.ChainInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.FinalizedBlockDTO;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link ChainRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class ChainRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

  private ChainRepositoryVertxImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new ChainRepositoryVertxImpl(apiClientMock);
  }

  @Test
  public void shouldGetBlockchainScore() throws Exception {
    ChainInfoDTO dto = new ChainInfoDTO();
    dto.setScoreLow(BigInteger.valueOf(1));
    dto.setScoreHigh(BigInteger.valueOf(2));
    dto.setHeight(BigInteger.valueOf(3));
    dto.latestFinalizedBlock(
        new FinalizedBlockDTO()
            .hash("abc")
            .height(BigInteger.valueOf(6))
            .finalizationEpoch(7L)
            .finalizationPoint(8L));
    mockRemoteCall(dto);
    ChainInfo chainInfo = repository.getChainInfo().toFuture().get();
    Assertions.assertEquals((dto.getScoreLow()), chainInfo.getScoreLow());
    Assertions.assertEquals((dto.getScoreHigh()), chainInfo.getScoreHigh());
    Assertions.assertEquals((dto.getHeight()), chainInfo.getHeight());
    Assertions.assertEquals(
        (dto.getLatestFinalizedBlock().getFinalizationPoint()),
        chainInfo.getLatestFinalizedBlock().getFinalizationPoint());
    Assertions.assertEquals(
        (dto.getLatestFinalizedBlock().getHeight()),
        chainInfo.getLatestFinalizedBlock().getHeight());
    Assertions.assertEquals(
        (dto.getLatestFinalizedBlock().getFinalizationEpoch()),
        chainInfo.getLatestFinalizedBlock().getFinalizationEpoch());
    Assertions.assertEquals(
        (dto.getLatestFinalizedBlock().getHash()), chainInfo.getLatestFinalizedBlock().getHash());
  }
}
