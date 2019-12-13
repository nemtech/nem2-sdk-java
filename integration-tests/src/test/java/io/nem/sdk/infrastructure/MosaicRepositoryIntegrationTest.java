/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MosaicRepositoryIntegrationTest extends BaseIntegrationTest {

    private Account testAccount = config().getDefaultAccount();
    private List<MosaicId> mosaicIds = new ArrayList<>();
    private MosaicId mosaicId;

    @BeforeAll
    void setup() {
        mosaicId = createMosaic(DEFAULT_REPOSITORY_TYPE, testAccount);
        mosaicIds.add(mosaicId);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicsFromAccount(RepositoryType type) {
        List<MosaicInfo> mosaicInfos = get(getMosaicRepository(type)
            .getMosaicsFromAccount(testAccount.getAddress()));
        Assertions.assertTrue(mosaicInfos.size() > 0);
        mosaicInfos.forEach(this::assertMosaic);
        Assertions.assertTrue(
            mosaicInfos.stream().anyMatch(mosaicInfo -> mosaicInfo.getMosaicId().equals(mosaicId)));
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicsFromAccounts(RepositoryType type) {
        List<MosaicInfo> mosaicInfos = get(getMosaicRepository(type)
            .getMosaicsFromAccounts(Collections.singletonList(testAccount.getAddress())));
        Assertions.assertTrue(mosaicInfos.size() > 0);
        mosaicInfos.forEach(this::assertMosaic);
        Assertions.assertTrue(
            mosaicInfos.stream().anyMatch(mosaicInfo -> mosaicInfo.getMosaicId().equals(mosaicId)));
    }

    private void assertMosaic(MosaicInfo m) {
        Assertions.assertEquals(testAccount.getPublicAccount(), m.getOwner());
        Assertions.assertNotNull(m.getMosaicId());
        Assertions.assertNotNull(m.getStartHeight());
        Assertions.assertNotNull(m.getDuration());
        Assertions.assertNotNull(m.getRevision());
        Assertions.assertNotNull(m.getSupply());
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicViaMosaicId(RepositoryType type) {
        MosaicInfo mosaicInfo = get(getMosaicRepository(type).getMosaic(mosaicId));
        assertEquals(mosaicId, mosaicInfo.getMosaicId());
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicsNames(RepositoryType type) {
        List<MosaicNames> mosaicNames = get(getRepositoryFactory(type).createNamespaceRepository()
            .getMosaicsNames(Collections.singletonList(mosaicId)));
        assertEquals(1, mosaicNames.size());
        assertEquals(mosaicId, mosaicNames.get(0).getMosaicId());
        assertEquals(0, mosaicNames.get(0).getNames().size());
    }

    private MosaicRepository getMosaicRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createMosaicRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicsViaMosaicId(RepositoryType type) {
        List<MosaicInfo> mosaicsInfo = get(getMosaicRepository(type).getMosaics(mosaicIds));

        assertEquals(mosaicIds.size(), mosaicsInfo.size());
        assertEquals(mosaicIds.get(0), mosaicsInfo.get(0).getMosaicId());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenMosaicDoesNotExists(RepositoryType type) {
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(getMosaicRepository(type)
                .getMosaic(new MosaicId("AAAAAE18BE375DA2"))));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'aaaaae18be375da2'",
            exception.getMessage());
    }

    private MosaicId createMosaic(RepositoryType type, Account testAccount) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, testAccount.getPublicAccount());

        System.out.println(mosaicId.getIdAsHex());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransactionFactory.create(getNetworkType(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                4, new BlockDuration(100)).maxFee(this.maxFee).build();

        MosaicDefinitionTransaction validateTransaction = announceAndValidate(type,
            testAccount, mosaicDefinitionTransaction);
        Assertions.assertEquals(mosaicId, validateTransaction.getMosaicId());
        return mosaicId;
    }
}
