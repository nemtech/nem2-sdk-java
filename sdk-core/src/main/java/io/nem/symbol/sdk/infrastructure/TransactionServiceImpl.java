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
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.receipt.ReceiptSource;
import io.nem.symbol.sdk.model.receipt.Statement;
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
import io.nem.symbol.sdk.model.transaction.TransactionInfo;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import java.util.List;
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
            r -> listener.confirmedOrError(signedTransaction.getSigner().getAddress(),
                signedTransaction.getHash()));
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
            r -> listener.aggregateBondedAddedOrError(signedAggregateTransaction.getSigner().getAddress(),
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
        Validate.isTrue(signedHashLockTransaction.getType() == TransactionType.HASH_LOCK,
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

        if (transaction.getType() == TransactionType.HASH_LOCK) {
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

        if (transaction.getType() == TransactionType.MOSAIC_METADATA) {
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

        Observable<Mosaic> resolvedMosaic = getResolvedMosaic(transaction, transaction.getMosaic(),
            statementObservable, expectedReceiptSource
        );

        return resolvedMosaic.map(mosaic -> HashLockTransactionFactory
            .create(transaction.getNetworkType(), mosaic, transaction.getDuration(),
                transaction.getHash()));
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        SecretLockTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<Address> resolvedAddress = getResolvedAddress(transaction,
            transaction.getRecipient(), statementObservable, expectedReceiptSource);
        Observable<Mosaic> resolvedMosaic = getResolvedMosaic(transaction, transaction.getMosaic(),
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
        Observable<Address> resolvedAddress = getResolvedAddress(transaction,
            transaction.getRecipient(), statementObservable, expectedReceiptSource);
        return resolvedAddress.map(address -> SecretProofTransactionFactory
            .create(transaction.getNetworkType(), transaction.getHashType(), address,
                transaction.getSecret(), transaction.getProof()));
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        TransferTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<List<Mosaic>> resolvedMosaics = Observable
            .fromIterable(transaction.getMosaics()).flatMap(
                m -> getResolvedMosaic(transaction, m, statementObservable, expectedReceiptSource))
            .toList().toObservable();

        Observable<Address> resolvedRecipient = getResolvedAddress(transaction,
            transaction.getRecipient(),
            statementObservable, expectedReceiptSource);

        BiFunction<Address, List<Mosaic>, TransferTransactionFactory> mergeFunction = (address, mosaics) ->
            TransferTransactionFactory
                .create(transaction.getNetworkType(), address, mosaics, transaction.getMessage());
        return Observable.combineLatest(resolvedRecipient, resolvedMosaics, mergeFunction);
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        MosaicGlobalRestrictionTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);
        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(transaction,
            transaction.getMosaicId(),
            statementObservable, expectedReceiptSource);

        Observable<MosaicId> resolvedReferenceMosaicId = getResolvedMosaicId(transaction,
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
        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(transaction,
            transaction.getMosaicId(),
            statementObservable, expectedReceiptSource);

        Observable<Address> resolvedTargetAddress = Observable
            .just(transaction.getTargetAddress())
            .flatMap(m -> getResolvedAddress(transaction, m, statementObservable,
                expectedReceiptSource));

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
        Observable<List<UnresolvedMosaicId>> unresolvedAdditions = getResolvedMosaicIds(transaction,
            transaction.getRestrictionAdditions(), statementObservable, expectedReceiptSource);

        Observable<List<UnresolvedMosaicId>> unresolvedDeletions = getResolvedMosaicIds(transaction,
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
        Observable<List<UnresolvedAddress>> unresolvedAdditions = getResolvedAddresses(transaction,
            transaction.getRestrictionAdditions(), statementObservable, expectedReceiptSource);

        Observable<List<UnresolvedAddress>> unresolvedDeletions = getResolvedAddresses(transaction,
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

        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(transaction,
            transaction.getTargetMosaicId(), statementObservable, expectedReceiptSource);

        return resolvedMosaicId.map(mosaicId -> MosaicMetadataTransactionFactory
            .create(transaction.getNetworkType(), transaction.getTargetAccount(), mosaicId,
                transaction.getScopedMetadataKey(), transaction.getValue())
            .valueSizeDelta(transaction.getValueSizeDelta()).valueSize(transaction.getValueSize()));
    }

    private Observable<TransactionFactory<? extends Transaction>> resolveTransactionFactory(
        MosaicSupplyChangeTransaction transaction, ReceiptSource expectedReceiptSource) {
        Observable<Statement> statementObservable = getStatement(transaction);

        Observable<MosaicId> resolvedMosaicId = getResolvedMosaicId(transaction,
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
        return receiptRepository.getBlockReceipts(getTransactionInfo(transaction)
            .getHeight()).cache();
    }


    private Observable<List<UnresolvedMosaicId>> getResolvedMosaicIds(Transaction transaction,
        List<UnresolvedMosaicId> unresolvedMosaicIds, Observable<Statement> statementObservable,
        ReceiptSource expectedReceiptSource) {
        return Observable.fromIterable(unresolvedMosaicIds)
            .flatMap(unresolved -> getResolvedMosaicId(transaction, unresolved, statementObservable,
                expectedReceiptSource)).map(m -> (UnresolvedMosaicId) m).toList().toObservable();
    }

    private Observable<List<UnresolvedAddress>> getResolvedAddresses(Transaction transaction,
        List<UnresolvedAddress> unresolvedMosaicIds, Observable<Statement> statementObservable,
        ReceiptSource expectedReceiptSource) {
        return Observable.fromIterable(unresolvedMosaicIds)
            .flatMap(unresolved -> getResolvedAddress(transaction, unresolved, statementObservable,
                expectedReceiptSource)).map(m -> (UnresolvedAddress) m).toList().toObservable();

    }

    private Observable<Mosaic> getResolvedMosaic(Transaction transaction, Mosaic unresolvedMosaic,
        Observable<Statement> statementObservable, ReceiptSource expectedReceiptSource) {
        return Observable.just(unresolvedMosaic)
            .flatMap(m -> getResolvedMosaicId(transaction, m.getId(), statementObservable,
                expectedReceiptSource).map(mId -> new Mosaic(mId, m.getAmount())));
    }

    private Observable<MosaicId> getResolvedMosaicId(
        Transaction transaction,
        UnresolvedMosaicId unresolvedMosaicId,
        Observable<Statement> statementObservable, ReceiptSource expectedReceiptSource) {
        if (unresolvedMosaicId instanceof MosaicId) {
            return Observable.just((MosaicId) unresolvedMosaicId);
        }
        return statementObservable.map(statement -> statement
            .getResolvedMosaicId(getTransactionInfo(transaction).getHeight(), unresolvedMosaicId,
                expectedReceiptSource.getPrimaryId(),
                expectedReceiptSource.getSecondaryId())
            .orElseThrow(() -> new IllegalArgumentException(
                "MosaicId could not be resolved for alias "
                    + unresolvedMosaicId.getIdAsHex())));
    }


    private Observable<Address> getResolvedAddress(Transaction transaction,
        UnresolvedAddress unresolvedAddress,
        Observable<Statement> statementObservable, ReceiptSource expectedReceiptSource) {
        if (unresolvedAddress instanceof Address) {
            return Observable.just((Address) unresolvedAddress);
        }
        return statementObservable.map(statement -> statement
            .getResolvedAddress(getTransactionInfo(transaction).getHeight(), unresolvedAddress,
                expectedReceiptSource.getPrimaryId(),
                expectedReceiptSource.getSecondaryId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Address could not be resolved for alias "
                    + ((NamespaceId) unresolvedAddress).getIdAsHex())));
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

    private TransactionInfo getTransactionInfo(Transaction transaction) {
        return transaction.getTransactionInfo().orElseThrow(() ->
            new IllegalArgumentException(
                "Transaction Info is required in " + transaction.getType() + " transaction"));
    }

}
