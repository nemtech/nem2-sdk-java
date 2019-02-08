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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.*;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listener
 *
 * @since 1.0
 */
public class Listener {
    private final URL url;
    private final Subject<ListenerMessage> messageSubject;
    private String UID;
    private WebSocket webSocket;
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param url nis host
     */
    public Listener(final String url) throws MalformedURLException {
        this.url = new URL(url);
        this.messageSubject = PublishSubject.create();
    }

    /**
     * @return a {@link CompletableFuture} that resolves when the websocket connection is opened
     */
    public CompletableFuture<Void> open() {
        HttpClient httpClient = Vertx.vertx().createHttpClient();

        CompletableFuture<Void> future = new CompletableFuture<>();
        if (this.webSocket != null) {
            return CompletableFuture.completedFuture(null);
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setHost(this.url.getHost());
        requestOptions.setPort(this.url.getPort());
        requestOptions.setURI("/ws");
        httpClient.websocket(requestOptions, webSocket -> {
            this.webSocket = webSocket;
            webSocket.handler(handler -> {
                JsonObject message = handler.toJsonObject();
                if (message.containsKey("uid")) {
                    this.UID = message.getString("uid");
                    future.complete(null);
                } else if (message.containsKey("transaction")) {
                    this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.rawValueOf(message.getJsonObject("meta").getString("channelName")),
                            new TransactionMapping().apply(new Gson().fromJson(message.toString(), com.google.gson.JsonObject.class))
                    ));
                } else if (message.containsKey("block")) {
                    final JsonObject meta = message.getJsonObject("meta");
                    final JsonObject block = message.getJsonObject("block");
                    int rawNetworkType = (int) Long.parseLong(Integer.toHexString(block.getInteger("version")).substring(0, 2), 16);
                    final NetworkType networkType;
                    if (rawNetworkType == NetworkType.MIJIN_TEST.getValue()) networkType = NetworkType.MIJIN_TEST;
                    else if (rawNetworkType == NetworkType.MIJIN.getValue()) networkType = NetworkType.MIJIN;
                    else if (rawNetworkType == NetworkType.MAIN_NET.getValue()) networkType = NetworkType.MAIN_NET;
                    else networkType = NetworkType.TEST_NET;

                    final int version = (int) Long.parseLong(Integer.toHexString(block.getInteger("version")).substring(2, 4), 16);
                    this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.BLOCK,
                            new BlockInfo(
                                    meta.getString("hash"),
                                    meta.getString("generationHash"),
                                    Optional.empty(),
                                    Optional.empty(),
                                    block.getString("signature"),
                                    new PublicAccount(block.getString("signer"), networkType),
                                    networkType,
                                    version,
                                    block.getInteger("type"),
                                    extractBigInteger(block.getJsonArray("height")),
                                    extractBigInteger(block.getJsonArray("timestamp")),
                                    extractBigInteger(block.getJsonArray("difficulty")),
                                    block.getString("previousBlockHash"),
                                    block.getString("blockTransactionsHash")
                            )
                    ));
                } else if (message.containsKey("status")) {
                    this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.STATUS,
                            new TransactionStatusError(
                                    message.getString("hash"),
                                    message.getString("status"),
                                    new Deadline(extractBigInteger(message.getJsonArray("deadline")))
                            )
                    ));
                } else if (message.containsKey("meta")) {
                    this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.rawValueOf(message.getJsonObject("meta").getString("channelName")),
                            message.getJsonObject("meta").getString("hash")
                    ));
                } else if (message.containsKey("parentHash")) {
                    this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.COSIGNATURE,
                            new CosignatureSignedTransaction(
                                    message.getString("parenthash"),
                                    message.getString("signature"),
                                    message.getString("signer")
                            )
                    ));
                }
            });
        });
        return future;
    }

    /**
     * // TODO: should we remove it?
     *
     * @return the UID connected to
     */
    public String getUID() {
        return UID;
    }

    /**
     * Close webSocket connection
     */
    public void close() {
        this.webSocket.close();
    }

    /**
     * Returns an observable stream of BlockInfo.
     * Each time a new Block is added into the blockchain,
     * it emits a new BlockInfo in the event stream.
     *
     * @return an observable stream of BlockInfo
     */
    public Observable<BlockInfo> newBlock() {
        this.subscribeTo(ListenerChannel.BLOCK.toString());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.BLOCK))
                .map(rawMessage -> (BlockInfo) rawMessage.getMessage());
    }

    /**
     * Returns an observable stream of Transaction for a specific address.
     * Each time a transaction is in confirmed state an it involves the address,
     * it emits a new Transaction in the event stream.
     *
     * @param address address we listen when a transaction is in confirmed state
     * @return an observable stream of Transaction with state confirmed
     */
    public Observable<Transaction> confirmed(final Address address) {
        this.subscribeTo(ListenerChannel.CONFIRMED_ADDED.toString() + "/" + address.plain());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.CONFIRMED_ADDED))
                .map(rawMessage -> (Transaction) rawMessage.getMessage())
                .filter(transaction -> this.transactionFromAddress(transaction, address));
    }

    /**
     * Returns an observable stream of Transaction for a specific address.
     * Each time a transaction is in unconfirmed state an it involves the address,
     * it emits a new Transaction in the event stream.
     *
     * @param address address we listen when a transaction is in unconfirmed state
     * @return an observable stream of Transaction with state unconfirmed
     */
    public Observable<Transaction> unconfirmedAdded(Address address) {
        this.subscribeTo(ListenerChannel.UNCONFIRMED_ADDED + "/" + address.plain());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.UNCONFIRMED_ADDED))
                .map(rawMessage -> (Transaction) rawMessage.getMessage())
                .filter(transaction -> this.transactionFromAddress(transaction, address));
    }

    /**
     * Returns an observable stream of Transaction Hashes for specific address.
     * Each time a transaction with state unconfirmed changes its state,
     * it emits a new message with the transaction hash in the event stream.
     *
     * @param address address we listen when a transaction is removed from unconfirmed state
     * @return an observable stream of Strings with the transaction hash
     */
    public Observable<String> unconfirmedRemoved(Address address) {
        this.subscribeTo(ListenerChannel.UNCONFIRMED_REMOVED + "/" + address.plain());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.UNCONFIRMED_REMOVED))
                .map(rawMessage -> (String) rawMessage.getMessage());
    }

    /**
     * Return an observable of {@link AggregateTransaction} for specific address.
     * Each time an aggregate bonded transaction is announced,
     * it emits a new {@link AggregateTransaction} in the event stream.
     *
     * @param address address we listen when a transaction with missing signatures state
     * @return an observable stream of AggregateTransaction with missing signatures state
     */
    public Observable<AggregateTransaction> aggregateBondedAdded(Address address) {
        this.subscribeTo(ListenerChannel.AGGREGATE_BONDED_ADDED + "/" + address.plain());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.AGGREGATE_BONDED_ADDED))
                .map(rawMessage -> (AggregateTransaction) rawMessage.getMessage())
                .filter(transaction -> this.transactionFromAddress(transaction, address));
    }

    /**
     * Returns an observable stream of Transaction Hashes for specific address.
     * Each time an aggregate bonded transaction is announced,
     * it emits a new message with the transaction hash in the event stream.
     *
     * @param address address we listen when a transaction is confirmed or rejected
     * @return an observable stream of Strings with the transaction hash
     */
    public Observable<String> aggregateBondedRemoved(Address address) {
        this.subscribeTo(ListenerChannel.AGGREGATE_BONDED_REMOVED + "/" + address.plain());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.AGGREGATE_BONDED_REMOVED))
                .map(rawMessage -> (String) rawMessage.getMessage());
    }

    /**
     * Returns an observable stream of {@link TransactionStatusError} for specific address.
     * Each time a transaction contains an error,
     * it emits a new message with the transaction status error in the event stream.
     *
     * @param address address we listen to be notified when some error happened
     * @return an observable stream of {@link TransactionStatusError}
     */
    public Observable<TransactionStatusError> status(Address address) {
        this.subscribeTo(ListenerChannel.STATUS + "/" + address.plain());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.STATUS))
                .map(rawMessage -> (TransactionStatusError) rawMessage.getMessage());
    }

    /**
     * Returns an observable stream of {@link CosignatureSignedTransaction} for specific address.
     * Each time a cosigner signs a transaction the address initialized,
     * it emits a new message with the cosignatory signed transaction in the even stream.
     *
     * @param address address we listen when a cosignatory is added to some transaction address sent
     * @return an observable stream of {@link CosignatureSignedTransaction}
     */
    public Observable<CosignatureSignedTransaction> cosignatureAdded(Address address) {
        this.subscribeTo(ListenerChannel.CONFIRMED_ADDED + "/" + address.plain());
        return this.messageSubject
                .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.COSIGNATURE))
                .map(rawMessage -> (CosignatureSignedTransaction) rawMessage.getMessage());
    }

    private void subscribeTo(String channel) {
        final ListenerSubscribeMessage subscribeMessage = new ListenerSubscribeMessage(this.UID, channel);
        String json;
        try {
            json = objectMapper.writeValueAsString(subscribeMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getCause());
        }
        this.webSocket.writeTextMessage(json);
    }

    private BigInteger extractBigInteger(JsonArray input) {
        UInt64DTO uInt64DTO = new UInt64DTO();
        input.stream().forEach(item -> uInt64DTO.add(new Long(item.toString())));
        return uInt64DTO.extractIntArray();
    }

    private boolean transactionFromAddress(final Transaction transaction, final Address address) {
        AtomicBoolean transactionFromAddress = new AtomicBoolean(this.transactionHasSignerOrReceptor(transaction, address));

        if (transaction instanceof AggregateTransaction) {
            final AggregateTransaction aggregateTransaction = (AggregateTransaction) transaction;
            aggregateTransaction.getCosignatures().forEach(cosignature -> {
                if (cosignature.getSigner().getAddress().equals(address)) {
                    transactionFromAddress.set(true);
                }
            });
            aggregateTransaction.getInnerTransactions().forEach(innerTransaction -> {
                if (this.transactionHasSignerOrReceptor(innerTransaction, address)) {
                    transactionFromAddress.set(true);
                }
            });
        }
        return transactionFromAddress.get();
    }

    private boolean transactionHasSignerOrReceptor(final Transaction transaction, final Address address) {
        boolean isReceptor = false;
        if (transaction instanceof TransferTransaction) {
            isReceptor = ((TransferTransaction) transaction).getRecipient().equals(address);
        }
        return transaction.getSigner().get().getAddress().equals(address) || isReceptor;
    }
}
