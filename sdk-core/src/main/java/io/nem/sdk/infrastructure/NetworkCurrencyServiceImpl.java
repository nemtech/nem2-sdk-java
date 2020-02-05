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

public class NetworkCurrencyServiceImpl implements NetworkCurrencyService {

    private final BlockRepository blockRepository;

    public NetworkCurrencyServiceImpl(RepositoryFactory repositoryFactory) {
        this.blockRepository = repositoryFactory.createBlockRepository();
    }

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
                            namespaceRegistrations,
                            mosaicSupplyChanges, mosaicTransaction,
                            mosaicAliasTransaction)).filter(Optional::isPresent).map(Optional::get);

                });
            return streamStream.flatMap(Function.identity())
                .collect(Collectors.toList());
        });
    }

    private Optional<NetworkCurrency> getNetworkCurrency(
        List<NamespaceRegistrationTransaction> namespaceRegistrations,
        List<MosaicSupplyChangeTransaction> mosaicSupplyChanges,
        MosaicDefinitionTransaction mosaicTransaction,
        MosaicAliasTransaction mosaicAliasTransaction) {
        MosaicId mosaicId = mosaicAliasTransaction.getMosaicId();

        Optional<String> namespaceNameOptional = getNemesisMosaicNamespaceName(
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

    private Optional<String> getNemesisMosaicNamespaceName(
        List<NamespaceRegistrationTransaction> transactions, NamespaceId subNamespaceId) {
        Optional<NamespaceRegistrationTransaction> childNamespaceOptional = transactions.stream()
            .filter(
                tx -> tx.getNamespaceRegistrationType() == NamespaceRegistrationType.SUB_NAMESPACE
                    && tx.getNamespaceId().equals(subNamespaceId)).findFirst();

        return childNamespaceOptional.flatMap(childNamespace -> childNamespace.getParentId()
            .flatMap(expectedParentId -> transactions.stream().filter(
                tx -> tx.getNamespaceRegistrationType()
                    == NamespaceRegistrationType.ROOT_NAMESPACE && tx.getNamespaceId()
                    .equals(expectedParentId)).findFirst()
                .map(parentNamespace -> parentNamespace.getNamespaceName() + "." + childNamespace
                    .getNamespaceName())));

    }
}
