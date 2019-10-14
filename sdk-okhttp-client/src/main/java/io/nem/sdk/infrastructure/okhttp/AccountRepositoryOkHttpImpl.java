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
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.infrastructure.okhttp.mappers.TransactionMapper;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.okhttp_gson.api.AccountRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.AccountDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountIds;
import io.nem.sdk.openapi.okhttp_gson.model.AccountInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountNamesDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountsNamesDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MultisigAccountGraphInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MultisigAccountInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MultisigDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public Observable<List<AccountNames>> getAccountsNames(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        return getAccountNames(accountIds);
    }

    private Observable<List<AccountNames>> getAccountNames(AccountIds accountIds) {
        Callable<AccountsNamesDTO> callback = () -> getClient()
            .getAccountsNames(accountIds);
        return exceptionHandling(
            call(callback).map(AccountsNamesDTO::getAccountNames).flatMapIterable(item -> item)
                .map(this::toAccountNames).toList().toObservable());
    }

    /**
     * Converts a {@link AccountNamesDTO} into a {@link AccountNames}
     *
     * @param dto {@link AccountNamesDTO}
     * @return {@link AccountNames}
     */
    private AccountNames toAccountNames(AccountNamesDTO dto) {
        return new AccountNames(MapperUtils.toAddressFromEncoded(dto.getAddress()),
            dto.getNames().stream().map(NamespaceName::new).collect(Collectors.toList()));
    }

    @Override
    public Observable<MultisigAccountInfo> getMultisigAccountInfo(Address address) {
        return exceptionHandling(call(
            () -> getClient().getAccountMultisig(address.plain()))
            .map(MultisigAccountInfoDTO::getMultisig)
            .map(this::toMultisigAccountInfo));

    }


    @Override
    public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {
        return exceptionHandling(call(
            () -> getClient().getAccountMultisigGraph(address.plain()))
            .map(multisigAccountGraphInfoDTOList -> {
                Map<Integer, List<MultisigAccountInfo>> multisigAccountInfoMap = new HashMap<>();
                multisigAccountGraphInfoDTOList.forEach(
                    item ->
                        multisigAccountInfoMap.put(
                            item.getLevel(),
                            toMultisigAccountInfo(item)));
                return new MultisigAccountGraphInfo(multisigAccountInfoMap);
            }));
    }

    private List<MultisigAccountInfo> toMultisigAccountInfo(MultisigAccountGraphInfoDTO item) {
        return item.getMultisigEntries().stream()
            .map(MultisigAccountInfoDTO::getMultisig)
            .map(this::toMultisigAccountInfo)
            .collect(Collectors.toList());
    }


    @Override
    public Observable<List<Transaction>> transactions(PublicAccount publicAccount) {
        return this.transactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> transactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.transactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> transactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().transactions(publicAccount.getPublicKey().toHex(),
                getPageSize(queryParams),
                getId(queryParams),
                null);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(PublicAccount publicAccount) {
        return this.incomingTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.incomingTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> incomingTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().incomingTransactions(publicAccount.getPublicKey().toHex(),
                getPageSize(queryParams), getId(queryParams), null);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(PublicAccount publicAccount) {
        return this.outgoingTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.outgoingTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> outgoingTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().outgoingTransactions(publicAccount.getPublicKey().toHex(),
                getPageSize(queryParams),
                getId(queryParams), null);

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
        return this.aggregateBondedTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.aggregateBondedTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {

        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().partialTransactions(publicAccount.getPublicKey().toHex(),
                getPageSize(queryParams), getId(queryParams), null);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction)
                .map(o -> (AggregateTransaction) o).toList()
                .toObservable());
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount) {
        return this.unconfirmedTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.unconfirmedTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> unconfirmedTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        Callable<List<TransactionInfoDTO>> callback = () ->
            getClient().unconfirmedTransactions(publicAccount.getPublicKey().toHex(),
                getPageSize(queryParams),
                getId(queryParams), null);
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

    private MultisigAccountInfo toMultisigAccountInfo(MultisigDTO dto) {
        NetworkType networkType = getNetworkTypeBlocking();
        return new MultisigAccountInfo(
            new PublicAccount(
                dto.getAccountPublicKey(), networkType),
            dto.getMinApproval(),
            dto.getMinRemoval(),
            dto.getCosignatoryPublicKeys().stream()
                .map(cosigner -> new PublicAccount(cosigner, networkType))
                .collect(Collectors.toList()),
            dto.getMultisigPublicKeys().stream()
                .map(multisigAccount -> new PublicAccount(multisigAccount, networkType))
                .collect(Collectors.toList()));
    }


    private AccountRoutesApi getClient() {
        return client;
    }
}
