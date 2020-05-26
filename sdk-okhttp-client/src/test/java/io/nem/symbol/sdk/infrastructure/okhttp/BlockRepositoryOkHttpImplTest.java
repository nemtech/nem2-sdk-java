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

import io.nem.symbol.sdk.api.BlockSearchCriteria;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.MerkleProofInfo;
import io.nem.symbol.sdk.model.blockchain.Position;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockMetaDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BlockPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerklePathItemDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleProofInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NetworkTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.PositionEnum;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NetworkRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class BlockRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private BlockRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new BlockRepositoryOkHttpImpl(apiClientMock);
    }


    @Test
    public void getMerkleTransaction() throws Exception {

        MerkleProofInfoDTO merkleProofInfoDTO = new MerkleProofInfoDTO();

        MerklePathItemDTO item = new MerklePathItemDTO().hash("someHash")
            .position(PositionEnum.LEFT);
        mockRemoteCall(merkleProofInfoDTO.addMerklePathItem(item));

        MerkleProofInfo merkleProofInfo = repository
            .getMerkleTransaction(BigInteger.ONE, "HASH!").toFuture()
            .get();
        Assertions.assertEquals(1, merkleProofInfo.getMerklePath().size());
        Assertions.assertEquals("someHash", merkleProofInfo.getMerklePath().get(0).getHash());
        Assertions
            .assertEquals(Position.LEFT, merkleProofInfo.getMerklePath().get(0).getPosition());

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
        metaDTO.setTotalFee(BigInteger.valueOf(8L));

        dto.setMeta(metaDTO);

        BlockDTO blockDto = new BlockDTO();
        blockDto.setType(16716);
        blockDto.setVersion(3);
        blockDto
            .setSignerPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setBeneficiaryPublicKey(
            "B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setHeight(BigInteger.valueOf(9L));

        blockDto.setNetwork(NetworkTypeEnum.NUMBER_144);
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
        Assertions.assertEquals(metaDTO.getNumStatements(), info.getNumStatements().get());
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
        blockDto
            .setSignerPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setBeneficiaryPublicKey(
            "B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        blockDto.setHeight(BigInteger.valueOf(9L));
        blockDto.setNetwork(NetworkTypeEnum.NUMBER_144);

        blockDto.setProofGamma("proofGamma");
        blockDto.setProofScalar("proofScalar");
        blockDto.setProofVerificationHash("proofVerificationHash");

        dto.setBlock(blockDto);

        mockRemoteCall(Collections.singletonList(dto));

        mockRemoteCall(toPage(dto));

        List<BlockInfo> resolvedList = repository
            .searchBlocks(new BlockSearchCriteria().offset("abc")).toFuture().get()
            .getData();

        BlockInfo info = resolvedList.get(0);
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
        Assertions.assertEquals(blockDto.getProofGamma(), info.getProofGamma());
        Assertions.assertEquals(blockDto.getProofScalar(), info.getProofScalar());
        Assertions.assertEquals(blockDto.getProofVerificationHash(), info.getProofVerificationHash());
    }


    private BlockPage toPage(BlockInfoDTO dto) {
        return new BlockPage()
            .data(Collections.singletonList(dto))
            .pagination(new Pagination().pageNumber(1).pageSize(2).totalEntries(3).totalPages(4));
    }

    @Override
    public BlockRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
