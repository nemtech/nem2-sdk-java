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

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.api.ChainRepository;
import io.nem.symbol.sdk.model.blockchain.BlockchainScore;
import java.math.BigInteger;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChainRepositoryIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getBlockchainHeight(RepositoryType type) {
    BigInteger blockchainHeight = get(getChainRepository(type).getBlockchainHeight());
    assertTrue(blockchainHeight.intValue() > 0);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getBlockchainScore(RepositoryType type) {
    BlockchainScore blockchainScore = get(getChainRepository(type).getChainScore());
    assertTrue(blockchainScore.getScoreLow().longValue() >= 0);
    assertTrue(blockchainScore.getScoreHigh().longValue() >= 0);
  }

  private ChainRepository getChainRepository(RepositoryType type) {
    return getRepositoryFactory(type).createChainRepository();
  }
}
