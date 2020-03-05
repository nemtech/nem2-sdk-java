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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MosaicDefinitionTransactionIntegrationTest extends BaseIntegrationTest {

    Account account = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneMosaicDefinitionTransaction(RepositoryType type) {
        MosaicId mosaicId = createMosaic(type);
        sleep(1000);
        MosaicInfo mosaicInfo = get(
            getRepositoryFactory(type).createMosaicRepository().getMosaic(mosaicId));

        Assertions.assertEquals(mosaicId, mosaicInfo.getMosaicId());
        Assertions.assertEquals(4, mosaicInfo.getDivisibility());
        Assertions.assertTrue(mosaicInfo.isTransferable());
        Assertions.assertTrue(mosaicInfo.isSupplyMutable());
        Assertions.assertTrue(mosaicInfo.isRestrictable());
        Assertions.assertEquals(100, mosaicInfo.getDuration().intValue());
    }

    private MosaicId createMosaic(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransactionFactory.create(
                getNetworkType(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                4, new BlockDuration(100)).maxFee(this.maxFee).build();

        MosaicDefinitionTransaction processed = announceAndValidate(type,
            this.account, mosaicDefinitionTransaction);
        Assertions.assertEquals(mosaicId, processed.getMosaicId());
        Assertions
            .assertEquals(mosaicDefinitionTransaction.getMosaicNonce(), processed.getMosaicNonce());
        return mosaicId;
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicDefinitionTransaction(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransactionFactory.create(
                getNetworkType(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, false, true),
                4, new BlockDuration(100)).maxFee(this.maxFee).build();

        MosaicDefinitionTransaction processed = announceAggregateAndValidate(
            type, mosaicDefinitionTransaction, this.account).getLeft();
        Assertions
            .assertEquals(mosaicDefinitionTransaction.getMosaicNonce(), processed.getMosaicNonce());
        Assertions.assertEquals(mosaicId, processed.getMosaicId());
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneMosaicSupplyChangeTransaction(RepositoryType type) {
        MosaicId mosaicId = createMosaic(type);

        MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
            MosaicSupplyChangeTransactionFactory.create(getNetworkType(),
                mosaicId,
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(11)
            ).maxFee(this.maxFee).build();

        announceAndValidate(type, account, mosaicSupplyChangeTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicSupplyChangeTransaction(RepositoryType type) {

        MosaicId mosaicId = createMosaic(type);

        MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
            MosaicSupplyChangeTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(12)).maxFee(this.maxFee).build();

        announceAggregateAndValidate(type, mosaicSupplyChangeTransaction, account);


    }
}
