/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.nem.sdk.infrastructure.vertx;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nem.core.crypto.SignSchema;
import io.nem.sdk.infrastructure.ListenerChannel;
import io.nem.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferFactoryImpl;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Tests for the {@link ListenerVertx} implementation of the {@link io.nem.sdk.infrastructure.Listener}
 */
public class ListenerVertxTest {

    private ListenerVertx listener;

    private HttpClient httpClientMock;

    private WebSocket webSocketMock;

    private JsonHelper jsonHelper;

    private String wsId = "TheWSid";

    @BeforeEach
    public void setUp() {
        httpClientMock = Mockito.mock(HttpClient.class);
        String url = "http://nem.com:3000/";
        listener = new ListenerVertx(httpClientMock, url, SignSchema.DEFAULT);
        jsonHelper = listener.getJsonHelper();
    }


    @Test
    public void shouldOpen() throws ExecutionException, InterruptedException, TimeoutException {

        Assertions.assertNull(listener.getUid());

        simulateWebSocketStartup();

        Assertions.assertEquals(wsId, listener.getUid());

        listener.close();
        listener.close();
    }


    @Test
    public void shouldHandleTransaction()
        throws InterruptedException, ExecutionException, TimeoutException {
        simulateWebSocketStartup();

        TransactionInfoDTO transactionInfo = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        ObjectNode transactionInfoDtoJsonObject = jsonHelper
            .convert(transactionInfo, ObjectNode.class);

        Address address = Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NetworkType.MIJIN_TEST, SignSchema.DEFAULT);

        String channelName = ListenerChannel.CONFIRMED_ADDED.toString();

        ((ObjectNode) transactionInfoDtoJsonObject.get("meta")).put("channelName", channelName);

        List<Transaction> transactions = new ArrayList<>();
        listener.confirmed(address).forEach(transactions::add);

        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper.print(new ListenerSubscribeMessage(this.wsId,
                channelName + "/" + address.plain())));

        listener.handle(transactionInfoDtoJsonObject, null);

        Assert.assertEquals(1, transactions.size());

        Assert.assertEquals(address, transactions.get(0).getSigner().get().getAddress());

    }

    private void simulateWebSocketStartup()
        throws InterruptedException, ExecutionException, TimeoutException {
        webSocketMock = Mockito.mock(WebSocket.class);
        ArgumentCaptor<Handler> webSocketHandlerCapture = ArgumentCaptor.forClass(Handler.class);
        ArgumentCaptor<Handler> bufferHandlerCapture = ArgumentCaptor.forClass(Handler.class);

        when(httpClientMock.websocket(any(RequestOptions.class), webSocketHandlerCapture.capture()))
            .thenReturn(httpClientMock);
        when(webSocketMock.handler(bufferHandlerCapture.capture())).thenReturn(webSocketMock);

        CompletableFuture<Void> future = listener.open();

        Handler<WebSocket> webSocketHandler = webSocketHandlerCapture.getValue();
        Assertions.assertNotNull(webSocketHandler);

        webSocketHandler.handle(webSocketMock);

        Handler<Buffer> bufferHandler = bufferHandlerCapture.getValue();
        Assertions.assertNotNull(bufferHandler);

        Buffer event = new BufferFactoryImpl()
            .buffer(jsonHelper.print(Collections.singletonMap("uid", wsId)));
        bufferHandler.handle(event);

        future.get(3, TimeUnit.SECONDS);
    }

}
