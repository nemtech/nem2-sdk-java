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

package io.nem.sdk.infrastructure.okhttp;

import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.TransactionSearchCriteria;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.infrastructure.okhttp.mappers.TransactionMapper;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.api.AccountRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.AccountDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.sdk.openapi.okhttp_gson.model.AccountInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.reactivex.Observable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by fernando on 29/07/19.
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    AccountRepository {

    private final AccountRoutesApi client;

    private final TransactionMapper transactionMapper;

    public AccountRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new AccountRoutesApi(apiClient);
        this.transactionMapper = new GeneralTransactionMapper(getJsonHelper());
    }


    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {

        Callable<AccountInfoDTO> callback = () -> getClient()
            .getAccountInfo(address.plain());
        return exceptionHandling(
            call(callback).map(AccountInfoDTO::getAccount).map(this::toAccountInfo));
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        Callable<List<AccountInfoDTO>> callback = () -> getClient()
            .getAccountsInfo(accountIds);
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

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().getAccountConfirmedTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionType(criteria.getTransactionTypes()));

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

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().getAccountIncomingTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionType(criteria.getTransactionTypes()));

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

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().getAccountOutgoingTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionType(criteria.getTransactionTypes()));

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

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().getAccountPartialTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionType(criteria.getTransactionTypes()));

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
        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().getAccountUnconfirmedTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionType(criteria.getTransactionTypes()));
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
        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().getAccountPartialTransactions(publicAccount.getPublicKey().toHex(),
                criteria.getPageSize(), criteria.getId(), criteria.getOrder(),
                toTransactionType(criteria.getTransactionTypes()));
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }


    private AccountInfo toAccountInfo(AccountDTO accountDTO) {
        return new AccountInfo(
            MapperUtils.toAddressFromEncoded(accountDTO.getAddress()),
            accountDTO.getAddressHeight(),
            accountDTO.getPublicKey(),
            accountDTO.getPublicKeyHeight(),
            accountDTO.getImportance(),
            accountDTO.getImportanceHeight(),
            accountDTO.getMosaics().stream()
                .map(mosaicDTO -> new Mosaic(toMosaicId(mosaicDTO.getId()), mosaicDTO.getAmount()))
                .collect(Collectors.toList()),
            AccountType.rawValueOf(accountDTO.getAccountType().getValue()));
    }


    private AccountRoutesApi getClient() {
        return client;
    }

    private String toTransactionType(List<TransactionType> transactionTypes) {
        return transactionTypes == null ? null
            : transactionTypes.stream()
                .map(transactionType -> Integer.toString(transactionType.getValue()))
                .collect(Collectors.joining(","));
    }
}
