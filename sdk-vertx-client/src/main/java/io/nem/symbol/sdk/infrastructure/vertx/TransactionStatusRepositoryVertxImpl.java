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

import io.nem.symbol.sdk.api.TransactionStatusRepository;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.TransactionState;
import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.nem.symbol.sdk.openapi.vertx.api.TransactionStatusRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.TransactionStatusRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionHashes;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStatusDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import java.util.function.Consumer;

/**
 * Transaction status http repository.
 *
 * @since 1.0
 */
public class TransactionStatusRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements TransactionStatusRepository {

  private final TransactionStatusRoutesApi client;

  public TransactionStatusRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    client = new TransactionStatusRoutesApiImpl(apiClient);
  }

  public TransactionStatusRoutesApi getClient() {
    return client;
  }

  @Override
  public Observable<TransactionStatus> getTransactionStatus(String transactionHash) {
    Consumer<Handler<AsyncResult<TransactionStatusDTO>>> callback =
        handler -> getClient().getTransactionStatus(transactionHash, handler);
    return exceptionHandling(call(callback).map(this::toTransactionStatus));
  }

  private TransactionStatus toTransactionStatus(TransactionStatusDTO transactionStatusDTO) {
    return new TransactionStatus(
        TransactionState.valueOf(transactionStatusDTO.getGroup().name()),
        transactionStatusDTO.getCode() == null ? null : transactionStatusDTO.getCode().getValue(),
        transactionStatusDTO.getHash(),
        new Deadline(transactionStatusDTO.getDeadline()),
        transactionStatusDTO.getHeight());
  }

  @Override
  public Observable<List<TransactionStatus>> getTransactionStatuses(
      List<String> transactionHashes) {
    Consumer<Handler<AsyncResult<List<TransactionStatusDTO>>>> callback =
        handler ->
            client.getTransactionStatuses(
                new TransactionHashes().hashes(transactionHashes), handler);
    return exceptionHandling(
        call(callback)
            .flatMapIterable(item -> item)
            .map(this::toTransactionStatus)
            .toList()
            .toObservable());
  }
}
