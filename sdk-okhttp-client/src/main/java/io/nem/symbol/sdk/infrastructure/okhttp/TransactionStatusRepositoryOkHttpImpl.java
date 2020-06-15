package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.sdk.api.TransactionStatusRepository;
import io.nem.symbol.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.TransactionState;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.TransactionRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.TransactionStatusRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionHashes;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatusDTO;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Implementation of {@link io.nem.symbol.sdk.api.TransactionStatusRepository}
 */
public class TransactionStatusRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements TransactionStatusRepository {

    private final TransactionStatusRoutesApi client;


    public TransactionStatusRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new TransactionStatusRoutesApi(apiClient);
    }

    @Override
    public Observable<TransactionStatus> getTransactionStatus(String transactionHash) {
        Callable<TransactionStatusDTO> callback = () -> getClient()
            .getTransactionStatus(transactionHash);
        return exceptionHandling(call(callback).map(this::toTransactionStatus));
    }

    private TransactionStatus toTransactionStatus(TransactionStatusDTO transactionStatusDTO) {
        return new TransactionStatus(
            TransactionState.valueOf(transactionStatusDTO.getGroup().name()),
            transactionStatusDTO.getCode() == null ? null
                : transactionStatusDTO.getCode().getValue(),
            transactionStatusDTO.getHash(),
            new Deadline((transactionStatusDTO.getDeadline())),
            (transactionStatusDTO.getHeight()));
    }

    @Override
    public Observable<List<TransactionStatus>> getTransactionStatuses(
        List<String> transactionHashes) {
        Callable<List<TransactionStatusDTO>> callback = () ->
            getClient().getTransactionStatuses(new TransactionHashes().hashes(transactionHashes));
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransactionStatus).toList()
                .toObservable());

    }

    public TransactionStatusRoutesApi getClient() {
        return client;
    }
}
