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

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nem.core.crypto.SignSchema;
import io.nem.sdk.infrastructure.Listener;
import io.nem.sdk.infrastructure.ListenerBase;
import io.nem.sdk.infrastructure.ListenerChannel;
import io.nem.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.sdk.infrastructure.vertx.mappers.TransactionMapper;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.nem.sdk.openapi.vertx.model.BlockInfoDTO;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.Json;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Vertx implementations of the {@link Listener}.
 *
 * @since 1.0
 */
public class ListenerVertx extends ListenerBase implements Listener {

    private final URL url;

    private final JsonHelper jsonHelper;

    private final HttpClient httpClient;

    private final TransactionMapper transactionMapper;
    private final SignSchema signSchema;

    private WebSocket webSocket;

    private String uid;

    /**
     * @param httpClient the http client instance.
     * @param url of the host
     * @param signSchema the {@link SignSchema}
     */
    public ListenerVertx(HttpClient httpClient, String url, SignSchema signSchema) {
        this.signSchema = signSchema;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Parameter '" + url +
                "' is not a valid URL. " + ExceptionUtils.getMessage(e));
        }
        this.httpClient = httpClient;
        this.jsonHelper = new JsonHelperJackson2(JsonHelperJackson2.configureMapper(Json.mapper));
        this.transactionMapper = new GeneralTransactionMapper(jsonHelper, this.signSchema);
    }

    /**
     * @param url of the host
     */
    public ListenerVertx(String url, SignSchema signSchema) {
        this(createHttpClient(), url, signSchema);
    }

    private static HttpClient createHttpClient() {
        return Vertx.vertx().createHttpClient();
    }

    /**
     * @return a {@link CompletableFuture} that resolves when the websocket connection is opened
     */
    @Override
    public CompletableFuture<Void> open() {

        CompletableFuture<Void> future = new CompletableFuture<>();
        if (this.webSocket != null) {
            return CompletableFuture.completedFuture(null);
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setHost(this.url.getHost());
        requestOptions.setPort(this.url.getPort());
        requestOptions.setURI("/ws");

        httpClient.websocket(
            requestOptions,
            ws -> {
                this.webSocket = ws;
                ws.handler(
                    handler -> {
                        ObjectNode message = jsonHelper
                            .convert(handler.toJsonObject(), ObjectNode.class);
                        handle(message, future);
                    });
            });
        return future;
    }

    protected void handle(Object message, CompletableFuture<Void> future) {
        if (jsonHelper.contains(message, "uid")) {
            uid = jsonHelper.getString(message, "uid");
            future.complete(null);
        } else if (jsonHelper.contains(message, "transaction")) {
            TransactionInfoDTO transactionInfo = jsonHelper
                .convert(message, TransactionInfoDTO.class);
            Transaction messageObject = toTransaction(transactionInfo);
            ListenerChannel channel = ListenerChannel
                .rawValueOf(jsonHelper.getString(message, "meta", "channelName"));
            onNext(channel, messageObject);
        } else if (jsonHelper.contains(message, "block")) {
            BlockInfoDTO blockInfoDTO = jsonHelper
                .convert(message, BlockInfoDTO.class);
            BlockInfo messageObject = toBlockInfo(blockInfoDTO);
            onNext(ListenerChannel.BLOCK, messageObject);
        } else if (jsonHelper.contains(message, "status")) {
            TransactionStatusError messageObject = new TransactionStatusError(
                jsonHelper.getString(message, "hash"),
                jsonHelper.getString(message, "status"),
                new Deadline(new BigInteger(jsonHelper.getString(message, "deadline"))));
            onNext(ListenerChannel.STATUS, messageObject);
        } else if (jsonHelper.contains(message, "meta")) {
            onNext(ListenerChannel.rawValueOf(
                jsonHelper.getString(message, "meta", "channelName")),
                jsonHelper.getString(message, "meta", "hash"));
        } else if (jsonHelper.contains(message, "parentHash")) {
            CosignatureSignedTransaction messageObject = new CosignatureSignedTransaction(
                jsonHelper.getString(message, "parenthash"),
                jsonHelper.getString(message, "signature"),
                jsonHelper.getString(message, "signer"));
            onNext(ListenerChannel.COSIGNATURE, messageObject);
        }
    }

    private BlockInfo toBlockInfo(BlockInfoDTO blockInfoDTO) {
        return BlockRepositoryVertxImpl.toBlockInfo(signSchema, blockInfoDTO);
    }

    private Transaction toTransaction(TransactionInfoDTO transactionInfo) {
        return transactionMapper.map(transactionInfo);
    }


    /**
     * Close webSocket connection
     */
    @Override
    public void close() {
        if (this.webSocket != null) {
            this.webSocket.close();
            this.webSocket = null;
        }
    }

    protected void subscribeTo(String channel) {
        final ListenerSubscribeMessage subscribeMessage = new ListenerSubscribeMessage(this.uid,
            channel);
        this.webSocket.writeTextMessage(jsonHelper.print(subscribeMessage));
    }

    /**
     * @return the UID connected to
     */
    @Override
    public String getUid() {
        return uid;
    }


    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }
}
