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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.sdk.model.blockchain.BlockchainScore;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ChainScoreDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.HeightInfoDTO;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link ChainRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class ChainRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

  private ChainRepositoryOkHttpImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new ChainRepositoryOkHttpImpl(apiClientMock);
  }

  @Test
  public void shouldGetBlockchainHeight() throws Exception {
    HeightInfoDTO dto = new HeightInfoDTO();
    dto.setHeight(BigInteger.valueOf(8L));
    mockRemoteCall(dto);
    BigInteger blockchainHeight = repository.getBlockchainHeight().toFuture().get();
    Assertions.assertEquals((dto.getHeight()), blockchainHeight);
  }

  @Test
  public void shouldGetBlockchainScore() throws Exception {
    ChainScoreDTO dto = new ChainScoreDTO();
    dto.setScoreLow(BigInteger.valueOf(3L));
    dto.setScoreHigh(BigInteger.valueOf(5L));
    mockRemoteCall(dto);
    BlockchainScore blockchainScore = repository.getChainScore().toFuture().get();
    Assertions.assertEquals((dto.getScoreLow()), blockchainScore.getScoreLow());
    Assertions.assertEquals((dto.getScoreHigh()), blockchainScore.getScoreHigh());
  }

  @Override
  public ChainRepositoryOkHttpImpl getRepository() {
    return repository;
  }
}
