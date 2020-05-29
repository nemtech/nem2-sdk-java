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

package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.api.TransactionSearchGroup;
import io.nem.symbol.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionState;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.TransactionRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AnnounceTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Cosignature;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionGroupSubsetEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionHashes;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoExtendedDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionPayload;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatusDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionTypeEnum;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Transaction http repository.
 *
 * @since 1.0
 */
public class TransactionRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    TransactionRepository {

    private final TransactionRoutesApi client;

    private final GeneralTransactionMapper transactionMapper;

    public TransactionRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new TransactionRoutesApi(apiClient);
        this.transactionMapper = new GeneralTransactionMapper(getJsonHelper());
    }


    public TransactionRoutesApi getClient() {
        return client;
    }

    @Override
    public Observable<Transaction> getTransaction(String transactionHash) {
        Callable<TransactionInfoDTO> callback = () -> getClient()
            .getTransaction(transactionHash);
        return exceptionHandling(call(callback).map(this.transactionMapper::map));
    }

    @Override
    public Observable<List<Transaction>> getTransactions(List<String> transactionHashes) {
        Callable<List<TransactionInfoExtendedDTO>> callback = () ->
            getClient().getTransactionsById(new TransactionIds().transactionIds(transactionHashes));
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this.transactionMapper::map).toList()
                .toObservable());


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
            getClient().getTransactionsStatuses(new TransactionHashes().hashes(transactionHashes));
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransactionStatus).toList()
                .toObservable());

    }

    @Override
    public Observable<TransactionAnnounceResponse> announce(SignedTransaction signedTransaction) {

        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient()
            .announceTransaction(
                new TransactionPayload().payload(signedTransaction.getPayload()));
        return exceptionHandling(
            call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBonded(
        SignedTransaction signedTransaction) {
        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient()
            .announcePartialTransaction(
                new TransactionPayload().payload(signedTransaction.getPayload()));
        return exceptionHandling(
            call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(
        CosignatureSignedTransaction cosignatureSignedTransaction) {

        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient()
            .announceCosignatureTransaction(
                new Cosignature().parentHash(cosignatureSignedTransaction.getParentHash())
                    .signature(cosignatureSignedTransaction.getSignature())
                    .signerPublicKey(cosignatureSignedTransaction.getSignerPublicKey()));
        return exceptionHandling(
            call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));

    }

    @Override
    public Observable<Page<Transaction>> search(TransactionSearchCriteria criteria) {
        Callable<TransactionPage> callback = () -> getClient()
            .searchTransactions(toDto(criteria.getAddress()),
                toDto(criteria.getRecipientAddress()),
                toDto(criteria.getSignerPublicKey()), criteria.getHeight(),
                criteria.getPageSize(),
                criteria.getPageNumber(), criteria.getOffset(), toDto(criteria.getGroup()),
                toDto(criteria.getOrder()), toDto(criteria.getTransactionTypes()),
                criteria.getEmbedded());

        return exceptionHandling(call(callback)
            .map(p -> {
                List<Transaction> data = p.getData().stream().map(transactionMapper::map)
                    .collect(Collectors.toList());
                return toPage(p.getPagination(), data);
            }));
    }



    private TransactionGroupSubsetEnum toDto(TransactionSearchGroup group) {
        return group == null ? null : TransactionGroupSubsetEnum.fromValue(group.getValue());
    }

    private List<TransactionTypeEnum> toDto(List<TransactionType> transactionTypes) {
        return transactionTypes == null ? null
            : transactionTypes.stream().map(e -> TransactionTypeEnum.fromValue(e.getValue()))
                .collect(Collectors.toList());
    }
}
