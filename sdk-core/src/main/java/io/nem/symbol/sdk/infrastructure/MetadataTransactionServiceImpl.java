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

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.api.AliasService;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataTransactionService;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * Implementation of {@link MetadataTransactionService}
 *
 * @author Ravi Shanker
 */
public class MetadataTransactionServiceImpl implements MetadataTransactionService {

    private final MetadataRepository metadataRepository;

    private final Observable<NetworkType> networkTypeObservable;

    private final AliasService aliasService;

    public MetadataTransactionServiceImpl(RepositoryFactory factory) {
        this.metadataRepository = factory.createMetadataRepository();
        this.networkTypeObservable = factory.getNetworkType();
        this.aliasService = new AliasServiceImpl(factory);
    }

    @Override
    public Observable<AccountMetadataTransactionFactory> createAccountMetadataTransactionFactory(
        PublicAccount targetPublicAccount, BigInteger key, String value,
        PublicKey senderPublicKey) {
        Address address = targetPublicAccount.getAddress();
        BiFunction<String, NetworkType, AccountMetadataTransactionFactory> factory = (newValue, networkType) -> AccountMetadataTransactionFactory
            .create(networkType, targetPublicAccount, key, newValue);
        return processMetadata(metadataRepository
                .getAccountMetadataByKeyAndSender(address, key, senderPublicKey.toHex()), factory,
            value);
    }

    @Override
    public Observable<MosaicMetadataTransactionFactory> createMosaicMetadataTransactionFactory(
        PublicAccount targetPublicAccount, BigInteger key, String value,
        PublicKey senderPublicKey, UnresolvedMosaicId unresolvedTargetId) {

        return aliasService.resolveMosaicId(unresolvedTargetId).flatMap(targetId -> {
            BiFunction<String, NetworkType, MosaicMetadataTransactionFactory> factory = (newValue, networkType) -> MosaicMetadataTransactionFactory
                .create(networkType, targetPublicAccount, unresolvedTargetId, key, newValue);
            return processMetadata(metadataRepository
                    .getMosaicMetadataByKeyAndSender(targetId, key, senderPublicKey.toHex()), factory,
                value);
        });
    }

    @Override
    public Observable<NamespaceMetadataTransactionFactory> createNamespaceMetadataTransactionFactory(
        PublicAccount targetPublicAccount, BigInteger key, String value,
        PublicKey senderPublicKey,
        NamespaceId targetId) {
        BiFunction<String, NetworkType, NamespaceMetadataTransactionFactory> factory = (newValue, networkType) -> NamespaceMetadataTransactionFactory
            .create(networkType, targetPublicAccount, targetId, key, newValue);
        return processMetadata(metadataRepository
                .getNamespaceMetadataByKeyAndSender(targetId, key, senderPublicKey.toHex()), factory,
            value);
    }

    /**
     * Generic way of processing a metadata entity and creating a new metadata transaction factory
     * depending on the existing metadata value. This works for Account, Mosaic and Namespace
     * metadata.
     *
     * @param metadataObservable the metadata observable
     * @param transactionFactory the function that creates a transaction factory
     * @param newValue the new value you want to set.
     * @param <T> the type of the transaction factory.
     * @return an Observable of a transaction factory.
     */
    private <T extends MetadataTransactionFactory> Observable<T> processMetadata(
        Observable<Metadata> metadataObservable,
        BiFunction<String, NetworkType, T> transactionFactory, String newValue) {
        return networkTypeObservable.flatMap(networkType -> metadataObservable.map(metadata -> {
            byte[] currentValueBytes = StringEncoder
                .getBytes(metadata.getMetadataEntry().getValue());
            byte[] newValueBytes = StringEncoder.getBytes(newValue);
            String xorValue = StringEncoder
                .getString(ConvertUtils.xor(currentValueBytes, newValueBytes));
            T factory = transactionFactory.apply(xorValue, networkType);
            factory.valueSizeDelta(newValueBytes.length - currentValueBytes.length);
            return factory;
        }).onErrorResumeNext(exception -> {
            if (exception instanceof RepositoryCallException
                && ((RepositoryCallException) exception).getStatusCode() == 404) {
                return Observable.just(transactionFactory.apply(newValue, networkType));
            } else {
                return Observable.error(exception);
            }
        }));

    }
}
