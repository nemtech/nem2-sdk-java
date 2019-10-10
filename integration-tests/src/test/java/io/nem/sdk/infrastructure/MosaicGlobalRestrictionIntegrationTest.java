/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.RestrictionRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MosaicGlobalRestrictionIntegrationTest extends BaseIntegrationTest {

    Account testAccount = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void createMosaicGlobalRestrictionAndValidateEndpoints(RepositoryType type)
        throws InterruptedException {

        //1) Create a new mosaic
        MosaicId mosaicId = createMosaic(type);
        BigInteger restrictionKey = BigInteger.valueOf(60641);

        //2) Create a restriction on the mosaic

        BigInteger originalValue = BigInteger.valueOf(20);
        MosaicRestrictionType originalRestrictionType = MosaicRestrictionType.GE;
        MosaicGlobalRestrictionTransaction createTransaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                restrictionKey,
                originalValue,
                originalRestrictionType
            ).build();

        //3) Announce the create restriction transaction
        MosaicGlobalRestrictionTransaction processedCreateTransaction = announceAndValidate(
            type, testAccount, createTransaction);
        //4) Validate that the received processedCreateTransaction and the create transaction are the same
        assertTransaction(createTransaction, processedCreateTransaction);

        //5) Validate the data from the endpoints
        sleep(1000);

        RestrictionRepository restrictionRepository = getRepositoryFactory(type)
            .createRestrictionRepository();

        assertMosaicGlobalRestriction(createTransaction, get(
            restrictionRepository.getMosaicGlobalRestriction(mosaicId)));

        assertMosaicGlobalRestriction(createTransaction, get(
            restrictionRepository
                .getMosaicGlobalRestrictions(Collections.singletonList(mosaicId))).get(0));

        // 6) Modifying the restriction by sending a new transaction with the previous values.
        MosaicGlobalRestrictionTransaction updateTransaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                restrictionKey,
                BigInteger.valueOf(40),
                MosaicRestrictionType.EQ
            ).previousRestrictionType(originalRestrictionType)
                .previousRestrictionValue(originalValue).build();


        //7) Announcing the update restriction transaction and checking the processed one.
        MosaicGlobalRestrictionTransaction processedUpdateTransaction = announceAndValidate(
            type, testAccount, updateTransaction);

        assertTransaction(updateTransaction, processedUpdateTransaction);


        //8) Validating that the endpoints show the new value and type.
        sleep(1000);

        assertMosaicGlobalRestriction(updateTransaction, get(
            restrictionRepository.getMosaicGlobalRestriction(mosaicId)));

        assertMosaicGlobalRestriction(updateTransaction, get(
            restrictionRepository
                .getMosaicGlobalRestrictions(Collections.singletonList(mosaicId))).get(0));

    }

    private void assertTransaction(MosaicGlobalRestrictionTransaction expectedTransaction,
        MosaicGlobalRestrictionTransaction processedTransaction) {
        Assertions.assertEquals(expectedTransaction.getMosaicId(),
            processedTransaction.getMosaicId());

        Assertions.assertEquals(expectedTransaction.getReferenceMosaicId(),
            processedTransaction.getReferenceMosaicId());

        Assertions.assertEquals(expectedTransaction.getNewRestrictionType(),
            processedTransaction.getNewRestrictionType());

        Assertions.assertEquals(expectedTransaction.getPreviousRestrictionType(),
            processedTransaction.getPreviousRestrictionType());

        Assertions.assertEquals(expectedTransaction.getNewRestrictionValue(),
            processedTransaction.getNewRestrictionValue());

        Assertions.assertEquals(expectedTransaction.getPreviousRestrictionValue(),
            processedTransaction.getPreviousRestrictionValue());

        Assertions.assertEquals(expectedTransaction.getRestrictionKey(),
            processedTransaction.getRestrictionKey());
    }

    private void assertMosaicGlobalRestriction(
        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction,
        MosaicGlobalRestriction mosaicGlobalRestriction) {

        BigInteger restrictionKey = mosaicGlobalRestrictionTransaction.getRestrictionKey();
        BigInteger newRestrictionValue = mosaicGlobalRestrictionTransaction
            .getNewRestrictionValue();

        Assertions.assertEquals(1, mosaicGlobalRestriction.getRestrictions().size());
        Assertions.assertEquals(newRestrictionValue,
            mosaicGlobalRestriction.getRestrictions().get(restrictionKey)
                .getRestrictionValue());

        Assertions.assertEquals(mosaicGlobalRestrictionTransaction.getNewRestrictionType(),
            mosaicGlobalRestriction.getRestrictions().get(restrictionKey)
                .getRestrictionType());

        Assertions.assertEquals(new MosaicId(BigInteger.ZERO),
            mosaicGlobalRestriction.getRestrictions().get(restrictionKey)
                .getReferenceMosaicId());
    }

    private MosaicId createMosaic(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, testAccount.getPublicAccount());

        System.out.println(mosaicId.getIdAsHex());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransactionFactory.create(getNetworkType(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                4, new BlockDuration(100)).build();

        MosaicDefinitionTransaction validateTransaction = announceAndValidate(type,
            testAccount, mosaicDefinitionTransaction);
        Assertions.assertEquals(mosaicId, validateTransaction.getMosaicId());
        return mosaicId;
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicGlobalRestrictionWhenMosaicDoesNotExist(RepositoryType type) {
        RestrictionRepository repository = getRepositoryFactory(type).createRestrictionRepository();

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class,
                () -> get(repository
                    .getMosaicGlobalRestriction(new MosaicId(BigInteger.valueOf(888888)))));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '00000000000d9038'",
            exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicGlobalRestrictionsWhenMosaicDoesNotExist(RepositoryType type) {
        RestrictionRepository repository = getRepositoryFactory(type).createRestrictionRepository();
        Assertions.assertEquals(0, get(repository
            .getMosaicGlobalRestrictions(
                Collections.singletonList(new MosaicId(BigInteger.valueOf(888888))))).size());
    }
}
