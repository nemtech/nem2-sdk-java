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

import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RestrictionAccountRepository;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRestrictionIntegrationTest extends BaseIntegrationTest {

  private Account testAccount;

  @BeforeAll
  void setup() {
    testAccount = helper().createTestAccount(TestHelper.DEFAULT_REPOSITORY_TYPE);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void addAndRemoveTransactionRestriction(RepositoryType type) {

    AccountOperationRestrictionFlags restrictionFlags =
        AccountOperationRestrictionFlags.BLOCK_OUTGOING_TRANSACTION_TYPE;
    TransactionType transactionType = TransactionType.SECRET_PROOF;

    Assertions.assertNotNull(
        get(
            getRepositoryFactory(type)
                .createAccountRepository()
                .getAccountInfo(testAccount.getAddress())));

    if (hasRestriction(type, testAccount, restrictionFlags, transactionType)) {
      System.out.println("Removing existing transaction restriction!");
      sendAccountRestrictionTransaction(type, transactionType, false, restrictionFlags);
      Assertions.assertFalse(hasRestriction(type, testAccount, restrictionFlags, transactionType));
    }

    System.out.println("Adding transaction restriction");
    sendAccountRestrictionTransaction(type, transactionType, true, restrictionFlags);

    Assertions.assertTrue(hasRestriction(type, testAccount, restrictionFlags, transactionType));

    //    System.out.println("Removing transaction restriction");
    //    sendAccountRestrictionTransaction(type, transactionType, false, restrictionFlags);
    //
    //    Assertions.assertFalse(hasRestriction(type, testAccount, restrictionFlags,
    // transactionType));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void addAndRemoveMosaicRestriction(RepositoryType type) {

    AccountMosaicRestrictionFlags restrictionFlags =
        AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC;

    MosaicNonce nonce = MosaicNonce.createRandom();
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, testAccount.getPublicAccount());

    Assertions.assertNotNull(
        get(
            getRepositoryFactory(type)
                .createAccountRepository()
                .getAccountInfo(testAccount.getAddress())));

    if (hasRestriction(type, testAccount, restrictionFlags, mosaicId)) {
      System.out.println("Removing existing mosaic restriction!");
      sendAccountRestrictionMosaic(type, mosaicId, false, restrictionFlags);
      Assertions.assertFalse(hasRestriction(type, testAccount, restrictionFlags, mosaicId));
    }

    System.out.println("Adding mosaic restriction");
    sendAccountRestrictionMosaic(type, mosaicId, true, restrictionFlags);

    Assertions.assertTrue(hasRestriction(type, testAccount, restrictionFlags, mosaicId));

    System.out.println("Removing mosaic restriction");
    sendAccountRestrictionMosaic(type, mosaicId, false, restrictionFlags);

    Assertions.assertFalse(hasRestriction(type, testAccount, restrictionFlags, mosaicId));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void addAndRemoveAddressRestriction(RepositoryType type) {

    AccountAddressRestrictionFlags restrictionFlags =
        AccountAddressRestrictionFlags.ALLOW_OUTGOING_ADDRESS;
    Address address = getRecipient();

    Assertions.assertNotNull(
        get(
            getRepositoryFactory(type)
                .createAccountRepository()
                .getAccountInfo(testAccount.getAddress())));

    if (hasRestriction(type, testAccount, restrictionFlags, address)) {
      System.out.println("Removing existing address restriction!");
      sendAccountRestrictionAddress(type, address, false, restrictionFlags);
      Assertions.assertFalse(hasRestriction(type, testAccount, restrictionFlags, address));
    }

    System.out.println("Adding address restriction");
    sendAccountRestrictionAddress(type, address, true, restrictionFlags);

    Assertions.assertTrue(hasRestriction(type, testAccount, restrictionFlags, address));

    System.out.println("Removing address restriction");
    sendAccountRestrictionAddress(type, address, false, restrictionFlags);

    Assertions.assertFalse(hasRestriction(type, testAccount, restrictionFlags, address));
  }

  private boolean hasRestriction(
      RepositoryType type,
      Account testAccount,
      AccountRestrictionFlags restrictionFlags,
      Object value) {
    try {
      AccountRestrictions restrictions =
          get(
              getRepositoryFactory(type)
                  .createRestrictionAccountRepository()
                  .getAccountRestrictions(testAccount.getAddress()));
      Assertions.assertEquals(testAccount.getAddress(), restrictions.getAddress());

      System.out.println("Current Restrictions: " + jsonHelper().print(restrictions));
      return restrictions.getRestrictions().stream()
          .anyMatch(
              r ->
                  r.getRestrictionFlags().equals(restrictionFlags)
                      && r.getValues().contains(value));
    } catch (Exception e) {
      // If it fails, it's because is a new account.
      Assertions.assertEquals(
          "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '"
              + testAccount.getAddress().plain()
              + "'",
          e.getMessage());
      return false;
    }
  }

  private void sendAccountRestrictionTransaction(
      RepositoryType type,
      TransactionType transactionType,
      boolean add,
      AccountOperationRestrictionFlags accountRestrictionFlags) {

    List<TransactionType> additions =
        add ? Collections.singletonList(transactionType) : Collections.emptyList();
    List<TransactionType> deletions =
        !add ? Collections.singletonList(transactionType) : Collections.emptyList();

    AccountOperationRestrictionTransaction transaction =
        AccountOperationRestrictionTransactionFactory.create(
                getNetworkType(), getDeadline(), accountRestrictionFlags, additions, deletions)
            .maxFee(maxFee)
            .build();

    AccountOperationRestrictionTransaction processedTransaction =
        announceAndValidate(type, testAccount, transaction);

    Assertions.assertEquals(accountRestrictionFlags, processedTransaction.getRestrictionFlags());
    Assertions.assertEquals(additions, processedTransaction.getRestrictionAdditions());
    Assertions.assertEquals(deletions, processedTransaction.getRestrictionDeletions());
  }

  private void sendAccountRestrictionMosaic(
      RepositoryType type,
      UnresolvedMosaicId unresolvedMosaicId,
      boolean add,
      AccountMosaicRestrictionFlags accountRestrictionFlags) {

    List<UnresolvedMosaicId> additions =
        add ? Collections.singletonList(unresolvedMosaicId) : Collections.emptyList();
    List<UnresolvedMosaicId> deletions =
        !add ? Collections.singletonList(unresolvedMosaicId) : Collections.emptyList();

    AccountMosaicRestrictionTransaction transaction =
        AccountMosaicRestrictionTransactionFactory.create(
                getNetworkType(), getDeadline(), accountRestrictionFlags, additions, deletions)
            .maxFee(maxFee)
            .build();

    AccountMosaicRestrictionTransaction processedTransaction =
        announceAndValidate(type, testAccount, transaction);

    Assertions.assertEquals(accountRestrictionFlags, processedTransaction.getRestrictionFlags());
    Assertions.assertEquals(additions, processedTransaction.getRestrictionAdditions());
    Assertions.assertEquals(deletions, processedTransaction.getRestrictionDeletions());
  }

  private void sendAccountRestrictionAddress(
      RepositoryType type,
      UnresolvedAddress unresolvedAddress,
      boolean add,
      AccountAddressRestrictionFlags accountRestrictionFlags) {

    List<UnresolvedAddress> additions =
        add ? Collections.singletonList(unresolvedAddress) : Collections.emptyList();
    List<UnresolvedAddress> deletions =
        !add ? Collections.singletonList(unresolvedAddress) : Collections.emptyList();

    AccountAddressRestrictionTransaction transaction =
        AccountAddressRestrictionTransactionFactory.create(
                getNetworkType(), getDeadline(), accountRestrictionFlags, additions, deletions)
            .maxFee(maxFee)
            .build();

    AccountAddressRestrictionTransaction processedTransaction =
        announceAndValidate(type, testAccount, transaction);

    Assertions.assertEquals(accountRestrictionFlags, processedTransaction.getRestrictionFlags());
    Assertions.assertEquals(accountRestrictionFlags, processedTransaction.getRestrictionFlags());
    Assertions.assertEquals(additions, processedTransaction.getRestrictionAdditions());
    Assertions.assertEquals(deletions, processedTransaction.getRestrictionDeletions());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getAccountRestrictionsWhenAccountDoesNotExist(RepositoryType type) {
    RestrictionAccountRepository repository =
        getRepositoryFactory(type).createRestrictionAccountRepository();

    Address address =
        Address.createFromPublicKey(
            "67F69FA4BFCD158F6E1AF1ABC82F725F5C5C4710D6E29217B12BE66397435DFB", getNetworkType());

    RepositoryCallException exception =
        Assertions.assertThrows(
            RepositoryCallException.class, () -> get(repository.getAccountRestrictions(address)));
    Assertions.assertTrue(
        exception
            .getMessage()
            .contains(
                "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id"));
  }
}
