/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import static io.nem.core.utils.MapperUtils.toAddressFromEncoded;
import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.TransactionSearchCriteria;
import io.nem.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.sdk.infrastructure.vertx.mappers.TransactionMapper;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.api.AccountRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.AccountRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.AccountDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountIds;
import io.nem.symbol.sdk.openapi.vertx.model.AccountInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionTypeEnum;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by fernando on 29/07/19.
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    AccountRepository {


    private final AccountRoutesApi client;

    private final TransactionMapper transactionMapper;

    public AccountRepositoryVertxImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new AccountRoutesApiImpl(apiClient);
        this.transactionMapper = new GeneralTransactionMapper(getJsonHelper());
    }

    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {

        Consumer<Handler<AsyncResult<AccountInfoDTO>>> callback = handler -> getClient()
            .getAccountInfo(address.plain(), handler);
        return exceptionHandling(
            call(callback).map(AccountInfoDTO::getAccount).map(this::toAccountInfo));
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        Consumer<Handler<AsyncResult<List<AccountInfoDTO>>>> callback = handler -> getClient()
            .getAccountsInfo(accountIds, handler);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item)
                .map(AccountInfoDTO::getAccount)
                .map(this::toAccountInfo).toList().toObservable());
    }


    @Override
    public Observable<List<Transaction>> transactions(PublicAccount publicAccount) {
        return this.transactions(publicAccount, new TransactionSearchCriteria());
    }

    @Override
    public Observable<List<Transaction>> transactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria) {

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getAccountConfirmedTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionTypes(criteria.getTransactionTypes()), handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(PublicAccount publicAccount) {
        return this.incomingTransactions(publicAccount, new TransactionSearchCriteria());
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria) {

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getAccountIncomingTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionTypes(criteria.getTransactionTypes()), handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(PublicAccount publicAccount) {
        return this.outgoingTransactions(publicAccount, new TransactionSearchCriteria());
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria) {

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getAccountOutgoingTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionTypes(criteria.getTransactionTypes()), handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    @Override
    public Observable<List<Transaction>> partialTransactions(PublicAccount publicAccount) {
        return this.partialTransactions(publicAccount, new TransactionSearchCriteria());
    }

    @Override
    public Observable<List<Transaction>> partialTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria) {

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getAccountPartialTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionTypes(criteria.getTransactionTypes()), handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    private Transaction toTransaction(TransactionInfoDTO input) {
        return transactionMapper.map(input);
    }


    @Override
    public Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount) {
        return this.aggregateBondedTransactions(publicAccount, new TransactionSearchCriteria());
    }

    @Override
    public Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria) {

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getAccountPartialTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionTypes(criteria.getTransactionTypes()), handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction)
                .map(o -> (AggregateTransaction) o).toList()
                .toObservable());
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount) {
        return this.unconfirmedTransactions(publicAccount, new TransactionSearchCriteria());
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(
        PublicAccount publicAccount, TransactionSearchCriteria criteria) {

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = handler ->
            client.getAccountUnconfirmedTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionTypes(criteria.getTransactionTypes()), handler);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }


    private AccountInfo toAccountInfo(AccountDTO accountDTO) {
        return new AccountInfo(
            toAddressFromEncoded(accountDTO.getAddress()),
            accountDTO.getAddressHeight(),
            accountDTO.getPublicKey(),
            accountDTO.getPublicKeyHeight(),
            accountDTO.getImportance(),
            accountDTO.getImportanceHeight(),
            accountDTO.getMosaics().stream()
                .map(
                    mosaicDTO ->
                        new Mosaic(
                            toMosaicId(mosaicDTO.getId()),
                            mosaicDTO.getAmount()))
                .collect(Collectors.toList()),
            AccountType.rawValueOf(accountDTO.getAccountType().getValue()));
    }


    private AccountRoutesApi getClient() {
        return client;
    }

    private List<TransactionTypeEnum> toTransactionTypes(List<TransactionType> transactionTypes) {
        return transactionTypes == null || transactionTypes.isEmpty() ? null
            : transactionTypes.stream()
                .map(transactionType -> TransactionTypeEnum.fromValue(transactionType.getValue()))
                .collect(Collectors.toList());
    }

}
