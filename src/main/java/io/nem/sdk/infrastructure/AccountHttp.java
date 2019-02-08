/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.nem.sdk.model.account.*;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Account http repository.
 *
 * @since 1.0
 */
public class AccountHttp extends Http implements AccountRepository {

    public AccountHttp(String host) throws MalformedURLException {
        this(host, new NetworkHttp(host));
    }

    public AccountHttp(String host, NetworkHttp networkHttp) throws MalformedURLException {
        super(host + "/account/", networkHttp);
    }

    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {
        return this.client
                .getAbs(this.url + address.plain())
                .map(Http::mapStringOrError)
                .map(str -> objectMapper.readValue(str, AccountInfoDTO.class))
                .map(AccountInfoDTO::getAccount)
                .map(accountDTO -> new AccountInfo(Address.createFromRawAddress(accountDTO.getAddressEncoded()),
                        accountDTO.getAddressHeight().extractIntArray(),
                        accountDTO.getPublicKey(),
                        accountDTO.getPublicKeyHeight().extractIntArray(),
                        accountDTO.getImportance().extractIntArray(),
                        accountDTO.getImportanceHeight().extractIntArray(),
                        accountDTO.getMosaics().stream().map(mosaicDTO -> new Mosaic(
                                new MosaicId(mosaicDTO.getId().extractIntArray()),
                                mosaicDTO.getAmount().extractIntArray()
                        )).collect(Collectors.toList())));
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
        JsonObject requestBody = new JsonObject();
        JsonArray addressesJsonArray = new JsonArray();
        for(Address address : addresses) {
            addressesJsonArray.add(address.plain());
        }
        requestBody.add("addresses", addressesJsonArray);

        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .postAbs(this.url.toString(), requestBody)
                        .map(Http::mapStringOrError)
                        .map(str -> objectMapper.<List<AccountInfoDTO>>readValue(str, new TypeReference<List<AccountInfoDTO>>() {
                        }))
                        .flatMapIterable(item -> item)
                        .map(AccountInfoDTO::getAccount)
                        .map(accountDTO -> new AccountInfo(Address.createFromRawAddress(accountDTO.getAddressEncoded()),
                                accountDTO.getAddressHeight().extractIntArray(),
                                accountDTO.getPublicKey(),
                                accountDTO.getPublicKeyHeight().extractIntArray(),
                                accountDTO.getImportance().extractIntArray(),
                                accountDTO.getImportanceHeight().extractIntArray(),
                                accountDTO.getMosaics().stream().map(mosaicDTO -> new Mosaic(
                                        new MosaicId(mosaicDTO.getId().extractIntArray()),
                                        mosaicDTO.getAmount().extractIntArray()
                                )).collect(Collectors.toList())))
                        .toList()
                        .toObservable());
    }

    @Override
    public Observable<MultisigAccountInfo> getMultisigAccountInfo(Address address) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .getAbs(this.url + address.plain() + "/multisig")
                        .map(Http::mapStringOrError)
                        .map(string -> objectMapper.readValue(string, MultisigAccountInfoDTO.class))
                        .map(MultisigAccountInfoDTO::getMultisig)
                        .map(transfromMultisigAccountInfoDTO(networkType)));
    }

    @Override
    public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .getAbs(this.url + address.plain() + "/multisig/graph")
                        .map(Http::mapStringOrError)
                        .map(str -> objectMapper.<List<MultisigAccountGraphInfoDTO>>readValue(str, new TypeReference<List<MultisigAccountGraphInfoDTO>>() {
                        }))
                        .map(multisigAccountGraphInfoDTOList -> {
                            Map<Integer, List<MultisigAccountInfo>> multisigAccountInfoMap = new HashMap<>();
                            multisigAccountGraphInfoDTOList.forEach(item -> multisigAccountInfoMap.put(item.getLevel(), item
                                    .getMultisigEntries()
                                    .stream()
                                    .map(MultisigAccountInfoDTO::getMultisig)
                                    .map(item2 -> new MultisigAccountInfo(new PublicAccount(item2.getAccount(), networkType),
                                            item2.getMinApproval(),
                                            item2.getMinRemoval(),
                                            item2.getCosignatories().stream().map(cosigner -> new PublicAccount(cosigner, networkType)).collect(Collectors.toList()),
                                            item2.getMultisigAccounts().stream().map(multisigAccount -> new PublicAccount(multisigAccount, networkType)).collect(Collectors.toList())))
                                    .collect(Collectors.toList())));
                            return new MultisigAccountGraphInfo(multisigAccountInfoMap);
                        }));
    }

    @Override
    public Observable<List<Transaction>> transactions(PublicAccount publicAccount) {
        return this.transactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> transactions(PublicAccount publicAccount, QueryParams queryParams) {
        return this.transactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> transactions(PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions");
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(PublicAccount publicAccount) {
        return this.incomingTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(PublicAccount publicAccount, QueryParams queryParams) {
        return this.incomingTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> incomingTransactions(PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/incoming");
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(PublicAccount publicAccount) {
        return this.outgoingTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(PublicAccount publicAccount, QueryParams queryParams) {
        return this.outgoingTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> outgoingTransactions(PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/outgoing");
    }

    @Override
    public Observable<List<AggregateTransaction>> aggregateBondedTransactions(PublicAccount publicAccount) {
        return this.aggregateBondedTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<AggregateTransaction>> aggregateBondedTransactions(PublicAccount publicAccount, QueryParams queryParams) {
        return this.aggregateBondedTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<AggregateTransaction>> aggregateBondedTransactions(PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/partial")
                .flatMapIterable(item -> item)
                .map(item -> (AggregateTransaction) item)
                .toList()
                .toObservable();
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount) {
        return this.unconfirmedTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount, QueryParams queryParams) {
        return this.unconfirmedTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/unconfirmed");
    }

    private Observable<List<Transaction>> findTransactions(PublicAccount publicAccount, Optional<QueryParams> queryParams, String path) {

        return this.client
                .getAbs(this.url + publicAccount.getPublicKey() + path + (queryParams.isPresent() ? queryParams.get().toUrl() : ""))
                .map(Http::mapStringOrError)
                .map(str -> StreamSupport.stream(new Gson().fromJson(str, JsonArray.class).spliterator(), false).map(s -> (JsonObject) s).collect(Collectors.toList()))
                .flatMapIterable(item -> item)
                .map(new TransactionMapping())
                .toList()
                .toObservable();
    }

    private Function<Multisig, MultisigAccountInfo> transfromMultisigAccountInfoDTO(NetworkType networkType) {
        return multisig -> new MultisigAccountInfo(
                new PublicAccount(multisig.getAccount(), networkType),
                multisig.getMinApproval(),
                multisig.getMinRemoval(),
                multisig.getCosignatories().stream().map(cosigner -> new PublicAccount(cosigner, networkType)).collect(Collectors.toList()),
                multisig.getMultisigAccounts().stream().map(multisigAccount -> new PublicAccount(multisigAccount, networkType)).collect(Collectors.toList())
        );
    }
}
