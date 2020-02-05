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

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.NetworkCurrencyService;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import io.nem.sdk.model.mosaic.NetworkCurrencyBuilder;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link NetworkCurrencyService}
 */
public class NetworkCurrencyServiceImpl implements NetworkCurrencyService {

    /**
     * The {@link BlockRepository} used to load the block 1 transactions.
     */
    private final BlockRepository blockRepository;

    /**
     * Constructor.
     *
     * @param repositoryFactory the repository factory.
     */
    public NetworkCurrencyServiceImpl(RepositoryFactory repositoryFactory) {
        this.blockRepository = repositoryFactory.createBlockRepository();
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
    public Observable<List<NetworkCurrency>> getNetworkCurrencies() {
        return this.blockRepository.getBlockTransactions(BigInteger.ONE).map(transactions -> {
            List<MosaicDefinitionTransaction> mosaicTransactions = transactions.stream()
                .filter(t -> t.getType() == TransactionType.MOSAIC_DEFINITION)
                .map(t -> (MosaicDefinitionTransaction) t).collect(Collectors.toList());

            List<MosaicAliasTransaction> aliasTransactions = transactions.stream()
                .filter(t -> t.getType() == TransactionType.MOSAIC_ALIAS)
                .map(t -> (MosaicAliasTransaction) t).collect(Collectors.toList());

            List<NamespaceRegistrationTransaction> namespaceRegistrations = transactions.stream()
                .filter(t -> t.getType() == TransactionType.NAMESPACE_REGISTRATION)
                .map(t -> (NamespaceRegistrationTransaction) t).collect(Collectors.toList());

            List<MosaicSupplyChangeTransaction> mosaicSupplyChanges = transactions.stream()
                .filter(t -> t.getType() == TransactionType.MOSAIC_SUPPLY_CHANGE)
                .map(t -> (MosaicSupplyChangeTransaction) t).collect(Collectors.toList());

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
                            mosaicTransaction, mosaicAliasTransaction, mosaicSupplyChanges,
                            namespaceRegistrations
                        )).filter(Optional::isPresent).map(Optional::get);

                });
            return streamStream.flatMap(Function.identity())
                .collect(Collectors.toList());
        });
    }

    /**
     * This method tries to {@link NetworkCurrency} from the original {@link
     * MosaicDefinitionTransaction} and {@link MosaicAliasTransaction}.
     *
     * @param mosaicTransaction the original mosiac transaction
     * @param mosaicAliasTransaction the original mosaic alias transaction used to know the
     * mosaic/currency namespace
     * @param mosaicSupplyChanges the list of supply changes used to resolve the currency original
     * supply
     * @param namespaceRegistrations the list of namespace registration used to resolve the
     * mosaic/currency full name
     * @return the {@link NetworkCurrency} if it can be resolved.
     */
    private Optional<NetworkCurrency> getNetworkCurrency(
        MosaicDefinitionTransaction mosaicTransaction,
        MosaicAliasTransaction mosaicAliasTransaction,
        List<MosaicSupplyChangeTransaction> mosaicSupplyChanges,
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

                NetworkCurrencyBuilder builder = new NetworkCurrencyBuilder(namespaceId,
                    mosaicTransaction.getDivisibility());
                builder.withNamespaceId(namespaceId);
                builder.withSupplyMutable(
                    mosaicTransaction.getMosaicFlags().isSupplyMutable());
                builder
                    .withTransferable(
                        mosaicTransaction.getMosaicFlags().isTransferable());
                builder.withMosaicId(mosaicId);
                builder.withInitialSupply(mosaicSupplyChanges.stream()
                    .filter(
                        tx -> tx.getMosaicId().equals(mosaicId) || tx
                            .getMosaicId()
                            .equals(namespaceId)).findFirst()
                    .map(MosaicSupplyChangeTransaction::getDelta)
                    .orElse(BigInteger.ZERO));
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
                    Optional<String> parentNamespaceName = getNamespaceFullName(
                        transactions, parentId);
                    return parentNamespaceName.map(
                        parentNamespace -> parentNamespaceName + "." + childNamespace
                            .getNamespaceName());
                });
            }
        });


    }
}
