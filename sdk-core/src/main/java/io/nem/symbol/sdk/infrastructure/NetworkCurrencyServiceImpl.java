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

import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NetworkCurrencyService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrencyBuilder;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;

/**
 * Implementation of {@link NetworkCurrencyService}
 */
public class NetworkCurrencyServiceImpl implements NetworkCurrencyService {

    /**
     * The {@link TransactionRepository} used to load the block 1 transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * The {@link MosaicRepository}.
     */
    private final MosaicRepository mosaicRepository;

    /**
     * The {@link NamespaceRepository}.
     */
    private final NamespaceRepository namespaceRepository;

    /**
     * Constructor.
     *
     * @param repositoryFactory the repository factory.
     */
    public NetworkCurrencyServiceImpl(RepositoryFactory repositoryFactory) {
        this.transactionRepository = repositoryFactory.createTransactionRepository();
        this.mosaicRepository = repositoryFactory.createMosaicRepository();
        this.namespaceRepository = repositoryFactory.createNamespaceRepository();
    }

    /*
     * Implementation of the interface getNetworkCurrencies.
     *
     * TODO: ATM, rest endpoints doesn't allow proper pagination nor loading transaction per
     * transaction type. We are just loading the first page, if there are many transaction in block
     * 1, some of them related to currencies may not be there and the {@link NetworkCurrency} may be
     * incomplete.
     *
     * TODO: If block 1 has 1000s of transactions, this method may not be very efficient. Ideally we
     * would only load the transaction of a given type signed by the nemesis account.
     */
    @Override
    public Observable<List<NetworkCurrency>> getNetworkCurrenciesFromNemesis() {
        return getBlockTransactions(BigInteger.ONE, 1).map(transactions -> {

            List<MosaicDefinitionTransaction> mosaicTransactions = transactions.stream()
                .filter(t -> t.getType() == TransactionType.MOSAIC_DEFINITION)
                .map(t -> (MosaicDefinitionTransaction) t).collect(Collectors.toList());

            List<MosaicAliasTransaction> aliasTransactions = transactions.stream()
                .filter(t -> t.getType() == TransactionType.MOSAIC_ALIAS)
                .map(t -> (MosaicAliasTransaction) t).collect(Collectors.toList());

            List<NamespaceRegistrationTransaction> namespaceRegistrations = transactions.stream()
                .filter(t -> t.getType() == TransactionType.NAMESPACE_REGISTRATION)
                .map(t -> (NamespaceRegistrationTransaction) t).collect(Collectors.toList());

            Stream<Stream<NetworkCurrency>> streamStream = mosaicTransactions
                .stream()
                .map(mosaicTransaction -> {
                    List<MosaicAliasTransaction> mosaicAliasTransactions = aliasTransactions
                        .stream()
                        .filter(a -> a.getMosaicId().equals(mosaicTransaction.getMosaicId()))
                        .collect(Collectors.toList());
                    return mosaicAliasTransactions
                        .stream()
                        .map(mosaicAliasTransaction -> getNetworkCurrency(
                            mosaicTransaction, mosaicAliasTransaction,
                            namespaceRegistrations
                        )).filter(Optional::isPresent).map(Optional::get);

                });
            return streamStream.flatMap(Function.identity())
                .collect(Collectors.toList());
        });
    }


    @Override
    public Observable<NetworkCurrency> getNetworkCurrencyFromMosaicId(MosaicId mosaicId) {
        Validate.notNull(mosaicId, "namespaceId is required");
        return this.mosaicRepository.getMosaic(mosaicId).flatMap(info -> {
            Observable<Optional<NamespaceId>> namespaceIdObservable = this.namespaceRepository
                .getMosaicsNames(Collections.singletonList(mosaicId)).flatMapIterable(item -> item)
                .map(mosaicNames -> {
                    if (mosaicNames.getNames().isEmpty()) {
                        return Optional.<NamespaceId>empty();
                    }

                    return Optional.of(NamespaceId
                        .createFromName(mosaicNames.getNames().get(0).getName()));
                })
                .first(Optional.empty()).onErrorReturnItem(Optional.empty()).toObservable();

            return namespaceIdObservable
                .map(namespaceIdOptional -> createNetworkCurrencyBuilder(mosaicId,
                    namespaceIdOptional.orElse(null), info.getDivisibility())
                    .withSupplyMutable(info.isSupplyMutable())
                    .withTransferable(info.isTransferable()).build());
        });
    }

    @Override
    public Observable<NetworkCurrency> getNetworkCurrencyFromNamespaceId(NamespaceId namespaceId) {
        Validate.notNull(namespaceId, "namespaceId is required");
        return this.namespaceRepository.getLinkedMosaicId(namespaceId)
            .flatMap(mosaicId -> this.mosaicRepository
                .getMosaic(mosaicId)
                .map(info -> createNetworkCurrencyBuilder(mosaicId, namespaceId,
                    info.getDivisibility()).withSupplyMutable(info.isSupplyMutable())
                    .withTransferable(info.isTransferable()).build()));
    }

    /**
     * This method tries to {@link NetworkCurrency} from the original {@link
     * MosaicDefinitionTransaction} and {@link MosaicAliasTransaction}.
     *
     * @param mosaicTransaction the original mosiac transaction
     * @param mosaicAliasTransaction the original mosaic alias transaction used to know the
     * mosaic/currency namespace
     * @param namespaceRegistrations the list of namespace registration used to resolve the
     * mosaic/currency full name
     * @return the {@link NetworkCurrency} if it can be resolved.
     */
    private Optional<NetworkCurrency> getNetworkCurrency(
        MosaicDefinitionTransaction mosaicTransaction,
        MosaicAliasTransaction mosaicAliasTransaction,
        List<NamespaceRegistrationTransaction> namespaceRegistrations) {
        MosaicId mosaicId = mosaicAliasTransaction.getMosaicId();

        Optional<String> namespaceNameOptional = getNamespaceFullName(
            namespaceRegistrations,
            mosaicAliasTransaction.getNamespaceId());
        return namespaceNameOptional
            .map(namespaceName -> {
                NamespaceId namespaceId = NamespaceId.createFromIdAndFullName(
                    mosaicAliasTransaction.getNamespaceId().getId(),
                    namespaceName);

                NetworkCurrencyBuilder builder = createNetworkCurrencyBuilder(mosaicId, namespaceId,
                    mosaicTransaction.getDivisibility());
                builder.withSupplyMutable(mosaicTransaction.getMosaicFlags().isSupplyMutable());
                builder.withTransferable(mosaicTransaction.getMosaicFlags().isTransferable());
                return builder.build();
            });
    }

    /**
     * This method resolves the full name of a leaf namespace if possible. It used the completed
     * {@link NamespaceRegistrationTransaction} and creates the full name recursively from button
     * (leaf) up (root)
     *
     * @param transactions the {@link NamespaceRegistrationTransaction} list
     * @param namespaceId the leaf namespace.
     * @return the full name of the namespace if all the parents namespace can be resolved.
     */
    private Optional<String> getNamespaceFullName(
        List<NamespaceRegistrationTransaction> transactions, NamespaceId namespaceId) {
        //If the fullname is already in the NamespaceId, we can shortcut the processing.
        if (namespaceId.getFullName().isPresent()) {
            return namespaceId.getFullName();
        }
        Optional<NamespaceRegistrationTransaction> namespaceOptional = transactions.stream()
            .filter(tx -> tx.getNamespaceId().equals(namespaceId)).findFirst();
        return namespaceOptional.flatMap(childNamespace -> {
            if (childNamespace.getNamespaceRegistrationType()
                == NamespaceRegistrationType.ROOT_NAMESPACE) {
                return Optional.of(childNamespace.getNamespaceName());
            } else {
                return childNamespace.getParentId().flatMap(parentId -> {
                    Optional<String> parentNamespaceNameOptional = getNamespaceFullName(
                        transactions, parentId);
                    return parentNamespaceNameOptional.map(
                        parentNamespaceName -> parentNamespaceName + "." + childNamespace
                            .getNamespaceName());
                });
            }
        });
    }

    /**
     * It returns all the transactions for the given block starting from the given transaction id.
     * If the fromId, it will start from the start.
     *
     * This method is recursive, moving thorough all the pages.
     *
     * @param height the transaction of the given height
     * @param pageNumber the page to be loaded
     * @return the list of all the transaction of the given height starting from from id
     */
    private Observable<List<Transaction>> getBlockTransactions(BigInteger height, int pageNumber) {
        return this.transactionRepository.search(
            new TransactionSearchCriteria(TransactionGroup.CONFIRMED).height(height).pageNumber(pageNumber))
            .flatMap(page -> {
                if (page.getPageNumber() <= pageNumber) {
                    return Observable.just(page.getData());
                } else {
                    return getBlockTransactions(height, pageNumber + 1)
                        .map(tail -> Stream.concat(page.getData().stream(), tail.stream())
                            .collect(Collectors.toList()));
                }
            });
    }


    private NetworkCurrencyBuilder createNetworkCurrencyBuilder(MosaicId mosaicId,
        NamespaceId namespaceId, int divisibility) {

        Validate.isTrue(mosaicId != null || namespaceId != null,
            "Either mosaic id or namespace id must be provided");
        //NOTE: If both namespace id and mosaic id are provided
        UnresolvedMosaicId unresolvedMosaicId = mosaicId == null ? namespaceId : mosaicId;
        NetworkCurrencyBuilder builder = new NetworkCurrencyBuilder(unresolvedMosaicId,
            divisibility);
        if (mosaicId != null) {
            builder.withMosaicId(mosaicId);
        }
        if (namespaceId != null) {
            builder.withNamespaceId(namespaceId);
        }
        return builder;
    }

}
