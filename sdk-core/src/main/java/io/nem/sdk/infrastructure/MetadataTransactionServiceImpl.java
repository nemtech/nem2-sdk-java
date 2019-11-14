package io.nem.sdk.infrastructure;

import io.nem.core.crypto.PublicKey;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.MetadataTransactionService;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.function.Function;

/**
 * Implementation of {@link MetadataTransactionService}
 *
 * @author Ravi Shanker
 */
public class MetadataTransactionServiceImpl implements MetadataTransactionService {

    private final MetadataRepository metadataRepository;

    public MetadataTransactionServiceImpl(RepositoryFactory factory) {
        this.metadataRepository = factory.createMetadataRepository();
    }

    @Override
    public Observable<AccountMetadataTransactionFactory> createAccountMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value,
        PublicKey senderPublicKey) {
        Address address = targetPublicAccount.getAddress();
        Function<String, AccountMetadataTransactionFactory> factory = newValue -> AccountMetadataTransactionFactory
            .create(networkType, targetPublicAccount, key, newValue);
        return processMetadata(metadataRepository
                .getAccountMetadataByKeyAndSender(address, key, senderPublicKey.toHex()), factory,
            value);
    }

    @Override
    public Observable<MosaicMetadataTransactionFactory> createMosaicMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value,
        PublicKey senderPublicKey,
        MosaicId targetId) {

        Function<String, MosaicMetadataTransactionFactory> factory = newValue -> MosaicMetadataTransactionFactory
            .create(networkType, targetPublicAccount, targetId, key, newValue);
        return processMetadata(metadataRepository
                .getMosaicMetadataByKeyAndSender(targetId, key, senderPublicKey.toHex()), factory,
            value);
    }

    @Override
    public Observable<NamespaceMetadataTransactionFactory> createNamespaceMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value,
        PublicKey senderPublicKey,
        NamespaceId targetId) {
        Function<String, NamespaceMetadataTransactionFactory> factory = newValue -> NamespaceMetadataTransactionFactory
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
        Function<String, T> transactionFactory, String newValue) {
        return metadataObservable.map(metadata -> {
            byte[] currentValueBytes = StringEncoder
                .getBytes(metadata.getMetadataEntry().getValue());
            byte[] newValueBytes = StringEncoder.getBytes(newValue);
            String xorValue = StringEncoder
                .getString(ConvertUtils.xor(currentValueBytes, newValueBytes));
            T factory = transactionFactory.apply(xorValue);
            factory.valueSizeDelta(newValueBytes.length - currentValueBytes.length);
            return factory;
        }).onErrorResumeNext(exception -> {
            if (exception instanceof RepositoryCallException
                && ((RepositoryCallException) exception).getStatusCode() == 404) {
                return Observable.just(transactionFactory.apply(newValue));
            } else {
                return Observable.error(exception);
            }
        });
    }
}
