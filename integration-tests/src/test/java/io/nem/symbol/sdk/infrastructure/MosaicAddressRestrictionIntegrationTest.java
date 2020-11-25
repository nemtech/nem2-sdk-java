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
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.restriction.MosaicAddressRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MosaicAddressRestrictionIntegrationTest extends BaseIntegrationTest {

  private Account testAccount;
  private Account testAccount2;
  private BigInteger restrictionKey;

  @BeforeEach
  void setup() {
    testAccount = config().getDefaultAccount();
    testAccount2 = config().getTestAccount2();
    restrictionKey = BigInteger.valueOf(22222);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void createMosaicAddressRestrictionAndValidateEndpoints(RepositoryType type) {

    // 1) Create a mosaic
    MosaicId mosaicId = createMosaic(type, testAccount);

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
    assertTransaction(createTransaction, announce1);

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
    assertTransaction(updateTransaction, announced);

    assertMosaicAddressRestriction(
        restrictionRepository, targetAddress, updateTransaction, targetAddress, mosaicId);
  }

  private void assertTransaction(
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
    MosaicNonce nonce = MosaicNonce.createRandom();
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, testAccount.getPublicAccount());

    System.out.println(mosaicId.getIdAsHex());

    MosaicDefinitionTransaction mosaicDefinitionTransaction =
        MosaicDefinitionTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                4,
                new BlockDuration(100))
            .maxFee(maxFee)
            .build();

    MosaicDefinitionTransaction validateTransaction =
        announceAndValidate(type, testAccount, mosaicDefinitionTransaction);
    Assertions.assertEquals(mosaicId, validateTransaction.getMosaicId());
    return mosaicId;
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
}
