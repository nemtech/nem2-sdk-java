/*
 * Copyright 2019 NEM
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

import io.nem.sdk.api.Listener;
import io.nem.sdk.api.ReceiptRepository;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.api.TransactionService;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.receipt.ReceiptSource;
import io.nem.sdk.model.receipt.ResolutionEntry;
import io.nem.sdk.model.receipt.ResolutionStatement;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang3.Validate;

/**
 * Implementation of {@link TransactionService}. It  uses the repository interfaces. It works for
 * the different low level implementations like Vertx and Okhttp.
 */
public class TransactionServiceImpl implements TransactionService {

    /**
     * The @{@link TransactionRepository} used to query and announce the different transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * The @{@link ReceiptRepository} used to resolve the aliases.
     */
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
    public Observable<Transaction> announce(Listener listener,
        SignedTransaction signedTransaction) {
        Validate.notNull(signedTransaction, "signedTransaction is required");
        Observable<TransactionAnnounceResponse> announce = transactionRepository
            .announce(signedTransaction);
        return announce.flatMap(
            r -> listener.confirmed(signedTransaction.getSigner(), signedTransaction.getHash()));
    }

    @Override
    public Observable<AggregateTransaction> announceAggregateBonded(
        Listener listener, SignedTransaction signedAggregateTransaction) {
        Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
        Validate.isTrue(signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
            "signedAggregateTransaction type must be AGGREGATE_BONDED");
        Observable<TransactionAnnounceResponse> announce = transactionRepository
            .announceAggregateBonded(signedAggregateTransaction);
        return announce.flatMap(
            r -> listener.aggregateBondedAdded(signedAggregateTransaction.getSigner(),
                signedAggregateTransaction.getHash()));
    }

    @Override
    public Observable<AggregateTransaction> announceHashLockAggregateBonded(
        Listener listener, SignedTransaction signedHashLockTransaction,
        SignedTransaction signedAggregateTransaction) {
        Validate.notNull(signedHashLockTransaction, "signedHashLockTransaction is required");
        Validate.notNull(signedAggregateTransaction, "signedAggregateTransaction is required");
        Validate.isTrue(signedAggregateTransaction.getType() == TransactionType.AGGREGATE_BONDED,
            "signedAggregateTransaction type must be AGGREGATE_BONDED");
        Validate.isTrue(signedHashLockTransaction.getType() == TransactionType.LOCK,
            "signedHashLockTransaction type must be LOCK");
        return announce(listener, signedHashLockTransaction)
            .flatMap(t -> announceAggregateBonded(listener, signedAggregateTransaction));
    }

    @Override
    public Observable<List<Transaction>> resolveAliases(List<String> transactionHashes) {
        return transactionRepository.getTransactions(transactionHashes).flatMapIterable(a -> a)
            .flatMap(transaction -> resolveTransaction(transaction,
                createExpectedReceiptSource(transaction))).toList().toObservable();
    }


    private Observable<Transaction> resolveTransaction(Transaction transaction,
        ReceiptSource expectedSource) {
        return basicTransactionFactory(transaction, expectedSource).map(
            transactionTransactionFactory -> completeAndBuild(transactionTransactionFactory,
                transaction));
    }

    private Observable<TransactionFactory<? extends Transaction>> basicTransactionFactory(
        Transaction transaction, ReceiptSource expectedReceiptSource) {

        if (transaction.getType() == TransactionType.TRANSFER) {
            return resolveTransactionFactory((TransferTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.LOCK) {
            return resolveTransactionFactory((HashLockTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.SECRET_LOCK) {
            return resolveTransactionFactory((SecretLockTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.SECRET_PROOF) {
            return resolveTransactionFactory((SecretProofTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.MOSAIC_GLOBAL_RESTRICTION) {
            return resolveTransactionFactory((MosaicGlobalRestrictionTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.MOSAIC_ADDRESS_RESTRICTION) {
            return resolveTransactionFactory((MosaicAddressRestrictionTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.ACCOUNT_MOSAIC_RESTRICTION) {
            return resolveTransactionFactory((AccountMosaicRestrictionTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.MOSAIC_METADATA_TRANSACTION) {
            return resolveTransactionFactory((MosaicMetadataTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.ACCOUNT_ADDRESS_RESTRICTION) {
            return resolveTransactionFactory((AccountAddressRestrictionTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.MOSAIC_SUPPLY_CHANGE) {
            return resolveTransactionFactory((MosaicSupplyChangeTransaction) transaction,
                expectedReceiptSource);
        }

        if (transaction.getType() == TransactionType.AGGREGATE_COMPLETE
            || transaction.getType() == TransactionType.AGGREGATE_BONDED) {
            return resolveTransactionFactory((AggregateTransaction) transaction,
                expectedReceiptSource);
        }

        return Observable.just(new TransactionFactory<Transaction>(transaction.getType(),
            transaction.getNetworkType()) {
            @Override
            public Transaction build() {
                return transaction;
            }
        });
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        HashLockTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);

        Observable<Mosaic> resolvedMosaic = getResolvedMosaic(transaction.getMosaic(),
            statementObservable, expectedReceiptSource
        );

        return resolvedMosaic.map(mosaic -> HashLockTransactionFactory
            .create(transaction.getNetworkType(), mosaic, transaction.getDuration(),
                transaction.getHash()));
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        SecretLockTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<Address> resolvedAddress = getResolvedValue(transaction.getRecipient(),
            statementObservable, expectedReceiptSource);
        Observable<Mosaic> resolvedMosaic = getResolvedMosaic(transaction.getMosaic(),
            statementObservable, expectedReceiptSource
        );
        return Observable.combineLatest(resolvedAddress, resolvedMosaic,
            (address, mosaic) -> SecretLockTransactionFactory
                .create(transaction.getNetworkType(),
                    mosaic,
                    transaction.getDuration(),
                    transaction.getHashAlgorithm(),
                    transaction.getSecret(),
                    address));
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        SecretProofTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<Address> resolvedAddress = getResolvedValue(transaction.getRecipient(),
            statementObservable, expectedReceiptSource);
        return resolvedAddress.map(address -> SecretProofTransactionFactory
            .create(transaction.getNetworkType(), transaction.getHashType(), address,
                transaction.getSecret(), transaction.getProof()));
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        TransferTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<List<Mosaic>> resolvedMosaics = Observable
            .just(transaction.getMosaics()).flatMapIterable(m -> m)
            .flatMap(
                m -> getResolvedMosaic(m, statementObservable, expectedReceiptSource))
            .toList()
            .toObservable();

        Observable<Address> resolvedRecipient = getResolvedValue(transaction.getRecipient(),
            statementObservable, expectedReceiptSource);

        BiFunction<Address, List<Mosaic>, TransferTransactionFactory> mergeFunction = (address, mosaics) ->
            TransferTransactionFactory
                .create(transaction.getNetworkType(), address, mosaics, transaction.getMessage());
        return Observable.combineLatest(resolvedRecipient, resolvedMosaics, mergeFunction);
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        MosaicGlobalRestrictionTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(transaction.getMosaicId(),
            statementObservable, expectedReceiptSource);

        Observable<MosaicId> resolvedReferenceMosaicId = getResolvedMosaicId(
            transaction.getReferenceMosaicId(),
            statementObservable, expectedReceiptSource);

        return Observable.combineLatest(resolvedMosaicId, resolvedReferenceMosaicId,
            (mosaicId, referenceMosaicId) ->
            {
                MosaicGlobalRestrictionTransactionFactory factory = MosaicGlobalRestrictionTransactionFactory
                    .create(transaction.getNetworkType(), mosaicId,
                        transaction.getRestrictionKey(), transaction.getNewRestrictionValue(),
                        transaction.getNewRestrictionType());
                if (referenceMosaicId != null) {
                    factory.referenceMosaicId(referenceMosaicId);
                }
                return factory.previousRestrictionValue(transaction.getPreviousRestrictionValue())
                    .previousRestrictionType(transaction.getPreviousRestrictionType());
            });
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        MosaicAddressRestrictionTransaction transaction,
        ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(transaction.getMosaicId(),
            statementObservable, expectedReceiptSource);

        Observable<Address> resolvedTargetAddress = Observable
            .just(transaction.getTargetAddress())
            .flatMap(
                m -> getResolvedValue(m, statementObservable, expectedReceiptSource));

        BiFunction<? super MosaicId, ? super Address, MosaicAddressRestrictionTransactionFactory> mapper = (mosaicId, targetAddress) ->
            MosaicAddressRestrictionTransactionFactory
                .create(transaction.getNetworkType(), mosaicId,
                    transaction.getRestrictionKey(), targetAddress,
                    transaction.getNewRestrictionValue())
                .previousRestrictionValue(transaction.getPreviousRestrictionValue());
        return Observable.combineLatest(resolvedMosaicId, resolvedTargetAddress, mapper);
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        AccountMosaicRestrictionTransaction transaction,
        ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<List<UnresolvedMosaicId>> unresolvedAdditions = getResolvedMosaicIds(
            transaction.getRestrictionAdditions(), statementObservable, expectedReceiptSource);

        Observable<List<UnresolvedMosaicId>> unresolvedDeletions = getResolvedMosaicIds(
            transaction.getRestrictionDeletions(), statementObservable, expectedReceiptSource);

        BiFunction<List<UnresolvedMosaicId>, List<UnresolvedMosaicId>, TransactionFactory<AccountMosaicRestrictionTransaction>> mapper =
            (additions, deletions) -> AccountMosaicRestrictionTransactionFactory
                .create(transaction.getNetworkType(), transaction.getRestrictionFlags(), additions,
                    deletions);
        return Observable.combineLatest(unresolvedAdditions, unresolvedDeletions, mapper);
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        AccountAddressRestrictionTransaction transaction,
        ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<List<UnresolvedAddress>> unresolvedAdditions = getResolvedAddresses(
            transaction.getRestrictionAdditions(), statementObservable, expectedReceiptSource);

        Observable<List<UnresolvedAddress>> unresolvedDeletions = getResolvedAddresses(
            transaction.getRestrictionDeletions(), statementObservable, expectedReceiptSource);

        BiFunction<List<UnresolvedAddress>, List<UnresolvedAddress>, AccountAddressRestrictionTransactionFactory> mapper =
            (additions, deletions) -> AccountAddressRestrictionTransactionFactory
                .create(transaction.getNetworkType(), transaction.getRestrictionFlags(), additions,
                    deletions);
        return Observable.combineLatest(unresolvedAdditions, unresolvedDeletions, mapper);
    }


    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        MosaicMetadataTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);

        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(
            transaction.getTargetMosaicId(), statementObservable, expectedReceiptSource);

        return resolvedMosaicId.map(mosaicId -> MosaicMetadataTransactionFactory
            .create(transaction.getNetworkType(), transaction.getTargetAccount(), mosaicId,
                transaction.getScopedMetadataKey(), transaction.getValue())
            .valueSizeDelta(transaction.getValueSizeDelta()).valueSize(transaction.getValueSize()));
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        MosaicSupplyChangeTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);

        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(
            transaction.getMosaicId(), statementObservable, expectedReceiptSource);

        return resolvedMosaicId.map(mosaicId -> MosaicSupplyChangeTransactionFactory
            .create(transaction.getNetworkType(), mosaicId,
                transaction.getAction(), transaction.getDelta()));
    }


    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        AggregateTransaction transaction, ReceiptSource aggregateTransactionReceiptSource) {
        Observable<List<Transaction>> innerTransactions = Observable
            .just(transaction.getInnerTransactions()).flatMapIterable(m -> m)
            .flatMap(innerTransaction -> resolveTransaction(innerTransaction,
                createExpectedReceiptSource(aggregateTransactionReceiptSource, innerTransaction)))
            .toList().toObservable();

        return innerTransactions.map(txs -> AggregateTransactionFactory
            .create(transaction.getType(), transaction.getNetworkType(), txs,
                transaction.getCosignatures()));
    }

    private Transaction completeAndBuild(
        TransactionFactory<? extends Transaction> transactionFactory, Transaction transaction) {
        transactionFactory.maxFee(transaction.getMaxFee());
        transactionFactory.deadline(transaction.getDeadline());
        transactionFactory.version(transaction.getVersion());
        transaction.getSignature().ifPresent(transactionFactory::signature);
        transaction.getSigner().ifPresent(transactionFactory::signer);
        transaction.getTransactionInfo().ifPresent(transactionFactory::transactionInfo);
        return transactionFactory.build();
    }

    private Observable<Statement> getStatement(Transaction transaction) {
        return receiptRepository.getBlockReceipts(transaction.getTransactionInfo().orElseThrow(() ->
            new IllegalArgumentException(
                "Transaction Info is required in " + transaction.getType() + " transaction"))
            .getHeight()).cache();
    }

    private Observable<List<UnresolvedMosaicId>> getResolvedMosaicIds(
        List<UnresolvedMosaicId> unresolvedMosaicIds, Observable<Statement> statementObservable,
        ReceiptSource expectedReceiptSource) {
        return Observable.just(unresolvedMosaicIds).flatMapIterable(m -> m)
            .flatMap(unresolved -> getResolvedMosaicId(unresolved, statementObservable,
                expectedReceiptSource)).map(m -> (UnresolvedMosaicId) m).toList().toObservable();
    }

    private Observable<List<UnresolvedAddress>> getResolvedAddresses(
        List<UnresolvedAddress> unresolvedMosaicIds, Observable<Statement> statementObservable,
        ReceiptSource expectedReceiptSource) {
        return Observable.just(unresolvedMosaicIds).flatMapIterable(m -> m)
            .flatMap(unresolved -> getResolvedValue(unresolved, statementObservable,
                expectedReceiptSource)).map(m -> (UnresolvedAddress) m).toList().toObservable();

    }

    private Observable<Mosaic> getResolvedMosaic(Mosaic unresolvedMosaic,
        Observable<Statement> statementObservable, ReceiptSource expectedReceiptSource) {
        return Observable.just(unresolvedMosaic)
            .flatMap(m -> getResolvedMosaicId(m.getId(), statementObservable, expectedReceiptSource)
                .map(mId -> new Mosaic(mId, m.getAmount())));
    }

    private Observable<MosaicId> getResolvedMosaicId(UnresolvedMosaicId unresolvedMosaicId,
        Observable<Statement> statementObservable, ReceiptSource expectedReceiptSource) {
        if (!unresolvedMosaicId.isAlias()) {
            return Observable.just((MosaicId) unresolvedMosaicId);
        }
        return getResolvedValue(unresolvedMosaicId,
            statementObservable.map(Statement::getMosaicResolutionStatement),
            expectedReceiptSource);
    }


    private Observable<Address> getResolvedValue(UnresolvedAddress unresolvedAddress,
        Observable<Statement> statementObservable, ReceiptSource expectedReceiptSource) {
        if (!unresolvedAddress.isAlias()) {
            return Observable.just((Address) unresolvedAddress);
        }
        return getResolvedValue(unresolvedAddress,
            statementObservable.map(Statement::getAddressResolutionStatements),
            expectedReceiptSource);
    }

    private <R, U> Observable<R> getResolvedValue(U unresolved,
        Observable<List<? extends ResolutionStatement<U, R>>> resolutionsObservable,
        ReceiptSource expectedReceiptSource) {

        Predicate<ResolutionEntry<R>> sourceFilter = entry ->
            entry.getReceiptSource().getPrimaryId() == expectedReceiptSource.getPrimaryId()
                && entry.getReceiptSource().getSecondaryId() == expectedReceiptSource
                .getSecondaryId();
        return resolutionsObservable
            .map(resolutions -> resolutions.stream()
                .filter(mrs -> mrs.getUnresolved().equals(unresolved))
                .map(mrs -> mrs.getResolutionEntries().stream().filter(sourceFilter).findFirst()
                    .orElseThrow(
                        () -> new IllegalArgumentException("Resolution Entries is empty.")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "ResolutionStatement could not be found for unresolved "
                        + unresolved))).map(ResolutionEntry::getResolved);
    }

    private ReceiptSource createExpectedReceiptSource(Transaction transaction) {
        int transactionIndex = transaction.getTransactionInfo().flatMap(TransactionInfo::getIndex)
            .orElseThrow(() -> new IllegalArgumentException(
                "TransactionIndex cannot be loaded from Transaction " + transaction.getType()));
        return new ReceiptSource(transactionIndex + 1, 0);
    }

    private ReceiptSource createExpectedReceiptSource(
        ReceiptSource aggregateTransactionReceiptSource,
        Transaction transaction) {
        int transactionIndex = transaction.getTransactionInfo().flatMap(TransactionInfo::getIndex)
            .orElseThrow(() -> new IllegalArgumentException(
                "TransactionIndex cannot be loaded from Transaction " + transaction.getType()));
        return new ReceiptSource(aggregateTransactionReceiptSource.getPrimaryId(),
            transactionIndex + 1);
    }

}
