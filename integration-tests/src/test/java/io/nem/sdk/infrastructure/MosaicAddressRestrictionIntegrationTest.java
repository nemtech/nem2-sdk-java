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
import io.nem.sdk.api.RestrictionMosaicRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.namespace.NamespaceId;
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

    private Account testAccount = config().getDefaultAccount();
    private Account testAccount2 = config().getTestAccount2();

    BigInteger restrictionKey = BigInteger.valueOf(22222);

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void createMosaicAddressRestrictionAndValidateEndpoints(RepositoryType type)
        throws InterruptedException {

        //1) Create a mosaic
        MosaicId mosaicId = createMosaic(type, testAccount);

        //2) Create a global restriction on the mosaic
        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                restrictionKey,
                BigInteger.valueOf(20),
                MosaicRestrictionType.GE
            ).maxFee(this.maxFee).build();

        announceAndValidate(
            type, testAccount, mosaicGlobalRestrictionTransaction);

        //3)Create a new MosaicAddressRestrictionTransaction
        BigInteger originalRestrictionValue = BigInteger.valueOf(30);

        Address targetAddress = testAccount2.getAddress();
        NamespaceId targetAddressAlias = NamespaceId.createFromName("testaccount2");
        MosaicAddressRestrictionTransaction createTransaction =
            MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                restrictionKey,
                targetAddressAlias,
                originalRestrictionValue
            ).maxFee(this.maxFee).build();

        //4)Announce and validate
        assertTransaction(createTransaction, announceAggregateAndValidate(
            type, createTransaction, testAccount));

        //5) Validate that endpoints have the data.
        sleep(1000);

        RestrictionMosaicRepository restrictionRepository = getRepositoryFactory(type)
            .createRestrictionMosaicRepository();

        assertMosaicAddressRestriction(targetAddress, createTransaction, get(
            restrictionRepository
                .getMosaicAddressRestriction(mosaicId, targetAddress)));

        assertMosaicAddressRestriction(targetAddress, createTransaction, get(
            restrictionRepository
                .getMosaicAddressRestrictions(mosaicId,
                    Collections.singletonList(targetAddress))).get(0));

        //6) Update the restriction
        MosaicAddressRestrictionTransaction updateTransaction =
            MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                mosaicId,
                restrictionKey,
                targetAddress,
                BigInteger.valueOf(40)
            ).previousRestrictionValue(originalRestrictionValue).maxFee(this.maxFee).build();

        //7) Announce and validate.
        assertTransaction(updateTransaction, announceAggregateAndValidate(
            type, updateTransaction, testAccount));

        sleep(1000);

        //8) Validates that the endpoints have the new values
        assertMosaicAddressRestriction(targetAddress, updateTransaction, get(
            restrictionRepository
                .getMosaicAddressRestriction(mosaicId, targetAddress)));

        assertMosaicAddressRestriction(targetAddress, updateTransaction, get(
            restrictionRepository
                .getMosaicAddressRestrictions(mosaicId,
                    Collections.singletonList(targetAddress))).get(0));


    }

    private void assertTransaction(
        MosaicAddressRestrictionTransaction expectedTransaction,
        MosaicAddressRestrictionTransaction processedTransaction) {
        Assertions.assertEquals(expectedTransaction.getMosaicId(),
            processedTransaction.getMosaicId());

        Assertions.assertEquals(restrictionKey, expectedTransaction.getRestrictionKey());
        Assertions.assertEquals(restrictionKey, processedTransaction.getRestrictionKey());

        Assertions.assertEquals(expectedTransaction.getNewRestrictionValue(),
            processedTransaction.getNewRestrictionValue());

        Assertions.assertEquals(expectedTransaction.getPreviousRestrictionValue(),
            processedTransaction.getPreviousRestrictionValue());

        Assertions.assertEquals(expectedTransaction.getRestrictionKey(),
            processedTransaction.getRestrictionKey());
    }

    private void assertMosaicAddressRestriction(
        Address address, MosaicAddressRestrictionTransaction transaction,
        MosaicAddressRestriction restriction) {

        BigInteger restrictionKey = transaction.getRestrictionKey();
        BigInteger newRestrictionValue = transaction
            .getNewRestrictionValue();

        Assertions.assertEquals(Collections.singleton(restrictionKey),
            restriction.getRestrictions().keySet());

        Assertions.assertEquals(address, restriction.getTargetAddress());
        Assertions.assertEquals(1, restriction.getRestrictions().size());
        Assertions
            .assertEquals(newRestrictionValue, restriction.getRestrictions().get(restrictionKey));

        Assertions.assertEquals(transaction.getNewRestrictionValue(),
            restriction.getRestrictions().get(restrictionKey));
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

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicAddressRestrictionWhenMosaicDoesNotExist(RepositoryType type) {
        RestrictionMosaicRepository repository = getRepositoryFactory(type)
            .createRestrictionMosaicRepository();

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

        RestrictionMosaicRepository repository = getRepositoryFactory(type)
            .createRestrictionMosaicRepository();
        Assertions.assertEquals(0, get(repository
            .getMosaicAddressRestrictions(new MosaicId(BigInteger.valueOf(888888)),
                Collections.singletonList(address))).size());
    }


}
