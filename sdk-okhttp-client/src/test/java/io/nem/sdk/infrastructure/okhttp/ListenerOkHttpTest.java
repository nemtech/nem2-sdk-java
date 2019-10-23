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
package io.nem.sdk.infrastructure.okhttp;

import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import io.nem.sdk.infrastructure.ListenerChannel;
import io.nem.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Tests for the {@link ListenerOkHttp} implementation of the {@link
 * io.nem.sdk.infrastructure.Listener}
 */
public class ListenerOkHttpTest {

    private ListenerOkHttp listener;

    private OkHttpClient httpClientMock;

    private WebSocket webSocketMock;

    private JsonHelper jsonHelper;

    private String wsId = "TheWSid";

    @BeforeEach
    public void setUp() {
        httpClientMock = Mockito.mock(OkHttpClient.class);
        String url = "http://nem.com:3000/";
        listener = new ListenerOkHttp(httpClientMock, url, new JSON());
        jsonHelper = listener.getJsonHelper();
    }


    @Test
    public void shouldOpen() throws ExecutionException, InterruptedException, TimeoutException {

        Assertions.assertNull(listener.getUid());

        Assertions.assertEquals("Listener has not been opened yet. Please call the open method before subscribing.",
            Assertions
                .assertThrows(IllegalStateException.class, () -> listener.newBlock()).getMessage());

        simulateWebSocketStartup();

        Assertions.assertNotNull(listener.newBlock());

        Assertions.assertEquals(wsId, listener.getUid());

        listener.close();
        listener.close();

        Assertions.assertNull(listener.getUid());

        Assertions.assertEquals("Listener has not been opened yet. Please call the open method before subscribing.",
            Assertions
                .assertThrows(IllegalStateException.class, () -> listener.newBlock()).getMessage());
    }



    @Test
    public void shouldHandleTransaction()
        throws InterruptedException, ExecutionException, TimeoutException {
        simulateWebSocketStartup();

        TransactionInfoDTO transactionInfo = TestHelperOkHttp.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        JsonObject transactionInfoDtoJsonObject = jsonHelper
            .convert(transactionInfo, JsonObject.class);

        Address address = Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NetworkType.MIJIN_TEST);

        String channelName = ListenerChannel.CONFIRMED_ADDED.toString();

        ((JsonObject) transactionInfoDtoJsonObject.get("meta"))
            .addProperty("channelName", channelName);

        List<Transaction> transactions = new ArrayList<>();
        listener.confirmed(address).forEach(transactions::add);

        Mockito.verify(webSocketMock)
            .send(jsonHelper.print(new ListenerSubscribeMessage(this.wsId,
                channelName + "/" + address.plain())));

        listener.handle(transactionInfoDtoJsonObject, null);

        Assertions.assertEquals(1, transactions.size());

        Assertions.assertEquals(address, transactions.get(0).getSigner().get().getAddress());

    }

    private void simulateWebSocketStartup()
        throws InterruptedException, ExecutionException, TimeoutException {
        webSocketMock = Mockito.mock(WebSocket.class);
        ArgumentCaptor<WebSocketListener> webSocketListenerArgumentCaptor = ArgumentCaptor
            .forClass(WebSocketListener.class);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        when(httpClientMock
            .newWebSocket(requestCaptor.capture(), webSocketListenerArgumentCaptor.capture()))
            .thenReturn(webSocketMock);

        CompletableFuture<Void> future = listener.open();

        WebSocketListener webSocketListener = webSocketListenerArgumentCaptor.getValue();

        Assertions.assertNotNull(webSocketListener);

        Assertions.assertEquals("http://nem.com:3000/ws", requestCaptor.getValue().url().toString());

        webSocketListener
            .onMessage(webSocketMock, jsonHelper.print(Collections.singletonMap("uid", wsId)));

        future.get(3, TimeUnit.SECONDS);
    }

}
