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

import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.ReceiptPaginationStreamer;
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.ResolutionStatementSearchCriteria;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ReceiptSource;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionInfo;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Implementation of {@link TransactionService}. It uses the repository interfaces. It works for the
 * different low level implementations like Vertx and Okhttp.
 */
public class TransactionServiceImpl implements TransactionService {

  /** The @{@link TransactionRepository} used to query and announce the different transactions. */
  private final TransactionRepository transactionRepository;

  /** The @{@link ReceiptRepository} used to resolve the aliases. */
  private final ReceiptRepository receiptRepository;

  /**
   * The constructor
   *
   * @param repositoryFactory the {@link RepositoryFactory} with the catapult server connection.
   */
  public TransactionServiceImpl(RepositoryFactory repositoryFactory) {
    this.transactionRepository = repositoryFactory.createTransactionRepository();
    this.receiptRepository = repositoryFactory.createReceiptRepository();
  }

  @Override
  public Observable<Transaction> announce(Listener listener, SignedTransaction signedTransaction) {
    Validate.notNull(signedTransaction, "signedTransaction is required");
    Observable<TransactionAnnounceResponse> announce =
        transactionRepository.announce(signedTransaction);
    return announce.flatMap(
        r ->
            listener.confirmedOrError(
                signedTransaction.getSigner().getAddress(), signedTransaction.getHash()));
  }

  @Override
  public Observable<AggregateTransaction> announceAggregateBonded(
      Listener listener, SignedTransaction signedAggregateTransaction) {
    Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
    Validate.isTrue(
        signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
        "signedAggregateTransaction type must be AGGREGATE_BONDED");
    Observable<TransactionAnnounceResponse> announce =
        transactionRepository.announceAggregateBonded(signedAggregateTransaction);
    return announce.flatMap(
        r ->
            listener.aggregateBondedAddedOrError(
                signedAggregateTransaction.getSigner().getAddress(),
                signedAggregateTransaction.getHash()));
  }

  @Override
  public Observable<AggregateTransaction> announceHashLockAggregateBonded(
      Listener listener,
      SignedTransaction signedHashLockTransaction,
      SignedTransaction signedAggregateTransaction) {
    Validate.notNull(signedHashLockTransaction, "signedHashLockTransaction is required");
    Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
    Validate.isTrue(
        signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
        "signedAggregateTransaction type must be AGGREGATE_BONDED");
    Validate.isTrue(
        signedHashLockTransaction.getType() == TransactionType.HASH_LOCK,
        "signedHashLockTransaction type must be LOCK");
    return announce(listener, signedHashLockTransaction)
        .flatMap(t -> announceAggregateBonded(listener, signedAggregateTransaction));
  }

  @Override
  public Observable<List<Transaction>> resolveAliases(List<String> transactionHashes) {
    return transactionRepository
        .getTransactions(TransactionGroup.CONFIRMED, transactionHashes)
        .flatMapIterable(a -> a)
        .flatMap(
            transaction ->
                resolveTransaction(transaction, createExpectedReceiptSource(transaction)))
        .toList()
        .toObservable();
  }

  private Observable<Transaction> resolveTransaction(
      Transaction transaction, ReceiptSource expectedSource) {

    Observable<List<AddressResolutionStatement>> addressResolutionStatements =
        getAddressResolutionStatements(transaction);
    Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements =
        getMosaicResolutionStatements(transaction);

    return resolveTransaction(
        transaction, expectedSource, addressResolutionStatements, mosaicResolutionStatements);
  }

  private Observable<Transaction> resolveTransaction(
      Transaction transaction,
      ReceiptSource expectedSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {

    return basicTransactionFactory(
            transaction, expectedSource, addressResolutionStatements, mosaicResolutionStatements)
        .map(
            transactionTransactionFactory ->
                completeAndBuild(transactionTransactionFactory, transaction));
  }

  private Observable<TransactionFactory<? extends Transaction>> basicTransactionFactory(
      Transaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {

    if (transaction.getType() == TransactionType.TRANSFER) {
      return resolveTransactionFactory(
          (TransferTransaction) transaction,
          expectedReceiptSource,
          addressResolutionStatements,
          mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.HASH_LOCK) {
      return resolveTransactionFactory(
          (HashLockTransaction) transaction, expectedReceiptSource, mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.SECRET_LOCK) {
      return resolveTransactionFactory(
          (SecretLockTransaction) transaction,
          expectedReceiptSource,
          addressResolutionStatements,
          mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.SECRET_PROOF) {
      return resolveTransactionFactory(
          (SecretProofTransaction) transaction, expectedReceiptSource, addressResolutionStatements);
    }

    if (transaction.getType() == TransactionType.MOSAIC_GLOBAL_RESTRICTION) {
      return resolveTransactionFactory(
          (MosaicGlobalRestrictionTransaction) transaction,
          expectedReceiptSource,
          mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.MOSAIC_ADDRESS_RESTRICTION) {
      return resolveTransactionFactory(
          (MosaicAddressRestrictionTransaction) transaction,
          expectedReceiptSource,
          addressResolutionStatements,
          mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.ACCOUNT_MOSAIC_RESTRICTION) {
      return resolveTransactionFactory(
          (AccountMosaicRestrictionTransaction) transaction,
          expectedReceiptSource,
          mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.MOSAIC_METADATA) {
      return resolveTransactionFactory(
          (MosaicMetadataTransaction) transaction,
          expectedReceiptSource,
          mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.ACCOUNT_ADDRESS_RESTRICTION) {
      return resolveTransactionFactory(
          (AccountAddressRestrictionTransaction) transaction,
          expectedReceiptSource,
          addressResolutionStatements);
    }

    if (transaction.getType() == TransactionType.MOSAIC_SUPPLY_CHANGE) {
      return resolveTransactionFactory(
          (MosaicSupplyChangeTransaction) transaction,
          expectedReceiptSource,
          mosaicResolutionStatements);
    }

    if (transaction.getType() == TransactionType.AGGREGATE_COMPLETE
        || transaction.getType() == TransactionType.AGGREGATE_BONDED) {
      return resolveTransactionFactory(
          (AggregateTransaction) transaction,
          expectedReceiptSource,
          addressResolutionStatements,
          mosaicResolutionStatements);
    }

    return Observable.just(
        new TransactionFactory<Transaction>(
            transaction.getType(), transaction.getNetworkType(), transaction.getDeadline()) {
          @Override
          public Transaction build() {
            return transaction;
          }
        });
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      HashLockTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {

    Observable<Mosaic> resolvedMosaic =
        getResolvedMosaic(
            transaction,
            transaction.getMosaic(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    return resolvedMosaic.map(
        mosaic ->
            HashLockTransactionFactory.create(
                transaction.getNetworkType(),
                transaction.getDeadline(),
                mosaic,
                transaction.getDuration(),
                transaction.getHash()));
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      SecretLockTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {
    Observable<Address> resolvedAddress =
        getResolvedAddress(
            transaction,
            transaction.getRecipient(),
            addressResolutionStatements,
            expectedReceiptSource);
    Observable<Mosaic> resolvedMosaic =
        getResolvedMosaic(
            transaction,
            transaction.getMosaic(),
            mosaicResolutionStatements,
            expectedReceiptSource);
    return Observable.combineLatest(
        resolvedAddress,
        resolvedMosaic,
        (address, mosaic) ->
            SecretLockTransactionFactory.create(
                transaction.getNetworkType(),
                transaction.getDeadline(),
                mosaic,
                transaction.getDuration(),
                transaction.getHashAlgorithm(),
                transaction.getSecret(),
                address));
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      SecretProofTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements) {

    Observable<Address> resolvedAddress =
        getResolvedAddress(
            transaction,
            transaction.getRecipient(),
            addressResolutionStatements,
            expectedReceiptSource);
    return resolvedAddress.map(
        address ->
            SecretProofTransactionFactory.create(
                transaction.getNetworkType(),
                transaction.getDeadline(),
                transaction.getHashType(),
                address,
                transaction.getSecret(),
                transaction.getProof()));
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      TransferTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {

    Observable<List<Mosaic>> resolvedMosaics =
        Observable.fromIterable(transaction.getMosaics())
            .flatMap(
                m ->
                    getResolvedMosaic(
                        transaction, m, mosaicResolutionStatements, expectedReceiptSource))
            .toList()
            .toObservable();

    Observable<Address> resolvedRecipient =
        getResolvedAddress(
            transaction,
            transaction.getRecipient(),
            addressResolutionStatements,
            expectedReceiptSource);

    BiFunction<Address, List<Mosaic>, TransferTransactionFactory> mergeFunction =
        (address, mosaics) ->
            TransferTransactionFactory.create(
                    transaction.getNetworkType(), transaction.getDeadline(), address, mosaics)
                .message(transaction.getMessage().orElse(null));
    return Observable.combineLatest(resolvedRecipient, resolvedMosaics, mergeFunction);
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      MosaicGlobalRestrictionTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {
    Observable<MosaicId> resolvedMosaicId =
        getResolvedMosaicId(
            transaction,
            transaction.getMosaicId(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    Observable<MosaicId> resolvedReferenceMosaicId =
        getResolvedMosaicId(
            transaction,
            transaction.getReferenceMosaicId(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    return Observable.combineLatest(
        resolvedMosaicId,
        resolvedReferenceMosaicId,
        (mosaicId, referenceMosaicId) -> {
          MosaicGlobalRestrictionTransactionFactory factory =
              MosaicGlobalRestrictionTransactionFactory.create(
                  transaction.getNetworkType(),
                  transaction.getDeadline(),
                  mosaicId,
                  transaction.getRestrictionKey(),
                  transaction.getNewRestrictionValue(),
                  transaction.getNewRestrictionType());
          if (referenceMosaicId != null) {
            factory.referenceMosaicId(referenceMosaicId);
          }
          return factory
              .previousRestrictionValue(transaction.getPreviousRestrictionValue())
              .previousRestrictionType(transaction.getPreviousRestrictionType());
        });
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      MosaicAddressRestrictionTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {
    Observable<MosaicId> resolvedMosaicId =
        getResolvedMosaicId(
            transaction,
            transaction.getMosaicId(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    Observable<Address> resolvedTargetAddress =
        Observable.just(transaction.getTargetAddress())
            .flatMap(
                m ->
                    getResolvedAddress(
                        transaction, m, addressResolutionStatements, expectedReceiptSource));

    BiFunction<? super MosaicId, ? super Address, MosaicAddressRestrictionTransactionFactory>
        mapper =
            (mosaicId, targetAddress) ->
                MosaicAddressRestrictionTransactionFactory.create(
                        transaction.getNetworkType(),
                        transaction.getDeadline(),
                        mosaicId,
                        transaction.getRestrictionKey(),
                        targetAddress,
                        transaction.getNewRestrictionValue())
                    .previousRestrictionValue(transaction.getPreviousRestrictionValue());
    return Observable.combineLatest(resolvedMosaicId, resolvedTargetAddress, mapper);
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      AccountMosaicRestrictionTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {
    Observable<List<UnresolvedMosaicId>> unresolvedAdditions =
        getResolvedMosaicIds(
            transaction,
            transaction.getRestrictionAdditions(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    Observable<List<UnresolvedMosaicId>> unresolvedDeletions =
        getResolvedMosaicIds(
            transaction,
            transaction.getRestrictionDeletions(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    BiFunction<
            List<UnresolvedMosaicId>,
            List<UnresolvedMosaicId>,
            TransactionFactory<AccountMosaicRestrictionTransaction>>
        mapper =
            (additions, deletions) ->
                AccountMosaicRestrictionTransactionFactory.create(
                    transaction.getNetworkType(),
                    transaction.getDeadline(),
                    transaction.getRestrictionFlags(),
                    additions,
                    deletions);
    return Observable.combineLatest(unresolvedAdditions, unresolvedDeletions, mapper);
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      AccountAddressRestrictionTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements) {
    Observable<List<UnresolvedAddress>> unresolvedAdditions =
        getResolvedAddresses(
            transaction,
            transaction.getRestrictionAdditions(),
            addressResolutionStatements,
            expectedReceiptSource);

    Observable<List<UnresolvedAddress>> unresolvedDeletions =
        getResolvedAddresses(
            transaction,
            transaction.getRestrictionDeletions(),
            addressResolutionStatements,
            expectedReceiptSource);

    BiFunction<
            List<UnresolvedAddress>,
            List<UnresolvedAddress>,
            AccountAddressRestrictionTransactionFactory>
        mapper =
            (additions, deletions) ->
                AccountAddressRestrictionTransactionFactory.create(
                    transaction.getNetworkType(),
                    transaction.getDeadline(),
                    transaction.getRestrictionFlags(),
                    additions,
                    deletions);
    return Observable.combineLatest(unresolvedAdditions, unresolvedDeletions, mapper);
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      MosaicMetadataTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {

    Observable<MosaicId> resolvedMosaicId =
        getResolvedMosaicId(
            transaction,
            transaction.getTargetMosaicId(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    return resolvedMosaicId.map(
        mosaicId ->
            MosaicMetadataTransactionFactory.create(
                    transaction.getNetworkType(),
                    transaction.getDeadline(),
                    transaction.getTargetAddress(),
                    mosaicId,
                    transaction.getScopedMetadataKey(),
                    transaction.getValue())
                .valueSizeDelta(transaction.getValueSizeDelta())
                .valueSize(transaction.getValueSize()));
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      MosaicSupplyChangeTransaction transaction,
      ReceiptSource expectedReceiptSource,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {

    Observable<MosaicId> resolvedMosaicId =
        getResolvedMosaicId(
            transaction,
            transaction.getMosaicId(),
            mosaicResolutionStatements,
            expectedReceiptSource);

    return resolvedMosaicId.map(
        mosaicId ->
            MosaicSupplyChangeTransactionFactory.create(
                transaction.getNetworkType(),
                transaction.getDeadline(),
                mosaicId,
                transaction.getAction(),
                transaction.getDelta()));
  }

  private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
      AggregateTransaction transaction,
      ReceiptSource aggregateTransactionReceiptSource,
      Observable<List<AddressResolutionStatement>> addressResolutionStatements,
      Observable<List<MosaicResolutionStatement>> mosaicResolutionStatements) {
    Observable<List<Transaction>> innerTransactions =
        Observable.just(transaction.getInnerTransactions())
            .flatMapIterable(m -> m)
            .flatMap(
                innerTransaction -> {
                  ReceiptSource expectedReceiptSource =
                      createExpectedReceiptSource(
                          aggregateTransactionReceiptSource, innerTransaction);
                  return resolveTransaction(
                      innerTransaction,
                      expectedReceiptSource,
                      addressResolutionStatements,
                      mosaicResolutionStatements);
                })
            .toList()
            .toObservable();

    return innerTransactions.map(
        txs ->
            AggregateTransactionFactory.create(
                transaction.getType(),
                transaction.getNetworkType(),
                transaction.getDeadline(),
                txs,
                transaction.getCosignatures()));
  }

  private Transaction completeAndBuild(
      TransactionFactory<? extends Transaction> transactionFactory, Transaction transaction) {
    transactionFactory.maxFee(transaction.getMaxFee());
    transactionFactory.version(transaction.getVersion());
    transaction.getSignature().ifPresent(transactionFactory::signature);
    transaction.getSigner().ifPresent(transactionFactory::signer);
    transaction.getTransactionInfo().ifPresent(transactionFactory::transactionInfo);
    return transactionFactory.build();
  }

  private Observable<List<MosaicResolutionStatement>> getMosaicResolutionStatements(
      Transaction transaction) {
    BigInteger height = getTransactionInfo(transaction).getHeight();
    return ReceiptPaginationStreamer.mosaics(receiptRepository)
        .search(new ResolutionStatementSearchCriteria().height(height))
        .toList()
        .toObservable()
        .cache();
  }

  private Observable<List<AddressResolutionStatement>> getAddressResolutionStatements(
      Transaction transaction) {
    BigInteger height = getTransactionInfo(transaction).getHeight();
    return ReceiptPaginationStreamer.addresses(receiptRepository)
        .search(new ResolutionStatementSearchCriteria().height(height))
        .toList()
        .toObservable()
        .cache();
  }

  private Observable<List<UnresolvedMosaicId>> getResolvedMosaicIds(
      Transaction transaction,
      List<UnresolvedMosaicId> unresolvedMosaicIds,
      Observable<List<MosaicResolutionStatement>> statementObservable,
      ReceiptSource expectedReceiptSource) {
    return Observable.fromIterable(unresolvedMosaicIds)
        .flatMap(
            unresolved ->
                getResolvedMosaicId(
                    transaction, unresolved, statementObservable, expectedReceiptSource))
        .map(m -> (UnresolvedMosaicId) m)
        .toList()
        .toObservable();
  }

  private Observable<List<UnresolvedAddress>> getResolvedAddresses(
      Transaction transaction,
      List<UnresolvedAddress> unresolvedMosaicIds,
      Observable<List<AddressResolutionStatement>> statementObservable,
      ReceiptSource expectedReceiptSource) {
    return Observable.fromIterable(unresolvedMosaicIds)
        .flatMap(
            unresolved ->
                getResolvedAddress(
                    transaction, unresolved, statementObservable, expectedReceiptSource))
        .map(m -> (UnresolvedAddress) m)
        .toList()
        .toObservable();
  }

  private Observable<Mosaic> getResolvedMosaic(
      Transaction transaction,
      Mosaic unresolvedMosaic,
      Observable<List<MosaicResolutionStatement>> statementObservable,
      ReceiptSource expectedReceiptSource) {
    return getResolvedMosaicId(
            transaction, unresolvedMosaic.getId(), statementObservable, expectedReceiptSource)
        .map(mId -> new Mosaic(mId, unresolvedMosaic.getAmount()));
  }

  private Observable<MosaicId> getResolvedMosaicId(
      Transaction transaction,
      UnresolvedMosaicId unresolvedMosaicId,
      Observable<List<MosaicResolutionStatement>> statementObservable,
      ReceiptSource expectedReceiptSource) {
    BigInteger height = getTransactionInfo(transaction).getHeight();
    return statementObservable.map(
        statements ->
            MosaicResolutionStatement.getResolvedMosaicId(
                    statements,
                    height,
                    unresolvedMosaicId,
                    expectedReceiptSource.getPrimaryId(),
                    expectedReceiptSource.getSecondaryId())
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "MosaicId could not be resolved for alias "
                                + unresolvedMosaicId.getIdAsHex())));
  }

  private Observable<Address> getResolvedAddress(
      Transaction transaction,
      UnresolvedAddress unresolvedAddress,
      Observable<List<AddressResolutionStatement>> statementObservable,
      ReceiptSource expectedReceiptSource) {
    BigInteger height = getTransactionInfo(transaction).getHeight();
    return statementObservable.map(
        statements ->
            AddressResolutionStatement.getResolvedAddress(
                    statements,
                    height,
                    unresolvedAddress,
                    expectedReceiptSource.getPrimaryId(),
                    expectedReceiptSource.getSecondaryId())
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Address could not be resolved for alias "
                                + ((NamespaceId) unresolvedAddress).getIdAsHex())));
  }

  private ReceiptSource createExpectedReceiptSource(Transaction transaction) {
    int transactionIndex =
        transaction
            .getTransactionInfo()
            .flatMap(TransactionInfo::getIndex)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "TransactionIndex cannot be loaded from Transaction "
                            + transaction.getType()));
    return new ReceiptSource(transactionIndex + 1, 0);
  }

  private ReceiptSource createExpectedReceiptSource(
      ReceiptSource aggregateTransactionReceiptSource, Transaction transaction) {
    int transactionIndex =
        transaction
            .getTransactionInfo()
            .flatMap(TransactionInfo::getIndex)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "TransactionIndex cannot be loaded from Transaction "
                            + transaction.getType()));
    return new ReceiptSource(
        aggregateTransactionReceiptSource.getPrimaryId(), transactionIndex + 1);
  }

  private TransactionInfo getTransactionInfo(Transaction transaction) {
    return transaction
        .getTransactionInfo()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Transaction Info is required in " + transaction.getType() + " transaction"));
  }
}
