package io.nem.sdk.infrastructure;

import io.nem.core.crypto.PublicKey;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.api.AliasService;
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.MetadataTransactionService;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
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
