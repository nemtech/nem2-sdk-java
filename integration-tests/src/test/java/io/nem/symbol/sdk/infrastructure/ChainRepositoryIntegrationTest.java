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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.api.ChainRepository;
import io.nem.symbol.sdk.model.blockchain.ChainInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChainRepositoryIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getChainInfo(RepositoryType type) {
    ChainInfo chainInfo = get(getChainRepository(type).getChainInfo());
    assertTrue(chainInfo.getScoreLow().longValue() >= 0);
    assertTrue(chainInfo.getScoreHigh().longValue() >= 0);
    assertTrue(chainInfo.getHeight().longValue() > 0);
    FinalizedBlock finalizedBlock = chainInfo.getLatestFinalizedBlock();

    assertTrue(finalizedBlock.getFinalizationEpoch() >= 0);
    assertTrue(finalizedBlock.getFinalizationPoint().longValue() >= 0);
    assertTrue(finalizedBlock.getHeight().longValue() > 0);
    assertNotNull(finalizedBlock.getHash());
  }

  private ChainRepository getChainRepository(RepositoryType type) {
    return getRepositoryFactory(type).createChainRepository();
  }
}
