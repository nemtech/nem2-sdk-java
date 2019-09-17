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

import io.nem.sdk.model.blockchain.BlockchainScore;
import io.nem.sdk.openapi.vertx.model.ChainScoreDTO;
import io.nem.sdk.openapi.vertx.model.HeightInfoDTO;
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
        repository = new ChainRepositoryVertxImpl(apiClientMock, networkType, signSchema);
    }

    @Test
    public void shouldGetBlockchainHeight() throws Exception {
        HeightInfoDTO dto = new HeightInfoDTO();
        dto.setHeight(BigInteger.valueOf(8));
        mockRemoteCall(dto);
        BigInteger blockchainHeight = repository.getBlockchainHeight().toFuture().get();
        Assertions.assertEquals((dto.getHeight()), blockchainHeight);
    }

    @Test
    public void shouldGetBlockchainScore() throws Exception {
        ChainScoreDTO dto = new ChainScoreDTO();
        dto.setScoreLow(BigInteger.valueOf(3));
        dto.setScoreHigh(BigInteger.valueOf(5));
        mockRemoteCall(dto);
        BlockchainScore blockchainScore = repository.getBlockchainScore().toFuture().get();
        Assertions.assertEquals((dto.getScoreLow()), blockchainScore.getScoreLow());
        Assertions.assertEquals((dto.getScoreHigh()), blockchainScore.getScoreHigh());
    }

}
