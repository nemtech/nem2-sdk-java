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
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
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
public class MosaicAddressRestrictionIntegrationTest extends BaseIntegrationTest {

    //TODO user regular test account, not nemesis.
    private Account testAccount = config().getNemesisAccount();
    private Account testAccount2 = config().getTestAccount2();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void createMosaicAddressRestrictionAndValidateEndpoints(RepositoryType type)
        throws InterruptedException {

        MosaicId mosaicId = createMosaic(type, testAccount);

        BigInteger restrictionKey = BigInteger.valueOf(60642);

        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                restrictionKey,
                BigInteger.valueOf(20),
                MosaicRestrictionType.GE
            ).build();

        announceAndValidate(
            type, testAccount, mosaicGlobalRestrictionTransaction);

        BigInteger newRestrictionValue = BigInteger.valueOf(30);

        MosaicAddressRestrictionTransaction mosaicAddressRestrictionTransaction =
            MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                restrictionKey,
                testAccount2.getAddress(),
                newRestrictionValue
            ).build();

        MosaicAddressRestrictionTransaction processedTransaction = announceAggregateAndValidate(
            type, testAccount, mosaicAddressRestrictionTransaction);

        Assertions.assertEquals(mosaicAddressRestrictionTransaction.getMosaicId(),
            processedTransaction.getMosaicId());

        Assertions.assertEquals(mosaicAddressRestrictionTransaction.getNewRestrictionValue(),
            processedTransaction.getNewRestrictionValue());

        Assertions.assertEquals(mosaicAddressRestrictionTransaction.getPreviousRestrictionValue(),
            processedTransaction.getPreviousRestrictionValue());

        Assertions.assertEquals(mosaicAddressRestrictionTransaction.getRestrictionKey(),
            processedTransaction.getRestrictionKey());

        Thread.sleep(1000);

        RestrictionRepository restrictionRepository = getRepositoryFactory(type)
            .createRestrictionRepository();

        assertMosaicAddressRestriction(mosaicAddressRestrictionTransaction, get(
            restrictionRepository
                .getMosaicAddressRestriction(mosaicId, testAccount2.getAddress())));

        assertMosaicAddressRestriction(mosaicAddressRestrictionTransaction, get(
            restrictionRepository
                .getMosaicAddressRestrictions(mosaicId,
                    Collections.singletonList(testAccount2.getAddress()))).get(0));

    }

    private void assertMosaicAddressRestriction(
        MosaicAddressRestrictionTransaction mosaicAddressRestrictionTransaction,
        MosaicAddressRestriction mosaicAddressRestriction) {

        BigInteger restrictionKey = mosaicAddressRestrictionTransaction.getRestrictionKey();
        BigInteger newRestrictionValue = mosaicAddressRestrictionTransaction
            .getNewRestrictionValue();

        Assertions.assertEquals(mosaicAddressRestrictionTransaction.getTargetAddress(),
            mosaicAddressRestriction.getTargetAddress());
        Assertions.assertEquals(1, mosaicAddressRestriction.getRestrictions().size());
        Assertions.assertEquals(newRestrictionValue,
            mosaicAddressRestriction.getRestrictions().get(restrictionKey)
        );
        Assertions
            .assertEquals(mosaicAddressRestrictionTransaction.getNewRestrictionValue(),
                mosaicAddressRestriction.getRestrictions().get(restrictionKey)
            );
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
                4, new BlockDuration(100)).build();

        MosaicDefinitionTransaction validateTransaction = announceAndValidate(type,
            testAccount, mosaicDefinitionTransaction);
        Assertions.assertEquals(mosaicId, validateTransaction.getMosaicId());
        return mosaicId;
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicAddressRestrictionWhenMosaicDoesNotExist(RepositoryType type) {
        RestrictionRepository repository = getRepositoryFactory(type).createRestrictionRepository();

        Address address = Address
            .createFromPublicKey("67F69FA4BFCD158F6E1AF1ABC82F725F5C5C4710D6E29217B12BE66397435DFB",
                getNetworkType());

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class,
                () -> get(repository
                    .getMosaicAddressRestriction(new MosaicId(BigInteger.valueOf(888888)),
                        address)));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'SCGEGBEHICF5PPOGIP2JSCQ5OYGZXOOJF7KUSUQJ'",
            exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicAddressRestrictionsWhenMosaicDoesNotExist(RepositoryType type) {

        Address address = Address
            .createFromPublicKey("67F69FA4BFCD158F6E1AF1ABC82F725F5C5C4710D6E29217B12BE66397435DFB",
                getNetworkType());

        RestrictionRepository repository = getRepositoryFactory(type).createRestrictionRepository();
        Assertions.assertEquals(0, get(repository
            .getMosaicAddressRestrictions(new MosaicId(BigInteger.valueOf(888888)),
                Collections.singletonList(address))).size());
    }


}
