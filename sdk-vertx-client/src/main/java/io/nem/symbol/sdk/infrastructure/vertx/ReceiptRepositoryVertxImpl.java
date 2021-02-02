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
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.ResolutionStatementSearchCriteria;
import io.nem.symbol.sdk.api.TransactionStatementSearchCriteria;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.openapi.vertx.api.ReceiptRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.ReceiptRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.Order;
import io.nem.symbol.sdk.openapi.vertx.model.ReceiptTypeEnum;
import io.nem.symbol.sdk.openapi.vertx.model.ResolutionStatementPage;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStatementPage;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** OkHttp implementation of {@link ReceiptRepository}. */
public class ReceiptRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements ReceiptRepository {

  private final ReceiptRoutesApi client;

  private final ReceiptMappingVertx mapper;

  public ReceiptRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new ReceiptRoutesApiImpl(apiClient);
    this.mapper = new ReceiptMappingVertx(getJsonHelper());
  }

  @Override
  public Observable<Page<TransactionStatement>> searchReceipts(
      TransactionStatementSearchCriteria criteria) {

    BigInteger height = criteria.getHeight();
    BigInteger fromHeight = criteria.getFromHeight();
    BigInteger toHeight = criteria.getToHeight();
    List<ReceiptTypeEnum> receiptTypes = toDto(criteria.getReceiptTypes());
    String recipientAddress = toDto(criteria.getRecipientAddress());
    String senderAddress = toDto(criteria.getSenderAddress());
    String targetAddress = toDto(criteria.getTargetAddress());
    String artifactId = criteria.getArtifactId();
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());

    Consumer<Handler<AsyncResult<TransactionStatementPage>>> callback =
        (handler) ->
            getClient()
                .searchReceipts(
                    height,
                    fromHeight,
                    toHeight,
                    receiptTypes,
                    recipientAddress,
                    senderAddress,
                    targetAddress,
                    artifactId,
                    pageSize,
                    pageNumber,
                    offset,
                    order,
                    handler);

    return exceptionHandling(
        call(callback)
            .map(
                page ->
                    this.toPage(
                        page.getPagination(),
                        page.getData().stream()
                            .map(mapper::createTransactionStatement)
                            .collect(Collectors.toList()))));
  }

  @Override
  public Observable<Page<AddressResolutionStatement>> searchAddressResolutionStatements(
      ResolutionStatementSearchCriteria criteria) {
    BigInteger height = criteria.getHeight();
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());
    Consumer<Handler<AsyncResult<ResolutionStatementPage>>> callback =
        (handler) ->
            getClient()
                .searchAddressResolutionStatements(
                    height, pageSize, pageNumber, offset, order, handler);
    return exceptionHandling(
        call(callback)
            .map(
                page ->
                    this.toPage(
                        page.getPagination(),
                        page.getData().stream()
                            .map(mapper::createAddressResolutionStatementFromDto)
                            .collect(Collectors.toList()))));
  }

  @Override
  public Observable<Page<MosaicResolutionStatement>> searchMosaicResolutionStatements(
      ResolutionStatementSearchCriteria criteria) {
    BigInteger height = criteria.getHeight();
    Integer pageSize = criteria.getPageSize();
    Integer pageNumber = criteria.getPageNumber();
    String offset = criteria.getOffset();
    Order order = toDto(criteria.getOrder());
    Consumer<Handler<AsyncResult<ResolutionStatementPage>>> callback =
        (handler) ->
            getClient()
                .searchMosaicResolutionStatements(
                    height, pageSize, pageNumber, offset, order, handler);
    return exceptionHandling(
        call(callback)
            .map(
                page ->
                    this.toPage(
                        page.getPagination(),
                        page.getData().stream()
                            .map(mapper::createMosaicResolutionStatementFromDto)
                            .collect(Collectors.toList()))));
  }

  private List<ReceiptTypeEnum> toDto(List<ReceiptType> values) {
    return values == null
        ? null
        : values.stream()
            .map(e -> ReceiptTypeEnum.fromValue(e.getValue()))
            .collect(Collectors.toList());
  }

  public ReceiptRoutesApi getClient() {
    return client;
  }
}
