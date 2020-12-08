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

import io.nem.symbol.sdk.api.MosaicRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.PaginationStreamer;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MosaicAddressRestrictionIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void createMosaicAddressRestrictionAndValidateEndpoints(RepositoryType type) {
    Account testAccount = helper().createTestAccount(type);
    Account testAccount2 = config().getTestAccount2();
    // 1) Create a mosaic
    MosaicId mosaicId = createMosaic(type, testAccount);
    BigInteger restrictionKey = BigInteger.valueOf(22222);

    // 2) Create a global restriction on the mosaic
    MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction =
        MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey,
                BigInteger.valueOf(20),
                MosaicRestrictionType.GE)
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, testAccount, mosaicGlobalRestrictionTransaction);

    sleep(1000);

    // 3)Create a new MosaicAddressRestrictionTransaction
    BigInteger originalRestrictionValue = BigInteger.valueOf(30);

    Address targetAddress = testAccount2.getAddress();

    MosaicAddressRestrictionTransaction createTransaction =
        MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey,
                targetAddress,
                originalRestrictionValue)
            .maxFee(maxFee)
            .build();

    // 4)Announce and validate
    MosaicAddressRestrictionTransaction announce1 =
        announceAggregateAndValidate(type, createTransaction, testAccount).getLeft();

    sleep(1000);
    assertTransaction(restrictionKey, createTransaction, announce1);

    // 5) Validate that endpoints have the data.

    sleep(1000);

    RestrictionMosaicRepository restrictionRepository =
        getRepositoryFactory(type).createRestrictionMosaicRepository();

    assertMosaicAddressRestriction(
        restrictionRepository, targetAddress, createTransaction, targetAddress, mosaicId);

    // 6) Update the restriction
    MosaicAddressRestrictionTransaction updateTransaction =
        MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey,
                targetAddress,
                BigInteger.valueOf(40))
            .previousRestrictionValue(originalRestrictionValue)
            .maxFee(maxFee)
            .build();

    // 7) Announce and validate.
    MosaicAddressRestrictionTransaction announced =
        announceAggregateAndValidate(type, updateTransaction, testAccount).getLeft();

    sleep(1000);
    assertTransaction(restrictionKey, updateTransaction, announced);

    assertMosaicAddressRestriction(
        restrictionRepository, targetAddress, updateTransaction, targetAddress, mosaicId);
  }

  private void assertTransaction(
      BigInteger restrictionKey,
      MosaicAddressRestrictionTransaction expectedTransaction,
      MosaicAddressRestrictionTransaction processedTransaction) {
    Assertions.assertEquals(expectedTransaction.getMosaicId(), processedTransaction.getMosaicId());

    Assertions.assertEquals(restrictionKey, expectedTransaction.getRestrictionKey());
    Assertions.assertEquals(restrictionKey, processedTransaction.getRestrictionKey());

    Assertions.assertEquals(
        expectedTransaction.getNewRestrictionValue(),
        processedTransaction.getNewRestrictionValue());

    Assertions.assertEquals(
        expectedTransaction.getPreviousRestrictionValue(),
        processedTransaction.getPreviousRestrictionValue());

    Assertions.assertEquals(
        expectedTransaction.getRestrictionKey(), processedTransaction.getRestrictionKey());
  }

  private void assertMosaicAddressRestriction(
      RestrictionMosaicRepository restrictionRepository,
      Address address,
      MosaicAddressRestrictionTransaction transaction,
      Address targetAddress,
      MosaicId mosaicId) {

    Page<MosaicRestriction<?>> page =
        get(
            restrictionRepository.search(
                new MosaicRestrictionSearchCriteria()
                    .entryType(MosaicRestrictionEntryType.ADDRESS)
                    .targetAddress(targetAddress)
                    .mosaicId(mosaicId)));

    Assertions.assertEquals(
        1,
        page.getData().size(),
        "Cannot find restriction target address "
            + targetAddress.plain()
            + " encoded: "
            + targetAddress.encoded()
            + " mosaicId "
            + mosaicId.getIdAsHex());

    MosaicAddressRestriction restriction = (MosaicAddressRestriction) page.getData().get(0);

    BigInteger restrictionKey = transaction.getRestrictionKey();
    BigInteger newRestrictionValue = transaction.getNewRestrictionValue();

    Assertions.assertEquals(
        Collections.singleton(restrictionKey), restriction.getRestrictions().keySet());

    Assertions.assertEquals(address, restriction.getTargetAddress());
    Assertions.assertEquals(1, restriction.getRestrictions().size());
    Assertions.assertEquals(newRestrictionValue, restriction.getRestrictions().get(restrictionKey));

    Assertions.assertEquals(
        transaction.getNewRestrictionValue(), restriction.getRestrictions().get(restrictionKey));
  }

  private MosaicId createMosaic(RepositoryType type, Account testAccount) {
    return helper().createMosaic(testAccount, type, BigInteger.ZERO, null);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMosaicAddressRestrictionWhenMosaicDoesNotExist(RepositoryType type) {
    RestrictionMosaicRepository repository =
        getRepositoryFactory(type).createRestrictionMosaicRepository();

    Address address =
        Address.createFromPublicKey(
            "67F69FA4BFCD158F6E1AF1ABC82F725F5C5C4710D6E29217B12BE66397435DFB", getNetworkType());

    Page<MosaicRestriction<?>> page =
        get(
            repository.search(
                new MosaicRestrictionSearchCriteria()
                    .mosaicId(new MosaicId(BigInteger.valueOf(888888)))
                    .targetAddress(address)
                    .entryType(MosaicRestrictionEntryType.ADDRESS)));
    Assertions.assertTrue(page.getData().isEmpty());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void createMultiAddressRestrictions(RepositoryType type) {
    Account testAccount = helper().createTestAccount(type);
    Account testAccount2 = config().getTestAccount2();
    Account testAccount3 = config().getTestAccount3();
    // 1) Create a mosaic
    MosaicId mosaicId = createMosaic(type, testAccount);
    BigInteger restrictionKey1 = BigInteger.valueOf(11);
    BigInteger restrictionKey2 = BigInteger.valueOf(22);

    // 2) Create a global restriction on the mosaic
    MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction1 =
        MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey1,
                BigInteger.valueOf(20),
                MosaicRestrictionType.GE)
            .maxFee(maxFee)
            .build();

    // 2) Create a global restriction on the mosaic
    MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction2 =
        MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey2,
                BigInteger.valueOf(10),
                MosaicRestrictionType.GT)
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, testAccount, mosaicGlobalRestrictionTransaction1);
    announceAndValidate(type, testAccount, mosaicGlobalRestrictionTransaction2);

    sleep(1000);

    // 3)Create a new MosaicAddressRestrictionTransaction

    MosaicAddressRestrictionTransaction createTransaction1 =
        MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey1,
                testAccount2.getAddress(),
                BigInteger.valueOf(30))
            .maxFee(maxFee)
            .build();

    MosaicAddressRestrictionTransaction createTransaction2 =
        MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey1,
                testAccount3.getAddress(),
                BigInteger.valueOf(20))
            .maxFee(maxFee)
            .build();

    MosaicAddressRestrictionTransaction createTransaction3 =
        MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                mosaicId,
                restrictionKey2,
                testAccount3.getAddress(),
                BigInteger.valueOf(70))
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, testAccount, createTransaction1);
    announceAndValidate(type, testAccount, createTransaction2);
    announceAndValidate(type, testAccount, createTransaction3);

    sleep(1000);

    RestrictionMosaicRepository restrictionRepository =
        getRepositoryFactory(type).createRestrictionMosaicRepository();

    PaginationStreamer<MosaicRestriction<?>, MosaicRestrictionSearchCriteria> streamer =
        restrictionRepository.streamer();
    List<MosaicRestriction<?>> restrictions =
        get(
            streamer
                .search(
                    new MosaicRestrictionSearchCriteria().targetAddress(testAccount.getAddress()))
                .toList()
                .toObservable());

    Assertions.assertEquals(1, restrictions.size());
  }
}
