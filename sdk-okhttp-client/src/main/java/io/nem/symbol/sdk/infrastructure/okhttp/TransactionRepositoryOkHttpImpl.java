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

import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.TransactionRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiException;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AnnounceTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Cosignature;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionIds;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionPayload;
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
public class TransactionRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements TransactionRepository {

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
    public Observable<Transaction> getTransaction(TransactionGroup group, String transactionHash) {
        Callable<TransactionInfoDTO> callback = () -> getBasicTransactions(group, transactionHash);
        return exceptionHandling(call(callback).map(transactionInfoDTO -> mapTransaction(group, transactionInfoDTO)));
    }

    private Transaction mapTransaction(TransactionGroup group, TransactionInfoDTO transactionInfoDTO) {
        return this.transactionMapper.mapToFactoryFromDto(transactionInfoDTO).group(group).build();
    }

    @Override
    public Observable<List<Transaction>> getTransactions(TransactionGroup group, List<String> transactionHashes) {
        Callable<List<TransactionInfoDTO>> callback = () -> getBasicTransactions(group, transactionHashes);
        return callList(callback, info -> mapTransaction(group, info));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announce(SignedTransaction signedTransaction) {

        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient()
            .announceTransaction(new TransactionPayload().payload(signedTransaction.getPayload()));
        return call(callback, dto -> new TransactionAnnounceResponse(dto.getMessage()));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBonded(SignedTransaction signedTransaction) {
        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient()
            .announcePartialTransaction(new TransactionPayload().payload(signedTransaction.getPayload()));
        return call(callback, dto -> new TransactionAnnounceResponse(dto.getMessage()));
    }

    @Override
    public Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(
        CosignatureSignedTransaction cosignatureSignedTransaction) {

        Callable<AnnounceTransactionInfoDTO> callback = () -> getClient().announceCosignatureTransaction(
            new Cosignature().parentHash(cosignatureSignedTransaction.getParentHash())
                .signature(cosignatureSignedTransaction.getSignature())
                .version(cosignatureSignedTransaction.getVersion())
                .signerPublicKey(cosignatureSignedTransaction.getSignerPublicKey()));
        return call(callback, dto -> new TransactionAnnounceResponse(dto.getMessage()));

    }

    @Override
    public Observable<Page<Transaction>> search(TransactionSearchCriteria criteria) {
        Callable<TransactionPage> callback = () -> basicSearch(criteria);
        return call(callback, p -> {
            List<Transaction> data = p.getData().stream()
                .map(transactionInfoDTO -> mapTransaction(criteria.getGroup(), transactionInfoDTO))
                .collect(Collectors.toList());
            return toPage(p.getPagination(), data);
        });
    }

    private TransactionPage basicSearch(TransactionSearchCriteria criteria) throws ApiException {
        switch (criteria.getGroup()) {
            case CONFIRMED:
                return getClient()
                    .searchConfirmedTransactions(toDto(criteria.getAddress()), toDto(criteria.getRecipientAddress()),
                        toDto(criteria.getSignerPublicKey()), criteria.getHeight(),
                        toDto(criteria.getTransactionTypes()), criteria.getEmbedded(), criteria.getPageSize(),
                        criteria.getPageNumber(), criteria.getOffset(), toDto(criteria.getOrder()));
            case PARTIAL:
                return getClient()
                    .searchPartialTransactions(toDto(criteria.getAddress()), toDto(criteria.getRecipientAddress()),
                        toDto(criteria.getSignerPublicKey()), criteria.getHeight(),
                        toDto(criteria.getTransactionTypes()), criteria.getEmbedded(), criteria.getPageSize(),
                        criteria.getPageNumber(), criteria.getOffset(), toDto(criteria.getOrder()));

            case UNCONFIRMED:
                return getClient()
                    .searchUnconfirmedTransactions(toDto(criteria.getAddress()), toDto(criteria.getRecipientAddress()),
                        toDto(criteria.getSignerPublicKey()), criteria.getHeight(),
                        toDto(criteria.getTransactionTypes()), criteria.getEmbedded(), criteria.getPageSize(),
                        criteria.getPageNumber(), criteria.getOffset(), toDto(criteria.getOrder()));
        }
        throw new IllegalArgumentException("Invalid group " + criteria.getGroup());
    }


    private TransactionInfoDTO getBasicTransactions(TransactionGroup group, String transactionHash)
        throws ApiException {
        switch (group) {
            case CONFIRMED:
                return getClient().getConfirmedTransaction(transactionHash);
            case PARTIAL:
                return getClient().getPartialTransaction(transactionHash);
            case UNCONFIRMED:
                return getClient().getUnconfirmedTransaction(transactionHash);
        }
        throw new IllegalArgumentException("Invalid group " + group);
    }

    private List<TransactionInfoDTO> getBasicTransactions(TransactionGroup group, List<String> transactionHashes)
        throws ApiException {
        TransactionIds transactionIds = new TransactionIds().transactionIds(transactionHashes);
        switch (group) {
            case CONFIRMED:
                return getClient().getConfirmedTransactions(transactionIds);
            case PARTIAL:
                return getClient().getPartialTransactions(transactionIds);
            case UNCONFIRMED:
                return getClient().getUnconfirmedTransactions(transactionIds);
        }
        throw new IllegalArgumentException("Invalid group " + group);
    }

    private List<TransactionTypeEnum> toDto(List<TransactionType> transactionTypes) {
        return transactionTypes == null ? null
            : transactionTypes.stream().map(e -> TransactionTypeEnum.fromValue(e.getValue()))
                .collect(Collectors.toList());
    }
}
