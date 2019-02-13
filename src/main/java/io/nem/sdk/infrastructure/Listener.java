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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.*;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.*;
import okio.ByteString;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

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
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (this.webSocket != null) {
            return CompletableFuture.completedFuture(null);
        }

        Request request = new Request.Builder()
                .url(this.url.toString() + "/ws")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                JsonObject message = new Gson().fromJson(text, JsonObject.class);
                if (message.has("uid")) {
                    Listener.this.UID = message.get("uid").getAsString();
                    future.complete(null);
                } else if (message.has("transaction")) {
                    Listener.this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.rawValueOf(message.getAsJsonObject("meta").get("channelName").getAsString()),
                            new TransactionMapping().apply(new Gson().fromJson(message.toString(), com.google.gson.JsonObject.class))
                    ));
                } else if (message.has("block")) {
                    final JsonObject meta = message.getAsJsonObject("meta");
                    final JsonObject block = message.getAsJsonObject("block");
                    int rawNetworkType = (int) Long.parseLong(Integer.toHexString(block.get("version").getAsInt()).substring(0, 2), 16);
                    final NetworkType networkType;
                    if (rawNetworkType == NetworkType.MIJIN_TEST.getValue()) networkType = NetworkType.MIJIN_TEST;
                    else if (rawNetworkType == NetworkType.MIJIN.getValue()) networkType = NetworkType.MIJIN;
                    else if (rawNetworkType == NetworkType.MAIN_NET.getValue()) networkType = NetworkType.MAIN_NET;
                    else networkType = NetworkType.TEST_NET;

                    final int version = (int) Long.parseLong(Integer.toHexString(block.get("version").getAsInt()).substring(2, 4), 16);
                    Listener.this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.BLOCK,
                            new BlockInfo(
                                    meta.get("hash").getAsString(),
                                    meta.get("generationHash").getAsString(),
                                    Optional.empty(),
                                    Optional.empty(),
                                    block.get("signature").getAsString(),
                                    new PublicAccount(block.get("signer").getAsString(), networkType),
                                    networkType,
                                    version,
                                    block.get("type").getAsInt(),
                                    extractBigInteger(block.getAsJsonArray("height")),
                                    extractBigInteger(block.getAsJsonArray("timestamp")),
                                    extractBigInteger(block.getAsJsonArray("difficulty")),
                                    block.get("previousBlockHash").getAsString(),
                                    block.get("blockTransactionsHash").getAsString()
                            )
                    ));
                } else if (message.has("status")) {
                    Listener.this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.STATUS,
                            new TransactionStatusError(
                                    message.get("hash").getAsString(),
                                    message.get("status").getAsString(),
                                    new Deadline(extractBigInteger(message.getAsJsonArray("deadline")))
                            )
                    ));
                } else if (message.has("meta")) {
                    Listener.this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.rawValueOf(message.getAsJsonObject("meta").get("channelName").getAsString()),
                            message.get("meta").getAsJsonObject().get("hash").getAsString()
                    ));
                } else if (message.has("parentHash")) {
                    Listener.this.messageSubject.onNext(new ListenerMessage(
                            ListenerChannel.COSIGNATURE,
                            new CosignatureSignedTransaction(
                                    message.get("parenthash").getAsString(),
                                    message.get("signature").getAsString(),
                                    message.get("signer").getAsString()
                            )
                    ));
                }

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);

            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }
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
        this.webSocket.close(1000, "Closed.");
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
        this.webSocket.send(json);
    }

    private BigInteger extractBigInteger(JsonArray input) {
        UInt64DTO uInt64DTO = new UInt64DTO();
        StreamSupport.stream(input.spliterator(), false).forEach(item -> uInt64DTO.add(new Long(item.toString())));
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
