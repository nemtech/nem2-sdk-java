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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.api.BlockOrderBy;
import io.nem.symbol.sdk.api.BlockPaginationStreamer;
import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.BlockSearchCriteria;
import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockRepositoryIntegrationTest extends BaseIntegrationTest {


    private BlockRepository getBlockRepository(RepositoryType type) {
        return getRepositoryFactory(type).createBlockRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockByHeight(RepositoryType type) {
        BlockInfo blockInfo = get(getBlockRepository(type).getBlockByHeight(BigInteger.valueOf(1)));
        assertEquals(1, blockInfo.getHeight().intValue());
        assertEquals(0, blockInfo.getTimestamp().intValue());
        assertNotEquals(getGenerationHash(), blockInfo.getGenerationHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchOrderByHeightAsc(RepositoryType type) {
        BlockSearchCriteria criteria = new BlockSearchCriteria();
        criteria.setOrderBy(BlockOrderBy.HEIGHT);
        criteria.setOrder(OrderBy.ASC);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(getBlockRepository(type));

        List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
        List<BlockInfo> sorted = blocks.stream().sorted(Comparator.comparing(BlockInfo::getHeight))
            .collect(Collectors.toList());
        Assertions.assertEquals(blocks, sorted);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchByBeneficiaryPublicKey(RepositoryType type) {
        BlockRepository blockRepository = getBlockRepository(type);
        BlockInfo block1 = get(blockRepository.getBlockByHeight(BigInteger.ONE));
        BlockSearchCriteria criteria = new BlockSearchCriteria();
        PublicKey expectedBeneficiaryPublicKey = block1.getBeneficiaryPublicAccount().getPublicKey();
        criteria.setBeneficiaryPublicKey(expectedBeneficiaryPublicKey);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
        List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
        blocks.forEach(
            b -> Assertions.assertEquals(expectedBeneficiaryPublicKey, b.getBeneficiaryPublicAccount().getPublicKey()));
        Assertions.assertFalse(blocks.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchByBeneficiaryPublicKeyWhenInvalid(RepositoryType type) {
        BlockRepository blockRepository = getBlockRepository(type);
        BlockSearchCriteria criteria = new BlockSearchCriteria();
        PublicKey expectedBeneficiaryPublicKey = PublicKey.generateRandom();
        criteria.setBeneficiaryPublicKey(expectedBeneficiaryPublicKey);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
        List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
        Assertions.assertTrue(blocks.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchBySignerPublicKey(RepositoryType type) {
        BlockRepository blockRepository = getBlockRepository(type);
        BlockInfo block1 = get(blockRepository.getBlockByHeight(BigInteger.ONE));
        BlockSearchCriteria criteria = new BlockSearchCriteria();
        PublicKey expectedSignerPublicKey = block1.getSignerPublicAccount().getPublicKey();
        criteria.setSignerPublicKey(expectedSignerPublicKey);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
        List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
        blocks
            .forEach(b -> Assertions.assertEquals(expectedSignerPublicKey, b.getSignerPublicAccount().getPublicKey()));
        Assertions.assertFalse(blocks.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchBySignerPublicKeyWhenInvalid(RepositoryType type) {
        BlockRepository blockRepository = getBlockRepository(type);
        BlockSearchCriteria criteria = new BlockSearchCriteria();
        PublicKey expectedSignerPublicKey = PublicKey.generateRandom();
        criteria.setSignerPublicKey(expectedSignerPublicKey);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
        List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
        Assertions.assertTrue(blocks.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchOrderByHeightDesc(RepositoryType type) {
        BlockSearchCriteria criteria = new BlockSearchCriteria();
        criteria.setOrderBy(BlockOrderBy.HEIGHT);
        criteria.setOrder(OrderBy.DESC);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(getBlockRepository(type));

        List<BlockInfo> blocks = get(streamer.search(criteria).toList().toObservable());
        List<BlockInfo> sorted = blocks.stream().sorted(Comparator.comparing(BlockInfo::getHeight).reversed())
            .collect(Collectors.toList());
        Assertions.assertEquals(blocks, sorted);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearch(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(null);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearchSize50(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(50);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearchBlock(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(null);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void defaultSearchBlockPageSize50(RepositoryType type) {
        getPaginationTester(type).basicTestSearch(50);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchOrderByIdAsc(RepositoryType type) {
        getPaginationTester(type).searchOrderByIdAsc();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void usingBigPageSize(RepositoryType type) {
        getPaginationTester(type).usingBigPageSize();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchUsingOffset(RepositoryType type) {
        BlockRepository blockRepository = getBlockRepository(type);
        BlockPaginationStreamer streamer = new BlockPaginationStreamer(blockRepository);
        BlockSearchCriteria criteria = new BlockSearchCriteria();
        criteria.setPageSize(10);
        int offsetIndex = 2;
        List<BlockInfo> blocksWithoutOffset = get(streamer.search(criteria).toList().toObservable());
        criteria.setOffset(blocksWithoutOffset.get(offsetIndex).getDatabaseId().get());
        List<BlockInfo> blockFromOffsets = get(streamer.search(criteria).toList().toObservable());
        PaginationTester.sameEntities(blocksWithoutOffset.stream().skip(offsetIndex + 1).collect(Collectors.toList()), blockFromOffsets);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchOrderByIdDesc(RepositoryType type) {
        getPaginationTester(type).searchOrderByIdDesc();
    }

    private PaginationTester<BlockInfo, BlockSearchCriteria> getPaginationTester(RepositoryType type) {
        return new PaginationTester<>(
            BlockSearchCriteria::new, getBlockRepository(type)::search);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenBlockDoesNotExists(RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(getBlockRepository(type)
                .getBlockByHeight(BigInteger.valueOf(0))));

        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '0'",
            exception.getMessage());
    }
}
