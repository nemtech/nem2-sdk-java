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

import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.vertx.model.BlockDTO;
import io.nem.sdk.openapi.vertx.model.BlockInfoDTO;
import io.nem.sdk.openapi.vertx.model.BlockMetaDTO;
import io.nem.sdk.openapi.vertx.model.NetworkTypeEnum;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link BlockRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class BlockRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private BlockRepositoryVertxImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new BlockRepositoryVertxImpl(apiClientMock);
    }

    @Test
    public void shouldGetBlockByHeight() throws Exception {

        BlockInfoDTO dto = new BlockInfoDTO();
        BlockMetaDTO metaDTO = new BlockMetaDTO();
        metaDTO.setHash("someHash");
        metaDTO.setNumTransactions(10);
        metaDTO.setGenerationHash("generationHash");
        metaDTO.setNumStatements(20);
        metaDTO.setStateHashSubCacheMerkleRoots(Arrays.asList("string1", "string2"));
        metaDTO.setTotalFee(BigInteger.valueOf(8));

        dto.setMeta(metaDTO);

        BlockDTO blockDto = new BlockDTO();
        blockDto.setType(16716);
        blockDto.setVersion(3);
        blockDto.setNetwork(NetworkTypeEnum.NUMBER_144);
        blockDto.setSignerPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setBeneficiaryPublicKey(
            "B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setHeight(BigInteger.valueOf(9));

        dto.setBlock(blockDto);

        mockRemoteCall(dto);

        BigInteger height = BigInteger.valueOf(10L);
        BlockInfo info = repository.getBlockByHeight(height).toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(blockDto.getBeneficiaryPublicKey(),
            info.getBeneficiaryPublicAccount().getPublicKey().toHex());

        Assertions.assertEquals(blockDto.getSignerPublicKey(),
            info.getSignerPublicAccount().getPublicKey().toHex());

        Assertions.assertEquals(16716, info.getType());
        Assertions.assertEquals(3, info.getVersion().intValue());
        Assertions.assertEquals(NetworkType.MIJIN_TEST, info.getNetworkType());
        Assertions.assertEquals(BigInteger.valueOf(9L), info.getHeight());
        Assertions.assertEquals(metaDTO.getHash(), info.getHash());
        Assertions.assertEquals(metaDTO.getNumTransactions(), info.getNumTransactions());
        Assertions.assertEquals(metaDTO.getGenerationHash(), info.getGenerationHash());
        Assertions.assertEquals(metaDTO.getNumTransactions(), info.getNumTransactions());
        Assertions
            .assertEquals(metaDTO.getStateHashSubCacheMerkleRoots(), info.getSubCacheMerkleRoots());
        Assertions
            .assertEquals(metaDTO.getTotalFee(), info.getTotalFee());

        Assertions.assertEquals(blockDto.getHeight(), info.getHeight());

    }

    @Test
    public void shouldGetBlocksByHeightWithLimit() throws Exception {

        BlockInfoDTO dto = new BlockInfoDTO();
        BlockMetaDTO metaDTO = new BlockMetaDTO();
        metaDTO.setHash("someHash");
        metaDTO.setNumTransactions(10);
        metaDTO.setGenerationHash("generationHash");
        metaDTO.setNumStatements(20);
        metaDTO.setStateHashSubCacheMerkleRoots(Arrays.asList("string1", "string2"));
        metaDTO.setTotalFee(BigInteger.valueOf(8));

        dto.setMeta(metaDTO);

        BlockDTO blockDto = new BlockDTO();
        blockDto.setType(16716);
        blockDto.setVersion(3);
        blockDto.setNetwork(NetworkTypeEnum.NUMBER_144);
        blockDto
            .setSignerPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setBeneficiaryPublicKey(
            "B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setHeight(BigInteger.valueOf(9));

        dto.setBlock(blockDto);

        mockRemoteCall(Collections.singletonList(dto));

        BigInteger height = BigInteger.valueOf(10L);
        BlockInfo info = repository
            .getBlocksByHeightWithLimit(height, 1,
                Optional.of(new QueryParams(10, "someId", "someOrder")))
            .toFuture().get().get(0);

        Assertions.assertNotNull(info);

        Assertions.assertEquals(blockDto.getBeneficiaryPublicKey(),
            info.getBeneficiaryPublicAccount().getPublicKey().toHex());

        Assertions.assertEquals(blockDto.getSignerPublicKey(),
            info.getSignerPublicAccount().getPublicKey().toHex());

        Assertions.assertEquals(16716, info.getType());
        Assertions.assertEquals(3, info.getVersion().intValue());
        Assertions.assertEquals(NetworkType.MIJIN_TEST, info.getNetworkType());
        Assertions.assertEquals(BigInteger.valueOf(9L), info.getHeight());
        Assertions.assertEquals(metaDTO.getHash(), info.getHash());
        Assertions.assertEquals(metaDTO.getNumTransactions(), info.getNumTransactions());
        Assertions.assertEquals(metaDTO.getGenerationHash(), info.getGenerationHash());
        Assertions.assertEquals(metaDTO.getNumTransactions(), info.getNumTransactions());
        Assertions
            .assertEquals(metaDTO.getStateHashSubCacheMerkleRoots(), info.getSubCacheMerkleRoots());
        Assertions
            .assertEquals(metaDTO.getTotalFee(), info.getTotalFee());

        Assertions.assertEquals(blockDto.getHeight(), info.getHeight());

    }


    @Test
    public void shouldGetBlockTransactions() throws Exception {
        TransactionInfoDTO transactionInfoDTO = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        mockRemoteCall(Collections.singletonList(transactionInfoDTO));

        BigInteger height = BigInteger.valueOf(10L);
        List<Transaction> transactions = repository.getBlockTransactions(height).toFuture().get();

        Assertions.assertNotNull(transactions);

        Assertions.assertEquals(1, transactions.size());

    }



}
