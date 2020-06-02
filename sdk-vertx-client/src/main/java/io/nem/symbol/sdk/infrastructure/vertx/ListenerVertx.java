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

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.infrastructure.ListenerBase;
import io.nem.symbol.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.infrastructure.TransactionMapper;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.openapi.vertx.model.BlockInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.Cosignature;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.Json;
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

    private final HttpClient httpClient;

    private final TransactionMapper transactionMapper;

    private WebSocket webSocket;


    /**
     * @param httpClient the http client instance.
     * @param url of the host
     */
    public ListenerVertx(HttpClient httpClient, String url,
        NamespaceRepository namespaceRepository) {
        super(new JsonHelperJackson2(JsonHelperJackson2.configureMapper(Json.mapper)),
            namespaceRepository);
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Parameter '" + url +
                "' is not a valid URL. " + ExceptionUtils.getMessage(e));
        }
        this.httpClient = httpClient;
        this.transactionMapper = new GeneralTransactionMapper(getJsonHelper());
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
                        ObjectNode message = getJsonHelper()
                            .convert(handler.toJsonObject(), ObjectNode.class);
                        handle(message, future);
                    });
            });
        return future;
    }


    @Override
    protected BlockInfo toBlockInfo(Object blockInfoDTO) {
        return BlockRepositoryVertxImpl
            .toBlockInfo(getJsonHelper().convert(blockInfoDTO, BlockInfoDTO.class));
    }

    @Override
    protected Transaction toTransaction(Object transactionInfo) {
        return transactionMapper
            .mapFromDto(getJsonHelper().convert(transactionInfo, TransactionInfoDTO.class));
    }

    @Override
    protected CosignatureSignedTransaction toCosignatureSignedTransaction(
        Object cosignatureJson) {
        Cosignature cosignature = getJsonHelper().convert(cosignatureJson, Cosignature.class);
        return new CosignatureSignedTransaction(cosignature.getParentHash(),
            cosignature.getSignature(), cosignature.getSignerPublicKey());
    }

    /**
     * Close webSocket connection
     */
    @Override
    public void close() {
        if (this.webSocket != null) {
            this.setUid(null);
            this.webSocket.close();
            this.webSocket = null;
        }
    }

    protected void subscribeTo(String channel) {
        final ListenerSubscribeMessage subscribeMessage = new ListenerSubscribeMessage(
            this.getUid(),
            channel);
        this.webSocket.writeTextMessage(getJsonHelper().print(subscribeMessage));
    }

}
