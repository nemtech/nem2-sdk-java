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
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.ResolutionStatementSearchCriteria;
import io.nem.symbol.sdk.api.TransactionStatementSearchCriteria;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.ReceiptRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ReceiptTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ResolutionStatementPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatementPage;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


/**
 * OkHttp implementation of {@link ReceiptRepository}.
 */
public class ReceiptRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements ReceiptRepository {

    private final ReceiptRoutesApi client;

    private final ReceiptMappingOkHttp mapper;

    public ReceiptRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new ReceiptRoutesApi(apiClient);
        this.mapper = new ReceiptMappingOkHttp(getJsonHelper());
    }

    @Override
    public Observable<Page<TransactionStatement>> searchReceipts(TransactionStatementSearchCriteria criteria) {

        BigInteger height = criteria.getHeight();
        List<ReceiptTypeEnum> receiptTypes = toDto(criteria.getReceiptTypes());
        String recipientAddress = toDto(criteria.getRecipientAddress());
        String senderAddress = toDto(criteria.getSenderAddress());
        String targetAddress = toDto(criteria.getTargetAddress());
        String artifactId = criteria.getArtifactId();
        Integer pageSize = criteria.getPageSize();
        Integer pageNumber = criteria.getPageNumber();
        String offset = criteria.getOffset();
        Order order = toDto(criteria.getOrder());

        Callable<TransactionStatementPage> callback = () -> getClient()
            .searchReceipts(height, receiptTypes, recipientAddress, senderAddress, targetAddress, artifactId, pageSize,
                pageNumber, offset, order);

        return exceptionHandling(call(callback).map(page -> this.toPage(page.getPagination(),
            page.getData().stream().map(mapper::createTransactionStatement).collect(Collectors.toList()))));

    }

    @Override
    public Observable<Page<AddressResolutionStatement>> searchAddressResolutionStatements(
        ResolutionStatementSearchCriteria criteria) {
        BigInteger height = criteria.getHeight();
        Integer pageSize = criteria.getPageSize();
        Integer pageNumber = criteria.getPageNumber();
        String offset = criteria.getOffset();
        Order order = toDto(criteria.getOrder());
        Callable<ResolutionStatementPage> callback = () -> getClient()
            .searchAddressResolutionStatements(height, pageSize, pageNumber, offset, order);
        return exceptionHandling(call(callback).map(page -> this.toPage(page.getPagination(),
            page.getData().stream().map(mapper::createAddressResolutionStatementFromDto)
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
        Callable<ResolutionStatementPage> callback = () -> getClient()
            .searchMosaicResolutionStatements(height, pageSize, pageNumber, offset, order);
        return exceptionHandling(call(callback).map(page -> this.toPage(page.getPagination(),
            page.getData().stream().map(mapper::createMosaicResolutionStatementFromDto).collect(Collectors.toList()))));

    }

    private List<ReceiptTypeEnum> toDto(List<ReceiptType> values) {
        return values == null ? null
            : values.stream().map(e -> ReceiptTypeEnum.fromValue(e.getValue())).collect(Collectors.toList());
    }

    public ReceiptRoutesApi getClient() {
        return client;
    }
}
