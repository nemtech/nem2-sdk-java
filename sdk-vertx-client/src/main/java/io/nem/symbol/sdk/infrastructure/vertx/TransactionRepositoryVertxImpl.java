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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.infrastructure.TransactionMapper;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.api.TransactionRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.TransactionRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.AnnounceTransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.Cosignature;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionIds;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionPage;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionPayload;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionTypeEnum;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Transaction http repository.
 *
 * @since 1.0
 */
public class TransactionRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements TransactionRepository {

  private final TransactionRoutesApi client;

  private final TransactionMapper transactionMapper;

  public TransactionRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    client = new TransactionRoutesApiImpl(apiClient);
    transactionMapper = new GeneralTransactionMapper(getJsonHelper());
  }

  public TransactionRoutesApi getClient() {
    return client;
  }

  @Override
  public Observable<Page<Transaction>> search(TransactionSearchCriteria criteria) {
    Consumer<Handler<AsyncResult<TransactionPage>>> callback = getSearchHandler(criteria);

    return exceptionHandling(
        call(callback)
            .map(
                p -> {
                  List<Transaction> data =
                      p.getData().stream()
                          .map(
                              transactionDto -> mapTransaction(criteria.getGroup(), transactionDto))
                          .collect(Collectors.toList());
                  return toPage(p.getPagination(), data);
                }));
  }

  private List<TransactionTypeEnum> toDto(List<TransactionType> transactionTypes) {
    return transactionTypes == null
        ? null
        : transactionTypes.stream()
            .map(e -> TransactionTypeEnum.fromValue(e.getValue()))
            .collect(Collectors.toList());
  }

  @Override
  public Observable<Transaction> getTransaction(TransactionGroup group, String transactionHash) {
    Consumer<Handler<AsyncResult<TransactionInfoDTO>>> callback =
        getTransactionHandler(group, transactionHash);
    return exceptionHandling(
        call(callback).map(transactionDto -> mapTransaction(group, transactionDto)));
  }

  @Override
  public Observable<List<Transaction>> getTransactions(
      TransactionGroup group, List<String> transactionHashes) {
    Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback =
        getTransactionsHandler(group, transactionHashes);
    return exceptionHandling(
        call(callback)
            .flatMapIterable(item -> item)
            .map(transactionDto -> mapTransaction(group, transactionDto))
            .toList()
            .toObservable());
  }

  @Override
  public Observable<TransactionAnnounceResponse> announce(SignedTransaction signedTransaction) {

    Consumer<Handler<AsyncResult<AnnounceTransactionInfoDTO>>> callback =
        handler ->
            getClient()
                .announceTransaction(
                    new TransactionPayload().payload(signedTransaction.getPayload()), handler);
    return exceptionHandling(
        call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));
  }

  @Override
  public Observable<TransactionAnnounceResponse> announceAggregateBonded(
      SignedTransaction signedTransaction) {
    Consumer<Handler<AsyncResult<AnnounceTransactionInfoDTO>>> callback =
        handler ->
            getClient()
                .announcePartialTransaction(
                    new TransactionPayload().payload(signedTransaction.getPayload()), handler);
    return exceptionHandling(
        call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));
  }

  @Override
  public Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(
      CosignatureSignedTransaction cosignatureSignedTransaction) {

    Consumer<Handler<AsyncResult<AnnounceTransactionInfoDTO>>> callback =
        handler ->
            getClient()
                .announceCosignatureTransaction(
                    new Cosignature()
                        .parentHash(cosignatureSignedTransaction.getParentHash())
                        .version(cosignatureSignedTransaction.getVersion())
                        .signature(cosignatureSignedTransaction.getSignature())
                        .signerPublicKey(
                            cosignatureSignedTransaction.getSigner().getPublicKey().toHex()),
                    handler);
    return exceptionHandling(
        call(callback).map(dto -> new TransactionAnnounceResponse(dto.getMessage())));
  }

  private Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> getTransactionsHandler(
      TransactionGroup group, List<String> transactionHashes) {
    TransactionIds transactionIds = new TransactionIds().transactionIds(transactionHashes);
    switch (group) {
      case CONFIRMED:
        return handler -> client.getConfirmedTransactions(transactionIds, handler);
      case PARTIAL:
        return handler -> client.getPartialTransactions(transactionIds, handler);
      case UNCONFIRMED:
        return handler -> client.getUnconfirmedTransactions(transactionIds, handler);
    }
    throw new IllegalArgumentException("Invalid group " + group);
  }

  private Consumer<Handler<AsyncResult<TransactionInfoDTO>>> getTransactionHandler(
      TransactionGroup group, String transactionHash) {
    switch (group) {
      case CONFIRMED:
        return handler -> getClient().getConfirmedTransaction(transactionHash, handler);
      case PARTIAL:
        return handler -> getClient().getPartialTransaction(transactionHash, handler);
      case UNCONFIRMED:
        return handler -> getClient().getUnconfirmedTransaction(transactionHash, handler);
    }
    throw new IllegalArgumentException("Invalid group " + group);
  }

  private Consumer<Handler<AsyncResult<TransactionPage>>> getSearchHandler(
      TransactionSearchCriteria criteria) {
    switch (criteria.getGroup()) {
      case CONFIRMED:
        return handler ->
            getClient()
                .searchConfirmedTransactions(
                    toDto(criteria.getAddress()),
                    toDto(criteria.getRecipientAddress()),
                    toDto(criteria.getSignerPublicKey()),
                    criteria.getHeight(),
                    criteria.getFromHeight(),
                    criteria.getToHeight(),
                    criteria.getFromTransferAmount(),
                    criteria.getToTransferAmount(),
                    toDto(criteria.getTransactionTypes()),
                    criteria.getEmbedded(),
                    toDto(criteria.getTransferMosaicId()),
                    criteria.getPageSize(),
                    criteria.getPageNumber(),
                    criteria.getOffset(),
                    toDto(criteria.getOrder()),
                    handler);
      case UNCONFIRMED:
        return handler ->
            getClient()
                .searchUnconfirmedTransactions(
                    toDto(criteria.getAddress()),
                    toDto(criteria.getRecipientAddress()),
                    toDto(criteria.getSignerPublicKey()),
                    criteria.getHeight(),
                    criteria.getFromHeight(),
                    criteria.getToHeight(),
                    criteria.getFromTransferAmount(),
                    criteria.getToTransferAmount(),
                    toDto(criteria.getTransactionTypes()),
                    criteria.getEmbedded(),
                    toDto(criteria.getTransferMosaicId()),
                    criteria.getPageSize(),
                    criteria.getPageNumber(),
                    criteria.getOffset(),
                    toDto(criteria.getOrder()),
                    handler);
      case PARTIAL:
        return handler ->
            getClient()
                .searchPartialTransactions(
                    toDto(criteria.getAddress()),
                    toDto(criteria.getRecipientAddress()),
                    toDto(criteria.getSignerPublicKey()),
                    criteria.getHeight(),
                    criteria.getFromHeight(),
                    criteria.getToHeight(),
                    criteria.getFromTransferAmount(),
                    criteria.getToTransferAmount(),
                    toDto(criteria.getTransactionTypes()),
                    criteria.getEmbedded(),
                    toDto(criteria.getTransferMosaicId()),
                    criteria.getPageSize(),
                    criteria.getPageNumber(),
                    criteria.getOffset(),
                    toDto(criteria.getOrder()),
                    handler);
    }
    throw new IllegalArgumentException("Invalid group " + criteria.getGroup());
  }

  private Transaction mapTransaction(TransactionGroup group, TransactionInfoDTO transactionDto) {
    return transactionMapper.mapToFactoryFromDto(transactionDto).group(group).build();
  }
}
