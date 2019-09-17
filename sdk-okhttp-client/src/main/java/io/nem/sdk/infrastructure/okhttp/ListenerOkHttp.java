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

import com.google.gson.JsonObject;
import io.nem.core.crypto.SignSchema;
import io.nem.sdk.infrastructure.Listener;
import io.nem.sdk.infrastructure.ListenerBase;
import io.nem.sdk.infrastructure.ListenerChannel;
import io.nem.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.infrastructure.okhttp.mappers.TransactionMapper;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON;
import io.nem.sdk.openapi.okhttp_gson.model.BlockInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * OkHttp implementations of the {@link Listener}.
 *
 * @since 1.0
 */
public class ListenerOkHttp extends ListenerBase implements Listener {

    private final URL url;

    private final JsonHelper jsonHelper;

    private final OkHttpClient httpClient;

    private final TransactionMapper transactionMapper;

    private final SignSchema signSchema;

    private WebSocket webSocket;

    private String uid;


    public ListenerOkHttp(OkHttpClient httpClient, String url, JSON json, SignSchema signSchema) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                "Parameter '" + url + "' is not a valid URL. " + ExceptionUtils.getMessage(e));
        }
        this.httpClient = httpClient;
        this.jsonHelper = new JsonHelperGson(json.getGson());
        this.transactionMapper = new GeneralTransactionMapper(jsonHelper, signSchema);
        this.signSchema = signSchema;
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
        Request webSocketRequest = new Request.Builder()
            .url(checkTrailingSlash(url.toString()) + "ws").build();
        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handle(jsonHelper.parse(text, JsonObject.class), future);
            }
        };
        this.webSocket = httpClient.newWebSocket(webSocketRequest, webSocketListener);
        return future;
    }

    private String checkTrailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
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
                new Deadline(
                    new BigInteger(jsonHelper.getString(message, "deadline"))));
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
        return BlockRepositoryOkHttpImpl.toBlockInfo(signSchema, blockInfoDTO);
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
            this.webSocket.close(1000, null);
            this.webSocket = null;
        }
    }

    protected void subscribeTo(String channel) {
        final ListenerSubscribeMessage subscribeMessage = new ListenerSubscribeMessage(this.uid,
            channel);
        this.webSocket.send(jsonHelper.print(subscribeMessage));
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
